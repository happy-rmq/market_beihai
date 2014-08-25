package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.BusinessContactVo;
import com.lenovo.market.vo.local.DepartmentMemberVo;
import com.lenovo.market.vo.local.DepartmentVo;

public class BusinessAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<BusinessContactVo> list;
    private ListView listView;

    public BusinessAdapter(ArrayList<BusinessContactVo> list, ListView listView) {
        mInflater = LayoutInflater.from(MarketApp.app);
        this.list = list;
        this.listView = listView;
    }

    public void setData(ArrayList<BusinessContactVo> list) {
        this.list = list;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        BusinessContactVo vo = list.get(position);
        if (vo instanceof DepartmentVo) {
            return MarketApp.HANDLERMESS_ZERO;
        } else {
            return MarketApp.HANDLERMESS_ONE;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public BusinessContactVo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder_Department holder_department = null;
        ViewHolder_Member holder_member = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
            case MarketApp.HANDLERMESS_ZERO:
                convertView = mInflater.inflate(R.layout.listitem_department, null);
                holder_department = new ViewHolder_Department();
                holder_department.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder_department);
                break;
            case MarketApp.HANDLERMESS_ONE:
                convertView = mInflater.inflate(R.layout.listitem_department_member, null);
                holder_member = new ViewHolder_Member();
                holder_member.name = (TextView) convertView.findViewById(R.id.tv_nick);
                holder_member.phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder_member.icon = (ImageView) convertView.findViewById(R.id.iv_pic);
                convertView.setTag(holder_member);
                break;
            }
        } else {
            switch (type) {
            case MarketApp.HANDLERMESS_ZERO:
                holder_department = (ViewHolder_Department) convertView.getTag();
                break;
            case MarketApp.HANDLERMESS_ONE:
                holder_member = (ViewHolder_Member) convertView.getTag();
                break;
            }
        }

        BusinessContactVo vo = list.get(position);
        switch (type) {
        case MarketApp.HANDLERMESS_ZERO:
            if (vo instanceof DepartmentVo) {
                DepartmentVo dvo = (DepartmentVo) vo;
                holder_department.name.setText(dvo.getName());
            }
            break;
        case MarketApp.HANDLERMESS_ONE:
            if (vo instanceof DepartmentMemberVo) {
                DepartmentMemberVo mvo = (DepartmentMemberVo) vo;
                holder_member.name.setText(mvo.getName());
                holder_member.phone.setText(mvo.getPhonenum());
                Utils.downloadImg(true, MarketApp.app, holder_member.icon, mvo.getPic(), R.drawable.icon_department_member, listView);
            }
            break;
        }
        return convertView;
    }

    static class ViewHolder_Department {
        TextView name;
        TextView sign;
        ImageView icon;
    }

    static class ViewHolder_Member {
        TextView name;
        TextView phone;
        TextView email;
        ImageView icon;
    }
}
