package com.tovos.uav.sample.route.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.djilib.dji.component.aircraft.database.CustomIMUData;
import com.tovos.uav.sample.R;
import com.example.commonlib.utils.CustomNumberUtils;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class IMUAdapter extends RecyclerView.Adapter<IMUAdapter.MyTVHolder>{
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<CustomIMUData> mData = new ArrayList<>();
    public IMUAdapter(Context context, List<CustomIMUData> mData) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = mData;

    }

    @Override
    public MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyTVHolder(mLayoutInflater.inflate(R.layout.imu_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyTVHolder holder, final int pos) {

        //holder.error_text.setText(mData.get(pos).getCalibrationProgress());
        holder.imu_name.setText("IMU"+mData.get(pos).getIndex());
        if (mData.get(pos).isMultiSideCalibrationType()){
            holder.selecetd_img.setVisibility(View.VISIBLE);
        }else {
            holder.selecetd_img.setVisibility(View.INVISIBLE);
        }

        holder.state_1_value.setText(CustomNumberUtils.format5(mData.get(pos).getAccelerometerValue())+"");
        holder.state_2_value.setText(CustomNumberUtils.format5(mData.get(pos).getGyroscopeValue())+"");
        holder.state_1_pro.setMax(100);
        holder.state_2_pro.setMax(100);
        holder.state_1_pro.setProgress(Math.round(mData.get(pos).getAccelerometerValue()*10000));
        holder.state_2_pro.setProgress(Math.round(mData.get(pos).getGyroscopeValue()*10000));
    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView imu_name,state_1_value,state_2_value;
        ImageView selecetd_img;
        ProgressBar state_1_pro,state_2_pro;
        MyTVHolder(View itemView) {
            super(itemView);
            imu_name = (TextView) itemView.findViewById(R.id.name);
            state_1_value = (TextView) itemView.findViewById(R.id.state_1_value);
            state_2_value = (TextView) itemView.findViewById(R.id.state_2_value);
            selecetd_img = (ImageView) itemView.findViewById(R.id.img_now);

            state_1_pro = (ProgressBar) itemView.findViewById(R.id.state_1_pro);
            state_2_pro = (ProgressBar) itemView.findViewById(R.id.state_2_pro);

        }
    }

}
