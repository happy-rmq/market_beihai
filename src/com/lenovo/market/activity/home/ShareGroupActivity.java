package com.lenovo.market.activity.home;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.adapter.GroupShareAdapter;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.vo.local.ChatRecordVo;

/**
 * 分享（群组列表）
 * 
 * @author muqiang
 * 
 */
public class ShareGroupActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    public static Handler handler;
    
    private ListView listview;
    private ArrayList<ChatRecordVo> chatRecordVos;
    private GroupShareAdapter adapter;
    private ChatRecordDBHelper gRecordDB;
    private RelativeLayout groupchat_record_title;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_groupchat_record);
        setTitleBarText(R.string.group);
        setTitleBarLeftBtnText();
        gRecordDB = new ChatRecordDBHelper();
    }

    @Override
    protected void findViewById() {
        listview = (ListView) findViewById(R.id.listview);
        groupchat_record_title = (RelativeLayout) findViewById(R.id.groupchat_record_title);
        groupchat_record_title.setVisibility(View.VISIBLE);
        chatRecordVos = gRecordDB.getGroupChatRecordList();
        adapter = new GroupShareAdapter(chatRecordVos);
        listview.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        listview.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChatRecordVo chatRecordVo = (ChatRecordVo) parent.getAdapter().getItem(position);
        Intent intent = new Intent(context, ShareDialogActivity.class);
        intent.putExtra(ShareDialogActivity.GROUP, chatRecordVo);
        startActivity(intent);
        finish();
    }
}
