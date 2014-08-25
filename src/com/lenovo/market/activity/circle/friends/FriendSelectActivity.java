package com.lenovo.market.activity.circle.friends;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import com.lenovo.market.util.*;
import com.lenovo.market.vo.server.FileVo;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.adapter.FriendSelectAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.dbhelper.GroupDBHelper;
import com.lenovo.market.dbhelper.RoomDBHelper;
import com.lenovo.market.dbhelper.RoomMemberDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.view.SideBarView;
import com.lenovo.market.view.SideBarView.OnTouchingLetterChangedListener;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.local.RoomMemberVo;
import com.lenovo.market.vo.local.RoomVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.market.vo.xmpp.MsgGroupVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.MucUtils;
import com.lenovo.platform.xmpp.XmppUtils;

/**
 * 添加群的通讯录
 *
 * @author muqiang
 */
@SuppressWarnings("unchecked")
public class FriendSelectActivity extends BaseActivity implements OnTouchingLetterChangedListener, OnClickListener, OnItemClickListener {

    public static Handler handler;
    public ArrayList<FriendMesVo> friends;

    private ListView lvShow;
    private TextView overlay;
    private SideBarView myView;
    private FriendSelectAdapter adapter;
    private OverlayThread overlayThread = new OverlayThread();
    private FriendInfoDBHelper friendInfoDB_;
    private FriendMesVo friend;
    private LinearLayout footer_layout;
    private ArrayList<FriendMesVo> selectedList;
    private ArrayList<FriendMesVo> friendlist_;
    private Button confirmBtn;
    private ChatRecordDBHelper recordDbHelper;
    private RoomMemberDBHelper memberDBHelper;
    private RoomDBHelper roomDb;
    private String room_id;
    private GroupDBHelper groupDbHelper;
    private HorizontalScrollView horizontal_scrollView;

