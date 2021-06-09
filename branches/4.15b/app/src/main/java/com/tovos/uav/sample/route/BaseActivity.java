package com.tovos.uav.sample.route;

import android.annotation.SuppressLint;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.amap.api.maps.offlinemap.OfflineMapActivity;
//import com.example.filgthhublibrary.view.FlightHubView;
import com.example.djilib.dji.DJIBaseActivity;
import com.example.djilib.dji.component.camera.CustomCameraManager;
import com.example.djilib.dji.component.gimbal.MyGimbal;
import com.example.filgthhublibrary.HubContext;
import com.example.filgthhublibrary.view.FlightHubView;
import com.nineoldandroids.view.ViewHelper;
import com.tovos.uav.sample.MApplication;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.playload.PayloadSendGetDataActivity;
import com.tovos.uav.sample.route.view.djiview.CompleteWidgetView;
import com.tovos.uav.sample.route.view.WaypointView;
import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.tovos.uav.sample.route.view.menu.FlightRecordView;
import com.tovos.uav.sample.route.view.menu.MapSetView;
import com.tovos.uav.sample.route.view.menu.TaskView;
import com.tovos.uav.sample.route.view.menu.TjKzView;
import com.tovos.uav.sample.route.view.set.BatterySetView;
import com.tovos.uav.sample.route.view.set.FlightControlView;
import com.tovos.uav.sample.route.view.set.GimbalSetView;
import com.tovos.uav.sample.route.view.set.InfoView;
import com.tovos.uav.sample.route.view.set.RTKView;
import com.tovos.uav.sample.route.view.set.RemoteControlView;
import com.tovos.uav.sample.route.view.set.ServerView;
import com.tovos.uav.sample.route.view.set.VisionSetView;
import com.example.commonlib.utils.DensityUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.TreeMap;


import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import dji.sdk.sdkmanager.DJISDKManager;

public class BaseActivity extends DJIBaseActivity {

    public RelativeLayout offline_map_rl, rtk_rl,
            ip_rl, clean_rl, info_rl, flightcontrol_rl, battery_set_rl, remotecontrol_rl, vission_set,gimbal_set;

    public FlightHubView flightHubView;
    public FlightRecordView flightRecordView;
    public VisionSetView visionSetView;
    public RemoteControlView remoteControlView;
    public FlightControlView flightControlView;
    public GimbalSetView gimbalSetView;
    public RTKView rtkView;
    public ServerView serverView;
    public BatterySetView batterysetView;
    public MapSetView mapSetView;
    public CompleteWidgetView completeWidgetView;
    public InfoView infoView;
    public ActionBar mActionBar;
    public DrawerLayout mDrawerLayout;

   // public TjKzView tjKzView;
    public CustomCameraManager customCameraManager;

    public TaskView taskView;
    public WaypointView waypointView;
    public RadioGroup waypoint_rg;
    public RelativeLayout menu_img;



