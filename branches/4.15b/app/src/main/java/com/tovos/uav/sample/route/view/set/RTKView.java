package com.tovos.uav.sample.route.view.set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;

import com.example.commonlib.utils.DialogManager;
import com.example.commonlib.utils.FileManager;
import com.example.commonlib.utils.LogUtil;
import com.example.djilib.dji.DJIContext;
import com.example.djilib.dji.component.aircraft.rtk.CustomRTKNetworkListener;
import com.example.djilib.dji.component.aircraft.rtk.CustomRTKNetworkService;
import com.example.djilib.dji.component.aircraft.rtk.RTKListener;
import com.example.djilib.dji.component.aircraft.rtk.RtkManager;
import com.example.djilib.dji.djierror.CustomNetworkServiceAccountState;
import com.example.djilib.dji.djierror.CustomNetworkServiceChannelState;
import com.example.djilib.dji.djierror.RTKNetServiceError;
import com.tovos.uav.sample.MApplication;
import com.tovos.uav.sample.R;
import com.tovos.uav.sample.databean.RtkInfo;
import com.tovos.uav.sample.route.view.BaseStationState;
import com.tovos.uav.sample.route.view.adapter.BaseStationAdapter;
import com.tovos.uav.sample.route.view.adapter.TowerAdapter;
import com.tovos.uav.sample.route.view.listener.ActivityListener;
import com.example.commonlib.utils.CustomNumberUtils;
import com.example.commonlib.utils.ToastUtils;
import com.tovos.uav.sample.route.view.listener.OnTaskItemClickListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dji.common.error.DJIError;
import dji.common.flightcontroller.rtk.CoordinateSystem;
import dji.common.flightcontroller.rtk.NetworkServiceAccountState;
import dji.common.flightcontroller.rtk.NetworkServiceChannelState;
import dji.common.flightcontroller.rtk.NetworkServicePlan;
import dji.common.flightcontroller.rtk.NetworkServicePlanType;
import dji.common.flightcontroller.rtk.NetworkServicePlansState;
import dji.common.flightcontroller.rtk.NetworkServiceState;
import dji.common.flightcontroller.rtk.RTKBaseStationInformation;
import dji.common.flightcontroller.rtk.RTKConnectionStateWithBaseStationReferenceSource;
import dji.common.flightcontroller.rtk.ReferenceStationSource;


import static android.content.Context.MODE_PRIVATE;

public class RTKView extends LinearLayout implements RTKListener, CustomRTKNetworkListener , OnTaskItemClickListener {

    private ActivityListener listener;
    public RadioGroup rtk_type,rtk_nzzz_type,rtk_zbx;
    public EditText rtk_addr, rtk_psot, rtk_gzd, rtk_user, rtk_pwd, server_ip;
    public TextView rtk_tx;
    public LinearLayout custom_rtk_ll,nzzh_ll,custom_ll,base_rtk_ll;
  //  public Switch rtk_switch;
    private RelativeLayout rtk_sure,rtk_back_rl;
    private Button button_start;
    private RecyclerView base_list;
    private RtkManager rtkManager = null;
    private Activity activity;
    private boolean isFly = false;
    private float Rtk_altitude = 0.0f;
    private BaseStationAdapter adapter;
    private CustomRTKNetworkService customRTKNetworkService;
    private Context context;
    private ReferenceStationSource referenceStationSource = ReferenceStationSource.UNKNOWN;
    private NetworkServiceAccountState networkServiceAccountState = NetworkServiceAccountState.UNKNOWN;
    private List<RTKBaseStationInformation> rtkBaseStationInformationArrayList = new ArrayList<>();

    public void setActivityListener(ActivityListener listener, Activity activity){
        this.listener = listener;
        this.activity = activity;
    }

    public boolean isFly() {
        return isFly;
    }

    public void setFly(boolean fly) {
        isFly = fly;
    }

    public float getRtk_altitude() {
        return Rtk_altitude;
    }

    public void setRtk_altitude(float rtk_altitude) {
        Rtk_altitude = rtk_altitude;
    }

