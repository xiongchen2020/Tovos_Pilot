package com.tovos.uav.sample.route.view.menu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlib.utils.CustomNumberUtils;
import com.example.commonlib.utils.CustomTimeUtils;
import com.tovos.uav.sample.MApplication;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.route.util.XMLUtil;
import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.tovos.uav.sample.databean.TaskBean;
import com.tovos.uav.sample.route.view.listener.OnTaskItemClickListener;
import com.tovos.uav.sample.route.view.adapter.TaskListAdapter;
import com.example.commonlib.utils.DialogManager;
import com.example.commonlib.utils.FileManager;
import com.example.commonlib.utils.ToastUtils;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FlightRecordView extends LinearLayout implements OnTaskItemClickListener {
    private Context context;


    public RecyclerView task_list, teamList;
    public List<TaskBean> taskBeanList = new ArrayList<>();
    public TaskListAdapter taskListAdapter;
    private Activity activity;
    private LinearLayout filght_edit_ll, llSetVideo;

    private RelativeLayout filght_all_select_rl, flight_dele_rl;
    private TextView all_flight_lc, all_nums, all_times;
    private ProgressBar progressBar;
    private RelativeLayout rl;
    private ActivityListener listener;
    private List<String> delePath = new ArrayList<>();

    public FlightRecordView(Context context) {
        this(context, null);

    }

    public FlightRecordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);


    }

    public FlightRecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FlightRecordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.flight_record_layout, this);
        initView();
    }

    public void setActivityListener(ActivityListener listener, Activity activity) {
        this.listener = listener;
        this.activity = activity;
    }

    public void initView() {
        progressBar = findViewById(R.id.flight_pg);
        rl = findViewById(R.id.flight_list_view);
        filght_edit_ll = (LinearLayout) findViewById(R.id.flight_record_edit_ll);
        filght_all_select_rl = (RelativeLayout) findViewById(R.id.flight_all_selected);
        flight_dele_rl = (RelativeLayout) findViewById(R.id.flight_all_dele);
        all_flight_lc = (TextView) findViewById(R.id.flight_all_lc);
        all_nums = (TextView) findViewById(R.id.flight_all_nums);
        all_times = (TextView) findViewById(R.id.flight_all_times);
        task_list = (RecyclerView) findViewById(R.id.task_list);
        filght_all_select_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                taskListAdapter.setSelectedAll();
            }
        });

        flight_dele_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = 0;
                for (int i = 0; i < taskBeanList.size(); i++) {
                    if (taskBeanList.get(i).isIschecked()) {
                        size++;
                        delePath.add(taskBeanList.get(i).getPath());
                        //Log.e("TAG","添加删除路径:"+taskBeanList.get(i).getPath());
                    }
                }
                if (size == 0) {
                    ToastUtils.setResultToToast("请选择要删除的任务！");
                } else {

                    DialogManager.showSelectDialog(context, "提示", "是否确定要删除选择的飞行记录？", "确定", "取消", false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //ToDo: 你想做的事情
                            for (int position = 0; position < delePath.size(); position++) {
                                FileManager.delete(delePath.get(position));
                            }
                            setTaskList();
                            delePath.clear();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //ToDo: 你想做的事情
                            delePath.clear();
                            dialog.dismiss();
                        }
                    });
                    // showDeleFlightRecord();
                }

            }
        });

    }


    public void setTaskList() {

        progressBar.setVisibility(VISIBLE);
        rl.setVisibility(GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                taskBeanList.clear();
                File[] files = FileManager.getAllFliesByPath(MApplication.picPath);
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {

//                        String name = files[i].getName();
//                        String[] names = name.split("\\.");
//                        String last = names[names.length - 1];
//                        if ("trl".equals(last)) {
                        try {
                            if (files[i].getName().indexOf(".trl") > 0) {
                                //打开文件
                                File file = new File(files[i].getPath());
                                TaskBean taskBean = XMLUtil.XMLToTask(file, files[i].getPath());
                                taskBeanList.add(taskBean);


                            }
                        }catch (Exception e){
                            //ToastUtils.setResultToToast("文件读取错误");
                        }

                    }
                    Collections.sort(taskBeanList, new Comparator<TaskBean>() {
                        @Override
                        public int compare(TaskBean o1, TaskBean o2) {
                            return o1.getEndtime() < o2.getEndtime() ? 1 : -1;
                        }
                    });

                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(GONE);
                        rl.setVisibility(VISIBLE);
                        initListView();
                    }
                });
            }
        }).start();
    }

    private void initListView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        task_list.setLayoutManager(linearLayoutManager);

        filght_edit_ll.setVisibility(View.GONE);
        taskListAdapter = new TaskListAdapter(context, taskBeanList, this);
        taskListAdapter.setSelectedAllFalse();
        task_list.setAdapter(taskListAdapter);
        ((TextView) findViewById(R.id.flight_all_nums)).setText("总飞行架次\n" + taskBeanList.size() + "架次");

        long all_times = 0;
        float all_selmiles = 0;
        for (int i = 0; i < taskBeanList.size(); i++) {
            long start = taskBeanList.get(i).getStatrtime();
            long end = taskBeanList.get(i).getEndtime();
            long times = end - start;
            float selmiles = taskBeanList.get(i).getMileage();
            all_times = all_times + times;
            all_selmiles = all_selmiles + selmiles;
        }

        String times = CustomTimeUtils.ScendsToHours(all_times);
        ((TextView) findViewById(R.id.flight_all_times)).setText("总飞行时间\n" + times);
        ((TextView) findViewById(R.id.flight_all_lc)).setText("飞行总里程\n" + CustomNumberUtils.format4(all_selmiles) + "M");
    }

    @Override
    public void onTaskItemClick(int Position) {
        listener.setStartMedialDataActivity(taskBeanList, Position);

    }

    @Override
    public void onTaskItemLongClick(int position) {
        if (filght_edit_ll.getVisibility() == View.VISIBLE) {
            filght_edit_ll.setVisibility(View.GONE);
            taskListAdapter.setCheckModel(false);
        } else {
            filght_edit_ll.setVisibility(View.VISIBLE);
            taskListAdapter.setCheckModel(true);
        }
        taskListAdapter.setSelectedAllFalse();
    }

//    class LongComparable implements  Comparable<Long>{
//
//        @Override
//        public int compareTo(Long o) {
//            return 0;
//        }
//    }
}
