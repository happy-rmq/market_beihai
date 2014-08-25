package com.lenovo.market.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lenovo.market.R;
import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.activity.contacts.AddFriendListActivity;
import com.lenovo.market.activity.contacts.BusinessContactsActivity;
import com.lenovo.market.activity.contacts.ContactsFragment;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.activity.home.WebHomePageFragment;
import com.lenovo.market.activity.setting.SettingsFragment;
import com.lenovo.market.activity.setting.friendscircle.FriendsCircleActivity;
import com.lenovo.market.adapter.PopupWindowAdapter;
import com.lenovo.market.common.CommonUtil;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.*;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.service.MainService;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.LoginUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.RoomMemberVo;
import com.lenovo.market.vo.local.RoomVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.GroupVo;
import com.lenovo.market.vo.server.PageDateVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;

/**
 * 活动页面
 * 
 * @author muqiang
 */
public class ViewPaperMenuActivity extends FragmentActivity implements OnClickListener {

    public static TextView tv_title, msg_unRead_count, tab_contacts_count, titlebar_text;
    public static ViewPaperMenuHandler handler;
    public static LinearLayout viewpaper_bottom_navigation;
    public static RelativeLayout viewpaper_title;
    public static Button btn_left;
    public static PopupWindow pop;

    private RelativeLayout viewpaper_event, viewpaper_friend, viewpaper_contact, viewpaper_setting;
    private ImageView menu_event, menu_friend, menu_contact, menu_setting;
    private Button btn_right;
    private FrameLayout vp_framelayout, vp_framelayout1, vp_framelayout2, vp_framelayout3;
    private boolean b1, b2, b3;
    private ChatRecordDBHelper recordDb;
    private RoomDBHelper roomDB;
    private RoomMemberDBHelper memberDB;
    private NewFriendInfoDBHelper newFriendDB;
    private SharedPreferences sp;
    private FriendInfoDBHelper friendInfoDB_;
    private int num;
    private long stopTime = 0;;
    private long endKeyDownTime;
    private boolean lean;
    private GroupDBHelper groupDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.viewpaper);
        handler = new ViewPaperMenuHandler(this);
        roomDB = new RoomDBHelper();
        memberDB = new RoomMemberDBHelper();
        newFriendDB = new NewFriendInfoDBHelper();
        recordDb = new ChatRecordDBHelper();
        friendInfoDB_ = new FriendInfoDBHelper();
        groupDBHelper = new GroupDBHelper();
        if (!MainService.serviceState) {
            startService(getIntent().setClass(this, MainService.class));
        }
        MainService.allActivity.add(this);

        findViewById();
        sp = getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, MODE_PRIVATE);
        String home_page = sp.getString(MarketApp.HOME_PAGE, "");
        if (TextUtils.isEmpty(home_page)) {
            InitOne();
        } else {
            viewpaper_bottom_navigation.setVisibility(View.GONE);
            viewpaper_title.setVisibility(View.GONE);
            InitTwo();
        }
        setListener();

        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            LoginUtils.getPersonalInfo();
            getRoomList();
            initFriends();
        }
    }

    public void initFriends() {
        long time = AdminUtils.getUpdateFriendInfoTime(this);
        if (time != -1 && System.currentTimeMillis() - time < 1000 * 60 * 60 * 24) {
            // 由于首页加载时间太长，获取通讯录的接口在首页每天只调用一次，保证进入程序后本地有数据即可
            return;
        }
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        maps.put("keystr", null);
        maps.put("currentPageNO", 1);
        maps.put("pageSize", 5000);
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
            }

            @Override
            public void onComplete(String resulte) {
                ResultVo rVo = ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        TypeToken<PageDateVo<FriendMesVo>> typeToken = new TypeToken<PageDateVo<FriendMesVo>>() {
                        };
                        PageDateVo<FriendMesVo> pageDataVo = ResultParser.parseJSON(rVo.getMsg().toString(), typeToken);
                        ArrayList<FriendMesVo> dataList = pageDataVo.getDateList();
                        if (null != dataList) {
                            // 保存好友信息到本地数据库
                            for (FriendMesVo vo : dataList) {
                                vo.setFriendType(1);// 设置好友类型为普通好友
                            }
                            friendInfoDB_.saveFriendList(dataList);
                        }
                        AdminUtils.saveUpdateFriendInfoTime(getApplicationContext());
                    }
                }
            }

            @Override
            public void onCancel() {
            }
        }, maps, MarketApp.USER_FRIEND_LIST_METHODNAME, MarketApp.USER_FRIEND_SERVICE, TaskConstant.GET_DATA_23);
    }

    // 从服务端拉去所在的群组
    private void getRoomList() {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        String uid = AdminUtils.getUserInfo(this).getUid();
        maps.put("uid", uid);
        maps.put("type", "");
        maps.put("currentPageNO", 1);
        maps.put("pageSize", 100);
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                CommonUtil.joinRooms();
            }

            @Override
            public void onComplete(String resulte) {
                ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        UserVo userInfo = AdminUtils.getUserInfo(MarketApp.app);
                        TypeToken<PageDateVo<GroupVo>> typeToken = new TypeToken<PageDateVo<GroupVo>>() {
                        };
                        PageDateVo<GroupVo> pageDataVo = ResultParser.parseJSON(rVo.getMsg().toString(), typeToken);
                        ArrayList<GroupVo> dataList = pageDataVo.getDateList();
                        ArrayList<RoomVo> rooms = roomDB.getRooms();
                        if (null != dataList) {
                            for (RoomVo room : rooms) {
                                String roomId = room.getRoomId();
                                boolean isExit = false;
                                for (GroupVo vo : dataList) {
                                    String gid = vo.getGid();
                                    if (gid != null && roomId != null && gid.equals(roomId)) {
                                        isExit = true;
                                        break;
                                    }
                                }
                                if (!isExit && roomId != null) {
                                    memberDB.deleteRoomMembers(roomId);
                                    recordDb.delete(roomId);
                                    groupDBHelper.delete(roomId);
                                    roomDB.delete(roomId);
                                }
                            }
                            String gid = null;
                            for (GroupVo vo : dataList) {
                                gid = vo.getGid();
                                if (gid != null) {
                                    roomDB.insert(gid, 0);
                                    RoomMemberVo member = new RoomMemberVo();
                                    member.setRoomId(gid);
                                    member.setAccount(userInfo.getAccount());
                                    member.setMemberId(userInfo.getUid());
                                    member.setNickName(userInfo.getAccount());
                                    member.setUserName(userInfo.getUserName());
                                    member.setAvatar(userInfo.getPicture());
                                    memberDB.insert(member);
                                }
                            }
                        } else {
                            for (RoomVo room : rooms) {
                                String roomId = room.getRoomId();
                                memberDB.deleteRoomMembers(roomId);
                                recordDb.delete(roomId);
                                groupDBHelper.delete(roomId);
                            }
                            roomDB.deleteAll();
                        }
                    }
                }
                CommonUtil.joinRooms();
            }

            @Override
            public void onCancel() {
                CommonUtil.joinRooms();
            }
        }, maps, MarketApp.GROUPLIST_METHOD, MarketApp.GROUP_SERVICE, TaskConstant.GET_DATA_36);
    }

    private void InitOne() {
        titlebar_text.setText(R.string.active_menu_event);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomePageFragment activity = new HomePageFragment();
        fragmentTransaction.add(R.id.vp_framelayout, activity);
        fragmentTransaction.commit();
    }

    private void InitTwo() {
        titlebar_text.setText(R.string.active_menu_event);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WebHomePageFragment activity = new WebHomePageFragment();
        fragmentTransaction.add(R.id.vp_framelayout, activity);
        fragmentTransaction.commit();
    }

    private void findViewById() {
        viewpaper_event = (RelativeLayout) findViewById(R.id.viewpaper_event);
        viewpaper_friend = (RelativeLayout) findViewById(R.id.viewpaper_friend);
        viewpaper_contact = (RelativeLayout) findViewById(R.id.viewpaper_contact);
        viewpaper_setting = (RelativeLayout) findViewById(R.id.viewpaper_setting);
        menu_event = (ImageView) findViewById(R.id.menu_event);
        menu_friend = (ImageView) findViewById(R.id.menu_friend);
        menu_contact = (ImageView) findViewById(R.id.menu_contact);
        menu_setting = (ImageView) findViewById(R.id.menu_setting);
        tab_contacts_count = (TextView) findViewById(R.id.tab_contacts_count);
        msg_unRead_count = (TextView) findViewById(R.id.tab_activity_count);
        vp_framelayout = (FrameLayout) findViewById(R.id.vp_framelayout);
        vp_framelayout1 = (FrameLayout) findViewById(R.id.vp_framelayout1);
        vp_framelayout2 = (FrameLayout) findViewById(R.id.vp_framelayout2);
        vp_framelayout3 = (FrameLayout) findViewById(R.id.vp_framelayout3);
        btn_left = (Button) findViewById(R.id.btn_left);
        titlebar_text = (TextView) findViewById(R.id.titlebar_text);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setText("快捷方式");
        viewpaper_bottom_navigation = (LinearLayout) findViewById(R.id.viewpaper_bottom_navigation);
        viewpaper_title = (RelativeLayout) findViewById(R.id.viewpaper_title);

        int unreadCount = newFriendDB.getNewFriendSubscription();
        if (unreadCount > 0) {
            tab_contacts_count.setVisibility(View.VISIBLE);
        }

        int count = recordDb.getUnReadMsgCount();
        if (count > 0) {
            msg_unRead_count.setVisibility(View.VISIBLE);
        } else {
            msg_unRead_count.setVisibility(View.INVISIBLE);
        }
    }

    private void setListener() {
        viewpaper_event.setOnClickListener(this);
        viewpaper_friend.setOnClickListener(this);
        viewpaper_contact.setOnClickListener(this);
        viewpaper_setting.setOnClickListener(this);
        btn_right.setOnClickListener(this);
    }

    private void setBackgroud(int index) {
        menu_event.setBackgroundResource(R.drawable.common_bottomico1);
        menu_friend.setBackgroundResource(R.drawable.common_bottomico2);
        menu_contact.setBackgroundResource(R.drawable.common_bottomico3);
        menu_setting.setBackgroundResource(R.drawable.common_bottomico5);
        vp_framelayout.setVisibility(View.GONE);
        vp_framelayout1.setVisibility(View.GONE);
        vp_framelayout2.setVisibility(View.GONE);
        vp_framelayout3.setVisibility(View.GONE);
        switch (index) {
        case 0:
            menu_event.setBackgroundResource(R.drawable.common_bottomico1new);
            vp_framelayout.setVisibility(View.VISIBLE);
            break;
        case 1:
            menu_friend.setBackgroundResource(R.drawable.common_bottomico2new);
            vp_framelayout1.setVisibility(View.VISIBLE);
            break;
        case 2:
            menu_contact.setBackgroundResource(R.drawable.common_bottomico3new);
            vp_framelayout2.setVisibility(View.VISIBLE);
            break;
        case 3:
            menu_setting.setBackgroundResource(R.drawable.common_bottomico5new);
            vp_framelayout3.setVisibility(View.VISIBLE);
            break;
        }
    }

    /**
     * zl 设置菜单的点击响应
     */
    @Override
    public void onClick(View v) {
        // HomePageFragment homePageFragment = (HomePageFragment)getSupportFragmentManager().findFragmentById(R.id.vp_framelayout);
        // if (null != homePageFragment && null != homePageFragment.customViewPage && homePageFragment.customViewPage.getVisibility() == View.VISIBLE) {
        // homePageFragment.customViewPage.setVisibility(View.GONE);
        // }
        titlebar_text.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.GONE);
        switch (v.getId()) {
        case R.id.viewpaper_event:
            num = 0;
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setText("快捷方式");
            titlebar_text.setText(R.string.active_menu_event);
            setBackgroud(0);
            break;
        case R.id.viewpaper_friend:
            titlebar_text.setText(R.string.active_menu_friend);
            setBackgroud(1);
            if (!b1) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FriendListFragment activity = new FriendListFragment();
                fragmentTransaction.add(R.id.vp_framelayout1, activity);
                fragmentTransaction.commit();
                b1 = true;
            }
            break;
        case R.id.viewpaper_contact:
            num = 1;
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setText("添加朋友");
            titlebar_text.setText(R.string.active_menu_contact);
            setBackgroud(2);
            if (!b2) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ContactsFragment fragment = new ContactsFragment();
                fragmentTransaction.add(R.id.vp_framelayout2, fragment);
                fragmentTransaction.commit();
                b2 = true;
            }
            int count = newFriendDB.getNewFriendSubscription();
            if (count > 0) {
                if (null != ContactsFragment.handler) {
                    ContactsFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);
                }
            }
            break;
        case R.id.viewpaper_setting:
            titlebar_text.setText(R.string.active_menu_setting);
            setBackgroud(3);
            if (!b3) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SettingsFragment fragment = new SettingsFragment();
                fragmentTransaction.add(R.id.vp_framelayout3, fragment);
                fragmentTransaction.commit();
                b3 = true;
            }
            break;
        case R.id.btn_right:
            btn_right.setVisibility(View.VISIBLE);
            if (num == 1) {
                // 通讯录
                Intent intent = new Intent(this, AddFriendListActivity.class);
                startActivity(intent);
            } else {
                long startTime = System.currentTimeMillis();
                if (stopTime == 0 || startTime - stopTime > 500) {
                    View viewP = View.inflate(this, R.layout.view_popupwindow, null);
                    ListView lv_suppiess = (ListView) viewP.findViewById(R.id.lv_supply);
                    UserVo userVo = AdminUtils.getUserInfo(this);
                    ArrayList<String> str = new ArrayList<String>();
                    str.add("扫一扫");
                    str.add("切换首页");
                    if (userVo != null && !TextUtils.isEmpty(userVo.getCompanyId())) {
                        str.add("企业通讯录");
                    }
                    PopupWindowAdapter pAdapter = new PopupWindowAdapter(this, str);
                    lv_suppiess.setAdapter(pAdapter);

                    pop = new PopupWindow(viewP, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
                    pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    pop.showAsDropDown(btn_right, 0, -1);
                    stopTime = System.currentTimeMillis();
                }
            }
            break;
        }
    }

    public static class ViewPaperMenuHandler extends Handler {
        WeakReference<ViewPaperMenuActivity> mActivity;

        public ViewPaperMenuHandler(ViewPaperMenuActivity activity) {
            mActivity = new WeakReference<ViewPaperMenuActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ViewPaperMenuActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
            case MarketApp.HANDLERMESS_ONE:// 切换主页
                activity.num = 0;
                activity.btn_right.setVisibility(View.VISIBLE);
                activity.btn_right.setText("快捷方式");
                titlebar_text.setText(R.string.active_menu_event);
                activity.setBackgroud(0);
                String home_page = activity.sp.getString(MarketApp.HOME_PAGE, "");
                if (!TextUtils.isEmpty(home_page)) {
                    viewpaper_bottom_navigation.setVisibility(View.GONE);
                    viewpaper_title.setVisibility(View.GONE);
                } else {
                    viewpaper_bottom_navigation.setVisibility(View.VISIBLE);
                    viewpaper_title.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.HANDLERMESS_TWO:// 更新新朋友未读消息条数
                SharedPreferences sp = activity.getApplicationContext().getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, MODE_PRIVATE);
                int news = sp.getInt("newFriendCount", 0);
                Editor edit = sp.edit();
                edit.putInt("newFriendCount", ++news);
                edit.commit();
                break;
            case MarketApp.HANDLERMESS_THREE:// 更新未读消息条数
                int count = activity.recordDb.getUnReadMsgCount();
                if (count > 0) {
                    msg_unRead_count.setVisibility(View.VISIBLE);
                } else {
                    msg_unRead_count.setVisibility(View.INVISIBLE);
                }
                break;
            case MarketApp.HANDLERMESS_FOUR:
                int sCount = activity.newFriendDB.getNewFriendSubscription();
                if (sCount > 0) {
                    tab_contacts_count.setVisibility(View.VISIBLE);
                } else {
                    tab_contacts_count.setVisibility(View.INVISIBLE);
                }
                break;
            case MarketApp.HANDLERMESS_FIVE:
                titlebar_text.setText(R.string.active_menu_friend);
                activity.setBackgroud(1);
                if (!activity.b1) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    FriendListFragment fragment = new FriendListFragment();
                    fragmentTransaction.add(R.id.vp_framelayout1, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    activity.b1 = true;
                }
                break;
            case MarketApp.HANDLERMESS_SIX:
                activity.btn_right.setVisibility(View.GONE);
                titlebar_text.setText(R.string.active_menu_setting);
                activity.setBackgroud(3);
                if (!activity.b3) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    SettingsFragment fragment = new SettingsFragment();
                    fragmentTransaction.add(R.id.vp_framelayout3, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    activity.b3 = true;
                }
                Intent intent = new Intent(activity, FriendsCircleActivity.class);
                activity.startActivity(intent);
                break;
            case MarketApp.HANDLERMESS_SEVEN:
                activity.btn_right.setVisibility(View.GONE);
                titlebar_text.setText(R.string.active_menu_setting);
                activity.setBackgroud(3);
                if (!activity.b3) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    SettingsFragment fragment = new SettingsFragment();
                    fragmentTransaction.add(R.id.vp_framelayout3, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    activity.b3 = true;
                }
                break;
            case MarketApp.HANDLERMESS_EIGHT:
                activity.num = 1;
                activity.btn_right.setVisibility(View.VISIBLE);
                activity.btn_right.setText("添加朋友");
                titlebar_text.setText(R.string.active_menu_contact);
                activity.setBackgroud(2);
                if (!activity.b2) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ContactsFragment fragment = new ContactsFragment();
                    fragmentTransaction.add(R.id.vp_framelayout2, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    activity.b2 = true;
                }
                count = activity.newFriendDB.getNewFriendSubscription();
                if (count > 0) {
                    if (null != ContactsFragment.handler) {
                        ContactsFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);
                    }
                }
                break;
            case MarketApp.HANDLERMESS_NINE:
                activity.num = 1;
                activity.btn_right.setVisibility(View.VISIBLE);
                activity.btn_right.setText("添加朋友");
                titlebar_text.setText(R.string.active_menu_contact);
                activity.setBackgroud(2);
                if (!activity.b2) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ContactsFragment fragment = new ContactsFragment();
                    fragmentTransaction.add(R.id.vp_framelayout2, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    activity.b2 = true;
                }
                count = activity.newFriendDB.getNewFriendSubscription();
                if (count > 0) {
                    if (null != ContactsFragment.handler) {
                        ContactsFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);
                    }
                }
                intent = new Intent(activity, BusinessContactsActivity.class);
                activity.startActivity(intent);
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long startTime = System.currentTimeMillis();
            if (endKeyDownTime == 0) {
                Utils.showToast(this, "再按一次退出程序");
                endKeyDownTime = System.currentTimeMillis();
                lean = false;
            } else if (startTime - endKeyDownTime < 3000) {
                lean = true;
                BaseActivity.exitApp(this);
            } else {
                Utils.showToast(this, "再按一次退出程序");
                endKeyDownTime = System.currentTimeMillis();
                lean = false;
            }
        }
        if (lean) {
            return super.onKeyDown(keyCode, event);
        } else {
            return false;
        }
    }
}
