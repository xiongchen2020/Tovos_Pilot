package com.tovos.uav.sample.databean.sql.bean;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@XStreamAlias("Tower")@DatabaseTable(tableName = "tower")
public class DbTower implements Serializable{

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "tid")
    private long tid;

    @DatabaseField(columnName = "pid")
    private long pid;

    @XStreamAsAttribute() @XStreamAlias("Name")@DatabaseField(columnName = "Name")
    private String Name;//杆塔名

    @DatabaseField(columnName = "TowerCode")
    private String TowerCode;

    private boolean ischecked = false;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTowerCode() {
        return TowerCode;
    }

    public void setTowerCode(String towerCode) {
        TowerCode = towerCode;
    }

    public void showData(){
        Log.e("Tower","Tower Name:"+Name);
        Log.e("Tower","ischecked:"+ischecked);
//        Log.e("Tower","HoverPoint list:"+WayPointList.size());
//        for (int i = 0;i<list.size();i++){
//
//            list.get(i).showData();
//        }
    }

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
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

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }
}
