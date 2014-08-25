/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lenovo.platform.xmpp;

import java.util.ArrayList;
import java.util.Iterator;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.content.Context;
import android.text.TextUtils;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.vo.local.ChildInfoVo;

/**
 * 好友管理类 ，修改此类请经我同意
 *
 * @author ck 0920
 */
public class XmppFriendManager extends XmppUtils {
    private static XmppFriendManager instance = null;

    public static XmppFriendManager getInstance() {
        if (null == instance) {
            instance = new XmppFriendManager();
        }
        return instance;
    }

    /**
     * 获取电话号码
     *
     * @throws XMPPException 取值时，调用XmppXmlParserUtils.getInstance().hash中取值;
     */
    public void sendInfoGetPhone() throws XMPPException {
        String getPhone = "<slookPersonalInfo xmlns=\"com:slook:PersonalInfo\"><action actionId=\"GET_PERSONALINFO\"></action></slookPersonalInfo>";
        IQ tem = getIq(getPhone);
        tem.setType(IQ.Type.SET);
        sendXmppMsg(getConnection(), tem);
    }

    /**
     * 更改用户名字
     *
     * @param recName 汉字名字，如：慕容云泽；
     * @throws XMPPException
     */
    public void sendInfoToChangeUserName(String recName) throws XMPPException {
        if (TextUtils.isEmpty(recName)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<slookPersonalInfo xmlns=\"com:slook:PersonalInfo\"><action actionId=\"UPDATE_PERSONALINFO\"><item name=\"");
        builder.append(recName);
        builder.append("\" /></action></slookPersonalInfo></iq>");
        IQ tem = getIq(builder.toString());
        tem.setType(IQ.Type.SET);

        // getConnection().sendPacket(tem);
        sendXmppMsg(getConnection(), tem);
    }

    /**
     * 保存VCARD（更改个人信息）
     *
     * @param vcard
     * @throws XMPPException
     */
    public void saveVcard(VCard vcard) throws XMPPException {
        if (null == vcard || null == getConnection()) {
            return;
        }
        if (null == getConnection() || !getConnection().isAuthenticated()) {
            return;
        }
        vcard.save(getConnection());
    }

    // /**
    // * 发送VCARD修改通知
    // *
    // * @author ck
    // * @date 2013-1-10 上午09:47:33
    // */
    // private void notifyVcardUpdate() {
    // Packet packet = new Packet() {
    // @Override
    // public String toXML() {
    // return "<presence><x xmlns=\"vcard-temp:x:update\"/></presence>";
    // }
    // };
    // sendXmppMsg(getConnection(), packet);
    // }

    /**
     * 获取登录用户的VCARD
     *
     * @param Vcard 一个VCARD的实例，里面包含用户的所有信息（不排除某些信息为空的情况）
     * @throws XMPPException
     */
    public VCard getMyVcard() throws XMPPException {
        if (null == getConnection()) {
            return null;
        }
        VCard vcard = new VCard();
        vcard.load(getConnection());
        return vcard;
    }

    /**
     * 获取用户的VCARD
     *
     * @param userJid 某一用户的JID;
     * @return Vcard 某一用户的VCARD；
     * @throws XMPPException
     */
    public VCard getUserVcard(String userJid) throws XMPPException {
        if (TextUtils.isEmpty(userJid) || null == getConnection()) {
            return null;
        }
        VCard vcard = new VCard();
        vcard.load(getConnection(), userJid);
        return vcard;
    }

    /**
     * 激活隐私列表
     *
     * @throws XMPPException
     */
    public void activatePrivacyList() throws XMPPException {
        IQ privacyIQ = getIq("<query xmlns=\"jabber:iq:privacy\"><active name=\"all-jid-example\"/></query>");
        privacyIQ.setType(IQ.Type.SET);
        privacyIQ.setFrom(getConnection().getUser());
        sendXmppMsg(getConnection(), privacyIQ);
    }

    /**
     * 对被封锁的用户发送自己下线状态
     *
     * @param uJid 将被封锁联络人的JID
     * @throws XMPPException
     */
    public void sendMsgOffLine(String uJid) throws XMPPException {
        if (TextUtils.isEmpty(uJid)) {
            return;
        }
        Presence msgPresence = new Presence(Presence.Type.unavailable);
        msgPresence.setFrom(getUser());
        msgPresence.setTo(uJid);

        // getConnection().sendPacket(msgPresence);
        sendXmppMsg(getConnection(), msgPresence);
    }

