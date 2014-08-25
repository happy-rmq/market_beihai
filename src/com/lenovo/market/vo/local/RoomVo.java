package com.lenovo.market.vo.local;

public class RoomVo {
    
    private String gid;//群组id
    private String roomId;
    private String loginUser;
    private int iskicked;
    private String name;// 房间名

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public int getIskicked() {
        return iskicked;
    }

    public void setIskicked(int iskicked) {
        this.iskicked = iskicked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
