package com.download.qiniu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.socks.library.KLog;
import com.tontru.thinkine.R;
import com.tontru.thinkine.ThinkineAppLication;
import com.tontru.thinkine.base.BaseFragment;
import com.tontru.thinkine.db.BaseDao;
import com.tontru.thinkine.db.bean.UploadInfoBean;
import com.tontru.thinkine.widget.RecyclerViewDivider;
import com.tontru.thinkine.widget.SwipeItemLayout;

import java.util.List;

import butterknife.BindView;

import static java.security.AccessController.getContext;

/**
 * @name android_thinkine
 * @class name：com.tontru.thinkine.fragment
 * @class describe
 * @anthor ${bruce} QQ:275762645
 * @time 2017/12/5 15:12
 * @change
 * @chang time
 * @class describe
 */
public class FileUploadProgressFragment extends BaseFragment {
    private static String TAG = "FileUploadProgressFragment";
    @BindView(R.id.rv_upload_list)
    RecyclerView recyclerView;

    UploadFileProgressAdapter uploadFileAdapter;
    private String name;
    private BaseDao<UploadInfoBean> mUploadBeanDao;
    private List<UploadInfoBean> mUploadInfoProceedList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_progress;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new RecyclerViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL, getResources().getDimensionPixelSize(R.dimen.x1), getResources().getColor(R.color.gray)
        ).setFirstDividerVisible(true).setBottomDividerVisible(true));

        initRegister();
        mUploadBeanDao = ThinkineAppLication.getDao(UploadInfoBean.class);
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            name = bundle.get("name").toString();
//            Log.d("FileUploadProgressFragment", name);
//        }

//        if (name.equals("正在上传")) {
        //显示进度
        //查询状态为1的数据
        try {
            mUploadInfoProceedList = mUploadBeanDao.query("fileFinish", "1");
            KLog.i("FileUploadProgressFragment" + mUploadInfoProceedList.size());
            uploadFileAdapter = new UploadFileProgressAdapter(getActivity(), mUploadInfoProceedList);
            recyclerView.setAdapter(uploadFileAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        } else if (name.equals("上传完成")) {
//            //显示名称    2
//            //查询状态为2的数据
//
//        } else if (name.equals("上传失败")) {
//            //显示失败的    3
//            //查询状态为3的数据
//        }


    }

    private void initRegister() { //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_PROGRESS_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);
    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                //更新进度条
                int finished = intent.getIntExtra("finished", 0);
                int fileId = intent.getIntExtra("fileId", 0);
                KLog.i("fileId" + fileId);
                KLog.i("finished" + finished);
                if (uploadFileAdapter != null) {
                    uploadFileAdapter.updateProgress(fileId, finished);
                }
            } else if (DownloadService.ACTION_PROGRESS_FINISHED.equals(intent.getAction())) {
                int filefinishId = intent.getIntExtra("filefinishId", 0);
                KLog.i("filefinishId" + filefinishId);
                if (filefinishId >= 0) {
                    uploadFileAdapter.reovmeUploadFile(filefinishId);
                }

//                UploadInfoBean fileinfo = (UploadInfoBean) intent.getSerializableExtra("uploadinfobean");
                //更新进度为100
//                uploadFileAdapter.updateProgress(fileinfo.fileId, 100);
//                if(uploadFileAdapter!=null){
//                    uploadFileAdapter.reovmeUploadFile(fileinfo.fileId);
//                }
//                Toast.makeText(getActivity(), filefinishId + "下载完成", Toast.LENGTH_SHORT).show();
            }
        }
    };

//    public static FileUploadProgressFragment newInstance(String name) {
//        KLog.i(TAG, name);
//        FileUploadProgressFragment fragment = new FileUploadProgressFragment();
//        Bundle args = new Bundle();
//        args.putString("name", name);
//        fragment.setArguments(args);
//        return fragment;
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }
}
