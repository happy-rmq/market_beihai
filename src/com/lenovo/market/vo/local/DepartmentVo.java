package com.lenovo.market.vo.local;

import java.util.ArrayList;


public class DepartmentVo implements BusinessContactVo {

    private String departmentId;// [部门id]
    private String name;// [部门名称]
    private String parentDepartmentId;// [父级部门id]
    private ArrayList<BusinessContactVo> members = new ArrayList<BusinessContactVo>();// [部门成员列表]

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentDepartmentId() {
        return parentDepartmentId;
    }

    public void setParentDepartmentId(String parentDepartmentId) {
        this.parentDepartmentId = parentDepartmentId;
    }

    public ArrayList<BusinessContactVo> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<BusinessContactVo> members) {
        this.members = members;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public int compareTo(BusinessContactVo another) {
        return 0;
    }
}
