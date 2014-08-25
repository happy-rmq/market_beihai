package com.lenovo.xjpsd.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.xjpsd.utils.CommonUtils;

/**
 * web显示页面
 *
 * @author muqiang
 */
@SuppressLint({"SetJavaScriptEnabled", "HandlerLeak"})
public class WebViewActivity extends Activity implements OnClickListener, DownloadListener {

    private WebView mWebView;
    private Button titlebar_left_bt;
    private TextView titlebar_title_tv;
    private String url;
    private ImageView fragment_item_img;
    private RelativeLayout fragment_item_rl;
    private ProgressBar progressBar1;
    private Timer timer;
    private boolean blean;
    private int num;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            progressBar1.setVisibility(View.GONE);
            fragment_item_rl.setVisibility(View.VISIBLE);
            System.out.println("newProgress=" + num);
            mWebView.stopLoading();
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.psd_webview);
        url = getIntent().getStringExtra("url");
        timer = new Timer();
        findViewById();
    }

    private void findViewById() {
        mWebView = (WebView) findViewById(R.id.webView);
        titlebar_left_bt = (Button) findViewById(R.id.titlebar_left_bt);
        titlebar_left_bt.setOnClickListener(this);
        titlebar_title_tv = (TextView) findViewById(R.id.titlebar_title_tv);
        titlebar_title_tv.setText("轻应用");
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        fragment_item_img = (ImageView) findViewById(R.id.fragment_item_img);
        fragment_item_rl = (RelativeLayout) findViewById(R.id.fragment_item_rl);
        fragment_item_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(url) && CommonUtils.isOpenNetwork(WebViewActivity.this)) {
                    mWebView.loadUrl(url);
                    progressBar1.setVisibility(View.VISIBLE);
                    fragment_item_rl.setVisibility(View.GONE);
                } else {
                    fragment_item_rl.setVisibility(View.VISIBLE);
                    CommonUtils.showToast(WebViewActivity.this, "网络不给力，请调整好您的网络！");
                }
            }
        });
        initWebView();
        if (!TextUtils.isEmpty(url) && CommonUtils.isOpenNetwork(this)) {
            mWebView.loadUrl(url);
        } else {
            fragment_item_rl.setVisibility(View.VISIBLE);
            CommonUtils.showToast(this, "网络不给力，请调整好您的网络！");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_bt:
                finish();
                break;
        }
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        // 加载含有JS界面
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        // 添加自定义js接口支持
        // mWebView.addJavascriptInterface(new JavaScriptInterface(this), "jsinterface");
        mWebView.setDownloadListener(this);
        mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("url++=" + url);
                // view.loadUrl(url);
                String authname = "";
                // if (null != CommonUtils.UMODEL) {
                // authname = CommonUtils.UMODEL.getUserName();
                // }
                Intent intent = new Intent(WebViewActivity.this, WebViewActivity.class);
                if (url.contains("&")) {
                    url += "&authname=" + authname;
                } else {
                    url += "?authname=" + authname;
                }
                intent.putExtra("url", url);
                startActivity(intent);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("url" + url);
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar1.setVisibility(View.GONE);
                    fragment_item_rl.setVisibility(View.GONE);
                    blean = true;
                }
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if (!blean) {
                            num = newProgress;
                            handler.sendEmptyMessage(0);
                        }
                    }
                }, 10000);
            }
        });
    }

    /**
     * Notify the host application that a file should be downloaded
     *
     * @param url                The full url to the content that should be downloaded
     * @param userAgent          the user agent to be used for the download.
     * @param contentDisposition Content-disposition http header, if present.
     * @param mimetype           The mimetype of the content reported by the server
     * @param contentLength      The file size reported by the server
     */
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        finish();
    }
}
