package com.lenovo.market.activity.contacts;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.Utils;

/**
 * 删除好友
 * 
 * @author zhouyang
 * 
 */
public class FriendDetailMenuActivity extends BaseActivity implements OnClickListener {

    public static final int DELETE = 100;

    private Button btn_cancel_;
    private Button btn_delete_;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_contacts_frienddetails_menudialog);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void findViewById() {
        btn_delete_ = (Button) findViewById(R.id.btn_delete);
        btn_cancel_ = (Button) findViewById(R.id.btn_cancel);
    }

    @Override
    protected void setListener() {
        btn_cancel_.setOnClickListener(this);
        btn_delete_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_cancel:
            finish();
            break;
        case R.id.btn_delete:
            if (MarketApp.network_available && NetUtils.hasNetwork()) {
                setResult(DELETE);
                finish();
            } else {
                Utils.showToast(context, "网络不可用,请连接网络！");
            }
            break;
        }
    }
}
