package com.lenovo.market.activity.login;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.Utils;

/**
 * 填写手机号码
 * 
 * @author zhouyang
 * 
 */
public class RegisterMessageActivity extends BaseActivity implements OnClickListener {

    private EditText phonenum_;
    private Button btn_next_;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_register_message_1);
        setTitleBarText("填写手机号");
        setTitleBarLeftBtnText();
    }

    @Override
    protected void findViewById() {
        phonenum_ = (EditText) findViewById(R.id.phonenum);
        btn_next_ = (Button) findViewById(R.id.btn_next);
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_next_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
        case R.id.btn_next:
            String phone = phonenum_.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                Utils.showToast(this, "手机号不能为空");
                break;
            } else {
                if (phone.length() != 11) {
                    Utils.showToast(this, "手机号长度应为11位");
                    break;
                } else {
                    if (!AdminUtils.isPhoneNumber(phone)) {
                        Utils.showToast(this, "手机号格式错误");
                        break;
                    }
                }
            }
            intent = new Intent(this, RegisterMessageStep2Activity.class);
            intent.putExtra("phonenum", phone);
            startActivity(intent);
            break;
        case R.id.btn_left:
            finish();
            break;
        }
    }
}
