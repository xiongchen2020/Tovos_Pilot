package com.tovos.uav.sample.route.waypoint;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.dji.mapkit.core.models.DJILatLng;
import com.example.commonlib.utils.CustomTimeUtils;
import com.example.commonlib.utils.DialogUtils;
import com.example.commonlib.utils.LogUtil;
import com.example.djilib.dji.djierror.WayPointError;

import com.example.commonlib.utils.ToastUtils;
import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.bean.DbMediaPoint;
import com.tovos.uav.sample.databean.sql.dao.MedailDao;
import com.tovos.uav.sample.databean.sql.dao.PointDao;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointDownloadProgress;
import dji.common.mission.waypoint.WaypointExecutionProgress;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionInterruption;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.mission.waypoint.WaypointUploadProgress;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.sdkmanager.DJISDKManager;

import static com.example.commonlib.utils.ToastUtils.setResultToToast;

public class CustomWayPoint1 extends CustomWayPoint implements WaypointMissionOperatorListener{


    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
    public static WaypointMission.Builder waypointMissionBuilder;
    private List<Waypoint> waypointList = new ArrayList<>();
    private WaypointMissionOperator instance;
    private WapointListener listener;
    private Context context;

    public CustomWayPoint1(Context context, WapointListener listener){
        this.context = context;
        activity=(Activity)context;
        this.listener = listener;
        configWayPointMission();
    }

    @Override
    public void initWaypointMissionOperator() {
        getWaypointMissionOperator();
        addListener();
    }

    @Override
    public String  getCustomWayPointVersion(){
        return "1";
    }
    public WaypointMissionOperator getWaypointMissionOperator() {
        if (instance == null) {
            if (DJISDKManager.getInstance()!=null){
                if (DJISDKManager.getInstance().getMissionControl()!=null){
                    instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
                }
            }
        }
        return instance;
    }


    private Waypoint file2Waypoint(DBHoverPoint hoverPoint,int position){
        Waypoint waypoint = null;
        String location = hoverPoint.getHoverLocation();

        Log.e("location","location:"+location);
        if (location != null){
            String[] locations = location.split(" ");
            double lat = Double.valueOf(locations[0]);
            double lng = Double.valueOf(locations[1]);
            Float height = Float.valueOf(locations[2]);
            waypoint = new Waypoint(lng,lat,height);

            TestUtilsForWaypoint.normalModel(context,hoverPoint,waypoint);
        }

        return waypoint;
    }

    @Override
    public void configWayPointMission() {
       // if (waypointMissionBuilder == null){
        waypointMissionBuilder = new WaypointMission.Builder();
        //}
        waypointMissionBuilder.finishedAction(mFinishedAction)
                .headingMode(mHeadingMode)
                .autoFlightSpeed(zdSpeed)
                .maxFlightSpeed(maxSpeed)
                .setGimbalPitchRotationEnabled(true)
                .flightPathMode(WaypointMissionFlightPathMode.NORMAL);
    }

    @Override
    public DJILatLng getFirstPoint() {
        DJILatLng djiLatLng = null;
        if (waypointList.size()>0){
            djiLatLng = new DJILatLng(waypointList.get(0).coordinate.getLatitude(),waypointList.get(0).coordinate.getLongitude());
        }
        return djiLatLng;
    }

    private boolean changeTowerToWayPoint(){
        photolist.clear();
        boolean issuccess = false;
        //List<Waypoint> list = new ArrayList<>();
        int position = 0;
        for (int i = 0;i<towerList.size();i++){
            if (towerList.get(i).isIschecked()){
                PointDao pointDao = new PointDao(getActivity());
                List<DBHoverPoint> hilst = new ArrayList<>();
                hilst.addAll(pointDao.selePiontByPid(towerList.get(i).getTid()));
                if (towerIndex == i){
                    hilst = hilst.subList(pointIndex,hilst.size());
                }
                for (int j=0;j<hilst.size();j++) {

                    DBHoverPoint hoverPoint = hilst.get(j);
                    waypointList.add(file2Waypoint(hoverPoint,position));
                    MedailDao medailDao = new MedailDao(getActivity());
                    List<DbMediaPoint> mediaPoints = medailDao.seleMedialByPid(hoverPoint.getHid());
                    if (mediaPoints!=null){
                        if (mediaPoints.size() > 0) {
                            for (int k = 0; k < mediaPoints.size(); k++) {
                                photolist.add(mediaPoints.get(k).getMediaName()+".jpg");
                            }
                        }
                    }
                    position = position+1;
                }
            }
        }
        if (waypointList.size() == 0){
            setResultToToast("航线点不能为空，请选择航线点！");
            issuccess = false;
        }else if (waypointList.size() < 4){
            setResultToToast("航线点过少，请配置有效航线！");
            issuccess = false;
        }else if(waypointList.size()>199){
            setResultToToast("航线点超过最大限制");
            issuccess = false;
        }
        else {
            issuccess = true;
        }

        return issuccess;
    }

