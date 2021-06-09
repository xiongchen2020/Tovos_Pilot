package com.tovos.uav.sample.route.view.set;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.component.aircraft.flightAssistant.CustomFlightAssistant;
import com.example.djilib.dji.component.aircraft.flightAssistant.CustomFlightAssistantListener;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.example.commonlib.utils.SPUtils;
import com.example.commonlib.utils.ToastUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import dji.common.error.DJIError;
import dji.common.flightcontroller.flightassistant.ObstacleAvoidanceSensorState;
import dji.common.flightcontroller.flightassistant.PerceptionInformation;
import dji.common.product.Model;

public class VisionSetView extends LinearLayout implements CustomFlightAssistantListener {

   Activity activity;
    SeekBar sbz_sb,xbz_sb,sjg_sb,xjh_sb,bz_sb,jg_sb;
    Switch sbz_sw,aroud_sw;
    LinearLayout s_bz_ll,around_bz_ll,x_bz_ll;
    TextView sbz_sb_tx,xbz_sb_tx,sjg_sb_tx,xjh_sb_tx,bz_sb_tx,jg_sb_tx;
    RelativeLayout vission_rl,rl_show_radar;
    public Switch  swith_avoidance, swith_radar, swith_position, swith_rth_avoidance,swith_precision_landing;
    private ActivityListener listener;
    private Context context;
    private CustomFlightAssistant customFlightAssistant;

    public CustomFlightAssistant getCustomFlightAssistant(){
        //CustomFlightAssistant item = null;
        if (DJIContext.getAircraftInstance()!=null){
            if (DJIContext.getAircraftInstance().getFlightController()!=null){
                if (customFlightAssistant == null){
                    customFlightAssistant = new CustomFlightAssistant(this);
                }
                return customFlightAssistant;
                //return MApplication.getAircraftInstance().getFlightController().getFlightAssistant();
            }
        }
        customFlightAssistant = null;
        return customFlightAssistant;
    }

    public VisionSetView(Context context) {
        super(context);
        initView(context);
    }

    public VisionSetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VisionSetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public VisionSetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public void setActivityListener(ActivityListener listener, Activity activity){
        this.listener = listener;
        this.activity = activity;
    }

    //public void setMyAircraft(MyAircraft myAircraft) {
    //    this.myAircraft = myAircraft;
    //}

