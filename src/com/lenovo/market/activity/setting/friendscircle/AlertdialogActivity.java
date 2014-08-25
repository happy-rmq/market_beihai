package com.lenovo.market.activity.setting.friendscircle;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lenovo.market.R;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendSquareDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.MFriendZoneTopicVo;
import com.lenovo.market.vo.server.ResultVo;

/**
 * 朋友圈删除主信息
 * 
 * @author muqiang
 * 
 */
public class AlertdialogActivity extends Activity {

    private FriendSquareDBHelper fsDb;
    private MFriendZoneTopicVo mv;
    private AlertDialog aDialog;
    private Handler handler;
    private int index;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.alert_dialog_layout);
        fsDb = new FriendSquareDBHelper();
        handler = new AlertHandler(this);

        mv = (MFriendZoneTopicVo) getIntent().getExtras().getSerializable("mv");
        index = Integer.parseInt(getIntent().getExtras().getString("index"));

        AlertDialog.Builder builder = new Builder(this);
        View view = View.inflate(this, R.layout.alert_dialog_item_layout, null);
        Button alert_dialog_cancel = (Button) view.findViewById(R.id.alert_dialog_cancel);
        Button alert_dialog_ok = (Button) view.findViewById(R.id.alert_dialog_ok);
        alert_dialog_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                aDialog.dismiss();
                finish();
            }
        });

        alert_dialog_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
                maps.put("id", mv.getId());
                NetUtils.startTask(new TaskListener() {

                    @Override
                    public void onError(int errorCode, String message) {
                    }

                    @Override
                    public void onComplete(String resulte) {
                        ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                        if (rVo != null) {
                            String result = rVo.getResult();
                            if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                                handler.sendEmptyMessage(0);
                            } else {
                                Utils.showToast(AlertdialogActivity.this, result);
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                }, maps, MarketApp.FRIENDSQUARE_CLEARMESS, MarketApp.FRIENDSQUARE, TaskConstant.GET_DATA_19);
                aDialog.dismiss();
                finish();
            }
        });

        aDialog = builder.create();
        aDialog.setOnCancelListener(new OnCancelListener() {
            // 在对话框被用户取消的时候 同时关闭activity
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        // 必须在create()后执行
        aDialog.setCanceledOnTouchOutside(false);// 调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用
        aDialog.setView(view, 0, 0, 0, 0);
        aDialog.show();
    }

    static class AlertHandler extends Handler {
        WeakReference<AlertdialogActivity> mActivity;

        public AlertHandler(AlertdialogActivity activity) {
            mActivity = new WeakReference<AlertdialogActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            AlertdialogActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }

            activity.fsDb.delCMessage(activity.mv.getId());
            activity.fsDb.delCommentMessageAll(activity.mv.getId());
            if (FriendsCircleActivity.friendSquareList != null && FriendsCircleActivity.friendSquareList.size() > 0) {
                FriendsCircleActivity.friendSquareList.remove(activity.index);
                FriendsCircleActivity.friendSquareAdapter.notifyDataSetChanged();
                // 诉它数据加载完毕;
                FriendsCircleActivity.mPullDownView.notifyDidMore();
                // 告诉它更新完毕
                FriendsCircleActivity.mPullDownView.RefreshComplete();
                activity.finish();
            }
        };
    }
}
