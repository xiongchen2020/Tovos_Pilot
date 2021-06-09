package com.tovos.uav.sample.route.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.commonlib.utils.LogUtil;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;
import com.tovos.uav.sample.route.view.listener.OnRouteItemClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyTVHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<DbUAVRoute> mData = new ArrayList<>();
    private OnRouteItemClickListener onRecyclerItemClickListener;
    private boolean checkModel = false;
    private boolean is_all_selected = false;

    public RouteAdapter(Context context, List<DbUAVRoute> mData, OnRouteItemClickListener onRecyclerItemClickListener) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = mData;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public int getUavIdByPosition(int position){
        return (int)mData.get(position).getId();
    }

    public DbUAVRoute getUavRouteByPosition(int position){
        return mData.get(position);
    }

    @Override
    public RouteAdapter.MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RouteAdapter.MyTVHolder(mLayoutInflater.inflate(R.layout.route_detail_new_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final RouteAdapter.MyTVHolder holder, final int pos) {

        holder.checkBox.setChecked(mData.get(pos).isSelected());
        holder.name.setText(mData.get(pos).getName());

       // holder.type.setText("通道巡检");

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("111111111", "点击item:");
                if (checkModel) {
                    if (holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    } else {
                        holder.checkBox.setChecked(true);
                    }
                    mData.get(pos).setSelected(holder.checkBox.isChecked());
                } else {
                    onRecyclerItemClickListener.onTaskItemClick(pos);
                }
            }
        });

        holder.ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LogUtil.e("111111111", "长按事件:");
                onRecyclerItemClickListener.onTaskItemLongClick(pos);
                return true;
            }
        });
    }

    public void setCheckModel(boolean checkModel) {
        this.checkModel = checkModel;
    }

    public void setSelectedAllFalse() {
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void setSelectedAll() {
        for (int i = 0; i < mData.size(); i++) {
            if (i == 0) {
                is_all_selected = mData.get(i).isSelected();
            } else {
                if (is_all_selected) {
                    is_all_selected = mData.get(i).isSelected();
                }
            }
        }

        if (is_all_selected) {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).setSelected(false);
            }
        } else {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).setSelected(true);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView type, name;
        LinearLayout ll;
        CheckBox checkBox;

        MyTVHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            ll = (LinearLayout) itemView.findViewById(R.id.ll);
            checkBox = (CheckBox) itemView.findViewById(R.id.checked);
          //  type = (TextView) itemView.findViewById(R.id.route_task_type);
        }
    }

}
