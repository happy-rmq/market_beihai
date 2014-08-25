package com.lenovo.market.vo.server;

import java.util.ArrayList;

/**
 * 微信自定义菜单
 * 
 * @author Administrator
 * 
 */
public class MenuVo {

    private String type;
    private String name;
    private String key;
    private String url;
    private ArrayList<MenuVo> subMenus;
    private String keyword;
    private String parentid;
    private String empid;

    public MenuVo() {

    }

    public MenuVo(String type, String name, String key, String url, String keyword) {
        super();
        this.type = type;
        this.name = name;
        this.key = key;
        this.url = url;
        this.keyword = keyword;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public ArrayList<MenuVo> getSubMenus() {
        if (null == subMenus) {
            subMenus = new ArrayList<MenuVo>();
        }
        return subMenus;
    }

    public void setSubMenus(ArrayList<MenuVo> subMenus) {
        this.subMenus = subMenus;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
