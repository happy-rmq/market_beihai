package com.lenovo.market.vo.server;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 分享
 * 
 * @author muqiang
 * 
 */
public class MFriendZoneTopicVo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String id;
    private String content;// 内容
    private String setting;// 设置是否隐私
    private String isShare;// 是否分享
    private String shareTitle;// 分享标题
    private String shareUrl;// 分享地址
    private String createUser;// 创建人
    private String createTime;// 创建时间
    private String pageSize;
    private String currentPage;
    private String loginUser;
    private String createUserPic;

    private ArrayList<MFriendZoneCommentVo> comments = new ArrayList<MFriendZoneCommentVo>();
    private ArrayList<MFriendZoneImageVo> images = new ArrayList<MFriendZoneImageVo>();

    public ArrayList<MFriendZoneImageVo> getImages() {
        return images;
    }

    public void setImages(ArrayList<MFriendZoneImageVo> images) {
        this.images = images;
    }

    public ArrayList<MFriendZoneCommentVo> getComments() {
        return comments;
    }

    public void setComments(ArrayList<MFriendZoneCommentVo> comments) {
        this.comments = comments;
    }

    public String getCreateUserPic() {
        return createUserPic;
    }

    public void setCreateUserPic(String createUserPic) {
        this.createUserPic = createUserPic;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getIsShare() {
        return isShare;
    }

    public void setIsShare(String isShare) {
        this.isShare = isShare;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public MFriendZoneTopicVo(String content, String setting, String isShare, String shareTitle, String shareUrl, String createUser, String createTime, String loginUser) {
        super();
        this.content = content;
        this.setting = setting;
        this.isShare = isShare;
        this.shareTitle = shareTitle;
        this.shareUrl = shareUrl;
        this.createUser = createUser;
        this.createTime = createTime;
        this.loginUser = loginUser;
    }
}