    //初始化UI，可根据业务需求设置默认值。
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.vision_set_layout, this);
        this.context = context;
        vission_rl = findViewById(R.id.vission_rl);
        swith_avoidance = findViewById(R.id.swith_avoidance);
        rl_show_radar = findViewById(R.id.rl_show_radar);
        swith_radar = findViewById(R.id.swith_radar);
        //rl_radar = findViewById(R.id.rl_radar);
        swith_position = findViewById(R.id.swith_position);
        swith_rth_avoidance = findViewById(R.id.swith_rth_avoidance);
        swith_precision_landing = findViewById(R.id.swith_precision_landing);

        s_bz_ll = findViewById(R.id.sx_bz_ll);
        x_bz_ll = findViewById(R.id.x_bz_ll);
        around_bz_ll = findViewById(R.id.around_bz_ll);
        sbz_sw = findViewById(R.id.swith_sbz);
        aroud_sw = findViewById(R.id.swith_aroundbz);
        sbz_sb = findViewById(R.id.sbzaqjl);
        xbz_sb = findViewById(R.id.xbzaqjl_sd);
        sjg_sb = findViewById(R.id.sjgjl_sd);
        xjh_sb = findViewById(R.id.xjgjl_sd);
        bz_sb = findViewById(R.id.bzjl);
        jg_sb = findViewById(R.id.jgjl);

        sbz_sb_tx = findViewById(R.id.sbzaqjl_tx);
        xbz_sb_tx = findViewById(R.id.xbzaqjl_tx);
        sjg_sb_tx = findViewById(R.id.sjgjl_tx);
        xjh_sb_tx = findViewById(R.id.xjgjl_tx);
        bz_sb_tx = findViewById(R.id.bzjl_tx);
        jg_sb_tx = findViewById(R.id.jgjl_tx);

        sbz_sw = findViewById(R.id.swith_sbz);
      //  xbz_sw = findViewById(R.id.swith_xbz);
        aroud_sw = findViewById(R.id.swith_aroundbz);
    }

    public void setListener(){

        sbz_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCustomFlightAssistant() != null) {
                    if (getCustomFlightAssistant().isSbz&&getCustomFlightAssistant().isLbz){
                        getCustomFlightAssistant().setUpwardVisionObstacleAvoidanceEnabled(false);
                        getCustomFlightAssistant().setLandingProtectionEnabled(false);
                    }else {
                        getCustomFlightAssistant().setUpwardVisionObstacleAvoidanceEnabled(true);
                        getCustomFlightAssistant().setLandingProtectionEnabled(true);
                    }
                }
            }
        });

        aroud_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCustomFlightAssistant() != null) {
                   // CustomFlightAssistant customFlightAssistant = myAircraft.getCustomFlightAssistant();
                    // CustomFlightAssistant customFlightAssistant = myAircraft.getCustomFlightAssistant();
                    if (getCustomFlightAssistant().isXbz){
                        getCustomFlightAssistant().setHorizontalVisionObstacleAvoidanceEnabled(false);
                    }else {
                        getCustomFlightAssistant().setHorizontalVisionObstacleAvoidanceEnabled(true);
                    }
                }
            }
        });

        sbz_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sbz_sb_tx.setText(progress+"M");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (getCustomFlightAssistant()!=null) {
                    getCustomFlightAssistant().setObstaclesAvoidanceDistance((float) seekBar.getProgress(),
                            PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Upward);
                }
            }
        });

        sjg_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sjg_sb_tx.setText(progress+"M");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (getCustomFlightAssistant()!=null) {
                    getCustomFlightAssistant().setMaxPerceptionDistance((float) seekBar.getProgress(),
                            PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Upward);
                }
            }
        });

        xbz_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                xbz_sb_tx.setText(progress+"M");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (getCustomFlightAssistant()!=null) {
                    getCustomFlightAssistant().setObstaclesAvoidanceDistance((float) seekBar.getProgress(),
                            PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Downward);
                }
            }
        });

        xjh_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                xjh_sb_tx.setText(progress+"M");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (getCustomFlightAssistant()!=null) {
                    getCustomFlightAssistant().setMaxPerceptionDistance((float) seekBar.getProgress(),
                            PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Downward);
                }
            }
        });

        bz_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bz_sb_tx.setText(progress+"M");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (getCustomFlightAssistant()!=null) {
                    getCustomFlightAssistant().setObstaclesAvoidanceDistance((float) (seekBar.getProgress()),
                            PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Horizontal);
                }
            }
        });

        jg_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                jg_sb_tx.setText(progress+"M");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (getCustomFlightAssistant()!=null) {
                    getCustomFlightAssistant().setMaxPerceptionDistance((float) seekBar.getProgress(),
                            PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Horizontal);
                }
            }
        });

        vission_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backToMenuView(VisionSetView.this);
            }
        });

        swith_avoidance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCustomFlightAssistant() != null) {
                    if (getCustomFlightAssistant().collisionAvoidanceEnabled) {
                        getCustomFlightAssistant().setCollisionAvoidanceEnabled(false);
                    } else {
                        getCustomFlightAssistant().setCollisionAvoidanceEnabled(true);
                    }
                }
            }
        });


        swith_radar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put("is_show_radar",isChecked);
                listener.setRadarView(isChecked);

            }
        });
        swith_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCustomFlightAssistant() != null) {

                    if (getCustomFlightAssistant().visionAssistedPositioningEnabled) {
                        getCustomFlightAssistant().setVisionAssistedPositioningEnable(false);
                    } else {
                        getCustomFlightAssistant().setVisionAssistedPositioningEnable(true);
                    }
                }
            }
        });
        swith_rth_avoidance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCustomFlightAssistant() != null) {
                    if (getCustomFlightAssistant().rthObstacleAvoidanceEnabled) {
                        getCustomFlightAssistant().setRTHObstacleAvoidanceEnabled(false);
                    } else {
                        getCustomFlightAssistant().setRTHObstacleAvoidanceEnabled(true);
                    }
                }

            }
        });
        swith_precision_landing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCustomFlightAssistant() != null) {
                    if (getCustomFlightAssistant().precisionLandingEnabled) {
                        getCustomFlightAssistant().setPrecisionLandingEnabled(false);
                    } else {
                        getCustomFlightAssistant().setPrecisionLandingEnabled(true);

                    }
                }
            }
        });

    }

    public void setConnVision() {

        if (getCustomFlightAssistant() != null) {
            swith_avoidance.setEnabled(true);
            swith_radar.setEnabled(true);
            swith_position.setEnabled(true);
            swith_rth_avoidance.setEnabled(true);
            swith_precision_landing.setEnabled(true);
            findViewById(R.id.vission_rl_bc).setVisibility(View.VISIBLE);
            rl_show_radar.setVisibility(View.VISIBLE);
            s_bz_ll.setVisibility(View.GONE);
            x_bz_ll.setVisibility(View.GONE);
            around_bz_ll.setVisibility(View.GONE);
            Boolean isShowRadar = (boolean) SPUtils.get("is_show_radar",false);
            swith_radar.setChecked(isShowRadar);
            listener.setRadarView(isShowRadar);
            if (DJIContext.getAircraftInstance().getModel().getDisplayName() == Model.MATRICE_300_RTK.getDisplayName()){
                rl_show_radar.setVisibility(View.GONE);
                s_bz_ll.setVisibility(View.VISIBLE);
                x_bz_ll.setVisibility(View.VISIBLE);
                around_bz_ll.setVisibility(View.VISIBLE);
                findViewById(R.id.vission_rl_bc).setVisibility(View.GONE);
                getCustomFlightAssistant().getUpwardVisionObstacleAvoidanceEnabled();
                getCustomFlightAssistant().getHorizontalVisionObstacleAvoidanceEnabled();
                getCustomFlightAssistant().getLandingProtectionEnabled();
                getCustomFlightAssistant().getMaxPerceptionDistance(PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Upward);
                getCustomFlightAssistant().getMaxPerceptionDistance(PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Downward);
                getCustomFlightAssistant().getMaxPerceptionDistance(PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Horizontal);
                getCustomFlightAssistant().getObstaclesAvoidanceDistance(PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Upward);
                getCustomFlightAssistant().getObstaclesAvoidanceDistance(PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Downward);
                getCustomFlightAssistant().getObstaclesAvoidanceDistance(PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Horizontal);
            }
            getCustomFlightAssistant().getCollisionAvoidanceEnable();
            getCustomFlightAssistant().getVisionAssistedPositioningEnable();
            getCustomFlightAssistant().getRTHObstraleAvoidanceEnable();
            getCustomFlightAssistant().getPrecisionLandingEnabled();

        }
    }
    public void setDisConnVision() {


            swith_avoidance.setEnabled(false);
            swith_radar.setEnabled(false);
            swith_position.setEnabled(false);
            swith_rth_avoidance.setEnabled(false);
            swith_precision_landing.setEnabled(false);
            rl_show_radar.setVisibility(View.VISIBLE);
            s_bz_ll.setVisibility(View.GONE);
            x_bz_ll.setVisibility(View.GONE);
            around_bz_ll.setVisibility(View.GONE);
            findViewById(R.id.vission_rl_bc).setVisibility(View.VISIBLE);

    }

    @Override
    public void getCollisionAvoidanceEnabled(Boolean aBoolean){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swith_avoidance.setChecked(aBoolean);
            }
        });

    }

    @Override
    public void setCollisionAvoidanceEnabled(DJIError djiError) {
        if (djiError != null) {
            ToastUtils.setResultToToast("设置启用视觉避障系统失败：" + djiError.getDescription());
        }
    }

    @Override
    public void getVisionAssistedPositioningEnabled(Boolean aBoolean) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swith_position.setChecked(aBoolean);
            }
        });

    }

    @Override
    public void setVisionAssistedPositioningEnabled(DJIError djiError) {
        if (djiError != null) {
            ToastUtils.setResultToToast("设置启用视下视视觉定位失败：" + djiError.getDescription());
        }
    }

    @Override
    public void getRTHObstacleAvoidanceEnabled(Boolean aBoolean) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swith_rth_avoidance.setChecked(aBoolean);
            }
        });

    }

    @Override
    public void setRTHObstacleAvoidanceEnabled(DJIError djiError) {
        if (djiError != null) {
            ToastUtils.setResultToToast("设置启用视返航障碍物失败：" + djiError.getDescription());
        }
    }

    @Override
    public void getFlightAssistantFailure(DJIError djiError) {
        if (djiError != null) {
            ToastUtils.setResultToToast("获取状态失败：" + djiError.getDescription());
        }
    }

    @Override
    public void setDownwardAvoidanceEnabled(boolean isable) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aroud_sw.setChecked(isable);
                if (isable) {
                    findViewById(R.id.around_bz_set).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.around_bz_set).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void setUpwardAvoidanceEnabled(boolean isable) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sbz_sw.setChecked(isable);
                if (isable) {
                    findViewById(R.id.sbz_sz).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.sbz_sz).setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void setLandingProtectionEnabled(boolean isable) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                xbz_sw.setChecked(isable);
