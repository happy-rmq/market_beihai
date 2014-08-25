package com.lenovo.platform.zxing;

import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lenovo.market.activity.circle.friends.PublicAccountDetailsActivity;
import com.lenovo.market.activity.contacts.FriendDetailsActivity;
import com.lenovo.market.activity.home.WebViewActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.ResultVo;

public class HandleResultHelper {

    public static final String TYPE = "type";
    public static final String RESULT = "result";

    private Activity activity;

    public HandleResultHelper(Activity activity) {
        super();
        this.activity = activity;
    }

    TaskListener taskListener = new TaskListener() {

        @Override
        public void onError(int errorCode, String message) {
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onComplete(String result) {
        }

        @Override
        public void onComplete(String resultstr, int type) {
            ResultVo rVo = ResultParser.parseJSON(resultstr, ResultVo.class);
            switch (type) {
                case TaskConstant.GET_DATA_29:

                    if (rVo != null) {
                        String result = rVo.getResult();
                        if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                            FriendMesVo friend = ResultParser.parseJSON(rVo.getMsg().toString(), FriendMesVo.class);
                            if (null != friend) {
                                Intent intent = new Intent(activity, PublicAccountDetailsActivity.class);
                                intent.putExtra(MarketApp.FRIEND, friend);
                                activity.startActivity(intent);
                            } else {
                                Utils.showToast(activity, "无法查找到此号码");
                            }

                        } else {
                            Utils.showToast(activity, "无法查找到此号码");
                        }
                    }
                    if (!activity.isFinishing()) {
                        activity.finish();
                    }
                    break;
                case TaskConstant.GET_DATA_31:
                    if (rVo != null) {
                        String result = rVo.getResult();
                        if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                            FriendMesVo friend = ResultParser.parseJSON(rVo.getMsg().toString(), FriendMesVo.class);
                            if (null != friend) {
                                Intent intent = new Intent(activity, FriendDetailsActivity.class);
                                intent.putExtra(MarketApp.FRIEND, friend);
                                activity.startActivity(intent);
                            } else {
                                Utils.showToast(activity, "无法查找到此号码");
                            }

                        } else {
                            Utils.showToast(activity, "无法查找到此号码");
                        }
                    }
                    if (!activity.isFinishing()) {
                        activity.finish();
                    }
                    break;
            }
        }

        ;
    };

    public void handleResult(Result result, int type) {
        if (null == result) {
            return;
        }
        BarcodeFormat format = result.getBarcodeFormat();
        if (format == BarcodeFormat.QR_CODE) {
            String text = result.getText();
            if (!TextUtils.isEmpty(text)) {
                if (text.startsWith("http://")) {
                    // 网址
                    handleUrlResult(text);
                } else if (text.startsWith("TEL:")) {
                    // 电话号码
                    handlePhoneNumberResult(text);
                } else if (text.startsWith("SMSTO:")) {
                    // SMS短信
                } else if (text.startsWith("WIFI:")) {
                    // WiFi网络
                } else if (text.startsWith("MATMSG:")) {
                    // 电子邮件
                } else if (text.startsWith("BEGIN:VCARD")) {
                    // 电子名片
                } else {
                    // 文本
                    handleTextResult(text);
                }
            }
        } else {
            Utils.showToast(activity, "您扫描的不是二维码");
        }
    }

    private void handleUrlResult(String text) {
        Intent intent = null;
        if (text.contains("service=hyk") || text.contains("service=uid")) {
            text = text + "&uid=" + AdminUtils.getUserInfo(activity).getUid();
            intent = new Intent(activity, WebViewActivity.class);
            intent.putExtra(WebViewActivity.URL, text);
        } else if (text.contains("service=from")) {
            text = text + "&from=vService";
            intent = new Intent(activity, WebViewActivity.class);
            intent.putExtra(WebViewActivity.URL, text);
        } else {
            intent = new Intent(activity, HandleResultActivity.class);
            intent.putExtra(TYPE, "网址");
            intent.putExtra(RESULT, text);
        }
        activity.startActivity(intent);
        activity.finish();
    }

    private void handlePhoneNumberResult(String text) {
        Intent intent = new Intent(activity, HandleResultActivity.class);
        intent.putExtra(TYPE, "电话号码");
        intent.putExtra(RESULT, text);
        activity.startActivity(intent);
        activity.finish();
    }

    private void handleTextResult(String text) {
        if (!TextUtils.isEmpty(text)) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            if (text.startsWith("#")) {
                // 普通账号
                map.put("uid", MarketApp.uid);
                map.put("findAccount", text);
                NetUtils.startTask(taskListener, map, MarketApp.FINDUSER, MarketApp.USER_FRIEND_SERVICE, TaskConstant.GET_DATA_31);
            } else {
                // 公众账号
                map.put("uid", MarketApp.uid);
                map.put("findAccount", text);
                NetUtils.startTask(taskListener, map, MarketApp.FINDUSER, MarketApp.USER_FRIEND_SERVICE, TaskConstant.GET_DATA_29);
            }
        }
    }
}
