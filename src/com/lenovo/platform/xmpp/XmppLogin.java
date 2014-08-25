/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lenovo.platform.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.OfflineMessageManager;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.RoomDBHelper;
import com.lenovo.market.dbhelper.RoomMemberDBHelper;
import com.lenovo.market.service.MainService;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.vo.local.RoomMemberVo;
import com.lenovo.market.vo.server.UserVo;

/**
 * xmpp登录类
 */
public class XmppLogin extends XmppUtils implements PacketListener, ConnectionListener {
    private static XmppLogin instance = null;
    public Hashtable<String, String> table = new Hashtable<String, String>();

    public static XmppLogin getInstance() {
        if (null == instance) {
            instance = new XmppLogin();
        }
        return instance;
    }

    /**
     * @param username
     * @param password
     * @return
     */
    public int rigisterUser(final String username, String password) {

        try {
            XMPPConnection conn = ConnectionManager.getConnection();
            if (conn == null || !conn.isConnected()) {
                return MarketApp.LOGIN_ERROR_NET;
            } else {
                // 登录
                connection = conn;
                connection.getAccountManager().createAccount(username, password);
                XmppFriendList.getInstance().parserRoster(true);
                return MarketApp.LOGIN_SUCC;
            }
        } catch (XMPPException e) {
            e.printStackTrace();
            int errorCode = 0;
            if (e.getXMPPError() != null) {
                errorCode = e.getXMPPError().getCode();
            }
            switch (errorCode) {
                case 409:
                case 403:
                    errorCode = MarketApp.REPEAT_LOGIN;
                    break;
                case 502:
                case 504:
                    errorCode = MarketApp.LOGIN_ERROR_NET;
                    break;
                case 401:
                    errorCode = MarketApp.LOGIN_ERROR_PWD;
                    break;
                default:
                    errorCode = MarketApp.LOGIN_ERROR_PWD;
                    break;
            }
            return errorCode;
        }
    }

