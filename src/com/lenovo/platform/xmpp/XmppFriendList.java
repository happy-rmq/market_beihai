/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lenovo.platform.xmpp;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smackx.packet.VCard;

import android.text.TextUtils;

import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.activity.contacts.ContactsFragment;
import com.lenovo.market.activity.contacts.NewFriendsActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.dbhelper.NewFriendInfoDBHelper;
import com.lenovo.market.service.MainService;
import com.lenovo.market.util.ContactsUtils;
import com.lenovo.market.util.MyLogger;
import com.lenovo.market.util.PullXml;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.xmpp.VCardVo;

public class XmppFriendList extends XmppUtils {
    private static XmppFriendList instance = null;

    public static XmppFriendList getInstance() {
        if (null == instance) {
            instance = new XmppFriendList();

        }
        return instance;
    }

    /**
     * 通过用户名删除好友
     *
     * @param userName 用户名称
     * @return 成功与否
     * @throws XMPPException
     */
    public boolean deleteFriendByUserName(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return false;
        }

        try {
            Roster rost = getConnection().getRoster();
            RosterEntry deleteUser = rost.getEntry(userName);
            if (null != deleteUser) {
                rost.removeEntry(deleteUser);
                android.os.Message msg = new android.os.Message();
                msg.what = MainService.DELET_FRIEND_UPDATE_WEBSERVICE;
                msg.obj = userName;
                MainService.sHandler.sendMessage(msg);
                return true;
            }
        } catch (/* XMPP */Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 得到好友的单边 还是双边的情况 to 等待验证 from通过验证 both 已添加 none 加为好友 friendType
     *
     * @return
     */
    public ArrayList<FriendMesVo> getFriendType() {
        ArrayList<FriendMesVo> friendList = new ArrayList<FriendMesVo>();
        FriendMesVo friendVo;
        if (XmppUtils.getInstance().getConnection() == null) {
            return null;
        } else if (XmppUtils.getInstance().getConnection().getRoster() != null) {
            Roster roster = XmppUtils.getInstance().getConnection().getRoster();
            Collection<RosterEntry> entrys = roster.getEntries();
            for (RosterEntry entry : entrys) {
                Collection<RosterGroup> groups = entry.getGroups();
                if (groups.size() == 0) {
                    friendVo = new FriendMesVo(Utils.getUsernameFromJid(entry.getUser()));
                    friendVo.setSubscription(entry.getType().toString());
                    friendVo.setUser(entry.getUser().toString());
                    friendList.add(friendVo);
                    friendVo = null;
                }
                for (RosterGroup group : groups) {
                    if (!group.getName().equals(MarketApp.EXHIBITOR_GROUPNAME)) {
                        friendVo = new FriendMesVo(Utils.getUsernameFromJid(entry.getName()));
                        friendVo.setSubscription(entry.getType().toString());
                        friendVo.setUser(entry.getUser().toString());
                        friendList.add(friendVo);
                        friendVo = null;
                    }
                }
            }
            return friendList;
        } else {
            return null;
        }
    }

    /**
     * 获取用户的vcard信息
     *
     * @param connection
     * @param user
     * @return
     * @throws XMPPException
     */
    public static VCard getUserVCard(XMPPConnection connection, String user) throws XMPPException {
        VCard vcard = new VCard();
        vcard.load(connection, user);
        return vcard;
    }

    /**
     * 得到指定群组内成员数量
     *
     * @param groupName 组名
     * @return
     * @throws XMPPException
     */
    public int getGroupCountNumber(String groupName) throws XMPPException {
        if (null == getConnection()) {
            return 0;
        }
        return getConnection().getRoster().getGroup(groupName).getEntryCount();
    }

    /**
     * 修改好友备注名
     *
     * @param username    用户的UserName
     * @param displayname 修改后要显示的名字（即修改后的备注名） (备注名可以为空 by wsc)
     * @param groupName   所属群组
     * @return
     * @throws XMPPException
     */
    public boolean changeFriendDisplayname(final String username, String displayname, final String groupName) throws XMPPException {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(groupName)) {
            return false;
        }
        if (displayname == null) {// author by wsc the displayname is can be ""
            displayname = "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<query xmlns=\"jabber:iq:roster\">");
        sb.append("<item jid=\"");
        sb.append(username);
        sb.append("\" name=\"");
        sb.append(displayname);
        sb.append("\" subscription=\"both\" >");
        sb.append("<group>");
        sb.append(groupName);
        sb.append("</group>");
        sb.append("</item>");
        sb.append("</query>");
        IQ changeDisplayName = getIq(sb.toString());
        changeDisplayName.setType(Type.SET);
        // getConnection().sendPacket(changeDisplayName);
        sendXmppMsg(getConnection(), changeDisplayName);
        return true;
    }

