package com.tovos.uav.sample.route.util.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.commonlib.utils.DialogManager;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.bean.DbMediaPoint;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;
import com.tovos.uav.sample.databean.sql.dao.MedailDao;
import com.tovos.uav.sample.route.view.WaypointView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SelectTakeOffAdapter extends RecyclerView.Adapter<SelectTakeOffAdapter.MyTVHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
     DbUAVRoute uavRoute;
    private List<DBHoverPoint> hoverPoints = new ArrayList<>();
    private List<DbMediaPoint> mediaPoints = new ArrayList<>();
    private List<DbTower> towers = new ArrayList<>();
    WaypointView waypointView;
    float rtkAltitude;
    Dialog pointDialog;
    int index = 0;
    public SelectTakeOffAdapter(Context context, Dialog pointDialog, WaypointView waypointView, float rtkAltitude, DbUAVRoute uavRoute, List<DbTower> towers, List<DBHoverPoint> hoverPoints, int index) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.pointDialog = pointDialog;
        this.waypointView = waypointView;
        this.rtkAltitude = rtkAltitude;
        this.hoverPoints = hoverPoints;
        this.index = index;
        this.uavRoute = uavRoute;
        this.towers = towers;


    }

    @Override
    public SelectTakeOffAdapter.MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectTakeOffAdapter.MyTVHolder(mLayoutInflater.inflate(R.layout.item_point, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyTVHolder holder, final int pos) {

        //holder.error_text.setText(mData.get(pos).getCalibrationProgress());
        holder.index.setText(pos+1+"");
      //  List<HoverPoint> hoverPoints = uavRoute.getList().get(index).getList();

        MedailDao medailDao = new MedailDao(mContext);
        mediaPoints = medailDao.seleMedialByPid(hoverPoints.get(pos).getHid());

        if (hoverPoints!=null&&hoverPoints.size()>0&&mediaPoints!=null&&mediaPoints.size()>0){
            holder.name.setText(mediaPoints.get(0).getDisplayName());
        }else{
            holder.name.setText("");
        }
        holder.ll_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogManager.showSelectDialog(mContext, "提示", "确定使用该点作为起飞点？", "确定", "取消", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            //ToDo: 你想做的事情
                      //  DbUAVRoute uavRouteTamp = SerializationUtils.clone(uavRoute);
                      //  List<HoverPoint> newHoverPoints =  uavRouteTamp.getList().get(index).getList().subList(pos,uavRouteTamp.getList().get(index).getList().size());
                      //  List<HoverPoint> oldHoverPoints =uavRouteTamp.getList().get(index).getList();
                      //  oldHoverPoints.retainAll(newHoverPoints);
                        waypointView.setDataToFliht(uavRoute,towers, rtkAltitude,index,pos);
                        pointDialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ToDo: 你想做的事情
                        dialog.dismiss();
                    }
                });
            }
        });

    }


    @Override
    public int getItemCount() {
        return hoverPoints == null ? 0 : hoverPoints.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView index;
        LinearLayout ll_point;
        MyTVHolder(View itemView) {
            super(itemView);
            index = (TextView) itemView.findViewById(R.id.index);
            name = (TextView) itemView.findViewById(R.id.name);
            ll_point = (LinearLayout) itemView.findViewById(R.id.ll_point);


        }
    }
}

