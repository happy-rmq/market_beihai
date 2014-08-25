//package com.lenovo.market.adapter;
//
//import java.util.ArrayList;
//
//import android.text.SpannableString;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.lenovo.market.R;
//import com.lenovo.market.common.MarketApp;
//import com.lenovo.market.util.DateUtil;
//import com.lenovo.market.util.FaceConversionUtil;
//import com.lenovo.market.vo.local.GroupRecordVo;
//
//public class GroupChatRecordAdapter extends BaseAdapter {
//
//    public ArrayList<GroupRecordVo> recordList;
//
//    public GroupChatRecordAdapter(ArrayList<GroupRecordVo> recordList) {
//        super();
//        this.recordList = recordList;
//    }
//
//    @Override
//    public int getCount() {
//        return recordList.size();
//    }
//
//    @Override
//    public Object getItem(int arg0) {
//        return recordList.get(arg0);
//    }
//
//    @Override
//    public long getItemId(int index) {
//        return index;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup arg2) {
//        Holder holder = null;
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_groupchat_record_item, null);
//            holder = new Holder();
//            holder.nick = (TextView) convertView.findViewById(R.id.friend_user_nick);
//            holder.date = (TextView) convertView.findViewById(R.id.friend_user_time);
//            holder.content = (TextView) convertView.findViewById(R.id.friend_user_sign);
//            holder.count = (TextView) convertView.findViewById(R.id.msg_count);
//
//            convertView.setTag(holder);
//        } else {
//            holder = (Holder) convertView.getTag();
//        }
//
//        GroupRecordVo record = recordList.get(position);
//        holder.nick.setText(record.getRoomname());
//        holder.date.setText(DateUtil.getDateStrFromLong(record.getTime()));
//
//        String content = record.getContent();
//        if (!TextUtils.isEmpty(content) && !record.getSendUser().equals(record.getRoomId())) {
//            if (!TextUtils.isEmpty(record.getSendUserName())) {
//                content = record.getSendUserName() + "：" + content;
//            }
//        }
//        if (!TextUtils.isEmpty(content) && content.contains("[") && content.contains("]")) {
//            SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(MarketApp.app, content, 17);
//            holder.content.setText(spannableString);
//        } else {
//            holder.content.setText(content);
//        }
//
//        if (record.getUnReadCount() > 0) {
//            holder.count.setVisibility(View.VISIBLE);
//            holder.count.setText(record.getUnReadCount() + "");
//        } else {
//            holder.count.setVisibility(View.INVISIBLE);
//        }
//        return convertView;
//    }
//
//    private static class Holder {
//        private TextView content;
//        private TextView nick;
//        private TextView count;
//        private TextView date;
//    }
//}
