package com.lenovo.market.activity.home;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.common.CommonUtil;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.ChatInfoDBHelper;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.dbhelper.RoomDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.local.RoomVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.xmpp.MsgChatVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.MucUtils;
import com.lenovo.platform.xmpp.XmppUtils;

/**
 * 分享给朋友及群组
 * 
 * @author muqiang
 */
public class ShareDialogActivity extends BaseActivity implements OnClickListener {

    public static final String GROUP = "group";

    private TextView sharedialo_title;
    private TextView sharedialo_url;
    private EditText sharedialo_speak;
    private Button sharedialo_cancel;
    private Button sharedialo_send;
    private FriendMesVo friend;
    private ChatRecordVo chatRecordVo;
    private ChatInfoDBHelper chatInfoDb;
    private FriendInfoDBHelper friendDb;
    private ChatRecordDBHelper recordDb;
    private RoomDBHelper roomDb;
    private boolean isKicked;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_sharedialog);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        chatInfoDb = new ChatInfoDBHelper();
        friendDb = new FriendInfoDBHelper();
        recordDb = new ChatRecordDBHelper();
        roomDb = new RoomDBHelper();
        friend = (FriendMesVo) getIntent().getSerializableExtra(MarketApp.FRIEND);
        chatRecordVo = (ChatRecordVo) getIntent().getSerializableExtra(ShareDialogActivity.GROUP);
    }

    @Override
    protected void findViewById() {
        sharedialo_title = (TextView) findViewById(R.id.sharedialo_title);
        sharedialo_title.setText(ShareFriendsCircleActivity.sharetitle_);
        sharedialo_url = (TextView) findViewById(R.id.sharedialo_url);
        sharedialo_url.setText(ShareFriendsCircleActivity.shareurl_);
        sharedialo_speak = (EditText) findViewById(R.id.sharedialo_speak);
        sharedialo_cancel = (Button) findViewById(R.id.sharedialo_cancel);
        sharedialo_send = (Button) findViewById(R.id.sharedialo_send);
    }

    @Override
    protected void setListener() {
        sharedialo_send.setOnClickListener(this);
        sharedialo_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.sharedialo_cancel:
            finish();
            break;
        case R.id.sharedialo_send:
            if (!MarketApp.network_available) {
                Utils.showToast(context, "网络不可用,请连接网络！");
                return;
            }
            String content = sharedialo_speak.getText().toString().trim();
            if (null != friend) {
                sendMessageChat(content);
            } else {
                RoomVo room = roomDb.getRoom(chatRecordVo.getRoomId());
                if (room != null) {
                    isKicked = room.getIskicked() == 1;
                }
                sendMessageGroup(content);
            }
            break;
        }
    }

    private void sendMessageChat(final String content) {
        Message message = new Message(Utils.getJidFromUsername(friend.getFriendAccount()), org.jivesoftware.smack.packet.Message.Type.chat);
        MsgXmlVo mVo = new MsgXmlVo();
        mVo.setCreateTime(System.currentTimeMillis() + "");
        mVo.setContent(content);
        mVo.setMsgType(MarketApp.SEND_SHARE);
        mVo.setTargetType(friend.getFriendType() + "");
        mVo.setTitle(ShareFriendsCircleActivity.sharetitle_);
        mVo.setUrl(ShareFriendsCircleActivity.shareurl_);
        mVo.setPicUrl(ShareFriendsCircleActivity.sharefilepath_);
        String createXML = XMLUtil.createXML(mVo, MarketApp.SEND_SHARE);
        message.setBody(createXML);
        if (XmppUtils.getInstance().getConnection() != null) {
            XmppUtils.getInstance().getConnection().sendPacket(message);
            String createTime = System.currentTimeMillis() + "";
            String account = AdminUtils.getUserInfo(getApplicationContext()).getAccount();
            MsgChatVo mv = new MsgChatVo(MarketApp.MESSAGE_TIME, createTime, account, friend.getFriendAccount(), "", account, "0", MarketApp.MESSAGE_TIME);
            chatInfoDb.getCreatMessageDate(mv);
            MsgChatVo chatMsg = new MsgChatVo(MarketApp.SEND_SHARE, createTime, account, friend.getFriendAccount(), createXML, account, "0", MarketApp.SEND_SHARE);
            chatInfoDb.insertNewMessage(chatMsg);
            FriendMesVo friendVo = friendDb.getFriend(Utils.getUsernameFromJid(friend.getFriendAccount()));
            ChatRecordVo record = new ChatRecordVo(friendVo.getFriendAccount(), friendVo.getFriendName(), createTime, 0, friendVo.getPicture(), friendVo.getFriendType(), "[链 接]", account, "0", "", "");
            recordDb.insertRecord(record, false);
            if (FriendListFragment.handler != null) {
                // 更新对话记录
                FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
            }
            friend = null;
            finish();
            Utils.showToast(context, "分享成功!");
        } else {
            Utils.showToast(context, "连接已经断开,正在重连,请稍后再试...");
            CommonUtil.ConnectionXmpp(context);
        }
    }

    private void sendMessageGroup(final String content) {
        if (isKicked) {
            Utils.showToast(context, "你已经被管理员移除房间，暂时不能分享到此群组!");
            return;
        }
        try {
            if (!MarketApp.network_available) {
                Utils.showToast(context, "网络连接不可用，请稍后重试!");
                return;
            }
            MultiUserChat muc_ = MucUtils.getMuc(chatRecordVo.getRoomId());
            if (!muc_.isJoined()) {
                muc_.join(AdminUtils.getUserInfo(context).getAccount());
            }
            Message message = new Message();
            message.setType(Message.Type.groupchat);
            message.setTo(chatRecordVo.getRoomId() + "@" + MarketApp.ROOM_SERVER_NAME);
            MsgXmlVo mVo = new MsgXmlVo();
            mVo.setCreateTime(System.currentTimeMillis() + "");
            mVo.setContent(content);
            mVo.setMsgType(MarketApp.SEND_SHARE);
            mVo.setTitle(ShareFriendsCircleActivity.sharetitle_);
            mVo.setUrl(ShareFriendsCircleActivity.shareurl_);
            mVo.setPicUrl(ShareFriendsCircleActivity.sharefilepath_);
            String createXML = XMLUtil.createXML(mVo, MarketApp.SEND_SHARE);
            message.setBody(createXML);

            String createTime = System.currentTimeMillis() + "";
            String account = AdminUtils.getUserInfo(getApplicationContext()).getAccount();
            String userName = AdminUtils.getUserInfo(getApplicationContext()).getUserName();
            ChatRecordVo recordVo = new ChatRecordVo(account, userName, createTime, 0, "", 3, "[链 接]", account, "0", "", "");

            recordDb.insertRecord(recordVo, false);
            muc_.sendMessage(message);
            chatRecordVo = null;
            Utils.showToast(context, "分享成功!");
        } catch (XMPPException e) {
            Utils.showToast(context, "连接已经断开,正在重连,请稍后再试...");
            CommonUtil.ConnectionXmpp(context);
            e.printStackTrace();
        }
        finish();
    }
}