    public RTKView(Context context) {
        super(context);
        this.context = context;
        initRtkView(context);
    }

    public RTKView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initRtkView(context);
    }

    public RTKView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initRtkView(context);
    }

    public RTKView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initRtkView(context);
    }

    public CustomRTKNetworkService getCustomRtkNetWorkService(){
        if (customRTKNetworkService == null){
            if (DJIContext.getAircraftInstance()!=null){
                customRTKNetworkService = new CustomRTKNetworkService(this);
            }
        }
        return customRTKNetworkService;
    }

    public RtkManager getRtkManager(){
        LogUtil.d("RTK网络服务: rtkManager:"+rtkManager);
        if (rtkManager == null) {
            if (DJIContext.getAircraftInstance()!=null) {
                rtkManager = new RtkManager(this);
            }
        }
        return rtkManager;
    }

    /**
     * 初始化RTK设置VIEW
     */
    public void initRtkView(Context context) {

        LayoutInflater.from(context).inflate(R.layout.rtk_set_layout, this);
        rtk_back_rl = (RelativeLayout) findViewById(R.id.rtk_back_rl);
        rtk_type = (RadioGroup) findViewById(R.id.rtk_group);
        rtk_nzzz_type = (RadioGroup) findViewById(R.id.rtk_in_nzzj);
        rtk_zbx = (RadioGroup) findViewById(R.id.rtk_zbx_group);
        rtk_sure = (RelativeLayout) findViewById(R.id.rtk_sure);
        button_start = (Button) findViewById(R.id.button_start);
        base_list = (RecyclerView) findViewById(R.id.base_list);

        rtk_addr = (EditText) findViewById(R.id.rtk_addr);
        rtk_psot = (EditText) findViewById(R.id.rtk_psot);
        rtk_gzd = (EditText) findViewById(R.id.rtk_gzd);
        rtk_user = (EditText) findViewById(R.id.rtk_user);
        rtk_pwd = (EditText) findViewById(R.id.rtk_pwd);
        rtk_tx = (TextView) findViewById(R.id.rtk_status);
        custom_rtk_ll = (LinearLayout) findViewById(R.id.custom_rtk_ll);
        base_rtk_ll = (LinearLayout) findViewById(R.id.base_rtk_ll);
        nzzh_ll = (LinearLayout) findViewById(R.id.nzzh_ll);
        custom_ll = (LinearLayout) findViewById(R.id.custom_ll);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        base_list.setLayoutManager(linearLayoutManager);
        adapter = new BaseStationAdapter(getContext(), rtkBaseStationInformationArrayList,this);
        base_list.setAdapter(adapter);

        rtk_back_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backToMenuView(RTKView.this);
            }
        });

        rtk_nzzz_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (getCustomRtkNetWorkService()==null)
                    return;

                switch (checkedId){
                    case R.id.a_rb:
                        ((TextView)findViewById(R.id.jh_time)).setText("未激活");

                        if (getCustomRtkNetWorkService().getSelectednetworkServicePlan() != null){
                            findViewById(R.id.jh_ll_activie).setVisibility(GONE);
                            if (getCustomRtkNetWorkService().getSelectednetworkServicePlan().getPlanName() == NetworkServicePlanType.A){
                                ((TextView)findViewById(R.id.jh_time))
                                        .setText(getCustomRtkNetWorkService().getSelectednetworkServicePlan() .getActivationDate()
                                                +" 到 "
                                                +getCustomRtkNetWorkService().getSelectednetworkServicePlan() .getExpirationDate());
                            }else if (getCustomRtkNetWorkService().getSelectednetworkServicePlan().getPlanName() == NetworkServicePlanType.B){
                                ((TextView)findViewById(R.id.jh_time))
                                        .setText("需要在"+getCustomRtkNetWorkService().getSelectednetworkServicePlan() .getExpirationDate()+"前激活，否则自动激活");
                            }
                        }else{
                            findViewById(R.id.jh_ll_activie).setVisibility(VISIBLE);
                        }

                        break;
                    case R.id.b_rb:
                        ((TextView)findViewById(R.id.jh_time)).setText("未激活");

                        if (getCustomRtkNetWorkService().getSelectednetworkServicePlan() != null){
                            findViewById(R.id.jh_ll_activie).setVisibility(GONE);
                            if (getCustomRtkNetWorkService().getSelectednetworkServicePlan().getPlanName() == NetworkServicePlanType.B){
                                ((TextView)findViewById(R.id.jh_time))
                                        .setText(getCustomRtkNetWorkService().getSelectednetworkServicePlan() .getActivationDate()
                                                +" 到 "
                                                +getCustomRtkNetWorkService().getSelectednetworkServicePlan() .getExpirationDate());
                            }else if (getCustomRtkNetWorkService().getSelectednetworkServicePlan().getPlanName() == NetworkServicePlanType.A){
                                ((TextView)findViewById(R.id.jh_time))
                                        .setText("需要在"+getCustomRtkNetWorkService().getSelectednetworkServicePlan() .getExpirationDate()+"前激活，否则自动激活");
                            }else {
                                findViewById(R.id.jh_ll_activie).setVisibility(VISIBLE);
                            }
                        }
                        break;
                }
            }
        });

        ((RelativeLayout)findViewById(R.id.jh_ll_activie)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                
                LogUtil.d("网络RTK服务","111111111111111");
                NetworkServicePlanType networkServicePlanType = NetworkServicePlanType.UNKNOWN;
                LogUtil.d("网络RTK服务","222222222222");
                if (((RadioButton)findViewById(R.id.a_rb)).isChecked()){
                    networkServicePlanType = NetworkServicePlanType.A;
                }else if (((RadioButton)findViewById(R.id.b_rb)).isChecked()){
                    networkServicePlanType = NetworkServicePlanType.B;
                }
                LogUtil.d("网络RTK服务","激活 networkServicePlanType："+networkServicePlanType);
                getCustomRtkNetWorkService().activateNetworkService(networkServicePlanType);
            }
        });

        rtk_zbx.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (getCustomRtkNetWorkService() != null){
                    switch (checkedId){
                        case R.id.wgs84_rb:
                            LogUtil.d("RTK网络服务","设置84坐标系");
                            getCustomRtkNetWorkService().setNetworkServiceCoordinateSystem(CoordinateSystem.WGS84);
                            break;
                        case R.id.c2000_rb:
                            LogUtil.d("RTK网络服务","C2000坐标系");
                            getCustomRtkNetWorkService().setNetworkServiceCoordinateSystem(CoordinateSystem.CGCS2000);
                            break;
                    }
                }
            }
        });

        rtk_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (!isRtkAble()) {
                    //ToastUtils.setResultToToast("RTK模块不可用！");
                    return;
                }
                switch (checkedId) {
                    case R.id.atuo_rb:
                        LogUtil.d("RTK网络服务","切换内置账号");
                        custom_rtk_ll.setVisibility(View.GONE);
                        base_rtk_ll.setVisibility(GONE);
                        rtkManager.setSignal(0);
                        break;
                    case R.id.custom_rb:
                        LogUtil.d("RTK网络服务","切换自定义账号");
                        custom_rtk_ll.setVisibility(View.VISIBLE);
                        base_rtk_ll.setVisibility(GONE);
                        rtkManager.setSignal(1);
                        break;
                    case R.id.base_rb:
                        LogUtil.d("RTK网络服务","搜索基站");
                        rtkManager.setRtkConnectionStateWithBaseStationCallback();
                        custom_rtk_ll.setVisibility(View.GONE);
                        base_rtk_ll.setVisibility(VISIBLE);
                        rtkManager.setSignal(2);
                        break;
                }
            }
        });
        rtk_type.setEnabled(false);
        rtk_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isRtkAble()){
                    ToastUtils.setResultToToast("RTK不可用");
                    return;
                }

                if (getCustomRtkNetWorkService() == null) {
                    ToastUtils.setResultToToast("RTK模块不可用！");
                    return;
                }

                if (referenceStationSource == ReferenceStationSource.CUSTOM_NETWORK_SERVICE) {
                    getCustomRtkNetWorkService().setNetWorkSeriverProvider(rtk_addr.getText().toString(), rtk_user.getText().toString(),
                            rtk_pwd.getText().toString(), rtk_psot.getText().toString(), rtk_gzd.getText().toString());

                }else if (referenceStationSource == ReferenceStationSource.NETWORK_RTK){
                    //getCustomRtkNetWorkService().activateNetworkService();
                }
                if (rtkManager.getRtk().isConnected()){
                    getCustomRtkNetWorkService().reOpenNetWorkService();
                }else {
                    getCustomRtkNetWorkService().openNetWorkService();
                }
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DJIContext.getAircraftInstance()!=null&&"MATRICE_300_RTK".equals(DJIContext.getAircraftInstance().getModel().getDisplayName())) {
                    if ("开始".equals(button_start.getText().toString())){
                        button_start.setText("停止");
                        rtkManager.startSearchBaseStation();
                    }else{
                        button_start.setText("开始");
                        rtkManager.stopSearchBaseStation();
                    }
                }else{
                    ToastUtils.setResultToToast("此功能只支持M300 RTK");
                }


            }});

        rtk_addr.setText(getRtkInfo().getIp());
        rtk_psot.setText(getRtkInfo().getPost() + "");
        rtk_gzd.setText(getRtkInfo().getGzd());
        rtk_user.setText(getRtkInfo().getUser());
        rtk_pwd.setText(getRtkInfo().getPwd());
    }

    public boolean isRtkAble() {
        boolean isRtkAble = true;
        if (rtkManager == null) {
            isRtkAble = false;
            LogUtil.d("RTK网络服务","getRtkManager:"+rtkManager);
        } else {
            if (!getRtkManager().isRtkAble()) {
                isRtkAble = false;
            }
        }
        return isRtkAble;
    }



    public void autoOpenNetWorkService() {
        if (isRtkAble()) {
            RtkInfo rtkInfo = getRtkInfo();
            if (rtkInfo != null) {
                if (rtkInfo.getIp().length() > 0 && rtkInfo.getUser().length() > 0 && rtkInfo.getPwd().length() > 0 && rtkInfo.getGzd().length() > 0 && rtkInfo.getPost().length() > 0) {

                    if (!getRtkManager().isRtkConnected && !getRtkManager().isRtkConnectFinish) {
                        getCustomRtkNetWorkService().setNetWorkSeriverProvider(rtkInfo.getIp(), rtkInfo.getUser(), rtkInfo.getPwd(), rtkInfo.getPost(), rtkInfo.getGzd());
                        getCustomRtkNetWorkService().openNetWorkService();
                    } else {
                        ToastUtils.setResultToToast("RTK已连接");
                    }
                }
            }
        }
    }


    public void setRtkUI(boolean isAble){
        rtk_type.setEnabled(isAble);
        if (isAble) {
            getRtkManager();
            getCustomRtkNetWorkService();
        }else {
            if (getCustomRtkNetWorkService() !=null) {
                getCustomRtkNetWorkService().removeRtkLinkListener();
            }

            if (rtkManager!=null){
                rtkManager.removeListener();
            }

            rtkManager = null;
        }
    }

    @Override
    public void openRtkResult(DJIError djiError) {

    }

    @Override
    public void getRtkStatues(boolean isopen) {

    }

    @Override
    public void setSignalResult(DJIError djiError) {

//        if (djiError == null){
//            getRtkManager().getNowSignal();
//        }
    }

    @Override
    public void getNowSignalResult(ReferenceStationSource referenceStationSource) {
        this.referenceStationSource = referenceStationSource;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rtk_type.clearCheck();
                custom_rtk_ll.setVisibility(GONE);
                custom_ll.setVisibility(GONE);
                nzzh_ll.setVisibility(GONE);
                base_rtk_ll.setVisibility(GONE);
                LogUtil.d("RTK网络服务: referenceStationSource "+referenceStationSource.name());
                if (referenceStationSource == ReferenceStationSource.NETWORK_RTK){
                    ((RadioButton)findViewById(R.id.atuo_rb)).setChecked(true);
                    custom_rtk_ll.setVisibility(VISIBLE);
                    nzzh_ll.setVisibility(VISIBLE);
                    if (getCustomRtkNetWorkService()!=null) {
                        getCustomRtkNetWorkService().getNetworkServiceOrderPlans();
                        if (getCustomRtkNetWorkService().getmCoordinateSystem() == CoordinateSystem.UNKNOWN) {
                            getCustomRtkNetWorkService().getNetworkServiceCoordinateSystem();
                        }
                    }

                }else if (referenceStationSource == ReferenceStationSource.CUSTOM_NETWORK_SERVICE){
                    ((RadioButton)findViewById(R.id.custom_rb)).setChecked(true);
                    custom_rtk_ll.setVisibility(VISIBLE);
                    custom_ll.setVisibility(VISIBLE);
                }else  if(referenceStationSource == ReferenceStationSource.BASE_STATION){

                    ((RadioButton)findViewById(R.id.base_rb)).setChecked(true);
                    base_rtk_ll.setVisibility(VISIBLE);
                 //   custom_ll.setVisibility(VISIBLE);


                }else{
                    //rtk_type.clearCheck();
                    // getRtkManager().setSignal(1);
                    //   ((RadioButton)findViewById(R.id.custom_rb)).setChecked(true);
//                    custom_rtk_ll.setVisibility(VISIBLE);
//                    custom_ll.setVisibility(VISIBLE);
                }
            }
        });

    }

    @Override
    public void openNetWorkResult(DJIError djiError) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (djiError != null) {
                    rtk_tx.setText(RTKNetServiceError.getError(djiError.getErrorCode()));
                } else {
                    rtk_tx.setText("正在连接...");
                }
            }
        });

    }

    @Override
    public void getRtkLinkStatues(NetworkServiceState networkServiceState) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String description5 = String.valueOf(networkServiceState.getChannelState());
                String str = CustomNetworkServiceChannelState.getError(description5);
                rtk_tx.setText(str);
            }
        });
        LogUtil.d("RTK网络服务","RTK连接状态："+networkServiceState.getChannelState());

        if (networkServiceState.getChannelState() == NetworkServiceChannelState.DISABLED){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.ACCOUNT_EXPIRED){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.NETWORK_NOT_REACHABLE){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.AIRCRAFT_DISCONNECTED){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.LOGIN_FAILURE){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.TRANSMITTING){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.DISCONNECTED){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.ACCOUNT_ERROR){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.SERVER_NOT_REACHABLE){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.CONNECTING){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.SERVICE_SUSPENSION){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.INVALID_REQUEST){

        }else if (networkServiceState.getChannelState() == NetworkServiceChannelState.UNKNOWN){

        }

    }

    @Override
    public void getNetworkServiceOrderPlans(NetworkServicePlansState networkServicePlansState) {
        setNzzhStatus(networkServicePlansState);

    }

    @Override
    public void getNetworkServiceCoordinateSystem(CoordinateSystem coordinateSystem) {
        //setNzzhStatus(networkServicePlansState);
         LogUtil.d("RTK网络服务","获取坐标系:"+coordinateSystem.name());
    }

    @Override
    public void setHomeAltitude(float altitude) {
        if (!isFly) {
            Rtk_altitude = altitude;
            float nums = Float.valueOf(CustomNumberUtils.format4(Rtk_altitude));
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    ((TextView) findViewById(R.id.hbgc)).setText("海拔高程:" + nums);
//                }
//            });
        }
    }

    @Override
    public void getRTKBaseStationInformationList(RTKBaseStationInformation[] rtkBaseStationInformations) {

        if (rtkBaseStationInformations!=null&&rtkBaseStationInformations.length>0){
            for (RTKBaseStationInformation rtkBaseStationInformation:rtkBaseStationInformations){
                rtkBaseStationInformationArrayList.add(rtkBaseStationInformation);
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
            LogUtil.d("基站："+rtkBaseStationInformations.length);
        }else{
            LogUtil.d("没有基站");
        }
    }

    @Override
    public void getRTKConnectionStateWithBaseStationState(RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource, RTKBaseStationInformation rtkBaseStationInformation) {
        rtk_tx.setText(BaseStationState.getState(rtkConnectionStateWithBaseStationReferenceSource.value()));
    }


    private void setNzzhStatus(NetworkServicePlansState networkServicePlansState){

        LogUtil.d("RTK网络服务","networkServicePlansState:"+networkServicePlansState.getState());
        this.networkServiceAccountState = networkServicePlansState.getState();
        for (int i = 0;i<networkServicePlansState.getPlans().size();i++){
            LogUtil.d("RTK网络服务","激活计划:"+networkServicePlansState.getPlans().get(i).getPlanName());
            LogUtil.d("RTK网络服务","激活时间:"+networkServicePlansState.getPlans().get(i).getActivationDate());
            LogUtil.d("RTK网络服务","到期时间:"+networkServicePlansState.getPlans().get(i).getExpirationDate());
        }

        String ns = CustomNetworkServiceAccountState.getError(networkServicePlansState.getState());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rtk_tx.setText(ns);
                if (networkServicePlansState.getPlans()!=null){
                    for (int i=0;i<networkServicePlansState.getPlans().size();i++){
                        NetworkServicePlan networkServicePlan = networkServicePlansState.getPlans().get(i);
                        if (networkServicePlan.getPlanName() == NetworkServicePlanType.A){
                            ((RadioButton)findViewById(R.id.a_rb)).setChecked(true);
                            getCustomRtkNetWorkService().setSelectednetworkServicePlan( networkServicePlan);
                        }else if (networkServicePlan.getPlanName() == NetworkServicePlanType.B){
                            ((RadioButton)findViewById(R.id.b_rb)).setChecked(true);
                            getCustomRtkNetWorkService().setSelectednetworkServicePlan( networkServicePlan);
                        }
                    }
                }

                if (getCustomRtkNetWorkService().getSelectednetworkServicePlan() !=null){
                    ((TextView)findViewById(R.id.jh_time))
                            .setText(getCustomRtkNetWorkService().getSelectednetworkServicePlan() .getActivationDate()
                                    +" 到 "
                                    +getCustomRtkNetWorkService().getSelectednetworkServicePlan() .getExpirationDate());
                    findViewById(R.id.jh_ll_activie).setVisibility(GONE);
                }else {
                    ((TextView)findViewById(R.id.jh_time)).setText("未激活");
                    findViewById(R.id.jh_ll_activie).setVisibility(VISIBLE);
                }
            }
        });
    }

    public  RtkInfo getRtkInfo(){
        RtkInfo rtkInfo = new RtkInfo();
        SharedPreferences shared = MApplication.getInstance().getBaseContext().getSharedPreferences("rtk_data", MODE_PRIVATE);
        //获取xml数据
        String ip = shared.getString("ip", "");
        String user = shared.getString("user", "");
        String pwd = shared.getString("pwd", "");
        String gzd = shared.getString("gzd", "");
        String post = shared.getString("post","");
        rtkInfo.setIp(ip);
        rtkInfo.setUser(user);
        rtkInfo.setPost(post);
        rtkInfo.setPwd(pwd);
        rtkInfo.setGzd(gzd);
        return rtkInfo;
    }

    @Override
    public void onTaskItemClick(int Position) {
        DialogManager.showSelectDialog(context, "提示", "确定连接此基站？", "确定", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rtkManager.connectToBaseStation(rtkBaseStationInformationArrayList.get(Position).getBaseStationID());
//                //ToDo: 你想做的事情
//                for (int position = 0; position < delePath.size(); position++) {
//                    FileManager.delete(delePath.get(position));
//                }
//                setTaskList();
//                delePath.clear();
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ToDo: 你想做的事情
               // delePath.clear();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onTaskItemLongClick(int position) {

    }

}
