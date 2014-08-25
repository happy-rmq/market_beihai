package com.lenovo.market.listener;

import android.content.Intent;
import android.text.TextUtils;
import com.lenovo.market.activity.RemoteLoginNotiiceActivity;
import com.lenovo.market.activity.login.LoginActivity;
import com.lenovo.market.common.MarketApp;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.packet.Presence;

import com.lenovo.market.util.MyLogger;
import com.lenovo.platform.xmpp.XmppUtils;

/**
 * 对XmppConnection的连接状态监听
 * 
 * @author ybb
 * @date 2013-1-10 下午7:00:36
 */
public class ReconnectionListener implements ConnectionListener {

    static {
        try {
            // When using asmack put some code like this in your app to make Dalvic load the ReconnectionManager class
            // and run it's static initialization block:
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionClosed() {
        MyLogger.commLog().d("connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        String message = e.getMessage();
        if(!TextUtils.isEmpty(message) && message.equals("stream:error (conflict)")){
            // 提醒当前用户该帐号在其他客户端登录
            Intent intent = new Intent(MarketApp.app, RemoteLoginNotiiceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MarketApp.app.startActivity(intent);
        }
        MyLogger.commLog().d("connectionClosedOnError");
    }

    @Override
    public void reconnectingIn(int seconds) {
        MyLogger.commLog().d("reconnectingIn");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        MyLogger.commLog().d("reconnectionFailed");
    }

    @Override
    public void reconnectionSuccessful() {
        if (XmppUtils.connection != null) {
            Presence presence = new Presence(Presence.Type.available);// 此时再上报用户状态
            XmppUtils.connection.sendPacket(presence);
        }
        MyLogger.commLog().d("reconnectionSuccessful");
    }
}
