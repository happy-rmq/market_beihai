package com.lenovo.market.activity.contacts;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.adapter.RoomAdapter;
import com.lenovo.market.dbhelper.RoomDBHelper;
import com.lenovo.market.vo.local.RoomVo;

/**
 * Created by zhouyang on 2014/4/24 0024.
 */
public class GroupChatListActivity extends BaseActivity implements View.OnClickListener {

    private RoomDBHelper dbHelper;
    private ListView listView;
    private ArrayList<RoomVo> rooms;

    @Override
    protected void setContentView() {
        setContentView(R.layout.group_chat_list);
        dbHelper = new RoomDBHelper();
    }

    @Override
    protected void findViewById() {
        setTitleBarText(R.string.contacts_groupChat);
        setTitleBarLeftBtnText();
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupChatListActivity.this, GroupChatActivity.class);
                if (rooms != null) {
                    intent.putExtra("roomId", rooms.get(position).getRoomId());
                }
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        rooms = dbHelper.getRooms();
        RoomAdapter adapter = new RoomAdapter(this, rooms,listView);
        listView.setAdapter(adapter);
        super.onResume();
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