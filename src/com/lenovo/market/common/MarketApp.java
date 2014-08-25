package com.lenovo.market.common;

import java.util.ArrayList;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.jivesoftware.smack.SmackAndroid;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lenovo.market.R;
import com.lenovo.market.util.FaceConversionUtil;
import com.lenovo.market.util.MyLogger;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.xjpsd.model.ActionBarMenuModel;

/**
 * https://www.bugsense.com/dashboard bug分析后台 更改账号的话只需要更改api_key=yourkey即可，可以使用google账号登录
 */
@ReportsCrashes(formKey = "", formUri = "http://www.bugsense.com/api/acra?api_key=699fe2d5", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_error_report_toast_text)
public class MarketApp extends Application {

    /**
     * 字符编码
     */
    public static final String StrEncode = "utf-8";
    /**
     * webservice服务器地址
     */
    public static final String WEBSERVICE_SERVER = "http://mp.vservice.com.cn";
    //    public static final String WEBSERVICE_SERVER = "http://219.149.144.231";//山西
    // public static final String WEBSERVICE_SERVER = "http://124.207.35.138";
    // public static final String WEBSERVICE_SERVER = "http://192.168.1.75:8081";


    /**
     * openfire服务器参数
     */
    // public static final String OPENFIRE_SERVER = "124.207.35.137";
    public static final String OPENFIRE_SERVER = "58.215.56.97";
    //    public static final String OPENFIRE_SERVER = "219.149.144.231";//山西

    public static final String OPENFIRE_SERVER_NAME = "lenovo-137";
    /**
     * 端口号
     */
    public static final int PORT = 5222;

    /**
     * 文件服务器上群组头像目录
     */
    public static final String GROUP_IMG_REMOTE_PATH = WEBSERVICE_SERVER + "/upload/mas/group/";

    public static String uid;
    /**
     * 账号
     */
    public static String account;
    public static String expoId;
    public static MarketApp app;
    public static UserVo userInfo;// 用户
    public static Boolean network_available = true;// 监听网络状态
    public static int index;// 删除时来记录下标
    public static boolean indexBool = false;// 记录最后一条信息是不是时间
    public static int whichPage;// 记录自定义CustomViewPage是哪个页面调用的
    public static ArrayList<ActionBarMenuModel> leftList = new ArrayList<ActionBarMenuModel>();// 轻应用左导航

    public static final String NAMESPACE = "http://webservice.mas.lenovo.com";
    public static final int LOGIN_ERROR_PWD = 0;
    public static final int LOGIN_ERROR_NET = 1;
    public static final int LOGIN_SUCC = 2;
    public static final int REPEAT_LOGIN = 3;

    public static final String ROOM_SERVER_NAME = "conference." + OPENFIRE_SERVER_NAME;// 群部分的消息
    public static final String FRIEND_DEFAULT_GROUPNAME = "Friends";// 现在没有分组的概念
    public static final String EXHIBITOR_GROUPNAME = "exhibitor";// 参展商
    public static final String RESOURCE_ANDROID = "android";

    /**
     * 用户的在线状态
     */
    public static final int USER_ONLINE = 0;
    public static final int USER_LEAVE = 1;
    public static final int USER_BUSY = 2;
    public static final int USER_OFFLINE = 3;
    public static final int USER_BLOCK = 4;
    public static final int USER_INVALID = 5;
    public static final int USER_UNKOWN = 7;

    /**
     * 网络是否能用 当为false时，网络不可用，当为true时，网络可用，其值的变化在广播里改变它
     */
    public static boolean NETWORK_OK = false;

    /**
     * /service/UserService
     */
    public static final String USERSERVICE = WEBSERVICE_SERVER + "/service/UserService";
    /**
     * 登录验证 isExistUser
     */
    public static final String LOGIN_METHODNAME = "isExistUser";
    /**
     * 注册 registerUser
     */
    public static final String REGISTER_METHODNAME = "registerUser";
    /**
     * 保存用户名 saveUserName
     */
    public static final String SAVEUSERNAME_METHODNAME = "saveUserName";
    /**
     * 用户信息 getUserInfo
     */
    public static final String GETUSERINFO_METHODNAME = "getUserInfo";
    /**
     * 上传用户头像 saveUserPhoto
     */
    public static final String SAVEUSERPHOTO_METHODNAME = "saveUserPhoto";
    /**
     * 保存用户性别 saveUserSex
     */
    public static final String SAVEUSERSEX_METHODNAME = "saveUserSex";
    /**
     * 保存个性签名 saveUserSign
     */
    public static final String SAVEUSERSIGN_METHODNAME = "saveUserSign";
    /**
     * 保存反馈建议 saveSuggest
     */
    public static final String SAVESUGGEST_METHODNAME = "saveSuggest";
    /**
     * 轻应用模式二级菜单
     */
    public static final String GETCOLUMN = "getColumn";
    /**
     * 通讯录管理 UserFriendService
     */
    public static final String USER_FRIEND_SERVICE = WEBSERVICE_SERVER + "/service/UserFriendService";
    /**
     * 好友列表 userFriendList
     */
    public static final String USER_FRIEND_LIST_METHODNAME = "userFriendList";
    /**
     * 查找好友 findFriendList
     */
    public static final String FIND_FRIEND_LIST_METHODNAME = "findFriendList";
    /**
     * 查找公众账号 findPubList
     */
    public static final String FIND_PUB_LIST_METHODNAME = "findPubList";
    /**
     * 添加好友(账户) addFriendByAccount
     */
    public static final String ADD_FRIEND_BY_ACCOUNT_METHODNAME = "addFriendByAccount";
    /**
     * 删除好友(账户) deleteFriendByAccount
     */
    public static final String DELETE_FRIEND_BY_ACCOUNT_METHODNAME = "deleteFriendByAccount";
    /**
     * 公众账号列表 userPubList
     */
    public static final String USER_PUB_LIST_METHODNAME = "userPubList";
    /**
     * 查找账号信息 findUser
     */
    public static final String FINDUSER = "findUser";
    /**
     * 获取企业通讯录信息 getAddressBook
     */
    public static final String GET_ADDRESSBOOK_METHODNAME = "getAddressBook";

