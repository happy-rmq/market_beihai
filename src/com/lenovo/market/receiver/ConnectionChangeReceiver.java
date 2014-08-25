package com.lenovo.market.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.common.CommonUtil;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.service.MainService;
import com.lenovo.platform.xmpp.MucUtils;

public class ConnectionChangeReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();
    private boolean success;

    @Override
    public synchronized void onReceive(final Context context, Intent intent) {

        if (null == MainService.sMainService) {
            return;
        }
        Log.e(TAG, "网络状态改变");
        // MarketApp.network_change = true;
        success = false;
        // 获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connManager.getActiveNetworkInfo();
        if (network != null) {
            success = connManager.getActiveNetworkInfo().isAvailable();
        }

        if (success) {
            Log.e(TAG, "有网络");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MarketApp.network_available = true;
            CommonUtil.ConnectionXmpp(context);
            if (FriendListFragment.handler != null) {
                FriendListFragment.handler.sendEmptyMessage(MarketApp.NETWORK_CONNECTED);
            }
            // if (GroupFragment.handler != null) {
            // GroupFragment.handler.sendEmptyMessage(MarketApp.NETWORK_CONNECTED);
            // }
        } else {
            Log.e(TAG, "无网络");
            MucUtils.clearMuc();
            MarketApp.network_available = false;
            if (FriendListFragment.handler != null) {
                FriendListFragment.handler.sendEmptyMessage(MarketApp.NETWORK_DISCONNECTED);
            }
            // if (GroupFragment.handler != null) {
            // GroupFragment.handler.sendEmptyMessage(MarketApp.NETWORK_DISCONNECTED);
            // }
        }
    }
}
