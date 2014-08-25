package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lenovo.market.R;
import com.lenovo.market.activity.home.PictureViewActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.MFriendZoneImageVo;

public class GridAdapter extends BaseAdapter {

    private ArrayList<MFriendZoneImageVo> images;
    private GridView gView;
    private Context context;

    public GridAdapter(Context context, ArrayList<MFriendZoneImageVo> images, GridView gView) {
        this.images = images;
        this.gView = gView;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_friend_img, null);
        ImageView listitem_iv_img = (ImageView) view.findViewById(R.id.listitem_iv_img);
        final String filePath = images.get(position).getUrl();

        // 异步下载图片
        Utils.downloadImg(false, context, listitem_iv_img, filePath, R.drawable.moren, gView);
        listitem_iv_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketApp.app, PictureViewActivity.class);
                intent.putExtra("filePath", filePath);
                context.startActivity(intent);
            }
        });
        return view;
    }
}