    /**
     * 更换群组名
     *
     * @param srcName 原名字
     * @param dstName 更改后的名字
     * @return 成功与否
     * @throws XMPPException
     */
    public boolean changeGroupName(final String srcName, final String dstName) throws XMPPException {
        if (TextUtils.isEmpty(srcName) || TextUtils.isEmpty(dstName)) {
            return false;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<slookGroupOperation xmlns=\"com:slook:groupOperation\">");
        sb.append("<action actionId=\"CHANGE_GROUP_NAME\">");
        sb.append("<group newGroup=\"" + dstName + "\"" + " oldGroup=" + "\"" + srcName + "\"" + "/>");
        sb.append("</action>");
        sb.append("</slookGroupOperation>");

        IQ changeGroupIQ = getIq(sb.toString());
        changeGroupIQ.setType(Type.SET);
        PacketCollector collector = XmppUtils.getInstance().getConnection().createPacketCollector(new PacketIDFilter(changeGroupIQ.getPacketID()));
        // getConnection().sendPacket(changeGroupIQ);
        sendXmppMsg(getConnection(), changeGroupIQ);

        IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        if (result == null) {
            return false;
        } else if (result.getType() == IQ.Type.ERROR) {
            // throw new XMPPException(result.getError());
            return false;
        }
        return true;
    }

    /**
     * zl 向新建的群组添加联络人 相当于新建一个分组 <iq id="2mc04-45" type="set"><query xmlns="jabber:iq:roster"><item jid="wuqiubin004\5c40slook.cc@w-pc" name="wuqiubin004\40slook.cc" subscription="none" ask="subscribe"> <group>aaa</group><group>bbb</group></item></query></iq>
     */
    public boolean insertFirstFriendToGroup(final String username, final String oldGroup, final String distGroup, final String displayname) throws XMPPException {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(oldGroup) || TextUtils.isEmpty(distGroup)) {
            return false;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<query xmlns=\"jabber:iq:roster\">");
        sb.append("<item jid=\"");
        sb.append(username);
        sb.append("@");
        sb.append(MarketApp.OPENFIRE_SERVER);
        sb.append("/");
        sb.append(MarketApp.RESOURCE_ANDROID);
        sb.append("\" name=\"");
        sb.append(displayname);
        sb.append("\" subscription=\"both\" >");
        sb.append("<group>");
        sb.append(distGroup);
        sb.append("</group>");
        sb.append("</item>");
        sb.append("</query>");

