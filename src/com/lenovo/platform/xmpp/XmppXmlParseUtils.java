/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lenovo.platform.xmpp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.TextUtils;


/**
 * XML解析工具类
 *
 * @author ck 0921
 */
public class XmppXmlParseUtils extends XmppUtils {
    public boolean DEBUG = true;
    private static XmppXmlParseUtils instance = null;
    public Hashtable<String, Object> userPhoneHash = null; // 用户注册时的手机号

    public static XmppXmlParseUtils getInstance() {
        if (null == instance) {
            instance = new XmppXmlParseUtils();
        }
        return instance;
    }

    private XmppXmlParseUtils() {
        userPhoneHash = new Hashtable<String, Object>();
    }

    /**
     * 获取XmlpullParser解析器实例
     *
     * @return
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    public XmlPullParser getXmlFactory() throws XmlPullParserException {
        // 构建XmlPullParserFactory
        XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();
        // 获取XmlPullParser的实例
        return pullFactory.newPullParser();
    }

    public XmlPullParser getXmlFactory(String xmlData)
            throws XmlPullParserException {
        // 构建XmlPullParserFactory
        XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();
        // 获取XmlPullParser的实例
        XmlPullParser xmlPullParser = pullFactory.newPullParser();
        ByteArrayInputStream bais = new ByteArrayInputStream(xmlData.getBytes());
        xmlPullParser.setInput(bais, "utf-8");
        return xmlPullParser;
    }

    public XmlPullParser getXmlFactory(byte[] xmlData)
            throws XmlPullParserException {
        // 构建XmlPullParserFactory
        XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();
        // 获取XmlPullParser的实例
        XmlPullParser xmlPullParser = pullFactory.newPullParser();
        ByteArrayInputStream bais = new ByteArrayInputStream(xmlData);
        xmlPullParser.setInput(bais, "utf-8");
        return xmlPullParser;
    }

    /**
     * 解析用户状态的变化
     *
     * @param xmlData
     */

