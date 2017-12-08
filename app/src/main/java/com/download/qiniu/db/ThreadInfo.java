package com.download.qiniu.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "thread_dao")
public class ThreadInfo extends IOperateType implements Serializable {
    @DatabaseField(id = true, canBeNull = false)
    public int id;
    @DatabaseField(canBeNull = false)
    public String fileUrl;
    @DatabaseField(canBeNull = false)
    public long start;
    @DatabaseField(canBeNull = false)
    public long end;
    @DatabaseField(canBeNull = false)
    public long finish;


}
