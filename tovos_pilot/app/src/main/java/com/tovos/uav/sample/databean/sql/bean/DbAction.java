package com.tovos.uav.sample.databean.sql.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

@XStreamAlias("Action")@DatabaseTable(tableName = "Action")
public class DbAction implements Serializable {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "pid")
    private long pid;

    @XStreamAsAttribute()
    @XStreamAlias("order")@DatabaseField(columnName = "order")
    private String order;

    @DatabaseField(columnName = "Type")
    private String Type;

    @DatabaseField(columnName = "Param")
    private String Param;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getParam() {
        return Param;
    }

    public void setParam(String param) {
        this.Param = param;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }
}
