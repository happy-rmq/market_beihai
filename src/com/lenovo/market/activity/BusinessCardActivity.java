package com.lenovo.market.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
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
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.adapter.FriendVoAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.view.SideBarView;
import com.lenovo.market.view.SideBarView.OnTouchingLetterChangedListener;
import com.lenovo.market.vo.server.FriendMesVo;

public class BusinessCardActivity extends BaseActivity implements OnTouchingLetterChangedListener, OnItemClickListener, OnClickListener {

    private ListView lvShow;
    private TextView overlay;
    private SideBarView myView;
    private FriendVoAdapter adapter;
    private FriendInfoDBHelper friendInfoDB;
    private ArrayList<FriendMesVo> friends;
    private OverlayThread overlayThread;
    private static BusinessCardHandler handler;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_contacts_select_friend);
        setTitleBarText(R.string.title_businesscard);
        setTitleBarLeftBtnText();
        friendInfoDB = new FriendInfoDBHelper();
        overlayThread = new OverlayThread();
        handler = new BusinessCardHandler(this);
    }

    @Override
    protected void findViewById() {
        lvShow = (ListView) findViewById(R.id.lvShow);
        myView = (SideBarView) findViewById(R.id.myView);
        overlay = (TextView) findViewById(R.id.tvLetter);
        lvShow.setTextFilterEnabled(true);
        overlay.setVisibility(View.INVISIBLE);

        View headerLayout = getLayoutInflater().inflate(R.layout.layout_contactslist_header, null);
        EditText search = (EditText) headerLayout.findViewById(R.id.et_search);
        lvShow.addHeaderView(headerLayout);
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
        friends = friendInfoDB.getFriendAll();
        Collections.sort(friends);
        if (friends != null) {
            adapter = new FriendVoAdapter(BusinessCardActivity.this,R.drawable.icon,friends, lvShow);
        }
        lvShow.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        myView.setOnTouchingLetterChangedListener(this);
        btn_left_.setOnClickListener(this);
        lvShow.setOnItemClickListener(this);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        overlay.setText(s);
        overlay.setVisibility(View.VISIBLE);
        handler.removeCallbacks(overlayThread);
        handler.postDelayed(overlayThread, 600);
        if (alphaIndexer(s) > 0) {
            int position = alphaIndexer(s);
            Log.i("coder", "position:" + position);
            lvShow.setSelection(position);
        }
    }

    private class OverlayThread implements Runnable {

        public void run() {
            overlay.setVisibility(View.GONE);
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

    static class BusinessCardHandler extends Handler {
        WeakReference<BusinessCardActivity> mActivity;

        public BusinessCardHandler(BusinessCardActivity activity) {
            mActivity = new WeakReference<BusinessCardActivity>(activity);
        }
    }

    public void updateData(String friendAccount) {
        if (!TextUtils.isEmpty(friendAccount)) {
            String tempAccount = null;
            for (FriendMesVo vo : friends) {
                tempAccount = vo.getFriendAccount();
                if (null != tempAccount && tempAccount.equals(friendAccount)) {
                    friends.remove(vo);
                    Collections.sort(friends);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FriendMesVo friend = (FriendMesVo) parent.getAdapter().getItem(position);
        Message updateMsg = new Message();
        updateMsg.what = MarketApp.HANDLERMESS_TEN;
        Bundle bundle = new Bundle();
        bundle.putSerializable(MarketApp.FRIEND, friend);
        updateMsg.setData(bundle);
        switch (MarketApp.whichPage) {
        case 0:
            ChatActivity.handler.sendMessage(updateMsg);
            break;
        case 1:
            GroupChatActivity.handler.sendMessage(updateMsg);
            break;
        }
        finish();
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
