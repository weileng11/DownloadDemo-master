package com.download.qiniu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;
import com.tontru.thinkine.R;
import com.tontru.thinkine.db.BaseDao;
import com.tontru.thinkine.db.bean.UploadInfoBean;

import java.util.List;

/**
 * @name android_thinkine
 * @class name：com.tontru.thinkine.adapter
 * @class describe 正在上传适配器
 * @anthor ${bruce} QQ:275762645
 * @time 2017/12/5 19:25
 * @change
 * @chang time
 * @class describe
 */
public class UploadFileProgressAdapter extends RecyclerView.Adapter<UploadFileProgressAdapter.ViewHolder> {
    private static final String TAG = "UploadFileProgressAdapter";

    private List<UploadInfoBean> mUploadInfoBeenList;
    private Context mcContext;
    UploadInfoBean fileInfo;
    private BaseDao<UploadInfoBean> mUploadBeanDao;


    public UploadFileProgressAdapter(Context context, List<UploadInfoBean> uploadInfoBeen) {
        this.mUploadInfoBeenList = uploadInfoBeen;
        this.mcContext = context;
        KLog.i("UploadFileAdapter" + mUploadInfoBeenList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder((LayoutInflater.from(parent.getContext())).inflate(R.layout.fragment_progress_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        fileInfo = mUploadInfoBeenList.get(position);
        if (!TextUtils.isEmpty(fileInfo.fileName)) {
            holder.txvFileName.setText(fileInfo.fileName);
        }
        if (fileInfo.getFileProgress() > 0) {
            int pro = (int) fileInfo.getFileProgress();
            holder.txvProgress.setText(new StringBuffer().append(pro).append("%"));
        } else {
            holder.txvProgress.setText(new StringBuffer().append(0).append("%"));
        }

    }

    @Override
    public int getItemCount() {
        return mUploadInfoBeenList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView startButton;
        private TextView txvFileName;
        private TextView txvProgress;
        private TextView progressText;
        private ImageView ivUploadImage;
        private Button filePauseBtn, fileStartBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ivUploadImage = (ImageView) itemView.findViewById(R.id.iv_upload_image);
            txvFileName = (TextView) itemView.findViewById(R.id.txv_file_name);
            txvProgress = (TextView) itemView.findViewById(R.id.txv_progress);
            filePauseBtn = (Button) itemView.findViewById(R.id.file_pause_btn);
            fileStartBtn = (Button) itemView.findViewById(R.id.file_start_btn);
            filePauseBtn.setOnClickListener(this);
            fileStartBtn.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.file_pause_btn:
//                    if (fileInfo.isUploading) {
////                        filePauseBtn.setText("开始");
//
////                        mListener.onStartButtonClick(task, false);
//                    } else {
//                        fileBtn.setText("暂停");
                    Intent intent = new Intent(mcContext, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);
                    intent.putExtra("uploadinfobean", fileInfo);
                    mcContext.startService(intent);
//                        mListener.onStartButtonClick(task, true);
//                    }
                    break;
                case R.id.file_start_btn:
                    Intent intent1 = new Intent(mcContext, DownloadService.class);
                    intent1.setAction(DownloadService.ACTION_STOP);
                    intent1.putExtra("uploadinfobean", fileInfo);
                    mcContext.startService(intent1);
                    break;

            }
        }
    }

    /**
     * 更新列表项中的进度条
     *
     * @param id       id
     * @param progress 进度
     */
    public void updateProgress(int id, long progress) {
        KLog.i("updateProgressid==============" + id + "mUploadInfoBeenList.size" + mUploadInfoBeenList.size());
        if (mUploadInfoBeenList.size() == 1) {
            UploadInfoBean fileInfo = mUploadInfoBeenList.get(0);
            fileInfo.setFileProgress(progress);
            notifyDataSetChanged();
        } else if (mUploadInfoBeenList.size() == 2) {
            if (id == 0) {
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            } else if (id == 1) {
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            } else if (id == 2) {
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id - 1);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            }
        } else if (mUploadInfoBeenList.size() == 3) {
            if (id == 3) {
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id - 1);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            } else if (id == 4) {
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id - 2);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            } else if (id == 4) {
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id - 3);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            }else{
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            }
        } else if (mUploadInfoBeenList.size() == 4) {
            if (id == 4) {
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id - 1);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            } else if (id == 5) {
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id - 2);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            }else{
                UploadInfoBean fileInfo = mUploadInfoBeenList.get(id);
                fileInfo.setFileProgress(progress);
                notifyDataSetChanged();
            }
        } else if (mUploadInfoBeenList.size() == 5) {
            UploadInfoBean fileInfo = mUploadInfoBeenList.get(id);
            fileInfo.setFileProgress(progress);
            notifyDataSetChanged();
        }

    }

    /**
     * 上传到100%移除当前记录
     */
    public void reovmeUploadFile(int id) {
        UploadInfoBean fileInfo = null;
        for (int i = 0; i < mUploadInfoBeenList.size(); i++) {
            if (id == mUploadInfoBeenList.get(i).fileId) {
                fileInfo = mUploadInfoBeenList.get(i);
            }
        }
        if (fileInfo != null) {
            mUploadInfoBeenList.remove(fileInfo);
            notifyDataSetChanged();
        }

//        //查询数据库数据
//        try {
//            String[] keyId = {"fileId", "fileFinish"};
//            String[] valueId = {String.valueOf(id), "1"};
//            List<UploadInfoBean> uploadInfoList = mUploadBeanDao.query(keyId, valueId);
//            UploadInfoBean fileInfo = null;
//            KLog.i(TAG, "数据库返回" + uploadInfoList.size());
//            for (int i = 0; i < mUploadInfoBeenList.size(); i++) {
//                if (uploadInfoList.get(0).fileId == mUploadInfoBeenList.get(i).fileId) {
//                    fileInfo = mUploadInfoBeenList.get(id);
//                }
//            }
//            if(fileInfo!=null){
//                mUploadInfoBeenList.remove(fileInfo);
//                notifyDataSetChanged();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        UploadInfoBean fileInfo = null;
//        if (mUploadInfoBeenList.size() == 1) {
//            id = 0;
//            fileInfo = mUploadInfoBeenList.get(id);
//        } else if (mUploadInfoBeenList.size() == 2) {
//            fileInfo = mUploadInfoBeenList.get(id - 1);
//        }else{
//        UploadInfoBean  fileInfo = mUploadInfoBeenList.get(id);
//        }

//        mUploadInfoBeenList.remove(fileInfo);
//        mUploadInfoBeenList.remove(id);
//        notifyItemRemoved(id);
//        notifyDataSetChanged();

//        documentResponseList.remove(deletePosition);
//        documentDetailAdapter.notifyItemRemoved(deletePosition);
//        documentDetailAdapter.notifyDataSetChanged();
    }

}
