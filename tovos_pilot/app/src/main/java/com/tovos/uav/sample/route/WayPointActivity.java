package com.tovos.uav.sample.route;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dji.mapkit.core.models.DJILatLng;

import com.example.commonlib.utils.DialogManager;
import com.example.commonlib.utils.LogUtil;
import com.example.commonlib.utils.SPUtils;
import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.bea.MessageEvent;
import com.example.djilib.dji.component.camera.CustomCameraManager;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.TaskBean;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;
import com.tovos.uav.sample.medial.MedialDataActivity;

import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.example.commonlib.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.TreeMap;


import androidx.annotation.Nullable;

import dji.sdk.camera.VideoFeeder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class WayPointActivity extends BaseActivity implements ActivityListener {

    String aircraftName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_widgets);

        EventBus.getDefault().register(this);
        initToolbar();
        initWayModelView();
        initView(this);
        completeWidgetView.onCreate(savedInstanceState);
        mapSetView.setMyMapManager(completeWidgetView.myMapManager);
        mapSetView.setView();
        dissRtkView();
        dissVisionView();
        if (DJIContext.getProductInstance()!=null){
            if (DJIContext.getProductInstance().isConnected()){
                if (DJIContext.getAircraftInstance().getCameras() != null){
                    cameraConnect();
                }

                if (DJIContext.getAircraftInstance().getBatteries() != null){
                    batterysetView.connect();
                }

                if (DJIContext.getAircraftInstance().getFlightController() != null){

                    DialogManager.showSingleButton(this, R.drawable.logo,"警告","在使用自主飞行功能前，请将飞机固件升级到最新版本！！！","确定",false, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // getWaypointMissionOperator();
                            dialog.dismiss();
                        }
                    });

                    initFlightData();
                }

                if (DJIContext.getAircraftInstance().getRemoteController()!=null){
                    remoteControlView.getRemoteManager();
                    remoteControlView.setConnectGroupSytly();
                }
                if (DJIContext.getAircraftInstance().getGimbals()!=null){
                    gimbalSetView.connData();
                }

            }
        }

    }


    @Override
    public void backToMenuView(View view) {
        findViewById(R.id.set_main).setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }


    @Override
    public void setStartMedialDataActivity(List<TaskBean> taskBeanList, int Position) {
        Intent intent = new Intent(this, MedialDataActivity.class);
        TaskBean taskBean = taskBeanList.get(Position);
        // taskBean.setPhotos(customWayPoint.getPhotolist());
        intent.putExtra("data", taskBean);
        startActivity(intent);
    }

    @Override
    public void setRadarView(boolean isChecked) {
        completeWidgetView.setRadarView(isChecked);
    }

    @Override
    public void addWaypointSuccess() {
        completeWidgetView.setWayPointToMap(waypointView.customWayPoint);
    }

    @Override
    public void setRtkView(boolean isFly) {
        rtkView.setFly(isFly);
    }

    @Override
    public void setCustomWaypoint(DJILatLng now) {
        if (waypointView.customWayPoint != null) {
            waypointView.customWayPoint.homeLocation = now;

        }
    }

    @Override
    public void cleanMap() {
        completeWidgetView.cleanMap();
    }


    @Override
    public void setSelectedFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, 111);
    }

    @Override
    public void setDataToFliht(DbUAVRoute uavRoute, List<DbTower> towers) {
            waypointView.setDataToFliht(uavRoute,towers, rtkView.getRtk_altitude(),-1,-1);
    }

    @Override
    public void selectFirstPoint(DbUAVRoute uavRoute, List<DbTower> towers) {

        waypointView.selectFirstPoint(uavRoute,towers, rtkView.getRtk_altitude());
    }


    // 获取文件的真实路径
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            // 用户未选择任何文件，直接返回
            return;
        }
        if (requestCode == 111) {
            taskView.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showRtkView() {
        findViewById(R.id.rtk_rk).setVisibility(View.VISIBLE);
    }
    public void showVisionView() {
        findViewById(R.id.vission_set).setVisibility(View.VISIBLE);
    }
    public void dissVisionView() {
        if (visionSetView.getVisibility() == View.VISIBLE) {
            findViewById(R.id.vission_set).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.vission_set).setVisibility(View.GONE);
        visionSetView.setVisibility(View.GONE);
    }

    public void dissRtkView() {
        if (rtkView.getVisibility() == View.VISIBLE) {
            findViewById(R.id.set_main).setVisibility(View.VISIBLE);
        }
        rtkView.setVisibility(View.GONE);
        findViewById(R.id.rtk_rk).setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        LogUtil.i(this.getClass().getSimpleName(),"event:"+event.getMessage());
        /* Do something */
        if ("productConnect".equals(event.getMessage())) {//产品连接
            batterysetView.connect();
            cameraConnect();
            remoteControlView.getRemoteManager();
            remoteControlView.setConnectGroupSytly();
            initFlightData();
        } else if ("productDisconnect".equals(event.getMessage())) {
            ToastUtils.setResultToToastUtil("产品断开连接");
        } else if ("aircraftConnect".equals(event.getMessage())) {
            DialogManager.showSingleButton(this, R.drawable.logo,"警告","在使用自主飞行功能前，请将飞机固件升级到最新版本！！！","确定",false, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // getWaypointMissionOperator();
                    dialog.dismiss();
                }
            });

            initFlightData();
        } else if ("dissaircraftConnect".equals(event.getMessage())) {
            ToastUtils.setResultToToast("飞机控制器断开连接");
            //myAircraft = null;
            waypointView.dissconnected();
            completeWidgetView.dissconnectCamera();
            gimbalSetView.disConnData();

            TreeMap<String,Object> treeMap2 = new TreeMap<>();
            treeMap2.put("message","flightDisConn");
            EventBus.getDefault().postSticky(treeMap2);

            customCameraManager = null;
            flightControlView.RefreshdissConnectView();
            rtkView.setRtkUI(false);

            dissRtkView();

            dissVisionView();
            visionSetView.setDisConnVision();

            batterysetView.setDisconnAircraftBattery();
            mapSetView.setMap();
            completeWidgetView.destoryHealthInfo();
            completeWidgetView.dissconnectFlight();

        } else if ("batteryConnect".equals(event.getMessage())) {
            batterysetView.connect();
        } else if ("dissbatteryConnect".equals(event.getMessage())) {
            batterysetView.disconnect();
        } else if ("cameraConnect".equals(event.getMessage())) {
            cameraConnect();
        } else if ("disscameraConnect".equals(event.getMessage())) {
            completeWidgetView.dissconnectCamera();
            //司空
            TreeMap<String,Object> treeMap = new TreeMap<>();
            treeMap.put("message","detoryLiveSteam");
            EventBus.getDefault().postSticky(treeMap);

            customCameraManager = null;
        } else if ("remoteControllerConnect".equals(event.getMessage())) {
            Log.i("remoteControllerConnect","remoteControllerConnect");
            remoteControlView.getRemoteManager();
            remoteControlView.setConnectGroupSytly();
        } else if ("dissremoteControllerConnect".equals(event.getMessage())) {
            remoteControlView.setDisConnectGroupSytly();
        } else if ("gimbalConnect".equals(event.getMessage())) {
            gimbalSetView.connData();
        } else if ("dissgimbalConnect".equals(event.getMessage())) {
            gimbalSetView.disConnData();
        } else if ("paylodConnect".equals(event.getMessage())) {
            //payLoadConnect();
        } else if ("disspaylodConnect".equals(event.getMessage())) {
           // payLoadDissConnect();
        }
    }


    public void cameraConnect() {
        if (customCameraManager == null) {
            customCameraManager = new CustomCameraManager(this);
            if (customCameraManager.getCamera()!=null&&customCameraManager.getCamera().size() > 0) {
                //司空
                TreeMap<String,Object> treeMap = new TreeMap<>();
                treeMap.put("message","initCustomLiveStream");
                EventBus.getDefault().postSticky(treeMap);

                for (int i = 0; i < customCameraManager.getCamera().size(); i++) {
                    if(customCameraManager.getCamera().get(i).getDisplayName()=="H20"||customCameraManager.getCamera().get(i).getDisplayName()=="H20T"){
                       // customCameraManager.getCamera().get(i).setCaptureCameraStreamSettings(CameraVideoStreamSource.ZOOM);
                    }
                }


            }
            completeWidgetView.setCameras();
        }else {
            customCameraManager.getCamera();
            completeWidgetView.setCameras();
        }

    }

    public void initFlightData() {
        if (DJIContext.getAircraftInstance().getFlightController() != null) {
            DJIContext.getProductInstance();
            DJIContext.getAircraftInstance();
            aircraftName = DJIContext.getAircraftInstance().getModel().getDisplayName();
            waypointView.connected();
            cameraConnect();
            gimbalSetView.connData();

           // tjKzView.initSpeedView(waypointView.customWayPoint);
            flightControlView.refreshConnectView();
            completeWidgetView.connectSuccess();
            batterysetView.setConnAircraftBattery();
            mapSetView.setMap();

            Log.d("RTK网络服务","连上飞机获取，是否支持RTK："+DJIContext.getAircraftInstance().getFlightController().isRTKSupported());
            if (DJIContext.getAircraftInstance().getFlightController().isRTKSupported()) {
                LogUtil.d("RTK网络服务","准备初始化RTK");
                rtkView.setRtkUI(true);
                showRtkView();

            }
            if (DJIContext.getAircraftInstance().getFlightController().isFlightAssistantSupported()){
                showVisionView();
                visionSetView.setConnVision();
            }

            //司空
            TreeMap<String,Object> treeMap = new TreeMap<>();
            treeMap.put("message","flightConn");
            EventBus.getDefault().postSticky(treeMap);

        }
    }

    private VideoFeeder.VideoFeed mainVideoFeed,secondVideoFeed;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
