package com.download.qiniu;

import android.content.Context;
import android.content.Intent;

import com.download.MyApplication;
import com.download.qiniu.db.BaseDao;
import com.download.qiniu.db.ThreadInfo;
import com.download.qiniu.db.UploadInfoBean;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * use for 下载任务类
 */
public class UploadTask {
    private static final String TAG = "UploadTask";
    private static String upToken;
    private static String upKey;
    private static QiniuUploadUtils qiniuUploadUtils;
    BaseDao<ThreadInfo> mThreadDao;
    BaseDao<UploadInfoBean> mUploadBeanDao;

    private Context mContext = null;
    private UploadInfoBean mFileInfo = null; //图片对象

    //    private ThreadDAO mThreadDAO = null;
//    private FileDAO mFileDAO = null;
    public boolean isPause = false;
    private long mFinished = 0;
    private int mThreadCount = -1;//线程数量
    private List<DownloadThread> mThreadList = null;//线程集合
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();//线程池

    public UploadTask(Context mContext, UploadInfoBean fileInfo, int mThreadCount) {
        this.mContext = mContext;
        this.mFileInfo = fileInfo;
        this.mThreadCount = mThreadCount;
//        mThreadDAO = ThreadDAOImpl.getInstance();
//        mFileDAO = FileDAOImpl.getInstance();
//        KLog.i("UploadInfoBean" + mFileInfo.fileId);
        //初始化数据库
        mThreadDao = MyApplication.getDao(ThreadInfo.class);
        mUploadBeanDao = MyApplication.getDao(UploadInfoBean.class);

        qiniuUploadUtils = new QiniuUploadUtils(mContext);
        //获取Key和uptoken
        upKey = QiniuUploadUtils.getQiNiuKey(); //获取key
        upToken = QiniuUploadUtils.getUpToken(); //获取token
    }

