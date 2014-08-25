package com.lenovo.market.adapter;


import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.Utils;
import com.lenovo.market.view.CustomGridView;
import com.lenovo.market.vo.server.FriendMesVo;

public class CharSettingAdapter extends BaseAdapter {

    private ArrayList<FriendMesVo> list_;
    private CustomGridView gridView_;

    public CharSettingAdapter(ArrayList<FriendMesVo> list_, CustomGridView gridView_) {
        this.list_ = list_;
        this.gridView_ = gridView_;
    }

    @Override
    public int getCount() {
        return list_.size();
    }

    @Override
    public Object getItem(int position) {
        return list_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.griditem_chatsetting, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.item_text);
            holder.image = (ImageView) convertView.findViewById(R.id.item_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FriendMesVo vo = list_.get(position);
        holder.text.setText(vo.getFriendName());
        int defaultImg = R.drawable.icon;
        if (position == list_.size() - 1) {
            defaultImg = R.drawable.sl_btn_roominfo_add;
        }
        Utils.downloadImg(true, MarketApp.app, holder.image, vo.getPicture(), defaultImg, gridView_);
        return convertView;
    }

    private static class ViewHolder {
        private TextView text;
        private ImageView image;
    }
}
