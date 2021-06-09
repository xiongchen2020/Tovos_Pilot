package com.tovos.uav.sample.route.view.set;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dji.mapkit.core.models.DJILatLng;
import com.example.commonlib.utils.FastClickUtils;
import com.example.commonlib.utils.LogUtil;
import com.example.commonlib.utils.ToastUtils;
import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.component.aircraft.MyAircraft;
import com.example.djilib.dji.component.aircraft.MyAircraftInterface;
import com.example.djilib.dji.component.aircraft.database.CustomCompassData;
import com.example.djilib.dji.component.aircraft.database.CustomIMUData;
import com.example.djilib.dji.component.battery.CustomBattery;
import com.example.djilib.dji.component.battery.CustomBatteryListener;
import com.example.djilib.dji.component.remote.RemoteListener;
import com.example.djilib.dji.component.remote.RemoteManger;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.adapter.CompassAdapter;
import com.tovos.uav.sample.route.view.adapter.IMUAdapter;
import com.tovos.uav.sample.route.view.listener.ActivityListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dji.common.battery.AggregationState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.model.LocationCoordinate2D;
import dji.common.remotecontroller.AircraftMappingStyle;
import dji.common.remotecontroller.GPSData;
import dji.common.remotecontroller.RCMode;

public class FlightControlView extends LinearLayout implements MyAircraftInterface {

    private MyAircraft myAircraft;

    private Activity activity;
    private ActivityListener listener;
    private RelativeLayout flightcontrol_back , imuCheck;
    public CheckBox switchModel, switchFlightRadius;
    public Button button_jzzx;
    public TextView flight_jl_tw, go_home_tw;
    public RadioGroup skxw_rg, sensors_rb;
    public RadioButton xt, xj, fh;
    public SeekBar flight_jl, gohome_height;
    public RecyclerView sensorslistView;
    public ImageView next;
    public LinearLayout sensorStatus;
    public IMUAdapter imuAdapter;
    public List<CustomIMUData> list = new ArrayList<>();
    public CompassAdapter compassAdapter;
    public List<CustomCompassData> compassDataList = new ArrayList<>();

    public MyAircraft getMyAircraft(){
        if (DJIContext.getAircraftInstance()!=null){
            if (DJIContext.getAircraftInstance()!=null){
                if (myAircraft == null){
                    myAircraft = new MyAircraft(this);
                }
                return myAircraft;
            }
        }
        myAircraft = null;
        return null;
    }

    public void setActivityListener(Activity activity, ActivityListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    public FlightControlView(Context context) {
        super(context);
        initFlightControlSetView(context);
    }

    public FlightControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initFlightControlSetView(context);
    }