//                if (isable) {
//                    findViewById(R.id.xbz_ll).setVisibility(View.VISIBLE);
//                } else {
//                    findViewById(R.id.xbz_ll).setVisibility(View.GONE);
//                }
//            }
//        });
    }

    @Override
    public void setActiveObstacleAvoidanceEnabled(boolean isable) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

    }

    @Override
    public void getObstaclesAvoidanceDistance(float var, PerceptionInformation.DJIFlightAssistantObstacleSensingDirection flightAssistantObstacleSensingDirection) {

       activity.runOnUiThread(new Runnable() {
           @Override
           public void run() {
               switch (flightAssistantObstacleSensingDirection) {
                   case Upward:
                       sbz_sb.setProgress(Math.round(var));
                       sbz_sb_tx.setText(Math.round(var) + "M");
                       break;
                   case Downward:
                       xbz_sb.setProgress(Math.round(var));
                       xbz_sb_tx.setText(Math.round(var) + "M");
                       break;
                   case Horizontal:
                       bz_sb.setProgress(Math.round(var));
                       bz_sb_tx.setText(Math.round(var) + "M");
                       break;
               }
           }
       });
    }

    @Override
    public void setMaxPerceptionDistance(float var, PerceptionInformation.DJIFlightAssistantObstacleSensingDirection flightAssistantObstacleSensingDirection) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (flightAssistantObstacleSensingDirection) {
                    case Upward:
                        sjg_sb.setProgress(Math.round(var));
                        sjg_sb_tx.setText(Math.round(var) + "M");
                        break;
                    case Downward:
                        xjh_sb.setProgress(Math.round(var));
                        xjh_sb_tx.setText(Math.round(var) + "M");
                        break;
                    case Horizontal:
                        jg_sb.setProgress(Math.round(var));
                        jg_sb_tx.setText(Math.round(var) + "M");
                        break;
                }
            }
        });

    }

    @Override
    public void setObstacleAvoidanceSensorState(ObstacleAvoidanceSensorState obstacleAvoidanceSensorState) {

    }

    @Override
    public void getPrecisionLandingEnabled(Boolean aBoolean) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swith_precision_landing.setChecked(aBoolean);
            }
        });
    }

    @Override
    public void getPrecisionLandingEnabledFailure(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError!=null){
                    swith_precision_landing.setChecked(!swith_precision_landing.isChecked());
                }
            }
        });
    }

    @Override
    public void setPrecisionLandingEnabled(DJIError djiError) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError!=null){
                    swith_precision_landing.setChecked(!swith_precision_landing.isChecked());
                    ToastUtils.setResultToToast("设置精准降落状态失败：" + djiError.getDescription());
                }
            }
        });
    }
}
