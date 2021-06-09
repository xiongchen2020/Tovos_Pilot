package com.tovos.uav.sample.route.view.djiview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dji.mapkit.core.maps.DJIMap;
import com.dji.mapkit.core.models.DJILatLng;
import com.dji.mapkit.core.models.annotations.DJIMarker;
import com.example.commonlib.utils.DialogManager;
import com.example.commonlib.utils.LogUtil;
import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.component.aircraft.MyAircraft;
import com.example.djilib.dji.component.aircraft.MyAircraftInterface;
import com.example.djilib.dji.component.airlink.CustomAirLink;
import com.example.djilib.dji.component.airlink.CustomAirLinkListener;
import com.example.djilib.dji.component.camera.CustomCamera;
import com.example.djilib.dji.component.camera.CustomCameraManager;
import com.example.djilib.dji.healthinfomation.CustomHealthInfomation;
import com.example.djilib.dji.healthinfomation.CustomHealthInfomationListener;
import com.example.djilib.dji.healthinfomation.HealthInfo;
import com.example.djilib.dji.manager.CustomFlyZoneManger;
import com.example.djilib.dji.map.MyMapListener;
import com.example.djilib.dji.map.MyMapManager;
import com.example.filgthhublibrary.util.ToastUtils;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.dao.PointDao;
import com.tovos.uav.sample.route.view.adapter.HealthInfoMationAdapter;
import com.tovos.uav.sample.route.waypoint.CustomWayPoint;
import com.example.commonlib.utils.DensityUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dji.common.airlink.PhysicalSource;
import dji.common.camera.CameraVideoStreamSource;
import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.model.LocationCoordinate2D;
import dji.common.product.Model;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.ux.beta.cameracore.widget.autoexposurelock.AutoExposureLockWidget;
import dji.ux.beta.cameracore.widget.cameracontrols.CameraControlsWidget;
import dji.ux.beta.cameracore.widget.focusexposureswitch.FocusExposureSwitchWidget;
import dji.ux.beta.cameracore.widget.focusmode.FocusModeWidget;
import dji.ux.beta.cameracore.widget.fpvinteraction.FPVInteractionWidget;
import dji.ux.beta.core.ui.GridLineView;
import dji.ux.beta.core.util.SettingDefinitions;
import dji.ux.beta.core.widget.fpv.FPVWidget;
import dji.ux.beta.visualcamera.widget.cameraconfig.aperture.CameraConfigApertureWidget;
import dji.ux.beta.visualcamera.widget.cameraconfig.ev.CameraConfigEVWidget;
import dji.ux.beta.visualcamera.widget.cameraconfig.iso.CameraConfigISOAndEIWidget;
import dji.ux.beta.visualcamera.widget.cameraconfig.shutter.CameraConfigShutterWidget;
import dji.ux.beta.visualcamera.widget.cameraconfig.ssd.CameraConfigSSDWidget;
import dji.ux.beta.visualcamera.widget.cameraconfig.storage.CameraConfigStorageWidget;
import dji.ux.beta.visualcamera.widget.cameraconfig.wb.CameraConfigWBWidget;
import dji.ux.panel.CameraSettingAdvancedPanel;
import dji.ux.panel.CameraSettingExposurePanel;
import dji.ux.panel.PreFlightCheckListPanel;
import dji.ux.widget.GPSSignalWidget;
import dji.ux.widget.MapWidget;
import dji.ux.widget.RadarWidget;

public class CompleteWidgetView extends BaseView implements MyMapListener, MyAircraftInterface, CustomHealthInfomationListener, CustomAirLinkListener {

    public MapWidget mapWidget;
    public ViewGroup parentView;
    public FPVWidget fpvWidget;
    public FPVInteractionWidget fpvInteractionWidget;
    public RadarWidget radarWidget;

