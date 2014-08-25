package com.lenovo.market.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.FriendMesVo;

@SuppressWarnings("unchecked")
public class FriendVoAdapter extends BaseAdapter implements Filterable {

    private final int defaultImg;
    private Context context;
    private ArrayList<FriendMesVo> friends;
    private ArrayList<FriendMesVo> initFriends;
    private Filter filter;
    private ViewHolder holder;
    private ListView actualListView;
    private Locale locale;

    public FriendVoAdapter(Context context,int defaultImg, ArrayList<FriendMesVo> friends, ListView actualListView) {
        this.context = context;
        this.defaultImg = defaultImg;
        this.friends = friends;
        this.initFriends = friends;
        this.actualListView = actualListView;
        this.locale = Locale.getDefault();
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_contacts, null);
            holder = new ViewHolder();
            holder.tv_catalog = (TextView) view.findViewById(R.id.tv_catalog);
            holder.tv_nick = (TextView) view.findViewById(R.id.tv_nick);
            holder.tv_sign = (TextView) view.findViewById(R.id.tv_sign);
            holder.img = (ImageView) view.findViewById(R.id.iv_pic);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.img.setImageResource(R.drawable.icon);
        String catalog = friends.get(position).getInitial().toUpperCase(locale);
        if (position == 0) {
            holder.tv_catalog.setVisibility(View.VISIBLE);
            holder.tv_catalog.setText(catalog);
        } else {
            String lastCatalog = friends.get(position - 1).getInitial().toUpperCase(locale);
            if (catalog.equals(lastCatalog)) {
                holder.tv_catalog.setVisibility(View.GONE);
            } else {
                holder.tv_catalog.setVisibility(View.VISIBLE);
                holder.tv_catalog.setText(catalog);
            }
        }

        holder.tv_nick.setText(friends.get(position).getFriendName());
        holder.tv_sign.setText(friends.get(position).getSign());
        String logo = friends.get(position).getPicture();
        // 异步下载图片
        Utils.downloadImg(true, context, holder.img, logo, defaultImg, actualListView);
        return view;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<FriendMesVo> Filtereddatas = new ArrayList<FriendMesVo>();

                    constraint = constraint.toString().toLowerCase(locale);
                    for (int i = 0; i < initFriends.size(); i++) {
                        String dataNames = initFriends.get(i).getPy();
                        if (dataNames.toLowerCase(locale).contains(constraint.toString())) {
                            Filtereddatas.add(initFriends.get(i));
                        }
                    }
                    results.count = Filtereddatas.size();
                    results.values = Filtereddatas;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    friends = (ArrayList<FriendMesVo>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
        return filter;
    }

    private static class ViewHolder {
        private TextView tv_catalog;
        private TextView tv_nick;
        private TextView tv_sign;
        private ImageView img;
    }
}