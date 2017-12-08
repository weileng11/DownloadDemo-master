package com.download.qiniu.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * @name android_thinkine
 * @class name：com.tontru.thinkine.db.bean
 * @class describe  文档上传图片保存图片信息类
 * @anthor ${bruce} QQ:275762645
 * @time 2017/12/7 9:49
 * @change
 * @chang time
 * @class describe
 */
@DatabaseTable(tableName = "upload_info_bean")
public class UploadInfoBean extends IOperateType implements Serializable {
    @DatabaseField(id = true, canBeNull = false)
    public int id;
    //文件id
    @DatabaseField(canBeNull = true)
    public int fileId;
    //文件名称
    @DatabaseField(canBeNull = true)
    public String fileName;
    //文件大小
    @DatabaseField(canBeNull = true)
    public String fileSize;
    //文件进度
    @DatabaseField(canBeNull = true)
    public long fileProgress;
    //文件路径（处理断点续传）
    @DatabaseField(canBeNull = true)
    public String filePath;
    //文件类型
    @DatabaseField(canBeNull = true)
    public String fileType;
    //文件是否已经下载完成  默认为1，在上传 2 代表已经完成  3.代表失败
    @DatabaseField(canBeNull = true)
    public String fileFinish;
    //qiniuKeyPath 七牛返回的路径
    @DatabaseField(canBeNull = true)
    public String qiniuKeyPath;
    //是否正在上传
    @DatabaseField(canBeNull = true)
    public boolean isUploading;

    public long getFileProgress() {
        return fileProgress;
    }

    public void setFileProgress(long fileProgress) {
        this.fileProgress = fileProgress;
    }
}
