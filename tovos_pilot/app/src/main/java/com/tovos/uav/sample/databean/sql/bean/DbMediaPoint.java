package com.tovos.uav.sample.databean.sql.bean;

import com.example.commonlib.utils.LogUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "MediaPoint")
public class DbMediaPoint implements Serializable {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "pid")
    private long pid;

    @DatabaseField(columnName = "PhotoPointLocation")
    private String PhotoPointLocation;

    @DatabaseField(columnName = "DisplayName")
    private String DisplayName;

    @DatabaseField(columnName = "MediaName")
    private String MediaName;

    @DatabaseField(columnName = "Code")
    private String Code;

    public void showData(){
        LogUtil.e("PhotoPoint","PhotoPointLocation:"+PhotoPointLocation);
        LogUtil.e("PhotoPoint","DisplayName:"+DisplayName);
    }

    public String getPhotoPointLocation() {
        return PhotoPointLocation;
    }

    public void setPhotoPointLocation(String photoPointLocation) {
        PhotoPointLocation = photoPointLocation;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }


    public String getMediaName() {
        return MediaName;
    }

    public void setMediaName(String mediaName) {
        MediaName = mediaName;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }
}
