package com.tovos.uav.sample.route.view.djiview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.commonlib.utils.ToastUtils;
import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.component.camera.CustomCamera;
import com.example.djilib.dji.component.camera.CustomCameraListener;
import com.example.djilib.dji.component.camera.lens.CustomLens;
import com.example.djilib.dji.component.camera.lens.LensListener;
import com.tovos.uav.sample.R;

import java.util.ArrayList;
import java.util.List;

import dji.common.camera.CameraVideoStreamSource;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.internal.logics.CommonUtil;
import dji.keysdk.CameraKey;
import dji.keysdk.DJIKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.ActionCallback;
import dji.sdk.camera.Lens;

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

    private void setH20Lens(SettingsDefinitions.LensType lensType,CameraVideoStreamSource viedoStreamSource){
        Log.e(this.getClass().getSimpleName(),"setH20Lens  相机镜头名称:"+lensType);
        int lensindex = -1;
        lensindex =  CommonUtil.getLensIndex(0, lensType);
        customLens = customCamera.getLensByIndex(lensindex,CameraView.this);
        customCamera.setCameraViedoStreamSource(viedoStreamSource);
    }

    private void setYu2Lens(String name){
        Log.e(this.getClass().getSimpleName(),"setYu2Lens  相机镜头名称初始化");
        for (int i=0;i<lensList.size();i++){
            Log.e(this.getClass().getSimpleName(),"相机镜头name:"+name);
            Log.e(this.getClass().getSimpleName(),"相机镜头customLens["+i+"]:"+lensList.get(i).getDisplayName());
            if (lensList.get(i).getDisplayName().equals(name)){

                customLens = customCamera.getLensByIndex(lensList.get(i).getIndex(),CameraView.this);
                Log.e(this.getClass().getSimpleName(),"setYu2Lens  customLens："+customLens.getDisplayName());
                return;
            }
        }
    }

    public void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.camera_view_layout, this);
        radioGroup =findViewById(R.id.camera_group);
        radioGroup2 =findViewById(R.id.group2);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if (customCamera == null)
                    return;

                radioGroup2.setVisibility(GONE);
                switch (checkedId){
                    case R.id.rb1:
                        setH20Lens(SettingsDefinitions.LensType.ZOOM,CameraVideoStreamSource.ZOOM);
                        break;
                    case R.id.rb2:
                        setH20Lens(SettingsDefinitions.LensType.WIDE,CameraVideoStreamSource.WIDE);
                        break;
                    case R.id.rb3:
                        setH20Lens(SettingsDefinitions.LensType.INFRARED_THERMAL,CameraVideoStreamSource.INFRARED_THERMAL);
                        radioGroup2.setVisibility(VISIBLE);
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
                        break;
                    case R.id.wd_rb2:
                        customLens.setDisplayMode(SettingsDefinitions.DisplayMode.PIP);
                        break;
                    case R.id.wd_rb3:
                        customLens.setDisplayMode(SettingsDefinitions.DisplayMode.MSX);
                        break;

                    case R.id.wd_rb4:
                        customLens.setDisplayMode(SettingsDefinitions.DisplayMode.VISUAL_ONLY);
                        break;
                }
            }
        });
    }

    private List<Lens> lensList= new ArrayList<>();

    public void setCustomCamera(CustomCamera customCamera){

        radioGroup2.setVisibility(GONE);
        this.customCamera = customCamera;
        if (customCamera != null){
            customCamera.getLenses();
            lensList = customCamera.getLensList();
            customCamera.setCustomCameraListener(this);
            Log.e(this.getClass().getSimpleName(),"相机镜头数:"+customCamera.getLensList().size());
            Log.e(this.getClass().getSimpleName(),"相机镜头名称:"+customCamera.getDisplayName());
            if (customCamera.getLensList().size()>0){

                if(customCamera.getDisplayName().equals("Mavic 2 Enterprise Advanced Camera")){
                    radioGroup.setVisibility(GONE);
                    findViewById(R.id.wd_rb3).setVisibility(GONE);
                    findViewById(R.id.wd_rb4).setVisibility(VISIBLE);
                    findViewById(R.id.rb2).setVisibility(GONE);
                    setYu2Lens("Mavic 2 Enterprise Advanced Camera Infrared Thermal Lens");
                    radioGroup2.setVisibility(VISIBLE);
                    customLens.getDisplayMode();

                }else {
                    radioGroup.setVisibility(VISIBLE);
                    findViewById(R.id.rb2).setVisibility(VISIBLE);
                    findViewById(R.id.wd_rb3).setVisibility(VISIBLE);
                    findViewById(R.id.wd_rb4).setVisibility(GONE);
                    customCamera.getCameraViedoStreamSource();
                }

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
            case VISUAL_ONLY:
                ((RadioButton)findViewById(R.id.wd_rb4)).setChecked(true);
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

    @Override
    public void getCameraModel(SettingsDefinitions.DisplayMode displayMode) {
        Log.e(this.getClass().getSimpleName(),"当前相机镜头模式"+displayMode);
        int lensindex = -1;

        switch (displayMode){
            case THERMAL_ONLY:
                ((RadioButton)findViewById(R.id.wd_rb1)).setChecked(true);
                break;
            case PIP:

                ((RadioButton)findViewById(R.id.wd_rb2)).setChecked(true);
                break;
            case MSX:
                ((RadioButton)findViewById(R.id.wd_rb3)).setChecked(true);
                break;
            case VISUAL_ONLY:
                ((RadioButton)findViewById(R.id.wd_rb4)).setChecked(true);
                break;
        }
    }
}
