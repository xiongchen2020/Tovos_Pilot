package com.tovos.uav.sample.route.view.set;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.commonlib.utils.CustomTimeUtils;
import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.component.aircraft.MyAircraft;
import com.example.djilib.dji.component.aircraft.SmartListener;
import com.example.djilib.dji.component.aircraft.database.CustomIMUData;
import com.example.djilib.dji.component.battery.CustomBattery;
import com.example.djilib.dji.component.battery.CustomBatteryListener;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.adapter.BatteryInfoAdapter;
import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.tovos.uav.sample.route.view.listener.BatterySetListener;
import com.example.commonlib.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dji.common.battery.AggregationState;
import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.sdk.battery.Battery;

public class BatterySetView extends LinearLayout implements BatterySetListener, CustomBatteryListener, SmartListener {

    public RelativeLayout battery_rl, rl_battery_info;
    public TextView tv_slb, tv_lb, total_battery, flight_time;
    public SeekBar bar_slb, bar_lb;
    public Switch swith_go_home;
    public RecyclerView battery_list;
    public List<CustomIMUData> list = new ArrayList<>();
    public List<Battery> batteryList = new ArrayList<>();
    public BatteryInfoAdapter batteryInfoAdapter;

    int minBarlb = 15; //低电量最小百分比
    int minBarSlb = 10; //严重低电量最小百分比
    int maxBarDifference = 5; //严重低电量最大值是低电量-5
    boolean isNeedSlb = false;
    private Context context;
    private Activity activity;
    private ActivityListener listener;
    private MyAircraft myAircraft;
    private CustomBattery customBattery;


    public MyAircraft getAircraft() {
        if (DJIContext.getAircraftInstance() != null) {
            if (DJIContext.getAircraftInstance().getFlightController() != null) {
                if (myAircraft == null) {
                    myAircraft = new MyAircraft(this);
                }
                return myAircraft;
            }
        }
        myAircraft = null;
        return myAircraft;
    }

    public CustomBattery getCustomBattery(){
        if (DJIContext.getAircraftInstance()!=null){
            if (DJIContext.getAircraftInstance().getFlightController()!=null) {
                if (customBattery == null) {
                    customBattery = new CustomBattery(this);
                } else {
                    customBattery.updateCustomBatteryInfo();
                }
                return customBattery;
            }
        }
        customBattery = null;
        return null;
    }

    public BatterySetView(Context context) {
        this(context, null);

    }

