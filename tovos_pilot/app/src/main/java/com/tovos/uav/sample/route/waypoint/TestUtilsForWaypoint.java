package com.tovos.uav.sample.route.waypoint;

import android.content.Context;
import android.graphics.PointF;

import com.tovos.uav.sample.databean.sql.dao.ActionDao;
import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.bean.DbAction;

import java.util.ArrayList;
import java.util.List;

import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointTurnMode;
import dji.common.mission.waypointv2.Action.ActionTypes;
import dji.common.mission.waypointv2.Action.WaypointActuator;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlRotateYawParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlStartStopFlyParam;
import dji.common.mission.waypointv2.Action.WaypointCameraActuatorParam;
import dji.common.mission.waypointv2.Action.WaypointCameraFocusParam;
import dji.common.mission.waypointv2.Action.WaypointCameraZoomParam;
import dji.common.mission.waypointv2.Action.WaypointGimbalActuatorParam;
import dji.common.mission.waypointv2.Action.WaypointIntervalTriggerParam;
import dji.common.mission.waypointv2.Action.WaypointReachPointTriggerParam;
import dji.common.mission.waypointv2.Action.WaypointTrajectoryTriggerParam;
import dji.common.mission.waypointv2.Action.WaypointTrigger;
import dji.common.mission.waypointv2.Action.WaypointV2Action;
import dji.common.mission.waypointv2.Action.WaypointV2AssociateTriggerParam;
import dji.common.mission.waypointv2.WaypointV2MissionTypes;

public class TestUtilsForWaypoint {

    public static void normalModel(Context context,DBHoverPoint hoverPoint, Waypoint waypoint) {

        if (hoverPoint.getUAVHeading() != null||!hoverPoint.getUAVHeading().equals("")) {
            float heading = Float.valueOf(hoverPoint.getUAVHeading());
            waypoint.heading = Math.round(heading);
        }
        if (hoverPoint.getSpeed()!= null&&!"0".equals(hoverPoint.getSpeed())){
            waypoint.speed = Float.valueOf(hoverPoint.getSpeed());
        }
//        if (hoverPoint.getGimbalPitch() != null) {
//            float gimbalPitch = Float.valueOf(hoverPoint.getGimbalPitch());
//            waypoint.gimbalPitch = Math.round(gimbalPitch);
//        }


        String rotationDirection = hoverPoint.getRotationDirection();
        WaypointTurnMode turnMode = WaypointTurnMode.CLOCKWISE;

        if ( "l".equals(rotationDirection )) {//左转
            turnMode = WaypointTurnMode.COUNTER_CLOCKWISE;
        } else if ("r".equals(rotationDirection )) {//右转
            turnMode = WaypointTurnMode.CLOCKWISE;
        }
        waypoint.turnMode = turnMode;

        addAction(context,hoverPoint, waypoint);


    }

