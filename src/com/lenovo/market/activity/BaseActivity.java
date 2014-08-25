package com.lenovo.market.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.service.MainService;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.MyLogger;

public abstract class BaseActivity extends Activity {

    protected BaseActivity context;
    /**
     * 标题栏中间textview
     */
    protected TextView title_;
    /**
     * 标题栏button
     */
    protected Button btn_left_;
    protected Button btn_right_;
    protected ImageView titlebar_right_img;

    /**
     * ProgressDialog
     */
    protected ProgressDialog pd;
    protected SharedPreferences sp;
    public static MyLogger log = MyLogger.commLog();

    // protected MyLogger zylog = MyLogger.zyLog();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context = this;
        // 刚进界面是隐藏键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!MainService.serviceState) {
            startService(getIntent().setClass(this, MainService.class));
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MainService.allActivity.add(this);
        initView();
    }

    private void initView() {
        setContentView();
        findViewById();
        setListener();
    }

    /**
     * 设置titlebar中间的文字，前提是layout文件include了titlebar.xml
     * 
     * @param titletext
     */
    protected void setTitleBarText(String titletext) {
        title_ = (TextView) findViewById(R.id.titlebar_text);
        if (null != title_ && null != titletext) {
            title_.setVisibility(View.VISIBLE);
            title_.setText(titletext);
        }
    }

    protected void setTitleBarText(int id) {
        title_ = (TextView) findViewById(R.id.titlebar_text);
        if (null != title_) {
            title_.setVisibility(View.VISIBLE);
            title_.setText(id);
        }
    }

    /**
     * 设置右边imageview的src
     * 
     * @param id
     */
    protected void setTitleBarRightImg(int id) {
        titlebar_right_img = (ImageView) findViewById(R.id.iv_right);
        titlebar_right_img.setVisibility(View.VISIBLE);
        titlebar_right_img.setImageResource(id);
    }

    /**
     * 设置titlebar左侧button文字，前提是layout文件include了titlebar.xml
     * 
     * @param btntext
     */
    protected void setTitleBarLeftBtnText(String btntext) {
        btn_left_ = (Button) findViewById(R.id.btn_left);
        if (null != btn_left_ && null != btntext) {
            btn_left_.setVisibility(View.VISIBLE);
            btn_left_.setText(btntext);
        }
    }

    protected void setTitleBarLeftBtnText() {
        btn_left_ = (Button) findViewById(R.id.btn_left);
        if (null != btn_left_) {
            btn_left_.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置titlebar左侧button文字，前提是layout文件include了titlebar.xml
     * 
     * @param btntext
     */
    protected void setTitleBarRightBtnText(String btntext) {
        btn_right_ = (Button) findViewById(R.id.btn_right);
        btn_right_.setVisibility(View.VISIBLE);
        if (null != btn_right_ && null != btntext) {
            btn_right_.setVisibility(View.VISIBLE);
            btn_right_.setText(btntext);
        }
    }

    protected void setTitleBarRightBtnText(int id) {
        btn_right_ = (Button) findViewById(R.id.btn_right);
        if (null != btn_right_) {
            btn_right_.setVisibility(View.VISIBLE);
            btn_right_.setText(id);
        }
    }

    protected abstract void setContentView();

    protected abstract void findViewById();

    protected abstract void setListener();

    /**
     * 退出app方法
     */
    @SuppressWarnings("deprecation")
    public static void exitApp(Context context) {
        MarketApp.queue.cancelAll(context);
        AdminUtils.saveGroupChatTime(context);
        // context.stopService(new Intent().setClass(context, MainService.class));
        if (null != MainService.allActivity) {
            for (Activity activity : MainService.allActivity) {
                activity.finish();
            }
            MainService.allActivity.clear();
        }
        log.e("exitApp");
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT < 8) {
            activityManager.restartPackage(context.getPackageName());
        } else {
            activityManager.killBackgroundProcesses(context.getPackageName());
        }

        if (MainService.sTaskMap.size() > 0) {
            MainService.sTaskMap.clear();
        }
        MainService.sMainService.stopSelf();
        MainService.sMainService = null;
        System.exit(0);
    }

    /**
     * 关闭除了当前activity外的所有activity
     */
    public void finishActivityExceptCurrent() {
        if (null == MainService.allActivity) {
            return;
        }
        for (Activity activity : MainService.allActivity) {
            if (activity == this) {
                continue;
            }
            activity.finish();
        }
        MainService.allActivity.clear();
        MainService.allActivity.add(this);
    }

    @Override
    protected void onDestroy() {
        MainService.allActivity.remove(this);
        super.onDestroy();
    }
}
