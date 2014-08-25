package com.lenovo.market.adapter;

import java.util.ArrayList;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.CommonUtil;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.NewFriendInfoDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.ContactsUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.platform.xmpp.XmppAddContact;

public class NewFriendsAdapter extends BaseAdapter {

    private ArrayList<FriendMesVo> dataList_;
    private ListView listview;
    private NewFriendInfoDBHelper dbHelper;
    private Context context;

    public NewFriendsAdapter(Context context, ArrayList<FriendMesVo> dataList_, ListView listview) {
        this.context = context;
        this.dataList_ = dataList_;
        this.listview = listview;
        this.dbHelper = new NewFriendInfoDBHelper();
    }

    @Override
    public int getCount() {
        return dataList_.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(MarketApp.app, R.layout.listitem_contacts_newfriends, null);
            holder = new ViewHolder();
            holder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            holder.tv_sign = (TextView) convertView.findViewById(R.id.tv_sign);
            holder.tv_added = (TextView) convertView.findViewById(R.id.tv_added);
            holder.btn = (Button) convertView.findViewById(R.id.btn_addfriend);
            holder.image = (ImageView) convertView.findViewById(R.id.iv_avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.tv_added.setVisibility(View.GONE);
            holder.btn.setVisibility(View.GONE);
        }

        if (dataList_.size() > 0) {
            final FriendMesVo vo = dataList_.get(position);
            holder.tv_nickname.setText(vo.getFriendName());
            holder.tv_sign.setText("对方向你打了声招呼");
            // 设置头像
            Utils.downloadImg(true, context, holder.image, vo.getPicture(), R.drawable.icon, listview);
            holder.tv_added.setText("");
            String friendType = vo.getSubscription();
            if (!TextUtils.isEmpty(friendType)) {
                if ("both".equals(friendType)) {
                    holder.tv_added.setVisibility(View.VISIBLE);
                    holder.tv_added.setText("已添加");
                } else if ("to".equals(friendType) || "none".equals(friendType)) {
                    holder.tv_added.setVisibility(View.VISIBLE);
                    holder.tv_added.setText("等待验证");
                } else if ("from".equals(friendType)) {
                    holder.btn.setVisibility(View.VISIBLE);
                    holder.btn.setText("通过验证");
                    holder.btn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                if (MarketApp.network_available && NetUtils.hasNetwork()) {
                                    UserVo userVo = AdminUtils.getUserInfo(context);
                                    if (userVo == null || userVo.getAccount() == null)
                                        return;

                                    // 向openfire发送同意请求
                                    String jid = Utils.getJidFromUsername(vo.getFriendAccount());
                                    XmppAddContact.getInstance().AcceptInvited(jid, MarketApp.FRIEND_DEFAULT_GROUPNAME, Presence.Mode.chat);

                                    // 向webservice服务器发送添加好友请求
                                    ContactsUtils.addFriend(userVo.getAccount(), vo.getFriendAccount());

                                    MarketApp.needUpdateContacts_ = true;

                                    v.setVisibility(View.GONE);
                                    holder.tv_added.setVisibility(View.VISIBLE);
                                    dataList_.get(position).setSubscription("both");
                                    holder.tv_added.setText("已添加");
                                    dbHelper.update(dataList_.get(position));
                                } else {
                                    Utils.showToast(context, "连接已经断开,正在重连,请稍后再试...");
                                    CommonUtil.ConnectionXmpp(context);
                                }
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            String logo = vo.getPicture();
            Utils.downloadImg(true, context, holder.image, logo, R.drawable.icon, listview);
        }
        return convertView;
    }

    private static class ViewHolder {
        private ImageView image;
        private TextView tv_nickname;
        private TextView tv_sign;
        private TextView tv_added;
        private Button btn;
    }
}
