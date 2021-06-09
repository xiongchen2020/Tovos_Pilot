package com.tovos.uav.sample.route.view.set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.component.aircraft.MyAircraft;
import com.example.djilib.dji.component.gimbal.GimbalListener;
import com.example.djilib.dji.component.gimbal.MyGimbal;
import com.example.filgthhublibrary.util.ToastUtils;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.listener.ActivityListener;

import java.util.List;

import androidx.annotation.Nullable;
import dji.common.error.DJIError;
import dji.common.gimbal.Axis;
import dji.common.gimbal.CapabilityKey;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.GimbalState;
import dji.common.product.Model;
import dji.sdk.gimbal.Gimbal;


public class GimbalSetView extends LinearLayout implements GimbalListener {
    Context context;
    RadioGroup gimbalIndex;
    MyGimbal myGimbal;
    RelativeLayout gimbal_rl, rlPitchSpeed, rlYaw, rlYawSpeed,rlpitch;
    Switch swithExtension;
    RadioGroup rgMode;
    SeekBar sbPitch, sbPitchSpeed, sbYaw, sbYawSpeed;
    TextView tvPitch, tvPitchSpeed, tvYaw, tvYawSpeed, tvReset, tvFactorySet, tvCalibration;
    RadioButton modeRb1, modeRb2, modeRb3;
    List<Gimbal> gimbals;
    Activity activity;
    private ActivityListener listener;

    Gimbal gimbal;

