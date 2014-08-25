package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.activity.contacts.BusinessContactsActivity;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.activity.home.WebHomePageFragment;
import com.lenovo.market.common.MarketApp;
import com.lenovo.platform.zxing.CaptureActivity;
import com.lenovo.xjpsd.activity.MainActivity;

public class PopupWindowAdapter extends BaseAdapter {

    private ArrayList<String> str;
    private Context context;

    public PopupWindowAdapter(Context context, ArrayList<String> str) {
        this.context = context;
        this.str = str;
    }

    @Override
    public int getCount() {
        return str.size();
    }

    @Override
    public Object getItem(int position) {
        return str.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_custom_menu, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_item);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        tv.setText(str.get(position));
        tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != ViewPaperMenuActivity.pop) {
                    ViewPaperMenuActivity.pop.dismiss();
                    ViewPaperMenuActivity.pop = null;
                }
                if (str.get(position).equals("扫一扫")) {
                    Intent intent = new Intent(context, CaptureActivity.class);
                    context.startActivity(intent);
                }else if(str.get(position).equals("切换首页")) {
                    SharedPreferences sp = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
                    String home_page = sp.getString(MarketApp.HOME_PAGE, "");
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (TextUtils.isEmpty(home_page)) {
                        WebHomePageFragment activity = new WebHomePageFragment();
                        fragmentTransaction.replace(R.id.vp_framelayout, activity);
                    } else {
                        HomePageFragment activity = new HomePageFragment();
                        fragmentTransaction.replace(R.id.vp_framelayout, activity);
                    }
                    fragmentTransaction.commit();
                    Editor editor = sp.edit();
                    if (TextUtils.isEmpty(home_page)) {
                        home_page = "web_home_page";
                    } else {
                        home_page = "";
                    }
                    editor.putString(MarketApp.HOME_PAGE, home_page);
                    editor.commit();
                    ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ONE);
                }else if(str.get(position).equals("轻应用模式")) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }else if(str.get(position).equals("企业通讯录")) {
                    Intent intent = new Intent(context, BusinessContactsActivity.class);
                    context.startActivity(intent);
                }
            }
        });
        return view;
    }
}
