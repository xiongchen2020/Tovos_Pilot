package com.tovos.uav.sample.route.waypoint;

import dji.common.error.DJIError;

public interface WapointListener {

    void startWayPoint();

    void setStartOrStop(boolean start);//true  表示开始飞行  false表示结束飞行

    void setPauseOrResume(boolean pause);//true  表示重新开始飞行  false表示暂停飞行

    void downLoadWayPoint();

    void onDownloadUpdate() ;

    void onUploadUpdate();

    void onExecutionUpdate(int position) ;

    void onExecutionStart();

    void onExecutionFinish(String djiError);

    void onExecutionStopped();

    void setStartFlightHeight();

    void setMayMarker();

    void getDownLoadDataSuccess();

    void getDownLoadDataFail(DJIError djiError);


    void upLoadFinish();


}
