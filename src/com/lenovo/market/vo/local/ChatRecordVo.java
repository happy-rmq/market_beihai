package com.lenovo.market.vo.local;

import java.io.Serializable;

public class ChatRecordVo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int id;
    private String friendAccount;
    private String friendName;
    private String createTime;
    private int unreadcount;
    private String friendPic;
    private int friendType;
    private String content;
    private String loginUser;
    private String status;
    private String roomId;
    private String roomName;

    public ChatRecordVo() {
        super();
    }

    public ChatRecordVo(String friendAccount, String friendName, String createTime, int unreadcount, String friendPic, int friendType, String content, String loginUser, String status, String roomId, String roomName) {
        super();
        this.friendAccount = friendAccount;
        this.friendName = friendName;
        this.createTime = createTime;
        this.unreadcount = unreadcount;
        this.friendPic = friendPic;
        this.friendType = friendType;
        this.content = content;
        this.loginUser = loginUser;
        this.status = status;
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFriendAccount() {
        return friendAccount;
    }

    public void setFriendAccount(String friendAccount) {
        this.friendAccount = friendAccount;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getUnreadcount() {
        return unreadcount;
    }

    public void setUnreadcount(int unreadcount) {
        this.unreadcount = unreadcount;
    }

    public String getFriendPic() {
        return friendPic;
    }

    public void setFriendPic(String friendPic) {
        this.friendPic = friendPic;
    }

    public int getFriendType() {
        return friendType;
    }

    public void setFriendType(int friendType) {
        this.friendType = friendType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
