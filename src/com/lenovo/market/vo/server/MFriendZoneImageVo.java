package com.lenovo.market.vo.server;

import java.io.Serializable;

/**
 * 朋友圈图片
 * 
 * @author muqiang
 * 
 */
public class MFriendZoneImageVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String topicId;// 分享id
    private String fileId;
    private String fileName;
    private String url;
    private String loginUser;

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /** default constructor */
    public MFriendZoneImageVo() {
    }

    /** minimal constructor */
    public MFriendZoneImageVo(String topicId) {
        this.topicId = topicId;
    }

    /** full constructor */
    public MFriendZoneImageVo(String topicId, String fileId, String fileName, String url) {
        this.topicId = topicId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.url = url;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicId() {
        return this.topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getFileId() {
        return this.fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}