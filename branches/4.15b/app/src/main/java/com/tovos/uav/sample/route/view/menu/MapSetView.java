package com.tovos.uav.sample.route.view.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.dji.mapkit.core.maps.DJIMap;
import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.map.MyMapManager;
import com.tovos.uav.sample.R;
import com.example.commonlib.utils.SPUtils;

import androidx.annotation.Nullable;


public class MapSetView extends LinearLayout {
    private Context context;

    public MyMapManager myMapManager;

    public RadioGroup mapType;

    public RadioButton mapTypeNormal, mapTypeSatellite;
    public Switch swithShowPath,swithShowFlyzone;
    public MapSetView(Context context) {
        this(context,null);
    }

    public MapSetView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public MapSetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context,attrs,defStyleAttr,0);
    }

    public MapSetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.map_set_layout, this);
        initView();
    }

    public void setMyMapManager(MyMapManager myMapManager) {
        this.myMapManager = myMapManager;
    }

    private void initView(){
        mapType = findViewById(R.id.map_type);
        mapTypeNormal = findViewById(R.id.map_type_normal);
        mapTypeSatellite = findViewById(R.id.map_type_satellite);
        swithShowPath = findViewById(R.id.swith_show_path);
        swithShowFlyzone = findViewById(R.id.swith_show_flyzone);



    }

    public void setView(){
        String type = (String) SPUtils.get("map_type","NORMAL");
        if ("SATELLITE".equals(type)){
            myMapManager.changeMapType(DJIMap.MapType.HYBRID);
            mapTypeSatellite.setChecked(true);
        }else{
            myMapManager.changeMapType(DJIMap.MapType.NORMAL);
            mapTypeNormal.setChecked(true);
        }



        mapType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                DJIMap.MapType  mapType= DJIMap.MapType.NORMAL;
                switch (checkedId) {
                    case R.id.map_type_normal:
                        mapType = DJIMap.MapType.NORMAL;
                        SPUtils.put("map_type","NORMAL");
                        break;
                    case R.id.map_type_satellite:
                        mapType = DJIMap.MapType.HYBRID;
                        SPUtils.put("map_type","SATELLITE");
                        break;

                }
                myMapManager.changeMapType(mapType);

            }
        });
        swithShowPath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myMapManager.setFlightPathVisible(isChecked);
            }
        });
        swithShowFlyzone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put("show_flyzone",isChecked);
                if (isChecked){
                    myMapManager.showFlyZones();
                }else{
                    myMapManager.hideAllFlyZones();
                }
            }
        });
        setMap();
    }

    public  void setMap(){
        swithShowPath.setEnabled(false);
        swithShowFlyzone.setEnabled(false);
        if (DJIContext.getAircraftInstance() !=null){

            if (DJIContext.getAircraftInstance().getFlightController() == null)
                return;

            swithShowPath.setEnabled(true);
            swithShowFlyzone.setEnabled(true);
            swithShowPath.setChecked(myMapManager.getFlightPathVisible());
            boolean isShowFlyZone = (Boolean) SPUtils.get("show_flyzone",false);
            swithShowFlyzone.setChecked(isShowFlyZone);
            if (isShowFlyZone){
                myMapManager.showFlyZones();
            }else{
                myMapManager.hideAllFlyZones();
            }
            myMapManager.setFlyZoneVisable(isShowFlyZone);
        }
    }
}
