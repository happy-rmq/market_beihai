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

/**
 * 修改用户签名
 * 
 * @author zhouyang
 * 
 */
public class ModifyUserSignActivity extends BaseActivity implements OnClickListener {

    public static final String M_USER_SIGN = "user_sign";

    private EditText et_sign_;// 名字文本框
    private ProgressDialog pd;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_settings_modifysign);
        setTitleBarText(R.string.title_modifysign);
        setTitleBarRightBtnText(R.string.title_save);
        setTitleBarLeftBtnText();
    }

    @Override
    protected void findViewById() {
        et_sign_ = (EditText) findViewById(R.id.et_sign);

        String sign = getIntent().getStringExtra(M_USER_SIGN);
        if (!TextUtils.isEmpty(sign)) {
            et_sign_.setText(sign);
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
        final String sign = et_sign_.getText().toString().trim();
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        maps.put("sign", sign);
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
                        data.putExtra(M_USER_SIGN, et_sign_.getText().toString().trim());
                        ModifyUserSignActivity.this.setResult(RESULT_OK, data);
                        LoginUtils.getPersonalInfo();
                        Utils.showToast(context, "修改签名成功！");
                        finish();
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.SAVEUSERSIGN_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_22);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在保存个性签名");
            pd.show();
        }
    }
}