    /**
     * 朋友圈
     */
    public static final String FRIENDSQUARE = WEBSERVICE_SERVER + "/service/ZoneService";
    /**
     * 发送消息
     */
    public static final String FRIENDSQUARE_SENDMESSAGE = "saveMFriendZoneTopic";
    /**
     * 获取消息
     */
    public static final String FRIENDSQUARE_GETMESSAGE = "getZoneTopic";
    /**
     * 添加评论及赞
     */
    public static final String FRIENDSQUARE_SENDCOMMENT = "saveZoneComment";
    /**
     * 删除评论及赞
     */
    public static final String FRIENDSQUARE_CLEARCOMMENT = "deleteZoneTopicComment";
    /**
     * 删除主信息
     */
    public static final String FRIENDSQUARE_CLEARMESS = "deleteZoneTopic";
    /**
     * 更新数据
     */
    public static final String FRIENDSQUARE_UPDMESSAGE = "getTopComments";

    /**
     * 组聊管理 GroupService
     */
    public static final String GROUP_SERVICE = WEBSERVICE_SERVER + "/service/GroupService";
    /**
     * 创建群组，并把用户加入到群组中 createGroup
     */
    public static final String CREATEGROUP_METHOD = "createGroup";
    /**
     * 删除群组 deleteGroup
     */
    public static final String DELETE_GROUP_METHOD = "deleteGroup";
    /**
     * 更新群组名称 updateGroup
     */
    public static final String UPDATEGROUP_METHOD = "updateGroup";
    /**
     * 用户加入群组 addUser
     */
    public static final String ADDUSER_METHOD = "addUser";
    /**
     * 群组里的用户
     */
    public static final String USERLIST_METHOD = "userList";
    /**
     * 从群组中删除用户 deleteUser
     */
    public static final String DELETEUSER_METHOD = "deleteUser";
    /**
     * 群组中用户修改昵称 updateUser
     */
    public static final String UPDATEUSER_METHOD = "updateUser";
    /**
     * 获取房间列表 groupList
     */
    public static final String GROUPLIST_METHOD = "groupList";

    /**
     * 公众账号自定义菜单
     */
    public static final String USERMENU_SERVICE = WEBSERVICE_SERVER + "/service/UserMenuService";
    /**
     * 获取所有信息
     */
    public static final String CREATEMENU_METHOD = "createMenu";

    /**
     * 应用管理 AppInfoService
     */
    public static final String APP_INFO_SERVICE = WEBSERVICE_SERVER + "/service/AppInfoService";
    /**
     * 获取服务器段的apk版本号
     */
    public static final String GET_VERSION = "getVersion";

    /**
     * 接收到的消息类型
     */
    public static final String MESSAGETYPE = "messageType";
    /**
     * 普通用户单聊消息
     */
    public static final String MESSAGETYPE_NORMALSINGLE = "normalSingle";
    /**
     * 群组邀请消息
     */
    public static final String MESSAGETYPE_GROUPINVITATION = "groupInvitation";
    /**
     * 离开房间消息
     */
    public static final String MESSAGETYPE_GROUPCHAT_LEAVEROOM = "groupChat_leaveRoom";
    /**
     * 向房间添加成员
     */
    public static final String MESSAGETYPE_GROUPCHAT_ADDMEMBER = "groupChat_addMember";
    /**
     * 移除房间成员
     */
    public static final String MESSAGETYPE_GROUPCHAT_KICKMEMBER = "groupChat_kickMember";

    // adapter里type
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int SIX = 6;
    public static final int SEVEN = 7;
    public static final int EIGHT = 8;
    public static final int NINE = 9;
    public static final int TEN = 10;
    public static final int ELEVEN = 11;
    public static final int TWELVE = 12;
    public static final int THIRTEEN = 13;
    public static final int FOURTEEN = 14;
    public static final int FIFTEEN = 15;

    public static final int NETWORK_DISCONNECTED = 1002;// 网络断开连接
    public static final int NETWORK_CONNECTED = 1003;// 网络已连接

