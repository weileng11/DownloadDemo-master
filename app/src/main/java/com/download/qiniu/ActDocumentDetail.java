package com.download.qiniu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.socks.library.KLog;
import com.thinkine.util.DateTimeUtil;
import com.thinkine.util.ToastUtils;
import com.tontru.thinkine.R;
import com.tontru.thinkine.ThinkineAppLication;
import com.tontru.thinkine.adapter.DocumentDetailAdapter;
import com.tontru.thinkine.base.BaseActivity;
import com.tontru.thinkine.constant.ComParamContact;
import com.tontru.thinkine.db.BaseDao;
import com.tontru.thinkine.db.bean.UploadInfoBean;
import com.tontru.thinkine.dialog.SelectDialog;
import com.tontru.thinkine.model.request.DocumentReq;
import com.tontru.thinkine.model.request.ProjectFileReq;
import com.tontru.thinkine.model.request.RechristenDocument;
import com.tontru.thinkine.model.response.AddFileResponse;
import com.tontru.thinkine.model.response.DocumentResponse;
import com.tontru.thinkine.model.response.DownLoadUrlResponse;
import com.tontru.thinkine.model.response.UploadFileResponse;
import com.tontru.thinkine.presenter.DocumentFragmentPresenter;
import com.tontru.thinkine.presenter.DownloadUrlPresenter;
import com.tontru.thinkine.ui.view.IDocumentFragmentView;
import com.tontru.thinkine.ui.view.IDownloadUrlView;
import com.tontru.thinkine.widget.RecyclerViewDivider;
import com.tontru.thinkine.widget.SwipeItemLayout;
import com.tontru.thinkine.widget.TopBarLayout;
import com.tontru.thinkine.widget.utils.CommonUtils;
import com.tontru.thinkine.widget.utils.SortUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @name Thinkine
 * @class name：com.tontru.thinkine.ui
 * @class describe   文件夹里面的文件类
 * @anthor ${bruce} QQ:275762645
 * @time 2017/12/1 11:39
 * @change
 * @chang time
 * @class describe
 */
public class ActDocumentDetail extends BaseActivity implements IDocumentFragmentView, IDownloadUrlView,
        DocumentDetailAdapter.DocumentDeleteListenter, OnItemClickListener {
    @BindView(R.id.toolbar)
    TopBarLayout toolbar;
    @BindView(R.id.rv_documentdl_list)
    RecyclerView rvDocumentdlList;
    @BindView(R.id.refreshLayout_documentdl)
    SmartRefreshLayout refreshLayoutDocumentdl;

    private String type = "1"; //1.文件 2.文件夹
    private String orderType = "2";//1.降序  2.升序
    //    private String var2="request";  //1.request  2.export
    private String isValidStatus = "1";
    private String projectId;
    private String fileName;
    private String fileId;

    DocumentDetailAdapter documentDetailAdapter;
    DocumentFragmentPresenter mDocumentFragmentPresenter;
    DownloadUrlPresenter mDownloadUrlPresenter;
    DocumentReq documentReq;
    List<DocumentResponse.FileListBean> documentResponseList = new ArrayList<>();
    private int deletePosition; //删除的下标
    private int rechristenPosition; //重命名下标
    private InputMethodManager imm;
    private String fileType;
    private String fileShowName; //文件的名字
    List<DocumentResponse.FileListBean> sortList;

    @Override
    protected int getLayoutId() {
        return R.layout.act_document_detail;
    }

    @Override
    protected void listenter() {

    }

    @Override
    protected void initData() {
        //注册广播
        initRegister();

        Bundle bundle = getIntent().getExtras();//获取一个句柄
        projectId = bundle.getString("projectId");
        fileName = bundle.getString("name");
        fileId = bundle.getString("fileId");

        toolbar.setTxvTitleName(fileName);
        //添加要上传的图片和文件
        toolbar.setRigthViewTypeMode(TopBarLayout.RightViewTypeMode.ADD);
        toolbar.setBtnRightDrawable(R.mipmap.projectdl_addfile);
        toolbar.setRightTxvOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddDocumentDialog();
            }
        });

        //进入上传
        toolbar.setTxvRightUploadShow(View.VISIBLE);
        toolbar.setTxvRightUploadClickListenter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(ActDocumentDetail.this, ActUpload.class);
            }
        });

        rvDocumentdlList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDocumentdlList.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
        documentDetailAdapter = new DocumentDetailAdapter(this, documentResponseList, this);
        rvDocumentdlList.setAdapter(documentDetailAdapter);
