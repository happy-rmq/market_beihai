package com.lenovo.market.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.lenovo.market.R;
import com.lenovo.market.activity.login.LoginActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.MarketDBHelper;
import com.lenovo.market.dbhelper.UserDBHelper;
import com.lenovo.market.util.LoginUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.vo.server.UserVo;

public class SplashActivity extends BaseActivity {

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_splash);

        // 初始化本地数据库
        MarketDBHelper.getInstance(this);
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            intoApplication();
        }
    }

    /**
     * 进入应用
     */
    public void intoApplication() {
        // 判断是否进入向导界面
        SharedPreferences sp_guide = context.getSharedPreferences(MarketApp.GUIDESP, Context.MODE_PRIVATE);
        boolean guided = sp_guide.getBoolean(MarketApp.IS_GUIDED, false);
        final Intent intent = new Intent();
        if (!guided) {
            intent.setClass(context, GuideActivity.class);
        } else {
            intent.setClass(context, LoginActivity.class);
            SharedPreferences sp_lenovo = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
            String account = sp_lenovo.getString(MarketApp.LOGIN_ACCOUNT, "");
            if (!TextUtils.isEmpty(account)) {
                UserDBHelper userDb = new UserDBHelper();
                UserVo userVo = userDb.getUserInfo(account);
                if (userVo != null) {
                    String user_account = userVo.getAccount();
                    String password = userVo.getPassword();
                    LoginUtils.login(context, user_account, password);
                    return;
                }
            }
        }
        context.startActivity(intent);
        context.finish();
    }

    @Override
    protected void findViewById() {
    }

    @Override
    protected void setListener() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseActivity.exitApp(this);
        }
        return super.onKeyDown(keyCode, event);
    }
}
