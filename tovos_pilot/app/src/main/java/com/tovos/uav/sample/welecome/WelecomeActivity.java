package com.tovos.uav.sample.welecome;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.example.commonlib.utils.CustomTimeUtils;
import com.example.djilib.dji.bea.MessageEvent;
import com.example.djilib.dji.component.CustomComponent;
import com.tovos.uav.sample.MApplication;
import com.example.commonlib.utils.MyCrashHandler;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.WayPointActivity;
import com.example.commonlib.utils.DialogManager;
import com.example.commonlib.utils.FileManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class WelecomeActivity extends Activity {

    private int screen_x = 0;
    private int screen_y = 0;
    private int startx = 0;
    private int starty = 0;
    private int nowx = 0;
    private int nowy = 0;
    private static final int REQUEST_PERMISSION_CODE = 12345;
    private List<String> missingPermission = new ArrayList<>();
    public  CustomComponent customComponent;
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE, // Gimbal rotation
            Manifest.permission.INTERNET, // API requests
            Manifest.permission.ACCESS_WIFI_STATE, // WIFI connected products
            Manifest.permission.ACCESS_COARSE_LOCATION, // Maps
            Manifest.permission.ACCESS_NETWORK_STATE, // WIFI connected products
            Manifest.permission.ACCESS_FINE_LOCATION, // Maps
            Manifest.permission.CHANGE_WIFI_STATE, // Changing between WIFI and USB connection
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // Log files
            Manifest.permission.BLUETOOTH, // Bluetooth connected products
            Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            Manifest.permission.READ_EXTERNAL_STORAGE, // Log files
            Manifest.permission.READ_PHONE_STATE, // Device UUID accessed upon registration
            Manifest.permission.RECORD_AUDIO, // Speaker accessory
            Manifest.permission.KILL_BACKGROUND_PROCESSES
    };

    private List<WelecomeAnimationBean> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉标题栏
        setContentView(R.layout.welecome_layout);
        EventBus.getDefault().register(this);
        checkAndRequestPermissions();
        initComponent();


    }

    public void initComponent(){
        customComponent = new CustomComponent(this);
        customComponent.startSDKRegistration(getApplicationContext());
    }


    private void setFile(){
        FileManager fileManager = new FileManager();
        File file = fileManager.createDirectory(MApplication.path);
        File file2 = fileManager.createDirectory(MApplication.wayPath);
        File file3 = fileManager.createDirectory(MApplication.picPath);

        MyCrashHandler myCrashHandler = MyCrashHandler.getInstance();
        myCrashHandler.init(getApplicationContext());


    }

    private boolean istimeend = false;
    private void setIntent(){

        getScreenXY();


        Timer timer=new Timer();
        TimerTask tast=new TimerTask() {
            @Override
            public void run(){
                istimeend = true;
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (!compareTime()){
                            if (istimeend && isLogin) {
                                setFile();
                                Intent localIntent=new Intent(WelecomeActivity.this, WayPointActivity.class);//你要转向的Activity
                                startActivity(localIntent);//执行
                                finish();
                            }
                        }else {
                            showTwo();
                        }
                    }
                });

            }
        };
        timer.schedule(tast,6000);//10秒后
    }

    private boolean compareTime(){
        boolean isOverTime = false;
        String now = CustomTimeUtils.getNowDate();
        String overTime = "2021-08-01 00:00:00";
        int nums = 0;
        nums = CustomTimeUtils.timeCompare(now,overTime,"yyyy-MM-dd HH:mm");
        if (nums != 3){
            isOverTime = true;
        }
        return isOverTime;
    }

    private AlertDialog.Builder routeDele;
    private void showTwo() {

        routeDele = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("软件已过期，请与供应商联系").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        finish();
                        dialogInterface.dismiss();

                    }
                });
        routeDele.create().show();
    }




    private void setAnimation(View v,final int position){

        if (position<list.size()){
            WelecomeAnimationBean welecomeAnimationBean = list.get(position);
            Animation translateAnimation = new TranslateAnimation(welecomeAnimationBean.getNowx(),welecomeAnimationBean.getMovex()
                    ,welecomeAnimationBean.getNowy(),welecomeAnimationBean.getMovey());//平移动画  从0,0,平移到100,100
            translateAnimation.setDuration(2000);//动画持续的时间为1.5s
            //v.setAnimation(translateAnimation_left_up);//给imageView添加的动画效果
            translateAnimation.setFillEnabled(true);//使其可以填充效果从而不回到原地
            translateAnimation.setFillAfter(true);//不回到起始位置
            //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
            v.startAnimation(translateAnimation);//动画开始执行 放在最后即可

            translateAnimation.setAnimationListener(
                    new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {//开始时

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {//结束时
                            int new_position = position +1;
                            if (new_position< list.size()){
                                setAnimation(findViewById(R.id.welecome_flight), new_position);
                            }else {
                                setAnimation(findViewById(R.id.welecome_flight), 0);
                            }

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {//进行时

                        }
                    });
        }


    }

    private void getScreenXY(){
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screen_x = metric.widthPixels;     // 屏幕宽度（像素）
        screen_y = metric.heightPixels;   // 屏幕高度（像素）

        Log.e("1111","screen_x:"+screen_x+"   screen_y: "+screen_y);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        findViewById(R.id.welecome_flight).getLocationOnScreen(location);
        startx = location[0];
        starty= location[1];
        setAnimationInfo();
        setAnimation(findViewById(R.id.welecome_flight),0);
        super.onWindowFocusChanged(hasFocus);
    }

    private void setAnimationInfo(){

        float top = getResources().getDimension(R.dimen.welecome_bg_martin_top);
        int movey =  starty - (int)top;


        float right = getResources().getDimension(R.dimen.welecome_bg_martin_right);
        int movex = screen_x - startx - findViewById(R.id.welecome_flight).getMeasuredWidth() - (int)right;

        WelecomeAnimationBean welecomeAnimationBean1 = new WelecomeAnimationBean(0,0,0,-movey);
        WelecomeAnimationBean welecomeAnimationBean2 = new WelecomeAnimationBean(0,movex,-movey,-movey);
        WelecomeAnimationBean welecomeAnimationBean3 = new WelecomeAnimationBean(movex,0,-movey,-movey);
        WelecomeAnimationBean welecomeAnimationBean4 = new WelecomeAnimationBean(0,0,-movey,0);
        list.add(welecomeAnimationBean1);
        list.add(welecomeAnimationBean2);
        list.add(welecomeAnimationBean3);
        list.add(welecomeAnimationBean4);
    }

    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {

            setIntent();
        } else {
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }
    }


    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            setIntent();
        } else {
            Toast.makeText(getApplicationContext(), "权限缺失! 无法注册SDK以及连接飞机.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    boolean isLogin = false;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        /* Do something */
        if ("notlogin".equals(event.getMessage())) {
            isLogin = false;
        }else if ("authorized".equals(event.getMessage())){
            isLogin = true;
            if (istimeend && isLogin) {
                Intent localIntent=new Intent(WelecomeActivity.this, WayPointActivity.class);//你要转向的Activity
                startActivity(localIntent);//执行
                finish();
            }
        }else if ("loginError".equals(event.getMessage())||"sdkRegisterError".equals(event.getMessage())){
            DialogManager.showSingleButton(WelecomeActivity.this,R.drawable.logo,"提示","大疆账号登录失败，退出应用!","确定",false,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //ToDo: 你想做的事情
                    dialog.dismiss();
                    finish();

                }
            });


        }else if ("notnetwork".equals(event.getMessage())){
            DialogManager.showSingleButton(this,R.drawable.logo,"提示","设备未联网，大疆账号登录失败，退出应用!","确定",false,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //ToDo: 你想做的事情
                    dialog.dismiss();
                    finish();

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
