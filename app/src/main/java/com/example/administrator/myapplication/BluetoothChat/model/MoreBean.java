package com.example.administrator.myapplication.BluetoothChat.model;

/**
 * Created by 46404 on 2016/11/9.
 */

public class MoreBean {

    /**
     * 更多模块里的数据源
     */

    private String taskName; //业务名
    private int drawabeResourcecId; //图标本地资源id

    public MoreBean(String taskName, int drawabeResourcecId) {
        this.taskName = taskName;
        this.drawabeResourcecId = drawabeResourcecId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getDrawabeResourcecId() {
        return drawabeResourcecId;
    }

    public void setDrawabeResourcecId(int drawabeResourcecId) {
        this.drawabeResourcecId = drawabeResourcecId;
    }
}