    /**
     * XMPP用户登录
     *
     * @param userName 用户名 的 格式必须为 test\40slook.cc 不能为test@slook.cc
     * @param userPass 密码 zhangsan 1
     */
    public synchronized int userLogin(final String userName, final String userPass) {

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPass)) {
            return MarketApp.LOGIN_ERROR_PWD;
        }
        tempUserName = userName;
        tempUserPass = userPass;

        try {
            if (connection == null) {
                XMPPConnection.DEBUG_ENABLED = true;
                AndroidConnectionConfiguration config = new AndroidConnectionConfiguration(MarketApp.OPENFIRE_SERVER, MarketApp.PORT, MarketApp.OPENFIRE_SERVER_NAME);
                config.setReconnectionAllowed(true);
                config.setSendPresence(false);
                connection = new XMPPConnection(config);
            }

            if (!connection.isConnected() && !connection.isAuthenticated()) {
                connection.connect();
                if (null == connection.getUser() && connection.isConnected()) {
                    connection.login(userName, userPass, MarketApp.RESOURCE_ANDROID);
                }
                XmppFriendList.getInstance().parserRoster(true);
                MainService.sMainService.addConnectionListener();
                MainService.sMainService.addPacketListener();
                MainService.sMainService.addInvitationListener();

                handleOfflineMessage(connection);

                Presence presence = new Presence(Presence.Type.available);// 此时再上报用户状态
                connection.sendPacket(presence);
            }
            return MarketApp.LOGIN_SUCC;

        } catch (XMPPException ex) {
            ex.printStackTrace();
            int errorCode = 0;
            if (ex.getXMPPError() != null) {
                errorCode = ex.getXMPPError().getCode();
            }
            // ck 1212
            switch (errorCode) {
                case 409:
                case 403:
                    errorCode = MarketApp.REPEAT_LOGIN;
                    break;
                case 502:
                case 504:
                    errorCode = MarketApp.LOGIN_ERROR_NET;
                    break;
                case 401:
                    errorCode = MarketApp.LOGIN_ERROR_PWD;
                    break;
                default:
                    errorCode = MarketApp.LOGIN_ERROR_PWD;
                    break;
            }
            return errorCode;
        }
    }

    private void handleOfflineMessage(XMPPConnection conn) {
        UserVo userInfo = AdminUtils.getUserInfo(MarketApp.app);
        OfflineMessageManager offlineManager = new OfflineMessageManager(conn);
        RoomDBHelper roomDb = new RoomDBHelper();
        RoomMemberDBHelper memberDB = new RoomMemberDBHelper();
        try {
            System.out.println(offlineManager.supportsFlexibleRetrieval() ? "支持离线消息" : "不支持离线消息");
            System.out.println("离线消息数?: " + offlineManager.getMessageCount());
            Iterator<Message> it = offlineManager.getMessages();
            offlineManager.deleteMessages(); // 上报服务器已获取，需删除服务器备份，不然下次登录会重新获取
            while (it.hasNext()) {
                Message message = it.next();
                String type = (String) message.getProperty(MarketApp.MESSAGETYPE);
                if (null != type && type.equals(MarketApp.MESSAGETYPE_GROUPINVITATION)) {
                    String room_jid = (String) message.getProperty("room_jid");
                    String room_members = (String) message.getProperty("room_members");
                    if (null != room_jid) {
                        String room_id = room_jid;
                        if (room_jid.indexOf("@") > -1) {
                            room_id = room_jid.split("@")[0];
                        }
                        roomDb.insert(room_id, 0);

                        RoomMemberVo member = new RoomMemberVo();
                        member.setRoomId(room_id);
                        member.setAccount(userInfo.getAccount());
                        member.setMemberId(userInfo.getUid());
                        member.setNickName(userInfo.getAccount());
                        member.setUserName(userInfo.getUserName());
                        member.setAvatar(userInfo.getPicture());
                        memberDB.insert(member);
                    }
                    if (!TextUtils.isEmpty(room_members)) {
                        TypeToken<ArrayList<RoomMemberVo>> typeToken = new TypeToken<ArrayList<RoomMemberVo>>() {
                        };
                        ArrayList<RoomMemberVo> list = ResultParser.parseJSON(room_members, typeToken);
                        if (list != null) {
                            for (RoomMemberVo memberVo : list) {
                                memberDB.insert(memberVo);
                            }
                        }
                    }
                }
            }
        } catch (/* XMPP */Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 获取共同设定 XmppXmlParseUtils.getInstance().parserUserSetting(parser);这个方法里解析
     */
    public void sendGetCommonSet() throws XMPPException {
        String xmlData = "<SlookSetting xmlns=\"com:slook:slookSetting\"><action actionId=\"GET_SETTING\"/></SlookSetting>";
        sendXmppMsg(getConnection(), getIq(xmlData));
    }

    /**
     * 用户强行登录
     */
    public void sendForceLogin() throws XMPPException {
        Packet packet = new Packet() {
            @Override
            public String toXML() {
                return "<presence type=\"unavailable\"></presence>";
            }
        };
        sendXmppMsg(getConnection(), packet);
    }

    @Override
    public void processPacket(Packet packet) {
        if (packet instanceof org.jivesoftware.smack.packet.Message) {
            if (((org.jivesoftware.smack.packet.Message) packet).getType() != null && !((org.jivesoftware.smack.packet.Message) packet).getType().toString().equals("error"))
                if (packet.getFrom().equals(MarketApp.OPENFIRE_SERVER)) {
                    // XmppXmlParseUtils.getInstance().parserSystemMessage(packet.toXML());
                } else {
                    int index = packet.getFrom().lastIndexOf("@");
                    String form = packet.getFrom().substring(index + 1);
                    if (form.equals(MarketApp.ROOM_SERVER_NAME)) {
                        XmppXmlParseUtils.getInstance().parserRoomInvaiteMsg(packet.toXML());
                    } else {
                        // Logs.v(XmppLogin.class, true, "form = ");
                        // XmppXmlParseUtils.getInstance().parserUserCardInfoUpdate(
                        // packet.toXML());
                    }
                }
        } else if (packet instanceof Presence) {
            Presence mPresence = (Presence) packet;
            String form = mPresence.getFrom().toString();
            if (mPresence.getType().equals(Presence.Type.subscribe)) { // 添加
                String mContent = null;
                Object obj = mPresence.getProperty("description");
                if (null != obj) {
                    mContent = (String) obj;
                } else {
                    mContent = "";
                }
                boolean isinvate = false;
                Collection<String> allString = mPresence.getPropertyNames();
                for (String tem : allString) {
                    if (tem.equals("description")) {
                        isinvate = true;
                        break;
                    }
                }

                if (!isinvate) {
                    XmppFriendList.getInstance().addFriend(form);

                    Log.i("XmppLogin.class", "???  -- >>>　　被邀请返回");
                } else {
                    /** 新朋友中添加一条数据 handler发送消息或者是 本地请求数据并保存数据库 */

                    if ((mContent != null)) {
                        if (ViewPaperMenuActivity.handler != null) {
                            ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);// 更新新朋友提示
                        }
                    }
                }
            }
        }
    }

    @Override
    public void connectionClosed() { // 这种情况下，有种情况是由服务器强行断开，或者自己强行断开
        // getConnection();
    }

    @Override
    public void connectionClosedOnError(Exception e1) { // 因为程序有异常导致断开了连接，
        // getConnection();
        // connection.isConnected();
        // 有网 且 连接不为空 且 连接没有被认证
        if (MarketApp.NETWORK_OK && connection != null && !connection.isConnected()) {// 当连接不为空

            connection.isConnected();
            // 有网 且 连接不为空 且 连接没有被认证
            // 且没连接到服务器时
            try {
                connection.connect();// 连接服务器
                if (!connection.isAuthenticated()) {// 没有被认证 则再次登录
                    // String[] user = Look.userHelper.getUserAndPwd();
                    // if (user.length == 2)
                    // connection.login(user[0],
                    // user[1], Cons.RESOURCE_ANDROID);
                    // connection.login("zhangsan", "1", Cons.RESOURCE_ANDROID);
                }
                // 发送在线状态
                Presence presence = new Presence(Type.available, "在线", 1, Presence.Mode.available);
                connection.sendPacket(presence);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void reconnectingIn(int seconds) {
    }

    @Override
    public void reconnectionSuccessful() {

    }

    @Override
    public void reconnectionFailed(Exception e) {

    }

    /**
     * 拉取后台各个模块更新时间
     */
    public void getUpdateTime() {
        String requestXml = "<itemUpdate xmlns=\"com:slook:itemUpdate\"><action actionId=\"GET_ITEMUPDATE\"/></itemUpdate>";
        sendXmppMsg(getConnection(), getIq(requestXml));
    }
}
