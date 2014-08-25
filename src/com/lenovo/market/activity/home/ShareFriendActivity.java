package com.lenovo.market.activity.home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.adapter.FriendVoAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.view.SideBarView;
import com.lenovo.market.view.SideBarView.OnTouchingLetterChangedListener;
import com.lenovo.market.vo.server.FriendMesVo;

/**
 * 分享（朋友列表）
 * 
 * @author muqiang
 * 
 */
public class ShareFriendActivity extends BaseActivity implements OnTouchingLetterChangedListener, OnClickListener, OnItemClickListener {

    public ArrayList<FriendMesVo> friends;
    public static Handler handler;

    private ListView lvShow;
    private TextView overlay;
    private SideBarView myView;
    private FriendVoAdapter adapter;
    private OverlayThread overlayThread = new OverlayThread();
    private FriendInfoDBHelper friendInfoDB_;

    private class OverlayThread implements Runnable {

        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_contacts);
        setTitleBarText(R.string.title_contacts);
        setTitleBarLeftBtnText();
        friendInfoDB_ = new FriendInfoDBHelper();
    }

    @Override
    protected void findViewById() {
        // 设置底部选择栏不可见
        View view = findViewById(R.id.footer);
        view.setVisibility(View.GONE);

        lvShow = (ListView) findViewById(R.id.lvShow);
        myView = (SideBarView) findViewById(R.id.myView);
        overlay = (TextView) findViewById(R.id.tvLetter);
        lvShow.setTextFilterEnabled(true);
        overlay.setVisibility(View.INVISIBLE);

        View headerLayout = getLayoutInflater().inflate(R.layout.layout_contactslist_header, null);
        EditText search = (EditText) headerLayout.findViewById(R.id.et_search);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        lvShow.addHeaderView(headerLayout);

        friends = friendInfoDB_.getFriendAll(MarketApp.FRIEND_TYPE_FRIEND);
        Collections.sort(friends);
        adapter = new FriendVoAdapter(ShareFriendActivity.this,R.drawable.icon,friends, lvShow);
        lvShow.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        myView.setOnTouchingLetterChangedListener(this);
        lvShow.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:
            finish();
            break;
        }
    }

    private int alphaIndexer(String s) {
        int position = -1;
        if (null == friends)
            return position;
        Locale locale = Locale.getDefault();
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getPy().toUpperCase(locale).startsWith(s)) {
                position = i;
                break;
            }
        }
        return position + 1;
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        overlay.setText(s);
        overlay.setVisibility(View.VISIBLE);
        handler.removeCallbacks(overlayThread);
        handler.postDelayed(overlayThread, 1000);
        if (alphaIndexer(s) > 0) {
            int position = alphaIndexer(s);
            Log.i("coder", "position:" + position);
            lvShow.setSelection(position);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FriendMesVo friend = (FriendMesVo) parent.getAdapter().getItem(position);
        Intent intent = new Intent(context, ShareDialogActivity.class);
        intent.putExtra(MarketApp.FRIEND, friend);
        startActivity(intent);
        finish();
    }
}
