package com.lenovo.xjpsd.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.xjpsd.adapter.ColumnAdapter;
import com.lenovo.xjpsd.fragment.ItemFragment;
import com.lenovo.xjpsd.fragment.ItemFragment.OnFragmentInteractionListener;
import com.lenovo.xjpsd.fragment.LeftCategoryFragment;
import com.lenovo.xjpsd.fragment.RightLoginFragment;
import com.lenovo.xjpsd.model.ActionBarMenuModel;
import com.lenovo.xjpsd.model.ResultModel;
import com.lenovo.xjpsd.net.ResultParser;

public class MainActivity extends SlidingFragmentActivity implements OnClickListener, OnFragmentInteractionListener {

    private Button main_left_bt;
    private Button main_right_bt;
    private ArrayList<Fragment> mFragmentsList;
    private LinearLayout ll_navigation;// 导航栏水平布局
    private LinearLayout layout;
    private LinearLayout layout2;
    private ImageView iv_navigation_arrow_down;// 下拉箭头
    private ImageView iv_navigation_arrow_up;
    private LinearLayout layout2_container;// 下拉页内容区域
    private LayoutInflater inflater;
    private ArrayList<ActionBarMenuModel> list;
    private MainHandler handler;

    static class MainHandler extends Handler {

        WeakReference<MainActivity> mActivity;

        public MainHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            activity.main_left_bt.setVisibility(View.VISIBLE);
        }

