package com.lenovo.market.activity.circle.friends;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.adapter.ChatListAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.ChatInfoDBHelper;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.FileUploadTask;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.SendMsgUtil;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.view.CustomChatControls;
import com.lenovo.market.view.CustomViewPage;
import com.lenovo.market.view.CustomViewPageItem;
import com.lenovo.market.view.PullDownView;
import com.lenovo.market.view.PullDownView.OnPullDownListener;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.server.FileVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.xmpp.MsgChatVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;

/**
 * 一对一好友聊天
 * 
 * @author muqiang
 */
@SuppressWarnings("unchecked")
public class ChatActivity extends BaseActivity implements OnClickListener, OnTouchListener, OnPullDownListener {

    public static int DBindex;// 上拉刷新时记录从数据库什么位置查询
    public static Handler handler;
    public static FriendMesVo friend;
    public static boolean ChatActivityIsVisible;// 判断单聊页面是否可见

    private MediaPlayer mPlayer;
    private ArrayList<MsgChatVo> msgChatVos;
    private ChatInfoDBHelper chatInfoDb;
    private ChatRecordDBHelper recordDb;
    private ChatListAdapter adapter;
    private PullDownView mPullDownView;
    private ListView listview;
    private MediaRecorder mRecorder;
    private Button btn_pressed_speek;
    private Button btn_voice;
    private Button btn_keyboard;
    private RelativeLayout layout_voice;
    private String voiceFilePath;
    private TextView tv_volume;
    private CustomChatControls chatControls;
    private CustomViewPage customViewPage;
    private boolean inputB;
    private int totalCount;// 记录数据库有多少信息

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_chat_list);
        setTitleBarRightImg(R.drawable.btn_titlebar_right_chat);
        setTitleBarLeftBtnText();
        friend = (FriendMesVo) getIntent().getExtras().get(MarketApp.FRIEND);
        setTitleBarText(friend.getFriendName());
        chatInfoDb = new ChatInfoDBHelper();
        recordDb = new ChatRecordDBHelper();
        handler = new ChatHandler(this);
        msgChatVos = new ArrayList<MsgChatVo>();
    }

    @Override
    protected void findViewById() {
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
        layout_voice = (RelativeLayout) findViewById(R.id.layout_voice);
        tv_volume = (TextView) findViewById(R.id.tv_volume);

        /*
         * 1.使用PullDownView 2.设置OnPullDownListener 3.从mPullDownView里面获取ListView
         */
        mPullDownView = (PullDownView) findViewById(R.id.chat_list_lv);
        // 隐藏 并禁用尾部
        mPullDownView.setHideFooter();
        mPullDownView.setOnPullDownListener(this);
        listview = mPullDownView.getListView();
        listview.setVerticalScrollBarEnabled(false);
        listview.setDividerHeight(0);
        listview.setCacheColorHint(getResources().getColor(R.color.transparent));
        listview.setSelector(R.color.transparent);
        adapter = new ChatListAdapter(this, msgChatVos, listview, friend);
        listview.setAdapter(adapter);

        totalCount = chatInfoDb.getTotalCount(friend.getFriendAccount());
        ArrayList<MsgChatVo> messages = chatInfoDb.getChatInfoListByName(friend.getFriendAccount(), totalCount);
        for (int i = 0; i < messages.size(); i++) {
            MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(messages.get(i).getContent());
            messages.get(i).setXmlVo(xmlVo);
        }
        msgChatVos.addAll(messages);
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
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.chatcontrols_send_bt:// 发送消息
            String msg = chatControls.getChatcontrols_inputbox_et().getEditableText().toString();
            if (TextUtils.isEmpty(msg)) {
                Utils.showToast(context, getResources().getString(R.string.chat_list_msg_explain));
            } else {
                String currentTimeMillis = System.currentTimeMillis() + "";
                MsgXmlVo mVo = new MsgXmlVo();
                mVo.setContent(msg);
                mVo.setTargetType("1");
                String xml = XMLUtil.createXML(mVo, MarketApp.SEND_TEXT);

                MsgChatVo mv = new MsgChatVo();
                mv.setContent(xml);
                mv.setToUserName(friend.getFriendAccount());
                mv.setFromUserName(AdminUtils.getUserInfo(context).getAccount());
                mv.setCreateTime(currentTimeMillis);
                mv.setMsgType(MarketApp.SEND_TEXT);
                mv.setType(MarketApp.SEND_TEXT);
                mv.setLoginUser(AdminUtils.getUserInfo(context).getAccount());
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    MsgXmlVo mxVo = new MsgXmlVo();
                    mxVo.setMsgType(MarketApp.SEND_TEXT);
                    mxVo.setContent(msg);
                    mxVo.setTargetType("1");
                    mxVo.setCreateTime(mv.getCreateTime());
                    String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_TEXT);
                    boolean isSuccess = SendMsgUtil.sendChatMessage(friend.getFriendAccount(), sendxml);
                    if (isSuccess) {
                        mv.setStatus("0");
                    } else {
                        mv.setStatus("1");
                    }
                } else {
                    Utils.showToast(context, "网络不可用,请连接网络！");
                    mv.setStatus("1");
                }
                saveMsgAndUpdateUI(mv);
                chatControls.getChatcontrols_inputbox_et().getEditableText().clear();
            }
            break;
        case R.id.iv_right:// 好友聊天设置
            Intent intent = new Intent(ChatActivity.this, ChatSettingActivity.class);
            intent.putExtra(MarketApp.FRIEND, friend);
            startActivityForResult(intent, 1);
            break;
        case R.id.btn_left:// 返回
            finish();
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
        case R.id.chatcontrols_select_bt:
            customViewPage.getRl_facechoose().setVisibility(View.GONE);
            CustomViewPage.et_input = chatControls.getChatcontrols_inputbox_et();
            MarketApp.whichPage = 0;
            chatControls.getChatcontrols_inputbox_et().setCursorVisible(false);
            int visibility = customViewPage.getVisibility();
            if (visibility == View.GONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(chatControls.getChatcontrols_inputbox_et().getWindowToken(), 0);
                if (inputB) {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {

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
        }
    }

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
                MsgXmlVo msgXmlVo = new MsgXmlVo();
                msgXmlVo.setVoiceUrl(voiceFilePath);
                String mediaId = voiceFilePath.substring(voiceFilePath.lastIndexOf("/") + 1, voiceFilePath.indexOf(".amr"));
                msgXmlVo.setMediaId(mediaId);
                msgXmlVo.setFormat("amr");
                msgXmlVo.setTargetType("1");
                String xml = XMLUtil.createXML(msgXmlVo, MarketApp.SEND_VOICE);

                MsgChatVo msgChatVo = new MsgChatVo();
                msgChatVo.setType(MarketApp.SEND_VOICE);
                msgChatVo.setCreateTime(System.currentTimeMillis() + "");
                msgChatVo.setContent(xml);
                msgChatVo.setToUserName(friend.getFriendAccount());
                msgChatVo.setFromUserName(AdminUtils.getUserInfo(context).getAccount());
                msgChatVo.setMsgType(MarketApp.SEND_VOICE);
                msgChatVo.setLoginUser(AdminUtils.getUserInfo(context).getAccount());
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    msgChatVo.setStatus("0");
                    long id = saveMsgAndUpdateUI(msgChatVo);
                    String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
                    String uid = AdminUtils.getUserInfo(this).getUid();
                    HashMap map = new HashMap();
                    map.put("id",id);
                    new FileUploadTask(voiceFilePath, FileUploadTask.FROM_CHAT, map).execute(url, FileUploadTask.FILE_TYPE_VOICE, uid);
                } else {
                    msgChatVo.setStatus("1");
                    saveMsgAndUpdateUI(msgChatVo);
                }
                break;
            }
            return true;
        }
        return false;
    }

    private long saveMsgAndUpdateUI(MsgChatVo msgChatVo) {
        MsgChatVo mv = new MsgChatVo(MarketApp.MESSAGE_TIME, msgChatVo.getCreateTime(), msgChatVo.getFromUserName(), msgChatVo.getToUserName(), "", AdminUtils.getUserInfo(context).getAccount(), "0", MarketApp.MESSAGE_TIME);
        long id = chatInfoDb.getCreatMessageDate(mv);
        if (id > 0) {
            mv.setId(id + "");
            msgChatVos.add(mv);
        }
        id = chatInfoDb.insertNewMessage(msgChatVo);
        // 生成recordvo 实体
        ChatRecordVo record = new ChatRecordVo(msgChatVo.getToUserName(), friend.getFriendName(), msgChatVo.getCreateTime(), 0, friend.getPicture(), 1, "", AdminUtils.getUserInfo(context).getAccount(), "0", "", "");
        if (msgChatVo.getType().equals(MarketApp.SEND_PIC)) {
            record.setContent("[图 片]");
        } else if (msgChatVo.getType().equals(MarketApp.SEND_VOICE)) {
            record.setContent("[语 音]");
        } else if (msgChatVo.getType().equals(MarketApp.SEND_BUSINESSCARD)) {
            record.setContent("[名 片]");
        } else if (msgChatVo.getType().equals(MarketApp.SEND_SHARE)) {
            record.setContent("[链 接]");
        } else if (msgChatVo.getType().equals(MarketApp.SEND_TEXT)) {
            MsgXmlVo mVopullXML = XMLUtil.pullXMLResolve(msgChatVo.getContent());
            record.setContent(mVopullXML.getContent());
        } else if (msgChatVo.getType().equals(MarketApp.SEND_VIDEO)) {
            record.setContent("[视 频]");
        } else if (msgChatVo.getType().equals(MarketApp.SEND_LOCATION)) {
            record.setContent("[位 置]");
        }
        recordDb.insertRecord(record, false);
        MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(msgChatVo.getContent());
        msgChatVo.setXmlVo(xmlVo);
        msgChatVo.setId(id + "");
        msgChatVos.add(msgChatVo);
        adapter.msgChatVos = msgChatVos;
        adapter.notifyDataSetChanged();
        listview.setSelection(listview.getBottom());
        if (FriendListFragment.handler != null) {
            FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
        }
        return id;
    }

    static class ChatHandler extends Handler {
        WeakReference<ChatActivity> mActivity;

        public ChatHandler(ChatActivity activity) {
            mActivity = new WeakReference<ChatActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ChatActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
            case MarketApp.HANDLERMESS_ZERO:
                // 接到服务器消息更新界面
                ArrayList<MsgChatVo> mVos = (ArrayList<MsgChatVo>) msg.obj;
                for (int i = 0; i < mVos.size(); i++) {
                    MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(mVos.get(i).getContent());
                    mVos.get(i).setXmlVo(xmlVo);
                }
                activity.msgChatVos.addAll(mVos);
                activity.listview.setSelection(activity.listview.getBottom());
                break;
            case MarketApp.HANDLERMESS_ONE:
                // 上拉刷新后界面添加数据
                activity.totalCount = DBindex;
                ArrayList<MsgChatVo> msgCvos = activity.chatInfoDb.getChatInfoListByName(friend.getFriendAccount(), activity.totalCount);
                for (int i = 0; i < msgCvos.size(); i++) {
                    MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(msgCvos.get(i).getContent());
                    msgCvos.get(i).setXmlVo(xmlVo);
                    activity.msgChatVos.add(i, msgCvos.get(i));
                }
                activity.adapter.notifyDataSetChanged();
                // 诉它数据加载完毕;
                activity.mPullDownView.notifyDidMore();
                // 告诉它更新完毕
                activity.mPullDownView.RefreshComplete();
                activity.listview.setSelection(msgCvos.size() + 1);
                break;
            case MarketApp.HANDLERMESS_FIVE:
                // 上传视频文件的消息
                String currentTimeMillis = System.currentTimeMillis() + "";
                String videoPath = (String) msg.obj;
                MsgXmlVo mVo = new MsgXmlVo();
                mVo.setVideoUrl(videoPath);
                String mediaId = videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.indexOf(".mp4"));
                mVo.setMediaId(mediaId);
                mVo.setTargetType("1");
                String xml = XMLUtil.createXML(mVo, MarketApp.SEND_VIDEO);

                MsgChatVo msgChatVo = new MsgChatVo();
                msgChatVo.setContent(xml);
                msgChatVo.setToUserName(friend.getFriendAccount());
                msgChatVo.setFromUserName(AdminUtils.getUserInfo(activity).getAccount());
                msgChatVo.setMsgType(MarketApp.SEND_VIDEO);
                msgChatVo.setType(MarketApp.SEND_VIDEO);
                msgChatVo.setLoginUser(AdminUtils.getUserInfo(activity).getAccount());
                msgChatVo.setCreateTime(currentTimeMillis);
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    msgChatVo.setStatus("0");
                    long id = activity.saveMsgAndUpdateUI(msgChatVo);
                    String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
                    String uid = AdminUtils.getUserInfo(activity).getUid();
                    HashMap map = new HashMap();
                    map.put("id",id);
                    new FileUploadTask(videoPath, FileUploadTask.FROM_CHAT, map).execute(url, FileUploadTask.FILE_TYPE_VIDEO, uid);
                } else {
                    msgChatVo.setStatus("1");
                    activity.saveMsgAndUpdateUI(msgChatVo);
                }
                break;
            case MarketApp.HANDLERMESS_SIX:
                // 删除信息
                activity.msgChatVos.remove(MarketApp.index);
                if (MarketApp.indexBool) {
                    activity.msgChatVos.remove(MarketApp.index - 1);
                    MarketApp.indexBool = false;
                }
                activity.adapter.msgChatVos = activity.msgChatVos;
                activity.adapter.notifyDataSetChanged();
                break;
            case MarketApp.HANDLERMESS_SEVEN:
                // 存储发送图片的消息
                String filePath = msg.getData().getString("filePath");// 接受msg传递过来的参数
                mVo = new MsgXmlVo();
                mVo.setPicUrl(filePath);
                mediaId = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.indexOf(".jpg"));
                mVo.setMediaId(mediaId);
                mVo.setTargetType("1");
                xml = XMLUtil.createXML(mVo, MarketApp.SEND_PIC);

                msgChatVo = new MsgChatVo();
                msgChatVo.setContent(xml);
                msgChatVo.setToUserName(friend.getFriendAccount());
                msgChatVo.setFromUserName(AdminUtils.getUserInfo(activity).getAccount());
                msgChatVo.setMsgType(MarketApp.SEND_PIC);
                msgChatVo.setType(MarketApp.SEND_PIC);
                msgChatVo.setCreateTime(System.currentTimeMillis() + "");
                msgChatVo.setLoginUser(AdminUtils.getUserInfo(activity).getAccount());
                activity.customViewPage.setVisibility(View.GONE);
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    msgChatVo.setStatus("0");
                    long id = activity.saveMsgAndUpdateUI(msgChatVo);
                    String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
                    String uid = AdminUtils.getUserInfo(activity).getUid();
                    HashMap map = new HashMap();
                    map.put("id",id);
                    new FileUploadTask(filePath, FileUploadTask.FROM_CHAT, map).execute(url, FileUploadTask.FILE_TYPE_IMAGE, uid);
                } else {
                    msgChatVo.setStatus("1");
                    activity.saveMsgAndUpdateUI(msgChatVo);
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
                    activity.chatInfoDb.updateContent(msg.arg2 + "", "1");
                    activity.msgChatVos.get(activity.msgChatVos.size() - 1).setStatus("1");
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
                        mVo.setTargetType("1");
                        mVo.setCreateTime(timeMillis);
                        xml = XMLUtil.createXML(mVo, MarketApp.SEND_VOICE);
                        break;
                    case 2:
                        // video
                        mVo.setMsgType(MarketApp.SEND_VIDEO);
                        mVo.setVideoUrl(path);
                        mVo.setMediaId(fileCode);
                        mVo.setTargetType("1");
                        mVo.setCreateTime(timeMillis);
                        xml = XMLUtil.createXML(mVo, MarketApp.SEND_VIDEO);
                        break;
                    case 3:
                        // image
                        mVo.setMsgType(MarketApp.SEND_PIC);
                        mVo.setPicUrl(path);
                        mVo.setMediaId(fileCode);
                        mVo.setTargetType("1");
                        mVo.setCreateTime(timeMillis);
                        xml = XMLUtil.createXML(mVo, MarketApp.SEND_PIC);
                        break;
                    }
                    if (MarketApp.network_available && NetUtils.hasNetwork()) {
                        boolean isSuccess = SendMsgUtil.sendChatMessage(friend.getFriendAccount(), xml);
                        if (isSuccess) {
                            activity.chatInfoDb.updateContent(msg.arg2 + "", "0");
                            activity.msgChatVos.get(activity.msgChatVos.size() - 1).setStatus("0");
                        } else {
                            activity.chatInfoDb.updateContent(msg.arg2 + "", "1");
                            activity.msgChatVos.get(activity.msgChatVos.size() - 1).setStatus("1");
                        }
                    } else {
                        Utils.showToast(activity, "网络不可用,请连接网络！");
                        activity.chatInfoDb.updateContent(msg.arg2 + "", "1");
                        activity.msgChatVos.get(activity.msgChatVos.size() - 1).setStatus("1");
                    }
                }
                activity.adapter.msgChatVos = activity.msgChatVos;
                activity.adapter.notifyDataSetChanged();
                break;
            case MarketApp.HANDLERMESS_TEN:
                // 存储发送名片的消息并发送到服务器
                currentTimeMillis = System.currentTimeMillis() + "";
                FriendMesVo friendMesVo = (FriendMesVo) msg.getData().getSerializable(MarketApp.FRIEND);// 接受msg传递过来的参数
                mVo = new MsgXmlVo();
                mVo.setTitle(friendMesVo.getFriendName());
                mVo.setDescription(friendMesVo.getSign());
                mVo.setPicUrl(friendMesVo.getPicture());
                mVo.setFriendId(friendMesVo.getFriendAccount());
                mVo.setTargetType(friendMesVo.getFriendType() + "");
                mediaId = "";
                if (friendMesVo.getPicture().contains("fileCode=")) {
                    mediaId = friendMesVo.getPicture().substring(friendMesVo.getPicture().indexOf("fileCode=") + 9, friendMesVo.getPicture().indexOf("&fileName"));
                }
                mVo.setMediaId(mediaId);
                xml = XMLUtil.createXML(mVo, MarketApp.SEND_BUSINESSCARD);

                MsgChatVo mv = new MsgChatVo();
                mv.setContent(xml);
                mv.setMsgType(MarketApp.SEND_BUSINESSCARD);
                mv.setType(MarketApp.SEND_BUSINESSCARD);
                mv.setToUserName(friend.getFriendAccount());
                mv.setFromUserName(AdminUtils.getUserInfo(activity).getAccount());
                mv.setCreateTime(currentTimeMillis);
                mv.setLoginUser(AdminUtils.getUserInfo(activity).getAccount());
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    MsgXmlVo mxVo = new MsgXmlVo();
                    mxVo.setMsgType(MarketApp.SEND_BUSINESSCARD);
                    mxVo.setTitle(friendMesVo.getFriendName());
                    mxVo.setDescription(friendMesVo.getSign());
                    mxVo.setPicUrl(friendMesVo.getPicture());
                    mxVo.setFriendId(friendMesVo.getFriendAccount());
                    mxVo.setTargetType(friendMesVo.getFriendType() + "");
                    mediaId = "";
                    if (friendMesVo.getPicture().contains("fileCode=")) {
                        mediaId = friendMesVo.getPicture().substring(friendMesVo.getPicture().indexOf("fileCode=") + 9, friendMesVo.getPicture().indexOf("&fileName"));
                    }
                    mxVo.setMediaId(mediaId);
                    mxVo.setCreateTime(currentTimeMillis);
                    String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_BUSINESSCARD);
                    boolean isSuccess = SendMsgUtil.sendChatMessage(friend.getFriendAccount(), sendxml);
                    if (isSuccess) {
                        mv.setStatus("0");
                    } else {
                        mv.setStatus("1");
                    }
                } else {
                    mv.setStatus("1");
                }
                activity.saveMsgAndUpdateUI(mv);
                break;
            case MarketApp.HANDLERMESS_ELEVEN:
                // 接收地理位置信息
                Bundle data = msg.getData();
                String Location_X = data.getString("Location_X");
                String Location_Y = data.getString("Location_Y");
                String Label = data.getString("Label");

                currentTimeMillis = System.currentTimeMillis() + "";
                mVo = new MsgXmlVo();
                mVo.setMsgType(MarketApp.SEND_LOCATION);
                mVo.setLocation_X(Location_X);
                mVo.setLocation_Y(Location_Y);
                mVo.setScale("17");
                mVo.setLabel(Label);
                mVo.setTargetType("1");
                xml = XMLUtil.createXML(mVo, MarketApp.SEND_LOCATION);

                mv = new MsgChatVo();
                mv.setContent(xml);
                mv.setMsgType(MarketApp.SEND_LOCATION);
                mv.setType(MarketApp.SEND_LOCATION);
                mv.setToUserName(friend.getFriendAccount());
                mv.setFromUserName(AdminUtils.getUserInfo(activity).getAccount());
                mv.setCreateTime(currentTimeMillis);
                mv.setLoginUser(AdminUtils.getUserInfo(activity).getAccount());

                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    MsgXmlVo mxVo = new MsgXmlVo();
                    mxVo.setMsgType(MarketApp.SEND_LOCATION);
                    mxVo.setLocation_X(Location_X);
                    mxVo.setLocation_Y(Location_Y);
                    mxVo.setScale("17");
                    mxVo.setLabel(Label);
                    mxVo.setTargetType("1");
                    mxVo.setCreateTime(currentTimeMillis);
                    String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_LOCATION);
                    boolean isSuccess = SendMsgUtil.sendChatMessage(friend.getFriendAccount(), sendxml);
                    if (isSuccess) {
                        mv.setStatus("0");
                    } else {
                        mv.setStatus("1");
                    }
                } else {
                    mv.setStatus("1");
                }
                activity.saveMsgAndUpdateUI(mv);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case RESULT_FIRST_USER:
            setResult(RESULT_FIRST_USER);
            finish();
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        ChatActivityIsVisible = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatActivityIsVisible = false;

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
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

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
