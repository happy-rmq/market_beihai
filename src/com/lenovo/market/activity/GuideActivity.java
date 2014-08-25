package com.lenovo.market.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.lenovo.market.R;
import com.lenovo.market.adapter.ViewPagerAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.UserDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.LoginUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.VersionManager;
import com.lenovo.market.vo.server.UserVo;

public class GuideActivity extends Activity implements OnClickListener {

    private ViewPager mViewPager;// 翻页控件
    private ArrayList<ImageView> dots_ = new ArrayList<ImageView>();// 底部点
    private int[] bgs_ = { R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3 };
    private int current_page_;
    private int flaggingWidth;// 在最后一页滑动屏幕的1/3才会跳转到主界面
    private GestureDetector gestureDetector;
    private Button login_go;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_guide);

        gestureDetector = new GestureDetector(this, new GuideViewTouch());
        // 获取分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        flaggingWidth = dm.widthPixels / 3;

        mViewPager = (ViewPager) findViewById(R.id.whatsnew_viewpager);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        login_go = (Button) findViewById(R.id.login_go);
        login_go.setOnClickListener(this);

        initDots();

        initViews();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 进入主界面按钮点击事件
     * 
     * @param view
     */
    public void go2main(View view) {
        SharedPreferences preferences = getSharedPreferences(MarketApp.GUIDESP, MODE_PRIVATE);
        Editor edit = preferences.edit();
        edit.putBoolean(MarketApp.IS_GUIDED, true);
        edit.putInt(MarketApp.VERSION_CODE, VersionManager.getLocalVersionCode(this));
        edit.commit();

        SharedPreferences sp_lenovo = getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
        String account = sp_lenovo.getString(MarketApp.LOGIN_ACCOUNT, "");
        if (!TextUtils.isEmpty(account)) {
            UserDBHelper userDb = new UserDBHelper();
            UserVo userVo = userDb.getUserInfo(account);
            if (userVo != null) {
                String user_account = userVo.getAccount();
                String password = userVo.getPassword();
                LoginUtils.login(this, user_account, password);
                if (view != null) {
                    Button btn = (Button) view;
                    btn.setText("正在加载");
                    btn.setEnabled(false);
                }
                return;
            }
        }

        if (AdminUtils.isLogin(this)) {
            Intent intent = new Intent(this, ViewPaperMenuActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void initViews() {
        final ArrayList<View> views = new ArrayList<View>();
        LayoutInflater mLi = LayoutInflater.from(this);
        View view = null;
        for (int i = 0; i < bgs_.length; i++) {
            view = mLi.inflate(R.layout.layout_guide_item, null);
            view.setBackgroundResource(bgs_[i]);
            views.add(view);
        }
        // 填充ViewPager的数据适配器
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(views);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initDots() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.guide_dots_group);
        ImageView img = null;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < bgs_.length; i++) {
            img = new ImageView(this);
            if (i > 0) {
                params.leftMargin = Utils.dip2px(this, 10);
                img.setEnabled(false);
            }
            img.setLayoutParams(params);
            img.setScaleType(ScaleType.MATRIX);
            img.setImageResource(R.drawable.sl_guide_dot);
            layout.addView(img);
            dots_.add(img);
        }
    }

    class GuideViewTouch extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (current_page_ == bgs_.length - 1) {
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY()) && (e1.getX() - e2.getX() <= (-flaggingWidth) || e1.getX() - e2.getX() >= flaggingWidth)) {
                    if (e1.getX() - e2.getX() >= flaggingWidth) {
                        // go2main(null);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    class MyOnPageChangeListener implements OnPageChangeListener {

        public void onPageSelected(int page) {
            current_page_ = page;
            int size = dots_.size();
            if (page == size - 1) {
                login_go.setVisibility(View.VISIBLE);
            } else {
                login_go.setVisibility(View.GONE);
            }
            ImageView img = null;
            for (int i = 0; i < size; i++) {
                img = dots_.get(i);
                if (i == page) {
                    img.setEnabled(true);
                } else {
                    img.setEnabled(false);
                }
            }
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.login_go:
            go2main(v);
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseActivity.exitApp(this);
        }
        return super.onKeyDown(keyCode, event);
    }
}