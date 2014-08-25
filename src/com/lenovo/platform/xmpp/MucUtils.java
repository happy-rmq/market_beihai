package com.lenovo.platform.xmpp;

import java.util.HashMap;

import org.jivesoftware.smackx.muc.MultiUserChat;

import com.lenovo.market.common.MarketApp;

/**
 * 群聊工具类 Created by zhouyang on 13-11-28.
 */
public class MucUtils {

    private static HashMap<String, MultiUserChat> mucs = new HashMap<String, MultiUserChat>();

    public static void addMuc(String roomId, MultiUserChat muc) {
        mucs.put(roomId, muc);
    }

    public static MultiUserChat getMuc(String roomId) {
        if (roomId == null) {
            return null;
        }
        MultiUserChat muc = mucs.get(roomId);
        if (muc == null && XmppUtils.connection != null && XmppUtils.connection.isConnected()) {
            String roomJid = roomId + "@" + MarketApp.ROOM_SERVER_NAME;
            muc = new MultiUserChat(XmppUtils.connection, roomJid);
            mucs.put(roomId, muc);
        }
        return muc;
    }

    public static void clearMuc() {
        mucs.clear();
    }
}
