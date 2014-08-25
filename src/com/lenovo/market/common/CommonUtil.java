package com.lenovo.market.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import android.content.Context;
import android.content.SharedPreferences;

import com.lenovo.market.dbhelper.RoomDBHelper;
import com.lenovo.market.dbhelper.UserDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.RequestVo;
import com.lenovo.market.vo.local.RoomVo;
import com.lenovo.platform.xmpp.MucUtils;
import com.lenovo.platform.xmpp.XmppLogin;
import com.lenovo.platform.xmpp.XmppUtils;

public class CommonUtil {

    private static String account;
    private static String pwd;

    /**
     * 加入群聊房间
     */
    public static void joinRooms() {
        RoomDBHelper roomDB = new RoomDBHelper();
        ArrayList<RoomVo> rooms = roomDB.getRooms();
        if (!MarketApp.network_available) {
            Utils.showToast(MarketApp.app, "网络不可用,请连接网络！");
            return;
        }
        String current_user = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        RoomVo room;
        String room_id;
        for (int i = 0; i < rooms.size(); i++) {
            room = rooms.get(i);
            if (room != null && room.getIskicked() == 1) {
                continue;
            }
            room_id = room.getRoomId();
            if (null != room_id && !room_id.equals("")) {
                try {
                    MultiUserChat chat = MucUtils.getMuc(room_id);
                    if (null != chat && XmppUtils.connection.isConnected()) {
                        String roomname = "未命名";
                        try {
                            RoomInfo roomInfo = MultiUserChat.getRoomInfo(XmppUtils.connection, room_id + "@" + MarketApp.ROOM_SERVER_NAME);
                            String subject2 = roomInfo.getSubject();
                            if (subject2 != null && !subject2.equals("未命名")) {
                                roomname = subject2;
                            } else {
                                roomname = roomInfo.getDescription();
                            }
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                        roomDB.updateRoomName(room_id,roomname);
                        DiscussionHistory history = new DiscussionHistory();
                        String time = AdminUtils.getGroupChatTimeFromSP(MarketApp.app, room_id);
                        if (time != null) {
                            history = new DiscussionHistory();
                            history.setSince(new Date(Long.parseLong(time)));
                        } else {
                            history.setMaxStanzas(10);
                        }
                        chat.join(current_user, null, history, SmackConfiguration.getPacketReplyTimeout());
                    }
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 登陆xmpp
     * 
     * @param context
     */
    public synchronized static void ConnectionXmpp(final Context context) {
        SharedPreferences sp = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
        account = sp.getString(MarketApp.LOGIN_ACCOUNT, "");
        UserDBHelper helper = new UserDBHelper();
        pwd = helper.getUserInfo(account).getPassword();
        if (null != XmppUtils.connection) {
            if (XmppUtils.connection.isConnected() && XmppUtils.connection.isAuthenticated()) {
                joinRooms();
            } else {
                new Thread() {
                    public void run() {
                        XmppLogin.getInstance().userLogin(account, pwd);
                        joinRooms();
                    }
                }.start();
            }
        } else {
            new Thread() {
                public void run() {
                    XmppLogin.getInstance().userLogin(account, pwd);
                    joinRooms();
                }
            }.start();
        }
    }

    public static String post(String host, RequestVo vo) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(host);

        HttpParams params = new BasicHttpParams();
        params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 10000); // 连接超时
        HttpConnectionParams.setSoTimeout(params, 500000); // 响应超时
        post.setParams(params);
        try {
            if (vo.getMaps() != null) {
                HashMap<String, Object> map = vo.getMaps();
                ArrayList<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                    pairList.add(pair);
                }
                HttpEntity entity = new UrlEncodedFormEntity(pairList, "UTF-8");
                post.setEntity(entity);
            }
            HttpResponse response = client.execute(post);// 包含响应的状态和返回的结果
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                return jsonStr;
            }
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
