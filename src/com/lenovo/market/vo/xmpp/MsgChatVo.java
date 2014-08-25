package com.lenovo.market.vo.xmpp;



/**
 * 用于单聊、公众账号
 * 
 * @author muqiang
 * 
 */
public class MsgChatVo {
    
    private String id;
    private String msgType;
    private String type;
    private String createTime;
    private String fromUserName;
    private String toUserName;
    private String content;
    private String loginUser;
    private String status;// 消息的状态
    
    private MsgXmlVo xmlVo;
    private String friendPic;

    public MsgChatVo() {
        super();
    }

    public MsgChatVo(String type, String createTime, String fromUserName, String toUserName, String content, String loginUser, String status,String msgType) {
        super();
        this.msgType = msgType;
        this.type=type;
        this.createTime = createTime;
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
        this.content = content;
        this.loginUser = loginUser;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public MsgXmlVo getXmlVo() {
        return xmlVo;
    }

    public void setXmlVo(MsgXmlVo xmlVo) {
        this.xmlVo = xmlVo;
    }

    public String getFriendPic() {
        return friendPic;
    }

    public void setFriendPic(String friendPic) {
        this.friendPic = friendPic;
    }
}
