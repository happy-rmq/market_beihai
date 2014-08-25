package com.lenovo.market.activity.contacts;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.platform.zxing.CaptureActivity;

/**
 * 添加好友
 * 
 * @author zhouyang
 * 
 */
public class AddFriendListActivity extends BaseActivity implements OnClickListener {

    private LinearLayout searchNumber, scan, searchPublicAccount;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_square_add_friend);
        setTitleBarText(R.string.search_friend_title);
        setTitleBarLeftBtnText();
    }

    @Override
    protected void findViewById() {
        searchNumber = (LinearLayout) findViewById(R.id.square_search_number);
        scan = (LinearLayout) findViewById(R.id.square_search_scan);
        searchPublicAccount = (LinearLayout) findViewById(R.id.search_public_account);
    }

    @Override
    protected void setListener() {
        searchNumber.setOnClickListener(this);
        scan.setOnClickListener(this);
        searchPublicAccount.setOnClickListener(this);
        btn_left_.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
        case R.id.btn_left:
            if (MarketApp.needUpdateContacts_) {
                // 若添加好友了，返回时要发送消息刷新好友列表
                ContactsFragment.handler.obtainMessage(MarketApp.HANDLERMESS_ONE).sendToTarget();
            }
            finish();
            break;
        case R.id.search_public_account:
            intent = new Intent(this, SearchPublicAccountsActivity.class);
            startActivity(intent);
            break;
        case R.id.square_search_scan:
            intent = new Intent(this, CaptureActivity.class);
            startActivity(intent);
            break;
        case R.id.square_search_number:
            intent = new Intent(this, SearchFriendByNumberActivity.class);
            startActivity(intent);
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (MarketApp.needUpdateContacts_) {
                // 若添加好友了，返回时要发送消息刷新好友列表
                ContactsFragment.handler.obtainMessage(MarketApp.HANDLERMESS_ONE).sendToTarget();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
