package com.lenovo.platform.xmpp;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

import android.text.TextUtils;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.listener.GroupChatInvitationListenner;
import com.lenovo.market.listener.ReconnectionListener;
import com.lenovo.market.util.Utils;

//import org.jivesoftware.smackx.provider.BytestreamsProvider;

public class XmppUtils {
    private TaskSubmitter taskSubmitter;
    public static ReconnectionListener reconnectionListener;
    private GroupChatInvitationListenner invitationListenner;
    private ExecutorService executorService;
    private static XmppUtils instance = null;
    protected boolean SMACK_DEBUG = true;
    public static XMPPConnection connection;
    public String username;
    public String password;
    // private XmppUtils xmppUtils;
    // private ReconnectionThread reconnectionThread ;
    private ArrayList<Runnable> taskList;
    private TaskTracker taskTracker;
    private boolean running = false;

    protected String tempUserName = null;
    protected String tempUserPass = null;
    private Future<?> futureTask;

    /**
     * 异常情况 *
     */
    public static synchronized XmppUtils getInstance() {
        if (null == instance) {
            instance = new XmppUtils();
        }
        return instance;
    }

    /**
     * 判断是否已经连接 -----connection *
     */
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    /**
     * 判断是否已经连接----login successed *
     */
    public boolean isAuthenticated() {
        return connection != null && connection.isConnected() && connection.isAuthenticated();
    }

    /**
     * XMPPConnection的连接监听 *
     */
    public ReconnectionListener getConnectionListener() {
        return reconnectionListener;
    }

    /**
     * 群聊邀请监听器
     *
     * @return
     */
    public GroupChatInvitationListenner getInvitationListenner() {
        return invitationListenner;
    }

    /**
     * 获取XmppConnection对象
     *
     * @return
     * @throws XMPPException
     */
    public XMPPConnection getConnection() {
        if (null == connection) {
        }
        return connection;

    }

    public void sendUserOnlineState(Presence.Mode emType, String to) {
        if (null == getConnection()) {
            return;
        }

        Presence presence = new Presence(Type.available);
        presence.setFrom(getUser());
        presence.setTo(to);
        presence.setMode(emType);
        if (emType == Mode.chat) {
            presence.setStatus("chat");
        } else if (emType == Mode.dnd) {
            presence.setStatus("dnd");
        } else if (emType == Mode.xa) {
            presence.setStatus("xa");
        }
        presence.setPriority(1);
        // else if(emType == Mode.away){
        // presence.setStatus("");
        // }
        sendXmppMsg(getConnection(), presence);
    }

    /**
     * Class for monitoring the running task count.<br>
     * 用来记录线程池里面的线程个数
     */
    public class TaskTracker {

        final XmppUtils xmppUtils;

        public int count;

        public TaskTracker(XmppUtils xmppUtils) {
            this.xmppUtils = xmppUtils;
            this.count = 0;
        }

        public void increase() {
            synchronized (getTaskTracker()) {
                getTaskTracker().count++;
            }
        }

        public void decrease() {
            synchronized (getTaskTracker()) {
                getTaskTracker().count--;
            }
        }
    }

    // public void connect() {
    // taskSubmitter.submit(new Runnable() {
    // public void run() {
    // connects();
    // }
    // });
    // }

    public String getUser() {
        if (getConnection() != null)
            return getConnection().getUser();
        else
            return null;
    }

    public IQ getIq(final String xml) {
        if (TextUtils.isEmpty(xml)) {
            return null;
        }
        IQ temIq = new IQ() {

            @Override
            public String getChildElementXML() {
                return xml;
            }
        };
        return temIq;
    }

    public void sendXmppMsg(XMPPConnection conn, Packet packet) {
        // if (null == conn || !conn.isAuthenticated() || packet == null) {
        // Logs.v(XmppUtils.class, true, " conn有问题，数据未发送成功 ");
        // return;
        // }
        // Logs.v(XmppUtils.class, true, "send msg ==>>  " + packet.toXML());
        // conn.sendPacket(packet);
        sendXmppMsg(packet);

    }

