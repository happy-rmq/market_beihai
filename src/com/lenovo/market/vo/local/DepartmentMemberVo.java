package com.lenovo.market.vo.local;

import java.io.Serializable;

import android.text.TextUtils;

import com.lenovo.market.util.PinyinUtils;


public class DepartmentMemberVo implements Serializable,BusinessContactVo{
    
    private static final long serialVersionUID = 1L;
    private String id;// [成员id]
    private String name;// [姓名]
    private String account;// [账号]
    private String pic;// [头像]
    private String phonenum;// [电话号码]
    private String email;// [电子邮件]
    private String parentDepartmentId;// [父级部门id]
    private String isSync;//[是否为为服务平台用户   if isSync == 1 是 else 否]

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        if(TextUtils.isEmpty(name)){
            return account;
        }
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsSync() {
        return isSync;
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync;
    }

    @Override
    public int compareTo(BusinessContactVo another) {
        DepartmentMemberVo anotherMember = (DepartmentMemberVo) another;
        String str1 = PinyinUtils.getPingYin(this.getName());
        String str2 = PinyinUtils.getPingYin(anotherMember.getName());
        char c1 = str1.charAt(0);
        char c2 = str2.charAt(0);
        boolean b1 = isAlphabet(c1);
        boolean b2 = isAlphabet(c2);
        if(b1 == b2){
            return str1.compareToIgnoreCase(str2);
        }else{
            return b1 ? -1 : 1;
        }
    }
    
    private boolean isAlphabet(char c) {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }
}
