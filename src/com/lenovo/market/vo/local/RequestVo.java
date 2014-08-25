package com.lenovo.market.vo.local;

import java.util.LinkedHashMap;

public class RequestVo {

    private String requestUrl;
    private String methodName;
    private String soapAction;
    private String nameSpace;
    private LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public LinkedHashMap<String, Object> getMaps() {
        return maps;
    }

    public void setMaps(LinkedHashMap<String, Object> maps) {
        this.maps = maps;
    }
}
