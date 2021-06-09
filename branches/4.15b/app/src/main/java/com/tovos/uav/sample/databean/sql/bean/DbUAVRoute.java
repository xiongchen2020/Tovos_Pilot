package com.tovos.uav.sample.databean.sql.bean;

import android.util.Log;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


@DatabaseTable(tableName = "task")
public class DbUAVRoute implements Serializable  {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(columnName = "uid")
    private long uid = 0;
    @DatabaseField(columnName = "path")
    private String path = "";
    @DatabaseField(columnName = "Name")
    private String Name= "";//文件名

    @DatabaseField(columnName = "version")
    private String version= "";  //UAVRoute数据版本

    @DatabaseField(columnName = "CreatedTime")
    private String CreatedTime= ""; //航线创建时间

    @DatabaseField(columnName = "Creator")
    private String Creator= ""; //航线创建人

    @DatabaseField(columnName = "Corporation")
    private String Corporation= ""; //创建人公司单位

    @DatabaseField(columnName = "UAVType")
    private String UAVType= "";//航线飞行使用的飞机型号

    @DatabaseField(columnName = "CameraType")
    private String CameraType= "";//拍照使用的相机型号

    @DatabaseField(columnName = "RouteType")
    private String RouteType= "";//航线类型  0，点云规划航线；1，手动学习记录航线

    @DatabaseField(columnName = "BaseLocation")
    private String BaseLocation= "";//地面基站经纬高。

    @DatabaseField(columnName = "Voltage")
    private String Voltage= "";//电压等级

    @DatabaseField(columnName = "LineName")
    private String LineName= "";//线路名称

    @DatabaseField(columnName = "SafetyDis")
    private String SafetyDis= "";

    @DatabaseField(columnName = "ShootingDis")
    private String ShootingDis;
    //格式：必须按照经度、维度、高程的顺序记录，以英文空格作为分隔符。
    //数值要求：经度值纬度值至少保留7位小数，高程值至少保留2位小数。
    //如果无此值，则文件中字段为空。
    private boolean isSelected = false;

    public void showData(){
        Log.e("RouteBean","Name:"+Name);
        Log.e("RouteBean","Version:"+version);
        Log.e("RouteBean","CreatedTime:"+CreatedTime);
        Log.e("RouteBean","Creator:"+Creator);
        Log.e("RouteBean","Corporation:"+Corporation);
        Log.e("RouteBean","BaseLocation:"+BaseLocation);
        Log.e("RouteBean","UAVType:"+UAVType);
        Log.e("RouteBean","CameraType:"+CameraType);
        Log.e("RouteBean","RouteType:"+RouteType);
//        Log.e("RouteBean","list:"+ dbTowerList.size());
//        for (int i = 0; i< dbTowerList.size(); i++){
//            Log.e("RouteBean","+++++++++杆塔列表遍历:"+i+"++++++++++++");
//            //TowerList.get(i).showData();
//        }
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        version = version;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }

    public String getCorporation() {
        return Corporation;
    }

    public void setCorporation(String corporation) {
        Corporation = corporation;
    }

    public String getUAVType() {
        return UAVType;
    }

    public void setUAVType(String UAVType) {
        this.UAVType = UAVType;
    }

    public String getCameraType() {
        return CameraType;
    }

    public void setCameraType(String cameraType) {
        CameraType = cameraType;
    }

    public String getRouteType() {
        return RouteType;
    }

    public void setRouteType(String routeType) {
        RouteType = routeType;
    }

    public String getBaseLocation() {
        return BaseLocation;
    }

    public void setBaseLocation(String baseLocation) {
        BaseLocation = baseLocation;
    }

//    public Collection<DbTower> getList() {
//        return TowerList;
//    }
//
//    public void setList(ArrayList<DbTower> list) {
//        this.TowerList = list;
//    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVoltage() {
        return Voltage;
    }

    public void setVoltage(String voltage) {
        Voltage = voltage;
    }

    public String getLineName() {
        return LineName;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public String getSafetyDis() {
        return SafetyDis;
    }

    public void setSafetyDis(String safetyDis) {
        SafetyDis = safetyDis;
    }

    public String getShootingDis() {
        return ShootingDis;
    }

    public void setShootingDis(String shootingDis) {
        ShootingDis = shootingDis;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
