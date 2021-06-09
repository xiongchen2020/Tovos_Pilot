package com.tovos.uav.sample.route.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;

import com.tovos.uav.sample.R;


import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;
import com.tovos.uav.sample.databean.sql.dao.PointDao;
import com.tovos.uav.sample.route.util.adapter.SelectTakeOffAdapter;
import com.tovos.uav.sample.route.view.WaypointView;

import com.tovos.uav.sample.route.view.menu.TjKzView;
import com.tovos.uav.sample.route.waypoint.CustomWayPoint;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomDialogUtil {

    public  Dialog tjkzDialog(Context context, CustomWayPoint customWayPoint){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tjkz_view,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        dialog.setCancelable(false);
        TjKzView tj_ll = view.findViewById(R.id.tj_ll);
        TextView tv_sure = view.findViewById(R.id.tv_sure);
        tj_ll.setCustomWayPoint(customWayPoint);
        tj_ll.initSpeedView();
        tj_ll.setStartFlightHeight();

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customWayPoint.loadData();
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }
    public  Dialog selectPointDialog(Context context, DbUAVRoute uavRoute,List<DbTower> towers,  WaypointView waypointView, float rtkAltitude){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_point,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        dialog.setCancelable(false);
        RecyclerView recyclerView = view.findViewById(R.id.point_list);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        List<DBHoverPoint> hoverPoints =new ArrayList<>();
        int index=0;
        for (int i = 0;i<towers.size();i++){
            if (towers.get(i).isIschecked()){
                PointDao pointDao = new PointDao(context);
                hoverPoints = pointDao.selePiontByPid(towers.get(i).getTid());
                index = i;
                break;
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        SelectTakeOffAdapter selectPointAdapter = new SelectTakeOffAdapter(context,dialog,waypointView,rtkAltitude,uavRoute, towers,hoverPoints,index);
        recyclerView.setAdapter(selectPointAdapter);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }


    }
