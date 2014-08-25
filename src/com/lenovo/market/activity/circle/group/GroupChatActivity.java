package com.lenovo.market.activity.circle.group;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.adapter.GroupChatAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.GroupDBHelper;
import com.lenovo.market.dbhelper.RoomDBHelper;
import com.lenovo.market.dbhelper.RoomMemberDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.DateUtil;
import com.lenovo.market.util.FileUploadTask;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.view.CustomChatControls;
import com.lenovo.market.view.CustomViewPage;
import com.lenovo.market.view.CustomViewPageItem;
import com.lenovo.market.view.PullDownView;
import com.lenovo.market.view.PullDownView.OnPullDownListener;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.local.RoomMemberVo;
import com.lenovo.market.vo.local.RoomVo;
import com.lenovo.market.vo.server.FileVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.GroupUserVo;
import com.lenovo.market.vo.server.PageDateVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.market.vo.xmpp.MsgGroupVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.MucUtils;

/**
 * 组聊界面
 * 
 * @author zhouyang
 */
@SuppressWarnings("unchecked")
public class GroupChatActivity extends BaseActivity implements OnClickListener, OnTouchListener, OnPullDownListener {

    public static int DBindex;// 上拉刷新时记录从数据库什么位置查询
    public static Handler handler;
    public static boolean isActive = false;// 是否处于前台
    public static String roomId;// 房间id

