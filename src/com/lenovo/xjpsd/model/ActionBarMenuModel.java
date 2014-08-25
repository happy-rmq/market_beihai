package com.lenovo.xjpsd.model;

import java.util.ArrayList;

public class ActionBarMenuModel {

    private String id;
    private String pid;
    private int sort; // 排序
    private String name; // 名称
    private String url; // 链接
    private String level;
    private int isShortcut;// 0 普通 1 快捷
    private ArrayList<ActionBarMenuModel> columnVOs = new ArrayList<ActionBarMenuModel>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getIsShortcut() {
        return isShortcut;
    }

    public void setIsShortcut(int isShortcut) {
        this.isShortcut = isShortcut;
    }

    public ArrayList<ActionBarMenuModel> getColumnVOs() {
        return columnVOs;
    }

    public void setColumnVOs(ArrayList<ActionBarMenuModel> columnVOs) {
        this.columnVOs = columnVOs;
    }
}