    public FlightControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFlightControlSetView(context);
    }

    public FlightControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFlightControlSetView(context);
    }

    public void initFlightControlSetView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.flightcontrol_set_layout, this);
        flight_jl_tw = (TextView) findViewById(R.id.fx_jx_tx);
        go_home_tw = (TextView) findViewById(R.id.fh_gd_tx);
        flight_jl = (SeekBar) findViewById(R.id.fx_jx);
        gohome_height = (SeekBar) findViewById(R.id.fh_gd);
        flightcontrol_back = (RelativeLayout) findViewById(R.id.flightcontrol_rl);
        imuCheck = (RelativeLayout) findViewById(R.id.check_rl);
        switchModel = (CheckBox) findViewById(R.id.swith_flight_model);
        switchFlightRadius = (CheckBox) findViewById(R.id.limit_gl);
        skxw_rg = (RadioGroup) findViewById(R.id.sk_xw_rg);
        xt = (RadioButton) findViewById(R.id.sk_xw_rb1);
        xj = (RadioButton) findViewById(R.id.sk_xw_rb2);
        fh = (RadioButton) findViewById(R.id.sk_xw_rb3);
        button_jzzx = (Button) findViewById(R.id.button_jzzx);
        sensorslistView = (RecyclerView) findViewById(R.id.flight_sensors);
        next = findViewById(R.id.iv_next);
        sensorStatus = findViewById(R.id.sensor_status);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        sensorslistView.setLayoutManager(linearLayoutManager);
        imuAdapter = new IMUAdapter(getContext(), list);
        sensorslistView.setAdapter(imuAdapter);

        compassAdapter = new CompassAdapter(getContext(), compassDataList);
        sensors_rb = (RadioGroup) findViewById(R.id.sensors_rb);

        flight_jl.setMax(8000);
        gohome_height.setMax(500);

        imuCheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((RadioButton)findViewById(R.id.imu_rb)).isChecked()){

                    myAircraft.startIMUCalibration();
                }else if (((RadioButton)findViewById(R.id.compass_rb)).isChecked()){

                    //myAircraft.star
                }
            }
        });

        flightcontrol_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backToMenuView(FlightControlView.this);
            }
        });

        switchModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getMyAircraft() == null) {
                    switchModel.setChecked(false);
                } else {
                    //
                    if (myAircraft.isAllowChangeModel) {
                        myAircraft.setAllowSwitchFlightModel(false);
                    } else {
                        myAircraft.setAllowSwitchFlightModel(true);
                    }
                }
            }
        });


        switchFlightRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMyAircraft() != null) {
                    if (myAircraft.isAllowLimit) {
                        myAircraft.setAllowFlightRadius(false);
                    } else {
                        myAircraft.setAllowFlightRadius(true);
                    }
                }
            }
        });

        flight_jl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                flight_jl_tw.setText(seekBar.getProgress() + "M");
                if (getMyAircraft()!=null){
                    myAircraft.setMaxFlightRadius(seekBar.getProgress());
                }

            }
        });

        gohome_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                go_home_tw.setText(seekBar.getProgress() + "M");
                if (getMyAircraft()!=null){
                    myAircraft.setGoHomeModel(seekBar.getProgress());
                }
            }
        });

        skxw_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (getMyAircraft() == null) {
                    return;
                }
                switch (checkedId) {
                    case R.id.sk_xw_rb1:
                        myAircraft.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.HOVER);
                        break;
                    case R.id.sk_xw_rb2:
                        myAircraft.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.LANDING);
                        break;
                    case R.id.sk_xw_rb3:
                        myAircraft.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.GO_HOME);
                        break;
                }
            }
        });

        button_jzzx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMyAircraft() != null) {
                    myAircraft.startGravityCenterCalibration();
                    // myAircraft.stopGravityCenterCalibration();
                }
            }
        });



        sensors_rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.imu_rb:
                        sensorslistView.setAdapter(imuAdapter);
                        imuAdapter.notifyDataSetChanged();
                        break;
                    case R.id.compass_rb:
                        sensorslistView.setAdapter(compassAdapter);
                        compassAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sensorStatus.getVisibility() == View.VISIBLE) {
                    next.setSelected(false);
                    sensorStatus.setVisibility(View.GONE);
                } else {
                    next.setSelected(true);
                    sensorStatus.setVisibility(View.VISIBLE);


                }
            }
        });
    }

    public void RefreshdissConnectView(){
        gohome_height.setProgress(20);
        flight_jl.setProgress(15);
        compassDataList.clear();
        go_home_tw.setText(20 + "M");
        flight_jl_tw.setText("15M");
        gohome_height.setEnabled(false);
        flight_jl.setEnabled(false);
        imuAdapter.notifyDataSetChanged();
        compassAdapter.notifyDataSetChanged();
        if (DJIContext.getAircraftInstance() == null) {
            gohome_height.setEnabled(false);
            flight_jl.setEnabled(false);
        }
    }

    public void refreshConnectView(){
        getMyAircraft();

        gohome_height.setEnabled(true);
        flight_jl.setEnabled(true);

    }

    @Override
    public void receiver(FlightControllerState flightControllerState) {

        listener.setRtkView(flightControllerState.isFlying());
        LocationCoordinate3D locationCoordinate3D = flightControllerState.getAircraftLocation();//飞机的3D位置
        if (locationCoordinate3D == null) {
            Log.e("TAG", "locationCoordinate3D==null");
            return;
        } else {
            DJILatLng now = new DJILatLng(locationCoordinate3D.getLatitude(), locationCoordinate3D.getLongitude());
            //customWayPoint.homeLocation = now;
            listener.setCustomWaypoint(now);
        }

        if (!FastClickUtils.isFastClick(2000)) {
            TreeMap<String,Object> treeMap = new TreeMap<>();
            treeMap.put("message","flightControllerState");
            treeMap.put("flightControllerState",flightControllerState);
            EventBus.getDefault().postSticky(treeMap);
        }
    }

    @Override
    public void getSetGoHomeHeightResult(DJIError djiError) {

    }

    @Override
    public void getFlightMaxHeight(final Integer integer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gohome_height.setMax(integer.intValue());
            }
        });

    }

    @Override
    public void getFlightGoHomeHieght(Integer integer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                go_home_tw.setText(integer.intValue() + "M");
                gohome_height.setProgress(integer.intValue());
            }
        });

    }

    @Override
    public void getHomeLocation(LocationCoordinate2D locationCoordinate2D) {

    }

    @Override
    public void getFlightMaxRadius(final Integer integer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                flight_jl_tw.setText(integer.intValue() + "M");
                flight_jl.setProgress(integer.intValue());
            }
        });

    }

    @Override
    public void getIsAllowSwitchFlightModel(boolean allow) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switchModel.setChecked(allow);
            }
        });
    }

    @Override
    public void setIsAllowSwitchFlightModel(DJIError djiError) {

    }

    @Override
    public void getIsAllowLimtFlightMaxRadius(boolean allow) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switchFlightRadius.setChecked(allow);
                flight_jl.setEnabled(allow);
            }
        });

    }

    @Override
    public void setIsAllowLimtFlightMaxRadius(DJIError djiError) {

        if (getMyAircraft()!=null){
            myAircraft.getAllowFlightRadius();
        }
    }


    @Override
    public void getFailSafeBehavior(ConnectionFailSafeBehavior failSafeBehavior) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (failSafeBehavior == ConnectionFailSafeBehavior.GO_HOME) {
                    fh.setChecked(true);
                } else if (failSafeBehavior == ConnectionFailSafeBehavior.HOVER) {
                    xt.setChecked(true);
                } else if (failSafeBehavior == ConnectionFailSafeBehavior.LANDING) {
                    xj.setChecked(true);
                }
            }
        });

    }

    @Override
    public void setFailSafeBehavior(DJIError djiError) {

    }

    @Override
    public void getIMUList() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                list.addAll(myAircraft.customIMUDatalist);
                imuAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void getCompassList() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                compassDataList.clear();
                compassDataList.addAll(myAircraft.customCompass.compassDataList);
                compassAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void getSerialNumber(String s) {

    }

    @Override
    public void setGoHomeLocation(DJIError djiError) {
        
    }


}
