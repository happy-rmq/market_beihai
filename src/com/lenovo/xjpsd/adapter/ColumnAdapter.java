package com.lenovo.xjpsd.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.xjpsd.model.ActionBarMenuModel;

/**
 * Created by zhouyang on 13-12-25.
 */
public class ColumnAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private ArrayList<ActionBarMenuModel> list;

    // private int ids[];

    public ColumnAdapter(Context context, ArrayList<ActionBarMenuModel> list) {
        this.list = list;
        // this.ids = ids;
        inflater = LayoutInflater.from(context);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     */
    @Override
    public ActionBarMenuModel getItem(int position) {
        return list.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) will apply default layout parameters unless you use {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)} to specify a root view and to prevent
     * attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.psd_column_adapter_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.column_adapter_name);
            // viewHolder.image = (ImageView) convertView.findViewById(R.id.column_adapter_img);
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.column_adapter_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int id = 0;
        if (position % 4 == 0) {
            id = R.drawable.psd_color1;
        } else if (position % 4 == 1) {
            id = R.drawable.psd_color2;
        } else if (position % 4 == 2) {
            id = R.drawable.psd_color3;
        } else if (position % 4 == 3) {
            id = R.drawable.psd_color4;
        }
        viewHolder.layout.setBackgroundResource(id);
        ActionBarMenuModel cm = getItem(position);
        if (cm != null && cm.getName() != null) {
            viewHolder.text.setText(cm.getName());
        }
        // viewHolder.image.setImageResource(ids[position]);
        return convertView;
    }

    static class ViewHolder {
        TextView text;
        // ImageView image;
        LinearLayout layout;
    }
}
