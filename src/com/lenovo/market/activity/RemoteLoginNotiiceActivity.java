package com.lenovo.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.lenovo.market.R;

/**
 * Created by zhouyang on 2014/7/1 0001.
 */
public class RemoteLoginNotiiceActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_quit;
    private TextView tv_reLogin;

    @Override
    protected void setContentView() {
        setContentView(R.layout.remote_login_notice);
        if(Build.VERSION.SDK_INT >= 11){
            setFinishOnTouchOutside(false);
        }
    }

    @Override
    protected void findViewById() {
        tv_quit = (TextView)findViewById(R.id.tv_quit);
        tv_reLogin = (TextView)findViewById(R.id.tv_reLogin);
    }

    @Override
    protected void setListener() {
        tv_quit.setOnClickListener(this);
        tv_reLogin.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_quit:
                exitApp(this);
                break;
            case R.id.tv_reLogin:
                Intent intent = new Intent(this,SplashActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}