    private PowerManager.WakeLock mWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        if(powerManager != null){
            mWakeLock=powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,"WakeLock");
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.pad_status_bar_bg));

        }
    }


    public void initView(ActivityListener listener){

        waypointView = (WaypointView)findViewById(R.id.waypointView);
        waypointView.setActivity(this,listener);
        waypointView.createCustromWayPointActivity("");

      //  tjKzView = (TjKzView)findViewById(R.id.tj_ll);
       // tjKzView.setListener(listener);
       // tjKzView.initSpeedView(waypointView.customWayPoint);

        flightRecordView = findViewById(R.id.tj_flight_record);
        flightRecordView.setActivityListener(listener,this);

        taskView = (TaskView)findViewById(R.id.task_ll);
        taskView.setActivityListener(listener,this);

        mapSetView = findViewById(R.id.map_ll);

        //司空view。不引入注释掉
        flightHubView = findViewById(R.id.hub_ll);
        findViewById(R.id.rt_flighthub).setVisibility(View.VISIBLE);
        flightHubView.resetHubView();
        flightHubView.setView();


        completeWidgetView = findViewById(R.id.completewidgetview);
        completeWidgetView.setActivity(this);

        visionSetView = (VisionSetView)findViewById(R.id.vissionSetView);
        visionSetView.setActivityListener(listener,this);
        visionSetView.setListener();
        visionSetView.setDisConnVision();

        flightControlView = (FlightControlView)findViewById(R.id.flightcontrolview);
        flightControlView.setActivityListener(this,listener);
        flightControlView.RefreshdissConnectView();

        batterysetView = findViewById(R.id.battery_set_view);
        batterysetView.setActivity(this);
        batterysetView.setView();
        batterysetView.setActivityListener(listener);
        batterysetView.setDisconnAircraftBattery();
        batterysetView.disconnect();

        remoteControlView = (RemoteControlView)findViewById(R.id.remoteControlView);
        remoteControlView.setActivityListener(listener,this);
        remoteControlView.setMapManager(completeWidgetView.getMapManager());

        infoView = (InfoView)findViewById(R.id.infoview);
        infoView.setActivityListener(listener);

        gimbalSetView = findViewById(R.id.gimbal_set_view);
        gimbalSetView.setActivityListener(listener);
        gimbalSetView.disConnData();


        rtkView = (RTKView) findViewById(R.id.rtkview);
        rtkView.setActivityListener(listener,this);

        serverView = (ServerView)findViewById(R.id.serverview);
        serverView.setActivityListener(listener);



        offline_map_rl = (RelativeLayout) findViewById(R.id.offlinemap_rl);
        rtk_rl = (RelativeLayout) findViewById(R.id.rtk_rk);
        ip_rl = (RelativeLayout) findViewById(R.id.ip_rk);
        clean_rl = (RelativeLayout) findViewById(R.id.clean_rk);
        info_rl = (RelativeLayout) findViewById(R.id.info_rl);

        flightcontrol_rl = (RelativeLayout) findViewById(R.id.flight_cont);
        battery_set_rl = findViewById(R.id.battery_set);
        vission_set = findViewById(R.id.vission_set);
        gimbal_set = findViewById(R.id.gimbal_set);

        remotecontrol_rl = (RelativeLayout) findViewById(R.id.remote_set);
        offline_map_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaseActivity.this, OfflineMapActivity.class));
            }
        });

        rtk_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.set_main).setVisibility(View.GONE);
                rtkView.setVisibility(View.VISIBLE);
            }
        });

        remotecontrol_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.set_main).setVisibility(View.GONE);
                remoteControlView.setVisibility(View.VISIBLE);
            }
        });

        flightcontrol_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.set_main).setVisibility(View.GONE);
                flightControlView.setVisibility(View.VISIBLE);
            }
        });

        gimbal_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.set_main).setVisibility(View.GONE);
                gimbalSetView.setVisibility(View.VISIBLE);

            }
        });
        ip_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.set_main).setVisibility(View.GONE);
                serverView.setVisibility(View.VISIBLE);
            }
        });

        clean_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        info_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.set_main).setVisibility(View.GONE);
                infoView.setVisibility(View.VISIBLE);
            }
        });


        battery_set_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.set_main).setVisibility(View.GONE);
                batterysetView.setVisibility(View.VISIBLE);
            }
        });

        vission_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.set_main).setVisibility(View.GONE);
                visionSetView.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * 初始化自动化飞行模块
     */
    public void initWayModelView() {
        waypoint_rg = (RadioGroup) findViewById(R.id.waypoint_rg);
        menu_img = (RelativeLayout) findViewById(R.id.menu_img);

        menu_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);

                }
            }
        });

        waypoint_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                findViewById(R.id.set_ll).setVisibility(View.GONE);
               // tjKzView.setVisibility(View.GONE);
                flightRecordView.setVisibility(View.GONE);
                taskView.setVisibility(View.GONE);
                mapSetView.setVisibility(View.GONE);
                flightHubView.setVisibility(View.GONE);
                switch (id) {

                    case R.id.rb_map:
                        mapSetView.setVisibility(View.VISIBLE);
                        break;

                    case R.id.rt_set:
                        findViewById(R.id.set_ll).setVisibility(View.VISIBLE);
                        break;

//                    case R.id.rb_tj:
//                        tjKzView.setVisibility(View.VISIBLE);
//                        break;

                    case R.id.rb_flight:
                        flightRecordView.setVisibility(View.VISIBLE);
                        flightRecordView.setTaskList();
                        break;

                    case R.id.rb_task:
                        taskView.setVisibility(View.VISIBLE);
                        taskView.initData();
                        break;

                    case R.id.rt_palyload:
                        Intent intent = new Intent(BaseActivity.this, PayloadSendGetDataActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.rt_flighthub:
                        flightHubView.setVisibility(View.VISIBLE);
                        flightHubView.setHubView();
                    default:
                        break;
                }
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_na);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setScrimColor(0x00ffffff);
        DrawerLayout.DrawerListener listen = new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(1);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                //改变DrawLayout侧栏透明度，若不需要效果可以不设置
                ViewHelper.setAlpha(mMenu, 1);//0.6f + 0.4f * (1 - scale));
                float x = DensityUtil.dip2px(BaseActivity.this, 69);
                if (slideOffset * drawerView.getWidth() <= x) {
                    mContent.setX(slideOffset * drawerView.getWidth());
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };
        mDrawerLayout.addDrawerListener(listen);
    }







    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        completeWidgetView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        completeWidgetView.onLowMemory();
    }

    @Override
    protected void onDestroy() {

        completeWidgetView.onDestroy();
        DJISDKManager.getInstance().destroy();

        if (MApplication.logcatHelper!=null){
            MApplication.logcatHelper.stop();
        }
        TreeMap<String,Object> treeMap = new TreeMap<>();
        treeMap.put("message","detory");
        EventBus.getDefault().postSticky(treeMap);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(getPackageName());

        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        completeWidgetView.onResume();
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        completeWidgetView.onPause();
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            //System.out.println("按下了back键   onKeyDown()");
            finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }


    public void initToolbar() {
        mActionBar = this.getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            mActionBar.setCustomView(R.layout.flight_status_layout);//设置自定义的布局：actionbar_custom
            //左侧按钮：可见+可用+更换图标
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setHomeButtonEnabled(false);
            mActionBar.setHomeAsUpIndicator(R.drawable.camera_controll_exposure_mode_p);
        }
        // mActionBar.hide();
    }

    /**
     * 复写：添加菜单布局
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fpv_set_menu, menu);
        return false;
    }

    /**
     * 复写：设置菜单监听
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //actionbar navigation up 按钮
            case android.R.id.home:

                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                }

                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);

                }
                break;

            case R.id.action_set:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
                break;

            default:
                break;
        }
        return true;
    }
}
