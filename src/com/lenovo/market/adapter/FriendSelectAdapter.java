package com.lenovo.market.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
public class FriendSelectAdapter extends BaseAdapter implements Filterable {

    public ArrayList<Boolean> isSelected;
    public ArrayList<Boolean> selectedList;
    private ArrayList<FriendMesVo> friends;
    private ArrayList<FriendMesVo> initFriends;
    private Filter filter;
    private ViewHolder holder;
    private ListView actualListView;
    private Locale locale;

    /**
     * @param friends
     *            数据
     * @param actualListView
     * @param selectedList
     *            保存了数据的每个item的默认状态是否选中，if checked该item不能操作，且改list的size必须和friends的size一致
     */
    public FriendSelectAdapter(ArrayList<FriendMesVo> friends, ListView actualListView, ArrayList<Boolean> selectedList) {
        this.friends = friends;
        this.initFriends = friends;
        this.actualListView = actualListView;
        this.selectedList = selectedList;
        this.isSelected = new ArrayList<Boolean>();
        this.locale = Locale.getDefault();
        if (selectedList == null) {
            for (int i = 0; i < friends.size(); i++) {
                this.isSelected.add(false);
            }
        } else {
            for (Boolean checked : selectedList) {
                this.isSelected.add(checked);
            }
        }
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
            view = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_select_friend, null);

            holder = new ViewHolder();
            view.setTag(holder);

            holder.tv_catalog = (TextView) view.findViewById(R.id.tv_catalog);
            holder.tv_nick = (TextView) view.findViewById(R.id.tv_nick);
            holder.img = (ImageView) view.findViewById(R.id.iv_pic);
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

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

        holder.checkBox.setChecked(isSelected.get(position));
        holder.tv_nick.setText(friends.get(position).getFriendName());
        holder.img.setImageResource(R.drawable.icon);
        String logo = friends.get(position).getPicture();
        // 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
        Utils.downloadImg(true, MarketApp.app, holder.img, logo, R.drawable.icon, actualListView);
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
        private ImageView img;
        private CheckBox checkBox;
    }
}