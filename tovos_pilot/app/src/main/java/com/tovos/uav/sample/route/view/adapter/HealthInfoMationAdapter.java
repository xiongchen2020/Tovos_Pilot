package com.tovos.uav.sample.route.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.djilib.dji.healthinfomation.HealthInfo;
import com.tovos.uav.sample.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class HealthInfoMationAdapter extends RecyclerView.Adapter<HealthInfoMationAdapter.MyTVHolder>{
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<HealthInfo> mData = new ArrayList<>();
    public HealthInfoMationAdapter(Context context, List<HealthInfo> mData) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = mData;
    }

    @Override
    public MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyTVHolder(mLayoutInflater.inflate(R.layout.error_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyTVHolder holder, final int pos) {


        holder.error_text.setText(mData.get(pos).getTipCn());

    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView error_text;

        CheckBox checkBox;
        MyTVHolder(View itemView) {
            super(itemView);
            error_text = (TextView) itemView.findViewById(R.id.error_text);
        }
    }

}
