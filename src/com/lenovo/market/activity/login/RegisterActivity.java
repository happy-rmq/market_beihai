package com.lenovo.market.activity.login;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;

public class RegisterActivity extends BaseActivity implements OnClickListener {

    private RelativeLayout message_;
    private RelativeLayout email_;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_register);
        setTitleBarText("注册");
        setTitleBarLeftBtnText();
    }

    @Override
    protected void findViewById() {
        message_ = (RelativeLayout) findViewById(R.id.register_message);
        email_ = (RelativeLayout) findViewById(R.id.register_email);
    }

    @Override
    protected void setListener() {
        message_.setOnClickListener(this);
        email_.setOnClickListener(this);
        btn_left_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
        case R.id.register_message:
            intent = new Intent(this, RegisterMessageActivity.class);
            startActivity(intent);
            break;
        case R.id.register_email:
            intent = new Intent(this, RegisterEmailActivity.class);
            startActivity(intent);
            break;
        case R.id.btn_left:
            finish();
            break;
        }
    }
}
