package com.lenovo.xjpsd.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.xjpsd.model.ActionBarMenuModel;

/**
 * 左边导航
 *
 * @author muqiang
 */
public class LeftCateGoryAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ActionBarMenuModel> mLists;
    private LayoutInflater mLayoutInflater;

    public LeftCateGoryAdapter(Context pContext, ArrayList<ActionBarMenuModel> leftList) {
        this.mContext = pContext;
        this.mLists = leftList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        return mLists != null ? mLists.size() : 0;
    }

    public Object getItem(int arg0) {
        return mLists.get(arg0);
    }

    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(int arg0, View view, ViewGroup arg2) {
        Holder _Holder = null;
        if (null == view) {
            _Holder = new Holder();
            view = mLayoutInflater.inflate(R.layout.psd_left_category_item, null);
            _Holder.left_category_item_name = (TextView) view.findViewById(R.id.left_category_item_name);
            view.setTag(_Holder);
        } else {
            _Holder = (Holder) view.getTag();
        }
        _Holder.left_category_item_name.setText(mLists.get(arg0).getName());
        return view;
    }

    private static class Holder {
        TextView left_category_item_name;
    }
}