    // /**
    // * 从现有好友列表中读取所有被封锁或未封锁的联络人（目前好友列表已无封锁的联络人，要获取封锁联络人需要从数据库中获取）
    // *
    // * @param ct
    // * @param state
    // * 1：封锁，0未封锁
    // * @return
    // */
    // public ArrayList<ChildInfo> getAllUserStateForUserList(Context ct, int
    // state) {
    // ArrayList<ChildInfo> allman = new ArrayList<ChildInfo>();
    // FriendDbInterface friendDb = ChatMsgDBHelper.getInstance(ct);
    // for (int i = 0; i < ExpandableAdapter.groupInfoList.size(); i++) {
    // if (SlookConstant.MUTICHAT.equals(ExpandableAdapter.groupInfoList
    // .get(i).getName())) {
    // continue;
    // }
    // List<ChildInfo> allchild = ExpandableAdapter.groupInfoList.get(i)
    // .getChild();
    // for (int j = 0; j < allchild.size(); j++) {
    // int[] sx = friendDb.getIsFriendAndIsBlock(allchild.get(j)
    // .getUsername());
    // if (sx[1] == state) {
    // allman.add(allchild.get(j));
    // }
    // }
    // }
    // return allman;
    // }
    //

    /**
     * 搜索联络人
     *
     * @param user 关键字
     * @return 搜索后的所有结果
     * @throws XMPPException
     * @throws Exception
     */

    public ArrayList<ChildInfoVo> getSearchContactUser(String user, Context ct) throws XMPPException {
        if (TextUtils.isEmpty(user) || getConnection() == null) {
            return null;
        }
        // String loginUser = XmppUtils.getInstance().getUser();
        ArrayList<ChildInfoVo> allUserInfo = new ArrayList<ChildInfoVo>();
        UserSearchManager searchManager = new UserSearchManager(getConnection());

        Form searchForm = searchManager.getSearchForm("search." + MarketApp.OPENFIRE_SERVER);
        Form answerForm = searchForm.createAnswerForm();

        answerForm.setAnswer("Username", true);
        answerForm.setAnswer("Name", true);
        answerForm.setAnswer("Email", true);
        answerForm.setAnswer("search", user);

        ReportedData data = searchManager.getSearchResults(answerForm, "search." + MarketApp.OPENFIRE_SERVER);
        Iterator<Row> it = data.getRows();
        // ChildInfo infos = null;
        while (it.hasNext()) {
            Row row = it.next();

            String email = row.getValues("Email").next().toString();
            final String jid = row.getValues("jid").next().toString();

            String usName = jid;
            ChildInfoVo info = new ChildInfoVo();
            info.setEmail(email);
            info.setUsername(usName);
            info.setTemjid(jid);

            // int[] temp =
            // ChatMsgDBHelper.getInstance(ct).getIsFriendAndIsBlock(
            // usName);
            // ChildInfo infoState = ExpandableAdapter.theExpandableAdapter
            // .getChildByContact(usName);
            //
            // if (temp[0] == 1) {
            // info.setFriend(true);
            // if (null != infoState) {
            // info = infoState;
            // info.setState(infoState.getState());
            // }
            // infoState = null;
            // } else {
            // info.setFriend(false);
            // info.setState(Cons.USER_OFFLINE);
            // }
            // if (temp[1] == 1) {
            // info.setBlock(true);
            // info.setState(Cons.USER_BLOCK);
            // } else {
            // info.setBlock(false);
            // }
            //
            info = null;
        }
        return allUserInfo;
    }

    /**
     * 得到好友列表
     *
     * @param actionId
     * @throws XMPPException
     */
    public void getAddressBookFriend(String actionId) throws XMPPException {
        if (TextUtils.isEmpty(actionId)) {
            return;
        }

        IQ temIQ = getIq("<slookFriendRecommend xmlns=\"com:slook:slookFriendRecommend\"><action actionId=\"" + actionId + "\"/></slookFriendRecommend>");
        temIQ.setType(IQ.Type.GET);
        sendXmppMsg(getConnection(), temIQ);
    }

