package com.tovos.uav.sample;

import android.app.Application;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Environment;


import com.example.commonlib.CommonContext;
import com.example.commonlib.utils.LogcatHelper;
import com.example.djilib.dji.DJIConnectionControlActivity;
import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.OnDJIUSBAttachedReceiver;
import com.example.filgthhublibrary.HubContext;
import com.tovos.uav.sample.installapp.MyInstalledReceiver;


import androidx.multidex.MultiDex;
import dji.ux.beta.core.base.DefaultGlobalPreferences;
import dji.ux.beta.core.base.GlobalPreferencesManager;

public class MApplication extends Application {


    public static String path = Environment.getExternalStorageDirectory()+"/tovos/";
    public static String wayPath = path+"waypoint/";
    public static String picPath = path+"pic/";
    public static boolean isTable = false;
    public static LogcatHelper logcatHelper;

    private static Application app = null;

    @Override
    public void onCreate() {
        super.onCreate();
        BroadcastReceiver br = new OnDJIUSBAttachedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIConnectionControlActivity.ACCESSORY_ATTACHED);
        registerReceiver(br, filter);

        BroadcastReceiver broadcastReceiver = new OnDJIUSBAttachedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        filter.addAction("android.intent.action.MY_PACKAGE_REPLACED");
        filter.addDataScheme("package");
        registerReceiver(broadcastReceiver, intentFilter);


        GlobalPreferencesManager.initialize(new DefaultGlobalPreferences(this));

        CommonContext.setContext(getApplicationContext());
        HubContext.setContext(getApplicationContext());
        isTable = isTabletDevice(this);
        //打开错误日志，保存到sd卡
        logcatHelper = LogcatHelper.getInstance(getApplicationContext());
        logcatHelper.start();


        DJIContext.setContext(getApplicationContext());

    }

    /**
     * 判断是否平板设备
     * @param context
     * @return true:平板,false:手机
     */
    private boolean isTabletDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        MultiDex.install(this);
        com.secneo.sdk.Helper.install(this);
        app = this;
    }

    public static Application getInstance(){
        return app;
    }
}
