package com.tovos.uav.sample.route.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.view.listener.OnTaskItemClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dji.common.flightcontroller.rtk.RTKBaseStationInformation;

public class BaseStationAdapter extends RecyclerView.Adapter<BaseStationAdapter.MyTVHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<RTKBaseStationInformation> mData = new ArrayList<>();
    private boolean checkModel = false;
    private boolean is_all_selected = false;
    private OnTaskItemClickListener onRecyclerItemClickListener;
    public BaseStationAdapter(Context context, List<RTKBaseStationInformation> mData, OnTaskItemClickListener onRecyclerItemClickListener) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = mData;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @Override
    public BaseStationAdapter.MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseStationAdapter.MyTVHolder(mLayoutInflater.inflate(R.layout.base_station_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final BaseStationAdapter.MyTVHolder holder, final int pos) {
       holder.name.setText(mData.get(pos).getBaseStationName());
       holder.level.setText(mData.get(pos).getSignalLevel()+"");
       holder.ll_base.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
             onRecyclerItemClickListener.onTaskItemClick(pos);
           }
       });


    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView name, level;
        LinearLayout ll_base;
        MyTVHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            level = (TextView) itemView.findViewById(R.id.level);
            ll_base = (LinearLayout) itemView.findViewById(R.id.ll_base);
        }

    }

}