    public void sendXmppMsg(Packet packet) {
        if (!isAuthenticated() || packet == null) {

        }
        try {
            getConnection().sendPacket(packet);
        } catch (Exception e) {
            Utils.showToast(MarketApp.app, "无法连接服务器");
            e.printStackTrace();
        }

    }

    // public void connects() {
    // submitLoginTask();
    // }

    // private void submitLoginTask() {
    // submitConnectTask();
    // addTask(new LoginTask());
    // }

    // private void submitConnectTask() {
    // addTask(new ConnectTask());
    // }

    /**
     * A runnable task to log into the server.
     */
    // private class LoginTask implements Runnable {
    //
    // final XmppUtils xmppUtils;
    //
    // private LoginTask() {
    // this.xmppUtils = XmppUtils.this;
    // }
    //
    // public void run() {
    //
    // if (!xmppUtils.isAuthenticated()) {
    //
    // try {
    // /** 开始登录 **/
    //
    // // xmppUtils.getConnection().sendPacket(packet);
    // xmppUtils.getConnection().login(getUsername(), getPassword(), MarketApp.RESOURCE_ANDROID);
    // MarketApp.network_available = true;
    // /** 添加对XMPPConnection的监听 **/
    //
    // PacketFilter filter = new PacketTypeFilter(Packet.class);
    // xmppUtils.getConnection().addPacketListener(XmppLogin.getInstance(), filter);
    //
    // /** 对Roster表的监听 **/
    // XmppFriendList.getInstance().parserRoster(false);
    // /** 如果正在对话界面,则重连后需要重新建立连接 **/
    // // if (null != ChatPage.thePage) {
    // // ChatPage.thePage.mHandler
    // // .sendEmptyMessage(SlookConstant.RECONNECT_SUCCESSED);
    // // }
    //
    // } catch (XMPPException e) {
    // String INVALID_CREDENTIALS_ERROR_CODE = "401";// 用户名或密码错误
    // // String INVALID_CREDENTIALS_ERROR_CONFLICT = "409";
    // String errorMessage = e.getMessage();
    //
    // if (errorMessage != null && errorMessage.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
    // /** 409有冲突 ---强制登录 -----需不需要强制登录??? **/
    // /** 401为用户名或密码错误---不再连接 **/
    // return;
    // }
    // // xmppUtils.startReconnectionThread();
    //
    // } catch (Exception e) {
    // // xmppUtils.startReconnectionThread();
    // }
    //
    // xmppUtils.runTask();
    // } else {
    // xmppUtils.runTask();
    // }
    //
    // }
    // }

    // private class ConnectTask implements Runnable {
    //
    // final XmppUtils xmppUtils;
    //
    // private ConnectTask() {
    // this.xmppUtils = XmppUtils.this;
    // }
    //
    // public void run() {
    // if (!xmppUtils.isConnected()) {
    //
    // // XmppLogin.getInstance().closeXmppConnection();// --add
    // // Create the configuration for this new connection
    // ConnectionConfiguration config = new ConnectionConfiguration(MarketApp.OPENFIRE_SERVER, MarketApp.PORT);
    //
    // config.setReconnectionAllowed(true);
    // config.setSendPresence(true);
    // config.setSASLAuthenticationEnabled(true);
    //
    // XmppUtils.getInstance().configure(ProviderManager.getInstance());
    //
    // XMPPConnection connection = new XMPPConnection(config);
    // instance.setConnection(connection);
    //
    // try {
    // // Connect to the server
    // connection.connect();
    //
    // } catch (/* XMPP */Exception e) {
    // // Log.e(LOGTAG, "XMPP connection failed", e);
    // }
    // xmppUtils.runTask();
    // } else {
    // // Log.i(LOGTAG, "XMPP connected already");
    // xmppUtils.runTask();
    // }
    // }
    // }

