package com.lenovo.xjpsd.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.lenovo.market.R;

public class CommonUtils {

    private static Handler handler;
    public static int flag = 0;

    public static void showToast(final Context context, String text) {
        if (null == context) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        String str = (String) msg.obj;
                        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
                        super.handleMessage(msg);
                    }
                };
            }
            Message message = handler.obtainMessage();
            message.obj = text;
            message.sendToTarget();
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.psd_progressbar, null);// 得到加载view

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(v);// 设置布局
        return loadingDialog;
    }

    /**
     * 参数编码
     *
     * @return
     */
    public static String encode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~").replace("#", "%23");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 参数反编码
     *
     * @param s
     * @return
     */
    public static String decode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param context
     * @param dipValue
     * @return
     * @方法描述 dip单位转为px单位
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 对网络连接状态进行判断
     *
     * @return true, 可用； false， 不可用
     */
    public static boolean isOpenNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * 获取更新安装包文件夹路径
     *
     * @param context
     * @return
     */
    public static String getUpdateCacheDir(Context context) {
        if (null == context) {
            return null;
        }
        String path = null;
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取sd卡根目录
            path = sdDir.getPath() + "/Android/data/" + context.getPackageName() + "/update";
        }
        return path;
    }

    /**
     * 是否是合法的用户名
     *
     * @param str
     * @return
     */
    public static boolean isCorrectUserName(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9a-zA-Z_]{6,60}");
        java.util.regex.Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * 是否是合法的密码
     *
     * @param str
     * @return
     */
    public static boolean isCorrectPwd(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[0-9a-zA-Z_]{6,18}");
        java.util.regex.Matcher match = pattern.matcher(str);
        return match.matches();
    }
}
