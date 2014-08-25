package com.lenovo.platform.zxing;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;

/**
 * 二维码扫描结果 Created by lyzhou1107 on 13-11-14.
 */
public class HandleResultActivity extends BaseActivity implements OnClickListener {

    private String type;
    private String result;
    private TextView tv_type;
    private TextView tv_result;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_handle_result);
        type = getIntent().getStringExtra(HandleResultHelper.TYPE);
        result = getIntent().getStringExtra(HandleResultHelper.RESULT);
    }

    @Override
    protected void findViewById() {
        setTitleBarText("扫描结果");

        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_result = (TextView) findViewById(R.id.tv_result);
        if (type != null) {
            tv_type.setText(tv_type.getText() + ":" + type);
        }
        if (result != null) {
            if (type != null && type.equals("url")) {
            }
            tv_result.setText(result);
        }
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
        }
    }
}