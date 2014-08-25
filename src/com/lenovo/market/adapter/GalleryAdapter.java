package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.Utils;
import com.lenovo.market.view.CustomGallery;
import com.lenovo.market.view.CustomImageView;

@SuppressWarnings("deprecation")
public class GalleryAdapter extends BaseAdapter {

    private ArrayList<String> mItems;
    private CustomGallery gallery;

    public void setData(ArrayList<String> data) {
        this.mItems = data;
        notifyDataSetChanged();
    }

    public GalleryAdapter(CustomGallery gallery) {
        this.gallery = gallery;
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomImageView view = new CustomImageView(MarketApp.app);
        view.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        String item = mItems.get(position);
        Utils.downloadImg(false, MarketApp.app, view, item, R.drawable.moren, gallery);
        return view;
    }
}
