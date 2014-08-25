package com.lenovo.market.activity.setting;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.UserVo;

public class QRCodeCardActivity extends BaseActivity implements OnClickListener {

    private TextView name_;
    private TextView account_;
    private TextView area_;
    private ImageView icon_;
    private ImageView qr_code_;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_settings_qr_code_card);
        setTitleBarText(R.string.settings_qr_code_card);
        setTitleBarLeftBtnText();
    }

    @Override
    protected void findViewById() {
        icon_ = (ImageView) findViewById(R.id.icon);
        name_ = (TextView) findViewById(R.id.name);
        account_ = (TextView) findViewById(R.id.account);
        area_ = (TextView) findViewById(R.id.area);
        qr_code_ = (ImageView) findViewById(R.id.qr_code);

        UserVo userInfo = AdminUtils.getUserInfo(this);
        if (userInfo != null) {
            String name = userInfo.getUserName();
            String account = userInfo.getAccount();
            String area = userInfo.getArea();
            String pic = userInfo.getPicture();
            String qrCode = userInfo.getQrCode();

            name_.setText(name);
            account = account != null ? "账号:" + account : account;
            account_.setText(account);
            area_.setText(area);
            Utils.downloadImg(true, this, icon_, pic, R.drawable.icon, icon_);
            Utils.downloadImg(true, this, qr_code_, qrCode, Integer.MIN_VALUE, false, qr_code_);
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