    private static void addAction(Context context,DBHoverPoint hoverPoint, Waypoint waypoint) {
        ActionDao actionDao = new ActionDao(context);

        List<DbAction> actions = actionDao.seleActionByPid(hoverPoint.getHid());
        WaypointAction ss = null;

        if (actions != null) {
            boolean isFirstGimbal = false;
            for (int i = 0; i < actions.size(); i++) {
                if ("0".equals(actions.get(i).getType())) {//悬停点
                    if (Integer.parseInt(actions.get(i).getParam())>0){
                        ss = new WaypointAction(WaypointActionType.STAY, Integer.parseInt(actions.get(i).getParam()));
                        waypoint.addAction(ss);
                    }
                }else if ("1".equals(actions.get(i).getType())) {//云台
                    if (!isFirstGimbal) {
                        isFirstGimbal = true;
                        waypoint.gimbalPitch = Math.round(Math.round(Float.valueOf(actions.get(i).getParam())));
                    } else {
                        ss = new WaypointAction(WaypointActionType.GIMBAL_PITCH, Math.round(Float.valueOf(actions.get(i).getParam())));
                        waypoint.addAction(ss);
                    }
                }
                else if ("2".equals(actions.get(i).getType())) {//飞机
                    ss = new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, Math.round(Float.valueOf(actions.get(i).getParam())));
                    waypoint.addAction(ss);

                }else if ("3".equals(actions.get(i).getType())) {//拍照
                    int num = Integer.parseInt(actions.get(i).getParam());
                    if (num > 0) {
                        for (int j = 0; j < num; j++) {
                            ss = new WaypointAction(WaypointActionType.START_TAKE_PHOTO, 0);
                            waypoint.addAction(ss);
                        }
                    }
                }
            }
        }

    }


    public static List<WaypointV2Action> addAction2(Context context,DBHoverPoint hoverPoint, int position) {
        ArrayList<WaypointV2Action> waypointV2ActionList = new ArrayList<>();
        ActionDao actionDao = new ActionDao(context);
        List<DbAction> actions = actionDao.seleActionByPid(hoverPoint.getHid());
        if (actions != null) {
            WaypointTrigger waypointTrigger = null;
            WaypointActuator waypointActuator = null;
            int id = position*10;
            int end = position + 1;
            for (int i = 0; i < actions.size(); i++) {
                if ("0".equals(actions.get(i).getType())){//悬停点
                    waypointTrigger = configReachPointTrigger(position,1);
                    waypointActuator = configAircraftFlyActuator(true);
                } else
                if ("3".equals(actions.get(i).getType())) {//拍照
                    int num = Integer.parseInt(actions.get(i).getParam());
                    if (num > 0) {
                        for (int j = 0; j < num; j++) {
                            waypointTrigger = configAssociatePointTrigger(ActionTypes.AssociatedTimingType.AFTER_FINISHED, 0, id);
                            waypointActuator = configCameraActuator(ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO, 0,null, 0);
                            id++;
                            WaypointV2Action waypointAction = new WaypointV2Action.Builder()
                                    .setActionID(id)
                                    .setTrigger(waypointTrigger)
                                    .setActuator(waypointActuator)
                                    .build();
                            waypointV2ActionList.add(waypointAction);
                        }
                    }
                } else {
                    if ("1".equals(actions.get(i).getType())) {//云台
                        waypointTrigger = configTrajectoryPointTrigger(position, end);
                        waypointActuator = configGimbalActuator(ActionTypes.GimbalOperationType.AIRCRAFT_CONTROL_GIMBAL,0,RotationMode.RELATIVE_ANGLE, Math.round(Float.valueOf(actions.get(i).getParam())));

                    } else if ("2".equals(actions.get(i).getType())) {//飞机
                        waypointTrigger = configAssociatePointTrigger(ActionTypes.AssociatedTimingType.SIMULTANEOUSLY, 0, id);
                        waypointActuator = configAircraftYawActuator(WaypointV2MissionTypes.WaypointV2TurnMode.CLOCKWISE, true, Math.round(Float.valueOf(actions.get(i).getParam())));

                    }
//            if (i == myActions.size()-1){
//                waypointTrigger = configAssociatePointTrigger(ActionTypes.AssociatedTimingType.AFTER_FINISHED,0,actionId);
//                waypointActuator = configAircraftFlyActuator(true);
//            }
                    id++;
                    WaypointV2Action waypointAction = new WaypointV2Action.Builder()
                            .setActionID(id)
                            .setTrigger(waypointTrigger)
                            .setActuator(waypointActuator)
                            .build();
                    waypointV2ActionList.add(waypointAction);
                }


            }
        }
        return waypointV2ActionList;

    }


