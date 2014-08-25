package com.lenovo.market.activity.home;

import java.lang.ref.WeakReference;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.lenovo.market.R;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.common.CommonUtil;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.SendMsgUtil;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.XmppFriendList;
import com.lenovo.platform.xmpp.XmppUtils;

/**
 * web显示页面
 * 
 * @author muqiang
 * 
 */
@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class WebHomePageFragment extends Fragment implements OnClickListener {

    public static WebHomePageHandler handler;

    private Button webview_goforward, webview_goback, webview_reload, webview_home, webview_switch;
    private RelativeLayout webview_titlebar;
    private WebView webView;
    private ProgressDialog pd;
    private String url_;
    private boolean blean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.layout_webview, container, false);
        pd = Utils.createProgressDialog(getActivity(), "正在加载数据中......");
        pd.show();
        setContentView();
        findViewById(inflate);
        return inflate;
    }

    private void setContentView() {
        handler = new WebHomePageHandler(this);
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            sendMsgToOperationalUser(AdminUtils.getOperationalAccount(getActivity()));
        } else {
            if (null != pd && pd.isShowing()) {
                pd.cancel();
                pd = null;
            }
        }
    }

    private void findViewById(View inflate) {
        webView = (WebView) inflate.findViewById(R.id.webview);
        webview_titlebar = (RelativeLayout) inflate.findViewById(R.id.webview_titlebar);
        webview_titlebar.setVisibility(View.GONE);
        webview_goforward = (Button) inflate.findViewById(R.id.webview_goforward);
        webview_goback = (Button) inflate.findViewById(R.id.webview_goback);
        webview_reload = (Button) inflate.findViewById(R.id.webview_reload);
        webview_home = (Button) inflate.findViewById(R.id.webview_home);
        webview_switch = (Button) inflate.findViewById(R.id.webview_switch);
        setListener();
    }

    private void setListener() {
        webview_goforward.setOnClickListener(this);
        webview_goback.setOnClickListener(this);
        webview_reload.setOnClickListener(this);
        webview_home.setOnClickListener(this);
        webview_switch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.webview_goforward:
            if (!blean) {
                return;
            }
            webView.goBack();
            break;
        case R.id.webview_goback:
            if (!blean) {
                return;
            }
            webView.goForward();
            break;
        case R.id.webview_reload:
            webView.reload(); // 刷新
            break;
        case R.id.webview_home:
            webview_goforward.setBackgroundResource(R.drawable.webviewtab_back_disable);
            webview_goback.setBackgroundResource(R.drawable.webviewtab_forward_disable);
            webView.loadUrl(url_);
            break;
        case R.id.webview_switch:
            if (null != ViewPaperMenuActivity.viewpaper_bottom_navigation && ViewPaperMenuActivity.viewpaper_bottom_navigation.getVisibility() == View.GONE) {
                ViewPaperMenuActivity.viewpaper_bottom_navigation.setVisibility(View.VISIBLE);
                ViewPaperMenuActivity.viewpaper_title.setVisibility(View.VISIBLE);
                webview_switch.setBackgroundResource(R.drawable.webviewtab_down);
            } else {
                ViewPaperMenuActivity.viewpaper_bottom_navigation.setVisibility(View.GONE);
                ViewPaperMenuActivity.viewpaper_title.setVisibility(View.GONE);
                webview_switch.setBackgroundResource(R.drawable.webviewtab_up);
            }
            break;
        }
    }

    private void initWebView() {
        WebSettings settings = webView.getSettings();
        // 加载含有JS界面
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);

        // 添加自定义js接口支持
        webView.addJavascriptInterface(new JavaScriptInterface(getActivity()), "JavaScriptInterface");

        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                blean = true;
                webview_goforward.setBackgroundResource(R.drawable.webviewtab_back_normal);
                webview_goback.setBackgroundResource(R.drawable.webviewtab_forward_disable);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!blean) {
                    return;
                }
                if (webView.canGoBack() && webView.canGoForward()) {
                    webview_goforward.setBackgroundResource(R.drawable.webviewtab_back_normal);
                    webview_goback.setBackgroundResource(R.drawable.webviewtab_forward_normal);
                } else if (webView.canGoBack()) {
                    webview_goforward.setBackgroundResource(R.drawable.webviewtab_back_normal);
                    webview_goback.setBackgroundResource(R.drawable.webviewtab_forward_disable);
                    System.out.println("前一页");
                } else {
                    webview_goforward.setBackgroundResource(R.drawable.webviewtab_back_disable);
                    webview_goback.setBackgroundResource(R.drawable.webviewtab_forward_normal);
                    System.out.println("后一页");
                }
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });

    }

    /**
     * 关键字 获取活动信息
     * 
     * @param operational_account
     */
    protected void sendMsgToOperationalUser(final String operational_account) {
        new Thread() {
            public void run() {
                try {
                    // 判断是否是已经关注过的
                    String jid = Utils.getJidFromUsername(operational_account);
                    if (!TextUtils.isEmpty(jid)) {
                        if (XmppUtils.getInstance() != null && XmppUtils.getInstance().getConnection() != null) {
                            if (XmppUtils.getInstance().getConnection().getRoster() != null) {
                                RosterEntry entry = XmppUtils.getInstance().getConnection().getRoster().getEntry(jid);
                                if ((entry != null) && ((entry.getType().toString().equals("to") || entry.getType().toString().equals("both")))) {
                                    MsgXmlVo mxVo = new MsgXmlVo();
                                    mxVo.setContent("首页");
                                    mxVo.setCreateTime(System.currentTimeMillis() + "");
                                    String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_TEXT, mxVo.getCreateTime());
                                    SendMsgUtil.sendMessage(Utils.getJidFromUsername(operational_account), sendxml);
                                } else {
                                    // 添加关注
                                    XmppFriendList.getInstance().addFriendForGroup(Utils.getJidFromUsername(operational_account), MarketApp.EXHIBITOR_GROUPNAME, "我添加你为好友");
                                    SendMsgUtil.sendMessage(jid, "");
                                }
                            } else {
                                // 添加关注
                                XmppFriendList.getInstance().addFriendForGroup(Utils.getJidFromUsername(operational_account), MarketApp.EXHIBITOR_GROUPNAME, "我添加你为好友");
                                SendMsgUtil.sendMessage(jid, "");
                            }
                        } else {
                            Utils.showToast(getActivity(), "连接已经断开,正在重连,请稍后再试...");
                            CommonUtil.ConnectionXmpp(getActivity());
                        }
                    }
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                if (null != pd && pd.isShowing()) {
                    pd.cancel();
                    pd = null;
                }
            }
        }.start();
    }

    public static class WebHomePageHandler extends Handler {
        WeakReference<WebHomePageFragment> mActivity;

        public WebHomePageHandler(WebHomePageFragment activity) {
            mActivity = new WeakReference<WebHomePageFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WebHomePageFragment activity = mActivity.get();
            if (null == activity) {
                return;
            }
            activity.url_ = (String) msg.obj;
            activity.initWebView();
            activity.webView.loadUrl(activity.url_);
            if (null != activity.pd && activity.pd.isShowing()) {
                activity.pd.cancel();
                activity.pd = null;
            }
        }
    }
}
