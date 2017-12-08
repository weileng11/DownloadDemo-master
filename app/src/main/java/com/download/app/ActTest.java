package com.download.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.download.entity.FileInfo;
import com.download.service.DownloadService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @name MultiThreadDownloadDemo-master
 * @class name：com.example.luckychuan.downloaddemo
 * @class describe
 * @anthor ${bruce} QQ:275762645
 * @time 2017/12/6 19:57
 * @change
 * @chang time
 * @class describe
 */
public class ActTest extends Activity {
    private static List<FileInfo> fileInfoList = null;
    public static final String[] videoPaths = {"http://baobab.wdjcdn.com/14564977406580.mp4",
            "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4",
            "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4"};
    private FileInfo fileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test);
        //创建文件信息集合
        fileInfoList = new ArrayList<FileInfo>();
        for (int i = 0; i < videoPaths.length; i++) {
            String videopath = videoPaths[i];
//            String videoTitle = videopath.substring(videopath.lastIndexOf('/') + 1);//URL中的视频名称
            String videoExtension = videopath.substring(videopath.lastIndexOf('.'));//URL中的视频后缀名
            UUID uuid = UUID.randomUUID();//生成随机文件名
            fileInfo = new FileInfo(i, videopath, uuid + videoExtension, 0, 0);
            fileInfoList.add(fileInfo);
        }

    }

    public void test(View v) {
        for (FileInfo fileInfo : fileInfoList) {
            Intent intent = new Intent(this, DownloadService.class);
            intent.setAction(DownloadService.ACTION_START);
            intent.putExtra("fileInfo", fileInfo);
            startService(intent);
        }
    }

    public void tz(View v) {
        Intent intent = new Intent(ActTest.this, MainActivity.class);
        startActivity(intent);
    }

}
