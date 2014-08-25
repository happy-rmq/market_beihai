package com.lenovo.market.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.text.TextUtils;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.vo.server.GraphicVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;

public class XMLUtil {

    public static String createXML(MsgXmlVo mVo, String msgType, String currentTimeMillis) {
        StringWriter xmlWriter = new StringWriter();
        try {
            // 使用工厂类XmlPullParserFactory的方式
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();

            xmlSerializer.setOutput(xmlWriter); // 保存创建的xml

            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xmlSerializer.startDocument("utf-8", null);

            xmlSerializer.startTag("", "xml");

            xmlSerializer.startTag("", "ToUserName");
            xmlSerializer.cdsect("");
            xmlSerializer.endTag("", "ToUserName");

            xmlSerializer.startTag("", "FromUserName");
            xmlSerializer.cdsect("");
            xmlSerializer.endTag("", "FromUserName");

            if (!TextUtils.isEmpty(currentTimeMillis)) {
                xmlSerializer.startTag("", "CreateTime");
                xmlSerializer.text(currentTimeMillis);
                xmlSerializer.endTag("", "CreateTime");
            }

            if (!TextUtils.isEmpty(mVo.getTargetType())) {
                xmlSerializer.startTag("", "TargetType");
                xmlSerializer.text(mVo.getTargetType());
                xmlSerializer.endTag("", "TargetType");
            }

            if (!TextUtils.isEmpty(msgType)) {
                xmlSerializer.startTag("", "MsgType");
                xmlSerializer.cdsect(msgType);
                xmlSerializer.endTag("", "MsgType");
            }

            if (msgType.equals("card")) {
                xmlSerializer.startTag("", "Title");
                xmlSerializer.cdsect(mVo.getTitle());
                xmlSerializer.endTag("", "Title");

                xmlSerializer.startTag("", "FriendId");
                xmlSerializer.cdsect(mVo.getFriendId());
                xmlSerializer.endTag("", "FriendId");

                xmlSerializer.startTag("", "Description");
                xmlSerializer.cdsect(mVo.getDescription());
                xmlSerializer.endTag("", "Description");

                xmlSerializer.startTag("", "PicUrl");
                xmlSerializer.cdsect(mVo.getPicUrl());
                xmlSerializer.endTag("", "PicUrl");

                xmlSerializer.startTag("", "MediaId");
                xmlSerializer.cdsect(mVo.getMediaId());
                xmlSerializer.endTag("", "MediaId");

            } else if (msgType.equals("image")) {
                xmlSerializer.startTag("", "PicUrl");
                xmlSerializer.cdsect(mVo.getPicUrl());
                xmlSerializer.endTag("", "PicUrl");

                xmlSerializer.startTag("", "MediaId");
                xmlSerializer.cdsect(mVo.getMediaId());
                xmlSerializer.endTag("", "MediaId");

            } else if (msgType.equals("text")) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");

            } else if (msgType.equals("voice")) {
                xmlSerializer.startTag("", "VoiceUrl");
                xmlSerializer.cdsect(mVo.getVoiceUrl());
                xmlSerializer.endTag("", "VoiceUrl");

                xmlSerializer.startTag("", "MediaId");
                xmlSerializer.cdsect(mVo.getMediaId());
                xmlSerializer.endTag("", "MediaId");

                xmlSerializer.startTag("", "Format");
                xmlSerializer.cdsect(mVo.getFormat());
                xmlSerializer.endTag("", "Format");

            } else if (msgType.equals("video")) {
                xmlSerializer.startTag("", "VideoUrl");
                xmlSerializer.cdsect(mVo.getVideoUrl());
                xmlSerializer.endTag("", "VideoUrl");

                xmlSerializer.startTag("", "MediaId");
                xmlSerializer.cdsect(mVo.getMediaId());
                xmlSerializer.endTag("", "MediaId");

            } else if (msgType.equals("location")) {
                xmlSerializer.startTag("", "Location_X");
                xmlSerializer.text(mVo.getLocation_X());
                xmlSerializer.endTag("", "Location_X");

                xmlSerializer.startTag("", "Location_Y");
                xmlSerializer.text(mVo.getLocation_Y());
                xmlSerializer.endTag("", "Location_Y");

                xmlSerializer.startTag("", "Scale");
                xmlSerializer.text(mVo.getScale());
                xmlSerializer.endTag("", "Scale");

                xmlSerializer.startTag("", "Label");
                xmlSerializer.cdsect(mVo.getLabel());
                xmlSerializer.endTag("", "Label");

            } else if (msgType.equals("link")) {
                xmlSerializer.startTag("", "Title");
                xmlSerializer.cdsect(mVo.getTitle());
                xmlSerializer.endTag("", "Title");

                xmlSerializer.startTag("", "Description");
                xmlSerializer.cdsect(mVo.getDescription());
                xmlSerializer.endTag("", "Description");

                xmlSerializer.startTag("", "Url");
                xmlSerializer.cdsect(mVo.getUrl());
                xmlSerializer.endTag("", "Url");
            } else if (msgType.equals("share")) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");

                xmlSerializer.startTag("", "Title");
                xmlSerializer.cdsect(mVo.getTitle());
                xmlSerializer.endTag("", "Title");

                xmlSerializer.startTag("", "PicUrl");
                xmlSerializer.cdsect(mVo.getPicUrl());
                xmlSerializer.endTag("", "PicUrl");

                xmlSerializer.startTag("", "Url");
                xmlSerializer.cdsect(mVo.getUrl());
                xmlSerializer.endTag("", "Url");
            } else if (msgType.equals("event")) {
                xmlSerializer.startTag("", "Event");
                xmlSerializer.cdsect(mVo.getEvent());
                xmlSerializer.endTag("", "Event");

                xmlSerializer.startTag("", "EventKey");
                xmlSerializer.cdsect(mVo.getEventKey());
                xmlSerializer.endTag("", "EventKey");
            }
            xmlSerializer.startTag("", "MsgId");
            xmlSerializer.text("123456789");
            xmlSerializer.endTag("", "MsgId");

