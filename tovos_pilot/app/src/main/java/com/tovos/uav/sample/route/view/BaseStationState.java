package com.tovos.uav.sample.route.view;

import com.example.djilib.dji.djierror.RTKNetServiceError;

import dji.common.error.DJIRTKNetworkServiceError;
import dji.common.flightcontroller.rtk.RTKConnectionStateWithBaseStationReferenceSource;

public enum BaseStationState {
    IDLE(RTKConnectionStateWithBaseStationReferenceSource.IDLE.value(),"RTK处于空闲状态"),
    SCANNING(RTKConnectionStateWithBaseStationReferenceSource.SCANNING.value(),"搜索中..."),
    CONNECTING(RTKConnectionStateWithBaseStationReferenceSource.CONNECTING.value(),"正在连接..."),
    CONNECTED(RTKConnectionStateWithBaseStationReferenceSource.CONNECTED.value(),"已连接"),
    DISCONNECTED(RTKConnectionStateWithBaseStationReferenceSource.DISCONNECTED.value(),"断开连接"),
    UNKNOWN(RTKConnectionStateWithBaseStationReferenceSource.UNKNOWN.value(),"未知错误");



    int code;
    String state;

    BaseStationState(int code, String state) {
        this.code = code;
        this.state = state;
    }
    public static  String getState(int code) {

        for (BaseStationState e : BaseStationState.values()) {
            if (e.code == code) {
                return e.state;
            }
        }
        return "连接失败";

    }


}
