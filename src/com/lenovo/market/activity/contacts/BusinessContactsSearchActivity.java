package com.lenovo.market.activity.contacts;

import java.util.ArrayList;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.dbhelper.DepartmentMemberDBHelper;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.DepartmentMemberVo;

/**
 * 企业通讯录查询
 * 
 * @author zhouyang
 * 
 */
public class BusinessContactsSearchActivity extends BaseActivity implements OnClickListener {

    private ListView listView;
    private EditText et_search;
    private DepartmentMemberDBHelper dbHelper;
    private ArrayList<DepartmentMemberVo> list;
    private ItemAdapter adapter;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_search_business_contacts);
        dbHelper = new DepartmentMemberDBHelper();
        adapter = new ItemAdapter();
    }

    @Override
    protected void findViewById() {
        setTitleBarText("企业通讯录查找");
        setTitleBarRightBtnText("查找");
        setTitleBarLeftBtnText();
        et_search = (EditText) findViewById(R.id.et_search);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DepartmentMemberVo child = adapter.getItem(position);
                Intent intent = new Intent(BusinessContactsSearchActivity.this, DepartmentMemberActivity.class);
                intent.putExtra("member", child);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_right_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:
            finish();
            break;
        case R.id.btn_right:
            search();
            break;
        }
    }

    private void search() {
        String name = et_search.getText().toString();
        if (list != null) {
            list.clear();
            adapter.notifyDataSetChanged();
        }
        if (TextUtils.isEmpty(name)) {
            Utils.showToast(context, "请输入要查询的姓名");
            return;
        }
        list = dbHelper.searchDepartmentMembers(name);
        if (list == null || list.isEmpty()) {
            Utils.showToast(context, "查无此人");
            return;
        }

        listView.setAdapter(adapter);
    }

    static class ViewHolder {
        TextView name;
        TextView phone;
        ImageView icon;
    }

    class ItemAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public ItemAdapter() {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public DepartmentMemberVo getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_department_member, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.tv_nick);
                holder.phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.icon = (ImageView) convertView.findViewById(R.id.iv_pic);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DepartmentMemberVo child = list.get(position);
            if (child.getName() != null) {
                holder.name.setText(child.getName());
            }
            if (child.getPhonenum() != null) {
                holder.phone.setText(child.getPhonenum());
            }
            Utils.downloadImg(true, context, holder.icon, child.getPic(), R.drawable.icon, listView);
            return convertView;
        }
    }
}
