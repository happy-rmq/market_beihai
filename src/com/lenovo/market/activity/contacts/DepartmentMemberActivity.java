package com.lenovo.market.activity.contacts;

import java.util.ArrayList;

import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import org.jivesoftware.smack.XMPPException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.DepartmentMemberVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.platform.xmpp.XmppFriendList;

/**
 * 部门成员详细信息
 * 
 * @author zhouyang
 */
public class DepartmentMemberActivity extends BaseActivity implements OnClickListener {

    private DepartmentMemberVo member;
    private TextView tv_nick_;
    private TextView tv_account_;
    private TextView tv_email_;
    private ImageView iv_avatar_;
    private TextView tv_phone;
    private Button btn_phone;
    private FriendInfoDBHelper friendDB;
    private Button friend_detail_send_message;
    private FriendMesVo friend;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_department_member_details);
        friendDB = new FriendInfoDBHelper();
        member = (DepartmentMemberVo) getIntent().getSerializableExtra("member");
        // friend = friendDB.getFriend(member.getAccount());
        friend = friendDB.getFriendById(member.getId());
        String name = member.getName();
        if (name != null) {
            setTitleBarText(name);
        } else {
            setTitleBarText(R.string.title_details);
        }
        setTitleBarLeftBtnText();
    }

    @Override
    protected void findViewById() {
        tv_nick_ = (TextView) findViewById(R.id.tv_nick);
        tv_account_ = (TextView) findViewById(R.id.tv_account);
        tv_email_ = (TextView) findViewById(R.id.tv_email);
        iv_avatar_ = (ImageView) findViewById(R.id.iv_avatar);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        btn_phone = (Button) findViewById(R.id.btn_phone);
        friend_detail_send_message = (Button) findViewById(R.id.friend_detail_send_message);
        if (null != member) {
            String nickname = member.getName();
            String account = member.getAccount();
            String phone = member.getPhonenum();
            String pic = member.getPic();
            String email = member.getEmail();
            if (!TextUtils.isEmpty(nickname)) {
                tv_nick_.setText(nickname);
            }
            if (!TextUtils.isEmpty(account)) {
                tv_account_.setText("账号：" + account);
            }
            if (!TextUtils.isEmpty(email)) {
                tv_email_.setText(email);
            }
            if (!TextUtils.isEmpty(phone)) {
                tv_phone.setText(phone);
            } else {
                btn_phone.setVisibility(View.GONE);
            }
            Utils.downloadImg(true, context, iv_avatar_, pic, R.drawable.icon, iv_avatar_);
            if (null == friend) {
                friend_detail_send_message.setText("加好友");
            }
            // String friendAccount = member.getAccount();
            String friendId = member.getId();
            // String current_user = AdminUtils.getUserInfo(context).getAccount();
            String current_user = AdminUtils.getUserInfo(context).getUid();
            if (friendId != null && friendId.equals(current_user)) {
                friend_detail_send_message.setVisibility(View.GONE);
                btn_phone.setVisibility(View.GONE);
            }
            String isSync = member.getIsSync();
            if (isSync == null || !isSync.equals("1")) {
                friend_detail_send_message.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_phone.setOnClickListener(this);
        friend_detail_send_message.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.btn_left:
            finish();
            break;
        case R.id.btn_phone:
            String phone = tv_phone.getText().toString();
            if (!TextUtils.isEmpty(phone)) {
                String[] strArr = null;
                phone = phone.trim();
                if (phone.contains(" ")) {
                    strArr = phone.split(" ");
                } else if (phone.contains(",")) {
                    strArr = phone.split(",");
                }
                if (strArr == null) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    startActivity(intent);
                } else {
                    ArrayList<String> list = new ArrayList<String>();
                    for (String str : strArr) {
                        if (str != null && !str.trim().equals("")) {
                            list.add(str.trim());
                        }
                    }
                    if (list.size() < 2) {
                        return;
                    }
                    final String[] phones = new String[list.size()];
                    list.toArray(phones);

                    AlertDialog dialog = new AlertDialog.Builder(this).setItems(phones, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String phone = null;
                            switch (which) {
                            case 0:
                                phone = phones[0];
                                break;
                            case 1:
                                phone = phones[1];
                                break;
                            }
                            if (phone == null) {
                                return;
                            }
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                            startActivity(intent);
                        }
                    }).create();
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }
            }
            break;
        case R.id.friend_detail_send_message:
            if (null != friend) {
                Intent msgIntent = new Intent(this, ChatActivity.class);
                msgIntent.putExtra(MarketApp.FRIEND, friend);
                startActivityForResult(msgIntent, 1);
            } else {
                MarketApp.sendAddFriend = true;
                try {
                    XmppFriendList.getInstance().addFriendForGroup(Utils.getJidFromUsername(member.getAccount()), MarketApp.FRIEND_DEFAULT_GROUPNAME, "我想添加你为好友");
                    Utils.showToast(context, "已经发送加好友申请!");
                } catch (XMPPException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
            break;
        }
    }
}
