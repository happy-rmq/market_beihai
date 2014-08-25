package com.lenovo.market.util;

import org.jivesoftware.smack.packet.Message;

import com.lenovo.market.common.CommonUtil;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.platform.xmpp.XmppUtils;

public class SendMsgUtil {

    // 首页 、公众账号发消息
    public static boolean sendMessage(String jid, String xml) {
        org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message(Utils.getJidFromUsername(jid), org.jivesoftware.smack.packet.Message.Type.chat);
        message.setBody(xml);
        try {
            XmppUtils.getInstance().getConnection().sendPacket(message);
            MyLogger.commLog().e(xml);
            return true;
        } catch (Exception e) {
            Utils.showToast(MarketApp.app, "连接已经断开,正在重连,请稍后再试...");
            CommonUtil.ConnectionXmpp(MarketApp.app);
            e.printStackTrace();
            return false;
        }
    }

    // 单聊发消息
    public static boolean sendChatMessage(String account, String xml) {
        FriendInfoDBHelper fHelper = new FriendInfoDBHelper();
        FriendMesVo friend = fHelper.getFriend(account);
        if (friend == null) {
            Utils.showToast(MarketApp.app, "您和对方已解除好友关系,请重新添加为好友！");
            return false;
        }
        UserVo userInfo = AdminUtils.getUserInfo(MarketApp.app);
        Message message = new Message(Utils.getJidFromUsername(account) + "/android", org.jivesoftware.smack.packet.Message.Type.chat);
        message.setFrom(Utils.getJidFromUsername(userInfo.getAccount()) + "/android");
        message.setProperty(MarketApp.MESSAGETYPE, MarketApp.MESSAGETYPE_NORMALSINGLE);
        message.setBody(xml);
        if (XmppUtils.getInstance().getConnection() != null && XmppUtils.getInstance().getConnection().isConnected()) {
            XmppUtils.getInstance().getConnection().sendPacket(message);
            return true;
        } else {
            Utils.showToast(MarketApp.app, "连接已经断开,正在重连,请稍后再试...");
            CommonUtil.ConnectionXmpp(MarketApp.app);
            return false;
        }
    }

    // 群组发消息
    // public static void sendGroupMessage(MultiUserChat muc_, String roomId_, String msg, boolean isKicked) {
    //
    // if (isKicked) {
    // Utils.showToast(MarketApp.app, "你已经被管理员移除房间，暂时不能发送消息!");
    // return;
    // }
    // try {
    // if (null != muc_) {
    // String room_jid = roomId_ + "@" + MarketApp.ROOM_SERVER_NAME;
    // Message message = new Message(room_jid, org.jivesoftware.smack.packet.Message.Type.groupchat);
    // message.setBody(msg);
    // muc_.sendMessage(message);
    // }
    // } catch (XMPPException e) {
    // e.printStackTrace();
    // }
    // }
}