//    //  重复动作
//    public static void action4(ArrayList<WaypointV2Action> waypointV2ActionList, List<DbAction> actions, int position) {
//
//        WaypointTrigger waypointTrigger = null;
//        WaypointActuator waypointActuator = null;
//        int end = position + 1;
//        int id = position*10;
//        for (int i = 0; i < actions.size(); i++) {
////            if (i > 0) {
////                actionId = waypointV2ActionList.get(i - 1).getActionID();
////            }
////            if (myActions.get(i).getType() == "0"){//悬停点
////                waypointTrigger = configReachPointTrigger(position,1);
////                waypointActuator = configAircraftFlyActuator(false);
////            } else
//
//            if ("3".equals(actions.get(i).getType())) {//拍照
//                int num = Integer.parseInt(actions.get(i).getParam());
//                if (num > 0) {
//                    for (int j = 0; j < num; j++) {
//                        waypointTrigger = configIntervalPointTrigger(ActionTypes.ActionIntervalType.TIME, position, 3);
//                        waypointActuator = configCameraActuator(ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO,0, null, 0);
//                        id++;
//                        WaypointV2Action waypointAction = new WaypointV2Action.Builder()
//                                .setActionID(id)
//                                .setTrigger(waypointTrigger)
//                                .setActuator(waypointActuator)
//                                .build();
//                        waypointV2ActionList.add(waypointAction);
//                    }
//                }
//            }else {
//                if ("1".equals(actions.get(i).getType())) {//云台
//                    waypointTrigger = configReachPointTrigger(position, 1);
//                    waypointActuator = configGimbalActuator(ActionTypes.GimbalOperationType.ROTATE_GIMBAL, 0,RotationMode.ABSOLUTE_ANGLE, Math.round(Float.valueOf(actions.get(i).getParam())));
//
//                } else if ("2".equals(actions.get(i).getType())) {//飞机
//                    waypointTrigger = configReachPointTrigger(position, 1);
//                    waypointActuator = configAircraftYawActuator(WaypointV2MissionTypes.WaypointV2TurnMode.CLOCKWISE, true, Math.round(Float.valueOf(actions.get(i).getParam())));
//
//                }
////                else if ("3".equals(actions.get(i).getType())) {//拍照
////                    waypointTrigger = configIntervalPointTrigger(ActionTypes.ActionIntervalType.TIME, position, 3);
////                    waypointActuator = configCameraActuator(ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO, null, 0);
////
////                }
////            if (i == myActions.size()-1){
////                waypointTrigger = configReachPointTrigger(position,1);
////                waypointActuator = configAircraftFlyActuator(true);
////            }
//                id++;
//                WaypointV2Action waypointAction = new WaypointV2Action.Builder()
//                        .setActionID(id)
//                        .setTrigger(waypointTrigger)
//                        .setActuator(waypointActuator)
//                        .build();
//                waypointV2ActionList.add(waypointAction);
//            }
//
//        }
//    }
//
//    //REACH_POINT 当飞机到达航路点时，将触发该动作。
//    public static void action5(ArrayList<WaypointV2Action> waypointV2ActionList, List<DbAction> actions, int position) {
//
////
//        int id = position*10;
//        for (int i = 0; i < actions.size(); i++) {
//            if ("0".equals(actions.get(i).getType())) {//悬停点
//                WaypointTrigger waypointTrigger = configReachPointTrigger(position, 1);  //悬停
//                WaypointActuator waypointActuator = configAircraftFlyActuator(false);
//                id++;
//                WaypointV2Action waypointAction = new WaypointV2Action.Builder()
//                        .setActionID(id)
//                        .setTrigger(waypointTrigger)
//                        .setActuator(waypointActuator)
//                        .build();
//                waypointV2ActionList.add(waypointAction);
//
//                WaypointTrigger waypointTrigger1 = configAssociatePointTrigger(ActionTypes.AssociatedTimingType.AFTER_FINISHED, Integer.parseInt(actions.get(i).getParam()), id);
//                id++;
//                WaypointV2Action waypointAction1 = new WaypointV2Action.Builder()
//                        .setActionID(id)
//                        .setTrigger(waypointTrigger1)
//                        //.setActuator(waypointActuator)
//                        .build();
//                waypointV2ActionList.add(waypointAction1);
//
//            } else if ("3".equals(actions.get(i).getType())) { //拍照
//                int num = Integer.parseInt(actions.get(i).getParam());
//                if (num > 0) {
//                    WaypointTrigger waypointTrigger = null;
//                    WaypointActuator waypointActuator = null;
//                    for (int j = 0; j < num; j++) {
//                        waypointTrigger = configReachPointTrigger(position, 1);
//                        waypointActuator = configCameraActuator(ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO,0, null, 0);
//                        id++;
//                        WaypointV2Action waypointAction = new WaypointV2Action.Builder()
//                                .setActionID(id)
//                                .setTrigger(waypointTrigger)
//                                .setActuator(waypointActuator)
//                                .build();
//                        waypointV2ActionList.add(waypointAction);
//                    }
//                }
//            } else {
//                WaypointTrigger waypointTrigger = null;
//                WaypointActuator waypointActuator = null;
//                if ("1".equals(actions.get(i).getType())) {//云台
//
//                    waypointTrigger = configReachPointTrigger(position, 1);
//                    waypointActuator = configGimbalActuator(ActionTypes.GimbalOperationType.ROTATE_GIMBAL,0, RotationMode.ABSOLUTE_ANGLE, Math.round(Float.valueOf(actions.get(i).getParam())));
//                } else if ("2".equals(actions.get(i).getType())) {//飞机
//
//                    waypointTrigger = configReachPointTrigger(position, 1);
//                    waypointActuator = configAircraftYawActuator(WaypointV2MissionTypes.WaypointV2TurnMode.CLOCKWISE, true, Math.round(Float.valueOf(actions.get(i).getParam())));
//                }
//                id++;
//                WaypointV2Action waypointAction = new WaypointV2Action.Builder()
//                        .setActionID(id)
//                        .setTrigger(waypointTrigger)
//                        .setActuator(waypointActuator)
//                        .build();
//                waypointV2ActionList.add(waypointAction);
//            }
//        }
//    }


    /**
     * 当飞机到达航向点时，动作将被触发
     *
     * @param start 开始航点
     * @param count 航点个数
     * @return
     */
    public static WaypointTrigger configReachPointTrigger(int start, int count) {
        WaypointReachPointTriggerParam param = new WaypointReachPointTriggerParam.Builder()
                .setAutoTerminateCount(count)
                .setStartIndex(start)
                .build();
        WaypointTrigger WaypointReachPoint = new WaypointTrigger.Builder()
                .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
                .setReachPointParam(param)
                .build();
        return WaypointReachPoint;
    }

    /**
     * 关联action
     *
     * @param type     ActionTypes.AssociatedTimingType.AFTER_FINISHED  触发器在关联触发器完成后启动。ActionTypes.AssociatedTimingType.SIMULTANEOUSLY  	触发器以关联的触发器同时启动。
     * @param waitTime 等待时间
     * @param actionId 动作ID
     * @return
     */
    public static WaypointTrigger configAssociatePointTrigger(ActionTypes.AssociatedTimingType type, float waitTime, int actionId) {
        // ASSOCIATE   关联action 关联action
        WaypointV2AssociateTriggerParam param2 = new WaypointV2AssociateTriggerParam.Builder()
                .setAssociateType(type)
                .setWaitingTime(waitTime)
                .setAssociateActionID(actionId)
                .build();
        WaypointTrigger associatePoint = new WaypointTrigger.Builder()
                .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
                .setAssociateParam(param2)
                .build();

        return associatePoint;
    }

    /**
     * 此触发器仅适用于GIMBAL。触发此操作后，云台将以恒定的速度从起点航路点索引到终点航路点索引旋转。
     *
     * @param start 开始航点
     * @param end   结束航点
     * @return
     */
    public static WaypointTrigger configTrajectoryPointTrigger(int start, int end) {
        //TRAJECTORY  (此触发器仅适用于云台)
        WaypointTrajectoryTriggerParam param3 = new WaypointTrajectoryTriggerParam.Builder()
                .setEndIndex(end)
                .setStartIndex(start)
                .build();
        WaypointTrigger TrajectoryPoint = new WaypointTrigger.Builder()
                .setTriggerType(ActionTypes.ActionTriggerType.TRAJECTORY)
                .setTrajectoryParam(param3)
                .build();
        return TrajectoryPoint;
    }

    /**
     * 当飞机在两个航点之间飞行时，将触发该动作
     *
     * @param type  触发类型   相同的时间/距离重复此动作
     * @param start 开始索引
     * @param value 间隔时间/距离  时间 s   距离 m
     * @return
     */
    public static WaypointTrigger configIntervalPointTrigger(ActionTypes.ActionIntervalType type, int start, float value) {
//        ActionTypes.ActionIntervalType type = ActionTypes.ActionIntervalType.DISTANCE;// ActionTypes.ActionIntervalType.TIME;
        WaypointIntervalTriggerParam param4 = new WaypointIntervalTriggerParam.Builder()
                .setStartIndex(start)
                .setInterval(value)
                .setType(type)
                .build();
        WaypointTrigger IntervalPoint = new WaypointTrigger.Builder()
                .setTriggerType(ActionTypes.ActionTriggerType.SIMPLE_INTERVAL)
                .setIntervalTriggerParam(param4)
                .build();
        return IntervalPoint;
    }

    /**
     * 该动作将由控制飞机的偏航
     *
     * @param mode       设置方向  顺时针或逆时针 COUNTER_CLOCKWISE  逆时针        CLOCKWISE 顺时针
     * @param isRelative 设置是否相对
     * @param yaw        设置偏航角
     * @return
     */

    public static WaypointActuator configAircraftYawActuator(WaypointV2MissionTypes.WaypointV2TurnMode mode, boolean isRelative, float yaw) {

        WaypointAircraftControlRotateYawParam yawParam = new WaypointAircraftControlRotateYawParam.Builder()
                //  .setDirection(mode)
                .setRelative(isRelative)
                .setYawAngle(yaw)
                .build();
        WaypointAircraftControlParam controlParam = new WaypointAircraftControlParam.Builder()
                .setAircraftControlType(ActionTypes.AircraftControlType.ROTATE_YAW)
                .setRotateYawParam(yawParam)
                .build();
        WaypointActuator WaypointActuator = new WaypointActuator.Builder()
                .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                .setAircraftControlActuatorParam(controlParam)
                .build();

        return WaypointActuator;
    }

    /**
     * 该动作将由控制飞机停止飞行或开始飞行
     *
     * @param isStartFly 设置是否开始飞行
     * @return
     */

    public static WaypointActuator configAircraftFlyActuator(boolean isStartFly) {


        WaypointAircraftControlStartStopFlyParam startParam = new WaypointAircraftControlStartStopFlyParam.Builder()
                .setStartFly(isStartFly)
                .build();
        WaypointAircraftControlParam controlParam = new WaypointAircraftControlParam.Builder()
                .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
                .setFlyControlParam(startParam)
                .build();

        WaypointActuator WaypointActuator = new WaypointActuator.Builder()
                .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                .setAircraftControlActuatorParam(controlParam)
                .build();

        return WaypointActuator;
    }

    /**
     * 该动作将由相机执行
     *
     * @param type        类型   ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO
     * @param pointF      镜头对焦目标点  X和y的范围从0.0到1.0。点[0.0，0.0]表示屏幕的左上角.
     * @param FocalLength 设置变焦镜头的焦距 变焦镜头的焦距。有效范围是[getMinFocalLength, getMaxFocalLength]
     * @return
     */
    public static WaypointActuator configCameraActuator(ActionTypes.CameraOperationType type,int index, PointF pointF, int FocalLength) {


        WaypointCameraActuatorParam.Builder actuatorParam = new WaypointCameraActuatorParam.Builder();
        actuatorParam.setCameraOperationType(type);
        if (pointF != null) {
            WaypointCameraFocusParam focusParam = new WaypointCameraFocusParam.Builder()
                    .focusTarget(pointF)
                    .build();
            actuatorParam.setFocusParam(focusParam);
        }
//        Random random = new Random(1);
//        random.nextInt(RandomUtils.nextInt(50,300));
        if (FocalLength != 0) {
            WaypointCameraZoomParam zoomParam = new WaypointCameraZoomParam.Builder()
                    .setFocalLength(FocalLength)
                    .build();
            actuatorParam.setZoomParam(zoomParam);
        }
        //.setFocusParam(focusParam)
        // .setZoomParam(zoomParam)

        WaypointActuator WaypointActuator = new WaypointActuator.Builder()
                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                //.setActuatorIndex(index)
                .setCameraActuatorParam(actuatorParam.build())
                .build();

        return WaypointActuator;
    }

    /**
     * 该动作将由云台执行
     *
     * @param type  ROTATE_GIMBAL 旋转云台。仅在触发器类型为REACH_POINT时有效。（关联航点，到达航点时才旋转云台）     AIRCRAFT_CONTROL_GIMBAL   旋转云台。仅在触发器类型TRAJECTORY为时有效。（一段距离内云台旋转）
     * @param mode  RELATIVE_ANGLE  当万向盘旋转时，相对于当前角度的角度值。 ABSOLUTE_ANGLE 当万向架旋转时，相对于0度(飞机航向)的角度值。 SPEED 在速度模式下旋转万向架的俯仰、滚动和偏航。方向可以设置为顺时针方向，也可以是逆时针方向.
     * @param pitch 角度
     * @return
     */
    public static WaypointActuator configGimbalActuator(ActionTypes.GimbalOperationType type,int index, RotationMode mode, float pitch) {
        Rotation.Builder rotationBuilder = new Rotation.Builder();
        // rotationBuilder.roll(2);
        rotationBuilder.pitch(pitch);
        // rotationBuilder.yaw(2);
        rotationBuilder.mode(mode);//RotationMode.ABSOLUTE_ANGLE
        rotationBuilder.time(2.0);
        WaypointGimbalActuatorParam gimbalactuatorParam = new WaypointGimbalActuatorParam.Builder()
                .operationType(type)
                .rotation(rotationBuilder.build())
                .build();
        WaypointActuator WaypointActuator = new WaypointActuator.Builder()
                .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
                // .setActuatorIndex(index)
                .setGimbalActuatorParam(gimbalactuatorParam)
                .build();


        return WaypointActuator;
    }
}
