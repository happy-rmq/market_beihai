package com.lenovo.market.activity.home;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.adapter.ShareAdapter;

/**
 * 分享
 * 
 * @author muqiang
 * 
 */
public class ShareActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

    private GridView layout_share_gv;
    private Button layout_share_cancel;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_share);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void findViewById() {
        layout_share_gv = (GridView) findViewById(R.id.layout_share_gv);
        layout_share_gv.setAdapter(new ShareAdapter());
        layout_share_cancel = (Button) findViewById(R.id.layout_share_cancel);
    }

    @Override
    protected void setListener() {
        layout_share_gv.setOnItemClickListener(this);
        layout_share_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.layout_share_cancel:
            finish();
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position) {
        case 0:
            intent = new Intent(this, ShareFriendsCircleActivity.class);
            startActivity(intent);
            break;
        case 1:
            intent = new Intent(this, ShareFriendActivity.class);
            startActivity(intent);
            break;
        case 2:
            intent = new Intent(this, ShareGroupActivity.class);
            startActivity(intent);
            break;
        }
        finish();
    }
}
