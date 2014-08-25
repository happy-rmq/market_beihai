package com.lenovo.market.listener;

import java.util.Date;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.RoomDBHelper;
import com.lenovo.market.dbhelper.RoomMemberDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.MyLogger;
import com.lenovo.market.vo.local.RoomMemberVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.platform.xmpp.MucUtils;

public class GroupChatInvitationListenner implements InvitationListener {

    private RoomDBHelper roomDb;
    private RoomMemberDBHelper memberDB;

    public GroupChatInvitationListenner() {
        super();
        roomDb = new RoomDBHelper();
        memberDB = new RoomMemberDBHelper();
    }

    @Override
    public void invitationReceived(Connection conn, String room, String inviter, String reason, String password, Message message) {
        MyLogger.commLog().e(inviter + "邀请你加入聊天室");
        UserVo userInfo = AdminUtils.getUserInfo(MarketApp.app);
        try {

            String room_id = room;
            if (room.indexOf("@") > -1) {
                room_id = room.split("@")[0];
            }
            DiscussionHistory history = new DiscussionHistory();
            String time = AdminUtils.getGroupChatTimeFromSP(MarketApp.app, room_id);
            if (time != null) {
                history = new DiscussionHistory();
                history.setSince(new Date(Long.parseLong(time)));
            }
            MultiUserChat multiUserChat = MucUtils.getMuc(room_id);
            multiUserChat.join(userInfo.getAccount(), null, history, SmackConfiguration.getPacketReplyTimeout());

            roomDb.insert(room_id, 0);

            RoomMemberVo member = new RoomMemberVo();
            member.setRoomId(room_id);
            member.setAccount(userInfo.getAccount());
            member.setMemberId(userInfo.getUid());
            member.setNickName(userInfo.getAccount());
            member.setUserName(userInfo.getUserName());
            member.setAvatar(userInfo.getPicture());
            memberDB.insert(member);

        } catch (XMPPException e) {
            MyLogger.commLog().e("加入聊天室失败");
            e.printStackTrace();
        }
        MyLogger.commLog().e("成功加入聊天室");
    }
}
