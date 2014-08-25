package com.lenovo.market.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;

public class ShareAdapter extends BaseAdapter {

    private String[] item = { "微门户朋友圈", "发送给朋友", "微门户群组" };
    private int[] imgs = { R.drawable.as_female_male, R.drawable.as_share, R.drawable.as_moment };

    public ShareAdapter() {
    }

    @Override
    public int getCount() {
        return item.length;
    }

    @Override
    public Object getItem(int position) {
        return item[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(MarketApp.app, R.layout.griditem_share, null);
        TextView griditem_share_content = (TextView) view.findViewById(R.id.griditem_share_content);
        ImageView griditem_share_img = (ImageView) view.findViewById(R.id.griditem_share_img);

        griditem_share_content.setText(item[position]);
        griditem_share_img.setImageResource(imgs[position]);
        return view;
    }
}
