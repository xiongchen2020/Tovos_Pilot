package com.tovos.uav.sample.route.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlib.utils.CustomNumberUtils;
import com.example.commonlib.utils.CustomTimeUtils;
import com.example.commonlib.utils.DialogUtils;
import com.example.djilib.dji.DJIContext;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;
import com.tovos.uav.sample.route.util.CustomDialogUtil;
import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.tovos.uav.sample.route.waypoint.CustomWayPoint;
import com.tovos.uav.sample.route.waypoint.CustomWayPointStatus;
import com.tovos.uav.sample.route.waypoint.WapointListener;
import com.tovos.uav.sample.route.waypoint.WayPointFactory;
import com.example.commonlib.utils.ToastUtils;

import java.util.List;

import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.keysdk.KeyManager;

public class WaypointView extends RelativeLayout implements WapointListener {

    public ImageView start, stop;
    public int wayPointPosition = 0;
    public CustomWayPoint customWayPoint;
    int actionType = 0;
    String type = "";
    private Activity activity;
    private ActivityListener listener;
    private Context context;
    CustomDialogUtil customDialogUtil;
    public void setActivity(Activity activity, ActivityListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void createCustromWayPointActivity(String aircraftName) {
        type = "waypoint";
        customWayPoint = WayPointFactory.create(type, activity, this);
    }

    public WaypointView(Context context) {
        super(context);
        initWayModelView(context);
    }

    public WaypointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWayModelView(context);
    }

