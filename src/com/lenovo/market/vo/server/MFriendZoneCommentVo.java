package com.lenovo.market.vo.server;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 评论
 * 
 * @author muqiang
 * 
 */
public class MFriendZoneCommentVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String topicId;
    private String content;//
    private String pid;
    private String type;
    private String createUser;
    private String createTime;
    private String loginUser;
    private ArrayList<MFriendZoneCommentVo> sanComments = new ArrayList<MFriendZoneCommentVo>();

    public MFriendZoneCommentVo(String topicId, String content, String pid, String type, String createUser, String createTime) {
        super();
        this.topicId = topicId;
        this.content = content;
        this.pid = pid;
        this.type = type;
        this.createUser = createUser;
        this.createTime = createTime;
    }

    public MFriendZoneCommentVo() {

    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getTopicId() {
        return this.topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ArrayList<MFriendZoneCommentVo> getSanComments() {
        return sanComments;
    }

    public void setSanComments(ArrayList<MFriendZoneCommentVo> sanComments) {
        this.sanComments = sanComments;
    }
}