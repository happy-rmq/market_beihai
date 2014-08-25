package com.lenovo.market.activity.setting;

import java.util.LinkedHashMap;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.ResultVo;

public class SuggestActivity extends BaseActivity implements OnClickListener {

    private EditText et_suggest_;// 建议文本框

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_settings_suggest);
        setTitleBarText(R.string.settings_suggest_text);
        setTitleBarLeftBtnText();
        setTitleBarRightBtnText(R.string.title_send);
    }

    @Override
    protected void findViewById() {
        et_suggest_ = (EditText) findViewById(R.id.et_suggest);
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
            saveSuggest();
            break;
        }
    }

    private void saveSuggest() {
        String suggest = et_suggest_.getText().toString().trim();
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        maps.put("suggest", suggest);
        boolean startTask = NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                Utils.showToast(context, "发送反馈建议失败");
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
                        et_suggest_.getText().clear();
                        Utils.showToast(context, "发送反馈建议成功");
                        finish();
                    } else {
                        Utils.showToast(context, "发送反馈建议失败");
                    }
                }
            }

            @Override
            public void onCancel() {
                Utils.showToast(context, "取消发送");
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.SAVESUGGEST_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_13);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在发送反馈建议");
            pd.show();
        }
    }
}
