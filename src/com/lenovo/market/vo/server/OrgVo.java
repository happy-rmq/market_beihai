package com.lenovo.market.vo.server;

import java.util.ArrayList;


public class OrgVo implements java.io.Serializable{

    private static final long serialVersionUID = 1L;
    private String id;//机构ID
    private String code;//机构编码
    private String name;//机构名称
    private String pid;//上级
    private ArrayList<EmpUserVo> users;//机构所属用户
    private ArrayList<OrgVo> orgVOs;//下属机构

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public ArrayList<EmpUserVo> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<EmpUserVo> users) {
		this.users = users;
	}

	public ArrayList<OrgVo> getOrgVOs() {
		return orgVOs;
	}

	public void setOrgVOs(ArrayList<OrgVo> orgVOs) {
		this.orgVOs = orgVOs;
	}
}
