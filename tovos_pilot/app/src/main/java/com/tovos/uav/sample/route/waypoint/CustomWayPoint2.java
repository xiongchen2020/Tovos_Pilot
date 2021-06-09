package com.tovos.uav.sample.route.waypoint;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.Nullable;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.dji.mapkit.core.models.DJILatLng;
import com.example.commonlib.utils.CustomTimeUtils;
import com.example.commonlib.utils.DialogUtils;
import com.example.commonlib.utils.LogUtil;
import com.example.djilib.dji.djierror.WayPointV2Error;
import com.tovos.uav.sample.R;

import com.example.commonlib.utils.DialogManager;
import com.example.commonlib.utils.ToastUtils;
import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.bean.DbMediaPoint;
import com.tovos.uav.sample.databean.sql.dao.MedailDao;
import com.tovos.uav.sample.databean.sql.dao.PointDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dji.common.error.DJIError;
import dji.common.error.DJIWaypointV2Error;
import dji.common.mission.waypointv2.Action.ActionDownloadEvent;
import dji.common.mission.waypointv2.Action.ActionExecutionEvent;
import dji.common.mission.waypointv2.Action.ActionUploadEvent;
import dji.common.mission.waypointv2.Action.WaypointV2Action;
import dji.common.mission.waypointv2.WaypointV2;
import dji.common.mission.waypointv2.WaypointV2Mission;
import dji.common.mission.waypointv2.WaypointV2MissionDownloadEvent;
import dji.common.mission.waypointv2.WaypointV2MissionExecutionEvent;
import dji.common.mission.waypointv2.WaypointV2MissionState;
import dji.common.mission.waypointv2.WaypointV2MissionTypes;
import dji.common.mission.waypointv2.WaypointV2MissionUploadEvent;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointV2ActionListener;
import dji.sdk.mission.waypoint.WaypointV2MissionOperator;
import dji.sdk.mission.waypoint.WaypointV2MissionOperatorListener;
import dji.sdk.sdkmanager.DJISDKManager;

import static com.example.commonlib.utils.ToastUtils.setResultToToast;

public class CustomWayPoint2 extends CustomWayPoint implements WaypointV2MissionOperatorListener, WaypointV2ActionListener {

    private WaypointV2MissionTypes.MissionFinishedAction mFinishedAction = WaypointV2MissionTypes.MissionFinishedAction.GO_HOME;
    private WaypointV2MissionTypes.WaypointV2HeadingMode mHeadingMode = WaypointV2MissionTypes.WaypointV2HeadingMode.WAYPOINT_CUSTOM;
    private WaypointV2MissionTypes.MissionGotoWaypointMode firstMode = WaypointV2MissionTypes.MissionGotoWaypointMode.SAFELY;
    private WaypointV2MissionTypes.WaypointV2FlightPathMode pathMode = WaypointV2MissionTypes.WaypointV2FlightPathMode.GOTO_POINT_STRAIGHT_LINE_AND_STOP;//.GOTO_POINT_CURVE_AND_STOP;
    public static WaypointV2Mission.Builder waypointMissionBuilder;
    private List<WaypointV2> waypointList = new ArrayList<>();
    private List<WaypointV2Action> v2Actions = new ArrayList<>();
    private WaypointV2MissionOperator waypointV2MissionOperator;
    private boolean canUploadAction;
    private WapointListener listener;
    private int qfHeight = 0;
    private float rtkAltitude = 0;

    public CustomWayPoint2(Activity activity, WapointListener listener) {
        this.activity = activity;
        this.listener = listener;
        configWayPointMission();

    }

    @Override

    public void initWaypointMissionOperator() {
        getWaypointMissionOperator();
    }

    @Override
    public String getCustomWayPointVersion() {
        return "2";
    }

    public void getWaypointMissionOperator() {
        if (waypointV2MissionOperator == null) {

            MissionControl missionControl = DJISDKManager.getInstance().getMissionControl();
            if (missionControl != null) {
                waypointV2MissionOperator = missionControl.getWaypointMissionV2Operator();
                addListener();
            }
        }
    }


