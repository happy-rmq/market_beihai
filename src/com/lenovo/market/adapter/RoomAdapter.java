package com.lenovo.market.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.FtpDownLoadTask;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.RoomVo;

import java.util.ArrayList;

/**
 * Created by zhouyang on 2014/4/28 0028.
 */
public class RoomAdapter extends BaseAdapter {

    private final ListView listView;
    private LayoutInflater mInflater;
    private ArrayList<RoomVo> rooms;
    private Context context;

    public RoomAdapter(Context context, ArrayList<RoomVo> rooms, ListView listView) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.rooms = rooms;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.vs_listitem_roomadapter, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.img = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RoomVo vo = rooms.get(position);
        if (vo != null) {
            if (!TextUtils.isEmpty(vo.getName())) {
                holder.name.setText(vo.getName());
            }
            String url = MarketApp.GROUP_IMG_REMOTE_PATH + vo.getRoomId();
            Utils.downloadImg(true, context, holder.img, url, R.drawable.ic_groupchat, true, listView);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        ImageView img;
    }
}