    private Activity activity;
    private GPSSignalWidget gpsSignalWidget;
    private FPVWidget secondaryFPVWidget;
    private PreFlightCheckListPanel preFlightCheckListPanel;
    public MyMapManager myMapManager;
    private FrameLayout secondaryVideoView;
    private RelativeLayout fpvView, mapView;
    private ImageView change;
    private CustomHealthInfomation customHealthInfomation;
    private boolean isMapMini = true;
    private int height;
    private int width;
    private int margin;
    private int deviceWidth;
    private int deviceHeight;
    private RecyclerView listView;
    private HealthInfoMationAdapter healthInfoMationAdapter;
    private List<HealthInfo> list = new ArrayList<>();
    private CustomCamera customCamera;
    private CameraView cameraView;
    private MyAircraft myAircraft;

    private CustomCameraManager customCameraManager;
    private CameraControlsWidget cameraControlsWidget;
    private CameraConfigISOAndEIWidget cameraConfigISOAndEIWidget;
    private CameraConfigShutterWidget  cameraConfigShutterWidget;
    private CameraConfigApertureWidget cameraConfigApertureWidget;
    private CameraConfigEVWidget cameraConfigEVWidget;
    private CameraConfigWBWidget cameraConfigWBWidget;
    private CameraConfigStorageWidget cameraConfigStorageWidget;
    private CameraConfigSSDWidget cameraConfigSSDWidget;
    private AutoExposureLockWidget autoExposureLockWidget;
    private FocusModeWidget focusModeWidget;
    private FocusExposureSwitchWidget focusExposureSwitchWidget;
    private CameraSettingExposurePanel cameraSettingExposurePanel;
    private CameraSettingAdvancedPanel cameraSettingAdvancedPanel;

    private ImageView jg_img;
    private TextView jg_tw;
    private LinearLayout jg_ll;
    private Context context;

    private ArrayList<String> vide = new ArrayList<>();
    final String[] videoName = new String[]{
            PhysicalSource.LEFT_CAM.name(),
            PhysicalSource.RIGHT_CAM.name(),
            PhysicalSource.TOP_CAM.name(),
            PhysicalSource.FPV_CAM.name()
    };

    public void setActivity(Activity activity) {
        this.activity = activity;
        cameraView.setActivity(activity);
    }
    public MyAircraft getMyAircraft(){
        if (DJIContext.getAircraftInstance()!=null){
            if (DJIContext.getAircraftInstance()!=null){
                if (myAircraft == null){
                    myAircraft = new MyAircraft(this);
                }
                return myAircraft;
            }
        }

        return null;
    }
    public CompleteWidgetView(Context context) {
        super(context);
        initCompleteViewS(context);
    }


