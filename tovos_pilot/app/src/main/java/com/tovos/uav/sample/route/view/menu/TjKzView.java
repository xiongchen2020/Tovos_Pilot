package com.tovos.uav.sample.route.view.menu;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.commonlib.utils.LogUtil;
import com.example.commonlib.utils.ToastUtils;
import com.example.djilib.dji.DJIContext;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.tovos.uav.sample.route.waypoint.CustomWayPoint;

import androidx.annotation.Nullable;

import dji.common.flightcontroller.ConnectionFailSafeBehavior;

public class TjKzView extends LinearLayout {
    public int qf_home_height = 0;
    private SeekBar  xj_sd, fx_sd, qfd_gd,hdsd;
    private TextView  xj_sd_tx,fx_sd_tx, qfd_gd_tx,hdsd_tx;

    private CustomWayPoint customWayPoint;




    public TjKzView(Context context) {
        super(context);
        initViewTjKz(context);
    }

    public TjKzView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViewTjKz(context);
    }

    public TjKzView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViewTjKz(context);
    }

    public TjKzView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViewTjKz(context);
    }

    public void setCustomWayPoint(CustomWayPoint customWayPoint) {
        this.customWayPoint = customWayPoint;
    }

    /**
     * 初始化调节控制设置VIEW
     */
    public void initViewTjKz(Context context) {
        LayoutInflater.from(context).inflate(R.layout.tj_layout, this);
        // tjRadioGroup = (RadioGroup) findViewById(R.id.tj_rbg);

        xj_sd = (SeekBar) findViewById(R.id.xjsd);
        fx_sd = (SeekBar) findViewById(R.id.fx_sd);
        xj_sd_tx = (TextView) findViewById(R.id.xjsd_tx);
        fx_sd_tx = (TextView) findViewById(R.id.fx_sd_tx);
        qfd_gd = (SeekBar) findViewById(R.id.qfd_gd);
        qfd_gd_tx = (TextView) findViewById(R.id.qfd_gd_tx);
        hdsd_tx = (TextView) findViewById(R.id.hdsd_tx);
        hdsd = (SeekBar) findViewById(R.id.hdsd);

        qfd_gd.setMax(100);
        qfd_gd_tx.setText(0 + "M");
        qfd_gd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qfd_gd_tx.setText(progress + "M");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                qf_home_height = seekBar.getProgress();
                customWayPoint.setQfPointHeight(seekBar.getProgress());

            }
        });


        if (DJIContext.getAircraftInstance() == null) {
            qfd_gd.setEnabled(false);
        }

        xj_sd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress()<fx_sd.getProgress()){
                    xj_sd_tx.setText(progress + "M/S");
                }else{
                    xj_sd_tx.setText(fx_sd.getProgress() + "M/S");
                    seekBar.setProgress(fx_sd.getProgress());
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress()<=fx_sd.getProgress()){
                    customWayPoint.setZdSpeed(seekBar.getProgress());
                }else{
                    customWayPoint.setZdSpeed(fx_sd.getProgress());
                    ToastUtils.setResultToToast("起飞点速度不能大于最大巡检速度！");
                }

            }
        });

        fx_sd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fx_sd_tx.setText(progress + "M/S");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                customWayPoint.setMaxSpeed(seekBar.getProgress());

            }
        });
        hdsd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress()<=fx_sd.getProgress()){
                    hdsd_tx.setText(progress+"M/S");
                }else{
                    hdsd_tx.setText(fx_sd.getProgress() + "M/S");
                    seekBar.setProgress(fx_sd.getProgress());
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress()<fx_sd.getProgress()){
                    customWayPoint.setHdSpeed(seekBar.getProgress());
                }else{
                    customWayPoint.setHdSpeed(fx_sd.getProgress());
                    ToastUtils.setResultToToast("巡检速度不能大于最大巡检速度！");
                }

            }
        });
    }

    public void initSpeedView() {
        int speed = 15;
        int maxspeed = 15;
        int hdSpeed = 15;
        if (customWayPoint != null) {
            speed = (int) customWayPoint.getZdSpeed();
            maxspeed = (int) customWayPoint.getMaxSpeed();
            hdSpeed = (int) customWayPoint.getHdSpeed();

        }
        xj_sd.setProgress(speed);
        fx_sd.setProgress(maxspeed);
        hdsd.setProgress(hdSpeed);
        xj_sd_tx.setText(speed + "M/S");
        fx_sd_tx.setText(maxspeed + "M/S");
        hdsd_tx.setText(hdSpeed + "M/S");
        xj_sd.setMax(15);
        fx_sd.setMax(15);
        hdsd.setMax(15);
    }

    public void setStartFlightHeight() {
          LogUtil.d("sudugaodu",customWayPoint.getHomeHeight()+"");
        qf_home_height = Math.round(customWayPoint.getHomeHeight());
        qfd_gd.setMax(Math.round(customWayPoint.getHomeHeight() + 50));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            qfd_gd.setMin(qf_home_height);
        }

        qfd_gd.setProgress(qf_home_height);
        qfd_gd_tx.setText(qf_home_height + "M");
        qfd_gd.setEnabled(true);

    }
}
