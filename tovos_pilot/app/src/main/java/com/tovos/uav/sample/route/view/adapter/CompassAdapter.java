package com.tovos.uav.sample.route.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.djilib.dji.component.aircraft.database.CustomCompassData;
import com.tovos.uav.sample.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CompassAdapter extends RecyclerView.Adapter<CompassAdapter.MyTVHolder>{
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<CustomCompassData> mData = new ArrayList<>();
    public CompassAdapter(Context context, List<CustomCompassData> mData) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = mData;
    }

    @Override
    public MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyTVHolder(mLayoutInflater.inflate(R.layout.compass_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyTVHolder holder, final int pos) {

        //holder.error_text.setText(mData.get(pos).getCalibrationProgress());
        holder.imu_name.setText("指南针"+mData.get(pos).getIndex());
        holder.state_1_value.setText(mData.get(pos).getSensorValue()+"");
        holder.state_1_pro.setMax(1000);
        holder.state_1_pro.setProgress(Math.round(mData.get(pos).getSensorValue()));
    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView imu_name,state_1_value;
        ImageView selecetd_img;
        ProgressBar state_1_pro;
        MyTVHolder(View itemView) {
            super(itemView);
            imu_name = (TextView) itemView.findViewById(R.id.name);
            state_1_value = (TextView) itemView.findViewById(R.id.state_1_value);
            selecetd_img = (ImageView) itemView.findViewById(R.id.img_now);

            state_1_pro = (ProgressBar) itemView.findViewById(R.id.state_1_pro);

        }
    }

}
