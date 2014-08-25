package com.lenovo.market.util;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.lenovo.market.vo.xmpp.VCardVo;

public class PullXml {
    public static VCardVo getVCard(InputStream xml) throws Exception { // 接收一个xml文件对象
        VCardVo vc = null;
        XmlPullParser parser = Xml.newPullParser(); // 利用Android的Xml工具类获取xmlPull解析器
        parser.setInput(xml, "UTF-8"); // 解析文件，设置字符集
        int event = parser.getEventType(); // 获取解析状态，返回的是int型数字状态
        while (event != XmlPullParser.END_DOCUMENT) { // 如果状态不是结束事件END_DOCUMENT，就递归
            switch (event) {
            case XmlPullParser.START_DOCUMENT: // 如果为开始解析头标签START_DOCUMENT，初始化数据
                vc = new VCardVo();
                break;

            case XmlPullParser.START_TAG: // 如果为开始解析属性START_TAG，则获取数据
                if ("uid".equals(parser.getName())) { // 如果要获取的数据在text中则调用nextText()方法获取
                    vc.setUid(parser.nextText());
                } else if ("picture".equals(parser.getName())) {
                    vc.setPicture(parser.nextText());
                } else if ("area".equals(parser.getName())) {
                    vc.setArea(parser.nextText());
                } else if ("FN".equals(parser.getName())) {
                    vc.setFN(parser.nextText());
                } else if ("account".equals(parser.getName())) {
                    vc.setAccount(parser.nextText());
                } else if ("userName".equals(parser.getName())) {
                    vc.setUserName(parser.nextText());
                } else if ("qrCode".equals(parser.getName())) {
                    vc.setQrCode(parser.nextText());
                } else if ("userFlag".equals(parser.getName())) {
                    vc.setUserFlag(parser.nextText());
                } else if ("userType".equals(parser.getName())) {
                    vc.setUserType(parser.nextText());
                }
                break;
            }
            event = parser.next(); // 让指针指向下一个节点
        }
        return vc;
    }
}
