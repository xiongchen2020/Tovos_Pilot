package com.tovos.uav.sample.route.view.listener;

import dji.common.battery.BatteryState;

public interface BatterySetListener {


    void setStateCallbackView(BatteryState batteryState);
}
