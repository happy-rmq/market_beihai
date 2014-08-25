package com.lenovo.market.util;

import java.util.LinkedHashMap;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.activity.login.LoginActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.UserDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.platform.xmpp.XmppLogin;
import com.lenovo.platform.xmpp.XmppUtils;

public class LoginUtils {

    public static void login(final Context context, final String account, final String pwd) {
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(context, "账号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(context, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("account", account);
        maps.put("pwd", pwd);
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {

            }

            @Override
            public void onComplete(String resulte) {
                ResultVo rVo = ResultParser.parseJSON(resulte, ResultVo.class);
                final Intent intent = new Intent();
                if (rVo != null) {
                    String result = rVo.getResult();
                    Log.i("result--->", rVo.getResult());
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        UserVo userVo = ResultParser.parseJSON(rVo.getMsg().toString(), UserVo.class);
                        if (userVo != null) {
                            userVo.setPassword(pwd);
                            MarketApp.userInfo = userVo;
                            MarketApp.uid = userVo.getUid();
                            userVo.setCompanyId("1000323");
                            UserDBHelper userDb = new UserDBHelper();
                            userDb.saveUserInfo(userVo);

                            // 存储运营id和运营账号
                            //                            String companyId = userVo.getCompanyId();
                            //                            String companyId="1000323";
                            SharedPreferences sp = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            String servId = "ba57b8e1463d46ca0146425e9b375fce";
                            if (!TextUtils.isEmpty(servId)) {
                                //                                String servId = userVo.getServId();
                                //                                String servAccount = userVo.getServAccount();
                                String servAccount = "gxbeihai";
                                edit.putString(MarketApp.OPERATIONAL_UID, servId);
                                edit.putString(MarketApp.OPERATIONAL_ACCOUNT, servAccount);
                            } else {
                                String defaultServId = userVo.getDefaultServId();
                                String defaultServAccount = userVo.getDefaultServAccount();
                                if (!TextUtils.isEmpty(defaultServId)) {
                                    edit.putString(MarketApp.OPERATIONAL_UID, defaultServId);
                                }
                                if (!TextUtils.isEmpty(defaultServAccount)) {
                                    edit.putString(MarketApp.OPERATIONAL_ACCOUNT, defaultServAccount);
                                }
                            }
                            edit.commit();
                        }
                        new Thread() {
                            public void run() {
                                int userLogin = XmppLogin.getInstance().userLogin(Utils.getSendName(account), pwd);
                                // 自动登录失败就跳转登录界面
                                if (userLogin == MarketApp.LOGIN_SUCC) {
                                    intent.setClass(context, ViewPaperMenuActivity.class);
                                } else {
                                    intent.setClass(context, LoginActivity.class);
                                }
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        }.start();
                    } else {
                        String errmsg = rVo.getErrmsg();
                        errmsg = (TextUtils.isEmpty(errmsg)) ? "登录失败！" : errmsg;
                        Utils.showToast(context, errmsg);
                        // 自动登录失败就跳转登录界面
                        intent.setClass(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                } else {
                    // 若服务器返回数据格式不正确则跳转登录界面
                    intent.setClass(context, LoginActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onCancel() {

            }
        }, maps, MarketApp.LOGIN_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_1);
    }

    public static void getPersonalInfo() {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
            }

            @Override
            public void onComplete(String resulte) {
                ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        Object msg = rVo.getMsg();
                        if (msg != null) {
                            UserVo userVo = (UserVo) ResultParser.parseJSON(msg.toString(), UserVo.class);

                            if (userVo != null) {
                                MarketApp.userInfo = userVo;
                                UserDBHelper userDb = new UserDBHelper();
                                userVo.setCompanyId("1000323");
                                userDb.saveUserInfo(userVo);

                                XMPPConnection connection = XmppUtils.connection;
                                try {
                                    if (null != connection && connection.isConnected()) {
                                        UpdateVCard.updateInformation(connection, userVo);
                                    }
                                } catch (XMPPException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        String errmsg = rVo.getErrmsg();
                        errmsg = (TextUtils.isEmpty(errmsg)) ? "获取用户信息失败" : errmsg;
                    }
                }
            }

            @Override
            public void onCancel() {
            }
        }, maps, MarketApp.GETUSERINFO_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_6);
    }
}
