package com.tovos.uav.sample.route.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djilib.dji.component.battery.bean.BatteryInfo;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.listener.BatterySetListener;
import com.example.commonlib.utils.CustomNumberUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dji.common.battery.BatteryState;
import dji.common.battery.ConnectionState;
import dji.keysdk.BatteryKey;
import dji.keysdk.DJIKey;
import dji.keysdk.KeyManager;
import dji.sdk.battery.Battery;

public class BatteryInfoAdapter extends RecyclerView.Adapter<BatteryInfoAdapter.MyTVHolder>{
    private LayoutInflater mLayoutInflater;
    private Activity mContext;
    private List<Battery> mData = new ArrayList<>();
    BatterySetListener listener;
    public BatteryInfoAdapter(Activity context, List<Battery> mData, BatterySetListener listener) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = mData;
        this.listener = listener;
    }

    @Override
    public MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyTVHolder(mLayoutInflater.inflate(R.layout.item_battery, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyTVHolder holder, final int pos) {

        //holder.error_text.setText(mData.get(pos).getCalibrationProgress());
        int name = pos+1;

        mData.get(pos).setStateCallback(new BatteryState.Callback() {
            @Override
            public void onUpdate(BatteryState batteryState) {


                BatteryInfo batteryInfo;
                if (KeyManager.getInstance() == null)
                    return;

                DJIKey key = BatteryKey.create(BatteryKey.CONNECTION_STATE);
                Object object = KeyManager.getInstance().getValue(key);
                String status = "";
                if (object instanceof ConnectionState) {
                    ConnectionState connectionState = (ConnectionState) object;
                    if (connectionState == ConnectionState.NORMAL) {
                        status = "正常";
                    } else if (connectionState == ConnectionState.EXCEPTION) {
                        status = "异常";
                    } else if (connectionState == ConnectionState.INVALID) {
                        status = "无效";
                    } else {
                        status = "未知";
                    }

                }
                batteryInfo = new BatteryInfo();
                batteryInfo.setQuantity(batteryState.getChargeRemainingInPercent());
                batteryInfo.setTemperature(batteryState.getTemperature());
                batteryInfo.setVoltage(batteryState.getVoltage());
                batteryInfo.setNum(batteryState.getNumberOfDischarges());
                batteryInfo.setStatus(status);
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        listener.setStateCallbackView(batteryState);
                        holder.tv_name.setText("电池 " +name);
                        holder.tv_status.setText(""+batteryInfo.getStatus());
                        holder.tv_quantity.setText(""+batteryInfo.getQuantity()+"%");
                        holder.tv_temperature.setText(""+ CustomNumberUtils.format6(batteryInfo.getTemperature())+"℃");
                        holder.tv_voltage.setText(""+ CustomNumberUtils.format6(batteryInfo.getVoltage()/1000.f)+"V");
                        holder.tv_num.setText(""+batteryInfo.getNum());

                    }
                });
            }
        });


    }


    private void updateData(MyTVHolder holder, BatteryInfo batteryInfo){

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_status,tv_quantity,tv_temperature,tv_voltage,tv_num;

        MyTVHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            tv_quantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            tv_temperature = (TextView) itemView.findViewById(R.id.tv_temperature);
            tv_voltage = (TextView) itemView.findViewById(R.id.tv_voltage);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);

        }
    }

}
