package com.lenovo.market.activity.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.vo.server.FriendMesVo;

/**
 * web显示页面
 * 
 * @author muqiang
 * 
 */
@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class WebViewActivity extends BaseActivity implements OnClickListener {

    public static final String DATA = "data";
    public static final String URL = "url";
    public static final String SHAREFILEPATH = "sharefilepath";
    public static final String TITLE = "title";
    public static final String SHARETITLE = "shareTitle";

    private FriendMesVo friend_;
    private WebView webView;
    private String data_;
    private String url_;
    private String title_;
    private String shareTitle_;
    private String sharefilepath_;
    private RelativeLayout webview_navigation;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_webview);
        setTitleBarRightBtnText(R.string.settings_share_text);
        setTitleBarLeftBtnText();
        data_ = getIntent().getStringExtra(DATA);
        url_ = getIntent().getStringExtra(URL);
        title_ = getIntent().getStringExtra(TITLE);
        shareTitle_ = getIntent().getStringExtra(SHARETITLE);
        sharefilepath_ = getIntent().getStringExtra(SHAREFILEPATH);
        friend_ = (FriendMesVo) getIntent().getSerializableExtra(MarketApp.FRIEND);
    }

    @Override
    protected void findViewById() {
        webview_navigation = (RelativeLayout) findViewById(R.id.webview_navigation);
        webview_navigation.setVisibility(View.GONE);
        if (null != title_ && title_.equals("首页")) {
            title_ = "微门户";
        }
        setTitleBarText(title_);

        initWebView();

        if (!TextUtils.isEmpty(data_)) {
            webView.loadDataWithBaseURL(null, data_, "text/html", "utf-8", null);
        }
        if (!TextUtils.isEmpty(url_)) {
            webView.loadUrl(url_);
        }
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
            Intent intent = new Intent(this, ShareActivity.class);
            ShareFriendsCircleActivity.sharetitle_ = shareTitle_;
            ShareFriendsCircleActivity.shareurl_ = url_;
            ShareFriendsCircleActivity.sharefilepath_ = sharefilepath_;
            startActivity(intent);
            break;
        }
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        // 加载含有JS界面
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);

        // 添加自定义js接口支持
        webView.addJavascriptInterface(new JavaScriptInterface(this), "JavaScriptInterface");

        // 自适应屏幕
        // settings.setUseWideViewPort(true);
        // settings.setLoadWithOverviewMode(true);
        // 支持缩放
        // settings.setSupportZoom(true);
        // settings.setBuiltInZoomControls(true);
        // settings.setRenderPriority(RenderPriority.HIGH);//设置渲染等级
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                log.d("load --->" + url);
                super.onPageStarted(view, url, favicon);
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                log.d("progress ----- " + newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null == ChatActivity.friend) {
            ChatActivity.friend = friend_;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