    public BatterySetView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatterySetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BatterySetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.battery_set_layout, this);
        initView();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setActivityListener(ActivityListener listener) {
        this.listener = listener;
    }


    public void initView() {
        //电池设置初始化
        //返回
        battery_rl = findViewById(R.id.battery_rl);
        //电池信息
        rl_battery_info = findViewById(R.id.rl_battery_info);
        total_battery = findViewById(R.id.total_battery);
        flight_time = findViewById(R.id.flight_time);
        //智能回家
        swith_go_home = (Switch) findViewById(R.id.swith_go_home);
        bar_slb = (SeekBar) findViewById(R.id.bar_slb);
        tv_slb = findViewById(R.id.tv_slb);
        tv_lb = findViewById(R.id.tv_lb);
        bar_lb = (SeekBar) findViewById(R.id.bar_lb);
        battery_list = (RecyclerView) findViewById(R.id.battery_list);


    }

    public void setView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        battery_list.setLayoutManager(linearLayoutManager);
        batteryInfoAdapter = new BatteryInfoAdapter(activity, batteryList, this);
        battery_list.setAdapter(batteryInfoAdapter);
        bar_slb.setMax(35);
        bar_lb.setMax(35);

        battery_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backToMenuView(BatterySetView.this);
            }
        });

        swith_go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getAircraft() != null) {
                    if (myAircraft.isGoHomeEnable) {
                        myAircraft.setSmartReturToHomeEnable(false);
                    } else {
                        myAircraft.setSmartReturToHomeEnable(true);
                    }

                } else {
                    swith_go_home.setChecked(myAircraft.isGoHomeEnable);
                }
            }
        });

        bar_slb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_slb.setText(minBarSlb + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (bar_lb.getProgress() - maxBarDifference < seekBar.getProgress()) {
                    seekBar.setPressed(false);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                tv_lb.setText(15+seekBar.getProgress()+"%");
                if (getAircraft() != null) {
                    myAircraft.setSeriousLowBatteryWarningThresHolding(minBarSlb + seekBar.getProgress());
                } else {
                    //  ToastUtils.setResultToToast("myAircraft为空");
                }
            }
        });
        bar_lb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_lb.setText(minBarlb + progress + "%");
                bar_slb.setMax(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isNeedSlb = false;
                if (getAircraft() != null) {
                    if (bar_slb.getProgress() > bar_lb.getProgress() - maxBarDifference) {
                        isNeedSlb = true;
                        myAircraft.setSeriousLowBatteryWarningThresHolding(minBarSlb + bar_slb.getProgress());

                    } else {
                        myAircraft.setLowBatteryWarningThreshold(minBarlb + bar_lb.getProgress());
                    }


                } else {
                    //  ToastUtils.setResultToToast("myAircraft为空");
                }
            }
        });


    }

    public void setConnAircraftBattery() {

        if (getAircraft() != null) {

            swith_go_home.setEnabled(true);
            bar_lb.setEnabled(true);
            bar_slb.setEnabled(true);
            myAircraft.getSmartReturToHomeEnable();
            myAircraft.getLowBatteryWarningThreshold();
            myAircraft.getSeriousLowBatteryWarningThreshold();

        }

    }

    public void setDisconnAircraftBattery() {

        swith_go_home.setEnabled(false);
        bar_lb.setEnabled(false);
        bar_slb.setEnabled(false);


    }

    public void connect() {
        if (getAircraft() != null) {

            if (getCustomBattery() != null) {
                rl_battery_info.setVisibility(View.VISIBLE);
                batteryList.clear();
                batteryList.addAll(customBattery.getBatteryKeyLis());
                Log.i("zsj","adapter刷新");
                batteryInfoAdapter.notifyDataSetChanged();

                customBattery.setAggregationStateCallback();
            }
        }
    }

    public void disconnect(){
        batteryList.clear();
        batteryInfoAdapter.notifyDataSetChanged();
        rl_battery_info.setVisibility(View.GONE);
    }

    @Override
    public void setStateCallbackView(BatteryState batteryState) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (batteryList.size() == 1) {
                    Log.i("zsj","获取电量："+batteryState.getChargeRemainingInPercent());
                    total_battery.setText(batteryState.getChargeRemainingInPercent() + "%");
//                    TreeMap<String,Object> treeMap = new TreeMap<>();
//                    treeMap.put("message","remainingInPercent");
//                    treeMap.put("getPercent",batteryState.getChargeRemainingInPercent());
//                    EventBus.getDefault().postSticky(treeMap);
                    //EventBus.getDefault().postSticky(new CustomMessageEvent("remainingInPercent",batteryState.getChargeRemainingInPercent()));
                }
                if (getAircraft() != null) {
                    flight_time.setText(CustomTimeUtils.ScendsToHoursBySecInt(myAircraft.getFlightTime()) + "");
                }
            }
        });

    }


    @Override
    public void getSmartReturnToHomeEnabled(Boolean aBoolean) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swith_go_home.setChecked(aBoolean);
            }
        });
    }

    @Override
    public void getSmartReturnToHomeEnabledError(DJIError djiError) {

    }

    @Override
    public void setSmartReturToHomeEnable(Boolean enable, DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError != null) {
                    ToastUtils.setResultToToast("智能回家设置失败:" + djiError.getDescription());
                    swith_go_home.setChecked(!enable);
                }
            }
        });
    }

    @Override
    public void confirmSmartReturnToHomeRequest(DJIError djiError) {

    }

    @Override
    public void getLowBatteryWarningThreshold(Integer integer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bar_lb.setMax(35);
                int slbMax = integer - maxBarDifference - minBarSlb;
                bar_lb.setProgress(integer - minBarSlb);
                bar_slb.setMax(slbMax);
                tv_lb.setText(integer + "%");
            }
        });
    }

    @Override
    public void getLowBatteryWarningThresholdError(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError != null) {
                    ToastUtils.setResultToToast("获取低电量失败:" + djiError.getDescription());
                }
            }
        });
    }

    @Override
    public void setLowBatteryWarningThreshold(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError != null) {
                    ToastUtils.setResultToToast("低电量报警设置失败:" + djiError.getDescription());
                }
            }
        });
    }

    @Override
    public void getSeriousLowBatteryWarningThreshold(Integer integer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bar_slb.setProgress(integer - minBarSlb);
                tv_slb.setText(integer + "%");
            }
        });

    }

    @Override
    public void getSeriousLowBatteryWarningThresholdError(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.setResultToToast("严重低电量报警获取失败:" + djiError.getDescription());
                isNeedSlb = false;
            }
        });
    }

    @Override
    public void setSeriousLowBatteryWarningThreshold(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError == null) {
                    //ToastUtils.setResultToToast("严重低电量报警设置成功");
                    if (isNeedSlb) {
                        isNeedSlb = false;
                        myAircraft.setLowBatteryWarningThreshold(minBarlb + bar_lb.getProgress());
                    }

                } else {
                    ToastUtils.setResultToToast("严重低电量报警设置失败:" + djiError.getDescription());
                }
            }
        });

    }

    @Override
    public void setAggregationStateCallback(AggregationState aggregationState) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (batteryList.size()>1){
                    total_battery.setText(aggregationState.getChargeRemainingInPercent() + "%");
//                    TreeMap<String,Object> treeMap = new TreeMap<>();
//                    treeMap.put("message","remainingInPercent");
//                    treeMap.put("getPercent",aggregationState.getChargeRemainingInPercent());
//                    EventBus.getDefault().postSticky(treeMap);
                    // EventBus.getDefault().postSticky(new CustomMessageEvent("remainingInPercent",aggregationState.getChargeRemainingInPercent()));
                }

            }
        });
    }

}