//        rvDocumentdlList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        rvDocumentdlList.addItemDecoration(new RecyclerViewDivider(this,
                LinearLayoutManager.HORIZONTAL, getResources().getDimensionPixelSize(R.dimen.x1), getResources().getColor(R.color.gray)
        ).setFirstDividerVisible(true).setBottomDividerVisible(true));

//        rvDocumentdlList.addItemDecoration(new RecyclerViewDivider(this,
//                LinearLayoutManager.VERTICAL,getResources().getDimensionPixelSize(R.dimen.x1),getResources().getColor(R.color.white)
//        ).setFirstDividerVisible(false).setBottomDividerVisible(false));

        refreshLayoutDocumentdl.setEnableAutoLoadmore(false);//开启自动加载功能（非必须）
        //头部
        refreshLayoutDocumentdl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
//                if (projectResponseArrayList.size() > 0) {
//                    projectResponseArrayList.clear();
//                    mItemPinnedHeadAdapter.notifyDataSetChanged();
//
//                }
                //默认显示已启动的项目
//                itemFragmentPresenter.getReqProjectData(status, isLoadCustomerInfo, isLoadTaskInfo, isLoadProjectStatus);
                mDocumentFragmentPresenter.getDocumentFileListData(documentReq);
                refreshLayoutDocumentdl.finishRefresh(500);
            }
        });
        //底部
        refreshLayoutDocumentdl.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                refreshLayoutDocumentdl.finishLoadmore(1000);