    /**
     * 查询用户性别
     *
     * @param userName 用户的USERNAME
     */
    public void checkUseSex(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<slookUpdateSerachSex xmlns=\"com:slook:UpdateSeachSex\"><action actionId=\"SLOOKGET_SEX\"><item username=\"");
        builder.append(userName);
        builder.append("\"/></action></slookUpdateSerachSex>");
        IQ temIQ = getIq(builder.toString());
        temIQ.setType(IQ.Type.SET);
        // getConnection().sendPacket(temIQ);
        sendXmppMsg(getConnection(), temIQ);
    }

    /**
     * 更改用户性别
     *
     * @param userName 用户的USERNAME
     */
    public void changeUseSex(String userSex) {
        if (TextUtils.isEmpty(userSex)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<slookUpdateSerachSex xmlns=\"com:slook:UpdateSeachSex\"><action actionId=\"SLOOKUPDATE_SEX\"><item sex=\"");
        builder.append(userSex);
        builder.append("\"/></action></slookUpdateSerachSex>");
        IQ temIQ = getIq(builder.toString());
        temIQ.setType(IQ.Type.SET);
        sendXmppMsg(getConnection(), temIQ);
    }

    /**
     * 通过电话和验证码添加好友
     *
     * @param phone
     * @param authcode
     */
    public void sendAuthCodeAndPhoneToCheckFriend(String phone, String authcode, String countCode) {
        // || TextUtils.isEmpty(authcode)
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(countCode)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<slookSearchUserByPhone xmlns=\"com:slook:slookSearchUserByPhone\"><action actionId=\"GETUSER_BYPHONE\"><item phone=\"");
        builder.append(phone);
        builder.append("\" countrycode=\"");
        builder.append(countCode);
        builder.append("\" authcode=\"");
        builder.append(authcode);
        builder.append("\" /></action></slookSearchUserByPhone>");
        // getConnection().sendPacket(getIq(builder.toString()));
        sendXmppMsg(getConnection(), getIq(builder.toString()));
    }

    /**
     * 自动添加为好友
     *
     * @param adduser 用户的JID
     */
    public void sendAutoAddFriend(String adduserJid) {
        if (TextUtils.isEmpty(adduserJid)) {
            return;
        }
        IQ temIQ = getIq("<presence  type=\"subscribe\"><priority>0</priority><properties xmlns=\"http://www.jivesoftware.com/" + "xmlns/xmpp/properties\"><property><name>description</name><value type=\"string\">auto</value></property></properties></presence>");
        temIQ.setTo(adduserJid);
        sendXmppMsg(getConnection(), temIQ);
    }

    /**
     * 通讯录自动添加为好友
     *
     * @author ck
     * @date 2013-1-21 下午08:34:08
     */
    public void sendContentAddress() {
        IQ temIQ = getIq("<slookAutoFriends xmlns=\"com:slook:AutoBeFriends\"><action actionId=\"AUTOBE_FRIENDS\" /></slookAutoFriends>");
        temIQ.setType(IQ.Type.GET);
        sendXmppMsg(getConnection(), temIQ);
    }

    /**
     * 得到用户的sip帐号
     *
     * @param userName 用户userName
     * @author ck
     * @date 2013-1-28 下午03:14:26
     */
    public void sendMsgToGetUserSipAccount(String userName) {
        StringBuilder builder = new StringBuilder();
        builder.append("<slookPersonalInfo xmlns=\"com:slook:PersonalInfo\"><action actionId=\"GET_SIPACCOUNT\"><item friendName=\"");
        builder.append(userName);
        builder.append("\"/></action></slookPersonalInfo>");
        IQ temIQ = getIq(builder.toString());
        sendXmppMsg(getConnection(), temIQ);
    }

    /**
     * 得到离线文件
     */
    public void getOfflineFile() {
        IQ tem = getIq("<slookFileOfflineTransfer xmlns=\"com:slook:slookFileOfflineTransfer\"><action actionId=\"GET_OFFLINEFILE\" /></slookFileOfflineTransfer>");
        tem.setType(IQ.Type.SET);
        sendXmppMsg(getConnection(), tem);
    }
}
