package com.lenovo.market.activity.setting;

import java.util.LinkedHashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lenovo.market.R;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.activity.home.WebHomePageFragment;
import com.lenovo.market.activity.setting.friendscircle.FriendsCircleActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.VersionManager;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;

/**
 * 设置
 * 
 * @author zhouyang
 * 
 */
public class SettingsFragment extends Fragment implements OnClickListener {

    private RelativeLayout friend_circle;// 朋友圈
    private RelativeLayout info_layout_;// 个人信息
    private RelativeLayout qr_code_card_;// 二维码名片
    private RelativeLayout manage_layout_;// 积分管理
    private RelativeLayout theme_layout_;// 主题
    private RelativeLayout update_layout_;// 版本升级
    private RelativeLayout update_home_;// 切换主页
    private RelativeLayout introduce_layout_;// 新功能介绍
    private RelativeLayout suggest_layout_;// 反馈建议
    private RelativeLayout about_layout_;// 关于
    private RelativeLayout exit_layout_;// 退出
    private ProgressDialog pd;
    private SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_settings, container, false);
        findViewById(view);
        setListener();
        return view;
    }

    protected void findViewById(View view) {
        friend_circle = (RelativeLayout) view.findViewById(R.id.friend_circle);
        info_layout_ = (RelativeLayout) view.findViewById(R.id.settings_info);
        qr_code_card_ = (RelativeLayout) view.findViewById(R.id.settings_qr_code_card);
        manage_layout_ = (RelativeLayout) view.findViewById(R.id.settings_manage);
        theme_layout_ = (RelativeLayout) view.findViewById(R.id.settings_theme);
        update_layout_ = (RelativeLayout) view.findViewById(R.id.settings_update);
        update_layout_.setVisibility(View.GONE);
        update_home_ = (RelativeLayout) view.findViewById(R.id.settings_update_home);
        introduce_layout_ = (RelativeLayout) view.findViewById(R.id.settings_introduce);
        suggest_layout_ = (RelativeLayout) view.findViewById(R.id.settings_suggest);
        about_layout_ = (RelativeLayout) view.findViewById(R.id.settings_about);
        exit_layout_ = (RelativeLayout) view.findViewById(R.id.settings_exit);

        // title_ = (TextView) findViewById(R.id.titlebar_text);
        // title_.setText(R.string.title_setting);
        // title_.setVisibility(View.VISIBLE);

    }

    protected void setListener() {
        friend_circle.setOnClickListener(this);
        info_layout_.setOnClickListener(this);
        qr_code_card_.setOnClickListener(this);
        manage_layout_.setOnClickListener(this);
        theme_layout_.setOnClickListener(this);
        update_layout_.setOnClickListener(this);
        introduce_layout_.setOnClickListener(this);
        suggest_layout_.setOnClickListener(this);
        about_layout_.setOnClickListener(this);
        exit_layout_.setOnClickListener(this);
        update_home_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.friend_circle:
            Intent intent = new Intent(getActivity(), FriendsCircleActivity.class);
            startActivity(intent);
            break;
        case R.id.settings_info:
            if (AdminUtils.isLogin(getActivity())) {
                go2personalInfo();
            }
            break;
        case R.id.settings_qr_code_card:
            go2QRCodeCard();
            break;
        case R.id.settings_manage:
            break;
        case R.id.settings_theme:
            break;
        case R.id.settings_update:
            VersionManager manager = new VersionManager(getActivity());
            manager.checkVersion(false);
            break;
        case R.id.settings_introduce:
            break;
        case R.id.settings_suggest:
            go2Suggest();
            break;
        case R.id.settings_about:
            break;
        case R.id.settings_exit:
            AdminUtils.logout(getActivity());
            break;
        case R.id.settings_update_home:
            sp = getActivity().getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
            String home_page = sp.getString(MarketApp.HOME_PAGE, "");
            Editor editor = sp.edit();
            if (TextUtils.isEmpty(home_page)) {
                home_page = "web_home_page";
            } else {
                home_page = "";
            }
            editor.putString(MarketApp.HOME_PAGE, home_page);
            editor.commit();
            home_page = sp.getString(MarketApp.HOME_PAGE, "");
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (TextUtils.isEmpty(home_page)) {
                HomePageFragment activity = new HomePageFragment();
                fragmentTransaction.replace(R.id.vp_framelayout, activity);
            } else {
                WebHomePageFragment activity = new WebHomePageFragment();
                fragmentTransaction.replace(R.id.vp_framelayout, activity);
            }
            // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ONE);
            break;
        }
    }

    private void go2Suggest() {
        Intent intent = new Intent(getActivity(), SuggestActivity.class);
        startActivity(intent);
    }

    private void go2QRCodeCard() {
        Intent intent = new Intent(getActivity(), QRCodeCardActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到个人信息界面
     */
    private void go2personalInfo() {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        boolean startTask = NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                if (pd != null)
                    pd.dismiss();
            }

            @Override
            public void onComplete(String resulte) {
                if (pd != null)
                    pd.dismiss();
                ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        UserVo userVo = (UserVo) ResultParser.parseJSON(rVo.getMsg().toString(), UserVo.class);
                        if (userVo != null) {
                            Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
                            intent.putExtra(PersonalInfoActivity.key, userVo);
                            startActivity(intent);
                        }
                    } else {
                        String errmsg = rVo.getErrmsg();
                        errmsg = (TextUtils.isEmpty(errmsg)) ? "获取用户信息失败" : errmsg;
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.GETUSERINFO_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_6);
        if (startTask) {
            pd = Utils.createProgressDialog(getActivity(), "正在加载个人信息");
            pd.show();
        }
    }

    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // if (keyCode == KeyEvent.KEYCODE_BACK) {
    // BaseActivity.exitApp(getActivity());
    // }
    // return super.onKeyDown(keyCode, event);
    // }
}
