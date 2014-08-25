package com.lenovo.market.activity.home;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.service.MainService;
import com.lenovo.market.util.Utils;
import com.lenovo.market.view.PictureLoader;

/**
 * 显示大图片
 * 
 * @author muqiang
 * 
 */
@SuppressWarnings("deprecation")
public class PictureViewActivity extends FragmentActivity {

    // 屏幕宽度
    public static int screenWidth;
    // 屏幕高度
    public static int screenHeight;
    public static PictureViewHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_bigimage);
        handler = new PictureViewHandler(this);
        String filePath = getIntent().getStringExtra("filePath");
        if (PictureLoader.dataResult.size() > 0) {
            PictureLoader.dataResult.clear();
        }
        if (filePath.startsWith("http")) {
            filePath = getBitmapFromFile(filePath);
        }
        PictureLoader.dataResult.add(filePath);
        initViews();
        MainService.allActivity.add(this);
    }

    public static class PictureViewHandler extends Handler {
        WeakReference<PictureViewActivity> mActivity;

        public PictureViewHandler(PictureViewActivity activity) {
            mActivity = new WeakReference<PictureViewActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            PictureViewActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
            case MarketApp.HANDLERMESS_ZERO:
                activity.finish();
                break;
            }
        }
    }

    private void initViews() {
        screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 从外部文件缓存中获取bitmap
     * 
     * @param url
     * @return
     */
    private String getBitmapFromFile(String url) {
        String fileName = Utils.getMD5Str(url);
        if (fileName == null)
            return null;

        String filePath = Utils.getCacheDir(this, "pictures") + "/" + fileName;
        return filePath;
    }
}
