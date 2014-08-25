package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.vo.server.MenuVo;

public class CustomMenuAdapter extends BaseAdapter {

    private ArrayList<MenuVo> menuVOs;
    private Context context;

    public CustomMenuAdapter(Context context, ArrayList<MenuVo> menuVOs) {
        this.menuVOs = menuVOs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return menuVOs.size();
    }

    @Override
    public Object getItem(int position) {
        return menuVOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String name = menuVOs.get(position).getName();
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_custom_menu, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_item);
        textView.setText(name);
        return view;
    }
}
