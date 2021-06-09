package com.tovos.uav.sample.databean.sql.bean;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


@DatabaseTable(tableName = "HoverPoint")
public class DBHoverPoint implements Serializable {

    @DatabaseField(generatedId = true)
    private long id;


    @DatabaseField(columnName = "hid")
    private long hid;

    @DatabaseField(columnName = "pid")
    private long pid;

    @XStreamAsAttribute() @XStreamAlias("Type")@DatabaseField(columnName = "Type")
    private String Type;//悬停点类型：0：拍照悬停点 1：辅助悬停点

    @DatabaseField(columnName = "Speed")
    private String Speed;

    @DatabaseField(columnName = "HoverLocation")
    private String HoverLocation;//悬停点经纬高。
    //格式：必须按照经度、维度、高程的顺序记录，以英文空格作为分隔符。
    //数值要求：经度值纬度值至少保留7位小数，高程值至少保留2位小数。

    @DatabaseField(columnName = "Number")
    private String Number;

    @DatabaseField(columnName = "UAVHeading")
    private String UAVHeading;//机头角度，范围[-180，180 ]。正北为0度，正东为90度，正南为180度，正西为-90度。拍照悬停点才需要此值。

    @DatabaseField(columnName = "GimbalPitch")
    private String GimbalPitch;//云台上下俯仰角度，范围[-90,30]。水平朝前为0度，垂直朝下为-90度，朝上最大30度。拍照悬停点才需要此值。

    @DatabaseField(columnName = "PicNumber")
    private String PicNumber;//表示在该位置要拍摄几张照片。此字段值为多少，下面就有几个PhotoPoint字段。//

    @DatabaseField(columnName = "RouteHeading")
    private String RouteHeading; //执行完action之后机头朝向下一个点的角度

    @DatabaseField(columnName = "RotationDirection")
    private String RotationDirection; //routeHeading的偏转方向  r 是右转 l 是左转


    public void showData(){
        Log.e("HoverPoint","Type:"+Type);
        Log.e("HoverPoint","HoverLocation:"+HoverLocation);
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getHoverLocation() {
        return HoverLocation;
    }

    public void setHoverLocation(String hoverLocation) {
        HoverLocation = hoverLocation;
    }

//    public Collection<DbAction> getActions() {
//        return dbActionList;
//    }
//
//    public void setActions(List<DbAction> dbActions) {
//        this.dbActionList = dbActions;
//    }
    //
    public String getUAVHeading() {
        return UAVHeading;
    }

    public void setUAVHeading(String UAVHeading) {
        this.UAVHeading = UAVHeading;
    }

    public String getGimbalPitch() {
        return GimbalPitch;
    }

    public void setGimbalPitch(String gimbalPitch) {
        GimbalPitch = gimbalPitch;
    }

    public String getPicNumber() {
        return PicNumber;
    }

    public void setPicNumber(String picNumber) {
        PicNumber = picNumber;
    }

//    public Collection<DbMediaPoint> getList() {
//        return dbMediaPointList;
//    }
//
//    public void setList(List<DbMediaPoint> list) {
//        this.dbMediaPointList = list;
//    }

    public String getRouteHeading() {
        return RouteHeading;
    }

    public void setRouteHeading(String routeHeading) {
        RouteHeading = routeHeading;
    }

    public String getRotationDirection() {
        return RotationDirection;
    }

    public void setRotationDirection(String rotationDirection) {
        RotationDirection = rotationDirection;
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

    public long getHid() {
        return hid;
    }

    public void setHid(long hid) {
        this.hid = hid;
    }
}