    public static final String SEND_NAVI = "navi";// 特殊导航
    public static final String RECEIVE_NAVI = "rnavi";// 特殊导航
    public static final String MESSAGE_TIME = "time"; // 显示时间
    public static final String MESSAGE_NOTICE = "notice"; // 群组通知类型
    public static final String SEND_VOICE = "voice";// 语音sender
    public static final String RECEIVE_VOICE = "rvoice";// 语音receiver
    public static final String SEND_TEXT = "text";// 显示自己文本信息的发送
    public static final String RECEIVE_TEXT = "rtext";// 显示好友文本信息的发送
    public static final String SEND_SHARE = "share";// 显示自己分享的内容
    public static final String RECEIVE_SHARE = "rshare";// 显示好友分享的内容
    public static final String SEND_PIC = "image";// 显示自己图片信息的发送
    public static final String RECEIVE_PIC = "rimage";// 显示好友图片信息的发送
    public static final String SEND_BUSINESSCARD = "card";// 显示自己名片信息的发送
    public static final String RECEIVE_BUSINESSCARD = "rcard";// 显示好友名片信息的发送
    public static final String SEND_VIDEO = "video";// video发送
    public static final String RECEIVE_VIDEO = "rvideo";// video receive
    public static final String SEND_NEWS = "news";// 发送的图文
    public static final String RECEIVE_NEWS = "rnews";// 图文(单)
    public static final String RECEIVE_NEWSS = "rnewss";// 图文(多)
    public static final String RECEIVE_MUSIC = "rmusic";// 音乐
    public static final String SEND_EVENT = "event";// 自定义菜单
    public static final String SEND_LOCATION = "location";// 显示自己地理位置消息
    public static final String RECEIVE_LOCATION = "rlocation";// 显示好友地理位置消息

    /**
     * menu消息类型
     */
    public static final String MSG_TYPE_MENU = "menu";
    /**
     * 创建菜单事件
     */
    public static final String EVENT_CREATE = "create";
    /**
     * 删除菜单事件
     */
    public static final String EVENT_DELETE = "delete";

    // MessageDBHelper
    public static final int COUNT = 15;

    public static final String IS_GUIDED = "isGuided";
    public static final String GUIDESP = "guidesp";

    // AdminUtils
    public static final String SHARED_PREFERENCES_LENOVO = "lenovo";
    public static final String HOME_PAGE = "home_page";
    public static final String REMEMBER_ACCOUNT = "remember_account";
    public static final String LOGIN_ACCOUNT = "login_account";
    public static final String SHARED_PREFERENCES_GROUPCHAT = "groupchat";
    public static final String SHARED_PREFERENCES_FRIEND_LIST = "friend_list";

    /**
     * 运营账号
     */
    public static final String OPERATIONAL_ACCOUNT = "operational_account";
    /**
     * 运营账号uid
     */
    public static final String OPERATIONAL_UID = "operational_uid";

    /**
     * handler消息
     */
    public static final int HANDLERMESS_ZERO = 0;
    public static final int HANDLERMESS_ONE = 1;
    public static final int HANDLERMESS_TWO = 2;
    public static final int HANDLERMESS_THREE = 3;
    public static final int HANDLERMESS_FOUR = 4;
    public static final int HANDLERMESS_FIVE = 5;
    public static final int HANDLERMESS_SIX = 6;
    public static final int HANDLERMESS_SEVEN = 7;
    public static final int HANDLERMESS_EIGHT = 8;
    public static final int HANDLERMESS_NINE = 9;
    public static final int HANDLERMESS_TEN = 10;
    public static final int HANDLERMESS_ELEVEN = 11;
    public static final int UPDATE_MESSAGE_LIST = 500;// 更新好友列表

    public static final String FRIEND = "friend";

    // FriendListActivity + ContactsFragment
    public static boolean needUpdateContacts_ = false;// 是否需要刷新联系人列表
    public static String PID = "";// 是否是子评论

    // addFriendActivity
    public static boolean sendAddFriend = false;

    public static final String VERSION_CODE = "version_code";
    public static int screen_height;
    public static int screen_width;

    /**
     * 普通好友
     */
    public static final String FRIEND_TYPE_FRIEND = "1";
    /**
     * 公众账号
     */
    public static final String FRIEND_TYPE_PUBLIC = "2";

    public static RequestQueue queue;// volley queue

    @Override
    public void onCreate() {
        ACRA.init(this);// crash信息收集库初始化
        SmackAndroid.init(this);
        super.onCreate();
        MyLogger.commLog().e("onCreate");
        app = this;
        initFaceFile();
        calculateScreenSize();
        queue = Volley.newRequestQueue(this);
    }

    @SuppressWarnings("deprecation")
    private void calculateScreenSize() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screen_height = display.getHeight();
        screen_width = display.getHeight();
    }

    /**
     * 读取表情配置文件
     */
    private void initFaceFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceConversionUtil.getInstace().getFileText(app);
            }
        }).start();
    }

    @Override
    public void onTerminate() {
        MyLogger.commLog().e("onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        MyLogger.commLog().e("onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        MyLogger.commLog().e("onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}