        ;
    }

    ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        handler = new MainHandler(this);
        initSlidingMenu();
        initLayout();
        initLayout2();
        initListener();
        downloadData();
    }

    private void initSlidingMenu() {
        getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
        getSlidingMenu().setShadowDrawable(R.drawable.shadow);
        getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
        getSlidingMenu().setFadeDegree(0.35f);
        setContentView(R.layout.psd_main);
        setBehindContentView(R.layout.psd_left_content);
        getSupportFragmentManager().beginTransaction().replace(R.id.left_content_id, new LeftCategoryFragment()).commit();
        getSlidingMenu().setSecondaryMenu(R.layout.psd_right_content);
        getSupportFragmentManager().beginTransaction().replace(R.id.right_content_id, new RightLoginFragment()).commit();
    }

    private void initLayout() {
        layout = (LinearLayout) findViewById(R.id.layout1);
        ll_navigation = (LinearLayout) findViewById(R.id.ll_navigation);
        iv_navigation_arrow_down = (ImageView) findViewById(R.id.iv_navigation_arrow_down);
        main_left_bt = (Button) findViewById(R.id.main_left_bt);
        main_right_bt = (Button) findViewById(R.id.main_right_bt);
    }

    private void initLayout2() {
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout2_container = (LinearLayout) findViewById(R.id.layout2_container);
        iv_navigation_arrow_up = (ImageView) findViewById(R.id.iv_navigation_arrow_up);
    }

    private void initListener() {
        main_left_bt.setOnClickListener(this);
        main_right_bt.setOnClickListener(this);
        iv_navigation_arrow_down.setOnClickListener(this);
        iv_navigation_arrow_up.setOnClickListener(this);
    }

    // 拉取一 二级菜单
    private void downloadData() {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        String uid = AdminUtils.getUserInfo(this).getServId();
        if (TextUtils.isEmpty(uid)) {
            uid = AdminUtils.getUserInfo(this).getDefaultServId();
        }
        maps.put("servid", uid);
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
            }

            @Override
            public void onComplete(String resulte) {
                ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        TypeToken<ResultModel<ActionBarMenuModel>> typeToken = new TypeToken<ResultModel<ActionBarMenuModel>>() {
                        };
                        ResultModel<ActionBarMenuModel> pageDataVo = ResultParser.parseJSON(rVo.getMsg().toString(), typeToken);
                        if (pageDataVo != null) {
                            list = pageDataVo.getColumnVOs();
                            initLayoutData(list);
                            initLayout2Data(list);
                        }
                    }
                }
            }

            @Override
            public void onCancel() {
            }
        }, maps, MarketApp.GETCOLUMN, MarketApp.USERSERVICE, TaskConstant.GET_DATA_40);
    }

    private void createColumn(ActionBarMenuModel model) {
        if (model == null)
            return;
        if (model.getColumnVOs().size() == 0)
            return;
        View view = inflater.inflate(R.layout.psd_column_item, null);
        TextView tv = (TextView) view.findViewById(R.id.column_name);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        String name = model.getName();
        if (!TextUtils.isEmpty(name)) {
            tv.setText(name);
        }
        if (model.getColumnVOs() != null) {
            ArrayList<ActionBarMenuModel> list = model.getColumnVOs();
            if (list.size() > 0) {
                final ColumnAdapter adapter = new ColumnAdapter(this, list);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ActionBarMenuModel cm = adapter.getItem(position);
                        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", cm.getUrl());
                        startActivity(intent);
                    }
                });
            }
        }
        layout2_container.addView(view);
    }

    private void initLayoutData(ArrayList<ActionBarMenuModel> list) {
        mFragmentsList = new ArrayList<Fragment>();
        int i = 0;
        for (ActionBarMenuModel model : list) {
            if (!model.getPid().equals("root") || model.getIsShortcut() == 1) {
                continue;
            }
            model.setUrl(model.getUrl());

            ItemFragment fragment = ItemFragment.newInstance(model.getName(), model.getUrl());
            mFragmentsList.add(fragment);
            TextView tv = (TextView) inflater.inflate(R.layout.psd_navigationbar_item, null);
            if (i == 0) {
                tv.setSelected(true);
                tv.setTextColor(Color.parseColor("#cc0000"));
            } else {
                tv.setTextColor(Color.BLACK);
            }
            tv.setText(model.getName());
            tv.setTag(i);
            ll_navigation.addView(tv);
            i++;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ItemFragment fragment = (ItemFragment) mFragmentsList.get(0);
        fragmentTransaction.add(R.id.main_relativelayout, fragment);
        fragmentTransaction.commit();
    }

    private void initLayout2Data(ArrayList<ActionBarMenuModel> list) {
        MarketApp.leftList.clear();
        for (ActionBarMenuModel model : list) {
            if (model.getPid().equals("root") && model.getIsShortcut() == 1) {
                MarketApp.leftList.add(model);
                continue;
            }
            if (model.getIsShortcut() == 0) {
                String code = model.getId();
                // the first level menu
                for (ActionBarMenuModel m : list) {
                    String pCode = m.getPid();
                    if (!TextUtils.isEmpty(pCode) && pCode.equals(code)) {
                        // the child of the first level menu
                        model.getColumnVOs().add(m);
                    }
                }
                createColumn(model);
            }
        }
        if (MarketApp.leftList.size() > 0) {
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_left_bt:
                showMenu();
                break;
            case R.id.main_right_bt:
                getSupportFragmentManager().beginTransaction().replace(R.id.right_content_id, new RightLoginFragment()).commit();
                showSecondaryMenu();
                break;
            case R.id.iv_navigation_arrow_down:
                layout2.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                break;
            case R.id.iv_navigation_arrow_up:
                layout.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && layout2.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.GONE);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void changeSelected(View view) {
        if (view instanceof TextView) {
            int size = ll_navigation.getChildCount();
            for (int i = 0; i < size; i++) {
                View v = ll_navigation.getChildAt(i);
                if (v instanceof TextView) {
                    TextView tv = (TextView) v;
                    if (tv == view) {
                        tv.setSelected(true);
                        tv.setTextColor(Color.parseColor("#cc0000"));
                    } else {
                        tv.setSelected(false);
                        tv.setTextColor(Color.BLACK);
                    }
                }
            }
        }
    }

    public void changeSelected(int position) {
        int size = ll_navigation.getChildCount();
        for (int i = 0; i < size; i++) {
            View v = ll_navigation.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                if (i == position) {
                    tv.setSelected(true);
                    tv.setTextColor(Color.parseColor("#cc0000"));
                } else {
                    tv.setSelected(false);
                    tv.setTextColor(Color.BLACK);
                }
            }
        }
    }

    public void onNavigationBarClick(View view) {
        Integer index = (Integer) view.getTag();
        changeSelected(view);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (int i = 0; i < mFragmentsList.size(); i++) {
            ItemFragment fragment = (ItemFragment) mFragmentsList.get(i);
            if (fragment.isAdded()) {
                fragmentTransaction.hide(fragment);
            }
        }
        ItemFragment fragment = (ItemFragment) mFragmentsList.get(index);
        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.main_relativelayout, fragment);
        } else {
            fragmentTransaction.show(mFragmentsList.get(index));
        }
        fragmentTransaction.commit();
    }
}
