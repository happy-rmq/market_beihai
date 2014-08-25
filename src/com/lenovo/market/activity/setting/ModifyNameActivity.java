package com.lenovo.market.activity.setting;

import java.util.LinkedHashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.LoginUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.ResultVo;

public class ModifyNameActivity extends BaseActivity implements OnClickListener {

    public static final String USER_NAME = "user_name";

    private EditText et_name_;// 名字文本框
    private ProgressDialog pd;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_settings_modifyname);
        setTitleBarText(R.string.title_modifyname);
        setTitleBarRightBtnText(R.string.title_save);
        setTitleBarLeftBtnText();
    }

    @Override
    protected void findViewById() {
        et_name_ = (EditText) findViewById(R.id.et_name);
        String name = getIntent().getStringExtra(USER_NAME);
        if (!TextUtils.isEmpty(name)) {
            et_name_.setText(name);
        }
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_right_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:
            finish();
            break;
        case R.id.btn_right:
            saveName();
            break;
        }
    }

    private void saveName() {
        String name = et_name_.getText().toString().trim();
        // 数据校验
        if (TextUtils.isEmpty(name)) {
            Utils.showToast(this, "名字不能为空");
            return;
        }
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        maps.put("userName", name);
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
                    log.d("result--->" + result);
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        Intent data = new Intent();
                        data.putExtra(USER_NAME, et_name_.getText().toString().trim());
                        ModifyNameActivity.this.setResult(RESULT_OK, data);
                        // 修改webservice后同步到openfire
                        LoginUtils.getPersonalInfo();
                        Utils.showToast(context, "修改成功！");
                        finish();
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.SAVEUSERNAME_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_8);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在保存名字");
            pd.show();
        }
    }
}
