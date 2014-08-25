package com.lenovo.market.activity.circle.friends;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.activity.home.WebViewActivity;
import com.lenovo.market.adapter.ActiveListAdapter;
import com.lenovo.market.adapter.CustomMenuAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.CustomMenuDBHelper;
import com.lenovo.market.dbhelper.MessageDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.SendMsgUtil;
import com.lenovo.market.util.FileUploadTask;
import com.lenovo.market.util.MyLogger;
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
import com.lenovo.market.vo.server.FileVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.MenuVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.ResultVoMsgArray;
import com.lenovo.market.vo.xmpp.MsgChatVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;

/**
 * 
 * 公众账号聊天
 * 
 * @author muqiang
 */
@SuppressWarnings("unchecked")
public class PublicChatActivity extends BaseActivity implements OnClickListener, OnPullDownListener, OnTouchListener {

    public static Handler handler;
    public static FriendMesVo friendVo;
    public static boolean isActive;// 判断页面是否可见
    public static boolean isClose;// 取消关注关闭页面
    public static int DBindex;// 上拉刷新时记录从数据库什么位置查询

    private ActiveListAdapter listAdapter;
    private int totalCount;// 记录数据库有多少信息
    private ArrayList<MsgChatVo> msgChatVos;
    private MessageDBHelper messageDb;
    private ChatRecordDBHelper recordDb;
    private CustomMenuDBHelper customMenuDb;
    private PullDownView mPullDownView;
    private LinearLayout active_message_input, active_message_menu, public_chat_lv;
    private Button bt_menu_one, bt_menu_two, bt_menu_three;
    private CustomChatControls chatControls;
    private CustomViewPage customViewPage;
    private ImageButton active_message_switch_menu, active_message_switch_input;
    private ListView listview, lv_menu_one, lv_menu_two, lv_menu_three;
    private ArrayList<MenuVo> menuVos, menuVOs1, menuVOs2, menuVOs3;
    private boolean lv1, lv2, lv3, blean, inputB;
    private Button active_message_speek, active_message_keyboard, active_message_voice;
    private TextView public_chat_volume;
    private RelativeLayout active_message_layout_voice;
    private MediaRecorder mRecorder;
    private String voiceFilePath;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_public_chat);
        setTitleBarRightImg(R.drawable.btn_titlebar_setting);
        setTitleBarLeftBtnText();
        messageDb = new MessageDBHelper();
        recordDb = new ChatRecordDBHelper();
        customMenuDb = new CustomMenuDBHelper();
        msgChatVos = new ArrayList<MsgChatVo>();
        menuVOs1 = new ArrayList<MenuVo>();
        menuVOs2 = new ArrayList<MenuVo>();
        menuVOs3 = new ArrayList<MenuVo>();
        handler = new PublicChatHandler(this);
        friendVo = (FriendMesVo) getIntent().getExtras().get(MarketApp.FRIEND);
        setTitleBarText(friendVo.getFriendName());
    }

    @Override
    protected void findViewById() {
        chatControls = (CustomChatControls) findViewById(R.id.active_message_chatcontrols);
        customViewPage = (CustomViewPage) findViewById(R.id.active_message_bottom_vp);
        ArrayList<CustomViewPageItem> items = new ArrayList<CustomViewPageItem>();
        items.add(new CustomViewPageItem("表情", R.drawable.app_panel_expression_icon));
        items.add(new CustomViewPageItem("图片", R.drawable.app_panel_pic_icon));
        items.add(new CustomViewPageItem("视频", R.drawable.app_panel_video_icon));
        items.add(new CustomViewPageItem("位置", R.drawable.app_panel_location_icon));
        customViewPage.setItems(items);

        active_message_input = (LinearLayout) findViewById(R.id.active_message_input);
        active_message_menu = (LinearLayout) findViewById(R.id.active_message_menu);
        active_message_menu.setVisibility(View.GONE);
        active_message_switch_input = (ImageButton) findViewById(R.id.active_message_switch_input);
        active_message_switch_menu = (ImageButton) findViewById(R.id.active_message_switch_menu);
        bt_menu_one = (Button) findViewById(R.id.bt_menu_one);
        bt_menu_two = (Button) findViewById(R.id.bt_menu_two);
        bt_menu_three = (Button) findViewById(R.id.bt_menu_three);
        lv_menu_one = (ListView) findViewById(R.id.lv_menu_one);
        lv_menu_two = (ListView) findViewById(R.id.lv_menu_two);
        lv_menu_three = (ListView) findViewById(R.id.lv_menu_three);
        public_chat_lv = (LinearLayout) findViewById(R.id.public_chat_lv);
        active_message_speek = (Button) findViewById(R.id.active_message_speek);
        active_message_keyboard = (Button) findViewById(R.id.active_message_keyboard);
        active_message_voice = (Button) findViewById(R.id.active_message_voice);
        active_message_layout_voice = (RelativeLayout) findViewById(R.id.public_chat_layout_voice);
        public_chat_volume = (TextView) findViewById(R.id.public_chat_volume);

        /*
         * 1.使用PullDownView 2.设置OnPullDownListener 3.从mPullDownView里面获取ListView
         */
        mPullDownView = (PullDownView) findViewById(R.id.public_chat_pull_down);
        // 隐藏 并禁用尾部
        mPullDownView.setHideFooter();
        mPullDownView.setOnPullDownListener(this);
        listview = mPullDownView.getListView();
        listview.setVerticalScrollBarEnabled(false);
        listview.setDividerHeight(0);
        listview.setCacheColorHint(getResources().getColor(R.color.transparent));
        listview.setSelector(R.color.transparent);
        listAdapter = new ActiveListAdapter(context, msgChatVos, listview, friendVo.getFriendName(), friendVo);
        listview.setAdapter(listAdapter);

        totalCount = messageDb.getTotalCount(friendVo.getFriendAccount());
        ArrayList<MsgChatVo> messages = messageDb.getOperationalUserMessage(friendVo.getFriendAccount(), totalCount, false);
        if (messages.size() > 0) {
            for (int i = 0; i < messages.size(); i++) {
                MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(messages.get(i).getContent());
                messages.get(i).setXmlVo(xmlVo);
            }
            msgChatVos.addAll(messages);
            listAdapter.notifyDataSetChanged();
            // 诉它数据加载完毕;
            mPullDownView.notifyDidMore();
            // 告诉它更新完毕
            mPullDownView.RefreshComplete();
            listview.setSelectionFromTop(listAdapter.getCount(), -3000);
        }

        menuVos = customMenuDb.getMenuVo(friendVo.getFriendId(), context);
        if (null != menuVos && menuVos.size() > 0) {
            MenuVo menuVo1 = menuVos.get(0);
            if (menuVo1 != null && menuVo1.getSubMenus() != null) {
                for (int i = 0; i < menuVo1.getSubMenus().size(); i++) {
                    menuVOs1.add(menuVo1.getSubMenus().get(i));
                }
            }
            MenuVo menuVo2 = menuVos.get(1);
            if (menuVo2 != null && menuVo2.getSubMenus() != null) {
                for (int i = 0; i < menuVo2.getSubMenus().size(); i++) {
                    menuVOs2.add(menuVo2.getSubMenus().get(i));
                }
            }
            MenuVo menuVo3 = menuVos.get(2);
            if (menuVo3 != null && menuVo3.getSubMenus() != null) {
                for (int i = 0; i < menuVo3.getSubMenus().size(); i++) {
                    menuVOs3.add(menuVo3.getSubMenus().get(i));
                }
            }
            handler.sendEmptyMessage(MarketApp.HANDLERMESS_THREE);
        } else {
            active_message_switch_input.setVisibility(View.GONE);
            if (MarketApp.network_available && NetUtils.hasNetwork()) {
                getCustomMenu();
            }
        }
    }

    @Override
    protected void setListener() {
        active_message_speek.setOnTouchListener(this);
        active_message_voice.setOnClickListener(this);
        active_message_keyboard.setOnClickListener(this);
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
        btn_left_.setOnClickListener(this);
        titlebar_right_img.setOnClickListener(this);
        active_message_switch_input.setOnClickListener(this);
        active_message_switch_menu.setOnClickListener(this);
        bt_menu_one.setOnClickListener(this);
        bt_menu_two.setOnClickListener(this);
        bt_menu_three.setOnClickListener(this);
        public_chat_lv.setOnClickListener(this);

        lv_menu_one.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getClickMenu(menuVOs1.get(position));
                getListViewVisibilityAll();
                public_chat_lv.setVisibility(View.GONE);
            }
        });
        lv_menu_two.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getClickMenu(menuVOs2.get(position));
                getListViewVisibilityAll();
                public_chat_lv.setVisibility(View.GONE);
            }
        });
        lv_menu_three.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getClickMenu(menuVOs3.get(position));
                getListViewVisibilityAll();
                public_chat_lv.setVisibility(View.GONE);
            }
        });
    }

    private void getClickMenu(MenuVo mv) {
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            if (mv.getType().equals("click")) {
                if (!TextUtils.isEmpty(mv.getName())) {
                    ChatRecordVo record = new ChatRecordVo(friendVo.getFriendAccount(), friendVo.getFriendName(), System.currentTimeMillis() + "", 0, friendVo.getPicture(), 2, mv.getName(), AdminUtils.getUserInfo(context).getAccount(), "0", "", "");
                    recordDb.insertRecord(record, false);
                    if (FriendListFragment.handler != null) {
                        FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                    }
                    MsgXmlVo mxVo = new MsgXmlVo();
                    mxVo.setEvent("click");
                    mxVo.setEventKey(mv.getKey());
                    mxVo.setCreateTime(System.currentTimeMillis() + "");
                    String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_EVENT, mxVo.getCreateTime());
                    SendMsgUtil.sendMessage(friendVo.getFriendAccount(), sendxml);
                    listAdapter.msgChatVos = msgChatVos;
                    listAdapter.notifyDataSetChanged();
                    listview.setSelection(listview.getBottom());
                }
            } else {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL, mv.getUrl());
                intent.putExtra(WebViewActivity.TITLE, mv.getName());
                startActivity(intent);
            }
        } else {
            Utils.showToast(context, "网络不可用,请连接网络！");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.chatcontrols_send_bt:
            String msg = chatControls.getChatcontrols_inputbox_et().getText().toString().trim();
            if (TextUtils.isEmpty(msg)) {
                Utils.showToast(context, "消息内容不能为空!");
            } else {
                String currentTimeMillis = System.currentTimeMillis() + "";
                MsgXmlVo mVo = new MsgXmlVo();
                mVo.setContent(msg);
                String xml = XMLUtil.createXML(mVo, MarketApp.SEND_TEXT);

                MsgChatVo mv = new MsgChatVo();
                mv.setContent(xml);
                mv.setToUserName(friendVo.getFriendAccount());
                mv.setFromUserName(AdminUtils.getUserInfo(MarketApp.app).getAccount());
                mv.setCreateTime(currentTimeMillis);
                mv.setMsgType(MarketApp.SEND_TEXT);
                mv.setType(MarketApp.SEND_TEXT);
                mv.setLoginUser(AdminUtils.getUserInfo(context).getAccount());
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    MsgXmlVo mxVo = new MsgXmlVo();
                    mxVo.setContent(msg);
                    mxVo.setMsgType(MarketApp.SEND_TEXT);
                    mxVo.setCreateTime(currentTimeMillis);
                    String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_TEXT, mv.getCreateTime());
                    boolean isSuccess = SendMsgUtil.sendMessage(friendVo.getFriendAccount(), sendxml);
                    if (isSuccess) {
                        mv.setStatus("0");
                    } else {
                        mv.setStatus("1");
                    }
                } else {
                    mv.setStatus("1");
                }
                saveMsgAndUpdateUI(mv);
                chatControls.getChatcontrols_inputbox_et().getEditableText().clear();
            }
            break;
        case R.id.iv_right:// 公众账号
            Intent intent = new Intent(PublicChatActivity.this, PublicAccountDetailsActivity.class);
            friendVo.setIsFriend("true");
            intent.putExtra(MarketApp.FRIEND, friendVo);
            startActivityForResult(intent, 1);
            break;
        case R.id.btn_left:// 返回
            finish();
            break;
        case R.id.active_message_switch_input:
            active_message_input.setVisibility(View.INVISIBLE);
            active_message_menu.setVisibility(View.VISIBLE);
            customViewPage.setVisibility(View.GONE);
            break;
        case R.id.active_message_switch_menu:
            public_chat_lv.setVisibility(View.GONE);
            active_message_menu.setVisibility(View.INVISIBLE);
            active_message_input.setVisibility(View.VISIBLE);
            getListViewVisibilityAll();
            break;
        case R.id.bt_menu_one:
            if (menuVOs1.size() <= 0) {
                public_chat_lv.setVisibility(View.GONE);
                getClickMenu(menuVos.get(0));
                getListViewVisibilityAll();
                return;
            }
            public_chat_lv.setVisibility(View.VISIBLE);
            Animation loadAnimation;
            if (!lv1) {
                loadAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_come);
                lv_menu_one.startAnimation(loadAnimation);
                getListViewVisibility(lv_menu_one);
                lv1 = true;
            } else {
                getListViewVisibilityAll();
                public_chat_lv.setVisibility(View.GONE);
            }
            break;
        case R.id.bt_menu_two:
            if (menuVOs2.size() <= 0) {
                public_chat_lv.setVisibility(View.GONE);
                getClickMenu(menuVos.get(1));
                getListViewVisibilityAll();
                return;
            }
            public_chat_lv.setVisibility(View.VISIBLE);
            if (!lv2) {
                loadAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_come);
                lv_menu_two.startAnimation(loadAnimation);
                getListViewVisibility(lv_menu_two);
                lv2 = true;
            } else {
                getListViewVisibilityAll();
                public_chat_lv.setVisibility(View.GONE);
            }
            break;
        case R.id.bt_menu_three:
            if (menuVOs3.size() <= 0) {
                public_chat_lv.setVisibility(View.GONE);
                getClickMenu(menuVos.get(2));
                getListViewVisibilityAll();
                return;
            }
            public_chat_lv.setVisibility(View.VISIBLE);
            if (!lv3) {
                loadAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_come);
                lv_menu_three.startAnimation(loadAnimation);
                getListViewVisibility(lv_menu_three);
                lv3 = true;
            } else {
                getListViewVisibilityAll();
                public_chat_lv.setVisibility(View.GONE);
            }
            break;
        case R.id.public_chat_lv:
            getListViewVisibilityAll();
            public_chat_lv.setVisibility(View.GONE);
            break;
        case R.id.chatcontrols_select_bt:
            customViewPage.getRl_facechoose().setVisibility(View.GONE);
            CustomViewPage.et_input = chatControls.getChatcontrols_inputbox_et();
            MarketApp.whichPage = 2;
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
        case R.id.active_message_voice:
            active_message_voice.setVisibility(View.GONE);
            chatControls.setVisibility(View.GONE);
            customViewPage.setVisibility(View.GONE);
            active_message_keyboard.setVisibility(View.VISIBLE);
            active_message_speek.setVisibility(View.VISIBLE);
            break;
        case R.id.active_message_keyboard:
            active_message_voice.setVisibility(View.VISIBLE);
            chatControls.setVisibility(View.VISIBLE);
            active_message_keyboard.setVisibility(View.GONE);
            active_message_speek.setVisibility(View.GONE);
            break;
        }
    }

    private long saveMsgAndUpdateUI(MsgChatVo msgChatVo) {
        MsgChatVo mv = new MsgChatVo(MarketApp.MESSAGE_TIME, msgChatVo.getCreateTime(), msgChatVo.getFromUserName(), msgChatVo.getToUserName(), "", AdminUtils.getUserInfo(context).getAccount(), "0", MarketApp.MESSAGE_TIME);
        long id = messageDb.getCreatMessageDate(mv);
        if (id > 0) {
            mv.setId(id + "");
            msgChatVos.add(mv);
        }
        id = messageDb.insertNewMessage(msgChatVo);
        msgChatVo.setId(id + "");
        MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(msgChatVo.getContent());
        msgChatVo.setXmlVo(xmlVo);
        msgChatVos.add(msgChatVo);
        listAdapter.msgChatVos = msgChatVos;
        listAdapter.notifyDataSetChanged();
        listview.setSelection(listview.getBottom());
        if (FriendListFragment.handler != null) {
            FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
        }
        return id;
    }

    /**
     * 获取自定义菜单的信息
     */
    private void getCustomMenu() {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("empId", friendVo.getFriendId());
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
            }

            @Override
            public void onComplete(String resulte) {
                if (null != resulte) {
                    ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);
                    if (rVo != null) {
                        String result = rVo.getResult();
                        if (result != null && result.equals("success")) {
                            TypeToken<ResultVoMsgArray<ArrayList<MenuVo>>> typeToken = new TypeToken<ResultVoMsgArray<ArrayList<MenuVo>>>() {
                            };
                            ResultVoMsgArray<ArrayList<MenuVo>> rvma = ResultParser.parseJSON(resulte, typeToken);
                            ArrayList<MenuVo> list = rvma.getMsg();
                            menuVos = list;
                            if (menuVos != null) {
                                for (int i = 0; i < menuVos.size(); i++) {
                                    MenuVo menuVo = new MenuVo(menuVos.get(i).getType(), menuVos.get(i).getName(), menuVos.get(i).getKey(), menuVos.get(i).getUrl(), menuVos.get(i).getKeyword());
                                    menuVo.setEmpid(friendVo.getFriendId());
                                    customMenuDb.insert(menuVo, context);
                                    ArrayList<MenuVo> subMenus = menuVos.get(i).getSubMenus();
                                    for (int j = 0; j < subMenus.size(); j++) {
                                        menuVo = new MenuVo(subMenus.get(j).getType(), subMenus.get(j).getName(), subMenus.get(j).getKey(), subMenus.get(j).getUrl(), subMenus.get(j).getKeyword());
                                        menuVo.setParentid(menuVos.get(i).getKey());
                                        menuVo.setEmpid(friendVo.getFriendId());
                                        customMenuDb.insert(menuVo, context);
                                    }
                                }
                                for (int i = 0; i < menuVos.get(0).getSubMenus().size(); i++) {
                                    menuVOs1.add(menuVos.get(0).getSubMenus().get(i));
                                }
                                for (int i = 0; i < menuVos.get(1).getSubMenus().size(); i++) {
                                    menuVOs2.add(menuVos.get(1).getSubMenus().get(i));
                                }
                                for (int i = 0; i < menuVos.get(2).getSubMenus().size(); i++) {
                                    menuVOs3.add(menuVos.get(2).getSubMenus().get(i));
                                }
                                handler.sendEmptyMessage(MarketApp.HANDLERMESS_THREE);
                            }
                            return;
                        } else {
                            MyLogger.commLog().e("自定义菜单：" + resulte);
                        }
                    }
                }
                handler.sendEmptyMessage(MarketApp.HANDLERMESS_FOUR);
            }

            @Override
            public void onCancel() {
            }
        }, maps, MarketApp.CREATEMENU_METHOD, MarketApp.USERMENU_SERVICE, TaskConstant.GET_DATA_20);
    }

    private void getListViewVisibilityAll() {
        lv_menu_one.setVisibility(View.GONE);
        lv_menu_two.setVisibility(View.GONE);
        lv_menu_three.setVisibility(View.GONE);
        lv1 = lv2 = lv3 = false;
    }

    private void getListViewVisibility(ListView lv_menu) {
        public_chat_lv.setVisibility(View.VISIBLE);
        lv_menu_one.setVisibility(View.INVISIBLE);
        lv_menu_two.setVisibility(View.INVISIBLE);
        lv_menu_three.setVisibility(View.INVISIBLE);
        lv_menu.setVisibility(View.VISIBLE);
        lv1 = lv2 = lv3 = false;
    }

    static class PublicChatHandler extends Handler {
        WeakReference<PublicChatActivity> mActivity;

        public PublicChatHandler(PublicChatActivity activity) {
            mActivity = new WeakReference<PublicChatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PublicChatActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
            case MarketApp.HANDLERMESS_ZERO:
                ArrayList<MsgChatVo> mVos = (ArrayList<MsgChatVo>) msg.obj;
                if (null != mVos) {
                    for (int i = 0; i < mVos.size(); i++) {
                        MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(mVos.get(i).getContent());
                        mVos.get(i).setXmlVo(xmlVo);
                    }
                    DBindex = DBindex + activity.msgChatVos.size();
                    activity.msgChatVos.addAll(mVos);
                    activity.listview.setSelection(activity.listview.getBottom());
                }
                break;
            case MarketApp.HANDLERMESS_ONE:
                // 上拉刷新后界面添加数据
                activity.totalCount = DBindex;
                ArrayList<MsgChatVo> msgCvos = activity.messageDb.getOperationalUserMessage(friendVo.getFriendAccount(), activity.totalCount, false);
                for (int i = 0; i < activity.msgChatVos.size(); i++) {
                    MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(msgCvos.get(i).getContent());
                    msgCvos.get(i).setXmlVo(xmlVo);
                    activity.msgChatVos.add(i, msgCvos.get(i));
                }
                activity.listAdapter.notifyDataSetChanged();
                // 诉它数据加载完毕;
                activity.mPullDownView.notifyDidMore();
                // 告诉它更新完毕
                activity.mPullDownView.RefreshComplete();
                activity.listview.setSelection(activity.msgChatVos.size());
                break;
            case MarketApp.HANDLERMESS_TWO:
                // 删除信息
                activity.msgChatVos.remove(MarketApp.index);
                if (MarketApp.indexBool) {
                    activity.msgChatVos.remove(MarketApp.index - 1);
                    MarketApp.indexBool = false;
                }
                activity.listAdapter.msgChatVos = activity.msgChatVos;
                activity.listAdapter.notifyDataSetChanged();
                break;
            case MarketApp.HANDLERMESS_THREE:
                // 加载自定义菜单的数据
                activity.active_message_switch_input.setVisibility(View.VISIBLE);
                activity.bt_menu_one.setText(activity.menuVos.get(0).getName());
                activity.bt_menu_two.setText(activity.menuVos.get(1).getName());
                activity.bt_menu_three.setText(activity.menuVos.get(2).getName());
                activity.lv_menu_one.setAdapter(new CustomMenuAdapter(activity,activity.menuVOs1));
                activity.lv_menu_two.setAdapter(new CustomMenuAdapter(activity,activity.menuVOs2));
                activity.lv_menu_three.setAdapter(new CustomMenuAdapter(activity,activity.menuVOs3));
                break;
            case MarketApp.HANDLERMESS_FOUR:
                activity.active_message_switch_input.setVisibility(View.GONE);
                break;
            case MarketApp.HANDLERMESS_FIVE:
                // 接受修改自定义菜单的消息
                String event = (String) msg.getData().getString("event");// 接受msg传递过来的参数
                if (event.equals(MarketApp.EVENT_DELETE) || event.equals(MarketApp.EVENT_CREATE)) {
                    activity.menuVOs1.clear();
                    activity.menuVOs3.clear();
                    activity.public_chat_lv.setVisibility(View.GONE);
                    activity.getListViewVisibilityAll();
                    if (event.equals(MarketApp.EVENT_CREATE)) {
                        if (MarketApp.network_available && NetUtils.hasNetwork()) {
                            activity.getCustomMenu();
                        }
                    }
                }
                break;
            case MarketApp.HANDLERMESS_SEVEN:
                // 存储发送图片的消息
                String filePath = msg.getData().getString("filePath");// 接受msg传递过来的参数
                MsgXmlVo mVo = new MsgXmlVo();
                mVo.setPicUrl(filePath);
                String mediaId = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.indexOf(".jpg"));
                mVo.setMediaId(mediaId);
                String xml = XMLUtil.createXML(mVo, MarketApp.SEND_PIC);

                MsgChatVo mv = new MsgChatVo();
                mv.setContent(xml);
                mv.setToUserName(friendVo.getFriendAccount());
                mv.setFromUserName(AdminUtils.getUserInfo(activity).getAccount());
                mv.setMsgType(MarketApp.SEND_PIC);
                mv.setType(MarketApp.SEND_PIC);
                mv.setCreateTime(System.currentTimeMillis() + "");
                mv.setLoginUser(AdminUtils.getUserInfo(activity).getAccount());
                activity.customViewPage.setVisibility(View.GONE);
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    mv.setStatus("0");
                    long id = activity.saveMsgAndUpdateUI(mv);
                    String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
                    String uid = AdminUtils.getUserInfo(activity).getUid();
                    HashMap map = new HashMap();
                    map.put("id",id);
                    new FileUploadTask(filePath, FileUploadTask.FROM_PUBLIC_CHAT, map).execute(url, FileUploadTask.FILE_TYPE_IMAGE, uid);
                } else {
                    mv.setStatus("1");
                    activity.saveMsgAndUpdateUI(mv);
                }
                break;
            case MarketApp.HANDLERMESS_EIGHT:
                // 软键盘先消失，然后自定义的选择菜单栏出现
                activity.customViewPage.setVisibility(View.VISIBLE);
                activity.customViewPage.getViewpage_content().setVisibility(View.VISIBLE);
                activity.listview.setAdapter(activity.listAdapter);
                activity.listAdapter.notifyDataSetChanged();
                activity.listview.setSelectionFromTop(activity.listAdapter.getCount(), -3000);
                break;
            case MarketApp.HANDLERMESS_NINE:
                // 文件上传成功后向服务器发送的消息(图片、语音、视频)
                FileVo fVo = (FileVo) msg.obj;
                if (fVo == null) {
                    activity.messageDb.update(msg.arg2 + "", "1");
                    activity.msgChatVos.get(activity.msgChatVos.size() - 1).setStatus("1");
                } else {
                    String path = fVo.getPath();
                    String fileCode = fVo.getFileCode();
                    mVo = new MsgXmlVo();
                    xml = "";
                    String currentTimeMillis = System.currentTimeMillis() + "";
                    switch (msg.arg1) {
                    case 1:
                        // voice
                        mVo.setMsgType(MarketApp.SEND_VOICE);
                        mVo.setVoiceUrl(path);
                        mVo.setFormat("amr");
                        mVo.setMediaId(fileCode);
                        mVo.setCreateTime(currentTimeMillis);
                        xml = XMLUtil.createXML(mVo, MarketApp.SEND_VOICE, currentTimeMillis);
                        break;
                    case 2:
                        // video
                        mVo.setMsgType(MarketApp.SEND_VIDEO);
                        mVo.setVideoUrl(path);
                        mVo.setMediaId(fileCode);
                        mVo.setCreateTime(currentTimeMillis);
                        xml = XMLUtil.createXML(mVo, MarketApp.SEND_VIDEO, currentTimeMillis);
                        break;
                    case 3:
                        // image
                        mVo.setMsgType(MarketApp.SEND_PIC);
                        mVo.setPicUrl(path);
                        mVo.setMediaId(fileCode);
                        mVo.setCreateTime(currentTimeMillis);
                        xml = XMLUtil.createXML(mVo, MarketApp.SEND_PIC, currentTimeMillis);
                        break;
                    }
                    if (MarketApp.network_available && NetUtils.hasNetwork()) {
                        boolean isSuccess = SendMsgUtil.sendMessage(friendVo.getFriendAccount(), path);
                        if (isSuccess) {
                            activity.messageDb.update(msg.arg2 + "", "0");
                            activity.msgChatVos.get(activity.msgChatVos.size() - 1).setStatus("0");
                        } else {
                            activity.messageDb.update(msg.arg2 + "", "1");
                            activity.msgChatVos.get(activity.msgChatVos.size() - 1).setStatus("1");
                        }
                    } else {
                        Utils.showToast(activity, "网络不可用,请连接网络！");
                        activity.messageDb.update(msg.arg2 + "", "1");
                        activity.msgChatVos.get(activity.msgChatVos.size() - 1).setStatus("1");
                    }
                }
                activity.listAdapter.msgChatVos = activity.msgChatVos;
                activity.listAdapter.notifyDataSetChanged();
                break;
            case MarketApp.HANDLERMESS_TEN:
                // 上传视频文件的消息
                String currentTimeMillis = System.currentTimeMillis() + "";
                String videoPath = (String) msg.obj;
                mVo = new MsgXmlVo();
                mVo.setVideoUrl(videoPath);
                mediaId = videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.indexOf(".mp4"));
                mVo.setMediaId(mediaId);
                xml = XMLUtil.createXML(mVo, MarketApp.SEND_VIDEO);

                mv = new MsgChatVo();
                mv.setContent(xml);
                mv.setToUserName(friendVo.getFriendAccount());
                mv.setFromUserName(AdminUtils.getUserInfo(activity).getAccount());
                mv.setMsgType(MarketApp.SEND_VIDEO);
                mv.setType(MarketApp.SEND_VIDEO);
                mv.setLoginUser(AdminUtils.getUserInfo(activity).getAccount());
                mv.setCreateTime(currentTimeMillis);
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    mv.setStatus("0");
                    long id = activity.saveMsgAndUpdateUI(mv);
                    String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
                    String uid = AdminUtils.getUserInfo(activity).getUid();
                    HashMap map = new HashMap();
                    map.put("id",id);
                    new FileUploadTask(videoPath, FileUploadTask.FROM_PUBLIC_CHAT, map).execute(url, FileUploadTask.FILE_TYPE_VIDEO, uid);
                } else {
                    mv.setStatus("1");
                    activity.saveMsgAndUpdateUI(mv);
                }
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
                xml = XMLUtil.createXML(mVo, MarketApp.SEND_LOCATION);

                mv = new MsgChatVo();
                mv.setContent(xml);
                mv.setMsgType(MarketApp.SEND_LOCATION);
                mv.setType(MarketApp.SEND_LOCATION);
                mv.setToUserName(friendVo.getFriendAccount());
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
                    mxVo.setCreateTime(currentTimeMillis);
                    String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_LOCATION, currentTimeMillis);
                    boolean isSuccess = SendMsgUtil.sendMessage(friendVo.getFriendAccount(), sendxml);
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
    protected void onResume() {
        isActive = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isActive = false;
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isClose) {
            finish();
        }
    }

    @Override
    public void onRefresh() {
        if (blean) {
            if (totalCount > MarketApp.COUNT) {
                handler.sendEmptyMessage(MarketApp.HANDLERMESS_ONE);
            } else {
                mPullDownView.RefreshComplete();
            }
        } else {
            blean = true;
            // 告诉它更新完毕
            mPullDownView.RefreshComplete();
        }
    }

    @Override
    public void onMore() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.active_message_speek) {
            int action = event.getAction();
            switch (action) {
            case MotionEvent.ACTION_DOWN:
                active_message_speek.setPressed(true);
                active_message_layout_voice.setVisibility(View.VISIBLE);
                startRecording();
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                active_message_speek.setPressed(false);
                active_message_layout_voice.setVisibility(View.GONE);
                stopRecording();
                MsgXmlVo msgXmlVo = new MsgXmlVo();
                msgXmlVo.setVoiceUrl(voiceFilePath);
                String mediaId = voiceFilePath.substring(voiceFilePath.lastIndexOf("/") + 1, voiceFilePath.indexOf(".amr"));
                msgXmlVo.setMediaId(mediaId);
                msgXmlVo.setFormat("amr");
                String xml = XMLUtil.createXML(msgXmlVo, MarketApp.SEND_VOICE);

                MsgChatVo msgChatVo = new MsgChatVo();
                msgChatVo.setType(MarketApp.SEND_VOICE);
                msgChatVo.setCreateTime(System.currentTimeMillis() + "");
                msgChatVo.setContent(xml);
                msgChatVo.setToUserName(friendVo.getFriendAccount());
                msgChatVo.setFromUserName(AdminUtils.getUserInfo(MarketApp.app).getAccount());
                msgChatVo.setMsgType(MarketApp.SEND_VOICE);
                msgChatVo.setLoginUser(AdminUtils.getUserInfo(MarketApp.app).getAccount());
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    msgChatVo.setStatus("0");
                    long id = saveMsgAndUpdateUI(msgChatVo);
                    String url = MarketApp.WEBSERVICE_SERVER + "/servlet/fileUploadServlet";
                    String uid = AdminUtils.getUserInfo(this).getUid();
                    HashMap map = new HashMap();
                    map.put("id",id);
                    new FileUploadTask(voiceFilePath, FileUploadTask.FROM_PUBLIC_CHAT, map).execute(url, FileUploadTask.FILE_TYPE_VOICE, uid);
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
            public_chat_volume.setText(volume + "");
            mVoiceHandler.postDelayed(mUpdateMicStatusTimer, 500);
        }
    }

    private final Handler mVoiceHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };
}
