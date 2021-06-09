package com.tovos.uav.sample.route.view.set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.dji.mapkit.core.models.DJILatLng;
import com.example.commonlib.utils.LogUtil;
import com.example.commonlib.utils.ToastUtils;
import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.component.aircraft.MyAircraft;
import com.example.djilib.dji.component.aircraft.MyAircraftInterface;
import com.example.djilib.dji.component.remote.RemoteListener;
import com.example.djilib.dji.component.remote.RemoteManger;
import com.example.djilib.dji.map.MyMapManager;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.listener.ActivityListener;

import androidx.annotation.Nullable;
import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.model.LocationCoordinate2D;
import dji.common.remotecontroller.AircraftMappingStyle;
import dji.common.remotecontroller.GPSData;
import dji.common.remotecontroller.PairingDevice;
import dji.common.remotecontroller.RCMode;


public class RemoteControlView extends LinearLayout implements RemoteListener {
    public RadioGroup mapping_style_group;
    private ActivityListener listener;
    private Activity activity;
    private RadioButton style_1_rb, style_2_rb, style_3_rb;
    private RelativeLayout remoteBackRl, remote_checked,rtk_channel_rl,multi_device_pairing_rl;
    private RadioGroup sk_st_rg;
    private RadioButton sk_st_rb, sk_st_rb2;
    private Switch rtk_channel,multi_device_pairing;
    private RemoteManger remoteManger;
    private MyMapManager myMapManager;

    public void setActivityListener(ActivityListener listener,Activity activity){
        this.listener = listener;
        this.activity = activity;
    }
    public  void setMapManager(MyMapManager mapManager){
        myMapManager = mapManager;
    }

    public RemoteManger getRemoteManager(){
        if (DJIContext.getAircraftInstance()!=null){
            if (DJIContext.getAircraftInstance().getRemoteController()!=null){
                if (remoteManger == null) {
                    remoteManger = new RemoteManger(this);
                }
                return remoteManger;
            }
        }
        remoteManger = null;
        return remoteManger;
    }

    public RemoteControlView(Context context) {
        super(context);
        initRemoteControlSetView(context);
    }