    public CompleteWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCompleteViewS(context);
    }

    public CompleteWidgetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCompleteViewS(context);
    }

    public CompleteWidgetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCompleteViewS(context);
    }

    //@SuppressLint("ResourceType")
    public void initCompleteViewS(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.dji_widgets_layout, this);
        height = getResources().getDimensionPixelSize(R.dimen.small_widget_height);
        width = getResources().getDimensionPixelSize(R.dimen.small_widget_width);
        margin = DensityUtil.dip2px(getContext(), 10);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;
        fpvInteractionWidget = findViewById(R.id.widget_fpv_interaction);
        mapWidget = (MapWidget) findViewById(R.id.map_widget);
        change = (ImageView)findViewById(R.id.changevideosouce);
        radarWidget = (RadarWidget)findViewById(R.id.rardarwidget);
        gpsSignalWidget = (GPSSignalWidget) findViewById(R.id.gpsSignalWidget);
       // preFlightCheckListPanel = findViewById(R.id.pre_flight_check_list);
        parentView = findViewById(R.id.root_view);
        fpvWidget = (FPVWidget) findViewById(R.id.fpv_widget);
        // R.id.backward_distance
        fpvWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewClick(fpvWidget);
            }
        });
        secondaryVideoView = findViewById(R.id.secondary_video_view);
        secondaryFPVWidget = (FPVWidget) findViewById(R.id.secondary_fpv_widget);
        fpvView = findViewById(R.id.fpv_main_view);
        mapView = findViewById(R.id.map_view);

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClick(mapWidget);
            }
        });

        change.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("121312312","选择切换视频源!");
                showList();
            }
        });

        secondaryFPVWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapVideoSource();
            }
        });

        dissconnectFlight();
        customHealthInfomation = new CustomHealthInfomation(getContext(), this);
        listView = findViewById(R.id.error_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(linearLayoutManager);
        healthInfoMationAdapter = new HealthInfoMationAdapter(getContext(), list);
        listView.setAdapter(healthInfoMationAdapter);
        CustomFlyZoneManger customFlyZoneManger = new CustomFlyZoneManger();
        change.setVisibility(GONE);
        cameraView = findViewById(R.id.cameraview);


        secondaryFPVWidget.setCodecManagerCallback(new FPVWidget.CodecManagerCallback() {
            @Override
            public void onCodecManagerChanged(@Nullable DJICodecManager djiCodecManager) {
                if (djiCodecManager!=null){
                    djiCodecManager.setOnNoVideoDataListener(new DJICodecManager.OnNoVideoDataListener() {
                        @Override
                        public void onNoVideoData() {
                            Log.i("fpv","是否有视频数据");
                            djiCodecManager.setSurfaceToBlack();
                        }
                    });
                }
            }
        });

        fpvWidget.setCodecManagerCallback(new FPVWidget.CodecManagerCallback() {
            @Override
            public void onCodecManagerChanged(@Nullable DJICodecManager djiCodecManager) {
                 Log.i("fpv","djiCodecManager："+djiCodecManager);
                 if (djiCodecManager!=null){
                     Log.i("fpv","isDecoderOK："+djiCodecManager.isDecoderOK());
                     djiCodecManager.setOnNoVideoDataListener(new DJICodecManager.OnNoVideoDataListener() {
                         @Override
                         public void onNoVideoData() {
                             Log.i("fpv","是否有视频数据");
                             djiCodecManager.setSurfaceToGray();
                         }
                     });
                 }
            }
        });

        secondaryFPVWidget.setGridLinesEnabled(false);
        fpvWidget.setGridLinesEnabled(true);
        fpvWidget.getGridLineView().setType(GridLineView.GridLineType.PARALLEL_DIAGONAL);

        fpvWidget.setStateChangeCallback(new FPVWidget.FPVStateChangeCallback() {


            @Override
            public void onStreamSourceChange(@Nullable CameraVideoStreamSource cameraVideoStreamSource) {

            }

            @Override
            public void onCameraNameChange(@Nullable String s) {

                Log.i("fpv","相机名称变更:"+s);
            }

            @Override
            public void onCameraSideChange(@Nullable SettingDefinitions.CameraSide cameraSide) {
                Log.i("fpv","相机位置变更:"+cameraSide);
            }

            @Override
            public void onFPVSizeChange(@Nullable FPVWidget.FPVSize fpvSize) {
                Log.i("fpv","相机大小变更:"+fpvSize.toString());
            }
        });

        cameraControlsWidget = findViewById(R.id.widget_camera_controls);
        cameraConfigISOAndEIWidget = findViewById(R.id.widget_camera_config_iso_and_ei);
        cameraConfigShutterWidget = findViewById(R.id.widget_camera_config_shutter);
        cameraConfigApertureWidget = findViewById(R.id.widget_camera_config_aperture);
        cameraConfigEVWidget = findViewById(R.id.widget_camera_config_ev);
        cameraConfigWBWidget = findViewById(R.id.widget_camera_config_wb);
        cameraConfigStorageWidget = findViewById(R.id.widget_camera_config_storage);
        cameraConfigSSDWidget = findViewById(R.id.widget_camera_config_ssd);
        autoExposureLockWidget = findViewById(R.id.widget_auto_exposure_lock);
        focusModeWidget = findViewById(R.id.widget_focus_mode);
        focusExposureSwitchWidget = findViewById(R.id.widget_focus_exposure_switch);
        cameraSettingExposurePanel = findViewById(R.id.camera_setting_exposure_panel);
        cameraSettingAdvancedPanel = findViewById(R.id.camera_setting_advanced_panel);


        cameraControlsWidget.getExposureSettingsIndicatorWidget().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cameraSettingAdvancedPanel.getVisibility() == VISIBLE){
                    cameraSettingAdvancedPanel.setVisibility(GONE);
                }


                if (cameraSettingExposurePanel.getVisibility() == VISIBLE){
                    cameraSettingExposurePanel.setVisibility(GONE);
                }else {
                    cameraSettingExposurePanel.setVisibility(VISIBLE);
                }
            }
        });


        cameraControlsWidget.getCameraSettingsMenuIndicatorWidget().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cameraSettingExposurePanel.getVisibility() == VISIBLE){
                    cameraSettingExposurePanel.setVisibility(GONE);
                }
                if (cameraSettingAdvancedPanel.getVisibility() == VISIBLE){
                    cameraSettingAdvancedPanel.setVisibility(GONE);
                }else {
                    cameraSettingAdvancedPanel.setVisibility(VISIBLE);
                }
            }
        });

        jg_img = findViewById(R.id.jg_img);
        jg_tw = findViewById(R.id.jg_muns);
        jg_ll = findViewById(R.id.jg_ll);
        jg_ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.error_list_view).getVisibility() == VISIBLE){
                    findViewById(R.id.error_list_view).setVisibility(GONE);
                    isShowjg = false;
                }else {
                    findViewById(R.id.error_list_view).setVisibility(VISIBLE);
                    healthInfoMationAdapter.notifyDataSetChanged();
                    isShowjg = true;
                }
            }
        });

    }

    public void connectSuccess(){
       // myAircraft.getHomeLocation();
        myMapManager.getLocation();

        initHealthInfo();
    }
    private void onViewClick(View view) {

        Log.e(this.getClass().getSimpleName(),"view:"+view);

        if (view == fpvWidget && !isMapMini) {
            Log.e(this.getClass().getSimpleName(),"缩小地图");
            resizeFPVWidget(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 0, 0);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mapWidget, deviceWidth, deviceHeight, width, height, margin);
            mapWidget.startAnimation(mapViewAnimation);
            isMapMini = true;
        } else if (view == mapWidget && isMapMini) {
            Log.e(this.getClass().getSimpleName(),"放大地图");
            resizeFPVWidget(width, height, margin, 12);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mapWidget, width, height, deviceWidth, deviceHeight, 0);
            mapWidget.startAnimation(mapViewAnimation);
            isMapMini = false;
        }
    }


    private void resizeFPVWidget(int width, int height, int margin, int fpvInsertPosition) {
        RelativeLayout.LayoutParams fpvParams = (RelativeLayout.LayoutParams) fpvView.getLayoutParams();
        fpvParams.height = height;
        fpvParams.width = width;
        fpvParams.rightMargin = margin;
        fpvParams.bottomMargin = margin;
        if (isMapMini) {
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        } else {
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
        fpvView.setLayoutParams(fpvParams);

        parentView.removeView(fpvView);
        parentView.addView(fpvView, fpvInsertPosition);
    }


    private void swapVideoSource() {
        Log.d("TOVOS", "swapVideoSource: "+secondaryFPVWidget.getVideoSource());

        if (secondaryFPVWidget.getVideoSource() == SettingDefinitions.VideoSource.SECONDARY) {

            fpvWidget.setVideoSource(SettingDefinitions.VideoSource.SECONDARY);
            secondaryFPVWidget.setVideoSource(SettingDefinitions.VideoSource.PRIMARY);
            //fpvWidget.notifyCalculator(fpvView.rotationAngle());
        } else {
            fpvWidget.setVideoSource(SettingDefinitions.VideoSource.PRIMARY);
            secondaryFPVWidget.setVideoSource(SettingDefinitions.VideoSource.SECONDARY);
        }
        updateMainFPV();
    }

    private void updateMainFPV(){
        SettingDefinitions.CameraSide cameraSide = SettingDefinitions.CameraSide.UNKNOWN;//SettingDefinitions.CameraSide.PORT;
        DJIContext.getAircraftInstance().getGimbal();
        PhysicalSource source = null;
        if (fpvWidget.getVideoSource() == SettingDefinitions.VideoSource.PRIMARY){
            source = VideoFeeder.getInstance().getPrimaryVideoFeed().getVideoSource();
        }else {
            source = VideoFeeder.getInstance().getSecondaryVideoFeed().getVideoSource();
        }
        if (source == PhysicalSource.LEFT_CAM){
            cameraSide = SettingDefinitions.CameraSide.PORT;
        }else if (source == PhysicalSource.RIGHT_CAM){
            cameraSide = SettingDefinitions.CameraSide.STARBOARD;
        }else if (source == PhysicalSource.TOP_CAM){
            cameraSide = SettingDefinitions.CameraSide.TOP;
        }else if (source == PhysicalSource.MAIN_CAM){
            //cameraSide = SettingDefinitions.CameraSide.
        }
        fpvInteractionWidget.onCameraSideChange(cameraSide);
        fpvInteractionWidget.onCameraSideChange(cameraSide);
        SettingDefinitions.CameraIndex cameraIndex = SettingDefinitions.CameraIndex.CAMERA_INDEX_0;
        customCamera = customCameraManager.getNowCamera(fpvWidget);
        if(customCamera != null) {
            if (source != PhysicalSource.FPV_CAM) {

                switch (customCamera.getCameraindex()) {
                    case 0:
                        cameraIndex = SettingDefinitions.CameraIndex.CAMERA_INDEX_0;
                        break;
                    case 1:
                        cameraIndex = SettingDefinitions.CameraIndex.CAMERA_INDEX_1;
                        break;
                    case 2:
                        cameraIndex = SettingDefinitions.CameraIndex.CAMERA_INDEX_2;
                        break;
                    case 4:
                        cameraIndex = SettingDefinitions.CameraIndex.CAMERA_INDEX_4;
                        break;
                }
            }
        }
        Log.i(this.getClass().getSimpleName(),"CameraIndex:"+cameraIndex);
        cameraConfigISOAndEIWidget.setCameraIndex(cameraIndex);
        cameraConfigShutterWidget .setCameraIndex(cameraIndex);
        cameraConfigApertureWidget.setCameraIndex(cameraIndex);
        cameraConfigEVWidget.setCameraIndex(cameraIndex);
        cameraConfigWBWidget.setCameraIndex(cameraIndex);
        cameraConfigStorageWidget.setCameraIndex(cameraIndex);
        cameraConfigSSDWidget.setCameraIndex(cameraIndex);
        autoExposureLockWidget.setCameraIndex(cameraIndex);
        focusModeWidget.setCameraIndex(cameraIndex);
        focusExposureSwitchWidget.setCameraIndex(cameraIndex);
        cameraControlsWidget.getPhotoVideoSwitchWidget().setCameraIndex(cameraIndex);
        cameraControlsWidget.getExposureSettingsIndicatorWidget().setCameraIndex(cameraIndex);
        cameraControlsWidget.getPhotoVideoSwitchWidget().setCameraIndex(cameraIndex);
        fpvInteractionWidget.setCameraIndex(cameraIndex);
        //cameraControlsWidget.getCameraCaptureWidget().getViewByMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);

        cameraView.setCustomCamera(customCamera);

    }

    public void onCreate(Bundle savedInstanceState){

        mapWidget.initAMap(new MapWidget.OnMapReadyListener() {
            @Override
            public void onMapReady(@NonNull DJIMap map) {
                map.setOnMapClickListener(new DJIMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(DJILatLng latLng) {
                        // onViewClick(mapWidget);
                    }
                });
            }
        });
        mapWidget.onCreate(savedInstanceState);

        initMapManager();
    }

    public void onResume() {

        mapWidget.onResume();
    }

    public void onPause() {
        mapWidget.onPause();
    }

    public void onDestroy() {
        mapWidget.onDestroy();
    }


    public void onSaveInstanceState(Bundle outState) {
        mapWidget.onSaveInstanceState(outState);
    }

    public void onLowMemory() {
        mapWidget.onLowMemory();
    }

    private boolean isShowjg = true;

    public MyMapManager getMapManager(){
        return myMapManager;
    }
    @Override
    public void upDateHealthInfoMatioList(List<HealthInfo> mylist) {

        Log.i(this.getClass().getSimpleName(),"mylist:"+mylist.size()+ " /list:"+list.size());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                list.addAll(mylist);
                if (isShowjg) {
                    if (list.size() > 0) {
                        findViewById(R.id.error_list_view).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.error_list_view).setVisibility(View.GONE);
                    }
                }
                healthInfoMationAdapter.notifyDataSetChanged();

                jg_tw.setText("警告数: "+list.size());
            }
        });

    }

    public void initMapManager() {
        myMapManager = new MyMapManager( mapWidget, this,activity);
        myMapManager.setMap(false);
     //  myMapManager.getLocation();
    }

    private CustomAirLink customAirLink;

    public void setCameras(){


        LogUtil.d("dji-ui  主图传 "+VideoFeeder.getInstance().getPrimaryVideoFeed().getVideoSource());
        LogUtil.d("dji-ui  副图传 "+VideoFeeder.getInstance().getSecondaryVideoFeed().getVideoSource());
        fpvWidget.setVisibility(View.VISIBLE);
        secondaryVideoView.setVisibility(View.INVISIBLE);

        if (customCameraManager == null) {
            customCameraManager = new CustomCameraManager(activity);
        }
        customCameraManager.getCamera();
        customAirLink = new CustomAirLink(this);



        if (customAirLink.isMultiStreamPlatform()){
            secondaryVideoView.setVisibility(View.VISIBLE);
            if (customAirLink.isM300Product()) {
                if (isSupportOcuSyncLink()) {
                    change.setVisibility(VISIBLE);

                    if (VideoFeeder.getInstance().getPrimaryVideoFeed().getVideoSource() ==PhysicalSource.UNKNOWN)
                    customAirLink.setM300(3,PhysicalSource.LEFT_CAM,"main");
                }
            }
        }
        fpvWidget.setVideoSource(SettingDefinitions.VideoSource.PRIMARY);
        secondaryFPVWidget.setVideoSource(SettingDefinitions.VideoSource.SECONDARY);
        customCamera = customCameraManager.getNowCamera(fpvWidget);

        cameraView.setCustomCamera(customCamera);

       // updateMainFPV();
    }

    public void initHealthInfo(){

        if (DJIContext.getAircraftInstance() !=null) {
            if (DJIContext.getAircraftInstance().getModel() != null) {

                String name = DJIContext.getAircraftInstance().getModel().getDisplayName();
                if (name == Model.MATRICE_300_RTK.getDisplayName()){
                    customHealthInfomation.setSupportHMS(true);
                }
                customHealthInfomation.addListener();
            }
        }
    }

    public void destoryHealthInfo(){
        customHealthInfomation.removeListener();
        customHealthInfomation.setSupportHMS(false);
        list.clear();
        healthInfoMationAdapter.notifyDataSetChanged();
    }

    public void dissconnectCamera(){
        customCameraManager.getCamera();
        setCameras();
    }

    public void dissconnectFlight() {
        change.setVisibility(GONE);
        secondaryVideoView.setVisibility(View.INVISIBLE);
        fpvWidget.setVideoSource(SettingDefinitions.VideoSource.PRIMARY);
        secondaryFPVWidget.setVideoSource(SettingDefinitions.VideoSource.SECONDARY);
    }


    public void cleanMap(){
        myMapManager.cleanMap();
    }

    public void setWayPointToMap(CustomWayPoint customWayPoint) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < customWayPoint.getTowerList().size(); i++) {
                    if (customWayPoint.getTowerList().get(i).isIschecked()) {
                        long tid = customWayPoint.getTowerList().get(i).getTid();
                        PointDao pointDao = new PointDao(context);
                        List<DBHoverPoint> dbHoverPoints = pointDao.selePiontByPid(tid);
                        if (customWayPoint.getTowerIndex() == i){
                            dbHoverPoints = dbHoverPoints.subList(customWayPoint.getPointIndex(),dbHoverPoints.size());
                        }
                        for (int j = 0; j < dbHoverPoints.size(); j++) {

                            String location = dbHoverPoints.get(j).getHoverLocation();
                            if (location!=null&&location!=""){
                                String[] locations = location.split(" ");
                                LogUtil.d(" newHeight:"+location +"   "+locations.length);
                                double lat = Double.valueOf(locations[0]);
                                double lng = Double.valueOf(locations[1]);
                                Float height = Float.valueOf(locations[2]);
                                DJILatLng djiLatLng = new DJILatLng(lng,lat,height);
                                Log.e("11111", "添加地图点位:" + djiLatLng);
                                myMapManager.markWaypoint(djiLatLng);
                                if (i==0&&j==0){
                                    myMapManager.cameraUpdate(djiLatLng.getLatitude(), djiLatLng.getLongitude());
                                }
                            }
                        }
                    }
                }
                myMapManager.PolyLine();
            }
        }).start();


    }


    public void setRadarView(boolean isChecked) {
        if (isChecked) {
            findViewById(R.id.rl_radar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rl_radar).setVisibility(View.GONE);
        }
    }

    private boolean isSupportOcuSyncLink(){
        if (DJIContext.getAircraftInstance()!=null){
            if (DJIContext.getAircraftInstance().getAirLink()!=null){
                return DJIContext.getAircraftInstance().getAirLink().isOcuSyncLinkSupported();
            }
        }
        return false;
    }



    /**
     * 列表 dialog
     */
    private void showList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder = new AlertDialog.Builder(getContext()).setIcon(R.mipmap.ic_launcher)
                .setTitle("列表dialog")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                })
                .setItems(videoName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String type = "main";
                        PhysicalSource source = null;
                        if (fpvWidget.getVideoSource() == SettingDefinitions.VideoSource.PRIMARY){
                            type = "second";
                            source = VideoFeeder.getInstance().getPrimaryVideoFeed().getVideoSource();
                        }else {
                            source = VideoFeeder.getInstance().getSecondaryVideoFeed().getVideoSource();
                        }
                        customAirLink.setM300(i,source,type);
                    }
                });
        builder.create().show();
    }
