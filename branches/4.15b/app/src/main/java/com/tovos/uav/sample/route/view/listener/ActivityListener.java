package com.tovos.uav.sample.route.view.listener;

import android.view.View;

import com.dji.mapkit.core.models.DJILatLng;
import com.tovos.uav.sample.databean.TaskBean;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;

import java.util.List;


public interface ActivityListener {

    void backToMenuView(View view);

    void setStartMedialDataActivity(List<TaskBean> taskBeanList, int Position);

    void setSelectedFile();

    void setDataToFliht(DbUAVRoute uavRoute, List<DbTower> towers);

    void selectFirstPoint(DbUAVRoute uavRoute, List<DbTower> towers);

    void setRadarView(boolean isChecked);

    void addWaypointSuccess();

    void setRtkView(boolean isFly);

    void setCustomWaypoint(DJILatLng nowLocation);

    void cleanMap();

}
