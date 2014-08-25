package com.lenovo.market.activity.circle.friends;

import org.jivesoftware.smack.XMPPException;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.dbhelper.MessageDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.SendMsgUtil;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.XmppFriendList;

/**
 * 详细资料(公众账号)
 * 
 * @author zhouyang
 */
public class PublicAccountDetailsActivity extends BaseActivity implements OnClickListener {

    private FriendMesVo friend_;
    private TextView tv_name_;
    private ImageView logo_;
    private TextView tv_account_;
    private TextView tv_summary_;
    private Button btn_focus_;
    private RelativeLayout view_msg_;
    private boolean isFocused_;
    private boolean initialState;
    private String account_;
    private FriendInfoDBHelper fHelper;
    private MessageDBHelper mHelper;
    private ChatRecordDBHelper cHelper;
    private int IsVisible;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_active_exhibitordetails);
        setTitleBarText(R.string.title_details);
        setTitleBarLeftBtnText();
        fHelper = new FriendInfoDBHelper();
        mHelper = new MessageDBHelper();
        cHelper = new ChatRecordDBHelper();
        friend_ = (FriendMesVo) getIntent().getSerializableExtra(MarketApp.FRIEND);
    }

    @Override
    protected void findViewById() {

        PublicChatActivity.isClose = false;
        tv_name_ = (TextView) findViewById(R.id.name);
        tv_account_ = (TextView) findViewById(R.id.account);
        tv_summary_ = (TextView) findViewById(R.id.summary);
        logo_ = (ImageView) findViewById(R.id.logo);
        view_msg_ = (RelativeLayout) findViewById(R.id.view_msg);
        btn_focus_ = (Button) findViewById(R.id.btn_with_focus_on);
        IsVisible = getIntent().getIntExtra("IsVisible", 0);
        if (IsVisible == 1) {
            btn_focus_.setVisibility(View.GONE);
        }

        if (null != friend_) {
            String name = friend_.getFriendName();
            account_ = friend_.getFriendAccount();
            String summary = friend_.getSign();
            String pic = friend_.getPicture();

            if (!TextUtils.isEmpty(name)) {
                tv_name_.setText(name);
            }
            if (!TextUtils.isEmpty(account_)) {
                tv_account_.setText("账号：" + account_);
            }
            if (!TextUtils.isEmpty(summary)) {
                tv_summary_.setText(summary);
            }

            FriendMesVo friend = fHelper.getFriend(account_);
            UserVo user = AdminUtils.getUserInfo(this);
            if (null != friend && null != user) {
                boolean isDefaultPublicAccount = false;
                String dId = user.getDefaultServId();
                String sId = user.getServId();
                if (!TextUtils.isEmpty(sId)) {
                    if (sId.equals(friend.getFriendId())) {
                        isDefaultPublicAccount = true;
                    }
                }
                if (!TextUtils.isEmpty(dId)) {
                    if (dId.equals(friend.getFriendId())) {
                        isDefaultPublicAccount = true;
                    }
                }
                view_msg_.setVisibility(View.VISIBLE);
                isFocused_ = true;
                if (isDefaultPublicAccount) {
                    btn_focus_.setVisibility(View.GONE);
                } else {
                    // 设置按钮为取消关注
                    btn_focus_.setBackgroundResource(R.drawable.red_bt);
                    btn_focus_.setText(R.string.cancel_focus);
                }
            } else {
                view_msg_.setVisibility(View.GONE);
            }
            initialState = isFocused_;// 好友关系初始状态
            Utils.downloadImg(true, context, logo_, pic, R.drawable.icon, logo_);
        }
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_focus_.setOnClickListener(this);
        view_msg_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:
            if (initialState != isFocused_) {
                Intent data = new Intent();
                data.putExtra(MarketApp.FRIEND, friend_.getFriendAccount());
                setResult(200, data);
            }
            finish();
            break;
        case R.id.btn_with_focus_on:
            if (isFocused_) {
                deleteFriend();
                PublicChatActivity.isClose = true;
            } else {
                MarketApp.sendAddFriend = true;
                addFriend();
            }
            break;
        case R.id.view_msg:
            ChatRecordVo recordVo = cHelper.getRecord(friend_.getFriendAccount());
            cHelper.update(recordVo);
            if (null != ViewPaperMenuActivity.handler) {
                ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_THREE);
            }
            if (null != FriendListFragment.handler) {
                FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
            }
            if (null != PublicChatActivity.handler) {
                PublicChatActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
            }
            if (IsVisible == 2) {
                Intent msgIntent = new Intent(this, PublicChatActivity.class);
                msgIntent.putExtra(MarketApp.FRIEND, friend_);
                startActivity(msgIntent);
            }
            finish();
            break;
        }
    }

    private void addFriend() {
        boolean flag = false;
        if (!TextUtils.isEmpty(account_)) {
            try {
                // 加公众账号为好友
                fHelper.saveFriend(friend_);
                String jid = Utils.getJidFromUsername(account_);
                flag = XmppFriendList.getInstance().addFriendForGroup(jid, MarketApp.EXHIBITOR_GROUPNAME, "我想添加你为好友");
                MsgXmlVo mxVo = new MsgXmlVo();
                mxVo.setContent("");
                mxVo.setCreateTime(System.currentTimeMillis() + "");
                String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_TEXT, mxVo.getCreateTime());
                SendMsgUtil.sendMessage(jid, sendxml);
                if (flag) {
                    // 设置按钮为取消关注
                    btn_focus_.setBackgroundResource(R.drawable.red_bt);
                    btn_focus_.setText(R.string.cancel_focus);
                    view_msg_.setVisibility(View.VISIBLE);
                    isFocused_ = true;
                } else {
                    log.d("addFriendForGroup返回false，关注失败!");
                }
            } catch (XMPPException e) {
                e.printStackTrace();
                Utils.showToast(context, "关注失败!");
            }
        }
    }

    private void deleteFriend() {
        boolean flag = false;
        if (!TextUtils.isEmpty(account_)) {
            flag = XmppFriendList.getInstance().deleteFriendByUserName(Utils.getJidFromUsername(account_));
            fHelper.delete(friend_.getFriendAccount());
            mHelper.delete(friend_.getFriendAccount());
            cHelper.deleteRecordByName(friend_.getFriendAccount());
            if (FriendListFragment.handler != null) {
                FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
            }
            if (flag) {
                if (null != ViewPaperMenuActivity.handler) {
                    ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_THREE);
                }
                isFocused_ = false;
                Intent data = new Intent();
                data.putExtra(MarketApp.FRIEND, friend_.getFriendAccount());
                setResult(200, data);
                finish();
            } else {
                log.d("deleteFriendByUserName返回false，取消关注失败!");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (initialState != isFocused_) {
            Intent data = new Intent();
            data.putExtra(MarketApp.FRIEND, friend_.getFriendAccount());
            setResult(200, data);
        }
        finish();
    }
}