        IQ addGroupIq = getIq(sb.toString());
        addGroupIq.setType(Type.SET);
        PacketCollector collector = getConnection().createPacketCollector(new PacketIDFilter(addGroupIq.getPacketID()));
        sendXmppMsg(getConnection(), addGroupIq);
        IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        if (result == null) {
            return false;
        } else if (result.getType() == IQ.Type.ERROR) {
            return false;
        }
        return true;
    }

    /**
     * 向群组中添加好友
     *
     * @param email SearchUserInfo中的email
     * @param group 将要添加的群组 注： FRIEND_DEFAULT_GROUPNAME 普通好友 EXHIBITOR_GROUPNAME 参展商公众帐号
     * @throws XMPPException
     */
    public boolean addFriendForGroup(String email, String group, String askValue) throws XMPPException {
        if (TextUtils.isEmpty(group) || TextUtils.isEmpty(email)) {
            return false;
        }
        String[] groups = {group};
        String tempjid = email;
        if (TextUtils.isEmpty(askValue)) {
            askValue = "";
        }
        XmppAddContact.getInstance().createEntry(tempjid, null, groups, askValue, false);
        return true;
    }

    /**
     * 创建一个好友分组（群组、类别）
     *
     * @param groupName 组名
     * @return 成功与否
     * @throws XMPPException
     */
    public boolean createFirendGroup(String groupName) throws XMPPException {
        if (null == getConnection() || TextUtils.isEmpty(groupName)) {
            return false;
        }
        RosterGroup rgoup = getConnection().getRoster().createGroup(groupName);
        if (null != rgoup) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取所有群组数量
     *
     * @return
     * @throws XMPPException
     */
    public int getAllRosterGroupSize() throws XMPPException {
        if (null == getConnection()) {
            return 0;
        }
        return getConnection().getRoster().getGroupCount();
    }

    /**
     * 获取所有群组名
     *
     * @return
     * @throws XMPPException
     */
    public ArrayList<String> getAllRosterGroupName() throws XMPPException {
        Collection<RosterGroup> allGroup = getConnection().getRoster().getGroups();
        Iterator<RosterGroup> rgoup = allGroup.iterator();
        ArrayList<String> allName = new ArrayList<String>();
        while (rgoup.hasNext()) {
            RosterGroup rg = rgoup.next();
            allName.add(rg.getName());
        }
        return allName;
    }

    /**
     * 删除一个群组
     *
     * @param groupName 组名
     * @throws XMPPException
     */
    public boolean deleteFriendGroup(String groupName) throws XMPPException {
        boolean isDele = false;
        Collection<RosterGroup> getGroup = getConnection().getRoster().getGroups();
        for (RosterGroup rosterGroup : getGroup) {
            if (rosterGroup.getName().equals(groupName)) {
                if (rosterGroup.getEntryCount() == 0) {
                    // getConnection().getRoster().removeGroup(srcName);
                    getGroup.remove(groupName);
                    isDele = true;
                    break;
                }
            }
        }
        return isDele;
    }

    /**
     * 解析花名册
     *
     * @throws XMPPException
     */
    public void parserRoster(boolean isOnlyAddRosterListener) throws XMPPException {
        final Roster roster = getConnection().getRoster();
        if (null == roster) {
            return;
        }
        roster.addRosterListener(new RosterListener() {

            @Override
            public void presenceChanged(Presence presence) {
                /** presence监听在xmpputils中已经有了 */
                XmppXmlParseUtils.getInstance().parserUserState(presence.toXML());
            }

            @Override
            public void entriesUpdated(Collection<String> addresses) {
                System.out.println("entriesUpdated");
                for (String name : addresses) {

                    RosterEntry entry = roster.getEntry(name);
                    if (entry == null) {
                        return;
                    }
                    MyLogger.commLog().e("name : " + name + ", type : " + entry.getType());
                    Collection<RosterGroup> groups = entry.getGroups();
                    boolean isInFriendsGroup = false;
                    for (RosterGroup group : groups) {
                        if (group.getName().equals(MarketApp.FRIEND_DEFAULT_GROUPNAME)) {
                            isInFriendsGroup = true;
                            break;
                        }
                    }

                    if (entry.getType().toString().equals("both")) { // 当正式的确定好友关系时
                        if (isInFriendsGroup) {
                            android.os.Message sucMsg = new android.os.Message();
                            sucMsg.what = MainService.ADD_FRIEND_SUC_UPDATE_SERVER;
                            sucMsg.obj = name;
                            MainService.sHandler.sendMessage(sucMsg);
                            // 当好友关系改变时添加好友到通讯录中,刷新好友列表
                            if (ContactsFragment.handler != null) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ContactsFragment.handler.obtainMessage(MarketApp.HANDLERMESS_ONE).sendToTarget();
                            }
                        }
                    }
                }
            }

            @Override
            public void entriesDeleted(Collection<String> addresses) {
                System.out.println("entriesDeleted");
                for (String name : addresses) {
                    FriendInfoDBHelper fHelper = new FriendInfoDBHelper();

                    name = name.substring(0, name.indexOf("@"));
                    FriendMesVo friend = fHelper.getFriend(name);

                    if (friend != null) {
                        ContactsUtils.deleteFriend(friend, true);
                        if (null != NewFriendsActivity.handler && null != NewFriendsActivity.newFriendAll) {
                            for (int i = 0; i < NewFriendsActivity.newFriendAll.size(); i++) {
                                if (NewFriendsActivity.newFriendAll.get(i).getFriendAccount().equals(friend.getFriendAccount())) {
                                    NewFriendsActivity.newFriendAll.remove(i);
                                    break;
                                }
                            }
                            NewFriendsActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ONE);
                        }
                    }
                }
            }

            @Override
            public void entriesAdded(Collection<String> addresses) {
                System.out.println("entriesAdded");
                for (String name : addresses) {
                    if (MarketApp.sendAddFriend) {
                        MarketApp.sendAddFriend = false;
                        return;
                    }
                    name = name.substring(0, name.indexOf("@"));
                    ArrayList<FriendMesVo> fVos = getFriendType();
                    for (int i = 0; i < fVos.size(); i++) {
                        if (fVos.get(i).getFriendAccount().equals(name)) {
                            Utils.Vibrate(300);
                            try {
                                VCard userVCard = XmppFriendList.getUserVCard(connection, fVos.get(i).getUser());
                                ByteArrayInputStream bis = new ByteArrayInputStream(userVCard.toString().getBytes());
                                VCardVo vCard = PullXml.getVCard(bis);
                                FriendMesVo fVo = new FriendMesVo(name);
                                fVo.setFriendName(vCard.getUserName());
                                fVo.setPicture(vCard.getPicture());
                                fVo.setArea(vCard.getArea());
                                fVo.setState("1");
                                fVo.setSubscription("from");
                                NewFriendInfoDBHelper newFriendDB = new NewFriendInfoDBHelper();
                                newFriendDB.insertNewFriend(MarketApp.app, fVo);
                                if (null != ViewPaperMenuActivity.handler) {
                                    ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_FOUR);
                                }
                                if (null != ContactsFragment.handler) {
                                    ContactsFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);
                                }
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    MarketApp.sendAddFriend = false;
                    MyLogger.commLog().i(name);
                    RosterEntry entry = roster.getEntry(name);
                    if (entry == null) {
                        return;
                    }
                }
            }
        });
        if (!isOnlyAddRosterListener) {

            Collection<RosterGroup> groups = roster.getGroups();
            HashMap<String, String> rosterList = new HashMap<String, String>();
            rosterList.clear();
            for (RosterGroup group : groups) {
                RosterGroup rosterGroup = roster.getGroup(group.getName());
                Collection<RosterEntry> entries = rosterGroup.getEntries();
                for (final RosterEntry entry : entries) {
                    // ItemType type = entry.getType(); 判斷當前這個聯絡人和自己之間的關係類型
                    // 如果不加判斷這個
                    // 就會出現單邊好友的情況

                    ItemType type = entry.getType();
                    if ("both".equals(type.toString())) {
                        // LoginUtils.friendDbHelper.saveFriendname(Utils.getJidToUserName(entry.getUser()),group.getName());
                        String displayname = entry.getName();
                        if (TextUtils.isEmpty(displayname) || displayname.contains(MarketApp.OPENFIRE_SERVER_NAME)) {
                            displayname = null;
                        }
                        if (!TextUtils.isEmpty(group.getName())) {

                            // Look.friendHelper.saveRoster(
                            // Utils.getJidToUserName(entry.getUser()),
                            // displayname, group.getName());
                        }
                    } else if (entry != null && (entry.getType().toString().equals("to") || (entry.getType().toString().equals("from")))) {

                        rosterList.put(entry.getName(), entry.getType().toString());

                    }

                }
            }
            // if(SearchFriendByNumberActivity.mHandler!=null){
            // android.os.Message msg=new android.os.Message();
            // msg.what=Cons.GET_NEW_FRIEND_LIST;
            // msg.obj=rosterList;
            // SearchFriendByNumberActivity.mHandler.sendMessage(msg);
            //
            // }

        }
    }

    public void addFriend(String form) {
        Presence response = new Presence(Presence.Type.subscribed);
        response.setTo(form);
        response.setMode(Mode.chat);
        sendXmppMsg(getConnection(), response);

        Presence response2 = new Presence(Presence.Type.available);
        response2.setTo(form);
        response2.setMode(Mode.chat);
        sendXmppMsg(getConnection(), response2);

        // Roster roster = XmppUtils.getInstance().getConnection().getRoster();
        // RosterEntry entry = roster.getEntry(form);
        // ChildInfo info = saveNewAddFriend(entry, form);
        sendUserOnlineState(Presence.Mode.chat, form);

    }

    // public ChildInfo saveNewAddFriend(RosterEntry entry, String username) {
    // String groupName = null;
    // Collection<RosterGroup> listGroup = entry.getGroups(); // 更新好友列表
    // for (RosterGroup group : listGroup) {
    // groupName = group.getName();
    // }
    // if (TextUtils.isEmpty(groupName)) {
    // groupName = Cons.FRIEND_DEFAULT_GROUPNAME;
    // }
    // FriendVo friend = new FriendVo();
    //
    // return friend.getChildInfo();
    //
    // }

    /**
     * 第一次登录 保存所有好友的信息
     *
     * @throws XMPPException
     */
    public void saveFriendInfoAll() throws XMPPException {
        Roster roster = getConnection().getRoster();

        Collection<RosterGroup> groups = roster.getGroups();
        for (RosterGroup group : groups) {
            // Look.friendHelper.saveFriendGroup(group.getName());
            RosterGroup rosterGroup = roster.getGroup(group.getName());
            Collection<RosterEntry> entries = rosterGroup.getEntries();

            for (final RosterEntry entry : entries) {

                if (entry.getType() != null && "both".equals(entry.getType().toString())) {

                    try {

                        // VCard tVcard =
                        // XmppFriendManager.getInstance().getUserVcard(entry.getUser());

                    } catch (Exception e) {
                        // LoginUtils.friendDbHelper.saveFriendname(entry.getUser(),
                        // wsc group.getName());
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    /**
     * 通过用户名保存用户信息
     *
     * @param userjid
     */
    public void saveFriendinfoByUserName(String userjid) {
        if (TextUtils.isEmpty(userjid)) {
            return;
        }
        try {
            // VCard tVcard =
            // XmppFriendManager.getInstance().getUserVcard(userjid);

        } catch (Exception e) {
            // wsc LoginUtils.friendDbHelper.saveFriendname(userjid, null);
            e.printStackTrace();
        }
    }

    // /**
    // * 获取封锁联络人
    // *
    // * @return List<String> 为被封锁的联络人表
    // * @throws XMPPException
    // * 若异常ID为404,则表示没有封锁的联络人
    // */
    // public List<String> getBlockadeContact() throws XMPPException {
    // return PrivacyProxy.getInstance().getAllBlock();
    // }
}