            xmlSerializer.endTag("", "xml");
            xmlSerializer.endDocument();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return xmlWriter.toString();
    }

    public static String createXML(MsgXmlVo mVo, String msgType) {
        StringWriter xmlWriter = new StringWriter();
        try {
            // 使用工厂类XmlPullParserFactory的方式
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();

            xmlSerializer.setOutput(xmlWriter); // 保存创建的xml

            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xmlSerializer.startDocument("utf-8", null);

            xmlSerializer.startTag("", "xml");

            if (!TextUtils.isEmpty(mVo.getMsgType())) {
                xmlSerializer.startTag("", "MsgType");
                xmlSerializer.cdsect(mVo.getMsgType());
                xmlSerializer.endTag("", "MsgType");
            }

            if (!TextUtils.isEmpty(mVo.getTargetType())) {
                xmlSerializer.startTag("", "TargetType");
                xmlSerializer.text(mVo.getTargetType());
                xmlSerializer.endTag("", "TargetType");
            }

            if (!TextUtils.isEmpty(mVo.getCreateTime())) {
                xmlSerializer.startTag("", "CreateTime");
                xmlSerializer.text(mVo.getCreateTime());
                xmlSerializer.endTag("", "CreateTime");
            }

            if (msgType.equals("card")) {
                xmlSerializer.startTag("", "Title");
                xmlSerializer.cdsect(mVo.getTitle());
                xmlSerializer.endTag("", "Title");

                xmlSerializer.startTag("", "FriendId");
                xmlSerializer.cdsect(mVo.getFriendId());
                xmlSerializer.endTag("", "FriendId");

                xmlSerializer.startTag("", "Description");
                xmlSerializer.cdsect(mVo.getDescription());
                xmlSerializer.endTag("", "Description");

                xmlSerializer.startTag("", "PicUrl");
                xmlSerializer.cdsect(mVo.getPicUrl());
                xmlSerializer.endTag("", "PicUrl");

                xmlSerializer.startTag("", "MediaId");
                xmlSerializer.cdsect(mVo.getMediaId());
                xmlSerializer.endTag("", "MediaId");

            } else if (msgType.equals("image")) {
                xmlSerializer.startTag("", "PicUrl");
                xmlSerializer.cdsect(mVo.getPicUrl());
                xmlSerializer.endTag("", "PicUrl");

                xmlSerializer.startTag("", "MediaId");
                xmlSerializer.cdsect(mVo.getMediaId());
                xmlSerializer.endTag("", "MediaId");

            } else if (msgType.equals("text")) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");

            } else if (msgType.equals("voice")) {
                xmlSerializer.startTag("", "VoiceUrl");
                xmlSerializer.cdsect(mVo.getVoiceUrl());
                xmlSerializer.endTag("", "VoiceUrl");

                xmlSerializer.startTag("", "MediaId");
                xmlSerializer.cdsect(mVo.getMediaId());
                xmlSerializer.endTag("", "MediaId");

                xmlSerializer.startTag("", "Format");
                xmlSerializer.cdsect(mVo.getFormat());
                xmlSerializer.endTag("", "Format");

            } else if (msgType.equals("video")) {
                xmlSerializer.startTag("", "VideoUrl");
                xmlSerializer.cdsect(mVo.getVideoUrl());
                xmlSerializer.endTag("", "VideoUrl");

                xmlSerializer.startTag("", "MediaId");
                xmlSerializer.cdsect(mVo.getMediaId());
                xmlSerializer.endTag("", "MediaId");

            } else if (msgType.equals("location")) {
                xmlSerializer.startTag("", "Location_X");
                xmlSerializer.text(mVo.getLocation_X());
                xmlSerializer.endTag("", "Location_X");

                xmlSerializer.startTag("", "Location_Y");
                xmlSerializer.text(mVo.getLocation_Y());
                xmlSerializer.endTag("", "Location_Y");

                xmlSerializer.startTag("", "Scale");
                xmlSerializer.text(mVo.getScale());
                xmlSerializer.endTag("", "Scale");

                xmlSerializer.startTag("", "Label");
                xmlSerializer.cdsect(mVo.getLabel());
                xmlSerializer.endTag("", "Label");

            } else if (msgType.equals("link")) {
                xmlSerializer.startTag("", "Title");
                xmlSerializer.cdsect(mVo.getTitle());
                xmlSerializer.endTag("", "Title");

                xmlSerializer.startTag("", "Description");
                xmlSerializer.cdsect(mVo.getDescription());
                xmlSerializer.endTag("", "Description");

                xmlSerializer.startTag("", "Url");
                xmlSerializer.cdsect(mVo.getUrl());
                xmlSerializer.endTag("", "Url");
            } else if (msgType.equals("share")) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");

                xmlSerializer.startTag("", "Title");
                xmlSerializer.cdsect(mVo.getTitle());
                xmlSerializer.endTag("", "Title");

                xmlSerializer.startTag("", "PicUrl");
                xmlSerializer.cdsect(mVo.getPicUrl());
                xmlSerializer.endTag("", "PicUrl");

                xmlSerializer.startTag("", "Url");
                xmlSerializer.cdsect(mVo.getUrl());
                xmlSerializer.endTag("", "Url");
            } else if (msgType.equals(MarketApp.MESSAGETYPE_GROUPCHAT_LEAVEROOM)) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");

                xmlSerializer.startTag("", "Uid");
                xmlSerializer.cdsect(mVo.getFriendId());
                xmlSerializer.endTag("", "Uid");
            } else if (msgType.equals(MarketApp.MESSAGETYPE_GROUPCHAT_ADDMEMBER)) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");

                xmlSerializer.startTag("", "Uid");
                xmlSerializer.cdsect(mVo.getFriendId());
                xmlSerializer.endTag("", "Uid");
            } else if (msgType.equals(MarketApp.MESSAGETYPE_GROUPCHAT_KICKMEMBER)) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");

                xmlSerializer.startTag("", "Uid");
                xmlSerializer.cdsect(mVo.getFriendId());
                xmlSerializer.endTag("", "Uid");
            } else if (msgType.equals(MarketApp.MESSAGE_TIME)) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");
            } else if (msgType.equals(MarketApp.MESSAGE_NOTICE)) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");
            } else if (msgType.equals(MarketApp.MESSAGETYPE_GROUPINVITATION)) {
                xmlSerializer.startTag("", "Content");
                xmlSerializer.cdsect(mVo.getContent());
                xmlSerializer.endTag("", "Content");
            }

            xmlSerializer.startTag("", "MsgId");
            if (!TextUtils.isEmpty(mVo.getMsgId())) {
                xmlSerializer.cdsect(mVo.getMsgId());
            } else {
                xmlSerializer.cdsect("");
            }
            xmlSerializer.endTag("", "MsgId");

            xmlSerializer.endTag("", "xml");
            xmlSerializer.endDocument();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return xmlWriter.toString();
    }

    public static MsgXmlVo pullXMLResolve(String xml) {
        MsgXmlVo mVo = null;
        try {
            // 构建XmlPullParserFactory
            XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
            pullParserFactory.setNamespaceAware(true);
            // 获取XmlPullParser的实例
            XmlPullParser xmlPullParser = pullParserFactory.newPullParser();
            // 设置输入流 xml文件
            // xmlPullParser.setInput(new StringReader(xml));
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            xmlPullParser.setInput(is, "UTF-8");
            int index = -1;

            // 开始
            int eventType = xmlPullParser.getEventType();

            try {
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = xmlPullParser.getName();
                    switch (eventType) {
                    // 开始节点
                    case XmlPullParser.START_TAG:
                        if ("xml".equals(nodeName)) {
                            mVo = new MsgXmlVo();
                        } else if ("ToUserName".equals(nodeName)) {
                            mVo.setToUserName(xmlPullParser.nextText().trim());
                        } else if ("FromUserName".equals(nodeName)) {
                            mVo.setFromUserName(xmlPullParser.nextText().trim());
                        } else if ("CreateTime".equals(nodeName)) {
                            mVo.setCreateTime(xmlPullParser.nextText().trim());
                        } else if ("MsgType".equals(nodeName)) {
                            mVo.setMsgType(xmlPullParser.nextText().trim());
                        } else if ("Title".equals(nodeName)) {
                            if (mVo.getnVos().size() > 0) {
                                mVo.getnVos().get(index).setTitle(xmlPullParser.nextText().trim());
                            } else {
                                mVo.setTitle(xmlPullParser.nextText().trim());
                            }
                        } else if ("FriendId".equals(nodeName)) {
                            mVo.setFriendId(xmlPullParser.nextText().trim());
                        } else if ("TargetType".equals(nodeName)) {
                            mVo.setTargetType(xmlPullParser.nextText().trim());
                        } else if ("Description".equals(nodeName)) {
                            if (mVo.getnVos().size() > 0) {
                                mVo.getnVos().get(index).setDescription(xmlPullParser.nextText().trim());
                            } else {
                                mVo.setDescription(xmlPullParser.nextText().trim());
                            }
                        } else if ("PicUrl".equals(nodeName)) {
                            if (mVo.getnVos().size() > 0) {
                                mVo.getnVos().get(index).setPicUrl(xmlPullParser.nextText().trim());
                            } else {
                                mVo.setPicUrl(xmlPullParser.nextText().trim());
                            }
                        } else if ("MediaId".equals(nodeName)) {
                            mVo.setMediaId(xmlPullParser.nextText().trim());
                        } else if ("MsgId".equals(nodeName)) {
                            mVo.setMsgId(xmlPullParser.nextText().trim());
                        } else if ("Content".equals(nodeName)) {
                            mVo.setContent(xmlPullParser.nextText().trim());
                        } else if ("Format".equals(nodeName)) {
                            mVo.setFormat(xmlPullParser.nextText().trim());
                        } else if ("Location_X".equals(nodeName)) {
                            mVo.setLocation_X(xmlPullParser.nextText().trim());
                        } else if ("Location_Y".equals(nodeName)) {
                            mVo.setLocation_Y(xmlPullParser.nextText().trim());
                        } else if ("Scale".equals(nodeName)) {
                            mVo.setScale(xmlPullParser.nextText().trim());
                        } else if ("Label".equals(nodeName)) {
                            mVo.setLabel(xmlPullParser.nextText().trim());
                        } else if ("MusicUrl".equals(nodeName)) {
                            mVo.setMusicUrl(xmlPullParser.nextText().trim());
                        } else if ("HQMusicUrl".equals(nodeName)) {
                            mVo.setHQMusicUrl(xmlPullParser.nextText().trim());
                        } else if ("ThumbMediaId".equals(nodeName)) {
                            mVo.setThumbMediaId(xmlPullParser.nextText().trim());
                        } else if ("ArticleCount".equals(nodeName)) {
                            mVo.setArticleCount(xmlPullParser.nextText().trim());
                        } else if ("Url".equals(nodeName)) {
                            if (mVo.getnVos().size() > 0) {
                                mVo.getnVos().get(index).setUrl(xmlPullParser.nextText().trim());
                            } else {
                                mVo.setUrl(xmlPullParser.nextText().trim());
                            }
                        } else if ("VideoUrl".equals(nodeName)) {
                            mVo.setVideoUrl(xmlPullParser.nextText().trim());
                        } else if ("VoiceUrl".equals(nodeName)) {
                            mVo.setVoiceUrl(xmlPullParser.nextText().trim());
                        } else if ("Item".equals(nodeName)) {
                            GraphicVo mxvo = new GraphicVo();
                            index += 1;
                            mVo.getnVos().add(mxvo);
                        } else if ("Event".equals(nodeName)) {
                            mVo.setEvent(xmlPullParser.nextText().trim());
                        } else if ("Errcode".equals(nodeName)) {
                            mVo.setErrcode(xmlPullParser.nextText().trim());
                        } else if ("Errmsg".equals(nodeName)) {
                            mVo.setErrmsg(xmlPullParser.nextText().trim());
                        } else if ("Code".equals(nodeName)) {
                            mVo.getnVos().get(index).setCode(xmlPullParser.nextText().trim());
                        } else if ("Method".equals(nodeName)) {
                            mVo.getnVos().get(index).setMethod(xmlPullParser.nextText().trim());
                        } else if ("Uid".equals(nodeName)) {
                            mVo.setFriendId(xmlPullParser.nextText().trim());
                        }
                        break;
                    }
                    eventType = xmlPullParser.nextToken();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return mVo;
    }
}