    private WaypointV2 file2Waypoint(DBHoverPoint hoverPoint, int position, float rtkAltitude) {
        WaypointV2 waypoint = null;
        String location = hoverPoint.getHoverLocation();
        String rotationDirection = hoverPoint.getRotationDirection();
        float speed = 0;
        Log.e("location", "location:" + location);
        Double newHeight = 0.0;

        if (location != null) {
            String[] locations = location.split(" ");
            LogUtil.d("waypoint2 newHeight:"+location +"   "+locations.length);
            double lng = Double.valueOf(locations[0]);
            double lat = Double.valueOf(locations[1]);
            Double height = Double.valueOf(locations[2]);
            if (position == 0) {
                newHeight = Double.valueOf(qfHeight);
            } else {
                newHeight = height - rtkAltitude;
            }



            WaypointV2MissionTypes.WaypointV2TurnMode turnMode = WaypointV2MissionTypes.WaypointV2TurnMode.CLOCKWISE;

            if ("l".equals(rotationDirection)) {//左转
                turnMode = WaypointV2MissionTypes.WaypointV2TurnMode.COUNTER_CLOCKWISE;
            } else if ("r".equals(rotationDirection)) {//右转
                turnMode = WaypointV2MissionTypes.WaypointV2TurnMode.CLOCKWISE;
            }

            if (hoverPoint.getSpeed() != null && !"0".equals(hoverPoint.getSpeed())) {
                speed = Float.valueOf(hoverPoint.getSpeed());
            } else {
                speed = hdSpeed;
            }
            float heading = Float.valueOf(hoverPoint.getUAVHeading());
            waypoint = new WaypointV2.Builder()
                    .setAltitude(newHeight)
                    .setCoordinate(new LocationCoordinate2D(lat, lng))
                    .setHeadingMode(mHeadingMode)
                    .setTurnMode(turnMode)
                    .setHeading(Math.round(heading))
                    .setFlightPathMode(pathMode)
                    .setUsingWaypointAutoFlightSpeed(true)
                    .setUsingWaypointMaxFlightSpeed(true)
                    .setAutoFlightSpeed(speed)
                    .setMaxFlightSpeed(maxSpeed)
                    .setDampingDistance(0.2f)
                    .build();


            List<WaypointV2Action> wappointV2Actions = TestUtilsForWaypoint.addAction2(getActivity(), hoverPoint, position);


            for (int i=0;i<wappointV2Actions.size();i++){
                LogUtil.d("waypoint2 index:"+i+"/"+wappointV2Actions.get(i).toString());
            }
            v2Actions.addAll(wappointV2Actions);
        }

        return waypoint;
    }


    public boolean changeTowerToWayPoint(float rtkAltitude) {
        boolean issuccess = false;
        List<WaypointV2> list = new ArrayList<>();
        int position = 0;

        for (int i = 0; i < towerList.size(); i++) {
            if (towerList.get(i).isIschecked()) {
                PointDao pointDao = new PointDao(getActivity());
                List<DBHoverPoint> hilst = new ArrayList<>();
                hilst.addAll(pointDao.selePiontByPid(towerList.get(i).getTid()));
                if (towerIndex == i){
                    hilst = hilst.subList(pointIndex,hilst.size());
                }
                for (int j = 0; j < hilst.size(); j++) {
                    DBHoverPoint hoverPoint = hilst.get(j);
                    if (hoverPoint != null || hoverPoint.getType() != null) {
                        list.add(file2Waypoint(hoverPoint, position, rtkAltitude));

                        MedailDao medailDao = new MedailDao(getActivity());
                        List<DbMediaPoint> mediaPoints = medailDao.seleMedialByPid(hoverPoint.getHid());

                        if (mediaPoints != null) {
                            if (mediaPoints.size() > 0) {
                                for (int k = 0; k < mediaPoints.size(); k++) {
                                    photolist.add(mediaPoints.get(k).getMediaName());
                                }
                            }
                        }
                        position = position + 1;
                    }
                }
            }
        }

        if (list.size() == 0) {
            setResultToToast("航线点不能为空，请选择航线点！");
            issuccess = false;
        } else if (list.size() < 4) {
            setResultToToast("航线点过少，请配置有效航线！");
            issuccess = false;
        } else if(list.size()>65535){
            setResultToToast("航线点超过最大限制");
            issuccess = false;
        }
        else {
            issuccess = true;
            waypointList = list;
            setResultToToast("航点数:" + waypointList.size());

        }
        return issuccess;
    }