    /**
     * 七流云上传
     */
    public void download() {
        //读取数据库的线程信息  查询文件的线程信息
//        try {
//            List<ThreadInfo> threadInfos = mThreadDao.query("fileUrl", mFileInfo.filePath);
//            KLog.i("threadInfos" + threadInfos.size());
//            if (threadInfos.size() == 0) {
//                for (int i = 0; i < mThreadCount; i++) {
//                    //初始化线程信息对象
//                    ThreadInfo threadInfo = new ThreadInfo();
//                    threadInfo.fileUrl = mFileInfo.filePath;
//                    threadInfo.finish = 0;
//                    threadInfo.end = 0;
//                    threadInfo.start = 0;
//                    //添加到线程信息集合中
//                    threadInfos.add(threadInfo);
//                    //向数据库插入线程信息
//                    mThreadDao.save(threadInfo);
//                }
//            }
//            mThreadList = new ArrayList<>();
//            KLog.i("启动多个线程进行下载" + threadInfos.size());
//            //启动多个线程进行下载
//            for (ThreadInfo thread : threadInfos) {
//                DownloadThread downloadThread = new DownloadThread();
////            downloadThread.start();
//                UploadTask.sExecutorService.execute(downloadThread);
//                //添加线程到集合中
//                mThreadList.add(downloadThread);
//                KLog.i("启动多个线程进行下载 次数");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        for (int i = 0; i < 1; i++) {
            //初始化线程信息对象
//            ThreadInfo threadInfo = new ThreadInfo();
//            threadInfo.fileUrl = mFileInfo.filePath;
//            threadInfo.finish = 0;
//            threadInfo.end = 0;
//            threadInfo.start = 0;
//            //添加到线程信息集合中
//            threadInfos.add(threadInfo);
//            //向数据库插入线程信息
//            mThreadDao.save(threadInfo);

            DownloadThread downloadThread = new DownloadThread();
//            downloadThread.start();
            UploadTask.sExecutorService.execute(downloadThread);
            //添加线程到集合中
//            mThreadList.add(downloadThread);

//            KLog.i("启动多个线程进行下载 次数");
        }

    }


    /**
     * 数据下载线程
     */
    class DownloadThread extends Thread {
        //        private ThreadInfo threadInfo = null;
        public boolean isFinished = false;//标示线程是否执行完毕

//        public DownloadThread(ThreadInfo threadInfo) {
//            this.threadInfo = threadInfo;
//        }

        @Override
        public void run() {
            qiniuUploadUtils.uploadMoreFile(upToken, upKey, mFileInfo.filePath, new QiniuUploadUtils.qiNiuListenter() {
                @Override
                public void uploadSuccess(String key) {
//                    KLog.i("mThreadCount" + mThreadCount);
                    //标识线程执行完毕
                    isFinished = true;
                    //检查下载任务是否完成
                    checkAllThreadFinished(key);
                }

                @Override
                public void uploadFail() {
                    //保存失败的状态，插入本地数据库
                    try {
                        if (mFileInfo != null) {
                            UploadInfoBean uploadInfoBean = new UploadInfoBean();
                            uploadInfoBean.id = mFileInfo.id;
                            uploadInfoBean.fileFinish = "3";
                            uploadInfoBean.fileId = 0;
                            uploadInfoBean.fileName = mFileInfo.fileName;
                            uploadInfoBean.fileType = mFileInfo.fileType;
                            uploadInfoBean.fileSize = mFileInfo.fileSize;
                            uploadInfoBean.filePath = mFileInfo.filePath;
                            mUploadBeanDao.update(uploadInfoBean);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void setFileProgress(double percent) {
//                    KLog.i("mFileInfo.fileId" + mFileInfo.fileId);
//                    long time = System.currentTimeMillis();
//                    if (System.currentTimeMillis() - time > 1000) {//减少UI负载
//                        time = System.currentTimeMillis();
//                    for(int i=0;i<mThreadCount;i++){
                        //发送进度到Activity
                        Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                        int mpercent = (int) (percent * 1000 / 10);
//                        KLog.i("mpercent" + mpercent + "fileId" + mFileInfo.fileId + "mThreadCount i");
                        intent.putExtra("finished", (int) mpercent);
                        intent.putExtra("fileId", mFileInfo.fileId);
                        mContext.sendBroadcast(intent);
//                    }

                    }
//                }
            });
        }
    }

    /**
     * 判断所有线程是否都执行完毕
     */
    private synchronized void checkAllThreadFinished(String qiniuKey) {
//        boolean allFinished = true;
//        //遍历线程集合，判断线程是否都执行完毕
//        for (DownloadThread thread : mThreadList) {
//            if (!thread.isFinished) {
//                allFinished = false;
//                break;
//            }
//        }
//        if (allFinished) {
        try {
            //删除线程信息
//                mThreadDao.deleteById("fileUrl", mFileInfo.filePath);
//                KLog.i("mThreadDao" +   mThreadDao.queryAll().size());

            //根据url修改对应的状态信息
//                mFileDAO.insertFile(mFileInfo);
            if (mFileInfo != null) {
                UploadInfoBean uploadInfoBean = new UploadInfoBean();
                uploadInfoBean.id = mFileInfo.id;
                uploadInfoBean.fileId = mFileInfo.fileId;
                uploadInfoBean.fileFinish = "2";
                uploadInfoBean.fileName = mFileInfo.fileName;
                uploadInfoBean.fileType = mFileInfo.fileType;
                uploadInfoBean.fileSize = mFileInfo.fileSize;
                uploadInfoBean.filePath = mFileInfo.filePath;
                uploadInfoBean.qiniuKeyPath = qiniuKey;
                mUploadBeanDao.update(uploadInfoBean);

                //发送广播通知UI下载结束
                Intent intent = new Intent(DownloadService.ACTION_FINISHED);
                intent.putExtra("uploadinfobean", uploadInfoBean);
                mContext.sendBroadcast(intent);

                //发送广播通知UI下载结束
                Intent intent1 = new Intent(DownloadService.ACTION_PROGRESS_FINISHED);
                intent1.putExtra("uploadinfobean", uploadInfoBean);
                mContext.sendBroadcast(intent1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    public interface UploadStatusLitenter {
        void uploadTaskSuccess();

        void uploadTaskFail();
    }
}

