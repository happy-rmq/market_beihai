package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.vo.local.ChatEmoticonsVo;

/**
 * 
 ****************************************** 
 * @文件名称 : FaceAdapter.java
 * @文件描述 : 表情填充器
 ****************************************** 
 */
public class FaceAdapter extends BaseAdapter {

    private ArrayList<ChatEmoticonsVo> data;
    private LayoutInflater inflater;
    private int size = 0;

    public FaceAdapter(ArrayList<ChatEmoticonsVo> list) {
        this.inflater = LayoutInflater.from(MarketApp.app);
        this.data = list;
        this.size = list.size();
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatEmoticonsVo emoji = data.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_face, null);
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (emoji.getId() == R.drawable.sl_btn_remove_expression) {
            convertView.setBackgroundResource(0);
            viewHolder.iv_face.setImageResource(emoji.getId());
        } else if (TextUtils.isEmpty(emoji.getCharacter())) {
            convertView.setBackgroundResource(0);
            viewHolder.iv_face.setImageDrawable(null);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setImageResource(emoji.getId());
        }
        return convertView;
    }

    private static class ViewHolder {
        private ImageView iv_face;
    }
}