    private class OverlayThread implements Runnable {

        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_contacts_select_friend);
        setTitleBarText(R.string.title_contacts);
        setTitleBarLeftBtnText();
        handler = new FriendSelectHandler(this);
        friend = (FriendMesVo) getIntent().getSerializableExtra(MarketApp.FRIEND);
        friendlist_ = (ArrayList<FriendMesVo>) getIntent().getSerializableExtra("friendlist");
        room_id = getIntent().getStringExtra("room_id");
        friends = new ArrayList<FriendMesVo>();
        selectedList = new ArrayList<FriendMesVo>();
        friendInfoDB_ = new FriendInfoDBHelper();
        recordDbHelper = new ChatRecordDBHelper();
        memberDBHelper = new RoomMemberDBHelper();
        roomDb = new RoomDBHelper();
        groupDbHelper = new GroupDBHelper();
    }

    @Override
    protected void findViewById() {
        // 设置底部选择栏可见
        View view = findViewById(R.id.footer);
        view.setVisibility(View.VISIBLE);
        horizontal_scrollView = (HorizontalScrollView) findViewById(R.id.horizontal_scrollView);

        footer_layout = (LinearLayout) findViewById(R.id.footer_layout);
        confirmBtn = (Button) findViewById(R.id.btn);// 确认按钮

        lvShow = (ListView) findViewById(R.id.lvShow);
        myView = (SideBarView) findViewById(R.id.myView);
        overlay = (TextView) findViewById(R.id.tvLetter);
        lvShow.setTextFilterEnabled(true);
        overlay.setVisibility(View.INVISIBLE);

        View headerLayout = getLayoutInflater().inflate(R.layout.layout_contactslist_header, null);
        EditText search = (EditText) headerLayout.findViewById(R.id.et_search);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        lvShow.addHeaderView(headerLayout);

        if (friend != null) {
            friends = friendInfoDB_.getFriendsExceptSpecifyFriend(friend.getFriendId());
        } else {
            friends = friendInfoDB_.getFriendAll(MarketApp.FRIEND_TYPE_FRIEND);
        }
        Collections.sort(friends);
        if (friend != null) {
            adapter = new FriendSelectAdapter(friends, lvShow, null);
        }
        if (friendlist_ != null) {
            ArrayList<Boolean> isSelected = new ArrayList<Boolean>();
            for (FriendMesVo vo : friends) {
                String account = vo.getFriendAccount();
                boolean checked = false;
                for (FriendMesVo vo2 : friendlist_) {
                    String account2 = vo2.getFriendAccount();
                    if (account.equals(account2)) {
                        checked = true;
                        break;
                    }
                }
                isSelected.add(checked);
            }
            adapter = new FriendSelectAdapter(friends, lvShow, isSelected);
        }
        lvShow.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        myView.setOnTouchingLetterChangedListener(this);
        lvShow.setOnItemClickListener(this);
    }

    /**
     * 根据selectedList中选择的好友个数刷新按钮状态
     */
    public void updateConfirmBtn() {
        int size = selectedList.size();
        if (size > 0) {
            confirmBtn.setEnabled(true);
        } else {
            confirmBtn.setEnabled(false);
        }
        confirmBtn.setText("确认(" + size + ")");
    }

    public void initFriends() {
        if (friend == null)
            return;
        ArrayList<FriendMesVo> dataList = friendInfoDB_.getFriendsExceptSpecifyFriend(friend.getFriendId());
        if (null != dataList) {
            friends.clear();
            friends.addAll(dataList);
            Collections.sort(friends);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        overlay.setText(s);
        overlay.setVisibility(View.VISIBLE);
        handler.removeCallbacks(overlayThread);
        handler.postDelayed(overlayThread, 1000);
        if (alphaIndexer(s) > 0) {
            int position = alphaIndexer(s);
            Log.i("coder", "position:" + position);
            lvShow.setSelection(position);
        }
    }

    private int alphaIndexer(String s) {
        int position = -1;
        if (null == friends)
            return position;
        Locale locale = Locale.getDefault();
        for (int i = 0; i < friends.size(); i++) {

            if (friends.get(i).getPy().toUpperCase(locale).startsWith(s)) {
                position = i;
                break;
            }
        }
        return position + 1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn:
                if (selectedList.size() > 0) {
                    if (friendlist_ == null) {
                        createRoomOnWebService();
                    } else {
                        addToRoom(room_id);
                    }

                } else {
                    Utils.showToast(context, "请至少选择一个好友!");
                }
                break;
        }
    }

    private void createRoomOnWebService() {
        String uid = AdminUtils.getUserInfo(this).getUid();
        StringBuilder builder = new StringBuilder();
        builder.append(uid);
        if (friend != null && !TextUtils.isEmpty(friend.getFriendId())) {
            builder.append(",").append(friend.getFriendId());
        }
        for (FriendMesVo vo : selectedList) {
            builder.append(",").append(vo.getFriendId());
        }
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", uid);
        maps.put("gid", null);
        maps.put("userIds", builder.toString());
        boolean startTask = NetUtils.startTask(listener, maps, MarketApp.CREATEGROUP_METHOD, MarketApp.GROUP_SERVICE, TaskConstant.GET_DATA_32);

        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在创建聊天室");
            pd.show();
        }
    }

    private void createRoomOnOpenFire(String roomId) {
        // 创建聊天室
        XMPPConnection connection = XmppUtils.getInstance().getConnection();
        UserVo currentUser = AdminUtils.getUserInfo(this);
        try {
            MultiUserChat muc = MucUtils.getMuc(roomId);
            if (muc == null)
                return;
            // 创建聊天室
            muc.create(roomId);
            String current_user = currentUser.getAccount();
            muc.join(current_user);

            Form form = muc.getConfigurationForm();
            Form answerForm = form.createAnswerForm();
            for (Iterator<FormField> fields = form.getFields(); fields.hasNext(); ) {
                FormField field = fields.next();
                if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
                    // 设置默认值作为答复
                    answerForm.setDefaultAnswer(field.getVariable());
                }
            }

            UserVo userInfo = AdminUtils.getUserInfo(this);
            String names = "";
            for (FriendMesVo vo : selectedList) {
                if (TextUtils.isEmpty(names)) {
                    names = vo.getFriendName();
                    continue;
                }
                names = names + "、" + vo.getFriendName();
            }

            String roomname = userInfo.getUserName();
            if (!TextUtils.isEmpty(names)) {
                roomname = roomname + "、" + names;
            }

            // room name
            answerForm.setAnswer("muc#roomconfig_roomname", current_user + "创建的房间");
            // Description of Room
            answerForm.setAnswer("muc#roomconfig_roomdesc", roomname);
            // 登录房间对话 Enable Public Logging?
            answerForm.setAnswer("muc#roomconfig_enablelogging", true);
            // 主题
            answerForm.setAnswer("muc#roomconfig_changesubject", true);
            // 允许占有者邀请其他人
            answerForm.setAnswer("muc#roomconfig_allowinvites", true);
            // Make Room Publicly Searchable?
            answerForm.setAnswer("muc#roomconfig_publicroom", true);
            // 创建持久房间，所有用户退出后不会销毁
            answerForm.setAnswer("muc#roomconfig_persistentroom", true);
            // 房间仅对成员开放
            answerForm.setAnswer("muc#roomconfig_membersonly", false);
            // 能够发现占有者真实 JID 的角色
            answerForm.setAnswer("muc#roomconfig_whois", Arrays.asList("anyone"));
            // 仅允许注册的昵称登录
            answerForm.setAnswer("x-muc#roomconfig_reservednick", true);
            // 允许使用者修改昵称
            answerForm.setAnswer("x-muc#roomconfig_canchangenick", false);
            // 允许用户注册房间
            answerForm.setAnswer("x-muc#roomconfig_registration", false);

            // 发送已完成的表单（有默认值）到服务器来配置聊天室
            muc.sendConfigurationForm(answerForm);
            muc.changeSubject("未命名");

            String reason = AdminUtils.getUserInfo(this).getUserName() + "邀请你群聊";
            muc.invite(Utils.getJidFromUsername(friend.getFriendAccount()), reason);

            ArrayList<String> jids = new ArrayList<String>();
            String jid = Utils.getJidFromUsername(friend.getFriendAccount());
            jids.add(jid);
            RoomMemberVo memberVo = new RoomMemberVo(roomId, userInfo.getUid(), userInfo.getAccount(), userInfo.getUserName(), "", userInfo.getPicture());
            RoomMemberVo memberVo2 = new RoomMemberVo(roomId, friend.getFriendId(), friend.getFriendAccount(), friend.getFriendName(), "", friend.getPicture());
            memberDBHelper.insert(memberVo);// 将自己作为member存储
            memberDBHelper.insert(memberVo2);// 将指定好友作为member存储（由于群组聊天时从某个好友的个人设置里发起的）
            ArrayList<RoomMemberVo> members = new ArrayList<RoomMemberVo>();
            members.add(memberVo);
            members.add(memberVo2);

            for (FriendMesVo vo : selectedList) {
                jid = Utils.getJidFromUsername(vo.getFriendAccount());
                muc.invite(jid, reason);
                jids.add(jid);
                memberVo = new RoomMemberVo(roomId, vo.getFriendId(), vo.getFriendAccount(), vo.getFriendName(), "", vo.getPicture());
                members.add(memberVo);
                memberDBHelper.insert(memberVo);
            }

            Gson gson = new Gson();
            String json = gson.toJson(members);

            MsgXmlVo mVo = new MsgXmlVo();
            mVo.setContent(reason);
            mVo.setMsgType(MarketApp.MESSAGETYPE_GROUPINVITATION);
            mVo.setCreateTime(System.currentTimeMillis() + "");
            String body = XMLUtil.createXML(mVo, MarketApp.MESSAGETYPE_GROUPINVITATION);

            for (String Jid : jids) {
                org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message(Jid, org.jivesoftware.smack.packet.Message.Type.chat);
                msg.setProperty(MarketApp.MESSAGETYPE, MarketApp.MESSAGETYPE_GROUPINVITATION);
                msg.setProperty("room_jid", roomId + "@" + MarketApp.ROOM_SERVER_NAME);
                msg.setProperty("room_members", json);
                msg.setBody(body);
                connection.sendPacket(msg);
            }
            // muc.grantOwnership(jids);
            // muc.grantMembership(jids);
            // muc.grantAdmin(jids);
            ArrayList<MsgGroupVo> list = new ArrayList<MsgGroupVo>();
            String timeMillis = System.currentTimeMillis() + "";
            String account = AdminUtils.getUserInfo(context).getAccount();

            // 添加一条时间
            String uuid = UUID.randomUUID().toString();
            MsgXmlVo timeVo = new MsgXmlVo();
            String timeContent = DateUtil.getDateStrFromLong(timeMillis);
            timeVo.setMsgType(MarketApp.MESSAGE_TIME);
            timeVo.setContent(timeContent);
            timeVo.setCreateTime(timeMillis);
            timeVo.setMsgId(uuid);
            String timeXml = XMLUtil.createXML(timeVo, MarketApp.MESSAGE_TIME);
            MsgGroupVo vo = new MsgGroupVo(uuid, roomId, MarketApp.MESSAGE_TIME, timeMillis, account, roomId, timeXml, account, "0", MarketApp.MESSAGE_TIME);
            long id = groupDbHelper.insert(vo);
            vo.setId(id + "");
            list.add(vo);
            if (GroupChatActivity.handler != null) {
                GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST, list).sendToTarget();
            }
            muc.sendMessage(timeXml);

            // 添加一条邀请信息
            list.clear();
            uuid = UUID.randomUUID().toString();
            timeMillis = System.currentTimeMillis() + "";
            MsgXmlVo invitationVo = new MsgXmlVo();
            invitationVo.setMsgType(MarketApp.MESSAGE_NOTICE);
            invitationVo.setContent(userInfo.getUserName() + "邀请" + names + "加入了群聊");
            invitationVo.setCreateTime(timeMillis);
            invitationVo.setMsgId(uuid);
            String xml = XMLUtil.createXML(invitationVo, MarketApp.MESSAGE_NOTICE);
            vo = new MsgGroupVo(uuid, roomId, MarketApp.MESSAGE_NOTICE, timeMillis, account, roomId, xml, account, "0", MarketApp.MESSAGE_NOTICE);
            id = groupDbHelper.insert(vo);
            vo.setId(id + "");
            list.add(vo);
            if (GroupChatActivity.handler != null) {
                GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST, list).sendToTarget();
            }
            muc.sendMessage(xml);

            // 添加一条速聊信息
            ChatRecordVo recordVo = new ChatRecordVo(account, userInfo.getUserName(), System.currentTimeMillis() + "", 0, "", 3, "你邀请" + names + "加入了群聊", account, "0", roomId, roomname);
            recordDbHelper.insertRecord(recordVo, false);
            roomDb.insert(roomId, 0);

        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

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

        public void onComplete(String resultstr, int type) {
            if (pd != null)
                pd.dismiss();
            ResultVo rVo = (ResultVo) ResultParser.parseJSON(resultstr, ResultVo.class);
            if (rVo == null)
                return;
            String result = rVo.getResult();
            switch (type) {
                case TaskConstant.GET_DATA_32:
                    // 创建房间
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        RoomVo vo = ResultParser.parseJSON(rVo.getMsg().toString(), RoomVo.class);
                        String gid = vo.getGid();
                        if (TextUtils.isEmpty(gid)) {
                            Utils.showToast(context, "房间创建失败");
                            return;
                        }
                    createRoomOnOpenFire(gid);
                    Utils.showToast(context, "房间创建成功");
                    uploadRoomPic(gid);
                    setResult(RESULT_FIRST_USER);
                    finish();
                    Intent intent = new Intent(FriendSelectActivity.this, GroupChatActivity.class);
                    intent.putExtra("roomId", gid);
                    startActivity(intent);
                    } else {
                        Utils.showToast(context, "房间创建失败");
                    }
                    break;
                case TaskConstant.GET_DATA_34:
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        addMemberToOpenFire();
                    } else {
                        Utils.showToast(context, "添加成员失败");
                    }
                    break;
            }
        }
    };

    private void uploadRoomPic(String gid) {
        UserVo userInfo = AdminUtils.getUserInfo(this);
        Bitmap userPic = ImageDownloader.getBitmapFromMemoryOrDisk(this, userInfo.getPicture(), true);
        Resources resources = getResources();
        Bitmap defaultBitmap = ((BitmapDrawable)resources.getDrawable(R.drawable.icon)).getBitmap();
        if(userPic == null){
            userPic = defaultBitmap;
        }

        Bitmap friendPic = ImageDownloader.getBitmapFromMemoryOrDisk(this, friend.getPicture(), true);
        if(friendPic == null){
            friendPic = defaultBitmap;
        }
        int size = selectedList.size();

        Bitmap[] bitmaps = new Bitmap[size + 2];
        bitmaps[0] = userPic;
        bitmaps[1] = friendPic;
        for (int i = 0; i < size; i++) {
            Bitmap bitmap = ImageDownloader.getBitmapFromMemoryOrDisk(this, selectedList.get(i).getPicture(), true);
            if(bitmap == null){
                bitmap = defaultBitmap;
            }
            bitmaps[i+2] = bitmap;
        }

        Bitmap bitmap = Utils.createGroupBitmap(this,bitmaps);
        String filePath = Utils.saveBitmap2File(this,bitmap, gid);

        String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
        String uid = userInfo.getUid();
        HashMap map = new HashMap();
        map.put("gid",gid);
        new FileUploadTask(filePath, FileUploadTask.FROM_FRIEND_SELECT_ACTIVITY, map).execute(url, FileUploadTask.FILE_TYPE_GROUP_IMAGE, uid);
    }

    /**
     * @param gid 群组ID
     */
    private void addToRoom(String gid) {
        FriendMesVo vo;
        String friendId;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < selectedList.size(); i++) {
            vo = selectedList.get(i);
            if (vo == null)
                continue;
            friendId = vo.getFriendId();
            if (i == 0) {
                builder.append(friendId);
                continue;
            }
            builder.append(",").append(vo.getFriendId());
        }

        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("gid", gid);
        maps.put("uid", builder.toString());
        boolean startTask = NetUtils.startTask(listener, maps, MarketApp.ADDUSER_METHOD, MarketApp.GROUP_SERVICE, TaskConstant.GET_DATA_34);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在添加聊天室成员");
            pd.show();
        }
    }

    // 加好友
    private void addMemberToOpenFire() {
        UserVo userInfo = AdminUtils.getUserInfo(context);
        XMPPConnection connection = XmppUtils.getInstance().getConnection();
        MultiUserChat muc = MucUtils.getMuc(room_id);
        // 添加成员
        ArrayList<String> jids = new ArrayList<String>();
        String jid;
        RoomMemberVo memberVo;
        String reason = userInfo.getUserName() + "邀请你群聊";
        for (FriendMesVo vo : selectedList) {
            jid = Utils.getJidFromUsername(vo.getFriendAccount());
            muc.invite(jid, reason);
            jids.add(jid);
            memberVo = new RoomMemberVo(room_id, vo.getFriendId(), vo.getFriendAccount(), vo.getFriendName(), "", vo.getPicture());
            memberDBHelper.insert(memberVo);
        }

        // 追加房间成员信息到body中
        ArrayList<RoomMemberVo> members = memberDBHelper.getMembers(room_id);
        Gson gson = new Gson();
        String json = gson.toJson(members);

        // 指定用户不存在的情况发送离线消息告知用户房间号
        for (String Jid : jids) {
            org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message(Jid, org.jivesoftware.smack.packet.Message.Type.chat);
            msg.setProperty(MarketApp.MESSAGETYPE, MarketApp.MESSAGETYPE_GROUPINVITATION);
            msg.setProperty("room_jid", room_id + "@" + MarketApp.ROOM_SERVER_NAME);
            msg.setProperty("room_members", json);
            msg.setBody(reason);
            connection.sendPacket(msg);
        }

        StringBuilder name_builder = new StringBuilder();
        StringBuilder uid_builder = new StringBuilder();
        int size = selectedList.size();
        FriendMesVo vo;
        for (int i = 0; i < size; i++) {
            vo = selectedList.get(i);
            if (vo == null)
                continue;
            if (i == 0) {
                name_builder.append(vo.getFriendName());
                uid_builder.append(vo.getFriendId());
                continue;
            }
            name_builder.append("、").append(vo.getFriendName());
            uid_builder.append(",").append(vo.getFriendId());
        }

        String body = userInfo.getUserName() + "邀请" + name_builder.toString() + "加入群聊";

        String room_jid = room_id + "@" + MarketApp.ROOM_SERVER_NAME;
        String uuid = UUID.randomUUID().toString();
        Message message = new Message(room_jid, Message.Type.groupchat);
        MsgXmlVo mVo = new MsgXmlVo();
        mVo.setContent(body);
        mVo.setFriendId(uid_builder.toString());
        mVo.setMsgType(MarketApp.MESSAGETYPE_GROUPCHAT_ADDMEMBER);
        mVo.setCreateTime(System.currentTimeMillis() + "");
        mVo.setMsgId(uuid);
        String createXML = XMLUtil.createXML(mVo, MarketApp.MESSAGETYPE_GROUPCHAT_ADDMEMBER);

        ArrayList<MsgGroupVo> list = new ArrayList<MsgGroupVo>();
        MsgGroupVo gVo = new MsgGroupVo(uuid, uid_builder.toString(), MarketApp.MESSAGE_NOTICE, mVo.getCreateTime(), userInfo.getUserName(), uid_builder.toString(), createXML, userInfo.getAccount(), "0", MarketApp.MESSAGE_NOTICE);
        long id = groupDbHelper.insert(gVo);
        gVo.setId(id + "");
        list.add(gVo);
        GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST, list).sendToTarget();

        message.setProperty("room_members", json);
        message.setBody(createXML);
        try {
            muc.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.putExtra("selectedList", selectedList);
        setResult(MarketApp.HANDLERMESS_TWO, intent);
        finish();

    }

    static class FriendSelectHandler extends Handler {
        WeakReference<FriendSelectActivity> mActivity;

        public FriendSelectHandler(FriendSelectActivity activity) {
            mActivity = new WeakReference<FriendSelectActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            FriendSelectActivity activity = mActivity.get();
            switch (msg.what) {
                case MarketApp.HANDLERMESS_ZERO:
                    String friendAccount = (String) msg.obj;
                    activity.updateData(friendAccount);
                    break;
                case MarketApp.HANDLERMESS_ONE:
                    activity.initFriends();
                    MarketApp.needUpdateContacts_ = false;
                    break;
                case MarketApp.HANDLERMESS_NINE:
                    MyLogger.commLog().d("群组头像上传成功");
                    break;
            }
        }
    }

    public void updateData(String friendAccount) {
        if (!TextUtils.isEmpty(friendAccount)) {
            String tempAccount = null;
            for (FriendMesVo vo : friends) {
                tempAccount = vo.getFriendAccount();
                if (null != tempAccount && tempAccount.equals(friendAccount)) {
                    friends.remove(vo);
                    Collections.sort(friends);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.selectedList != null) {
            Boolean checked = adapter.selectedList.get(position - 1);
            if (checked) {
                return;
            }
        }
        FriendMesVo friend = friends.get(position - 1);
        addOrDeleteFriend(friend);
        boolean checked = adapter.isSelected.get(position - 1);
        adapter.isSelected.set(position - 1, !checked);
        adapter.notifyDataSetChanged();
    }

    private void addOrDeleteFriend(FriendMesVo friend) {
        if (selectedList.contains(friend)) {
            // 如果用户点击了已经添加过的好友就是要取消选择
            int index = selectedList.indexOf(friend);
            selectedList.remove(index);
            footer_layout.removeViewAt(index);
        } else {
            // 选择好友
            selectedList.add(friend);
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_chatsetting_selectfriend, null);
            ImageView img = (ImageView) layout.findViewById(R.id.item);
            Utils.downloadImg(true, context, img, friend.getPicture(), R.drawable.icon, img);
            layout.removeView(img);
            footer_layout.addView(img);
            // 绑定点击事件，点击的时候取消选择
            img.setTag(friend);
            img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectedList.remove(v.getTag());
                    int index = friends.indexOf(v.getTag());
                    boolean checked = adapter.isSelected.get(index);
                    adapter.isSelected.set(index, !checked);
                    adapter.notifyDataSetChanged();
                    footer_layout.removeView(v);
                    updateConfirmBtn();
                }
            });
            horizontal_scrollView.fullScroll(View.FOCUS_RIGHT);
        }
        updateConfirmBtn();
    }




}
