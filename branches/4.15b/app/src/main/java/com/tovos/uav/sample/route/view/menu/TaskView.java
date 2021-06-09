package com.tovos.uav.sample.route.view.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tovos.uav.sample.MApplication;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.sax.UavRouteHandler;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;
import com.tovos.uav.sample.databean.sql.dao.TowerDao;
import com.tovos.uav.sample.databean.sql.dao.UavDao;
import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.tovos.uav.sample.route.view.listener.OnRouteItemClickListener;
import com.tovos.uav.sample.route.view.adapter.RouteAdapter;
import com.tovos.uav.sample.route.view.adapter.TowerAdapter;
import com.example.commonlib.utils.FileManager;
import com.example.commonlib.utils.ToastUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TaskView extends LinearLayout {

    public RecyclerView recyclerView;
    public List<DbUAVRoute> list = new ArrayList<>();
    public OnRouteItemClickListener listener;
    public RouteAdapter routeAdapter;
    private Activity activity;
    //public RouteFlightManager routeFlightManager;
    private ActivityListener activityListener;
    private List<String> routePath = new ArrayList<>();
    public RecyclerView towerListView;
    public TowerAdapter towerAdapter;
    public DbUAVRoute uavRoute;
    private ProgressBar progressBar;
    private RelativeLayout task_ll;
    private Context context;


    public void setActivityListener(ActivityListener activityListener,Activity activity) {
        this.activityListener = activityListener;
        this.activity = activity;
    }

    public TaskView(Context context) {
        super(context);
        this.context =context;
        initRouteView(context);
    }

    public TaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
        initRouteView(context);
    }

    public TaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context =context;
        initRouteView(context);
    }

    public TaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context =context;
        initRouteView(context);
    }

    /**
     * 初始化航线选择View
     */
    public void initRouteView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.task_layout, this);
        progressBar = findViewById(R.id.task_pg);
        task_ll = findViewById(R.id.task_ll_in);
        listener = new OnRouteItemClickListener() {

            @Override
            public void onTaskItemClick(int Position) {
                findViewById(R.id.tower_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.route_manager).setVisibility(View.GONE);
                uavRoute = list.get(Position);
                initTowerView();
            }

            @Override
            public void onTaskItemLongClick(int position) {
                if (findViewById(R.id.route_edit_ll).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.route_edit_ll).setVisibility(View.GONE);
                    routeAdapter.setCheckModel(false);
                } else {
                    findViewById(R.id.route_edit_ll).setVisibility(View.VISIBLE);
                    routeAdapter.setCheckModel(true);
                }
                routeAdapter.setSelectedAllFalse();
            }
        };
        recyclerView = (RecyclerView) findViewById(R.id.route_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        routeAdapter = new RouteAdapter(getContext(), list, listener);
        recyclerView.setAdapter(routeAdapter);

        findViewById(R.id.route_all_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeAdapter.setSelectedAll();
            }
        });

        findViewById(R.id.route_dele).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isSelected()) {
                        size++;
                        routePath.add(list.get(i).getPath());
                    }
                }
                if (size == 0) {
                    ToastUtils.setResultToToast("请选择要删除的任务！");
                } else {
                    showDeleWay();
                }
            }
        });

        findViewById(R.id.sd_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityListener.setSelectedFile();
            }
        });

        findViewById(R.id.web_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int size = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isSelected()) {
                        size++;
                        routePath.add(list.get(i).getPath());
                    }
                }
                ToastUtils.setResultToToast("暂不支持此功能！");
                if (size == 0) {
                    //ToastUtils.setResultToToast("请选择要删除的任务！");
                } else {
                    //showTwo();
                }
            }
        });
    }


    private  List<DbTower> towers = new ArrayList<>();
    /**
     * 初始化杆塔界面
     */
    public void initTowerView() {
        findViewById(R.id.route_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.tower_layout).setVisibility(View.GONE);
                findViewById(R.id.route_manager).setVisibility(View.VISIBLE);
                //customWayPoint.cleanData();
                //myMapManager.cleanMap();
            }
        });

        ((TextView) findViewById(R.id.route_name)).setText(uavRoute.getName());

        towerListView = (RecyclerView) findViewById(R.id.towerlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        towerListView.setLayoutManager(linearLayoutManager);
        TowerDao towerDao = new TowerDao(context);
        towers = towerDao.seleTowerByPid(uavRoute.getUid());//selectAll();
        towerAdapter = new TowerAdapter(getContext(), towers);
        towerListView.setAdapter(towerAdapter);

        setRouteClick();
    }


    // 获取文件的真实路径
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {

        String path = "";
        Uri uri = data.getData(); // 获取用户选择文件的URI
        FileManager fileManager = new FileManager();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
             String nn =data.getData().getPath();
            Log.e("11111", "nn:" + nn);
            path = fileManager.getPath(getContext(), uri);
        } else {
            //4.4以下下系统调用方法            c
            path = fileManager.getRealPathFromURI(getContext(), uri);
        }
        Log.e("11111", "path:" + path);
        if (path==null){return; }
        if (path.indexOf(".trl") > 0) {
            selectDataToSd(path);
        } else {
            ToastUtils.setResultToToast("文件格式不对");
        }
    }


    public void selectDataToSd(String path){
        task_ll.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                filetoData(path);

                UavDao uavDao = new UavDao(context);
                list = uavDao.selectAll();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        task_ll.setVisibility(VISIBLE);
                        progressBar.setVisibility(GONE);
                        //routeAdapter.notifyDataSetChanged();
                        routeAdapter = new RouteAdapter(getContext(), list, listener);
                        recyclerView.setAdapter(routeAdapter);
                    }
                });
            }
        }).start();
    }

    public void initData() {

        task_ll.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                File[] files = FileManager.getAllFliesByPath(MApplication.wayPath);
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        Log.e("1111", "files[i].getName():" + files[i].getName());
                        if (files[i].getName().indexOf(".trl") > 0) {
                            filetoData(files[i].getPath());
                        }
                    }

                    for (File file:files){
                        file.delete();
                    }
                }


                UavDao uavDao = new UavDao(context);
                list = uavDao.selectAll();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        task_ll.setVisibility(VISIBLE);
                        progressBar.setVisibility(GONE);
                        //routeAdapter.notifyDataSetChanged();
                        routeAdapter = new RouteAdapter(getContext(), list, listener);
                        recyclerView.setAdapter(routeAdapter);
                    }
                });
            }
        }).start();
    }

    private void filetoData(String path) {

        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                InputStream is = new FileInputStream(file.getPath());//getContext().getAssets().open(file.getPath());
                UavRouteHandler handle = new UavRouteHandler();
                handle.setPath(path);
                saxParser.parse(is, handle);
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 删除任务
     */
    private AlertDialog.Builder routeDele;

    private void showDeleWay() {

        routeDele = new AlertDialog.Builder(getContext()).setIcon(R.mipmap.ic_launcher).setTitle("提示")
                .setMessage("是否确定要删除选择的任务？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        //
                        for (int position = 0; position < routeAdapter.getItemCount(); position++) {
                            DbUAVRoute uavRoute = routeAdapter.getUavRouteByPosition(position);
                            if (uavRoute.isSelected()){
                                UavDao uavDao = new UavDao(getContext());
                                uavDao.delete(uavRoute);
                            }
                        }
                        routeAdapter.setSelectedAllFalse();
                        initData();
                        routePath.clear();
                        findViewById(R.id.route_edit_ll).setVisibility(View.GONE);
                        routeAdapter.setCheckModel(false);

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        routePath.clear();
                        dialogInterface.dismiss();
                    }
                });
        routeDele.create().show();
    }

    private void setRouteClick() {
        findViewById(R.id.save_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                DbUAVRoute item = new DbUAVRoute();
//                item.setBaseLocation(uavRoute.getBaseLocation());
//                item.setCameraType(uavRoute.getCameraType());
//                item.setCorporation(uavRoute.getCorporation());
//                item.setCreatedTime(uavRoute.getCreatedTime());
//                item.setCreator(uavRoute.getCreator());
//                item.setVersion(uavRoute.getVersion());
//                item.setRouteType(uavRoute.getRouteType());
//                item.setUAVType(uavRoute.getUAVType());
//                for (int i = 0; i < towers.size(); i++) {
//                    if (towers.get(i).isIschecked()) {
//                        item.getList().add(towers.get(i));
//                    }
//                }
//                showSaveWay(item);
            }
        });

        findViewById(R.id.load_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityListener.setDataToFliht(uavRoute,towers);
            }
        });
        findViewById(R.id.select_point).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activityListener.selectFirstPoint(uavRoute,towers);
            }
        });
    }
}
