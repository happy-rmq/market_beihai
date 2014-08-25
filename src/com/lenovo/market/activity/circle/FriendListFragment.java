package com.lenovo.market.activity.circle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.lenovo.market.R;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.activity.circle.friends.PublicChatActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.adapter.FriendListAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.server.FriendMesVo;

/**
 * 圈子好友部分
 * 
 * @author muqiang
 * 
 */
public class FriendListFragment extends Fragment implements OnClickListener {

    public static Handler handler;

    private ListView listView;
    private FriendListAdapter adapter;
    private ChatRecordDBHelper recordDb;
    private ArrayList<ChatRecordVo> recordList;
    private FriendInfoDBHelper friendInfoDb;
    private View netErrorLayout;
    private Button view_details;
    private ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.layout_friend_fragment, container, false);
        pd = Utils.createProgressDialog(getActivity(), "正在加载数据中......");
        pd.show();
        setContentView();
        findViewById(inflate);
        return inflate;
    }

    private void setContentView() {
        recordList = new ArrayList<ChatRecordVo>();
        recordDb = new ChatRecordDBHelper();
        friendInfoDb = new FriendInfoDBHelper();
        handler = new MyHandler(this);
    }

    private void findViewById(View inflate) {
        netErrorLayout = inflate.findViewById(R.id.net_error_layout);
        view_details = (Button) inflate.findViewById(R.id.btn_view_details);
        listView = (ListView) inflate.findViewById(R.id.active_listview);
        setListener();
        initDatas();
    }

    private void setListener() {
        view_details.setOnClickListener(this);
    }

    private void initDatas() {
        if (!MarketApp.network_available) {
            netErrorLayout.setVisibility(View.VISIBLE);
        }
        recordList = recordDb.getAllRecords();
        if (recordList.size()>0) {
            for (int i = 0; i < recordList.size(); i++) {
                FriendMesVo vo = friendInfoDb.getFriend(recordList.get(i).getFriendAccount());
                if (vo != null) {
                    recordList.get(i).setFriendPic(vo.getPicture());
                }
            }
        }
        adapter = new FriendListAdapter(recordList, listView);
        listView.setAdapter(adapter);
        if (null != pd && pd.isShowing()) {
            pd.cancel();
            pd = null;
        }
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                Intent intent = null;
                if (recordList.get(index).getFriendType() == 1) {
                    intent = new Intent(getActivity(), ChatActivity.class);
                } else if (recordList.get(index).getFriendType() == 2) {
                    intent = new Intent(getActivity(), PublicChatActivity.class);
                } else {
                    intent = new Intent(getActivity(), GroupChatActivity.class);
                    intent.putExtra("roomId", adapter.recordList.get(index).getRoomId());
                }
                FriendMesVo friend = new FriendMesVo(recordList.get(index).getFriendAccount());
                FriendMesVo friend_localdb = friendInfoDb.getFriend(recordList.get(index).getFriendAccount());
                if (null != friend_localdb) {
                    friend = friend_localdb;
                }
                intent.putExtra(MarketApp.FRIEND, friend);
                FriendListFragment.this.startActivity(intent);
                if (adapter.recordList.get(index).getUnreadcount() > 0) {
                    adapter.recordList.get(index).setUnreadcount(0);
                    recordDb.update(adapter.recordList.get(index));
                    recordList.get(index).setUnreadcount(0);
                    adapter.recordList.get(index).setUnreadcount(0);
                    adapter.notifyDataSetChanged();
                }
                if (ViewPaperMenuActivity.handler != null) {
                    ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_THREE);
                }
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<FriendListFragment> mActivity;

        public MyHandler(FriendListFragment activity) {
            mActivity = new WeakReference<FriendListFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FriendListFragment activity = mActivity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
            case MarketApp.HANDLERMESS_ZERO:
                activity.updateUI();
                break;
            case MarketApp.NETWORK_DISCONNECTED:
                activity.netErrorLayout.setVisibility(View.VISIBLE);
                break;
            case MarketApp.NETWORK_CONNECTED:
                activity.netErrorLayout.setVisibility(View.GONE);
                break;
            }
        }
    }

    public void updateUI() {
        recordList = recordDb.getAllRecords();
        for (int i = 0; i < recordList.size(); i++) {
            FriendMesVo vo = friendInfoDb.getFriend(recordList.get(i).getFriendAccount());
            if (vo != null) {
                recordList.get(i).setFriendPic(vo.getPicture());
            }
        }
        adapter.recordList = recordList;
        adapter.notifyDataSetChanged();
    }

    public void updateFriendInfo() {
        if (null != recordList && recordList.size() > 0) {
            ChatRecordVo recordVo = null;
            for (int i = 0; i < recordList.size(); i++) {
                recordVo = recordList.get(i);
                if (recordVo != null) {
                    FriendMesVo friend = friendInfoDb.getFriend(recordVo.getFriendAccount());
                    recordVo.setFriendName(friend.getFriendName());
                    recordVo.setFriendPic(friend.getPicture());
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_view_details:
            openWirelessSettings();
            break;
        }
    }

    private void openWirelessSettings() {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            // 3.0以上打开设置界面
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }
}
