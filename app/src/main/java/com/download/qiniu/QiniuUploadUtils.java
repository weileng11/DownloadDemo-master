package com.download.qiniu;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.socks.library.KLog;
import com.thinkine.util.SharedPreferencesUtils;
import com.tontru.thinkine.constant.ComParamContact;
import com.tontru.thinkine.constant.RequestUrl;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.ProgressDialogCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONObject;

import java.util.UUID;

/**
 * @name android_thinkine
 * @class name：com.tontru.thinkine.upload
 * @class describe
 * @anthor ${bruce} QQ:275762645
 * @time 2017/12/6 10:52
 * @change
 * @chang time
 * @class describe
 */
public class QiniuUploadUtils {

    private TextView title;  //显示上传结果
    private ImageView image;  //显示下载的图片内容
    private ProgressDialog progressDialog;  //上传进度提示框
    private boolean isProgressCancel;  //网络请求过程中是否取消上传或下载
    private static UploadManager uploadManager;  //七牛SDK的上传管理者
    private UploadOptions uploadOptions;  //七牛SDK的上传选项
    //    private MyUpCompletionHandler mHandler;  //七牛SDK的上传返回监听
    private UpProgressHandler upProgressHandler;  //七牛SDK的上传进度监听
    private UpCancellationSignal upCancellationSignal;  //七牛SDK的上传过程取消监听
    private final static String TOKEN_URL = "http://xxx.xxx.xxx/x/";  //服务器请求token的网址
    private static String uptoken;  //服务器请求Token值
    private static String qiniuKey;  //上传文件的Key值
    private byte[] upLoadData;  //上传的文件

    static Context context;

    public QiniuUploadUtils(Context mcontext) {
        this.context = mcontext;
        uploadManager = new UploadManager(); //初始化第一次，后面不在需要
    }

    public void uploadFiles() {

        upProgressHandler = new UpProgressHandler() {
            /**
             * @param key 上传时的upKey；
             * @param percent 上传进度；
             */
            @Override
            public void progress(String key, double percent) {
                progressDialog.setProgress((int) (upLoadData.length * percent));
            }
        };

        upCancellationSignal = new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return isProgressCancel;
            }
        };
        //定义数据或文件上传时的可选项
        uploadOptions = new UploadOptions(
                null,  //扩展参数，以<code>x:</code>开头的用户自定义参数
                "mime_type",  //指定上传文件的MimeType
                true,  //是否启用上传内容crc32校验
                upProgressHandler,  //上传内容进度处理
                upCancellationSignal  //取消上传信号
        );

//        mHandler = new MyUpCompletionHandler();
    }

    /**
     * 上传图片
     */
    private static volatile boolean isCancelled = false;

    public static void uploadMoreFile(String uptoken, String qiniuKey, String fileUrl, final qiNiuListenter qiniuListenter) {
        isCancelled = false;
//        qiniuKey = getQiNiuKey(); //获取key
//        uploadToken(); //获取uptoken
        uploadManager.put(fileUrl, qiniuKey, uptoken,
                new UpCompletionHandler() {
                    public void complete(String key,
                                         ResponseInfo info, JSONObject res) {
                        Log.i("LxQiniuUploadUtils", key + ",\r\n " + info
                                + ",\r\n " + res.toString());

                        if (info.isOK() == true) {
                            //成功后设置
//                            textview.setText(res.toString());
                            qiniuListenter.uploadSuccess(key);
                        } else {
                            //失败回调
                            qiniuListenter.uploadFail();
                        }
                    }
                }, new UploadOptions(null, null, false, //map 指的是参数
                        new UpProgressHandler() {
                            public void progress(String key, double percent) {
                                qiniuListenter.setFileProgress(percent);
//                                Log.i("LxQiniuUploadUtils", key + ": " + percent);
//                                progressbar.setVisibility(View.VISIBLE);
//                                int progress = (int)(percent*1000);
////											Log.d("LxQiniuUploadUtils", progress+"");
//                                progressbar.setProgress(progress);
//                                if(progress==1000){
//                                    progressbar.setVisibility(View.GONE);
//                                }
                            }

                        }, new UpCancellationSignal() {
                    @Override
                    public boolean isCancelled() {
                        return isCancelled;
                    }
                }));
    }

    /**
     * 获取uuid
     *
     * @return
     */
    private static String getMyUUID() {
//        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        final String tmDevice, tmSerial, tmPhone, androidId;
//        tmDevice = "" + tm.getDeviceId();
//        tmSerial = "" + tm.getSimSerialNumber();
//        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
//        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
//        String uniqueId = deviceUuid.toString();
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        Log.d("debug", "uuid=" + uniqueId);
        return uniqueId;
    }

    /**
     * 拼接key
     */
    public static String getQiNiuKey() {
        //获取系统的当前时间
        long systemTime = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        sb.append(getMyUUID())
                .append(systemTime);
        KLog.i("拼接key" + sb.toString());
        return sb.toString();
    }

    /**
     * 获取七牛上传的token
     */
    public static void uploadToken() {
        EasyHttp.post(RequestUrl.GetUploadToken_Thinkine)
                //登录或者注册后才有
                .params(ComParamContact.Common.UserId, SharedPreferencesUtils.getString(context, ComParamContact.Common.UserId, ""))
//                .params(requestParams)
                .accessToken(true)
                .timeStamp(true)
                .sign(true)
                .cacheKey(context.getClass().getSimpleName())
                .execute(new ProgressDialogCallBack<String>(null, null, true) {

                    @Override
                    public void onError(ApiException e) {
                        super.onError(e);//super.onError(e)必须写不能删掉或者忘记了
//                        onEasyHttpListener.onError(e.getMessage());
                        KLog.e("获取uptoken失败");
                        uptoken = "";
                    }

                    @Override
                    public void onSuccess(String o) {
                        KLog.e("Object：" + o.toString());
                        //获得status
                        try {
                            JSONObject json = new JSONObject(o.toString());
                            uptoken = json.getString("uptoken");
                            KLog.e("uptoken：" + uptoken);
                            setUpToken(uptoken);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static String upToken;

    public static String getUpToken() {
        return upToken;
    }

    public static void setUpToken(String upToken) {
        QiniuUploadUtils.upToken = upToken;
    }

    public interface qiNiuListenter {
        void uploadSuccess(String key);

        void uploadFail();

        void setFileProgress(double percent); //百分比
    }
}
