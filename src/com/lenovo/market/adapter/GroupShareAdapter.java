package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.vo.local.ChatRecordVo;

public class GroupShareAdapter extends BaseAdapter {

    public ArrayList<ChatRecordVo> recordList;

    public GroupShareAdapter(ArrayList<ChatRecordVo> recordList) {
        super();
        this.recordList = recordList;
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
        Holder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_groupshare_item, null);
            holder = new Holder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_groupshare_title);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        ChatRecordVo record = recordList.get(position);
        holder.title.setText(record.getRoomName());

        return convertView;
    }

    private static class Holder {
        private TextView title;
    }
}
