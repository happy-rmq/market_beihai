package com.lenovo.xjpsd.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.xjpsd.activity.WebViewActivity;
import com.lenovo.xjpsd.adapter.LeftCateGoryAdapter;

public class LeftCategoryFragment extends Fragment {

    private View mView;
    private Context mContext;
    private ListView listview_right_category;
    private LeftCateGoryAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.psd_left_category, container, false);
            initView();
            initValidata();
            bindData();
            initListener();
        }
        return mView;
    }

    private void initView() {
        listview_right_category = (ListView) mView.findViewById(R.id.listview_left_category);
    }

    private void initValidata() {
        mContext = mView.getContext();
        mAdapter = new LeftCateGoryAdapter(mContext, MarketApp.leftList);
    }

    private void bindData() {
        listview_right_category.setAdapter(mAdapter);
    }

    private void initListener() {
        listview_right_category.setOnItemClickListener(new MyOnItemClickListener());
    }

    class MyOnItemClickListener implements OnItemClickListener {

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra("url", MarketApp.leftList.get(arg2).getUrl());
            startActivity(intent);
        }
    }
}
