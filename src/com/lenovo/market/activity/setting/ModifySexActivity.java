package com.lenovo.market.activity.setting;

import java.util.LinkedHashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

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

public class ModifySexActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

    public static final String USER_SEX = "user_sex";

    private RadioButton rb_male;
    private RadioButton rb_female;
    private Button btn_ok;
    private String sex_;
    private String initialValue;// 初始值
    private ProgressDialog pd;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_sex_select_dialog);
    }

    @Override
    protected void findViewById() {
        rb_male = (RadioButton) findViewById(R.id.radio_male);
        rb_female = (RadioButton) findViewById(R.id.radio_female);
        btn_ok = (Button) findViewById(R.id.btn_ok);

        String sex = getIntent().getStringExtra(USER_SEX);
        if (!TextUtils.isEmpty(sex)) {
            if (sex.equals("男")) {
                rb_male.setChecked(true);
                sex_ = (String) rb_male.getTag();
            } else {
                rb_female.setChecked(true);
                sex_ = (String) rb_female.getTag();
            }
        } else {
            // 默认选中男
            rb_male.setChecked(true);
            sex_ = (String) rb_male.getTag();
        }
        initialValue = sex_;
    }

    @Override
    protected void setListener() {
        rb_male.setOnCheckedChangeListener(this);
        rb_female.setOnCheckedChangeListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_ok:
            if (sex_.equals(initialValue)) {
                // 如果初始值没有发生变化则不作任何操作
                finish();
                break;
            }
            saveSex();
            break;
        }
    }

    private void saveSex() {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        maps.put("sex", sex_.equals("男") ? "0" : "1");
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
                        data.putExtra(USER_SEX, sex_);
                        setResult(RESULT_OK, data);
                        LoginUtils.getPersonalInfo();
                        Utils.showToast(context, "性别修改成功");
                        finish();
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.SAVEUSERSEX_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_21);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在保存性别");
            pd.show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            sex_ = (String) btn.getTag();
            if (btn == rb_male) {
                rb_female.setChecked(false);
            } else {
                rb_male.setChecked(false);
            }
        }
    }
}