    public GimbalSetView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public GimbalSetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public GimbalSetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public GimbalSetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initView(context);
    }


    public void getMyGimbal() {

        if (myGimbal == null) {
            myGimbal = new MyGimbal(this);
        }


    }

    public void setActivityListener(ActivityListener listener) {
        this.listener = listener;
    }

    private void initView(Context context) {

        LayoutInflater.from(context).inflate(R.layout.gimbal_set_layout, this);
        activity = (Activity) context;
        getMyGimbal();
        //返回
        gimbal_rl = findViewById(R.id.gimbal_rl);
        gimbalIndex = findViewById(R.id.gimbal_index);
        swithExtension = findViewById(R.id.swith_extension);
        rgMode = findViewById(R.id.rg_mode);
        rlpitch = findViewById(R.id.rl_pitch);
        rlPitchSpeed = findViewById(R.id.rl_pitch_speed);
        rlYaw = findViewById(R.id.rl_yaw);
        rlYawSpeed = findViewById(R.id.rl_yaw_speed);
        sbPitch = findViewById(R.id.sb_pitch);
        tvPitch = findViewById(R.id.tv_pitch);
        sbPitchSpeed = findViewById(R.id.sb_pitch_speed);
        tvPitchSpeed = findViewById(R.id.tv_pitch_speed);
        sbYaw = findViewById(R.id.sb_yaw);
        sbYawSpeed = findViewById(R.id.sb_yaw_speed);
        tvYaw = findViewById(R.id.tv_yaw);
        tvYawSpeed = findViewById(R.id.tv_yaw_speed);
        tvReset = findViewById(R.id.tv_reset);
        tvFactorySet = findViewById(R.id.tv_factory_set);
        tvCalibration = findViewById(R.id.tv_calibration);
        modeRb1 = findViewById(R.id.mode_rb1);
        modeRb2 = findViewById(R.id.mode_rb2);
        modeRb3 = findViewById(R.id.mode_rb3);

        gimbal_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backToMenuView(GimbalSetView.this);
            }
        });


        gimbalIndex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // group.clearCheck();
                group.check(checkedId);
                if (gimbals != null && gimbals.size() > 0 && gimbals.size() > checkedId) {
                    myGimbal.getGimbalInstance(checkedId);
                    getGimbalSet(gimbal);
                }

            }
        });
        swithExtension.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myGimbal.getGimbal() != null) {
                    if (myGimbal.pitchRangeExtensionEnabled) {
                        myGimbal.setPitchExtensionIfPossible(false);
                    } else {
                        myGimbal.setPitchExtensionIfPossible(true);
                    }
                }

            }
        });

        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (myGimbal.getGimbal() != null) {
                    switch (checkedId) {
                        case R.id.mode_rb1:
                            myGimbal.setGimbalMode(GimbalMode.FREE);
                            break;
                        case R.id.mode_rb2:
                            myGimbal.setGimbalMode(GimbalMode.YAW_FOLLOW);
                            break;
                        case R.id.mode_rb3:
                            myGimbal.setGimbalMode(GimbalMode.FPV);
                            break;
                    }
                }

            }
        });
        sbPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myGimbal.getGimbal() != null) {
                    tvPitch.setText(seekBar.getProgress() + "");
                    myGimbal.setControllerSmoothingFactor(Axis.PITCH, seekBar.getProgress());
                }

            }
        });
        sbPitchSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myGimbal.getGimbal() != null) {
                    tvPitchSpeed.setText(seekBar.getProgress() + "%");
                    myGimbal.setControllerMaxSpeed(Axis.PITCH, seekBar.getProgress());
                }
            }
        });

        sbYaw.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myGimbal.getGimbal() != null) {
                    tvYaw.setText(seekBar.getProgress() + "");
                    myGimbal.setControllerSmoothingFactor(Axis.YAW, seekBar.getProgress());
                }
            }
        });
        sbYawSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myGimbal.getGimbal() != null) {
                    tvYawSpeed.setText(seekBar.getProgress() + "%");
                    myGimbal.setControllerMaxSpeed(Axis.YAW, seekBar.getProgress());
                }
            }
        });

        tvReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myGimbal.getGimbal() != null) {
                    myGimbal.resetGimbal();
                }
            }
        });
        tvFactorySet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myGimbal.getGimbal() != null) {
                    myGimbal.restoreFactorySettings();
                    getGimbalSet(gimbal);
                }
            }
        });

        tvCalibration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myGimbal.getGimbal() != null) {
                    myGimbal.startCalibrationGimbal();
                }
            }
        });
    }

    public void setView() {

    }

    public void connData() {
        gimbals = myGimbal.getLimits();
        if (gimbals != null && gimbals.size() > 0) {

            swithExtension.setEnabled(true);
            // rgMode.setEnabled(true);
            modeRb1.setEnabled(true);
            modeRb2.setEnabled(true);
            modeRb3.setEnabled(true);
            sbPitch.setEnabled(true);
            sbYaw.setEnabled(true);
            sbYawSpeed.setEnabled(true);
            sbPitchSpeed.setEnabled(true);
            tvReset.setEnabled(true);
            tvFactorySet.setEnabled(true);
            tvCalibration.setEnabled(true);
            rlPitchSpeed.setVisibility(GONE);
            rlYaw.setVisibility(GONE);
            rlYawSpeed.setVisibility(GONE);
            Log.i("zsj", "云台数量：" + gimbals.size() + "");
            gimbal = myGimbal.getGimbalInstance(0);
            gimbalIndex.removeAllViews();
            for (int i = 0; i < gimbals.size(); i++) {
                RadioButton rb = new RadioButton(context);
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                int index = i + 1;
                rb.setId(i);
                rb.setText("云台" + index);
                rb.setTextColor(Color.parseColor("#FFFFFF"));
                rb.setTextSize(11);
                rb.setGravity(Gravity.CENTER);
                rb.setLayoutParams(params);

                if (i == 0) {
                    rb.setChecked(true);
                }
                gimbalIndex.addView(rb);
            }


            getGimbalSet(gimbal);
        }


    }

    public void disConnData() {

        swithExtension.setEnabled(false);
        //rgMode.setEnabled(false);
        modeRb1.setEnabled(false);
        modeRb2.setEnabled(false);
        modeRb3.setEnabled(false);
        sbPitch.setEnabled(false);
        sbYaw.setEnabled(false);
        sbYawSpeed.setEnabled(false);
        sbPitchSpeed.setEnabled(false);
        tvReset.setEnabled(false);
        tvFactorySet.setEnabled(false);
        tvCalibration.setEnabled(false);
        gimbalIndex.removeAllViews();
    }

    private void getGimbalSet(Gimbal gimbal) {
        myGimbal.getPitchExtensionIfPossible();
        GimbalMode gimbalMode = myGimbal.getGimbalMode();
        Log.i("zsj", "云台模式：" + gimbalMode);
        if (gimbalMode == GimbalMode.FPV) {
            modeRb3.setChecked(true);
        } else if (gimbalMode == GimbalMode.FREE) {
            modeRb2.setChecked(true);
        } else if (gimbalMode == GimbalMode.YAW_FOLLOW) {
            modeRb1.setChecked(true);
        }

        //获取俯仰


        if (myGimbal.isFeatureSupported(CapabilityKey.PITCH_CONTROLLER_SMOOTHING_FACTOR)){
            rlpitch.setVisibility(VISIBLE);
            myGimbal.getControllerSmoothingFactor(Axis.PITCH);
        }
        if (myGimbal.isFeatureSupported(CapabilityKey.PITCH_CONTROLLER_MAX_SPEED)){
            rlPitchSpeed.setVisibility(VISIBLE);
            myGimbal.getControllerMaxSpeed(Axis.PITCH);
        }

        if (myGimbal.isFeatureSupported(CapabilityKey.YAW_CONTROLLER_SMOOTHING_FACTOR)){
            rlYaw.setVisibility(VISIBLE);
            myGimbal.getControllerSmoothingFactor(Axis.YAW);
        }


        if (myGimbal.isFeatureSupported(CapabilityKey.YAW_CONTROLLER_MAX_SPEED)){
            rlYawSpeed.setVisibility(VISIBLE);
            myGimbal.getControllerMaxSpeed(Axis.YAW);
        }








    }


    @Override
    public void getPitchRangeExtensionEnabled(boolean aBoolean) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swithExtension.setChecked(aBoolean);
            }
        });
    }

    @Override
    public void getPitchRangeExtensionEnabledFailure(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError != null) {
                    swithExtension.setChecked(!swithExtension.isChecked());
                }
            }
        });
    }

    @Override
    public void setPitchRangeExtensionEnabledResult(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError != null) {
                    swithExtension.setChecked(!swithExtension.isChecked());
                }
            }
        });
    }

    @Override
    public void updateState(GimbalState gimbalState) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GimbalMode gimbalMode = gimbalState.getMode();
                Log.i("zsj", "云台模式：" + gimbalState.getMode());
                if (gimbalMode == GimbalMode.FPV) {
                    modeRb3.setChecked(true);
                } else if (gimbalMode == GimbalMode.FREE) {
                    modeRb2.setChecked(true);
                } else if (gimbalMode == GimbalMode.YAW_FOLLOW) {
                    modeRb1.setChecked(true);
                }
            }
        });
    }

    @Override
    public void setGimbalMode(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError != null) {
                    ToastUtils.setResultToToast("设置云台模式失败");
                    Log.i("zsj", "错误信息：" + djiError.getDescription());
                }

            }
        });
    }

    @Override
    public void getControllerSmoothingFactor(Axis axis, Integer integer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (axis == Axis.PITCH) {//俯仰
                    sbPitch.setProgress(integer);
                    tvPitch.setText(integer + "");
                } else if (axis == Axis.YAW) {//偏航
                    sbYaw.setProgress(integer);
                    tvYaw.setText(integer + "");


                }
            }
        });
    }

    @Override
    public void getControllerSmoothingFactorFailure(Axis axis, DJIError djiError) {
        Log.i("zsj", "错误：" + axis + "/" + djiError.getDescription());

    }

    @Override
    public void setControllerSmoothingFactor(Axis axis, DJIError djiError) {

    }

    @Override
    public void getControllerMaxSpeed(Axis axis, Integer integer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (axis == Axis.PITCH) {//俯仰
                    sbPitchSpeed.setProgress(integer);
                    tvPitchSpeed.setText(integer + "%");
                } else if (axis == Axis.YAW) {//偏航
                    sbYawSpeed.setProgress(integer);
                    tvYawSpeed.setText(integer + "%");
                }
            }
        });


    }

    @Override
    public void getControllerMaxSpeedFailure(Axis axis, DJIError djiError) {
        Log.i("zsj", "错误：" + axis + "/" + djiError.getDescription());
    }

    @Override
    public void setControllerMaxSpeed(Axis axis, DJIError djiError) {

    }
}
