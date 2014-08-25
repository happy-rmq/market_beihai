package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.tencent.mm.sdk.platformtools.Util;

public class AlertAdapter extends BaseAdapter {
	
	public static final int TYPE_BUTTON = 0;
	public static final int TYPE_CANCEL = 1;
	private ArrayList<String> items;
	private int[] types;

	public AlertAdapter(String[] items, String cancel) {
		if (items == null || items.length == 0) {
			this.items = new ArrayList<String>();
		} else {
			this.items = (ArrayList<String>) Util.stringsToList(items);
		}
		this.types = new int[this.items.size() + 3];
		if (cancel != null && !cancel.equals("")) {
		    this.types[this.items.size()] = TYPE_CANCEL;
			this.items.add(cancel);
		}
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String textString = (String) getItem(position);
		ViewHolder holder;
		int type = types[position];
		if (convertView == null || ((ViewHolder) convertView.getTag()).type != type) {
			holder = new ViewHolder();
			if (type == TYPE_CANCEL) {
				convertView = View.inflate(MarketApp.app, R.layout.alert_dialog_menu_list_layout_cancel, null);
			} else if (type == TYPE_BUTTON) {
				convertView = View.inflate(MarketApp.app, R.layout.alert_dialog_menu_list_layout, null);
			}

			holder.text = (TextView) convertView.findViewById(R.id.popup_text);
			holder.type = type;

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(textString);
		return convertView;
	}

	private static class ViewHolder {
	    private TextView text;
	    private int type;
	}
}