    public void parserUserState(String xmlData) {
        if (TextUtils.isEmpty(xmlData)) {
            return;
        }
        try {
            //			String from = null;
            XmlPullParser xmlPullParser = getXmlFactory(xmlData);
            int xmlType = xmlPullParser.getEventType();
            while (xmlType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (xmlType) {
                    case XmlPullParser.START_TAG:
                        if (nodeName.equals("presence")) {
                            int count = xmlPullParser.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                String couNode = xmlPullParser.getAttributeName(i);
                                if (TextUtils.equals("from", couNode)) {
                                    //								from = xmlPullParser.getAttributeValue(i);
                                }
                            }
                        } else if (TextUtils.equals("x", nodeName)) { // vcard头像更新
                            String value = xmlPullParser.getAttributeValue(0);
                            if (TextUtils.equals("vcard-temp:x:update", value)) {// vcard更新，不包括头像变化
                                try {
                                    //								int index = from.lastIndexOf("/");
                                    //								String tempFrom = null;
                                    //								if (index == -1) {
                                    //									tempFrom = from;
                                    //								} else {
                                    //									tempFrom = from.substring(0, index);
                                    //								}
                                    //								VCard card = XmppFriendManager.getInstance().getUserVcard(tempFrom);
                                    //
                                    //							} catch (XMPPException e) {
                                    //								if (e.getXMPPError() != null)
                                    //									Logs.e(XmppXmlParseUtils.class, DEBUG,"load vcard error : "+ e.getXMPPError().getCode());
                                    //								else
                                    //									Logs.e(XmppXmlParseUtils.class,DEBUG,"load vcard error : "+ e.getMessage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (TextUtils.equals("http://jabber.org/protocol/muc#user", value)) { // 群里用户状态改变
                                //							int index = from.lastIndexOf("/");
                                //							String temp = from.substring(from.indexOf("@"),index);
                            }

                        } else if (TextUtils.equals("item", nodeName)) {
                            //						int itemCount = xmlPullParser.getAttributeCount();
                            //						String aff = null;
                            //						String role = null;
                            //						String jid = null;
                            //						for (int i = 0; i < itemCount; i++) {
                            //							String valueNode = xmlPullParser.getAttributeName(i);
                            //							if (TextUtils.equals(valueNode, "affiliation")) {
                            //								aff = xmlPullParser.getAttributeValue(i);
                            //							} else if (TextUtils.equals(valueNode, "role")) {
                            //								role = xmlPullParser.getAttributeValue(i);
                            //							} else if (TextUtils.equals(valueNode, "jid")) {
                            //								jid = xmlPullParser.getAttributeValue(i);
                            //							}
                            //						}
                        }
                        break;
                }
                xmlType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 房间邀请信息解析
     *
     * @param xmlData
     */
    public void parserRoomInvaiteMsg(String xmlData) {
        if (TextUtils.isEmpty(xmlData)) {
            return;
        }
        // XmlPullParser xmlPullParser;
        try {
            //			String roomForm = null;
            //			String roomInvateFormUser = null;
            //			String reason = null;
            //
            //			String roomSubject = null;
            //			String roomMember = null;
            //			String roomAvatar = null;
            // xmlPullParser = getXmlFactory();
            // ByteArrayInputStream bais = new ByteArrayInputStream(
            // xmlData.getBytes());
            // xmlPullParser.setInput(bais, "utf-8");

            XmlPullParser xmlPullParser = getXmlFactory(xmlData);

            int xmlType = xmlPullParser.getEventType();
            while (xmlType != XmlPullParser.END_DOCUMENT) {
                //				String nodeName = xmlPullParser.getName();
                switch (xmlType) {
                    case XmlPullParser.START_TAG:
                        //					if (nodeName.equals("message")) {
                        //						int count = xmlPullParser.getAttributeCount();
                        //						for (int i = 0; i < count; i++) {
                        //							String couNode = xmlPullParser.getAttributeName(i);
                        //							if (couNode.equals("from")) {
                        //								String roomForm = xmlPullParser.getAttributeValue(i);
                        //							}
                        //						}
                        //					} else if (nodeName.equals("invite")) {
                        //						String roomInvateFormUser = xmlPullParser.getAttributeValue(0);
                        //					} else if (nodeName.equals("reason")) {
                        //						String reason = xmlPullParser.nextText();
                        //					} else if (nodeName.equals("avatar")) {
                        //						String roomAvatar = xmlPullParser.nextText();
                        //					} else if (nodeName.equals("subject")) {
                        //						String roomSubject = xmlPullParser.nextText();
                        //					} else if (nodeName.equals("members")) {
                        //						String roomMember = xmlPullParser.nextText();
                        //					}
                        //					break;
                    case XmlPullParser.END_TAG:
                        //					if (nodeName.equals("message")) {
                        //						XmppRoomList.getInstance().receiveRoomInvaiteMsg(
                        //								roomForm, roomInvateFormUser, reason,
                        //								roomAvatar, roomSubject, roomMember);
                        //					}
                }
                xmlType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 解析用户是否在线状态
     *
     * @param xmlData
     * @return
     */
    public String parseUserState(byte[] xmlData) {
        String result = null;
        if (xmlData == null) {
            return null;
        }
        try {
            XmlPullParser xmlPullParser = getXmlFactory(xmlData);

            boolean done = false;
            while (!done) {
                int xmlType = xmlPullParser.next();
                String nodeName = xmlPullParser.getName();
                switch (xmlType) {
                    case XmlPullParser.START_TAG:
                        if (!TextUtils.isEmpty(nodeName) && nodeName.equals("show")) {
                            result = xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (nodeName.equals("presence")) {
                            done = true;
                        }
                        break;
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 解析监听到的系统通知
     *
     * @param xmlData
     */
    //	public void parserSystemMessage(String xmlData) {
    //		if (TextUtils.isEmpty(xmlData)) {
    //			return;
    //		}
    //		try {
    //			XmlPullParser xmlPullParser = getXmlFactory(xmlData);
    //
    //			int xmlType = xmlPullParser.getEventType();
    //			while (xmlType != XmlPullParser.END_DOCUMENT) {
    //				String nodeName = xmlPullParser.getName();
    //				switch (xmlType) {
    //				case XmlPullParser.START_DOCUMENT:
    //					break;
    //				case XmlPullParser.START_TAG:
    //					if (nodeName.equals("subject")) {
    //						String subj = xmlPullParser.nextText();
    //					} else if (nodeName.equals("body")) {
    //						String msgbody = xmlPullParser.nextText();
    //					}
    //					break;
    //				}
    //				xmlType = xmlPullParser.next();
    //			}
    //		} catch (Exception ex) {
    //		}
    //	}

    /**
     * 解析通过手机号获取登录帐号
     *
     * @param xmlData
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public HashMap<String, String> parserGetUserAccountByPhone(String xmlData)
            throws XmlPullParserException, IOException {
        XmlPullParser parser = getXmlFactory(xmlData);
        HashMap<String, String> map = new HashMap<String, String>();
        boolean done = false;
        while (!done) {
            int itemType = parser.next();
            String itemName = parser.getName();
            switch (itemType) {
                case XmlPullParser.START_TAG:
                    if (!TextUtils.isEmpty(itemName)) {
                        if (itemName.equals("item")) {
                            map.put("error", parser.nextText());
                        } else if (itemName.equals("phone_mobile")) {
                            map.put("phone_mobile", parser.nextText());
                        } else if (itemName.equals("username")) {
                            map.put("username", parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (itemName.equals("province")) {
                        done = true;
                    }
                    break;
            }
        }
        return map;
    }
}
