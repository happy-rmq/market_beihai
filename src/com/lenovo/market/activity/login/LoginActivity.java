package com.lenovo.market.activity.login;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.UserDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.platform.xmpp.XmppLogin;

/**
 * 登陆
 *
 * @author zhouyang
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

    public static LoginHandler handler;
    public static boolean blean;

    private Button bt_login_go;
    private CheckBox cb_login;
    private EditText et_login_name;
    private EditText et_login_pwd;
    private String account;
    private String accountGo;
    private String pwd;
    private Button bt_register_;
    private ProgressDialog pd;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_login);
        handler = new LoginHandler(this);
        sp = getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, MODE_PRIVATE);
    }

    @Override
    protected void findViewById() {
        bt_login_go = (Button) findViewById(R.id.bt_login_go);
        cb_login = (CheckBox) findViewById(R.id.cb_login);
        et_login_name = (EditText) findViewById(R.id.et_login_name);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        bt_register_ = (Button) findViewById(R.id.bt_register_go);
        accountGo = sp.getString(MarketApp.REMEMBER_ACCOUNT, "");
        if (!TextUtils.isEmpty(accountGo)) {
            cb_login.setChecked(true);
            et_login_name.setText(accountGo);
            et_login_pwd.requestFocus();
            et_login_pwd.setSelected(true);
        }
        boolean isLogout = getIntent().getBooleanExtra("isLogout", false);
        if (isLogout) {
            finishActivityExceptCurrent();
        }
    }

    @Override
    protected void setListener() {
        bt_login_go.setOnClickListener(this);
        bt_register_.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login_go:
                account = et_login_name.getText().toString().trim();
                pwd = et_login_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    Utils.showToast(context, "账号不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    Utils.showToast(context, "密码不能为空！");
                    return;
                }
                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                    login();
                } else {
                    Utils.showToast(context, "网络不可用,请查看网络连接状态！");
                }
                break;
            case R.id.bt_register_go:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("account", account);
        maps.put("pwd", pwd);
        boolean startTask = NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                if (pd != null)
                    pd.dismiss();
            }

            @Override
            public void onComplete(String resulte) {
                ResultVo rVo = ResultParser.parseJSON(resulte, ResultVo.class);
                if (rVo != null) {
                    String result = rVo.getResult();
                    log.d("result--->" + rVo.getResult() + "---" + account);
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        UserVo userVo = ResultParser.parseJSON(rVo.getMsg().toString(), UserVo.class);
                        if (userVo != null) {
                            if (TextUtils.isEmpty(userVo.getAccount())) {
                                userVo.setAccount(account);
                            }
                            userVo.setPassword(pwd);
                            MarketApp.userInfo = userVo;
                            MarketApp.uid = userVo.getUid();
                            userVo.setCompanyId("1000323");
                            UserDBHelper userDb = new UserDBHelper();
                            userDb.saveUserInfo(userVo);

                            // 存储运营id和运营账号
                            //                            String servId = userVo.getServId();
                            String servId = "ba57b8e1463d46ca0146425e9b375fce";
                            Editor edit = sp.edit();
                            if (!TextUtils.isEmpty(servId)) {
                                edit.putString(MarketApp.OPERATIONAL_UID, servId);
                                //                                String servAccount = userVo.getServAccount();
                                String servAccount = "gxbeihai";
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

                        // 是否记录账号
                        Editor editor = sp.edit();
                        if (cb_login.isChecked()) {
                            editor.putString(MarketApp.REMEMBER_ACCOUNT, account);
                            editor.commit();
                        } else {
                            editor.remove(MarketApp.REMEMBER_ACCOUNT);
                            editor.commit();
                        }

                        // 登录成功后记录当前用户的账号
                        editor.putString(MarketApp.LOGIN_ACCOUNT, account);
                        editor.commit();

                        new Thread() {
                            public void run() {
                                int userLogin = XmppLogin.getInstance().userLogin(Utils.getSendName(account), pwd);
                                if (pd != null)
                                    pd.dismiss();
                                if (userLogin == MarketApp.LOGIN_SUCC) {
                                    Intent intent = new Intent(LoginActivity.this, ViewPaperMenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }.start();
                    } else {
                        if (pd != null)
                            pd.dismiss();
                        String errmsg = rVo.getErrmsg();
                        errmsg = (TextUtils.isEmpty(errmsg)) ? "登录失败！" : errmsg;
                        Utils.showToast(context, errmsg);
                    }
                } else {
                    if (pd != null)
                        pd.dismiss();
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.LOGIN_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_1);
        if (startTask) {
            pd = Utils.createProgressDialog(context, "正在登录……");
            pd.show();
        }
    }

    public static class LoginHandler extends Handler {
        WeakReference<LoginActivity> mActivity;

        public LoginHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
                // 拉取展会信息
                case MarketApp.HANDLERMESS_ZERO:
                    activity.login();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseActivity.exitApp(this);
        }
        return super.onKeyDown(keyCode, event);
    }
}
