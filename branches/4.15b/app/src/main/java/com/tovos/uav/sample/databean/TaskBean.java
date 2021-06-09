package com.tovos.uav.sample.databean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaskBean implements Serializable {

    private String path;
    private String name;
    private long statrtime;
    private long endtime;
    private float mileage = 0     ;
    private List<String> waypointPicList = new ArrayList<>();
    private List<String> djiPicList = new ArrayList<>();
    private boolean ischecked = false;

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    private ArrayList<String> photos = new ArrayList<>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStatrtime() {
        return statrtime;
    }

    public void setStatrtime(long statrtime) {
        this.statrtime = statrtime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public float getMileage() {
        return mileage;
    }

    public void setMileage(float mileage) {
        this.mileage = mileage;
    }

    public List<String> getWaypointPicList() {
        return waypointPicList;
    }

    public void setWaypointPicList(List<String> waypointPicList) {
        this.waypointPicList = waypointPicList;
    }

    public List<String> getDjiPicList() {
        return djiPicList;
    }

    public void setDjiPicList(List<String> djiPicList) {
        this.djiPicList = djiPicList;
    }
}
