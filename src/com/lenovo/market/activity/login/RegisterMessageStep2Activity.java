package com.lenovo.market.activity.login;

import java.util.LinkedHashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.platform.xmpp.XmppLogin;
import com.lenovo.platform.xmpp.XmppUtils;

/**
 * 注册提交
 * 
 * @author zhouyang
 * 
 */
public class RegisterMessageStep2Activity extends BaseActivity implements OnClickListener {

    private String phone_;
    private TextView tv_account_;
    private EditText code_;
    private EditText password_;// 密码
    private EditText password2_;// 确认密码
    private Button btn_submit_;
    private ProgressDialog pd;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_register_message_2);
        setTitleBarText("填写验证码");
        setTitleBarLeftBtnText("上一步");
        phone_ = getIntent().getStringExtra("phonenum");
    }

    @Override
    protected void findViewById() {
        tv_account_ = (TextView) findViewById(R.id.tv_account);
        if (!TextUtils.isEmpty(phone_)) {
            tv_account_.append(phone_);
        }
        code_ = (EditText) findViewById(R.id.et_verification_code);
        password_ = (EditText) findViewById(R.id.et_password);
        password2_ = (EditText) findViewById(R.id.et_password2);
        btn_submit_ = (Button) findViewById(R.id.btn_submit);
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_submit_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_submit:
            if (!MarketApp.network_available) {
                Utils.showToast(context, "网络不可用,请连接网络！");
                return;
            }
            submitForm();
            break;
        case R.id.btn_left:
            finish();
            break;
        }
    }

    private void submitForm() {
        final String psd = password_.getText().toString().trim();
        String psd2 = password2_.getText().toString();
        String code = code_.getText().toString().trim();
        // 数据校验
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
        maps.put("account", phone_);
        maps.put("password", psd);
        maps.put("captcha", code);// 邮箱注册不需要验证码
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
                                String psd = password_.getText().toString().trim();
                                int rigisterUser = XmppLogin.getInstance().rigisterUser(phone_, psd);

                                if (rigisterUser == 2) {
                                    Utils.showToast(context, "注册成功！");
                                    if (XmppUtils.getInstance().getConnection() != null && XmppUtils.getInstance().getConnection().isConnected()) {
                                        new Thread(new Runnable() {

                                            @Override
                                            public void run() {
                                                XmppUtils.getInstance().getConnection().disconnect();
                                            }
                                        }).start();
                                    }
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Utils.showToast(context, "注册失败！");
                                }
                            }
                        }.start();
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
