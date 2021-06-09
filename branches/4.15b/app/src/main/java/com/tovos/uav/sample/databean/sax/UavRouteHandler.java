package com.tovos.uav.sample.databean.sax;

import com.tovos.uav.sample.databean.GeneratID;
import com.tovos.uav.sample.databean.sql.dao.ActionDao;
import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.bean.DbAction;
import com.tovos.uav.sample.databean.sql.bean.DbMediaPoint;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;
import com.tovos.uav.sample.databean.sql.dao.MedailDao;
import com.tovos.uav.sample.databean.sql.dao.PointDao;
import com.tovos.uav.sample.databean.sql.dao.TowerDao;
import com.tovos.uav.sample.databean.sql.dao.UavDao;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dji.midware.util.ContextUtil.getContext;

public class UavRouteHandler extends DefaultHandler {

    private DbUAVRoute uavRoute;

    private ArrayList<DbTower> mList;
    private DbTower tower;

    private ArrayList<DBHoverPoint> hoverPoints;
    private DBHoverPoint hoverPoint;

    private ArrayList<DbAction> actions ;
    private DbAction action;

    private ArrayList<DbMediaPoint> mediaPoints ;
    private DbMediaPoint mediaPoint;

    //声明一个字符串变量
    private String content;
    private String targ;
    private String type = "";
    private String path = "";
    private long uid;
    private long tid;
    private long hid;
    private long aid;
    private long mid;

    public DbUAVRoute getUavRoute(){
        return uavRoute;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 当SAX解析器解析到XML文档开始时，会调用的方法
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        mList = new ArrayList<>();
        hoverPoints = new ArrayList<>();
        actions = new ArrayList<>();
        mediaPoints = new ArrayList<>();
    }

    /**
     * 当SAX解析器解析到XML文档结束时，会调用的方法
     */
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /**
     * 当SAX解析器解析到某个属性值时，会调用的方法
     * 其中参数ch记录了这个属性值的内容
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        content = new String(ch, start, length);//.trim();

        if (length>0 &&getStringNoBlank(content).length() ==0){//sax在解析XML遇到换行符就会执行多次解析，此方法过滤换行符
            return;
        }
        setUavRouteData();
        setTowerData();
        setHoverPointData();
        setActionsData();
        setMediaPointData();
    }

    /**
     * 当SAX解析器解析到某个元素开始时，会调用的方法
     * 其中localName记录的是元素属性名
     */
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        targ = localName;

