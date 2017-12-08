package com.download.qiniu;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.download.qiniu.db.UploadInfoBean;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 上传图片服务类
 */
public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    //上传进度结束下载
    public static final String ACTION_PROGRESS_FINISHED = "ACTION_PROGRESS_FINISHED";
    //文档文件结束下载
    public static final String ACTION_FINISHED = "ACTION_FINISHED";
    //更新UI
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    //    //下载路径
//    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
    //初始化
    private static final int MSG_INIT = 0;
    //下载任务集合
    private Map<Integer, UploadTask> mTasks = new LinkedHashMap<>();
    public  int runThreadCount = 0;
//    private InitThread mInitThread;
    public UploadInfoBean fileInfo;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获得Activity传来的参数
        if (ACTION_START.equals(intent.getAction())) {
            fileInfo = (UploadInfoBean) intent.getSerializableExtra("uploadinfobean");
            Log.i(TAG, "onStartCommand: ACTION_START：" + fileInfo.toString());
            runThreadCount = intent.getIntExtra("runThreadCount", 0);
//            KLog.i("runThreadCount"+runThreadCount);
            startUploadReqData(fileInfo);

//            mInitThread = new InitThread(fileInfo);
//            UploadTask.sExecutorService.execute(mInitThread);
        } else if (ACTION_STOP.equals(intent.getAction())) {
//            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
//            Log.i(TAG, "onStartCommand:ACTION_STOP：" + fileInfo.toString());
//            //从集合中取出下载任务
//            UploadTask mDownloadTask = mTasks.get(fileInfo.getId());
//            if (mDownloadTask != null) {
//                //停止下载任务
//                mDownloadTask.isPause = true;
//            }
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 开始执行上传图片的类
     */
    public void startUploadReqData(UploadInfoBean fileinfo) {
//        UploadInfoBean fileinfo = (UploadInfoBean) msg.obj;
//        Log.i(TAG, "init:" + fileinfo.toString());
        //启动下载任务
        UploadTask mDownloadTask = new UploadTask(DownloadService.this, fileinfo, runThreadCount);
        mDownloadTask.download();
        //将下载任务添加到集合中
        mTasks.put(fileinfo.fileId, mDownloadTask);
    }

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_INIT:
//                    startUploadReqData(msg);
//                    break;
//            }
//        }
//    };

    /**
     * 初始化 子线程
     */
//    class InitThread extends Thread {
//        private UploadInfoBean tFileInfo = null;
//
//        public InitThread(UploadInfoBean tFileInfo) {
//            this.tFileInfo = tFileInfo;
//        }
//
//        @Override
//        public void run() {
//            try {
//                mHandler.obtainMessage(MSG_INIT, tFileInfo).sendToTarget();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
