package com.tovos.uav.sample.route.view.djiview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.djilib.dji.component.camera.CustomCamera;
import com.example.djilib.dji.component.camera.CustomCameraListener;
import com.example.djilib.dji.component.camera.lens.CustomLens;
import com.example.djilib.dji.component.camera.lens.LensListener;
import com.tovos.uav.sample.R;
import dji.common.camera.CameraVideoStreamSource;
import dji.common.camera.SettingsDefinitions;
import dji.internal.logics.CommonUtil;

public class CameraView extends RelativeLayout implements LensListener, CustomCameraListener {

    CustomCamera customCamera ;
    RadioGroup radioGroup,radioGroup2;
    CustomLens customLens;
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.camera_view_layout, this);
        radioGroup =findViewById(R.id.camera_group);
        radioGroup2 =findViewById(R.id.group2);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int lensindex = -1;
                if (customCamera == null)
                    return;

                radioGroup2.setVisibility(GONE);
                switch (checkedId){
                    case R.id.rb1:
                        lensindex =  CommonUtil.getLensIndex(0, SettingsDefinitions.LensType.ZOOM);
                        customLens = customCamera.getLensByIndex(lensindex,CameraView.this);
                        customCamera.setCameraViedoStreamSource(CameraVideoStreamSource.ZOOM);
                        break;
                    case R.id.rb2:

                        lensindex =  CommonUtil.getLensIndex(0, SettingsDefinitions.LensType.WIDE);
                        customLens = customCamera.getLensByIndex(lensindex,CameraView.this);
                        customCamera.setCameraViedoStreamSource(CameraVideoStreamSource.WIDE);
                        break;
                    case R.id.rb3:
                        lensindex =  CommonUtil.getLensIndex(0, SettingsDefinitions.LensType.INFRARED_THERMAL);
                        customLens = customCamera.getLensByIndex(lensindex,CameraView.this);
                        Log.e(this.getClass().getSimpleName(),"镜头:");
                        radioGroup2.setVisibility(VISIBLE);
                        customCamera.setCameraViedoStreamSource(CameraVideoStreamSource.INFRARED_THERMAL);
                        customLens.getDisplayMode();
                        break;
                }

            }
        });

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (customLens == null)
                    return;

                switch (checkedId){
                    case R.id.wd_rb1:
                        customLens.setDisplayMode(SettingsDefinitions.DisplayMode.THERMAL_ONLY);
                        customCamera.setCameraViedoStreamSource(CameraVideoStreamSource.INFRARED_THERMAL);
                        break;
                    case R.id.wd_rb2:
                        customLens.setDisplayMode(SettingsDefinitions.DisplayMode.PIP);
                        customCamera.setCameraViedoStreamSource(CameraVideoStreamSource.INFRARED_THERMAL);
                        break;
                    case R.id.wd_rb3:
                        customLens.setDisplayMode(SettingsDefinitions.DisplayMode.MSX);
                        customCamera.setCameraViedoStreamSource(CameraVideoStreamSource.INFRARED_THERMAL);
                        break;
                }
            }
        });
    }

    public void setCustomCamera(CustomCamera customCamera){
        radioGroup2.setVisibility(GONE);
        this.customCamera = customCamera;
        if (customCamera != null){
            customCamera.getLenses();
            customCamera.setCustomCameraListener(this);
            Log.e(this.getClass().getSimpleName(),"相机镜头数:"+customCamera.getLensList().size());
            if (customCamera.getLensList().size()>0){
                radioGroup.setVisibility(VISIBLE);
                customCamera.getCameraViedoStreamSource();
            }else {
                radioGroup.setVisibility(GONE);
                radioGroup2.setVisibility(GONE);

            }
        }else {
            radioGroup.setVisibility(GONE);
            radioGroup2.setVisibility(GONE);
        }

    }

    @Override
    public void setThermal(SettingsDefinitions.DisplayMode var) {

        switch (var){
            case THERMAL_ONLY:
                ((RadioButton)findViewById(R.id.wd_rb1)).setChecked(true);
                break;
            case PIP:
                ((RadioButton)findViewById(R.id.wd_rb2)).setChecked(true);
                break;
            case MSX:
                ((RadioButton)findViewById(R.id.wd_rb3)).setChecked(true);
                break;
        }
    }

    @Override
    public void getCameraViedoStreamSource(CameraVideoStreamSource cameraVideoStreamSource) {
        Log.e(this.getClass().getSimpleName(),"当前相机镜头源"+customCamera.getVideoStreamSource());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraVideoStreamSource!=null){
                    int lensindex = -1;
                    switch (cameraVideoStreamSource){
                        case WIDE:
                            lensindex =  CommonUtil.getLensIndex(0, SettingsDefinitions.LensType.WIDE);
                            customLens = customCamera.getLensByIndex(lensindex,CameraView.this);
                            ((RadioButton)findViewById(R.id.rb2)).setChecked(true);
                            break;
                        case ZOOM:
                            lensindex =  CommonUtil.getLensIndex(0, SettingsDefinitions.LensType.ZOOM);
                            customLens = customCamera.getLensByIndex(lensindex,CameraView.this);
                            ((RadioButton)findViewById(R.id.rb1)).setChecked(true);
                            break;
                        case INFRARED_THERMAL:
                            lensindex =  CommonUtil.getLensIndex(0, SettingsDefinitions.LensType.INFRARED_THERMAL);
                            customLens = customCamera.getLensByIndex(lensindex,CameraView.this);
                            ((RadioButton)findViewById(R.id.rb3)).setChecked(true);
                            radioGroup2.setVisibility(VISIBLE);
                            customLens.getDisplayMode();
                            break;
                        case DEFAULT:
                            break;

                    }
                }
            }
        });
    }
}
