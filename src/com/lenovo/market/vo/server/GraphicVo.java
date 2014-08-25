package com.lenovo.market.vo.server;

/**
 * 图文对象
 * 
 * @author muqiang
 * 
 */
public class GraphicVo {

    private String Id;
    private String Title;
    private String Url;
    private String Description;
    private String PicUrl;
    private Long MessageId;
    private String Code;
    private String Method;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public Long getMessageId() {
        return MessageId;
    }

    public void setMessageId(Long messageId) {
        MessageId = messageId;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }
}
