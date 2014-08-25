package com.lenovo.market.activity.contacts;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.packet.VCard;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.adapter.NewFriendsAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.NewFriendInfoDBHelper;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.PullXml;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.xmpp.VCardVo;
import com.lenovo.platform.xmpp.XmppFriendList;

/**
 * 新朋友
 *
 * @author muqiang
 */
public class NewFriendsActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    public static ArrayList<FriendMesVo> newFriendAll;
    public static NewFriendsHandler handler;
    
    private ListView listview;
    private NewFriendInfoDBHelper dbHelper;
    private NewFriendsAdapter newFriendsAdapter;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_contacts_newfriends);
        setTitleBarText(R.string.title_newfriends);
        setTitleBarRightBtnText("清空列表");
        setTitleBarLeftBtnText();
        dbHelper = new NewFriendInfoDBHelper();
        handler = new NewFriendsHandler(this);
        // 先查询出所有的新朋友
        newFriendAll = new ArrayList<FriendMesVo>();
        ArrayList<FriendMesVo> newFriendAllFrom = dbHelper.getNewFriendAll("from");
        ArrayList<FriendMesVo> newFriendAllTo = dbHelper.getNewFriendAll("to");
        ArrayList<FriendMesVo> newFriendAllBoth = dbHelper.getNewFriendAll("both");
        if (newFriendAllFrom.size() > 0) {
            newFriendAll.addAll(newFriendAllFrom);
        }
        if (newFriendAllTo.size() > 0) {
            newFriendAll.addAll(newFriendAllTo);
        }
        if (newFriendAllBoth.size() > 0) {
            newFriendAll.addAll(newFriendAllBoth);
        }
        if (newFriendAll.size() <= 0) {
            pd = Utils.createProgressDialog(context, "正在加载新朋友列表……");
            pd.show();
        }
        handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
    }

    @Override
    protected void findViewById() {
        listview = (ListView) findViewById(R.id.listview);
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            new Thread() {
                public void run() {
                    XmppFriendList instance = XmppFriendList.getInstance();
                    ArrayList<FriendMesVo> dataList_ = instance.getFriendType();
                    XMPPConnection connection = instance.getConnection();

                    try {
                        if (dataList_ != null) {
                            int num = 0;
                            for (int i = 0; i < dataList_.size(); i++) {
                                VCard userVCard = XmppFriendList.getUserVCard(connection, dataList_.get(i).getUser());
                                ByteArrayInputStream bis = new ByteArrayInputStream(userVCard.toString().getBytes());
                                VCardVo vCard = PullXml.getVCard(bis);
                                dataList_.get(i).setFriendName(vCard.getUserName());
                                dataList_.get(i).setPicture(vCard.getPicture());
                                dataList_.get(i).setArea(vCard.getArea());
                                dataList_.get(i).setState("0");
                                long newFriend = dbHelper.insertNewFriend(context, dataList_.get(i));
                                if (newFriend > 0) {
                                    newFriendAll.add(num, dataList_.get(i));
                                    num++;
                                }
                            }
                            handler.sendEmptyMessage(MarketApp.HANDLERMESS_ONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }.start();
        }
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        listview.setOnItemClickListener(this);
        btn_right_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                if (MarketApp.needUpdateContacts_) {
                    // 若添加好友了，返回时要发送消息刷新好友列表
                    ContactsFragment.handler.obtainMessage(MarketApp.HANDLERMESS_ONE).sendToTarget();
                }
                finish();
                break;
            case R.id.btn_right:
                dbHelper.updateState();
                if (newFriendAll.size() > 0) {
                    newFriendAll.clear();
                }
                ArrayList<FriendMesVo> newFriendAllFrom = dbHelper.getNewFriendAll("from");
                ArrayList<FriendMesVo> newFriendAllTo = dbHelper.getNewFriendAll("to");
                if (newFriendAllFrom.size() > 0) {
                    newFriendAll.addAll(newFriendAllFrom);
                }
                if (newFriendAllTo.size() > 0) {
                    newFriendAll.addAll(newFriendAllTo);
                }
                handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FriendMesVo friend = (FriendMesVo) parent.getAdapter().getItem(position);
        Intent intent = new Intent(view.getContext(), FriendDetailsActivity.class);
        intent.putExtra(MarketApp.FRIEND, friend);
        if (friend.getSubscription().equals("both")) {
            if (MarketApp.needUpdateContacts_) {
                // 若添加好友了，返回时要发送消息刷新好友列表
                ContactsFragment.handler.obtainMessage(MarketApp.HANDLERMESS_ONE).sendToTarget();
            }
            intent.putExtra(FriendDetailsActivity.DETAILED, 2);
        } else {
            intent.putExtra(FriendDetailsActivity.DETAILED, 0);
        }
        startActivity(intent);
    }

    public static class NewFriendsHandler extends Handler {
        WeakReference<NewFriendsActivity> mActivity;

        public NewFriendsHandler(NewFriendsActivity activity) {
            mActivity = new WeakReference<NewFriendsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NewFriendsActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
                case MarketApp.HANDLERMESS_ZERO:
                    activity.newFriendsAdapter = new NewFriendsAdapter(activity.context, newFriendAll, activity.listview);
                    activity.listview.setAdapter(activity.newFriendsAdapter);
                    if (null != activity.pd && activity.pd.isShowing()) {
                        activity.pd.dismiss();
                        activity.pd = null;
                    }
                    break;
                case MarketApp.HANDLERMESS_ONE:
                    activity.newFriendsAdapter.notifyDataSetChanged();
                    break;
            }
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
