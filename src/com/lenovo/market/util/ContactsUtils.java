package com.lenovo.market.util;

import java.util.LinkedHashMap;

import android.text.TextUtils;

import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.activity.contacts.ContactsFragment;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.ChatInfoDBHelper;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.dbhelper.MessageDBHelper;
import com.lenovo.market.dbhelper.NewFriendInfoDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.platform.xmpp.XmppFriendList;

public class ContactsUtils {
    /**
     * 告知webservice服务器uAccount和fAccount这两个账号的用户已经添加为好友
     * 
     * @param uAccount
     *            用户账号
     * @param fAccount
     *            朋友账号
     */
    public static void addFriend(final String uAccount, final String fAccount) {
        if (TextUtils.isEmpty(uAccount) || TextUtils.isEmpty(fAccount))
            return;

        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uAccount", uAccount);
        maps.put("fAccount", fAccount);
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onComplete(String resulte) {
                ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    MyLogger.commLog().d("result--->" + result);
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        MyLogger.commLog().e("=====addFriend(" + uAccount + "," + fAccount + ")");
                    }
                }
            }

            public void onError(int errorCode, String message) {
            }

            @Override
            public void onCancel() {
            }
        }, maps, MarketApp.ADD_FRIEND_BY_ACCOUNT_METHODNAME, MarketApp.USER_FRIEND_SERVICE, TaskConstant.GET_DATA_25);
    }

    /**
     * 告知webservice服务器uAccount和fAccount这两个账号的用户已经取消关注
     */
    public static void deleteFriend(final FriendMesVo friend,final boolean blean) {
        if (friend == null || friend.getFriendAccount() == null)
            return;

        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uAccount", AdminUtils.getUserInfo(MarketApp.app).getAccount());
        maps.put("fAccount", friend.getFriendAccount());
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
            }

            @Override
            public void onComplete(String resulte) {
                ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    MyLogger.commLog().d("result--->" + result);
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        XmppFriendList.getInstance().deleteFriendByUserName(Utils.getJidFromUsername(friend.getFriendAccount()));
                        ContactsFragment.handler.obtainMessage(MarketApp.HANDLERMESS_ZERO, friend.getFriendAccount()).sendToTarget();

                        FriendInfoDBHelper fHelper = new FriendInfoDBHelper();
                        MessageDBHelper mHelper = new MessageDBHelper();
                        ChatRecordDBHelper cHelper = new ChatRecordDBHelper();
                        ChatInfoDBHelper chatDb = new ChatInfoDBHelper();
                        NewFriendInfoDBHelper nHelper = new NewFriendInfoDBHelper();

                        fHelper.delete(friend.getFriendAccount());
                        mHelper.delete(friend.getFriendAccount());
                        cHelper.deleteRecordByName(friend.getFriendAccount());
                        nHelper.delete(friend.getFriendAccount());
                        if (!blean) {
                            chatDb.delete(friend.getFriendAccount());
                        }
                        if (FriendListFragment.handler != null) {
                            FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                        }
                    }
                }
            }

            @Override
            public void onCancel() {
            }
        }, maps, MarketApp.DELETE_FRIEND_BY_ACCOUNT_METHODNAME, MarketApp.USER_FRIEND_SERVICE, TaskConstant.GET_DATA_28);
    }
}