    private void getRealityHeights(float altitude){
        for (int i = 0;i<waypointList.size();i++){
            waypointList.get(i).altitude = waypointList.get(i).altitude - altitude;
        }
        super.setHomeHeight((int) waypointList.get(0).altitude);
        super.setMaxSpeed(15);
        super.setZdSpeed(3);
        super.setHdSpeed(3);
    }

    @Override
    public void loadData(){

        if (isWaypointAble()){
            if (instance.getCurrentState() == WaypointMissionState.READY_TO_UPLOAD
                    || instance.getCurrentState() == WaypointMissionState.READY_TO_EXECUTE){
                configWayPointMission();

                for (int i=0;i<waypointList.size();i++){
                    Waypoint waypoint = waypointList.get(i);
                    if (waypoint.speed == 0f){
                        waypoint.speed = getHdSpeed();
                    }
                 
                }


                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
                DJIError error = instance.loadMission(waypointMissionBuilder.build());
                if (error == null) {
                    uploadPoints();

                } else {
                    DialogUtils.dismissWaitingDialog();
                    setResultToToast("配置线路失败：" + WayPointError.getError(error.getErrorCode()));
                }
            }
        }
    }


    private void uploadPoints(){

        if (isWaypointAble()){
            if (instance.getCurrentState() == WaypointMissionState.READY_TO_UPLOAD  ||instance.getCurrentState() == WaypointMissionState.READY_TO_EXECUTE){
                DialogUtils.showWaitingDialog(getActivity(),"正在上传航点....");
                instance.uploadMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        DialogUtils.dismissWaitingDialog();
                        if (error == null) {
                            setResultToToast("线路文件上传成功!");
                        } else {
                            setResultToToast("线路文件上传失败: " + WayPointError.getError(error.getErrorCode()));
                        }
                    }
                });
            }else {
                DialogUtils.dismissWaitingDialog();
            }
        }
    }

    @Override
    public void downloadWayPointMission(){
        if(isWaypointAble()){
            if (instance.getCurrentState() == WaypointMissionState.EXECUTING ||
            instance.getCurrentState() == WaypointMissionState.EXECUTION_PAUSED){
                instance.downloadMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            if (instance.getLoadedMission() != null){
                                if (instance.getLoadedMission().getWaypointList()!=null){
                                    if (instance.getLoadedMission().getWaypointList().size()!=0){
                                        waypointList = instance.getLoadedMission().getWaypointList();
                                    }
                                }
                            }
                            listener.getDownLoadDataSuccess();
                        }else{
                            listener.getDownLoadDataFail(djiError);
                        }
                    }
                });
            }
        }

    }

    @Override
    public void setQfPointHeight(int height) {
        waypointList.get(0).altitude = height;

    }

    @Override
    public boolean isWaypointModel() {
        boolean isWayPoint = false;
        if (isWaypointAble()){
            if (instance.getCurrentState() == WaypointMissionState.EXECUTING
                    || instance.getCurrentState() == WaypointMissionState.EXECUTION_PAUSED){
                isWayPoint = true;
            }
        }
        return isWayPoint;
    }

    boolean isOk = false;

    @Override
    public void loadWaypoints(float rtkAltitude) {
        if(instance != null) {
            if (instance.getCurrentState() == WaypointMissionState.EXECUTING
                    ||instance.getCurrentState() == WaypointMissionState.EXECUTION_PAUSED) {
                ToastUtils.setResultToToast("航线任务正在执行，请稍后再试！");
                return;
            }
            DialogUtils.showWaitingDialog(getActivity(),"正在检索航点..");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    isOk = changeTowerToWayPoint();
                    if (isOk){
                        getRealityHeights(rtkAltitude);

                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.dismissWaitingDialog();
                            if (isOk){
                                listener.setMayMarker();
                                listener.setStartFlightHeight();
                            }
                        }
                    });
                }
            }).start();


        }
    }


    @Override
    public void startWaypointMission() {
        if (isWaypointAble()){
            if (instance.getCurrentState() == WaypointMissionState.READY_TO_EXECUTE){
                instance.startMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        DialogUtils.dismissWaitingDialog();

                        if (error == null){
                            statrTime = CustomTimeUtils.getNowTime(time_type);
                            listener.onExecutionStart();
                            LogUtil.d("waypoint onExecutionStart 开始执行任务");
                        }else {
                            setResultToToast("开始飞行失败:"+WayPointError.getError(error.getErrorCode()));
                        }
                    }
                });
            }
        }
    }

    private boolean isStartOrStop(){
        boolean isStart = false;
        if (instance.getCurrentState() == WaypointMissionState.EXECUTING){
            isStart = true;
        }
        return isStart;
    }

    public void getPreviousInterruption(){
        if (isWaypointAble()){
            instance.getPreviousInterruption(new CommonCallbacks.CompletionCallbackWith<WaypointMissionInterruption>() {
                @Override
                public void onSuccess(WaypointMissionInterruption waypointMissionInterruption) {
                    //waypointMissionInterruption
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }
    }

    /**
     *重启航线飞行
     */
    @Override
    public void resumeMission(){
        if (isWaypointAble()){
            if (instance.getCurrentState() == WaypointMissionState.EXECUTION_PAUSED ){
                instance.resumeMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        if (error == null){
                            listener.setPauseOrResume(true);
                        }else {
                            setResultToToast("重新飞行失败:"+WayPointError.getError(error.getErrorCode()));
                        }
                    }
                });
            }
        }
    }

    /**
     * 暂停飞行
     */
    @Override
    public void pauseMission(){
        if (isWaypointAble()){
            if (instance.getCurrentState() == WaypointMissionState.EXECUTING){
                instance.pauseMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {

                        if (error == null){
                            listener.setPauseOrResume(false);
                        }else {
                            setResultToToast("暂停失败:"+WayPointError.getError(error.getErrorCode()));
                        }
                    }
                });
            }
        }
    }



    @Override
    public void stopWaypointMission() {

        if (isWaypointAble()){
            if (instance.getCurrentState()==WaypointMissionState.EXECUTING || instance.getCurrentState() == WaypointMissionState.EXECUTION_PAUSED){
                instance.stopMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                        if (error == null){
                        }else {
                            setResultToToast("飞行停止失败:"+WayPointError.getError(error.getErrorCode()));
                        }
                    }
                });
            }
        }
    }

    @Override
    public void addListener() {
        if (isWaypointAble()){
            instance.addListener(this);
        }
        setContorListener();
        LogUtil.d("waypoint 添加航线任务监听");
    }

    @Override
    public void removeListener() {
        if (isWaypointAble()) {
            instance.removeListener(this);

        }
        removeContorListener();
        LogUtil.d("waypoint 移除航线任务监听");
    }


    @Override
    public void onDownloadUpdate(WaypointMissionDownloadEvent waypointMissionDownloadEvent) {
        WaypointDownloadProgress waypointDownloadProgress = waypointMissionDownloadEvent.getProgress();
        if (waypointDownloadProgress!=null){
            LogUtil.d("waypoint onDownloadUpdate 当前下载点："+waypointDownloadProgress.downloadedWaypointIndex+"/"+waypointDownloadProgress.totalWaypointCount);
            LogUtil.d("waypoint onDownloadUpdate isSummaryDownloaded："+waypointDownloadProgress.isSummaryDownloaded);
        }

        listener.onDownloadUpdate();
    }

    @Override
    public void onUploadUpdate(WaypointMissionUploadEvent waypointMissionUploadEvent) {

        WaypointMissionState waypointMissionState1 = waypointMissionUploadEvent.getPreviousState();
        if (waypointMissionState1!=null){
            LogUtil.d("waypoint onUploadUpdate 上一个点上传状态："+waypointMissionState1);
        }

        WaypointMissionState waypointMissionState = waypointMissionUploadEvent.getCurrentState();
        if (waypointMissionState!=null){
            LogUtil.d("waypoint onUploadUpdate 当前上传状态："+waypointMissionState);
            if ( waypointMissionState == WaypointMissionState.READY_TO_EXECUTE) {
                listener.upLoadFinish();
                customWayPointStatus = CustomWayPointStatus.READY_TO_EXECUTE;
            }else if ( waypointMissionState == WaypointMissionState.DISCONNECTED) {
                customWayPointStatus = CustomWayPointStatus.DISCONNECTED;
            }else if ( waypointMissionState == WaypointMissionState.EXECUTING) {
                customWayPointStatus = CustomWayPointStatus.EXECUTING;
            }else if ( waypointMissionState == WaypointMissionState.NOT_SUPPORTED) {
                customWayPointStatus = CustomWayPointStatus.NOT_SUPPORTED;
            }else if ( waypointMissionState == WaypointMissionState.EXECUTION_PAUSED) {
                customWayPointStatus = CustomWayPointStatus.EXECUTION_PAUSED;
            }else if ( waypointMissionState == WaypointMissionState.UPLOADING) {
                customWayPointStatus = CustomWayPointStatus.UPLOADING;
            }else if ( waypointMissionState == WaypointMissionState.READY_TO_UPLOAD) {
                customWayPointStatus = CustomWayPointStatus.READY_TO_EXECUTE;
            }else if ( waypointMissionState == WaypointMissionState.RECOVERING) {
                customWayPointStatus = CustomWayPointStatus.RECOVERING;
            }
        }

        WaypointUploadProgress waypointUploadProgress = waypointMissionUploadEvent.getProgress();
        if (waypointUploadProgress != null){
            LogUtil.d("waypoint onUploadUpdate 当前上传进度："+waypointUploadProgress.uploadedWaypointIndex+"/"+waypointUploadProgress.totalWaypointCount);
            LogUtil.d("waypoint onUploadUpdate isSummaryDownloaded："+waypointUploadProgress.isSummaryUploaded);
        }
        listener.onUploadUpdate();
    }

    @Override
    public void onExecutionUpdate(WaypointMissionExecutionEvent waypointMissionExecutionEvent) {

        if (waypointMissionExecutionEvent!=null){

            WaypointMissionState waypointMissionState1 = waypointMissionExecutionEvent.getPreviousState();
            if (waypointMissionState1!=null){
                LogUtil.d("waypoint onExecutionUpdate 上一个执行点："+waypointMissionState1);
            }

            WaypointMissionState waypointMissionState = waypointMissionExecutionEvent.getCurrentState();
            if (waypointMissionState!=null){
                LogUtil.d("waypoint onExecutionUpdate 当前执行点："+waypointMissionState);
            }

            WaypointExecutionProgress waypointUploadProgress = waypointMissionExecutionEvent.getProgress();
            if (waypointUploadProgress != null){
                LogUtil.d("waypoint onUploadUpdate 当前任务执行进度："+waypointUploadProgress.targetWaypointIndex+"/"+waypointUploadProgress.totalWaypointCount);
                LogUtil.d("waypoint onUploadUpdate 是否到达执行点："+waypointUploadProgress.isWaypointReached);
                LogUtil.d("waypoint onUploadUpdate 执行状态："+waypointUploadProgress.executeState);
                if (waypointMissionExecutionEvent.getProgress().targetWaypointIndex>=0){

                    listener.onExecutionUpdate(waypointMissionExecutionEvent.getProgress().targetWaypointIndex+1);
                }
            }
        }

    }

    @Override
    public void onExecutionStart() {

    }

    @Override
    public void onExecutionFinish(DJIError djiError) {
        String log = "";
        if (djiError != null){
            log = WayPointError.getError(djiError.getErrorCode());
        }
        listener.onExecutionFinish(log);
        endTime = CustomTimeUtils.getNowTime(time_type);
        LogUtil.d("waypoint onExecutionStart 结束执行任务");
        saveTask();
        photolist.clear();
    }


    @Override
    public float getAllLength(){
        all_wapoints = waypointMissionBuilder.calculateTotalDistance();

        float lengthTo = 0.0f;
        float lengthBack = 0.0f;
        double heightLast = waypointList.get(0).altitude;
        for (int i=0;i<waypointList.size();i++){
            if (i!=0){
                double heightNow = waypointList.get(i).altitude;
                all_wapoints = all_wapoints +(float) getAltitudeDifference(heightLast,heightNow);
                heightLast = heightNow;
            }
        }

        if (homeLocation!=null){
            DPoint first = new DPoint(waypointList.get(0).coordinate.getLatitude(), waypointList.get(0).coordinate.getLongitude());
            DPoint homew = new DPoint(homeLocation.getLatitude(),homeLocation.getLongitude());
            lengthTo = CoordinateConverter.calculateLineDistance(homew, first) + (float) waypointList.get(0).altitude;

            DPoint end = new DPoint(waypointList.get(waypointList.size()-1).coordinate.getLatitude(),
                    waypointList.get(waypointList.size()-1).coordinate.getLongitude());
            lengthBack = CoordinateConverter.calculateLineDistance(homew, end)+(float)waypointList.get(waypointList.size()-1).altitude;
        }

        all_wapoints = all_wapoints+lengthTo+lengthBack;

        return waypointMissionBuilder.calculateTotalDistance();
    }

    @Override
    public int getWayPointSize() {
        return waypointList.size();
    }

    @Override
    public float getWayPointTotalTime() {
        return all_wapoints/zdSpeed;
    }

    @Override
    public void cleanData(){
        for (int i=0;i<waypointMissionBuilder.getWaypointCount();i++){
            waypointMissionBuilder.removeWaypoint(i);
        }
      //  towerList.clear();
        waypointList.clear();
        photolist.clear();
    }

    private boolean isWaypointAble(){
        boolean isable = false;
        if (instance == null){
            ToastUtils.setResultToToast("请先连上飞机！");
        }else {
            isable = true;
        }
        return isable;
    }
}
