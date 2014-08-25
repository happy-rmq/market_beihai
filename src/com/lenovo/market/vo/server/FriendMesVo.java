package com.lenovo.market.vo.server;

import java.io.Serializable;

import android.text.TextUtils;

import com.lenovo.market.util.PinyinUtils;

public class FriendMesVo implements Comparable<FriendMesVo>, Serializable {

    private static final long serialVersionUID = 1L;
    private String friendId;// [好友id]
    private String friendAccount;// [好友账户]
    private String friendName;// [好友名称]
    private String picture;// [好友头像]
    private String avatarLocalPath;
    private String sex;// [好友性别]
    private String area;// [好友地区]
    private String sign;// [好友个性签名]
    private String group;// Cons.FRIEND_DEFAULT_GROUPNAME Cons.EXHIBITOR_GROUPNAME
    private String user;
    private String subscription; // 好友关系 to 等待验证 * from通过验证 * both 已添加 * none 加为好友
    private String py;// 好友名称拼音[ 排序和过滤使用，必选项 ]
    private String isFriend;// 是否为当前登录用户的好友
    private int friendType;// 1.普通朋友 2.公众账号
    private String state;// 未读状态
    private String initial;//首字母

    public FriendMesVo(String friendId, String friendAccount, String picture, String group, String friendName) {
        super();
        this.friendId = friendId;
        this.friendAccount = friendAccount;
        this.group = group;
        this.picture = picture;
        this.friendName = friendName;
        this.py = PinyinUtils.getPingYin(friendName);
        initial = getInitial(this.py);
    }

    private String getInitial(String str){
        String initial;
        char c = str.charAt(0);
        if(isAlphabet(c)){
            initial = str.substring(0,1);
        }else{
            initial = "#";
        }
        return initial;
    }

    public String getInitial() {
        if(TextUtils.isEmpty(this.initial)){
            return getInitial(getPy());
        }else{
            return initial;
        }
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public FriendMesVo(String account) {
        this.friendAccount = account;
    }

    public String getPy() {
        if (TextUtils.isEmpty(py)) {
            py = PinyinUtils.getPingYin(getFriendName());
        }
        return py;
    }

    public void setPy(String py) {
        this.py = py;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAvatarLocalPath() {
        return avatarLocalPath;
    }

    public void setAvatarLocalPath(String avatarLocalPath) {
        this.avatarLocalPath = avatarLocalPath;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(String friendAccount) {
        this.friendAccount = friendAccount;
    }

    public String getFriendName() {
        if (TextUtils.isEmpty(friendName)) {
            friendName = getFriendAccount();
        }
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(String isFriend) {
        this.isFriend = isFriend;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
    
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * 1.普通朋友 2.公众账号
     * 
     * @return
     */
    public int getFriendType() {
        return friendType;
    }

    /**
     * 1.普通朋友 2.公众账号
     * 
     * @param friendType
     */
    public void setFriendType(int friendType) {
        this.friendType = friendType;
    }

    @Override
    public int compareTo(FriendMesVo vo) {
        String str1 = PinyinUtils.getPingYin(this.getPy());
        String str2 = PinyinUtils.getPingYin(vo.getPy());
        char c1 = str1.charAt(0);
        char c2 = str2.charAt(0);
        boolean b1 = isAlphabet(c1);
        boolean b2 = isAlphabet(c2);
        if(b1 == b2){
            return str1.compareToIgnoreCase(str2);
        }else{
            return b1 ? -1 : 1;
        }
    }

    private boolean isAlphabet(char c) {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }
}
