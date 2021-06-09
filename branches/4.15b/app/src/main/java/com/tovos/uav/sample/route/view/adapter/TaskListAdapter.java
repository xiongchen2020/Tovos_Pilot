package com.tovos.uav.sample.route.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlib.utils.CustomNumberUtils;
import com.example.commonlib.utils.CustomTimeUtils;
import com.example.commonlib.utils.LogUtil;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.TaskBean;
import com.tovos.uav.sample.route.view.listener.OnTaskItemClickListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.MyTVHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<TaskBean> mData = new ArrayList<>();
    private OnTaskItemClickListener onRecyclerItemClickListener;
    private boolean checkModel = false;
    private boolean is_all_selected = false;

    public TaskListAdapter(Context context, List<TaskBean> mData, OnTaskItemClickListener onRecyclerItemClickListener) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = mData;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @Override
    public TaskListAdapter.MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskListAdapter.MyTVHolder(mLayoutInflater.inflate(R.layout.flight_detail_new_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final TaskListAdapter.MyTVHolder holder, final int pos) {


        holder.checkBox.setChecked(mData.get(pos).isIschecked());
        holder.name.setText(mData.get(pos).getName());
        holder.index.setText(""+(pos+1));
        String formatType = "yyyy-MM-dd HH:mm:ss";
        LogUtil.e("111111111","转化前开始时间:"+mData.get(pos).getStatrtime());
        LogUtil.e("111111111","转date:"+ CustomTimeUtils.longToDate(mData.get(pos).getStatrtime(),formatType));
        holder.lc.setText("里程:"+ CustomNumberUtils.format4(mData.get(pos).getMileage()));
        try {
            holder.start.setText("开始时间:"+CustomTimeUtils.longToString(mData.get(pos).getStatrtime(),formatType));
            holder.end.setText("结束时间:"+CustomTimeUtils.longToString(mData.get(pos).getEndtime(),formatType));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("111111111","点击item:");
                if (checkModel){
                    if (holder.checkBox.isChecked()){
                        holder.checkBox.setChecked(false);
                    }else {
                        holder.checkBox.setChecked(true);
                    }
                    mData.get(pos).setIschecked(holder.checkBox.isChecked());
                }else {
                    onRecyclerItemClickListener.onTaskItemClick(pos);
                }
            }
        });

        holder.ll.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                LogUtil.e("111111111","长按事件:");
                onRecyclerItemClickListener.onTaskItemLongClick(pos);
                return true;
            }
        });

        holder.exp_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.ll_detail.getVisibility() == View.VISIBLE){
                    holder.ll_detail.setVisibility(View.GONE);
                }else {
                    holder.ll_detail.setVisibility(View.VISIBLE);
                }
                if(holder.ll_detail.getVisibility() == View.VISIBLE){
                    holder.im.setSelected(true);
                }else {
                    holder.im.setSelected(false);
                }
            }
        });

    }

    public void setCheckModel(boolean checkModel){
        this.checkModel = checkModel;
    }

    public void setSelectedAllFalse(){
        for (int i=0;i<mData.size();i++){
            mData.get(i).setIschecked(false);
        }
        notifyDataSetChanged();
    }

    public void setSelectedAll(){
        for (int i=0;i<mData.size();i++){
            if (i == 0){
                is_all_selected =  mData.get(i).isIschecked();
            }else {
                if (is_all_selected){
                    is_all_selected =  mData.get(i).isIschecked();
                }
            }
        }

        if (is_all_selected){
            for (int i=0;i<mData.size();i++){
                mData.get(i).setIschecked(false);
            }
        }else {
            for (int i=0;i<mData.size();i++){
                mData.get(i).setIschecked(true);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView index,name,start,end,lc;
        LinearLayout ll,ll_detail;
        RelativeLayout exp_rl;
        ImageView im;
        CheckBox checkBox;
        MyTVHolder(View itemView) {
            super(itemView);
            im = (ImageView) itemView.findViewById(R.id.expendable);
            index = (TextView) itemView.findViewById(R.id.index);
            name = (TextView) itemView.findViewById(R.id.name);
            start = (TextView) itemView.findViewById(R.id.start_time);
            lc = (TextView)itemView.findViewById(R.id.lc);
            end = (TextView) itemView.findViewById(R.id.end_time);
            ll = (LinearLayout)itemView.findViewById(R.id.ll);
            ll_detail = (LinearLayout)itemView.findViewById(R.id.ll_detail);
            checkBox = (CheckBox)itemView.findViewById(R.id.checked);
            exp_rl = (RelativeLayout)itemView.findViewById(R.id.expendable_rl);
        }
    }

}