//                      refreshLayoutDocumentdl.setLoadmoreFinished(true);//将不会再次触发加载更多事件
            }
        });

        mDownloadUrlPresenter = new DownloadUrlPresenter(this, this);
        mDocumentFragmentPresenter = new DocumentFragmentPresenter(this, this);
        documentReq = new DocumentReq();
        documentReq.projectIdValue = projectId;
        documentReq.typeValue = type;
        documentReq.orderTypeValue = orderType;
        documentReq.isValidStatusValue = isValidStatus;
        documentReq.parentIdValue = fileId;

        //获取所有的文件
        mDocumentFragmentPresenter.getDocumentFileListData(documentReq);

        qiniuUploadUtils = new QiniuUploadUtils(ActDocumentDetail.this);
        //获取uptoken,key
        getUpTokenKey();
    }


    /**
     * 获取所有的文件
     *
     * @param documentResponse
     */
    @Override
    public void getDocumentCallBackData(DocumentResponse documentResponse) {
        if (documentResponse.fileList.size() > 0) {
            if (documentResponseList.size() > 0) {
                documentResponseList.clear();
            }
            documentResponseList.addAll(documentResponse.fileList);
            documentDetailAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 接口删除成功
     */
    @Override
    public void deleteDocumentCallBackSuccess() {
        documentResponseList.remove(deletePosition);
        documentDetailAdapter.notifyItemRemoved(deletePosition);
        documentDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 重命名成功
     */
    @Override
    public void rechristenDocumentCallBackSuccess() {
        documentResponseList.get(rechristenPosition).name = name;
        documentDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 增加文件返回
     *
     * @param addFileResponse
     */
    @Override
    public void addDocumentCallBackSuccess(AddFileResponse addFileResponse) {
        if (addFileResponse != null) {
            KLog.i("增加文件" + documentResponseList.size());
            if (documentResponseList.size() > 0) {
                DocumentResponse.FileListBean mAddFileResponse = new DocumentResponse.FileListBean();
                mAddFileResponse.name = addFileResponse.newFolder.name;
                mAddFileResponse.fileId = addFileResponse.newFolder.fileId;

                mAddFileResponse.status = addFileResponse.newFolder.status;
//                mAddFileResponse.childFileCount=addFileResponse.newFolder.childFileCount;
                mAddFileResponse.var1 = addFileResponse.newFolder.var1;
                mAddFileResponse.type = addFileResponse.newFolder.type;
                mAddFileResponse.updateDate = DateTimeUtil.stringToLongAddFile(addFileResponse.newFolder.updateDate);
                mAddFileResponse.parentId = addFileResponse.newFolder.parentId;
                mAddFileResponse.category = addFileResponse.newFolder.category;
//                mAddFileResponse.flag= DateTimeUtil.stringToLongAddFile(addFileResponse.newFolder.flag);
                mAddFileResponse.level = addFileResponse.newFolder.level;
                mAddFileResponse.userName = addFileResponse.newFolder.userName;
                mAddFileResponse.createDate = DateTimeUtil.stringToLongAddFile(addFileResponse.newFolder.createDate);
                mAddFileResponse.path = keyPath;
                documentResponseList.add(mAddFileResponse);
                //排序
                sortList = SortUtils.sortData(documentResponseList);

                documentDetailAdapter.replaceAll(sortList);
                documentDetailAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 删除
     *
     * @param position
     */
    @Override
    public void deleteFile(int position) {
        if (documentResponseList.size() > 0) {
            deletePosition = position;
            mDocumentFragmentPresenter.deleteDocumentFile(documentResponseList.get(deletePosition).fileId);
        }
    }

    /**
     * 重命名
     *
     * @param position
     */
    @Override
    public void stickFile(int position) {
        if (documentResponseList.size() > 0) {
            rechristenPosition = position;
            showEdDialog("请输入要修改的文件名称!");
        }
    }

    /**
     * 显示图片文件或者其他操作
     *
     * @param position
     */
    @Override
    public void toDocumentDetailFiles(int position, View view) {
        if (documentResponseList.size() > 0) {
            fileShowName = documentResponseList.get(position).name;
            KLog.i("点击图片的名称" + fileShowName + "和" + documentResponseList.get(position).path);
            //根据名字和文件路径在调用一次接口
            mDownloadUrlPresenter.getDownloadUrlData(documentResponseList.get(position).path, documentResponseList.get(position).name);
        }
    }

    @Override
    public void getDownLoadUrlReqData(DownLoadUrlResponse downLoadUrlResponse) {
        if (downLoadUrlResponse != null) {
            String downloadUrlName = downLoadUrlResponse.downloadUrl;
            //截取字符窜
//            String newStr = fileEName.substring(fileEName.indexOf("attname"),fileEName.length());
//            String result = newStr.substring(0, newStr.indexOf("&e"));
            String result = downloadUrlName.substring(downloadUrlName.indexOf("attname=") + 8, downloadUrlName.indexOf("&e"));
            // 获取文件后缀名
            String fileEName = CommonUtils.getExtensionName(result);
            KLog.i("result" + result);
            if (ComParamContact.ImageStatus.Suffx_Picture.contains(fileEName)) { //如果是图片\
                KLog.i("图片路径xx", "图片路径" + downLoadUrlResponse.downloadUrl);
                // 如果是图片 显示图片
                Intent intent = new Intent(this, ActImagePager.class);
                intent.putExtra("imgurl", downLoadUrlResponse.downloadUrl);
                startActivity(intent);
            } else if (ComParamContact.ImageStatus.Suffx_Video.contains(fileEName)) {// 如果是视频
                fileType = "1";
                Bundle bundle = new Bundle();
                bundle.putString("filetype", fileType);
                bundle.putString("fileUrl", downLoadUrlResponse.downloadUrl);
                bundle.putString("fileshowname", fileShowName);
                showActivity(ActDocumentDetail.this, ActShowFiles.class, bundle);
            } else if (ComParamContact.ImageStatus.ELSE_Type.contains(fileEName)) {// 其他类型
                //传递路径用webview显示
                fileType = "2";
                Bundle bundle = new Bundle();
                bundle.putString("filetype", fileType);
                bundle.putString("fileUrl", downLoadUrlResponse.downloadUrl);
                bundle.putString("fileshowname", fileShowName);
                showActivity(ActDocumentDetail.this, ActShowFiles.class, bundle);
            } else { //没有.....
                fileType = "3";
                Bundle bundle = new Bundle();
                bundle.putString("filetype", fileType);
                bundle.putString("fileUrl", downLoadUrlResponse.downloadUrl);
                bundle.putString("fileshowname", fileShowName);
                showActivity(ActDocumentDetail.this, ActShowFiles.class, bundle);
            }
        }
    }

    /**
     * 上传文件接口返回的参数
     */
    @Override
    public void UploadFileSuccess(UploadFileResponse uploadFileResponse) {
        //在rv上面显示
        if (uploadFileResponse != null) {
            KLog.i("上传后增加文件" + documentResponseList.size());
            if (documentResponseList.size() > 0) {
                DocumentResponse.FileListBean mAddFileResponse = new DocumentResponse.FileListBean();
                mAddFileResponse.name = uploadFileResponse.newFile.name;
                mAddFileResponse.fileId = uploadFileResponse.newFile.fileId;

                mAddFileResponse.status = uploadFileResponse.newFile.status;
//                mAddFileResponse.childFileCount=addFileResponse.newFolder.childFileCount;
                mAddFileResponse.var1 = uploadFileResponse.newFile.var1;
                mAddFileResponse.type = uploadFileResponse.newFile.type;
                mAddFileResponse.updateDate = DateTimeUtil.stringToLongAddFile(uploadFileResponse.newFile.updateDate);
                mAddFileResponse.parentId = uploadFileResponse.newFile.parentId;
                mAddFileResponse.category = uploadFileResponse.newFile.category;
//                mAddFileResponse.flag= DateTimeUtil.stringToLongAddFile(addFileResponse.newFolder.flag);
                mAddFileResponse.level = uploadFileResponse.newFile.level;
                mAddFileResponse.userName = uploadFileResponse.newFile.userName;
                mAddFileResponse.createDate = DateTimeUtil.stringToLongAddFile(uploadFileResponse.newFile.createDate);
                KLog.i("uploadFileResponse.newFile.path" + uploadFileResponse.newFile.path);
                mAddFileResponse.path = uploadFileResponse.newFile.path;

                documentResponseList.add(mAddFileResponse);
                //同时更改数据库这一条记录的路径
                uploadSuccessPath(mAddFileResponse);
                //排序
                sortList = SortUtils.sortData(documentResponseList);


                documentDetailAdapter.replaceAll(sortList);
                documentDetailAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 更改上传成功后数据库的新路径
     */
    public void uploadSuccessPath(DocumentResponse.FileListBean mAddFileResponse) {
        try {
            UploadInfoBean uploadInfoBean = new UploadInfoBean();
            uploadInfoBean.id = fileinfo.id;
            uploadInfoBean.fileId = fileinfo.fileId;
            uploadInfoBean.fileFinish = "2";
            uploadInfoBean.fileName = fileinfo.fileName;
            uploadInfoBean.fileType = fileinfo.fileType;
            uploadInfoBean.fileSize = fileinfo.fileSize;
            uploadInfoBean.filePath = fileinfo.filePath;
            uploadInfoBean.qiniuKeyPath = mAddFileResponse.path;
            mUploadDao.update(uploadInfoBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //=============================对话框==========================//
    /**
     * 弹框
     */
    private AlertView mAlertViewExt;
    private EditText etName;//拓展View内容
    private String name;
    private boolean isStick = false; //重命名
    private boolean isAddEdFiles = false; //弹出输入的内

    public void showEdDialog(String content) {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //拓展窗口
        mAlertViewExt = new AlertView("提示", content, "取消", null, new String[]{"完成"}, this, AlertView.Style.Alert, this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.alertext_form, null);
        etName = (EditText) extView.findViewById(R.id.etName);
//        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean focus) {
        //输入框出来则往上移动
//                boolean isOpen=imm.isActive();
//                mAlertViewExt.setMarginBottom(isOpen&&focus ? 120 :0);
//                System.out.println(isOpen);
//            }
//        });
        mAlertViewExt.addExtView(extView);
        mAlertViewExt.setCancelable(true);
        mAlertViewExt.show();
    }

    private void closeKeyboard() {
        //关闭软键盘
        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
        //恢复位置
//        mAlertViewExt.setMarginBottom(0);
    }

    @Override
    public void onItemClick(Object o, int position) {
        closeKeyboard();
        //判断是否是拓展窗口View，而且点击的是非取消按钮
        if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
            name = etName.getText().toString();
            if (name.isEmpty()) {
                ToastUtils.showToastLong(this, "请输入要修改的文件名称");
            } else {
                mAlertViewExt.dismiss(); //关闭窗口
                //弹窗
                RechristenDocument rechristenDocument = new RechristenDocument();
                rechristenDocument.fileIdValue = documentResponseList.get(deletePosition).fileId;
                rechristenDocument.newFileNameValue = name;
                rechristenDocument.projectIdValue = projectId;
                mDocumentFragmentPresenter.rechristenDocumentFile(rechristenDocument);
            }
            return;
        } else {
            mAlertViewExt.dismiss(); //关闭窗口
            closeKeyboard();
        }
    }

    @Override
    public void setBolleanStatus() {
        closeKeyboard();
        mAlertViewExt.dismiss(); //关闭窗口
    }

    //===========================点击加号 选择要上传的文件============================//
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    //    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 5;               //允许选择图片最大数

    private String upToken;
    private String upKey;
    private QiniuUploadUtils qiniuUploadUtils;
    private String keyPath;
    private String fileUploadSize;
    private String fileUploadName;
    private String fileUploadType;
    ArrayList<ImageItem> images = null;
    BaseDao<UploadInfoBean> mUploadDao;
    private ArrayList<UploadInfoBean> mUploadInfoList;
    UploadInfoBean fileinfo;

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    /**
     * 点击加号调用dialog
     */
    public void getAddDocumentDialog() {
        selImageList = new ArrayList<>(); //保存图片的各种参数
        mUploadDao = ThinkineAppLication.getDao(UploadInfoBean.class);
        mUploadInfoList = new ArrayList<>();

        List<String> names = new ArrayList<>();
        names.add("拍照");
        names.add("相册");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 直接调起相机
                        /**
                         * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
                         * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
                         * 如果实在有所需要，请直接下载源码引用。
                         */
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(5);
                        Intent intent = new Intent(ActDocumentDetail.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(5);
                        Intent intent1 = new Intent(ActDocumentDetail.this, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                        startActivityForResult(intent1, REQUEST_CODE_SELECT);
                        break;
                    default:
                        break;
                }

            }
        }, names);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    selImageList.addAll(images);
                    //开始上传文件
                    startFileUpload();
                }


//                upKey = QiniuUploadUtils.getQiNiuKey(); //获取key
//                upToken = QiniuUploadUtils.getUpToken(); //获取token
//                KLog.i("upToken" + upToken);
//                //单独传递一张看是否七牛成功
////                for(int i=0;i<selImageList.size();i++){
//                fileUploadSize = String.valueOf(selImageList.get(0).size);
//                fileUploadType = selImageList.get(0).mimeType;
//                fileUploadName = selImageList.get(0).name;
//
//                qiniuUploadUtils.uploadMoreFile(upToken, upKey, selImageList.get(0).path, new QiniuUploadUtils.qiNiuListenter() {
//                    @Override
//                    public void uploadSuccess(String key) {
//                        //新key=path
//                        KLog.i("uploadSuccess" + key);
//                        keyPath = key;
//                        //上传文件
//                        mDownloadUrlPresenter.UploadFileData(getFileBeanData());
//                    }
//
//                    @Override
//                    public void uploadFail() {
//                        ToastUtils.showToastLong(ActDocumentDetail.this, "上传文件失败");
//                        KLog.i("uploadFail");
//                    }
//
//                    @Override
//                    public void setFileProgress(double percent) {
//                        KLog.i("percent" + percent);
//                    }
//                });
            } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
                //预览图片返回
                if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                    images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                    if (images != null) {
                        selImageList.clear();
                        selImageList.addAll(images);
                        //开始上传文件
                        startFileUpload();
                    }
                }
            }
        }
    }

    /**
     * 点击完成执行文件上传
     */
    public void startFileUpload() {
        /**
         * 将字段保存到本地数据库
         */
        long systemTime = System.currentTimeMillis();
        for (int i = 0; i < selImageList.size(); i++) {
            UploadInfoBean uploadInfoBean = new UploadInfoBean();
            uploadInfoBean.id = i + (int) systemTime;
            uploadInfoBean.fileId = i;
            uploadInfoBean.filePath = selImageList.get(i).path;
            uploadInfoBean.fileName = selImageList.get(i).name;
            uploadInfoBean.fileSize = String.valueOf(selImageList.get(i).size);
            uploadInfoBean.fileType = selImageList.get(i).mimeType;
            uploadInfoBean.fileFinish = "1"; //正在进行的上传文件
            uploadInfoBean.isUploading = true;
            mUploadInfoList.add(uploadInfoBean);
        }
        try {
            mUploadDao.saveList(mUploadInfoList);
            List<UploadInfoBean> mUploadInfoBeen = mUploadDao.query("fileFinish", "1");
            KLog.i("savelist" + mUploadInfoBeen.size());
        } catch (Exception e) {
            e.printStackTrace();
        }


        //for循环启动服务
        for (UploadInfoBean uploadinfobean : mUploadInfoList) {
            Intent intent = new Intent(this, DownloadService.class);
            intent.setAction(DownloadService.ACTION_START);
            intent.putExtra("uploadinfobean", uploadinfobean);
            intent.putExtra("runThreadCount", mUploadInfoList.size());
            startService(intent);
        }
    }


//    /**
//     * 上传文件bean
//     */
//    public ProjectFileReq getFileBeanData() {
//        ProjectFileReq projectFileReq = new ProjectFileReq();
//        projectFileReq.fileIdValue = fileId;
//        if (null != fileUploadName) {
//            projectFileReq.fileNameValue = fileUploadName;
//        } else {
//            projectFileReq.fileNameValue = "no-name";
//        }
//
//        projectFileReq.filePathValue = keyPath;
//        KLog.i("fileSize" + fileUploadSize);
//        projectFileReq.fileSizeValue = fileUploadSize;
//        projectFileReq.projectIdValue = projectId;
//        return projectFileReq;
//    }

    /**
     * 获取token和key
     */
    public void getUpTokenKey() {
        QiniuUploadUtils.uploadToken();
    }

    /**
     * 广播
     */
    private void initRegister() { //注册广播接收器
        IntentFilter filter = new IntentFilter();
//        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
//                //更新进度条
//                int finished = intent.getIntExtra("finished", 0);
//                int id = intent.getIntExtra("id", 0);
//                mAdapter.updateProgress(id, finished);
//            } else

            if (DownloadService.ACTION_FINISHED.equals(intent.getAction())) {
                fileinfo = (UploadInfoBean) intent.getSerializableExtra("uploadinfobean");
//                Toast.makeText(ActDocumentDetail.this, fileinfo.filePath + "下载完成", Toast.LENGTH_SHORT).show();
                //修改数据库状态
//                Toast.makeText(ActDocumentDetail.this, fileinfo.fileFinish + "上传完成", Toast.LENGTH_SHORT).show();
                KLog.i("上传完成" + fileinfo.fileFinish);

                //上传文件
                mDownloadUrlPresenter.UploadFileData(getFileBeanData2(fileinfo));
            }
        }
    };


    /**
     * 上传文件服务返回的bean
     */
    public ProjectFileReq getFileBeanData2(UploadInfoBean uploadInfoBean) {
        ProjectFileReq projectFileReq = new ProjectFileReq();
        projectFileReq.fileIdValue = fileId;
        if (null != uploadInfoBean.fileName) {
            projectFileReq.fileNameValue = uploadInfoBean.fileName;
        } else {
            projectFileReq.fileNameValue = "no-name";
        }

        projectFileReq.filePathValue = uploadInfoBean.qiniuKeyPath;
        KLog.i("qiniuKeyPath" + uploadInfoBean.qiniuKeyPath);
        projectFileReq.fileSizeValue = uploadInfoBean.fileSize;

        projectFileReq.projectIdValue = projectId;
        return projectFileReq;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
