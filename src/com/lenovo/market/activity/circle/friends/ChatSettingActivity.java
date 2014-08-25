package com.lenovo.market.activity.circle.friends;

import java.util.ArrayList;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.adapter.CharSettingAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.Utils;
import com.lenovo.market.view.CustomGridView;
import com.lenovo.market.vo.server.FriendMesVo;

/**
 * 添加群组
 * 
 * @author muqiang
 * 
 */
public class ChatSettingActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private ImageView chatsetting_iv_add;
    private ImageView iv_avatar;
    private FriendMesVo friend_;
    private TextView tv_name_;
    private CustomGridView gridView_;
    private ArrayList<FriendMesVo> list_;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_friend_chatsetting);
        setTitleBarLeftBtnText();
        friend_ = (FriendMesVo) getIntent().getSerializableExtra(MarketApp.FRIEND);
    }

    @Override
    protected void findViewById() {
        chatsetting_iv_add = (ImageView) findViewById(R.id.chatsetting_iv_add);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        tv_name_ = (TextView) findViewById(R.id.tv_name);
        gridView_ = (CustomGridView) findViewById(R.id.gridview);

        if (null != friend_) {
            String friendName = friend_.getFriendName();
            String picture = friend_.getPicture();
            if (!TextUtils.isEmpty(friendName)) {
                tv_name_.setText(friendName);
            }
            Utils.downloadImg(true, context, iv_avatar, picture, R.drawable.icon, iv_avatar);
        }
        initData();
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        chatsetting_iv_add.setOnClickListener(this);
        gridView_.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.chatsetting_iv_add:
            Intent intent = new Intent(context, FriendSelectActivity.class);
            intent.putExtra(MarketApp.FRIEND, friend_);
            startActivityForResult(intent, 100);
            break;
        case R.id.btn_left:// 返回
            if (ChatActivity.friend == null) {
                ChatActivity.friend = friend_;
            }
            finish();
            break;
        }
    }

    private void initData() {
        list_ = new ArrayList<FriendMesVo>();
        list_.add(friend_);
        FriendMesVo addVo = new FriendMesVo("");
        list_.add(addVo);

        CharSettingAdapter adapter = new CharSettingAdapter(list_, gridView_);
        gridView_.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case RESULT_OK:
            break;
        case RESULT_FIRST_USER:
            setResult(RESULT_FIRST_USER);
            finish();
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == list_.size() - 1) {
            Intent intent = new Intent(context, FriendSelectActivity.class);
            intent.putExtra(MarketApp.FRIEND, friend_);
            startActivityForResult(intent, 100);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (ChatActivity.friend == null) {
            ChatActivity.friend = friend_;
        }
        return super.onKeyDown(keyCode, event);
    }
}