        if("UAVRoute".equals(localName)){
            mList.clear();
            uavRoute = new DbUAVRoute(); //新建对象
            type = "UAVRoute";
            uid = GeneratID.getGeneratID();
            uavRoute.setUid(uid);
            String name = getFileName(path);
            if (name!=null){
                uavRoute.setName(name);
            }

            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getLocalName(i).equals("version")){
                    uavRoute.setVersion(attributes.getValue(i));
                }
            }
        }

        if ("Tower".equals(localName)){
            hoverPoints.clear();
            tower = new DbTower();
            type = "Tower";
            tid = GeneratID.getGeneratID();
            tower.setTid(tid);
            tower.setPid(uid);
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getLocalName(i).equals("Name")){
                    tower.setName(attributes.getValue(i));
                }
            }
        }

        if ("HoverPoint".equals(localName)){
            actions.clear();
            mediaPoints.clear();
            hoverPoint = new DBHoverPoint();
            type = "HoverPoint";
            hid = GeneratID.getGeneratID();
            hoverPoint.setHid(hid);
            hoverPoint.setPid(tid);
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getLocalName(i).equals("Type")){
                    hoverPoint.setType(attributes.getValue(i));
                }
            }
        }

        if ("Action".equals(localName)){
            action = new DbAction();
            type = "Action";
            action.setPid(hid);
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getLocalName(i).equals("order")){
                    action.setOrder(attributes.getValue(i));
                }
            }
        }

        if ("MediaPoint".equals(localName)){
            mediaPoint = new DbMediaPoint();
            type = "MediaPoint";
            mediaPoint.setPid(hid);
        }
    }

    /**
     * 当SAX解析器解析到某个元素结束时，会调用的方法
     * 其中localName记录的是元素属性名
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);


        if("UAVRoute".equals(localName)){

            UavDao uavDao = new UavDao(getContext());
            uavDao.insert(uavRoute);
            TowerDao towerDao = new TowerDao(getContext());
            towerDao.insert(mList);
        }

        if ("Tower".equals(localName)){
            mList.add(tower);
            PointDao pointDao = new PointDao(getContext());
            pointDao.insert(hoverPoints);
        }


        if ("HoverPoint".equals(localName)){
            hoverPoints.add(hoverPoint);
            ActionDao actionDao = new ActionDao(getContext());
            actionDao.insert(actions);
            MedailDao medailDao = new MedailDao(getContext());
            medailDao.insert(mediaPoints);
        }

        if ("Action".equals(localName)){
            actions.add(action);
        }

        if ("MediaPoint".equals(localName)){
            mediaPoints.add(mediaPoint);
        }
    }


    /**
     * 解析写入多媒体参数
     */
    private void setMediaPointData(){
        if ("PhotoPointLocation".equals(targ)) {
            if (mediaPoint.getPhotoPointLocation()!=null){
                content = mediaPoint.getPhotoPointLocation()+content;
            }
            mediaPoint.setPhotoPointLocation(content);
        } else if ("DisplayName".equals(targ)) {
            if (mediaPoint.getDisplayName()!=null){
                content = mediaPoint.getDisplayName()+content;
            }
            mediaPoint.setDisplayName(content);
        } else if ("MediaName".equals(targ)) {
            if (mediaPoint.getMediaName()!=null){
                content = mediaPoint.getMediaName()+content;
            }
            mediaPoint.setMediaName(content);
        } else if ("Code".equals(targ)) {
            if (mediaPoint.getCode()!=null){
                content = mediaPoint.getCode()+content;
            }
            mediaPoint.setCode(content);
        }
    }

    /**
     * 解析写入动作参数
     */
    private void setActionsData(){
        if ("Type".equals(targ)) {
            if (action.getType()!=null){
                content = action.getType()+content;
            }
            action.setType(content);
        } else if ("Param".equals(targ)) {
            if (action.getParam()!=null){
                content = action.getParam()+content;
            }
            action.setParam(content);
        }
    }

    /**
     * 解析写入航点参数
     */
    private void setHoverPointData(){
        if ("Speed".equals(targ)) {
            if (hoverPoint.getSpeed()!=null){
                content = hoverPoint.getSpeed()+content;
            }
            hoverPoint.setSpeed(content);
        } else if ("HoverLocation".equals(targ)) {
            if (hoverPoint.getHoverLocation()!=null){
                content = hoverPoint.getHoverLocation()+content;
            }
            hoverPoint.setHoverLocation(content);
        } else if ("Number".equals(targ)) {
            if (hoverPoint.getNumber()!=null){
                content = hoverPoint.getNumber()+content;
            }
            hoverPoint.setNumber(content);
        } else if ("UAVHeading".equals(targ)) {
            if (hoverPoint.getUAVHeading()!=null){
                content = hoverPoint.getUAVHeading()+content;
            }
            hoverPoint.setUAVHeading(content);
        } else if ("GimbalPitch".equals(targ)) {
            if (hoverPoint.getGimbalPitch()!=null){
                content = hoverPoint.getGimbalPitch()+content;
            }
            hoverPoint.setGimbalPitch(content);
        } else if ("PicNumber".equals(targ)) {
            if (hoverPoint.getPicNumber()!=null){
                content = hoverPoint.getPicNumber()+content;
            }
            hoverPoint.setPicNumber(content);
        } else if ("RouteHeading".equals(targ)) {
            if (hoverPoint.getRouteHeading()!=null){
                content = hoverPoint.getRouteHeading()+content;
            }
            hoverPoint.setRouteHeading(content);
        } else if ("RotationDirection".equals(targ)) {
            if (hoverPoint.getRotationDirection()!=null){
                content = hoverPoint.getRotationDirection()+content;
            }
            hoverPoint.setRotationDirection(content);
        }
    }

    /**
     * 解析写入杆塔参数
     */
    private void setTowerData(){
        if ("TowerCode".equals(targ)) {
            if (tower.getTowerCode()!=null){
                content = tower.getTowerCode()+content;
            }
            tower.setTowerCode(content);
        }
    }

    /**
     * 解析写入航迹参数
     */
    private void setUavRouteData(){
        if("path".equals(targ)){
            if (uavRoute.getPath()!=null){
                content = uavRoute.getPath()+content;
            }
            uavRoute.setPath(content);
        }else if("Name".equals(targ)){

        }else if("CreatedTime".equals(targ)){
            if (uavRoute.getCreatedTime()!=null){
                content = uavRoute.getCreatedTime()+content;
            }
            uavRoute.setCreatedTime(content);
        }else if("Creator".equals(targ)){
            if (uavRoute.getCreator()!=null){
                content = uavRoute.getCreator()+content;
            }
            uavRoute.setCreator(content);
        }else if("Corporation".equals(targ)){
            if (uavRoute.getCorporation()!=null){
                content = uavRoute.getCorporation()+content;
            }
            uavRoute.setCorporation(content);
        }else if("UAVType".equals(targ)){
            if (uavRoute.getUAVType()!=null){
                content = uavRoute.getUAVType()+content;
            }
            uavRoute.setUAVType(content);
        }else if("CameraType".equals(targ)){
            if (uavRoute.getCameraType()!=null){
                content = uavRoute.getCameraType()+content;
            }
            uavRoute.setCameraType(content);
        }else if("RouteType".equals(targ)){
            if (uavRoute.getRouteType()!=null){
                content = uavRoute.getRouteType()+content;
            }
            uavRoute.setRouteType(content);
        }else if("BaseLocation".equals(targ)){
            if (uavRoute.getBaseLocation()!=null){
                content = uavRoute.getBaseLocation()+content;
            }
            uavRoute.setBaseLocation(content);
        }else if("Voltage".equals(targ)){
            if (uavRoute.getVoltage()!=null){
                content = uavRoute.getVoltage()+content;
            }
            uavRoute.setVoltage(content);
        }else if("LineName".equals(targ)){
            if (uavRoute.getLineName()!=null){
                content = uavRoute.getLineName()+content;
            }
            uavRoute.setLineName(content);
        }else if("SafetyDis".equals(targ)){
            if (uavRoute.getSafetyDis()!=null){
                content = uavRoute.getSafetyDis()+content;
            }
            uavRoute.setSafetyDis(content);
        }else if("ShootingDis".equals(targ)){
            if (uavRoute.getShootingDis()!=null){
                content = uavRoute.getShootingDis()+content;
            }
            uavRoute.setShootingDis(content);
        }
    }

    private  String getStringNoBlank(String str) {
        if(str!=null && !"".equals(str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            String strNoBlank = m.replaceAll("");
            return strNoBlank;
        }else {
            return str;
        }
    }

    public String getFileName(String pathandname){

        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return pathandname.substring(start+1,end);
        }else{
            return null;
        }

    }
}
