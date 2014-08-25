/**
 *
 */
package com.lenovo.platform.xmpp;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;

import android.text.TextUtils;

import com.lenovo.market.util.Utils;

/**
 * @author ck 1015 添加好友的所有操作 ；
 * @author 改代码请经过我的同意，并加上注释，修改人 ，修改时间
 */
public class XmppAddContact extends XmppUtils {
    private static XmppAddContact instance = null;

    public static XmppAddContact getInstance() {
        if (null == instance) {
            instance = new XmppAddContact();
        }
        return instance;
    }

    /**
     * 邀请人向被邀请人发出一个添加好友的消息
     *
     * @param user      被邀请人的email
     * @param name      被邀请人的昵称
     * @param groups    要添加的群组
     * @param callMsg   招呼消息
     * @param isInvaite 该数据是否为邀请
     * @throws XMPPException
     * @author ck
     * @date 2012-12-28 上午10:44:19
     */
    public void createEntry(String user, String name, String[] groups, String callMsg, boolean isInvaite) throws XMPPException {
        if (TextUtils.isEmpty(user) || groups == null || groups.length == 0) {
            return;
        }
        // ----------- 加上昵称

        name = user.substring(0, user.indexOf("@"));
        // -------------
        RosterPacket rosterPacket = new RosterPacket();
        rosterPacket.setType(IQ.Type.SET);
        RosterPacket.Item item = new RosterPacket.Item(user, name);
        if (groups != null) {
            for (String group : groups) {
                if (!TextUtils.isEmpty(group)) {
                    item.addGroupName(group);
                }
            }
        }
        rosterPacket.addRosterItem(item);
        PacketCollector collector = getConnection().createPacketCollector(new PacketIDFilter(rosterPacket.getPacketID()));
        sendXmppMsg(getConnection(), rosterPacket);
        IQ response = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        if (response == null) {
            throw new XMPPException("No response from the server.");
        } else if (response.getType() == IQ.Type.ERROR) {
            throw new XMPPException(response.getError());
        }

        Presence presencePacket = new Presence(Presence.Type.subscribe);
        presencePacket.setTo(user);
        if (!isInvaite) { // 同意邀请 时不带这个接口
            presencePacket.setProperty("description", callMsg);
        }
        sendXmppMsg(getConnection(), presencePacket);

        // sendMsg(user);
        if (isInvaite) {
            sendUserOnlineState(Presence.Mode.chat, user);
        }
    }

    // 添加者向被添加者发送订阅请求
    void sendMsg(String usJid) {
        if (TextUtils.isEmpty(usJid)) {
            return;
        }
        StringBuilder build = new StringBuilder();
        build.append("<query xmlns=\"jabber:iq:roster\"><item jid=\"");
        build.append(usJid);
        build.append("\" subscription=\"from\"/></query>");
        IQ temp = getIq(build.toString());
        temp.setType(IQ.Type.SET);

        sendXmppMsg(getConnection(), temp);
    }

    /**
     * 通过好友邀请
     *
     * @param userJid   邀请人
     * @param GroupName 群组名
     * @param model     在线状态
     * @throws XMPPException
     */
    public void AcceptInvited(String userJid, String GroupName, Presence.Mode model) throws XMPPException {

        Presence response = new Presence(Presence.Type.subscribed);
        response.setTo(userJid);
        response.setMode(model); // 用户状态
        sendXmppMsg(getConnection(), response);

        String[] mGroupName = {GroupName};
        createEntry(userJid, Utils.getJidFromUsername(userJid), mGroupName, "AgreeSub", true);

        Presence response2 = new Presence(Presence.Type.available);
        response2.setTo(userJid);
        response2.setFrom(getUser());
        response2.setMode(model);
        response2.setStatus("在线");
        sendXmppMsg(getConnection(), response2);
    }

    /**
     * 拒绝好友
     *
     * @param UserName
     * @throws XMPPException
     */
    public void RejetInvited(String UserName) throws XMPPException {
        if (TextUtils.isEmpty(UserName)) {
            return;
        }
        Presence response = new Presence(Presence.Type.unsubscribed);
        response.setTo(UserName);
        response.setFrom(getUser());
        // getConnection().sendPacket(response);
        sendXmppMsg(getConnection(), response);
    }
}
