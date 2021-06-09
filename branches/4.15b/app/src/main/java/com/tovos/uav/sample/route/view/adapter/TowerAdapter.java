package com.tovos.uav.sample.route.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlib.utils.LogUtil;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.sql.bean.DbTower;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class TowerAdapter extends RecyclerView.Adapter<TowerAdapter.MyTVHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<DbTower> mData = new ArrayList<>();
    private boolean checkModel = false;
    private boolean is_all_selected = false;

    public TowerAdapter(Context context, List<DbTower> mData) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = mData;
    }

    @Override
    public TowerAdapter.MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TowerAdapter.MyTVHolder(mLayoutInflater.inflate(R.layout.tower_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final TowerAdapter.MyTVHolder holder, final int pos) {

        holder.nums.setText((pos + 1) + "");

        holder.id.setText(mData.get(pos).getName());
        holder.checkBox.setChecked(mData.get(pos).isIschecked());
//        holder.type.setText("直线塔");

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("111111111", "点击item:");
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                } else {
                    holder.checkBox.setChecked(true);
                }
                mData.get(pos).setIschecked(holder.checkBox.isChecked());
            }
        });
//        holder.type.setText("直线塔");

//        holder.ll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtil.e("111111111", "点击item:");
//
//                if (mData.get(pos).isIschecked()) {
//                    holder.checkBox.setChecked(false);
//                    mData.get(pos).setIschecked(false);
//                    map.put(pos,false);
//                } else {
//                    holder.checkBox.setChecked(true);
//                    mData.get(pos).setIschecked(true);
//                    map.put(pos,true);
//                }
//
//
//            }
//        });
//        if(map!=null&&map.get(pos)!=null&&map.get(pos)){
//            holder.checkBox.setChecked(true);
//        }else {
//            holder.checkBox.setChecked(false);
//        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView nums, type, id;
        RelativeLayout ll;
        CheckBox checkBox;

        MyTVHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.tower_id);
            nums = (TextView) itemView.findViewById(R.id.tower_nums);
//            type = (TextView) itemView.findViewById(R.id.tower_type);
            ll = (RelativeLayout) itemView.findViewById(R.id.rl);
            checkBox = (CheckBox) itemView.findViewById(R.id.checked);
        }
    }

}
