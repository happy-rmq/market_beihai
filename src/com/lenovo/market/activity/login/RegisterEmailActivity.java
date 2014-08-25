package com.lenovo.market.activity.login;

import java.util.LinkedHashMap;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.platform.xmpp.XmppLogin;

public class RegisterEmailActivity extends BaseActivity implements OnClickListener {

    private EditText email_;// 邮箱
    private EditText password_;// 密码
    private EditText password2_;// 确认密码
    private Button submit_;
    private ProgressDialog pd;
    private Locale locale;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_register_email);
        setTitleBarText("填写邮箱");
        setTitleBarLeftBtnText();
        locale = Locale.getDefault();
    }

    @Override
    protected void findViewById() {
        email_ = (EditText) findViewById(R.id.email);
        password_ = (EditText) findViewById(R.id.password);
        password2_ = (EditText) findViewById(R.id.password2);
        submit_ = (Button) findViewById(R.id.btn_submit);
    }

    @Override
    protected void setListener() {
        submit_.setOnClickListener(this);
        btn_left_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    submitForm();
                } else {
                    Utils.showToast(context, "网络不可用,请查看网络连接状态！");
                }
                break;
            case R.id.btn_left:
                finish();
                break;
        }
    }

    private void submitForm() {
        final String email = email_.getText().toString().trim().toLowerCase(locale);
        final String psd = password_.getText().toString().trim();
        String psd2 = password2_.getText().toString();
        // 数据校验
        if (TextUtils.isEmpty(email)) {
            Utils.showToast(this, "邮箱不能为空");
            return;
        } else {
            if (!AdminUtils.isEmail(email)) {
                Utils.showToast(this, "邮箱格式错误");
                return;
            }
        }
        if (TextUtils.isEmpty(psd)) {
            Utils.showToast(this, "密码不能为空");
            return;
        } else {
            if (psd.length() < 6) {
                Utils.showToast(this, "密码长度不能小于6位");
                return;
            }
        }
        if (TextUtils.isEmpty(psd2)) {
            Utils.showToast(this, "确认密码不能为空");
            return;
        }
        if (!psd.equals(psd2)) {
            Utils.showToast(this, "两次输入的密码不一致");
            return;
        }
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("account", email);
        maps.put("password", psd);
        maps.put("captcha", "");// 邮箱注册不需要验证码
        maps.put("regType", "email");// 账号类型
        boolean startTask = NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                if (pd != null)
                    pd.dismiss();
            }

            @Override
            public void onComplete(String resulte) {
                if (pd != null)
                    pd.dismiss();
                ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    log.d("result--->" + rVo.getResult());
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        // 注册到openfire服务器
                        new Thread() {
                            public void run() {
                                String email = email_.getText().toString().trim();
                                String psd = password_.getText().toString().trim();
                                XmppLogin.getInstance().rigisterUser(Utils.getSendName(email), psd);
                            }
                        }.start();
                        Utils.showToast(context, "注册成功！");
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else if (rVo.getErrmsg().equals("用户已存在")) {
                        Utils.showToast(context, "注册成功！");
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        String errmsg = rVo.getErrmsg();
                        errmsg = (TextUtils.isEmpty(errmsg)) ? "注册失败！" : errmsg;
                        Utils.showToast(context, errmsg);
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.REGISTER_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_3);
        if (startTask) {
            pd = Utils.createProgressDialog(context, "正在提交注册信息");
            pd.show();
        }
    }
}
