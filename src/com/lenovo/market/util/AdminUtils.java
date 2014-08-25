package com.lenovo.market.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.lenovo.market.activity.login.LoginActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.UserDBHelper;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.platform.xmpp.XmppUtils;

public class AdminUtils {

    /**
     * 注销当前账号
     */
    public static void logout(Activity activity) {
        if (XmppUtils.getInstance().getConnection() != null && XmppUtils.getInstance().getConnection().isConnected()) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    XmppUtils.getInstance().getConnection().disconnect();
                }
            }).start();
        }
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra("isLogout", true);
        SharedPreferences sp = activity.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Activity.MODE_PRIVATE);
        sp.edit().remove(MarketApp.LOGIN_ACCOUNT).commit();
        activity.startActivity(intent);
    }

    public static boolean isLogin(Context context) {
        boolean isLogin = MarketApp.uid == null ? false : true;
        if (!isLogin) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
        return isLogin;
    }

    // public static boolean isLogin(Context context, boolean isLoginDialog) {
    // boolean isLogin = MarketApp.uid == null ? false : true;
    // if (!isLogin) {
    // Intent intent = null;
    // if (isLoginDialog) {
    // intent = new Intent(context, LoginDialogActivity.class);
    // } else {
    // intent = new Intent(context, LoginActivity.class);
    // }
    // context.startActivity(intent);
    // }
    // return isLogin;
    // }

    /**
     * 邮箱格式校验
     * 
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email))
            return false;
        Pattern pattern = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 手机号格式校验
     * 
     * @param phone
     * @return
     */
    public static boolean isPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone))
            return false;
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    /**
     * 返回当前登录用户信息
     * 
     * @return
     */
    public static UserVo getUserInfo(Context context) {
        UserVo userInfo = MarketApp.userInfo;
        if (null == context) {
            return userInfo;
        }
        if (userInfo == null) {
            SharedPreferences sp = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
            String account = sp.getString(MarketApp.LOGIN_ACCOUNT, "");
            if (TextUtils.isEmpty(account)) {
                return userInfo;
            }
            UserDBHelper helper = new UserDBHelper();
            userInfo = helper.getUserInfo(account);
        }
        return userInfo;
    }

    public static String getOperationalAccount(Context context) {
        if (null == context) {
            context = MarketApp.app;
        }
        if (null == context) {
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
        String account = sp.getString(MarketApp.OPERATIONAL_ACCOUNT, null);
        return account;
    }

    public static String getOperationalUid(Context context) {
        if(context ==null){
            MyLogger.commLog().w("context 为null，无法获取从SharedPreferences中获取OperationalUid");
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
        String uid = sp.getString(MarketApp.OPERATIONAL_UID, "");
        return uid;
    }

    /**
     * 保存上次更新好友通讯录时间
     * 
     * @param context
     */
    public static void saveUpdateFriendInfoTime(Context context) {
        String key = MarketApp.SHARED_PREFERENCES_FRIEND_LIST + "_" + AdminUtils.getUserInfo(context).getAccount();
        SharedPreferences sp = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putLong(key, System.currentTimeMillis());
        edit.commit();
    }

    /**
     * 获取上次更新好友通讯录的时间
     * 
     * @param context
     * @return 时间
     */
    public static long getUpdateFriendInfoTime(Context context) {
        String key = MarketApp.SHARED_PREFERENCES_FRIEND_LIST + "_" + AdminUtils.getUserInfo(context).getAccount();
        SharedPreferences sp = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
        long time = sp.getLong(key, -1);
        return time;
    }

    /**
     * 保存聊天室最后一条消息的时间
     * 
     * @param context
     */
    public static void saveGroupChatTime(Context context) {
        if (null != AdminUtils.getUserInfo(context) && !TextUtils.isEmpty(AdminUtils.getUserInfo(context).getAccount())) {
            String name = MarketApp.SHARED_PREFERENCES_GROUPCHAT + "_" + AdminUtils.getUserInfo(context).getAccount();
            SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            Editor edit = sp.edit();

            ChatRecordDBHelper recordDbHelper = new ChatRecordDBHelper();
            ArrayList<ChatRecordVo> messageList = recordDbHelper.getGroupChatRecordList();
            ChatRecordVo vo;
            for (int i = 0; i < messageList.size(); i++) {
                vo = messageList.get(i);
                if (null != vo && vo.getRoomId() != null) {
                    edit.putString(vo.getRoomId(), vo.getCreateTime());
                }
            }
            edit.commit();
        }
    }

    /**
     * 获取指定聊天室的上次在线的最后一条记录的时间
     * 
     * @param context
     * @param room
     * @return
     */
    public static String getGroupChatTimeFromSP(Context context, String room) {
        String name = MarketApp.SHARED_PREFERENCES_GROUPCHAT + "_" + AdminUtils.getUserInfo(context).getAccount();
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        String time = sp.getString(room, null);
        return time;
    }
}
