package com.lenovo.market.vo.server;

import java.io.Serializable;

/**
 * 用户
 *
 * @author muqiang
 */
public class UserVo implements Serializable {

    private static final long serialVersionUID = 6680466363552378049L;
    private String uid;
    private String account;
    private String userName;// 好友姓名
    private String password;
    private String phone;
    private String time;// 时间
    private String sign;// 个人说明
    private String picture;
    private String sex; // 性别
    private String area;
    private String qrCode;
    private String defaultServAccount;//平台账号
    private String defaultServId;//平台id
    private String companyId;//企业id(企业通讯录)
    private String servAccount;//企业账号(首页)
    private String servId;//企业id(首页)
    private String isColumn;//是否有轻应用模式

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUserName() {
        if (userName == null) {
            userName = account;
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserVo() {
    }

    public UserVo(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public UserVo(String account, String password, String sign, String picture, String sex, String area, String uid) {
        super();
        this.account = account;
        this.password = password;
        this.sign = sign;
        this.picture = picture;
        this.sex = sex;
        this.area = area;
        this.uid = uid;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDefaultServAccount() {
        return defaultServAccount;
    }

    public void setDefaultServAccount(String defaultServAccount) {
        this.defaultServAccount = defaultServAccount;
    }

    public String getDefaultServId() {
        return defaultServId;
    }

    public void setDefaultServId(String defaultServId) {
        this.defaultServId = defaultServId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getServAccount() {
        return servAccount;
    }

    public void setServAccount(String servAccount) {
        this.servAccount = servAccount;
    }

    public String getServId() {
        return servId;
    }

    public void setServId(String servId) {
        this.servId = servId;
    }

    public String getIsColumn() {
        return isColumn;
    }

    public void setIsColumn(String isColumn) {
        this.isColumn = isColumn;
    }
}