    /**
     * 断开连接----清除与连接有关的数据 *
     */
    public void disconnect() {
        taskSubmitter.submit(new Runnable() {
            public void run() {
                // NotificationService.this.getXmppManager().disconnect();
                terminatePersistentConnection();
            }
        });
    }

    public void terminatePersistentConnection() {
        Runnable runnable = new Runnable() {

            final XmppUtils xmppUtils = XmppUtils.this;

            public void run() {
                if (xmppUtils.isConnected()) {
                    if (null != xmppUtils.getConnection()) {
                        /** 移除监听 **/
                        // xmppUtils.getConnection().removePacketListener(
                        // XmppLogin.getInstance());
                        closeXmppConnection();
                        // xmppUtils.getConnection().disconnect();
                    }
                }
                xmppUtils.runTask();
            }

        };
        addTask(runnable);
    }

    /**
     * 执行线程 *
     */
    public void runTask() {
        synchronized (taskList) {
            running = false;
            futureTask = null;
            if (!taskList.isEmpty()) {
                Runnable runnable = (Runnable) taskList.get(0);
                taskList.remove(0);
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            }
        }
        taskTracker.decrease();
    }

    /**
     * 重连线程 **
     */
    // public void startReconnectionThread() {
    // synchronized (reconnectionThread) {
    // if (!reconnectionThread.isAlive()) {
    // reconnectionThread.setName("Xmpp Reconnection Thread");
    // reconnectionThread.start();
    // }
    // }
    // }
    // public ReconnectionThread getReconnectionThread() {
    // return reconnectionThread;
    // }
    //
    // public void setNewReconnectionThread() {
    // reconnectionThread = new ReconnectionThread(xmppUtils);
    // }
    public XmppUtils() {
        XMPPConnection.DEBUG_ENABLED = SMACK_DEBUG;
        // this.xmppUtils = XmppUtils.this;
        reconnectionListener = new ReconnectionListener();
        invitationListenner = new GroupChatInvitationListenner();
        // reconnectionThread = new ReconnectionThread(xmppUtils);
        taskList = new ArrayList<Runnable>();
        executorService = Executors.newSingleThreadExecutor();
        taskSubmitter = new TaskSubmitter(this);
        taskTracker = new TaskTracker(this);
        // connectivityReceiver = new ConnectivityReceiver(xmppUtils);
        // handler = new Handler();
    }

    /**
     * 添加线程 *
     */
    private void addTask(Runnable runnable) {
        taskTracker.increase();
        synchronized (taskList) {
            if (taskList.isEmpty() && !running) {
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            } else {
                taskList.add(runnable);
            }
        }
    }

    /**
     * 关闭XmppConnection连接
     */
    public void closeXmppConnection() {
        if (null != connection && connection.isConnected()) {
            Presence pres = new Presence(Presence.Type.unavailable);
            if (connection != null)
                connection.disconnect(pres);
            connection = null;
        }
    }

    public TaskSubmitter getTaskSubmitter() {
        return taskSubmitter;
    }