    private MediaPlayer mPlayer;
    private PullDownView mPullDownView;
    private ListView listview;
    private ArrayList<MsgGroupVo> messageList;
    private GroupChatAdapter adapter;
    private GroupDBHelper groupDbHelper;
    private RoomMemberDBHelper memberDBHelper;
    private ArrayList<RoomMemberVo> members;// 记录这个组里有多少人
    private RoomDBHelper roomDbHelper;
    private MultiUserChat muc_;
    private MediaRecorder mRecorder = null;
    private Button btn_pressed_speek;
    private Button btn_voice;
    private Button btn_keyboard;
    private RelativeLayout layout_voice;
    private String voiceFilePath;
    private TextView tv_volume;
    private CustomChatControls chatControls;
    private CustomViewPage customViewPage;
    private int totalCount;// 记录数据库有多少信息
    private boolean inputB;
    private ChatRecordDBHelper recordDbHelper;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_groupchat);
        setTitleBarLeftBtnText();
        groupDbHelper = new GroupDBHelper();
        memberDBHelper = new RoomMemberDBHelper();
        roomDbHelper = new RoomDBHelper();
        recordDbHelper = new ChatRecordDBHelper();
        messageList = new ArrayList<MsgGroupVo>();
        roomId = getIntent().getStringExtra("roomId");
        int member = memberDBHelper.getMember(roomId);
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            if (member < 2) {
                getRoomUserList();
            }
            muc_ = MucUtils.getMuc(roomId);
        }
        handler = new MyHandler(this);
    }

    private void getRoomUserList() {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("gid", roomId);
        maps.put("currentPageNO", 1);
        maps.put("pageSize", 5000);
        boolean startTask = NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                Utils.showToast(context, "获取成员列表失败");
                if (pd != null)
                    pd.dismiss();
            }

            @Override
            public void onComplete(String resulte) {
                if (pd != null)
                    pd.dismiss();
                ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        TypeToken<PageDateVo<GroupUserVo>> typeToken = new TypeToken<PageDateVo<GroupUserVo>>() {
                        };
                        PageDateVo<GroupUserVo> pageDataVo = ResultParser.parseJSON(rVo.getMsg().toString(), typeToken);
                        ArrayList<GroupUserVo> dataList = pageDataVo.getDateList();
                        if (null != dataList) {
                            for (GroupUserVo vo : dataList) {
                                RoomMemberVo member = new RoomMemberVo();
                                member.setRoomId(vo.getGid());
                                member.setAccount(vo.getAccount());
                                member.setMemberId(vo.getUserId());
                                member.setNickName(vo.getNickname());
                                member.setUserName(vo.getUserName());
                                member.setAvatar(vo.getPicture());
                                memberDBHelper.insert(member);
                                updateTitle();
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.USERLIST_METHOD, MarketApp.GROUP_SERVICE, TaskConstant.GET_DATA_39);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在获取成员列表");
            pd.show();
        }
    }

    @Override
    protected void findViewById() {
        setTitleBarRightImg(R.drawable.btn_titlebar_right_groupchat);
        chatControls = (CustomChatControls) findViewById(R.id.customChatControls);
        customViewPage = (CustomViewPage) findViewById(R.id.customViewPage);
        ArrayList<CustomViewPageItem> items = new ArrayList<CustomViewPageItem>();
        items.add(new CustomViewPageItem("表情", R.drawable.app_panel_expression_icon));
        items.add(new CustomViewPageItem("图片", R.drawable.app_panel_pic_icon));
        items.add(new CustomViewPageItem("视频", R.drawable.app_panel_video_icon));
        items.add(new CustomViewPageItem("名片", R.drawable.app_panel_friendcard_icon));
        items.add(new CustomViewPageItem("位置", R.drawable.app_panel_location_icon));
        customViewPage.setItems(items);

        btn_pressed_speek = (Button) findViewById(R.id.btn_pressed_speek);
        btn_voice = (Button) findViewById(R.id.btn_voice);
        btn_keyboard = (Button) findViewById(R.id.btn_keyboard);
        layout_voice = (RelativeLayout) findViewById(R.id.groupchat_voice);
        tv_volume = (TextView) findViewById(R.id.groupchat_volume);

        if (isKicked()) {
            titlebar_right_img.setVisibility(View.GONE);
        }

        /*
         * 1.使用PullDownView 2.设置OnPullDownListener 3.从mPullDownView里面获取ListView
         */
        mPullDownView = (PullDownView) findViewById(R.id.lv_groupchat);
        // 隐藏 并禁用尾部
        mPullDownView.setHideFooter();
        mPullDownView.setOnPullDownListener(this);
        listview = mPullDownView.getListView();
        listview.setVerticalScrollBarEnabled(false);
        listview.setDividerHeight(0);
        listview.setCacheColorHint(getResources().getColor(R.color.transparent));
        listview.setSelector(R.color.transparent);
        adapter = new GroupChatAdapter(this, messageList, listview);
        listview.setAdapter(adapter);

        totalCount = groupDbHelper.getTotalCount(roomId);
        ArrayList<MsgGroupVo> messages = groupDbHelper.getMessageList(roomId, totalCount);
        if (messages != null && messages.size() > 0) {
            for (int i = 0; i < messages.size(); i++) {
                MsgGroupVo vo = messages.get(i);
                RoomMemberVo member = memberDBHelper.getMember(vo.getRoomId(), vo.getFromUserName());
                if (member != null) {
                    // 头像和名字
                    vo.setFromUserName(member.getAccount());
                    vo.setFromUserPic(member.getAvatar());
                    vo.setFromUserNom(member.getUserName());
                }
                MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(vo.getContent());
                vo.setXmlVo(xmlVo);
            }
        }
        messageList.addAll(messages);
        adapter.notifyDataSetChanged();
        // 诉它数据加载完毕;
        mPullDownView.notifyDidMore();
        // 告诉它更新完毕
        mPullDownView.RefreshComplete();
        listview.setSelectionFromTop(adapter.getCount(), -3000);
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        titlebar_right_img.setOnClickListener(this);
        btn_voice.setOnClickListener(this);
        btn_keyboard.setOnClickListener(this);
        btn_pressed_speek.setOnTouchListener(this);
        chatControls.getChatcontrols_send_bt().setOnClickListener(this);
        chatControls.getChatcontrols_select_bt().setOnClickListener(this);
        chatControls.getChatcontrols_inputbox_et().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    chatControls.getChatcontrols_inputbox_et().setCursorVisible(true);
                    customViewPage.setVisibility(View.GONE);
                    inputB = true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(chatControls.getChatcontrols_inputbox_et(), InputMethodManager.SHOW_FORCED);
                    break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:
            finish();
            break;
        case R.id.iv_right:// 群组聊天设置
            Intent intent = new Intent(GroupChatActivity.this, GroupChatSettingActivity.class);
            intent.putExtra(GroupChatSettingActivity.ROOM_ID, roomId);
            startActivityForResult(intent, 1);
            break;
        case R.id.chatcontrols_send_bt:// 发送消息
            String msg = chatControls.getChatcontrols_inputbox_et().getEditableText().toString();
            if (TextUtils.isEmpty(msg)) {
                Utils.showToast(context, getResources().getString(R.string.chat_list_msg_explain));
            } else {
                String uuid = UUID.randomUUID().toString();
                String timeMillis = System.currentTimeMillis() + "";
                MsgXmlVo mVo = new MsgXmlVo();
                mVo.setMsgType(MarketApp.SEND_TEXT);
                mVo.setContent(msg);
                mVo.setCreateTime(timeMillis);
                mVo.setMsgId(uuid);
                String xml = XMLUtil.createXML(mVo, MarketApp.SEND_TEXT);

                String fromUser = AdminUtils.getUserInfo(this).getAccount();
                MsgGroupVo vo = new MsgGroupVo(uuid, roomId, MarketApp.SEND_TEXT, timeMillis, fromUser, roomId, xml, fromUser, "0", MarketApp.SEND_TEXT);
                if (groupDbHelper.needInsertTime(vo)) {
                    // 需要插入时间
                    String timeContent = DateUtil.getDateStrFromLong(timeMillis);
                    String uuidTime = UUID.randomUUID().toString();
                    MsgXmlVo timeVo = new MsgXmlVo();
                    timeVo.setMsgType(MarketApp.MESSAGE_TIME);
                    timeVo.setContent(timeContent);
                    timeVo.setCreateTime(timeMillis);
                    timeVo.setMsgId(uuidTime);
                    String status;
                    String timeXml = XMLUtil.createXML(timeVo, MarketApp.MESSAGE_TIME);
                    if (MarketApp.network_available && NetUtils.hasNetwork()) {
                        boolean isSuccess = sendMessage(timeXml);
                        if (isSuccess) {
                            status = "0";
                        } else {
                            status = "1";
                        }
                    } else {
                        status = "1";
                    }
                    updateUI(uuidTime, timeXml, MarketApp.MESSAGE_TIME, status);
                }

                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    boolean isSuccess = sendMessage(xml);
                    if (isSuccess) {
                        updateUI(uuid, xml, MarketApp.SEND_TEXT, "0");
                    } else {
                        updateUI(uuid, xml, MarketApp.SEND_TEXT, "1");
                    }
                } else {
                    updateUI(uuid, xml, MarketApp.SEND_TEXT, "1");
                }
                chatControls.getChatcontrols_inputbox_et().getEditableText().clear();
            }
            break;
        case R.id.chatcontrols_select_bt:
            customViewPage.getRl_facechoose().setVisibility(View.GONE);
            CustomViewPage.et_input = chatControls.getChatcontrols_inputbox_et();
            MarketApp.whichPage = 1;
            chatControls.getChatcontrols_inputbox_et().setCursorVisible(false);
            int visibility = customViewPage.getVisibility();
            if (visibility == View.GONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(chatControls.getChatcontrols_inputbox_et().getWindowToken(), 0);
                if (inputB) {
                    new Timer().schedule(new TimerTask() {

                        @Override
                        public void run() {
                            handler.sendEmptyMessage(MarketApp.HANDLERMESS_EIGHT);
                            inputB = false;
                        }
                    }, 1000);
                } else {
                    handler.sendEmptyMessage(MarketApp.HANDLERMESS_EIGHT);
                }
            } else {
                customViewPage.setVisibility(View.GONE);
            }
            break;
        case R.id.btn_voice:// 点击语音按钮
            btn_voice.setVisibility(View.GONE);
            chatControls.setVisibility(View.GONE);
            customViewPage.setVisibility(View.GONE);
            btn_keyboard.setVisibility(View.VISIBLE);
            btn_pressed_speek.setVisibility(View.VISIBLE);
            break;
        case R.id.btn_keyboard:// 点击键盘按钮
            btn_voice.setVisibility(View.VISIBLE);
            chatControls.setVisibility(View.VISIBLE);
            btn_keyboard.setVisibility(View.GONE);
            btn_pressed_speek.setVisibility(View.GONE);
            break;
        }
    }

    @Override
    protected void onResume() {
        isActive = true;
        updateTitle();
        handleMemberChanged();
        super.onResume();
    }

    private void updateTitle() {
        members = memberDBHelper.getMembers(roomId);
        String titlebar_text = "群聊";
        if (members.size() != 0) {
            titlebar_text = "群聊(" + members.size() + "人)";
        }
        setTitleBarText(titlebar_text);
    }

    @Override
    protected void onPause() {
        isActive = false;
        if (null != FriendListFragment.handler) {
            FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
        }

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        super.onPause();
    }

    /**
     * 发送消息
     * 
     * @param msg
     */
    private boolean sendMessage(String msg) {

        if (isKicked()) {
            Utils.showToast(this, "你已经被管理员移除房间，暂时不能发送消息!");
            return false;
        }
        try {
            if (MarketApp.network_available && NetUtils.hasNetwork()) {
                // if (MarketApp.network_change) {
                // CommonUtil.joinRooms();
                // MarketApp.network_change = false;
                // }
                if (null != muc_) {
                    String room_jid = roomId + "@" + MarketApp.ROOM_SERVER_NAME;
                    Message message = new Message(room_jid, org.jivesoftware.smack.packet.Message.Type.groupchat);
                    message.setBody(msg);
                    muc_.sendMessage(message);
                    return true;
                }
                return false;
            } else {
                Utils.showToast(MarketApp.app, "网络连接不可用，请稍后重试");
                return false;
            }
        } catch (XMPPException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case RESULT_FIRST_USER:
            setResult(RESULT_FIRST_USER);
            finish();
            break;
        case RESULT_CANCELED:
            totalCount = groupDbHelper.getTotalCount(roomId);
            ArrayList<MsgGroupVo> messages = groupDbHelper.getMessageList(roomId, totalCount);
            if (messages != null && messages.size() > 0) {
                for (int i = 0; i < messages.size(); i++) {
                    MsgGroupVo vo = messages.get(i);
                    RoomMemberVo member = memberDBHelper.getMember(vo.getRoomId(), vo.getFromUserName());
                    if (member != null) {
                        // 头像和名字
                        vo.setFromUserName(member.getAccount());
                        vo.setFromUserPic(member.getAvatar());
                        vo.setFromUserNom(member.getUserName());
                    }
                    MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(vo.getContent());
                    vo.setXmlVo(xmlVo);
                }
            }
            messageList.clear();
            messageList.addAll(messages);
            adapter.notifyDataSetChanged();
            // 诉它数据加载完毕;
            mPullDownView.notifyDidMore();
            // 告诉它更新完毕
            mPullDownView.RefreshComplete();
            listview.setSelectionFromTop(adapter.getCount(), -3000);
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleMemberChanged() {
        if (isKicked()) {
            titlebar_right_img.setVisibility(View.GONE);
        } else {
            titlebar_right_img.setVisibility(View.VISIBLE);
        }
    }

    private boolean isKicked() {
        boolean isKicked = false;
        if (roomId != null) {
            RoomVo room = roomDbHelper.getRoom(roomId);
            if (room != null) {
                isKicked = room.getIskicked() == 1;
            }
        }
        return isKicked;
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to get a chance to respond before the target view.
     * 
     * @param v
     *            The view the touch event has been dispatched to.
     * @param event
     *            The MotionEvent object containing full information about the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.btn_pressed_speek) {
            int action = event.getAction();
            switch (action) {
            case MotionEvent.ACTION_DOWN:
                btn_pressed_speek.setPressed(true);
                layout_voice.setVisibility(View.VISIBLE);
                startRecording();
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                btn_pressed_speek.setPressed(false);
                layout_voice.setVisibility(View.GONE);
                stopRecording();

                String uuid = UUID.randomUUID().toString();
                MsgXmlVo mVo = new MsgXmlVo();
                mVo.setMsgType(MarketApp.SEND_VOICE);
                mVo.setVoiceUrl(voiceFilePath);
                mVo.setFormat("amr");
                String mediaId = voiceFilePath.substring(voiceFilePath.lastIndexOf("/") + 1, voiceFilePath.indexOf(".amr"));
                mVo.setMediaId(mediaId);
                mVo.setMsgId(uuid);
                mVo.setCreateTime(System.currentTimeMillis() + "");
                String xml = XMLUtil.createXML(mVo, MarketApp.SEND_VOICE);
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    long id = updateUI(uuid, xml, MarketApp.SEND_VOICE, "0");
                    String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
                    String uid = AdminUtils.getUserInfo(this).getUid();
                    HashMap map = new HashMap();
                    map.put("id",id);
                    new FileUploadTask(voiceFilePath, FileUploadTask.FROM_GROUP_CHAT, map).execute(url, FileUploadTask.FILE_TYPE_VOICE, uid);
                } else {
                    updateUI(uuid, xml, MarketApp.SEND_VOICE, "1");
                }
                break;
            }
            return true;
        }
        return false;
    }

    // 存储信息并更新界面
    private long updateUI(String uuid, String content, String msgType, String status) {
        UserVo userInfo = AdminUtils.getUserInfo(this);
        String fromUserName = userInfo.getAccount();
        String createTime = System.currentTimeMillis() + "";
        MsgGroupVo vo = new MsgGroupVo(uuid, roomId, msgType, createTime, fromUserName, roomId, content, fromUserName, status, msgType);
        long id = groupDbHelper.insert(vo);
        vo.setId(id + "");
        MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(vo.getContent());
        vo.setXmlVo(xmlVo);
        messageList.add(vo);
        adapter.notifyDataSetChanged();
        listview.setSelection(listview.getCount() - 1);

        RoomVo roomVo = roomDbHelper.getRoom(roomId);
        ChatRecordVo record = new ChatRecordVo(userInfo.getAccount(), userInfo.getUserName(), System.currentTimeMillis() + "", 0, "", 3, "", userInfo.getAccount(), "0", roomId, roomVo.getName());
        if (msgType.equals(MarketApp.SEND_PIC)) {
            record.setContent("[图 片]");
        } else if (msgType.equals(MarketApp.SEND_VOICE)) {
            record.setContent("[语 音]");
        } else if (msgType.equals(MarketApp.SEND_BUSINESSCARD)) {
            record.setContent("[名 片]");
        } else if (msgType.equals(MarketApp.SEND_SHARE)) {
            record.setContent("[链 接]");
        } else if (msgType.equals(MarketApp.SEND_TEXT)) {
            MsgXmlVo mVopullXML = XMLUtil.pullXMLResolve(vo.getContent());
            record.setContent(mVopullXML.getContent());
        } else if (msgType.equals(MarketApp.SEND_VIDEO)) {
            record.setContent("[视 频]");
        } else if (msgType.equals(MarketApp.SEND_LOCATION)) {
            record.setContent("[位 置]");
        }
        recordDbHelper.updateContent(record);
        if (null != FriendListFragment.handler) {
            FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
        }
        return id;
    }

    private void startRecording() {
        String audioDir = Utils.getCacheDir(this, "audio");
        if (TextUtils.isEmpty(audioDir)) {
            return;
        }
        String userAcc = AdminUtils.getUserInfo(this).getAccount();
        voiceFilePath = audioDir + "/" + userAcc + "-" + System.currentTimeMillis() + ".amr";
        mRecorder = new MediaRecorder();
        // 1.设置MediaRecorder的音频源为麦克风
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 2.设置MediaRecorder录制的音频格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 3.设置文件输出路径
        mRecorder.setOutputFile(voiceFilePath);
        // 4.设置编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
            updateMicStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updateMicStatus() {
        if (mRecorder != null) {
            int volume = 100 * mRecorder.getMaxAmplitude() / 32768;
            tv_volume.setText(volume + "");
            mVoiceHandler.postDelayed(mUpdateMicStatusTimer, 500);
        }
    }

    private final Handler mVoiceHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
    }

    static class MyHandler extends Handler {
        WeakReference<GroupChatActivity> mActivity;

        public MyHandler(GroupChatActivity activity) {
            mActivity = new WeakReference<GroupChatActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            GroupChatActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
            case MarketApp.UPDATE_MESSAGE_LIST:
                // 接到服务器消息更新界面
                ArrayList<MsgGroupVo> list = (ArrayList<MsgGroupVo>) msg.obj;
                for (int i = 0; i < list.size(); i++) {
                    MsgGroupVo vo = list.get(i);
                    RoomMemberVo member = activity.memberDBHelper.getMember(vo.getRoomId(), vo.getFromUserName());
                    if (member != null) {
                        // 头像和名字
                        vo.setFromUserName(member.getAccount());
                        vo.setFromUserPic(member.getAvatar());
                        vo.setFromUserNom(member.getUserName());
                    }
                    MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(list.get(i).getContent());
                    list.get(i).setXmlVo(xmlVo);
                }
                activity.updateMessageList(list);
                activity.updateTitle();
                switch (msg.arg1) {
                case MarketApp.HANDLERMESS_ONE:
                    // 跟自己有关的变化处理，eg：被添加到或者被踢出房间
                    activity.handleMemberChanged();
                    break;
                }
                break;
            case MarketApp.HANDLERMESS_ONE:
                // 上拉刷新后界面添加数据
                activity.totalCount = DBindex;
                ArrayList<MsgGroupVo> msgCvos = activity.groupDbHelper.getMessageList(roomId, activity.totalCount);
                for (int i = 0; i < msgCvos.size(); i++) {
                    MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(msgCvos.get(i).getContent());
                    msgCvos.get(i).setXmlVo(xmlVo);
                    activity.messageList.add(i, msgCvos.get(i));
                }
                activity.adapter.notifyDataSetChanged();
                // 诉它数据加载完毕;
                activity.mPullDownView.notifyDidMore();
                // 告诉它更新完毕
                activity.mPullDownView.RefreshComplete();
                activity.listview.setSelection(msgCvos.size() + 1);
                break;
            case MarketApp.HANDLERMESS_THREE:
                String name = (String) msg.obj;
                activity.chatControls.getChatcontrols_inputbox_et().getText().append("@" + name + " ");
                break;
            case MarketApp.HANDLERMESS_FIVE:
                // 上传视频文件的消息
                String videoPath = (String) msg.obj;
                String uuid = UUID.randomUUID().toString();
                MsgXmlVo mVo = new MsgXmlVo();
                mVo.setMsgType(MarketApp.SEND_VIDEO);
                mVo.setVideoUrl(videoPath);
                String mediaId = videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.indexOf(".mp4"));
                mVo.setMediaId(mediaId);
                mVo.setCreateTime(System.currentTimeMillis() + "");
                mVo.setMsgId(uuid);
                String xml = XMLUtil.createXML(mVo, MarketApp.SEND_VIDEO);
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    long id = activity.updateUI(uuid, xml, MarketApp.SEND_VIDEO, "0");
                    String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
                    String uid = AdminUtils.getUserInfo(activity).getUid();
                    HashMap map = new HashMap();
                    map.put("id",id);
                    new FileUploadTask(videoPath, FileUploadTask.FROM_GROUP_CHAT, map).execute(url, FileUploadTask.FILE_TYPE_VIDEO, uid);
                } else {
                    activity.updateUI(uuid, xml, MarketApp.SEND_VIDEO, "1");
                }
                break;
            case MarketApp.HANDLERMESS_SIX:
                // 删除信息
                activity.messageList.remove(MarketApp.index);
                if (MarketApp.indexBool) {
                    activity.messageList.remove(MarketApp.index - 1);
                    MarketApp.indexBool = false;
                }
                activity.adapter.messageList = activity.messageList;
                activity.adapter.notifyDataSetChanged();
                break;
            case MarketApp.HANDLERMESS_SEVEN:
                // 存储发送图片的消息
                String filePath = msg.getData().getString("filePath");// 接受msg传递过来的参数
                uuid = UUID.randomUUID().toString();
                mVo = new MsgXmlVo();
                mVo.setMsgType(MarketApp.SEND_PIC);
                mVo.setPicUrl(filePath);
                mediaId = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.indexOf(".jpg"));
                mVo.setMediaId(mediaId);
                mVo.setCreateTime(System.currentTimeMillis() + "");
                xml = XMLUtil.createXML(mVo, MarketApp.SEND_PIC);
                activity.customViewPage.setVisibility(View.GONE);
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    long id = activity.updateUI(uuid, xml, MarketApp.SEND_PIC, "0");
                    String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
                    String uid = AdminUtils.getUserInfo(activity).getUid();
                    HashMap map = new HashMap();
                    map.put("id",id);
                    new FileUploadTask(filePath, FileUploadTask.FROM_GROUP_CHAT, map).execute(url, FileUploadTask.FILE_TYPE_IMAGE, uid);
                } else {
                    activity.updateUI(uuid, xml, MarketApp.SEND_PIC, "1");
                }
                break;
            case MarketApp.HANDLERMESS_EIGHT:
                activity.customViewPage.setVisibility(View.VISIBLE);
                activity.customViewPage.getViewpage_content().setVisibility(View.VISIBLE);
                activity.listview.setAdapter(activity.adapter);
                activity.adapter.notifyDataSetChanged();
                activity.listview.setSelectionFromTop(activity.adapter.getCount(), -3000);
                break;
            case MarketApp.HANDLERMESS_NINE:
                // 文件上传成功后向服务器发送的消息(图片、语音、视频)
                FileVo fVo = (FileVo) msg.obj;
                if (fVo == null) {
                    activity.groupDbHelper.update(msg.arg2 + "", "1");
                    activity.messageList.get(activity.messageList.size() - 1).setStatus("1");
                } else {
                    String path = fVo.getPath();
                    String fileCode = fVo.getFileCode();
                    mVo = new MsgXmlVo();
                    xml = "";
                    String timeMillis = System.currentTimeMillis() + "";
                    switch (msg.arg1) {
                    case 1:
                        // voice
                        mVo.setMsgType(MarketApp.SEND_VOICE);
                        mVo.setVoiceUrl(path);
                        mVo.setFormat("amr");
                        mVo.setMediaId(fileCode);
                        mVo.setCreateTime(timeMillis);
                        mVo.setMsgId(activity.groupDbHelper.getMessageId(msg.arg2 + ""));
                        xml = XMLUtil.createXML(mVo, MarketApp.SEND_VOICE);
                        break;
                    case 2:
                        // video
                        mVo.setMsgType(MarketApp.SEND_VIDEO);
                        mVo.setVideoUrl(path);
                        mVo.setMediaId(fileCode);
                        mVo.setCreateTime(timeMillis);
                        mVo.setMsgId(activity.groupDbHelper.getMessageId(msg.arg2 + ""));
                        xml = XMLUtil.createXML(mVo, MarketApp.SEND_VIDEO);
                        break;
                    case 3:
                        // image
                        mVo.setMsgType(MarketApp.SEND_PIC);
                        mVo.setPicUrl(path);
                        mVo.setMediaId(fileCode);
                        mVo.setCreateTime(timeMillis);
                        mVo.setMsgId(activity.groupDbHelper.getMessageId(msg.arg2 + ""));
                        xml = XMLUtil.createXML(mVo, MarketApp.SEND_PIC);
                        break;
                    }
                    if (MarketApp.network_available && NetUtils.hasNetwork()) {
                        boolean isSuccess = activity.sendMessage(xml);
                        if (isSuccess) {
                            activity.groupDbHelper.update(msg.arg2 + "", "0");
                            activity.messageList.get(activity.messageList.size() - 1).setStatus("0");
                        } else {
                            activity.groupDbHelper.update(msg.arg2 + "", "1");
                            activity.messageList.get(activity.messageList.size() - 1).setStatus("1");
                        }
                    } else {
                        Utils.showToast(activity, "网络不可用,请连接网络！");
                        activity.groupDbHelper.update(msg.arg2 + "", "1");
                        activity.messageList.get(activity.messageList.size() - 1).setStatus("1");
                    }
                }
                activity.adapter.messageList = activity.messageList;
                activity.adapter.notifyDataSetChanged();
                break;
            case MarketApp.HANDLERMESS_TEN:
                // 存储发送名片的消息并发送到服务器
                FriendMesVo friendMesVo = (FriendMesVo) msg.getData().getSerializable(MarketApp.FRIEND);// 接受msg传递过来的参数
                uuid = UUID.randomUUID().toString();
                mVo = new MsgXmlVo();
                mVo.setMsgType(MarketApp.SEND_BUSINESSCARD);
                mVo.setTitle(friendMesVo.getFriendName());
                mVo.setDescription(friendMesVo.getSign());
                mVo.setPicUrl(friendMesVo.getPicture());
                mVo.setFriendId(friendMesVo.getFriendAccount());
                mVo.setTargetType(friendMesVo.getFriendType() + "");
                mediaId = friendMesVo.getPicture().substring(friendMesVo.getPicture().indexOf("fileCode=") + 9, friendMesVo.getPicture().indexOf("&fileName"));
                mVo.setMediaId(mediaId);
                mVo.setCreateTime(System.currentTimeMillis() + "");
                mVo.setMsgId(uuid);
                xml = XMLUtil.createXML(mVo, MarketApp.SEND_BUSINESSCARD);
                String status;
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    boolean isSuccess = activity.sendMessage(xml);
                    if (isSuccess) {
                        status = "0";
                    } else {
                        status = "1";
                    }
                } else {
                    status = "1";
                }
                activity.updateUI(uuid, xml, MarketApp.SEND_BUSINESSCARD, status);
                break;
            case MarketApp.HANDLERMESS_ELEVEN:
                // 接收地理位置信息
                Bundle data = msg.getData();
                String Location_X = data.getString("Location_X");
                String Location_Y = data.getString("Location_Y");
                String Label = data.getString("Label");

                uuid = UUID.randomUUID().toString();
                String currentTimeMillis = System.currentTimeMillis() + "";
                mVo = new MsgXmlVo();
                mVo.setMsgType(MarketApp.SEND_LOCATION);
                mVo.setLocation_X(Location_X);
                mVo.setLocation_Y(Location_Y);
                mVo.setScale("17");
                mVo.setLabel(Label);
                mVo.setCreateTime(currentTimeMillis);
                mVo.setMsgId(uuid);
                xml = XMLUtil.createXML(mVo, MarketApp.SEND_LOCATION);
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    boolean isSuccess = activity.sendMessage(xml);
                    if (isSuccess) {
                        status = "0";
                    } else {
                        status = "1";
                    }
                } else {
                    status = "1";
                }
                activity.updateUI(uuid, xml, MarketApp.SEND_LOCATION, status);
                break;
            }
        }
    }

    public void updateMessageList(ArrayList<MsgGroupVo> list) {
        messageList.addAll(list);
        adapter.notifyDataSetChanged();
        listview.setSelection(listview.getCount() - 1);
    }

    @Override
    public void onRefresh() {
        if (totalCount > MarketApp.COUNT) {
            handler.sendEmptyMessage(MarketApp.HANDLERMESS_ONE);
        } else {
            mPullDownView.RefreshComplete();
        }
    }

    @Override
    public void onMore() {

    }
}