    public RemoteControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initRemoteControlSetView(context);
    }

    public RemoteControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRemoteControlSetView(context);
    }

    public RemoteControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initRemoteControlSetView(context);
    }

    public void initRemoteControlSetView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.remotecontrol_set_layout, this);
        sk_st_rb = (RadioButton) findViewById(R.id.sk_st_rb);
        sk_st_rb2 = (RadioButton) findViewById(R.id.sk_st_rb2);
        remote_checked = (RelativeLayout) findViewById(R.id.remote_checked);
        sk_st_rg = (RadioGroup) findViewById(R.id.sk_st_rg);
        rtk_channel = (Switch) findViewById(R.id.rtk_channel);
        multi_device_pairing = (Switch) findViewById(R.id.multi_device_pairing);
        rtk_channel_rl = (RelativeLayout) findViewById(R.id.rtk_channel_rl);
        multi_device_pairing_rl = (RelativeLayout) findViewById(R.id.multi_device_pairing_rl);

        sk_st_rb.setEnabled(false);
        sk_st_rb2.setEnabled(false);
        rtk_channel.setChecked(false);
        multi_device_pairing.setChecked(false);
        remote_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRemoteManager() != null) {
                    remoteManger.startPairingRemoteControl();
                }
            }
        });
        sk_st_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (getRemoteManager() == null)
                    return;
                switch (checkedId) {
                    case R.id.sk_st_rb:
                        remoteManger.setMasterSlaveMode(RCMode.CHANNEL_A);
                        break;
                    case R.id.sk_st_rb2:
                        remoteManger.setMasterSlaveMode(RCMode.CHANNEL_B);
                        break;
                }
            }
        });

        remoteBackRl = (RelativeLayout) findViewById(R.id.remotecontrol_rl);
        remoteBackRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backToMenuView(RemoteControlView.this);
            }
        });

        //摇杆模式设置
        mapping_style_group = findViewById(R.id.mapping_style_group);
        style_1_rb = findViewById(R.id.style_1_rb);
        style_2_rb = findViewById(R.id.style_2_rb);
        style_3_rb = findViewById(R.id.style_3_rb);
        mapping_style_group.clearCheck();
        setDisConnectGroupSytly();
        mapping_style_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AircraftMappingStyle style = null;
                if (getRemoteManager() == null)
                    return;

                switch (checkedId) {

                    case R.id.style_1_rb:
                        style = AircraftMappingStyle.STYLE_1;
                        break;
                    case R.id.style_2_rb:
                        style = AircraftMappingStyle.STYLE_2;
                        break;
                    case R.id.style_3_rb:
                        style = AircraftMappingStyle.STYLE_3;
                        break;
                }
                remoteManger.setAircraftMappingStyle(style);
            }
        });
        rtk_channel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (remoteManger!=null){
                    remoteManger.setRTKChannelEnabled(b);
                }else{
                    rtk_channel.setChecked(false);
                }

            }
        });

        multi_device_pairing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (remoteManger!=null){
                    if (b){
                        PairingDevice device = PairingDevice.AIRCRAFT;
                        if (rtk_channel.isChecked()){
                            device = PairingDevice.RTK_BASE_STATION;
                        }
                        remoteManger.startMultiDevicePairing(device);
                    }else{
                        remoteManger.stopMultiDevicePairing();
                    }
                }else{
                    multi_device_pairing.setChecked(false);
                }

            }
        });
    }

    public void setDisConnectGroupSytly() {
        style_1_rb.setEnabled(false);
        style_2_rb.setEnabled(false);
        style_3_rb.setEnabled(false);
        sk_st_rb.setEnabled(false);
        sk_st_rb2.setEnabled(false);
    }

    public void setConnectGroupSytly() {
        Log.i(this.getClass().getSimpleName(),"setConnectGroupSytly");
        Log.i(this.getClass().getSimpleName(),"remoteManger:"+remoteManger);
        //remoteManger.getGPSData();
        if (getRemoteManager() != null) {
            style_1_rb.setEnabled(true);
            style_2_rb.setEnabled(true);
            style_3_rb.setEnabled(true);
            remoteManger.getAircraftMappingStyle();
            if (remoteManger.isMasterSlaveModeSupported()) {
                sk_st_rb.setEnabled(true);
                sk_st_rb2.setEnabled(true);

            } else {
                sk_st_rb.setEnabled(false);
                sk_st_rb2.setEnabled(false);
            }
            if (remoteManger.isMultiDevicePairingSupported()){
                rtk_channel_rl.setVisibility(VISIBLE);
                multi_device_pairing_rl.setVisibility(VISIBLE);
            }else{
                rtk_channel_rl.setVisibility(GONE);
                multi_device_pairing_rl.setVisibility(GONE);
            }
        }

    }

    @Override
    public void getMappingStyle(AircraftMappingStyle aircraftMappingStyle) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (aircraftMappingStyle == AircraftMappingStyle.STYLE_1) {
                    style_1_rb.setChecked(true);
                } else if (aircraftMappingStyle == AircraftMappingStyle.STYLE_2) {
                    style_2_rb.setChecked(true);
                } else if (aircraftMappingStyle == AircraftMappingStyle.STYLE_3) {
                    style_3_rb.setChecked(true);
                }
            }
        });

    }

    @Override
    public void setMappingStyle(DJIError djiError) {

        if (djiError == null) {
            remoteManger.getAircraftMappingStyle();
        }
    }

    @Override
    public void getMasterSlaveMode(RCMode rcMode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rcMode == RCMode.CHANNEL_A) {
                    sk_st_rb.setChecked(true);
                } else {
                    sk_st_rb2.setChecked(true);
                }
            }
        });

    }

    @Override
    public void setMasterSlaveMode(DJIError djiError) {

    }

    @Override
    public void startPairing() {
        showPairing();
    }

    @Override
    public void stopParing() {

        if (dialog!=null){
            dialog.create().dismiss();
        }
    }

    @Override
    public void startMultiDevicePairing(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError!=null){
                    multi_device_pairing.setChecked(false);
                    ToastUtils.setResultToToast("开启多设备对频失败");
                }
            }
        });

    }

    @Override
    public void stopMultiDevicePairing(DJIError djiError) {

    }

    @Override
    public void setRTKChannelEnabled(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError!=null){
                    rtk_channel.setChecked(false);
                    ToastUtils.setResultToToast("开启rtk对频失败");
                }
            }
        });
    }

    @Override
    public void setGPSData(GPSData gpsData) {
       LogUtil.d("RcGPS gpsData"+gpsData);
        if (gpsData!=null){

            myMapManager.markRcGPS(new DJILatLng( gpsData.getLocation().getLatitude(),gpsData.getLocation().getLongitude()));
        }
    }

    private AlertDialog.Builder dialog;

    private void showPairing() {

        dialog = new AlertDialog.Builder(getContext()).setIcon(R.mipmap.ic_launcher).setTitle("提示")
                .setMessage("正在进行遥控器对频，请长按飞机电源键5秒！")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getRemoteManager()!=null){
                            remoteManger.stopPairingRemoteControl();
                        }

                    }
                });
        dialog.create().show();
    }

    public void setDialogDiss(){
        if (dialog !=null){
            dialog.create().dismiss();
        }
    }


}
