package com.tovos.uav.sample.route.waypoint;

import android.app.Activity;

public class WayPointFactory {

    public static CustomWayPoint create(String type, Activity activity, WapointListener listener){
        CustomWayPoint customWayPoint = null;
        switch (type) {
            case "waypoint":
                customWayPoint = new CustomWayPoint1(activity,listener);
                break;
            case "waypoint2.0":
                customWayPoint = new CustomWayPoint2(activity,listener);
                break;
            default:
                customWayPoint = new CustomWayPoint1(activity,listener);
                break;
        }
        return customWayPoint;
    }
}