    public WaypointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWayModelView(context);
    }

    public WaypointView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initWayModelView(context);
    }

    /**
     * 初始化自动化飞行模块
     */
    public void initWayModelView(Context context) {
        //customWayPoint = WayPointFactory.create("waypoint", activity, this);
        this.context = context;
        customDialogUtil = new CustomDialogUtil();
        LayoutInflater.from(context).inflate(R.layout.waypoint_mission_layout, this);
        type = "waypoint";
        start = (ImageView) findViewById(R.id.start);
        stop = (ImageView) findViewById(R.id.stop);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customWayPoint == null) {
                    return;
                }
                if (customWayPoint.isWaypointModel()) {
                    if (start.isSelected()) {
                        customWayPoint.pauseMission();
                    } else {
                        customWayPoint.resumeMission();
                    }
                } else {
                    customWayPoint.startWaypointMission();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customWayPoint == null) {
                    return;
                }

                if (customWayPoint.isWaypointModel()) {
                    customWayPoint.stopWaypointMission();
                }
            }
        });

    }


    public void  selectFirstPoint(DbUAVRoute uavRoute, List<DbTower> towers,  float rtkAltitude){

       customDialogUtil.selectPointDialog(context,uavRoute,towers,this,rtkAltitude);

    }

    public void setDataToFliht(DbUAVRoute uavRoute, List<DbTower> towers, float rtkAltitude, int towerIndex, int pointIndex) {
        if (customWayPoint != null && DJIContext.getAircraftInstance() != null) {
            if (customWayPoint.customWayPointStatus == CustomWayPointStatus.EXECUTING
                    || customWayPoint.customWayPointStatus == CustomWayPointStatus.EXECUTION_PAUSED) {
                ToastUtils.setResultToToast("当前正在执行航迹任务，请稍后再试！");
                return;
            }


            listener.cleanMap();
            if (DJIContext.getAircraftInstance().getModel().getDisplayName() == Model.MATRICE_300_RTK.getDisplayName()) {

                // 创建数据
                final String[] items = new String[]{"普通模式", "高阶模式"};
                // 创建对话框构建器
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // 设置参数
                builder.setCancelable(false);
                builder.setIcon(R.drawable.alert_icon).setTitle("请选择任务模式")
                        .setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 1) {
                                    customWayPoint = WayPointFactory.create("waypoint2.0", activity, WaypointView.this);
                                } else {
                                    customWayPoint = WayPointFactory.create("waypoint", activity, WaypointView.this);
                                }
                                //dialog.dismiss();
                                customWayPoint.initWaypointMissionOperator();
                                getExecutionWayPointMission();
                                setWayPoint( customWayPoint, uavRoute,towers,  rtkAltitude,towerIndex,pointIndex);
                            }
                        });
                builder.create().show();

            }else{
                setWayPoint( customWayPoint, uavRoute,towers,  rtkAltitude,towerIndex,pointIndex);
            }



        } else {
            ToastUtils.setResultToToast("请连上飞机后再试");

        }
    }

    private void setWayPoint(CustomWayPoint customWayPoint,DbUAVRoute uavRoute, List<DbTower> towers, float rtkAltitude,int towerIndex,int pointIndex) {

        customWayPoint.configWayPointMission();
        if ("2".equals(customWayPoint.getCustomWayPointVersion())) {
            customWayPoint.cleanData();
            customWayPoint.setRouteBean(uavRoute,towers,towerIndex,pointIndex);
            customWayPoint.loadWaypoints(rtkAltitude);
            //-------------测试v2-------------------
        } else {
            customWayPoint.cleanData();
            customWayPoint.setRouteBean(uavRoute,towers,towerIndex,pointIndex);
            customWayPoint.loadWaypoints(rtkAltitude);
        }
    }

    @Override
    public void startWayPoint() {

    }

    @Override
    public void setStartOrStop(boolean s) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                start.setSelected(s);
            }
        });
    }

    @Override
    public void setPauseOrResume(boolean pause) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                start.setSelected(pause);
            }
        });
    }

    @Override
    public void downLoadWayPoint() {

    }

    @Override
    public void onDownloadUpdate() {

    }

    @Override
    public void onUploadUpdate() {

    }

    @Override
    public void onExecutionUpdate(int position) {
        wayPointPosition = position;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                ((TextView) findViewById(R.id.flight_pro)).setText("进度:" + position + "/" + customWayPoint.getWayPointSize());
            }
        });

    }

    @Override
    public void onExecutionStart() {
        ToastUtils.setResultToToast("任务开始");
        activity.runOnUiThread(new Runnable() {
            public void run() {

                start.setSelected(true);
            }
        });
    }

    @Override
    public void onExecutionFinish(String djiError) {
        ToastUtils.setResultToToast("任务结束");
        activity.runOnUiThread(new Runnable() {
            public void run() {
                start.setSelected(false);
            }
        });
    }

    @Override
    public void onExecutionStopped() {
        ToastUtils.setResultToToast("任务终止");
        activity.runOnUiThread(new Runnable() {
            public void run() {
                start.setSelected(true);
            }
        });
    }

    @Override
    public void setStartFlightHeight() {
        customDialogUtil.tjkzDialog(context, customWayPoint);
    }

    @Override
    public void setMayMarker() {

        listener.addWaypointSuccess();
    }

    @Override
    public void getDownLoadDataSuccess() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                start.setSelected(true);
                initWayMissionData();
                if (DialogUtils.isWaitingDialogShow()) {
                    DialogUtils.dismissWaitingDialog();
                }
            }
        });
    }

    @Override
    public void getDownLoadDataFail(DJIError djiError) {
        if (DialogUtils.isWaitingDialogShow()) {
            DialogUtils.dismissWaitingDialog();
        }
        ToastUtils.setResultToToast("下载任务失败，原因：" + djiError.getDescription());
    }

    @Override
    public void upLoadFinish() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initWayMissionData();
            }
        });

    }


    public void initWayMissionData() {
        customWayPoint.getAllLength();
        ((TextView) findViewById(R.id.all_length)).setText("总距离: " + CustomNumberUtils.format4(customWayPoint.all_wapoints) + "M");
        ((TextView) findViewById(R.id.flight_length)).setText("预计时间: " + CustomTimeUtils.ScendsToHoursBySec(customWayPoint.getWayPointTotalTime()));
        ((TextView) findViewById(R.id.flight_pro)).setText("进度: 0/" + customWayPoint.getWayPointSize());
    }

    protected void getExecutionWayPointMission() {
        if (KeyManager.getInstance() == null)
            return;
//        DJIKey key = FlightControllerKey.create(FlightControllerKey.FLIGHT_MODE);
//        FlightMode flightMode = (FlightMode) KeyManager.getInstance().getValue(key);
//        if (flightMode != null) {
//            if (flightMode.value() == FlightMode.GPS_WAYPOINT.value()) {
//                if (!customWayPoint.isWaypointModel()) {
//                    DialogUtils.showWaitingDialog(getContext(), "任务同步中。。。");
//                    customWayPoint.downloadWayPointMission();
//                }
//            }
//        }
    }

    public void connected() {
        // createCustromWayPointActivity(DJIContext.getAircraftInstance().getModel().getDisplayName());
        customWayPoint = WayPointFactory.create("waypoint", activity, this);
        type = "waypoint";
        //isSetMap = false;
        customWayPoint.initWaypointMissionOperator();
        getExecutionWayPointMission();
    }

    public void dissconnected() {
        customWayPoint.removeListener();
        customWayPoint = WayPointFactory.create("waypoint", activity, this);
        type = "waypoint";
    }

}
