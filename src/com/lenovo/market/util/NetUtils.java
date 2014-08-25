package com.lenovo.market.util;

import java.util.LinkedHashMap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.Task;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.vo.local.RequestVo;

/**
 * 
 * @author renmq
 * 
 */
public class NetUtils {

    public static boolean hasNetwork() {
        boolean success = false;
        ConnectivityManager con = (ConnectivityManager) MarketApp.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo workinfo = con.getActiveNetworkInfo();
        if (workinfo != null) {
            success = con.getActiveNetworkInfo().isAvailable();
        }
        if (!success) {
            MyLogger.commLog().d("无网络！");
        }
        return success;
    }

    /** 组织请求 新建一个任务 并放到任务队列中 */
    public static boolean startTask(TaskListener taskListener, LinkedHashMap<String, Object> map, String methodName, String url, int taskId) {
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            Task task = new Task(taskListener, url, taskId);
            RequestVo vo = new RequestVo();
            vo.setRequestUrl(url);
            vo.setNameSpace(MarketApp.NAMESPACE);
            vo.setMethodName(methodName);
            vo.setSoapAction(MarketApp.NAMESPACE + "/" + methodName);
            vo.setMaps(map);
            task.setReqVo(vo);
            task.executeTask();
            return true;
        } else {
            Utils.showToast(MarketApp.app, "网络连接不可用，请稍后重试");
            return false;
        }
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

}
