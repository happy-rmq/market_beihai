package com.lenovo.market.service;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lenovo.market.util.MyLogger;
import com.lenovo.platform.xmpp.XmppLogin;
import com.lenovo.platform.xmpp.XmppUtils;

public abstract class PlateformService extends Service {

    private SlookPacketListener packetListener;
    private static PlateformService thePlateService;
    private boolean mTestDelayElapsed = true; // no timer
    public boolean DEBUG = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static boolean isReady() {
        return thePlateService != null && thePlateService.mTestDelayElapsed;
    }

    /**
     * @throws RuntimeException
     *             service not instantiated
     */
    public static PlateformService instance() {
        if (isReady())
            return thePlateService;

        throw new RuntimeException("SlookService not instantiated yet");
    }

    public void addPacketListener() {
        PacketFilter filter = new PacketTypeFilter(Message.class);
        PacketFilter presenceFilter = new PacketTypeFilter(Packet.class);
        try {
            if (packetListener == null) {
                packetListener = new SlookPacketListener();
            }

            /** 添加SlookPacketListener的监听 **/
            if (XmppUtils.getInstance().isConnected()) {
                XmppUtils.getInstance().getConnection().addPacketListener(packetListener, filter);
                XmppUtils.getInstance().getConnection().addPacketListener(XmppLogin.getInstance(), presenceFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 判断是否登录成功,并添加监听 **/
    public void addConnectionListener() {
        if (XmppUtils.getInstance().isConnected()) {
            XmppUtils.getInstance().getConnection().addConnectionListener(XmppUtils.getInstance().getConnectionListener());
        }
    }

    public void addInvitationListener() {
        if (XmppUtils.getInstance().isConnected()) {
            MultiUserChat.addInvitationListener(XmppUtils.getInstance().getConnection(), XmppUtils.getInstance().getInvitationListenner());
        }
    }
    
    public class SlookPacketListener implements PacketListener {

        @Override
        public void processPacket(Packet packet) {
            MyLogger.commLog().i("[ " + packet.getFrom() + " ( to ) " + packet.getTo() + " ]");
            if (packet instanceof org.jivesoftware.smack.packet.Message) {
                org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
                if (message.getType() == org.jivesoftware.smack.packet.Message.Type.chat) {
                    processSinginChat(message);
                } else if (message.getType() == org.jivesoftware.smack.packet.Message.Type.groupchat) {
                    processMutilChat(message);
                }
            }
        }

    }

    /** 群聊消息处理 */
    public abstract void processMutilChat(Message mMsg);

    /**
     * 一对一消息处理
     * 
     * @param mMsg
     * @author zl
     * @throws Exception
     *             {"errcode":"91000","errmsg":"获取信息发生异常:null","msg":"","result":"error"}
     */
    public abstract void processSinginChat(Message mMsg);

    public abstract void processPublicAccountMessage(Message mMsg);
}
