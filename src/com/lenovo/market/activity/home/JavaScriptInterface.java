package com.lenovo.market.activity.home;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.lenovo.market.util.MyLogger;
import com.lenovo.platform.zxing.CaptureActivity;

public class JavaScriptInterface {
    
    private Context context;
    
    public JavaScriptInterface(Context context) {
        super();
        this.context = context;
    }

    /**
     * 调用二维码扫描功能
     */
    @JavascriptInterface
    public void scan() {
        MyLogger.commLog().d("js---->scan()");
        Intent intent = new Intent(context, CaptureActivity.class);
        context.startActivity(intent);
    }
}