DJIMarker djiMarker;
    @Override
    public void selectedMarkerItem(DJIMarker djiMarker) {
        this.djiMarker = djiMarker;
        if (djiMarker.getTag()=="rc") {
            DialogManager.showSelectDialog(activity, "提示", "确定使用该点作为home点？", "确定", "取消", false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (getMyAircraft() != null) {
                        myAircraft.setGoHomeLocation(new LocationCoordinate2D(djiMarker.getPosition().latitude, djiMarker.getPosition().longitude));
                    }


                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //ToDo: 你想做的事情
                    dialog.dismiss();
                }
            });

        }
    }

    @Override
    public void addItem(DJILatLng djiLatLng) {


    }


    @Override
    public void refreshFpv() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //secondaryFPVWidget.updateFpvInfo();
            }
        });
    }

    @Override
    public void receiver(FlightControllerState flightControllerState) {

    }

    @Override
    public void getSetGoHomeHeightResult(DJIError djiError) {

    }

    @Override
    public void getFlightMaxHeight(Integer integer) {

    }

    @Override
    public void getFlightMaxRadius(Integer integer) {

    }

    @Override
    public void getFlightGoHomeHieght(Integer integer) {

    }

    @Override
    public void getHomeLocation(LocationCoordinate2D locationCoordinate2D) {
        myMapManager.markhomePoint(new DJILatLng(locationCoordinate2D.getLatitude(),locationCoordinate2D.getLongitude()));
    }

    @Override
    public void getIsAllowSwitchFlightModel(boolean b) {

    }

    @Override
    public void setIsAllowSwitchFlightModel(DJIError djiError) {

    }

    @Override
    public void getIsAllowLimtFlightMaxRadius(boolean b) {

    }

    @Override
    public void setIsAllowLimtFlightMaxRadius(DJIError djiError) {

    }

    @Override
    public void getFailSafeBehavior(ConnectionFailSafeBehavior connectionFailSafeBehavior) {

    }

    @Override
    public void setFailSafeBehavior(DJIError djiError) {

    }

    @Override
    public void getIMUList() {

    }

    @Override
    public void getCompassList() {

    }

    @Override
    public void getSerialNumber(String s) {

    }

    @Override
    public void setGoHomeLocation(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 if (djiError!=null){
                     ToastUtils.setResultToToastUtil("设置home点出错 "+djiError.getDescription());
                 }else{
                     ToastUtils.setResultToToastUtil("设置home点成功");
                   //  myAircraft.getHomeLocation();
                    // myMapManager.markhomePoint(new DJILatLng(djiMarker.getPosition().getLatitude(),djiMarker.getPosition().longitude));
                 }
            }
        });
    }
}
