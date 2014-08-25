package com.lenovo.market.vo.xmpp;



/**
 * 组聊信息实体类
 * 
 * @author zhouyang
 * 
 */
public class MsgGroupVo {

    private String id;
    private String messageId;
    private String roomId;
    private String msgType;
    private String type;
    private String createTime;
    private String fromUserName;
    private String toUserName;
    private String content;
    private String loginUser;
    private String status;
    private String fromUserPic;// 发消息人的头像
    private String fromUserNom;// 发消息人的名字
    private MsgXmlVo xmlVo;

    public MsgGroupVo() {
        super();
    }

    public MsgGroupVo(String messageId, String roomId, String type, String createTime, String fromUserName, String toUserName, String content, String loginUser, String status, String msgType) {
        super();
        this.messageId = messageId;
        this.roomId = roomId;
        this.msgType = msgType;
        this.type = type;
        this.createTime = createTime;
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
        this.content = content;
        this.loginUser = loginUser;
        this.status = status;
    }

    public MsgXmlVo getXmlVo() {
        return xmlVo;
    }

    public void setXmlVo(MsgXmlVo xmlVo) {
        this.xmlVo = xmlVo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
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

    public String getFromUserPic() {
        return fromUserPic;
    }

    public void setFromUserPic(String fromUserPic) {
        this.fromUserPic = fromUserPic;
    }

    public String getFromUserNom() {
        return fromUserNom;
    }

    public void setFromUserNom(String fromUserNom) {
        this.fromUserNom = fromUserNom;
    }
}
