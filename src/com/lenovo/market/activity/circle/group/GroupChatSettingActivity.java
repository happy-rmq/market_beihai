package com.lenovo.market.activity.circle.group;

import java.util.*;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.lenovo.market.util.*;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.activity.circle.friends.FriendSelectActivity;
import com.lenovo.market.activity.contacts.FriendDetailsActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.GroupDBHelper;
import com.lenovo.market.dbhelper.RoomDBHelper;
import com.lenovo.market.dbhelper.RoomMemberDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.view.CustomGridView;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.local.RoomMemberVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.market.vo.xmpp.MsgGroupVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.MucUtils;
import com.lenovo.platform.xmpp.XmppUtils;

/**
 * 组聊设置
 * 
 * @author zhouyang
 */
@SuppressWarnings("unchecked")
public class GroupChatSettingActivity extends BaseActivity implements OnClickListener {

    public static final String ROOM_ID = "room_id";

    private CustomGridView gridView_;
    private ArrayList<FriendMesVo> list_;
    private RelativeLayout groupchat_name;
    private TextView groupchat_name_tv;
    private String room_id;
    private ChatRecordDBHelper groupRecordDB;
    private GroupDBHelper groupChatDBHelper;
    private RoomMemberDBHelper memberDBHelper;
    private RoomDBHelper roomDb;
    private Button btn_delete;
    private ItemAdapter adapter;
    private int del_position;
    private String subject;

