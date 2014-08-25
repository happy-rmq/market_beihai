package com.lenovo.market.activity.contacts;

import org.jivesoftware.smack.XMPPException;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.ContactsUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.platform.xmpp.XmppFriendList;

/**
 * 好友详细信息
 * 
 * @author zhouyang
 * 
 */
public class FriendDetailsActivity extends BaseActivity implements OnClickListener {

    public static final String DETAILED = "detailed";

    private FriendMesVo friend_;
    private TextView tv_nick_;
    private TextView tv_account_;
    private TextView tv_sign_;
    private ImageView iv_avatar_;
    private Button sendMsg_;
    private int detailed = 1;
    private FriendInfoDBHelper friendDB;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_contacts_frienddetails);
        setTitleBarText(R.string.title_details);
        setTitleBarLeftBtnText();
        friendDB = new FriendInfoDBHelper();
        friend_ = (FriendMesVo) getIntent().getSerializableExtra(MarketApp.FRIEND);
        detailed = getIntent().getExtras().getInt(DETAILED);
    }

    @Override
    protected void findViewById() {
        tv_nick_ = (TextView) findViewById(R.id.tv_nick);
        tv_account_ = (TextView) findViewById(R.id.tv_account);
        tv_sign_ = (TextView) findViewById(R.id.tv_sign);
        iv_avatar_ = (ImageView) findViewById(R.id.iv_avatar);
        sendMsg_ = (Button) findViewById(R.id.friend_detail_send_message);
        switch (detailed) {
        case 1:
            sendMsg_.setVisibility(View.GONE);
            break;
        case 2:
            setTitleBarRightImg(R.drawable.btn_titlebar_more);
            break;
        default:
            FriendMesVo friend2 = friendDB.getFriend(friend_.getFriendAccount());
            if (null != friend2) {
                sendMsg_.setText("发消息");
                setTitleBarRightImg(R.drawable.btn_titlebar_more);
                detailed = 2;
            } else {
                sendMsg_.setText("加好友");
            }
            break;
        }
        if (null != friend_) {
            String nickname = friend_.getFriendName();
            String account = friend_.getFriendAccount();
            String sign = friend_.getSign();
            String pic = friend_.getPicture();
            if (!TextUtils.isEmpty(nickname)) {
                tv_nick_.setText(nickname);
            }
            if (!TextUtils.isEmpty(account)) {
                tv_account_.setText("账号：" + account);
            }
            if (!TextUtils.isEmpty(sign)) {
                tv_sign_.setText(sign);
            }
            Utils.downloadImg(true, context, iv_avatar_, pic, R.drawable.icon, iv_avatar_);
        }
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        if (detailed > 1) {
            titlebar_right_img.setOnClickListener(this);
        }
        sendMsg_.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.btn_left:
            finish();
            break;
        case R.id.iv_right:
            Intent intent = new Intent(this, FriendDetailMenuActivity.class);
            intent.putExtra(MarketApp.FRIEND, friend_);
            startActivityForResult(intent, 0);
            break;
        case R.id.friend_detail_send_message:
            if (detailed == 2) {
                Intent msgIntent = new Intent(this, ChatActivity.class);
                msgIntent.putExtra(MarketApp.FRIEND, friend_);
                startActivityForResult(msgIntent, 1);
            } else {
                MarketApp.sendAddFriend = true;
                String account = friend_.getFriendAccount();
                boolean flag = false;
                if (!TextUtils.isEmpty(account)) {
                    if (account.equals(AdminUtils.getUserInfo(getApplicationContext()).getAccount())) {
                        Utils.showToast(this, "不能将自己加为好友");
                        return;
                    }
                    try {
                        flag = XmppFriendList.getInstance().addFriendForGroup(Utils.getJidFromUsername(account), MarketApp.FRIEND_DEFAULT_GROUPNAME, "我想添加你为好友");
                        Utils.showToast(this, flag ? "已经发送加好友申请!" : "发送好友申请失败");
                        if (flag) {
                            MarketApp.needUpdateContacts_ = true;
                        }
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }
            }
            finish();
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case FriendDetailMenuActivity.DELETE:
            deleteFriend();
            break;
        case RESULT_FIRST_USER:
            finish();
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteFriend() {
        if (null != friend_) {
            ContactsUtils.deleteFriend(friend_, false);
            if (null != NewFriendsActivity.handler && null != NewFriendsActivity.newFriendAll) {
                for (int i = 0; i < NewFriendsActivity.newFriendAll.size(); i++) {
                    if (NewFriendsActivity.newFriendAll.get(i).getFriendAccount().equals(friend_.getFriendAccount())) {
                        NewFriendsActivity.newFriendAll.remove(i);
                        break;
                    }
                }
                NewFriendsActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ONE);
            }
            finish();
        }
    }
}
