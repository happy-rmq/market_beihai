package com.lenovo.market.vo.server;

import java.util.ArrayList;


/**
 * 服务器响应结果
 * 
 * @author muqiang
 */
public class ResultFriendVo {
    
    private ArrayList<MFriendZoneTopicVo> datas;
    private String result;

    public ArrayList<MFriendZoneTopicVo> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<MFriendZoneTopicVo> datas) {
        this.datas = datas;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
