package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.DateUtil;
import com.lenovo.market.util.FaceConversionUtil;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.ChatRecordVo;

public class FriendListAdapter extends BaseAdapter {

    public ArrayList<ChatRecordVo> recordList;
    private ListView listView;

    public FriendListAdapter(ArrayList<ChatRecordVo> recordList, ListView listView) {
        super();
        this.recordList = recordList;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return recordList.get(arg0);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        UserHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_friend_item, null);
            holder = new UserHolder();
            holder.userAvatar = (ImageView) convertView.findViewById(R.id.friend_user_avatar);
            holder.nick = (TextView) convertView.findViewById(R.id.friend_user_nick);
            holder.date = (TextView) convertView.findViewById(R.id.friend_user_time);
            holder.content = (TextView) convertView.findViewById(R.id.friend_user_sign);
            holder.count = (TextView) convertView.findViewById(R.id.msg_count);

            convertView.setTag(holder);
        } else {
            holder = (UserHolder) convertView.getTag();
        }

        ChatRecordVo record = recordList.get(position);
        if (TextUtils.isEmpty(record.getRoomName())) {
            holder.nick.setText(record.getFriendName());
        } else {
            holder.nick.setText(record.getRoomName());
        }
        holder.date.setText(DateUtil.getDateStrFromLong(record.getCreateTime()));

        String content = record.getContent();
        if (!TextUtils.isEmpty(record.getRoomName())) {
            content = record.getFriendName() + "：" + content;
        }
        if (!TextUtils.isEmpty(content) && content.contains("[") && content.contains("]")) {
            SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(MarketApp.app, content, 17);
            holder.content.setText(spannableString);
        } else {
            holder.content.setText(content);
        }

        if (record.getUnreadcount() > 0) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(record.getUnreadcount() + "");
        } else {
            holder.count.setVisibility(View.INVISIBLE);
        }
        String url = record.getFriendPic();
        int defaultImg = R.drawable.ic_single_chat;
        switch (record.getFriendType()){
            case 2:
                defaultImg = R.drawable.ic_publicchat;
                break;
            case 3:
                defaultImg = R.drawable.ic_groupchat;
                url = MarketApp.GROUP_IMG_REMOTE_PATH + record.getRoomId();
                break;
        }
        Utils.downloadImg(true, MarketApp.app, holder.userAvatar, url, defaultImg, listView);
        return convertView;
    }

    private static class UserHolder {
        private ImageView userAvatar;
        private TextView content;
        private TextView nick;
        private TextView count;
        private TextView date;
    }
}