    private void loadMyMission() {


    }

    private void uploadPoints() {
        setResultToToast("航点数：" + waypointV2MissionOperator.getLoadedMission().getWaypointList().size() + "/" + waypointV2MissionOperator.getLoadedMission().getWaypointCount());
        if (waypointV2MissionOperator != null) {

            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.READY_TO_UPLOAD) {

                waypointV2MissionOperator.uploadMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        DialogUtils.dismissWaitingDialog();
                        if (error == null) {
                            LogUtil.d("V2任务测试：uploadPoints");
                            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.READY_TO_UPLOAD) {
                                ToastUtils.setResultToToast("开始上传");
                            }

                            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.READY_TO_EXECUTE) {
                                ToastUtils.setResultToToast("上传完毕");
                            }
                            // ToastUtils.setResultToToast("uploadMission后任务的状态值 " + getWaypointMissionOperator().getCurrentState());

                            canUploadAction = true;
                        } else {
                            setResultToToast("线路文件上传失败, error: " + WayPointV2Error.getError(error.getErrorCode()) + " retrying...");
                        }
                    }
                });
            }
        }

    }

    public void uploadActions() {
        if (waypointV2MissionOperator != null) {
            ToastUtils.setResultToToast("uploadActions状态： " + waypointV2MissionOperator.getCurrentState());
            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.READY_TO_EXECUTE) {
                waypointV2MissionOperator.uploadWaypointActions(v2Actions, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        DialogUtils.dismissWaitingDialog();
                        LogUtil.d("V2任务测试：uploadActions");
                        if (error == null) {
                            setResultToToast("线路文件上传成功!");

                        } else {
                            setResultToToast("动作上传失败, error: " + error.getDescription() + " retrying...");
                        }
                    }
                });
            }

        }
    }

    @Override
    public void configWayPointMission() {
        waypointMissionBuilder = new WaypointV2Mission.Builder();
        waypointMissionBuilder.setFinishedAction(mFinishedAction)
                .setMissionID(new Random().nextInt(65535))
                .setGotoFirstWaypointMode(firstMode)
                .setAutoFlightSpeed(zdSpeed)
                .setMaxFlightSpeed(maxSpeed);
    }

    @Override
    public DJILatLng getFirstPoint() {
        DJILatLng djiLatLng = null;
        if (waypointList.size() > 0) {
            djiLatLng = new DJILatLng(waypointList.get(0).getCoordinate().getLatitude(), waypointList.get(0).getCoordinate().getLongitude());
        }
        return djiLatLng;

    }


    @Override
    public boolean isWaypointModel() {
        boolean isWayPoint = false;
        if (isWaypointAble()) {
            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.EXECUTING
                    || waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.INTERRUPTED) {
                isWayPoint = true;
            }
        }
        return isWayPoint;
    }

    @Override
    public void startWaypointMission() {
        if (waypointV2MissionOperator != null) {
            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.READY_TO_EXECUTE) {
                waypointV2MissionOperator.startMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        setResultToToast("开始航线飞行: " + (error == null ? "成功" : WayPointV2Error.getError(error.getErrorCode())));
                        if (error == null) {
                            //addListener();
                            statrTime = CustomTimeUtils.getNowTime(time_type);
                            listener.onExecutionStart();
                            listener.setStartOrStop(true);
                        }

                    }
                });
            }
        }
    }

    @Override
    public void stopWaypointMission() {
        if (waypointV2MissionOperator != null) {
            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.EXECUTING
                    || waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.INTERRUPTED) {
                waypointV2MissionOperator.stopMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        if (error == null) {
                            removeListener();
                            listener.setStartOrStop(false);
                        }
                        setResultToToast("航线飞行停止: " + (error == null ? "成功" : WayPointV2Error.getError(error.getErrorCode())));
                    }
                });
            }
        }

    }

    @Override
    public void resumeMission() {
        if (waypointV2MissionOperator != null) {
            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.INTERRUPTED) {
                waypointV2MissionOperator.recoverMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        if (error == null) {
                            listener.setStartOrStop(false);
                        }
                        setResultToToast("航线重新飞行开始: " + (error == null ? "成功" : WayPointV2Error.getError(error.getErrorCode())));
                    }
                });
            }
        }

    }

    @Override
    public void pauseMission() {
        if (waypointV2MissionOperator != null) {
            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.EXECUTING) {
                waypointV2MissionOperator.interruptMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        if (error == null) {
                            listener.setStartOrStop(false);
                        }
                        setResultToToast("航线飞行暂停: " + (error == null ? "成功" : WayPointV2Error.getError(error.getErrorCode())));
                    }
                });
            }
        }
    }


    @Override
    public void addListener() {
        if (waypointV2MissionOperator != null) {
            waypointV2MissionOperator.addWaypointEventListener(this);
            waypointV2MissionOperator.addActionListener(this);

        }

        setContorListener();
    }

    @Override
    public void removeListener() {
        if (waypointV2MissionOperator != null) {
            waypointV2MissionOperator.removeWaypointListener(this);
            waypointV2MissionOperator.removeActionListener(this);

        }
        removeContorListener();
    }


    @Override
    public float getAllLength() {
        float length = 0.0f;
        if (waypointList.size() > 0) {
            DPoint last = new DPoint(waypointList.get(0).getCoordinate().getLatitude(), waypointList.get(0).getCoordinate().getLongitude());
            double heightLast = waypointList.get(0).getAltitude();
            for (int i = 0; i < waypointList.size(); i++) {
                if (i != 0) {
                    DPoint nowpoint = new DPoint(waypointList.get(i).getCoordinate().getLatitude(), waypointList.get(i).getCoordinate().getLongitude());
                    waypointList.get(0).getAltitude();
                    double heightNow = waypointList.get(i).getAltitude();
                    length = length + CoordinateConverter.calculateLineDistance(last, nowpoint) + (float) getAltitudeDifference(heightNow, heightLast);
                    last = nowpoint;
                    heightLast = heightNow;
                }
            }

            float lengthTo = 0.0f;
            float lengthBack = 0.0f;

            if (homeLocation != null) {
                DPoint first = new DPoint(waypointList.get(0).getCoordinate().getLatitude(), waypointList.get(0).getCoordinate().getLongitude());
                DPoint homew = new DPoint(homeLocation.getLatitude(), homeLocation.getLongitude());
                lengthTo = CoordinateConverter.calculateLineDistance(homew, first) + (float) waypointList.get(0).getAltitude();

                DPoint end = new DPoint(waypointList.get(waypointList.size() - 1).getCoordinate().getLatitude(), waypointList.get(waypointList.size() - 1).getCoordinate().getLongitude());
                lengthBack = CoordinateConverter.calculateLineDistance(homew, end) + (float) waypointList.get(waypointList.size() - 1).getAltitude();
            }

            length = length + lengthTo + lengthBack;
            all_wapoints = length;
        }
        return all_wapoints;
    }

    @Override
    public void loadWaypoints(float rtkAltitude) {

        if (waypointMissionBuilder == null) {
            ToastUtils.setResultToToastUtil("未检测到航线配置，请先配置航线");
            return;
        }

        this.rtkAltitude = rtkAltitude;

        super.setMaxSpeed(15);
        super.setZdSpeed(3);
        super.setHdSpeed(3);

        for (int i = 0; i < towerList.size(); i++) {
            if (towerList.get(i).isIschecked()) {
                PointDao pointDao = new PointDao(getActivity());
                List<DBHoverPoint> hlist = pointDao.selePiontByPid(towerList.get(i).getTid());
                for (int j = 0; j < hlist.size(); j++) {
                    DBHoverPoint hoverPoint = hlist.get(j);
                    if (hoverPoint.getHoverLocation() != null) {
                        String[] locations = hoverPoint.getHoverLocation().split(" ");
                        Float height = Float.valueOf(locations[2]);

                        float mheight = height - rtkAltitude;

                        setHomeHeight((int)mheight);
                        setQfPointHeight((int)mheight);

                        listener.setStartFlightHeight();
                        return;
                    }
                }
            }
        }

    }

    @Override
    public void downloadWayPointMission() {
        if (waypointV2MissionOperator != null) {
            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.EXECUTING ||
                    waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.INTERRUPTED) {
                waypointV2MissionOperator.downloadMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            setResultToToast("线路文件下载成功!");
                            if (waypointV2MissionOperator.getLoadedMission().getWaypointList().size() != 0) {
                                waypointList = waypointV2MissionOperator.getLoadedMission().getWaypointList();
                            }
                            listener.getDownLoadDataSuccess();
                        } else {
                            listener.getDownLoadDataFail(djiError);
                        }
                    }
                });
            }
        }

    }

    @Override
    public void setQfPointHeight(int height) {
        qfHeight = height;
    }

    boolean isOk = false;

    @Override
    public void loadData() {
        if (waypointV2MissionOperator != null) {
            //ToastUtils.setResultToToast("loadmymission状态："+waypointV2MissionOperator.getCurrentState());
            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.READY_TO_EXECUTE ||
                    waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.READY_TO_UPLOAD) {

                DialogUtils.showWaitingDialog(getActivity(),"正在检索航点..");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isOk = changeTowerToWayPoint(rtkAltitude);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isOk){
                                    listener.setMayMarker();
                                }
                                DialogUtils.dismissWaitingDialog();
                                configWayPointMission();
                                //  LogUtil.d("sudugaodu",homeHeight+"/"+maxSpeed+"/"+mSpeed);
                                waypointV2MissionOperator.loadMission(waypointMissionBuilder.addwaypoints(waypointList).build(), new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                                    @Override
                                    public void onResult(DJIWaypointV2Error error) {
                                        if (error == null) {
                                            LogUtil.d("V2任务测试：loadData");
                                            uploadPoints();
                                        } else {
                                            setResultToToast("配置线路失败：" + WayPointV2Error.getError(error.getErrorCode()));
                                            DialogUtils.dismissWaitingDialog();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        }
    }

    @Override
    public int getWayPointSize() {
        int size = 0;
        if (waypointV2MissionOperator != null) {
            if (waypointV2MissionOperator.getLoadedMission() != null) {
                size = waypointV2MissionOperator.getLoadedMission().getWaypointList().size();
            }
        }
        return size;
    }

    @Override
    public float getWayPointTotalTime() {
        //ToDo  时间不对

        return all_wapoints / zdSpeed;
    }

    @Override
    public void onDownloadUpdate(WaypointV2MissionDownloadEvent waypointV2MissionDownloadEvent) {
        listener.onDownloadUpdate();
    }


    @Override
    public void onUploadUpdate(WaypointV2MissionUploadEvent waypointV2MissionUploadEvent) {
        if (waypointV2MissionUploadEvent.getProgress() != null) {
            int index = waypointV2MissionUploadEvent.getProgress().getLastUploadedWaypointIndex();
            if (waypointV2MissionUploadEvent.getError() != null) {
                ToastUtils.setResultToToast("上传任务进度：" + index + "     发生的错误：" + waypointV2MissionUploadEvent.getError());
            }
        }

        WaypointV2MissionState waypointMissionState = waypointV2MissionUploadEvent.getCurrentState();
        if (waypointMissionState != null) {
            LogUtil.d("waypoint onUploadUpdate 当前上传状态：" + waypointMissionState);
            if (waypointMissionState == WaypointV2MissionState.READY_TO_EXECUTE) {
                customWayPointStatus = CustomWayPointStatus.READY_TO_EXECUTE;
            } else if (waypointMissionState == WaypointV2MissionState.DISCONNECTED) {
                customWayPointStatus = CustomWayPointStatus.DISCONNECTED;
            } else if (waypointMissionState == WaypointV2MissionState.EXECUTING) {
                customWayPointStatus = CustomWayPointStatus.EXECUTING;
            } else if (waypointMissionState == WaypointV2MissionState.NOT_SUPPORTED) {
                customWayPointStatus = CustomWayPointStatus.NOT_SUPPORTED;
            } else if (waypointMissionState == WaypointV2MissionState.INTERRUPTED) {
                customWayPointStatus = CustomWayPointStatus.EXECUTION_PAUSED;
            } else if (waypointMissionState == WaypointV2MissionState.UPLOADING) {
                customWayPointStatus = CustomWayPointStatus.UPLOADING;
            } else if (waypointMissionState == WaypointV2MissionState.READY_TO_UPLOAD) {
                customWayPointStatus = CustomWayPointStatus.READY_TO_EXECUTE;
            } else if (waypointMissionState == WaypointV2MissionState.RECOVERING) {
                customWayPointStatus = CustomWayPointStatus.RECOVERING;
            }
        }

        if (canUploadAction && waypointV2MissionUploadEvent.getCurrentState() == WaypointV2MissionState.READY_TO_EXECUTE) {
            canUploadAction = false;
            listener.upLoadFinish();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    DialogManager.showSingleButton(activity, R.mipmap.ic_launcher,"提示","请点击确定加载动作","确定",false, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (waypointV2MissionOperator.getCurrentState() == WaypointV2MissionState.READY_TO_EXECUTE){
                                LogUtil.d("V2任务测试：任务状态 READY_TO_EXECUTE");
                                uploadActions();
                                dialog.dismiss();
                            }else{
                                ToastUtils.setResultToToast("请稍后再试！！！！");
                            }
                        }
                    }).show();
                }
            });


        }
        listener.onUploadUpdate();
    }

    @Override
    public void onDownloadUpdate(ActionDownloadEvent actionDownloadEvent) {

    }

    @Override
    public void onUploadUpdate(ActionUploadEvent actionUploadEvent) {
        if (actionUploadEvent.getError() != null) {
            ToastUtils.setResultToToast("动作上传错误：" + WayPointV2Error.getError(actionUploadEvent.getError().getErrorCode()));
        }


    }

    @Override
    public void onExecutionUpdate(ActionExecutionEvent actionExecutionEvent) {

    }

    @Override
    public void onExecutionStart(int i) {

    }

    @Override
    public void onExecutionFinish(int i, @Nullable DJIWaypointV2Error djiWaypointV2Error) {

    }

    @Override
    public void onExecutionUpdate(WaypointV2MissionExecutionEvent waypointV2MissionExecutionEvent) {
        if (waypointV2MissionExecutionEvent.getProgress() != null) {
            int index = waypointV2MissionExecutionEvent.getProgress().getTargetWaypointIndex();

            LogUtil.d("waypoint2 onExecutionUpdate:"+index);
            LogUtil.d("waypoint2 onExecutionUpdate 是否到达:"+waypointV2MissionExecutionEvent.getProgress().isWaypointReached());
            if (index >= 0) {
                listener.onExecutionUpdate(index + 1);
            }
        }
    }

    @Override
    public void onExecutionStart() {

    }


    @Override
    public void onExecutionFinish(DJIWaypointV2Error djiWaypointV2Error) {
        String error = "";
        if (djiWaypointV2Error != null) {

            error = WayPointV2Error.getError(djiWaypointV2Error.getErrorCode());
        } else {
            endTime = CustomTimeUtils.getNowTime(time_type);
            saveTask();
        }
        listener.onExecutionFinish(error);
    }

    @Override
    public void onExecutionStopped() {

        listener.onExecutionStopped();
    }

    @Override
    public void cleanData() {
        photolist.clear();
        waypointList.clear();
        v2Actions.clear();
    }

    private boolean isWaypointAble() {
        boolean isable = false;
        if (waypointV2MissionOperator == null) {
            ToastUtils.setResultToToast("请先连上飞机！");
        } else {
            isable = true;
        }
        return isable;
    }
}