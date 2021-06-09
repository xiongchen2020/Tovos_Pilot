package com.tovos.uav.sample.route.util;

import com.j256.ormlite.dao.Dao;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.tovos.uav.sample.MApplication;
import com.tovos.uav.sample.databean.sql.TovosDBOpenHelper;
import com.tovos.uav.sample.databean.TaskBean;
import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.bean.DbAction;
import com.tovos.uav.sample.databean.sql.bean.DbMediaPoint;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;

import static dji.midware.util.ContextUtil.getContext;


public class XMLUtil {

    private static void insertData(Class c,Object data)throws SQLException{
        TovosDBOpenHelper helper = new TovosDBOpenHelper(getContext());
        Dao dao =  helper.getUserDao(c);
        dao.create(data);
        helper.close();
    }


    public static void TaskToXML(TaskBean taskBean) {
        XStream xStream = new XStream();
        String s = xStream.toXML(taskBean);
        saveDatas(MApplication.picPath, taskBean.getName() + "_" + taskBean.getStatrtime() + "_" + taskBean.getEndtime() + ".trl", s);
    }

    public static TaskBean XMLToTask(File file, String path) {
        XStream xstream = new XStream(new Xpp3Driver());
        TaskBean taskBean = (TaskBean) xstream.fromXML(file);
        taskBean.setPath(path);
        return taskBean;
    }

    private static void saveDatas(String path, String name, String nr) {
        File saveFile = new File(path, name);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(saveFile);
            outStream.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>".getBytes());
            outStream.write("\n".getBytes());
            outStream.write(nr.getBytes());

            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static XStream getDbUAVXStream() {
        XStream xStream = new XStream(new Xpp3Driver());
        //将别名与xml名字对应
        xStream.alias("UAVRoute", DbUAVRoute.class);
        xStream.alias("Tower", DbTower.class);
        xStream.alias("HoverPoint", DBHoverPoint.class);
        xStream.alias("Action", DbAction.class);
        xStream.alias("MediaPoint", DbMediaPoint.class);
        xStream.useAttributeFor(DbUAVRoute.class, "version");
        xStream.useAttributeFor(DbTower.class, "Name");
        xStream.useAttributeFor(DBHoverPoint.class, "Type");
        xStream.useAttributeFor(DbAction.class, "order");
        return xStream;
    }

}
