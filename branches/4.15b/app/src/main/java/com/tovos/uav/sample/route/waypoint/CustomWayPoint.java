package com.tovos.uav.sample.route.waypoint;

import android.app.Activity;

import com.dji.mapkit.core.models.DJILatLng;

import com.example.commonlib.utils.CustomTimeUtils;
import com.example.commonlib.utils.LogUtil;
import com.tovos.uav.sample.MApplication;
import com.tovos.uav.sample.databean.TaskBean;

import com.example.commonlib.utils.FileManager;
import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;
import com.tovos.uav.sample.databean.sql.dao.PointDao;
import com.tovos.uav.sample.route.util.XMLUtil;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import dji.keysdk.DJIKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.KeyListener;

public abstract class CustomWayPoint{

    public Activity activity;
    public float hdSpeed = 3.0f;
    public float zdSpeed = 3.0f;
    public float maxSpeed = 10.0f;
    public ArrayList<String> photolist = new ArrayList<>();
    public List<DbTower> towerList = new ArrayList<>();
    public DbUAVRoute uavRoute;
    public String fileName = "";
    public String statrTime = "";
    public String endTime = "";
    public String time_type = "yyyy-MM-dd  HH:mm:ss";
    private DJIKey getDataKey;
    public float all_wapoints = 0;
    public DJILatLng homeLocation;
    TaskBean taskBean;
    public int homeHeight;
    public int towerIndex;
    public int pointIndex;
    public CustomWayPointStatus customWayPointStatus = CustomWayPointStatus.UNKNOWN;
    public abstract void initWaypointMissionOperator();

    public abstract String getCustomWayPointVersion();

    public abstract void configWayPointMission();

    public abstract DJILatLng getFirstPoint();

    public abstract void startWaypointMission();

    public abstract void stopWaypointMission();

    public abstract void resumeMission();

    public abstract void pauseMission();

    public abstract void addListener();

    public abstract void removeListener();

    public abstract void cleanData();

    public abstract float getAllLength();

    public abstract void loadWaypoints(float rtkAltitude);

    public abstract int getWayPointSize();

    public abstract float getWayPointTotalTime();

    public abstract void downloadWayPointMission();

    public abstract void setQfPointHeight(int height);

    public abstract void loadData();

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public float getZdSpeed() {
        return zdSpeed;
    }

    public void setZdSpeed(float zdSpeed) {
        this.zdSpeed = zdSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getHomeHeight() {
        return homeHeight;
    }

    public void setHomeHeight(int homeHeight) {
        this.homeHeight = homeHeight;
    }

    public float getHdSpeed() {
        return hdSpeed;
    }

    public void setHdSpeed(float hdSpeed) {
        this.hdSpeed = hdSpeed;
    }

    public List<DbTower> getTowerList() {
        return towerList;
    }

    public int getTowerIndex() {
        return towerIndex;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public abstract boolean isWaypointModel();

    public DJILatLng getLocation(int position, int j){
        DJILatLng djiLatLng = null;
        if (towerList.size()!=0){
            long tid = towerList.get(position).getTid();
            PointDao pointDao = new PointDao(getActivity());
            List<DBHoverPoint> dbHoverPoints = pointDao.selePiontByPid(tid);
            DBHoverPoint item = dbHoverPoints.get(j);
            String location = item.getHoverLocation();
            if (location!=null&&location!=""){
                String[] locations = location.split(" ");
                LogUtil.d(" newHeight:"+location +"   "+locations.length);
                double lat = Double.valueOf(locations[0]);
                double lng = Double.valueOf(locations[1]);
                Float height = Float.valueOf(locations[2]);
                djiLatLng = new DJILatLng(lng,lat,height);
            }

        }

        return djiLatLng;
    }

    public void setRouteBean(DbUAVRoute UAVRoute,List<DbTower> towers,int towerIndex,int pointIndex){
        //waypointList = ChangeToWayPointList(routeBean);
        this.uavRoute = UAVRoute;
        this.towerIndex = towerIndex;
        this.pointIndex = pointIndex;
        this.towerList = towers;
        fileName = uavRoute.getName();
        LogUtil.d("waypoint 加载航线任务");
    }


    public void saveTask() {
        LogUtil.d("waypoint 保存飞行记录");
        taskBean = new TaskBean();
        taskBean.setName(fileName);
        if (statrTime==""){
            taskBean.setStatrtime(0L);
        }else{
            taskBean.setStatrtime(CustomTimeUtils.dateToLong(CustomTimeUtils.stringToDate(statrTime, "yyyy-MM-dd  HH:mm:ss")));
        }
        long endLongTime =  CustomTimeUtils.dateToLong(CustomTimeUtils.stringToDate(endTime, "yyyy-MM-dd  HH:mm:ss"))+3000L;
        taskBean.setEndtime(endLongTime);
        taskBean.setWaypointPicList(photolist);
        taskBean.setMileage(all_wapoints);
        FileManager fileManager = new FileManager();
        File file = fileManager.createDirectory(MApplication.picPath +fileName+"_"+statrTime+"_"+endTime+"/");
        XMLUtil.TaskToXML(taskBean);
        LogUtil.d("waypoint 保存飞行记录结束");

    }


    public void setContorListener(){
        if (KeyManager.getInstance() == null)
            return;
        getDataKey = FlightControllerKey.create(FlightControllerKey.CURRENT_MODE);
        KeyManager.getInstance().addListener(getDataKey, getDataListener);
    }

    public void removeContorListener(){
        if (KeyManager.getInstance()!=null){
            KeyManager.getInstance().removeListener(getDataListener);
        }
    }

    private KeyListener getDataListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable final Object newValue) {

            String str = "";
            if (oldValue != null){
                str = oldValue.toString();
            }
            LogUtil.d("waypoint 挡位切换，暂停任务");
            if (isWaypointModel()){
                stopWaypointMission();
            }
        }
    };

    public double getAltitudeDifference(double var1,double var2){
        double value = 0.0;
        if (var1 > var2){
            value = var1 - var2;
        }else {
            value = var2 - var1;
        }
        return value;
    }

}
