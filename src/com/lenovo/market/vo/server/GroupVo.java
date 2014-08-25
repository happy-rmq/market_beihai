package com.lenovo.market.vo.server;

/**
 * 群组对象
 * @author Administrator
 *
 */
public class GroupVo implements java.io.Serializable{

    private static final long serialVersionUID = 1L;
    private String gid ;//
    private String name ;//圈子名称
    private String summary ;//圈子简介
    private String keyword ;//关键字
    private String notice ;//圈子公告
    private String type ;//圈子类别
    private String logoPath ;//圈子logo
    private String state ;//状态
    private String remark ;//备注
    private String createUser ;//创建人
    private String createTime ;//创建时间

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLogoPath() {
		return logoPath;
	}
	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
}