    // private Collection<Affiliate> owners;
    // private boolean isOwner;// 是否为房间拥有者

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_groupchatsetting);
        setTitleBarLeftBtnText();
        room_id = getIntent().getStringExtra(ROOM_ID);
        groupRecordDB = new ChatRecordDBHelper();
        memberDBHelper = new RoomMemberDBHelper();
        groupChatDBHelper = new GroupDBHelper();
        roomDb = new RoomDBHelper();

        XMPPConnection connection = XmppUtils.getInstance().getConnection();
        try {
            // MultiUserChat chat = MucUtils.getMuc(room_id);
            // owners = chat.getOwners();
            RoomInfo roomInfo = MultiUserChat.getRoomInfo(connection, room_id + "@" + MarketApp.ROOM_SERVER_NAME);
            subject = roomInfo.getSubject();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void findViewById() {
        updateTitle();

        groupchat_name = (RelativeLayout) findViewById(R.id.groupchat_name);
        groupchat_name_tv = (TextView) findViewById(R.id.groupchat_name_tv);
        btn_delete = (Button) findViewById(R.id.btn_delete_and_exit);

        if (!TextUtils.isEmpty(subject)) {
            groupchat_name_tv.setText(subject);
        }

        gridView_ = (CustomGridView) findViewById(R.id.gridview);
        initData();
    }

    private void updateTitle() {
        ArrayList<RoomMemberVo> members = memberDBHelper.getMembers(room_id);
        String titlebar_text = "聊天信息";
        if (members.size() != 0) {
            titlebar_text = "聊天信息(" + members.size() + "人)";
        }
        setTitleBarText(titlebar_text);
    }

    private void initData() {
        ArrayList<RoomMemberVo> members = memberDBHelper.getMembers(room_id);
        list_ = new ArrayList<FriendMesVo>();
        FriendMesVo mesVo;
        String account = AdminUtils.getUserInfo(context).getAccount();
        for (RoomMemberVo member : members) {
            mesVo = new FriendMesVo("", member.getAccount(), member.getAvatar(), "", member.getUserName());
            mesVo.setFriendId(member.getMemberId());
            if (member.getAccount().equals(account)) {
                list_.add(0, mesVo);
            } else
                list_.add(mesVo);
        }

        RoomMember addVo = new RoomMember(RoomMember.ADD_ITEM);// 添加成员item
        list_.add(addVo);
        if (isOwner()) {
            RoomMember delVo = new RoomMember(RoomMember.DEL_ITEM);// 删除成员item
            list_.add(delVo);
        }

        int num = 0;
        int size = list_.size();
        if (size % 4 != 0) {
            num = 4 - size % 4;
        }
        for (int i = 0; i < num; i++) {
            RoomMember vo = new RoomMember(RoomMember.FILLING_ITEM);// 添加填充item
            list_.add(vo);
        }

        adapter = new ItemAdapter();
        gridView_.setAdapter(adapter);
        gridView_.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendMesVo vo = list_.get(position);
                if (vo instanceof RoomMember) {
                    // 添加删除及其后面的填充item点击事件处理
                    RoomMember member = (RoomMember) vo;
                    int itemType = member.getItemType();
                    switch (itemType) {
                    case RoomMember.ADD_ITEM:
                        if (adapter.showDelImg) {
                            adapter.showDelImg = false;
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        Intent intent = new Intent(context, FriendSelectActivity.class);
                        ArrayList<FriendMesVo> friendlist = new ArrayList<FriendMesVo>();
                        String account = AdminUtils.getUserInfo(context).getAccount();
                        for (FriendMesVo temp : list_) {
                            if (temp.getFriendAccount().equals(account)) {
                                continue;
                            }
                            friendlist.add(temp);
                        }

                        intent.putExtra("friendlist", friendlist);
                        intent.putExtra("room_id", room_id);
                        startActivityForResult(intent, 1);
                        break;
                    case RoomMember.DEL_ITEM:
                        if (adapter.showDelImg) {
                            adapter.showDelImg = false;
                        } else {
                            adapter.showDelImg = true;
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case RoomMember.FILLING_ITEM:
                        if (adapter.showDelImg) {
                            adapter.showDelImg = false;
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        break;
                    }
                } else {
                    if (adapter.showDelImg) {
                        String friendId = vo.getFriendId();
                        String currentId = AdminUtils.getUserInfo(context).getUid();
                        if (friendId != null && currentId != null && friendId.equals(currentId)) {
                            Utils.showToast(context, "不能将自己提出房间!");
                            adapter.showDelImg = false;
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        del_position = position;
                        deleteMemberFromRoom(room_id, friendId);
                    } else {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        intent.putExtra(MarketApp.FRIEND, vo);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 0);
                        context.startActivity(intent);
                    }
                }
            }
        });
    }

    protected void deleteMemberFromRoom(String gid, String uid) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("gid", gid);
        maps.put("uid", uid);
        boolean startTask = NetUtils.startTask(listener, maps, MarketApp.DELETEUSER_METHOD, MarketApp.GROUP_SERVICE, TaskConstant.GET_DATA_37);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在删除聊天室成员");
            pd.show();
        }
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        groupchat_name.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:// 返回
            finish();
            break;
        case R.id.btn_delete_and_exit:// 删除并退出
            deleteAndExit();
            break;
        case R.id.groupchat_name:// 修改房间名
            Intent intent = new Intent(context, ModifyRoomNameActivity.class);
            String name = groupchat_name_tv.getText().toString().trim();
            intent.putExtra(ModifyRoomNameActivity.ROOM_NAME, name);
            if (room_id != null) {
                intent.putExtra(ROOM_ID, room_id);
            }
            startActivityForResult(intent, 1);
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case MarketApp.HANDLERMESS_ONE:
            String room_name = data.getStringExtra(ModifyRoomNameActivity.ROOM_NAME);
            groupchat_name_tv.setText(room_name);
            ChatRecordVo record = groupRecordDB.getRecordGroup(room_id);
            if (record != null) {
                record.setRoomName(room_name);
                groupRecordDB.insertRecord(record, false);
            } else {
                // 如果速聊表中没有数据，就添加一条
                // RoomDBHelper dbHelper=new RoomDBHelper();
                // RoomVo room = dbHelper.getRoom(room_id);
                // record=new ChatRecordVo(friendAccount, friendName, createTime, unreadcount, friendPic, friendType, content, AdminUtils.getUserInfo(context).getAccount(), 0, room_id, room_name);
            }
            break;
        case MarketApp.HANDLERMESS_TWO:
            ArrayList<FriendMesVo> selectedList = (ArrayList<FriendMesVo>) data.getSerializableExtra("selectedList");
            int size = list_.size();
            FriendMesVo vo;
            for (int i = 0; i < size; i++) {
                vo = list_.get(i);
                if (vo instanceof RoomMember) {
                    list_.addAll(i, selectedList);
                    break;
                }
            }

            handleGridView();

            // 更新titlebar文字
            ArrayList<RoomMemberVo> members = memberDBHelper.getMembers(room_id);
            String titlebar_text = "聊天信息";
            if (members.size() != 0) {
                titlebar_text = "聊天信息(" + members.size() + "人)";
            }
            setTitleBarText(titlebar_text);

            uploadRoomPic(room_id);
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 处理gridview中item删除或添加后的情况
     */
    private void handleGridView() {
        int size = list_.size();
        int quotient = size / 4;// 商
        int mod = size % 4;// mod
        if (mod > 0) {
            if (quotient > 0) {
                // 大于一行情况
                FriendMesVo mesVo = list_.get(quotient * 4);
                if (mesVo instanceof RoomMember) {
                    RoomMember member = (RoomMember) mesVo;
                    if (member.getItemType() == RoomMember.FILLING_ITEM) {
                        for (int i = 0; i < mod; i++) {
                            list_.remove(list_.size() - 1);
                        }
                    } else {
                        int num = 4 - mod;
                        for (int i = 0; i < num; i++) {
                            RoomMember tempVo = new RoomMember(RoomMember.FILLING_ITEM);// 添加成员item
                            list_.add(tempVo);
                        }
                    }
                } else {
                    int num = 4 - mod;
                    for (int i = 0; i < num; i++) {
                        RoomMember tempVo = new RoomMember(RoomMember.FILLING_ITEM);// 添加成员item
                        list_.add(tempVo);
                    }
                }
            } else {
                // 一行的情况
                int num = 4 - mod;
                for (int i = 0; i < num; i++) {
                    RoomMember tempVo = new RoomMember(RoomMember.FILLING_ITEM);// 添加成员item
                    list_.add(tempVo);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void deleteAndExit() {
        UserVo userInfo = AdminUtils.getUserInfo(this);
        deleteSelfFromRoom(room_id, userInfo.getUid());
    }

    /**
     * @param gid
     *            群组ID
     * @param uid
     *            用户id
     */
    private void deleteSelfFromRoom(String gid, String uid) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("gid", gid);
        maps.put("uid", uid);
        boolean startTask = NetUtils.startTask(listener, maps, MarketApp.DELETEUSER_METHOD, MarketApp.GROUP_SERVICE, TaskConstant.GET_DATA_35);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在退出聊天室");
            pd.show();
        }
    }

    /**
     * 删除房间
     * 
     * @param uid
     * @param gid
     *            房间id
     */
    // private void deleteRoom(String uid, String gid) {
    // LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
    // maps.put("uid", uid);
    // maps.put("gid", gid);
    // boolean startTask = NetUtils.startTask(listener, maps, MarketApp.DELETE_GROUP_METHOD, MarketApp.GROUP_SERVICE, TaskConstant.GET_DATA_33);
    // if (startTask) {
    // pd = Utils.createProgressDialog(this, "正在删除房间");
    // pd.show();
    // }
    // }

    TaskListener listener = new TaskListener() {

        @Override
        public void onError(int errorCode, String message) {
            if (pd != null)
                pd.dismiss();
            Utils.showToast(context, "操作失败");
        }

        @Override
        public void onComplete(String resultstr) {
        }

        @Override
        public void onCancel() {
            if (pd != null)
                pd.dismiss();
            Utils.showToast(context, "取消操作");
        }

        public void onComplete(String resultStr, int type) {
            if (pd != null)
                pd.dismiss();
            log.e(resultStr);
            ResultVo rVo = ResultParser.parseJSON(resultStr, ResultVo.class);
            switch (type) {
            case TaskConstant.GET_DATA_33:
                break;
            case TaskConstant.GET_DATA_35:
                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        leaveRoom();
                    } else {
                        Utils.showToast(context, "删除失败");
                    }
                }
                // 删除成员
                break;
            case TaskConstant.GET_DATA_37:
                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        kickMember(list_.get(del_position));
                    } else {
                        Utils.showToast(context, "删除失败");
                    }
                }
                break;
            }
        }
    };

    static class ViewHolder {
        public TextView text;
        public ImageView image;
        public ImageView delImg;
    }

    class ItemAdapter extends BaseAdapter {
        private ViewHolder holder;
        public boolean showDelImg;

        @Override
        public int getCount() {
            return list_.size();
        }

        @Override
        public Object getItem(int position) {
            return list_.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.griditem_chatsetting, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.item_text);
                holder.image = (ImageView) convertView.findViewById(R.id.item_img);
                holder.delImg = (ImageView) convertView.findViewById(R.id.img_del);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.image.setVisibility(View.VISIBLE);
            if (showDelImg) {
                holder.delImg.setVisibility(View.VISIBLE);
            } else {
                holder.delImg.setVisibility(View.INVISIBLE);
            }
            FriendMesVo vo = list_.get(position);
            holder.text.setText(vo.getFriendName());
            int defaultImg = R.drawable.icon;
            if (vo instanceof RoomMember) {
                RoomMember member = (RoomMember) vo;
                int itemType = member.getItemType();
                switch (itemType) {
                case RoomMember.ADD_ITEM:
                    defaultImg = R.drawable.sl_btn_roominfo_add;
                    if (showDelImg) {
                        holder.image.setVisibility(View.INVISIBLE);
                        holder.delImg.setVisibility(View.INVISIBLE);
                    } else {
                        holder.image.setVisibility(View.VISIBLE);
                    }
                    break;
                case RoomMember.DEL_ITEM:
                    defaultImg = R.drawable.sl_btn_roominfo_del;
                    if (showDelImg) {
                        holder.image.setVisibility(View.INVISIBLE);
                        holder.delImg.setVisibility(View.INVISIBLE);
                    } else {
                        holder.image.setVisibility(View.VISIBLE);
                    }
                    break;
                case RoomMember.FILLING_ITEM:
                    holder.image.setVisibility(View.INVISIBLE);
                    holder.delImg.setVisibility(View.INVISIBLE);
                    break;
                }
            }
            Utils.downloadImg(true, context, holder.image, vo.getPicture(), defaultImg, gridView_);
            return convertView;
        }
    }

    private boolean isOwner() {
        Collection<Affiliate> owners = null;
        boolean isOwner = false;
        try {
            MultiUserChat chat = MucUtils.getMuc(room_id);
            owners = chat.getOwners();
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        if (owners != null) {
            String currentUser = AdminUtils.getUserInfo(context).getAccount();
            for (Affiliate affiliate : owners) {
                String jid = affiliate.getJid();
                if (jid != null) {
                    String userAccount = Utils.getUsernameFromJid(jid);
                    if (userAccount != null && currentUser != null && userAccount.equals(currentUser)) {
                        isOwner = true;
                        break;
                    }
                }
            }
        }
        return isOwner;
    }

    private void leaveRoom() {
        UserVo userInfo = AdminUtils.getUserInfo(context);
        String room_jid = room_id + "@" + MarketApp.ROOM_SERVER_NAME;
        XMPPConnection connection = XmppUtils.getInstance().getConnection();
        MultiUserChat muc = MucUtils.getMuc(room_id);

        groupRecordDB.delete(room_id);
        groupChatDBHelper.delete(room_id);
        roomDb.delete(room_id);
        memberDBHelper.delete(room_id, null);

        ArrayList<RoomMemberVo> members = memberDBHelper.getMembers(room_id);
        int size = members.size();
        if (size > 0) {
            RoomMemberVo vo = members.get(0);
            if (vo != null && vo.getAccount() != null) {
                try {
                    if (isOwner()) {
                        muc.grantOwnership(Utils.getJidFromUsername(vo.getAccount()));
                        muc.revokeMembership(Utils.getJidFromUsername(userInfo.getAccount()));
                    }
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
            Message message = new Message(room_jid, org.jivesoftware.smack.packet.Message.Type.groupchat);
            String uuid = UUID.randomUUID().toString();
            MsgXmlVo mVo = new MsgXmlVo();
            mVo.setContent(userInfo.getUserName() + "退出了群聊");
            mVo.setCreateTime(System.currentTimeMillis() + "");
            mVo.setFriendId(userInfo.getUid());
            mVo.setMsgType(MarketApp.MESSAGETYPE_GROUPCHAT_LEAVEROOM);
            mVo.setMsgId(uuid);
            String createXML = XMLUtil.createXML(mVo, MarketApp.MESSAGETYPE_GROUPCHAT_LEAVEROOM);
            message.setBody(createXML);

            ArrayList<MsgGroupVo> list = new ArrayList<MsgGroupVo>();
            MsgGroupVo gVo = new MsgGroupVo(uuid, userInfo.getUid(), MarketApp.MESSAGE_NOTICE, mVo.getCreateTime(), userInfo.getUserName(), userInfo.getUid(), createXML, userInfo.getAccount(), "0", MarketApp.MESSAGE_NOTICE);
            GroupDBHelper groupDb = new GroupDBHelper();
            long id = groupDb.insert(gVo);
            gVo.setId(id + "");
            list.add(gVo);
            GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST, list).sendToTarget();

            try {
                muc.sendMessage(message);
            } catch (XMPPException e) {
                e.printStackTrace();
            }

            Presence presence = new Presence(Type.unavailable);
            String jid = room_jid + "/" + userInfo.getAccount();
            presence.setFrom(jid);
            presence.setTo(room_jid);
            connection.sendPacket(presence);
        } else {
            try {
                if (isOwner()) {
                    muc.destroy("房间内没有成员了", null);
                }
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }

        if (null != FriendListFragment.handler) {
            FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
        }
        setResult(RESULT_FIRST_USER);
        finish();
    }

    protected void kickMember(FriendMesVo vo) {
        UserVo userInfo = AdminUtils.getUserInfo(context);
        String room_jid = room_id + "@" + MarketApp.ROOM_SERVER_NAME;
        MultiUserChat muc = MucUtils.getMuc(room_id);

        Message message = new Message(room_jid, org.jivesoftware.smack.packet.Message.Type.groupchat);
        String uuid = UUID.randomUUID().toString();
        MsgXmlVo mVo = new MsgXmlVo();
        mVo.setContent(userInfo.getUserName() + "将" + vo.getFriendName() + "移除群聊");
        mVo.setFriendId(vo.getFriendId());
        mVo.setCreateTime(System.currentTimeMillis() + "");
        mVo.setMsgType(MarketApp.MESSAGETYPE_GROUPCHAT_KICKMEMBER);
        mVo.setMsgId(uuid);
        String createXML = XMLUtil.createXML(mVo, MarketApp.MESSAGETYPE_GROUPCHAT_KICKMEMBER);
        message.setBody(createXML);

        ArrayList<MsgGroupVo> list = new ArrayList<MsgGroupVo>();
        MsgGroupVo gVo = new MsgGroupVo(uuid, vo.getFriendId(), MarketApp.MESSAGE_NOTICE, mVo.getCreateTime(), userInfo.getUserName(), vo.getFriendId(), createXML, userInfo.getAccount(), "0", MarketApp.MESSAGE_NOTICE);
        GroupDBHelper groupDb = new GroupDBHelper();
        long id = groupDb.insert(gVo);
        gVo.setId(id + "");
        list.add(gVo);
        GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST, list).sendToTarget();

        try {
            muc.sendMessage(message);
            muc.revokeMembership(vo.getFriendId());
            muc.kickParticipant(vo.getFriendAccount(), userInfo.getUserName() + "将你移除群聊");
            list_.remove(del_position);
            adapter.showDelImg = false;
            handleGridView();
            memberDBHelper.delete(room_id, vo.getFriendId());
            updateTitle();
            uploadRoomPic(room_id);
        } catch (XMPPException e) {
            e.printStackTrace();
            Utils.showToast(context, "无法踢出此成员");
            adapter.showDelImg = false;
            adapter.notifyDataSetChanged();
        }
    }


    private void uploadRoomPic(String gid) {
        UserVo userInfo = AdminUtils.getUserInfo(this);
        String account = userInfo.getAccount();
        String uid = userInfo.getUid();

        ArrayList<RoomMemberVo> members = memberDBHelper.getMembers(room_id);
        int size = members.size();
        RoomMemberVo memberVo;
        for (int i=0;i<size;i++) {
            memberVo = members.get(i);
            if (memberVo.getAccount().equals(account)) {
                RoomMemberVo self = members.remove(i);
                members.add(0,self);
                break;
            }
        }

        ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
        for (int i=0;i<size;i++) {
            if(i > 8){
                break;
            }
            memberVo = members.get(i);
            Bitmap bitmapTemp = ImageDownloader.getBitmapFromMemoryOrDisk(this, memberVo.getAvatar(), true);
            bitmapList.add(bitmapTemp);
        }

        if(bitmapList.size()<3){
            return;
        }

        Bitmap bitmap = Utils.createGroupBitmap(this,bitmapList.toArray(new Bitmap[bitmapList.size()]));
        String filePath = Utils.saveBitmap2File(this,bitmap, gid);

        String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
        HashMap map = new HashMap();
        map.put("gid",gid);
        new FileUploadTask(filePath, FileUploadTask.FROM_GroupChatSetting_ACTIVITY, map).execute(url, FileUploadTask.FILE_TYPE_GROUP_IMAGE, uid);
    }
}
