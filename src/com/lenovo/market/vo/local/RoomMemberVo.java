package com.lenovo.market.vo.local;

public class RoomMemberVo {
    
    private String roomId;
    private String memberId;
    private String account;
    private String userName;
    private String nickName;
    private String avatar;

    public RoomMemberVo() {
        super();
    }

    public RoomMemberVo(String room_id, String member_id, String account, String user_name, String nick_name, String avatar) {
        super();
        this.roomId = room_id;
        this.memberId = member_id;
        this.account = account;
        this.userName = user_name;
        this.nickName = nick_name;
        this.avatar = avatar;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