    public TaskTracker getTaskTracker() {
        return taskTracker;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setNewExecutorService() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public final static void userState(Presence presence) {
        // String form = presence.getFrom();
        // Presence.Mode mode = presence.getMode();

        // if (presence.getType().equals(Presence.Type.unavailable)) {
        // presence.setMode(Presence.Mode.xa);
        // mode = Presence.Mode.xa;
        // if (!TextUtils.isEmpty(getUserReSource(presence.getFrom()))
        // && getUserReSource(presence.getFrom()).equals("iPhone")) {
        // mode = Presence.Mode.chat;
        // }
        // }
        // if (null != mode) {
        // Look.friendHelper.saveFriendState(Utils.getJidToUserName(form),
        // Utils.getPresenceModeToInt(mode));
        // sortFriendList(form, mode);
        // }
    }

    /**
     * Class for summiting a new runnable task.<br>
     * 此类用来往线程池里面提交线程
     */
    public class TaskSubmitter {

        final XmppUtils xmppUtils;

        public TaskSubmitter(XmppUtils xmppUtils) {
            this.xmppUtils = xmppUtils;
        }

        @SuppressWarnings("rawtypes")
        public Future submit(Runnable task) {
            Future result = null;
            if (!getExecutorService().isTerminated() && !getExecutorService().isShutdown() && task != null) {
                result = getExecutorService().submit(task);
            }

            return result;
        }
    }

    /**
     * 此方法为第一次登录创建连接时方法 *
     */
    // public XMPPConnection buildConnection(String username, String password) {
    // if (!instance.isConnected()) {
    // // Create the configuration for this new connection
    // ConnectionConfiguration config = new ConnectionConfiguration(MarketApp.OPENFIRE_SERVER, MarketApp.PORT);
    // config.setReconnectionAllowed(true);
    // config.setSecurityMode(SecurityMode.disabled); // SecurityMode.required/disabled
    // config.setSASLAuthenticationEnabled(false); // true/false
    // config.setCompressionEnabled(false);
    //
    // // config.setReconnectionAllowed(true);
    // // config.setSendPresence(true);
    // // config.setSASLAuthenticationEnabled(true);
    // // config.setSASLAuthenticationEnabled(true);
    // configure(ProviderManager.getInstance());
    // XMPPConnection connection = new XMPPConnection(config);
    // XMPPConnection.DEBUG_ENABLED = SMACK_DEBUG;
    // this.username = username;
    // this.password = password;
    // this.xmppUtils.setUsername(username);
    // this.xmppUtils.setPassword(password);
    // try {
    // // Connect to the server
    // connection.connect();
    //
    // instance.setConnection(connection);
    // return connection;
    // } catch (Exception e) {
    // e.printStackTrace();
    // return null;
    // }
    // // xmppManager.runTask();
    // } else {
    // // Log.i(LOGTAG, "XMPP connected already");
    // return instance.getConnection();
    // }
    // }
    public void setConnection(XMPPConnection connection) {
        XmppUtils.connection = connection;
    }

    /**
     * xmpp配置
     *
     * @param pm
     */
    // public void configure(ProviderManager pm) {
    // // Private Data Storage
    // pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
    //
    // // Time
    // try {
    // pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
    // } catch (ClassNotFoundException e) {
    // // Logs.v(TAG,
    // // "Can't load class for org.jivesoftware.smackx.packet.Time");
    // }
    // // Message Events
    // pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());
    //
    // // Chat State
    // pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
    //
    // pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
    //
    // pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
    //
    // pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
    //
    // pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
    //
    // // XHTML
    // pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
    //
    // // Group Chat Invitations
    // pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());
    //
    // // Service Discovery # Items //解析房间列表
    // pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
    //
    // // Service Discovery # Info //某一个房间的信息
    // pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
    //
    // // Data Forms
    // pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
    //
    // // MUC User
    // pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
    //
    // // MUC Admin
    // pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
    //
    // // MUC Owner
    // pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
    //
    // // Delayed Delivery
    // pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
    //
    // // Version
    // try {
    // pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
    // } catch (ClassNotFoundException e) {
    // // Not sure what's happening here.
    // }
    // // VCard
    // pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
    //
    // // Offline Message Requests
    // pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
    //
    // // Offline Message Indicator
    // pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
    //
    // // Last Activity
    // pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
    //
    // // User Search
    // pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
    //
    // // SharedGroupsInfo
    // pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
    //
    // // JEP-33: Extended Stanza Addressing
    // pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());
    // // FileTransfer
    // pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
    //
    // pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
    //
    // // Privacy
    // pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
    //
    // pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
    // pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
    // pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
    // pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
    // pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
    // pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
    //
    // }

}
