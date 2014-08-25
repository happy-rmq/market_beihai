package com.lenovo.market.activity.setting.friendscircle;

import java.util.LinkedHashMap;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendSquareDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.MFriendZoneTopicVo;
import com.lenovo.market.vo.server.ResultVo;

/**
 * 好友圈发送文字信息
 * 
 * @author muqiang
 * 
 */
public class SendFriendSquareActivity extends BaseActivity implements OnClickListener {

    private EditText et_sign_;// 名字文本框
    private String sign;
    private FriendSquareDBHelper fsDb;
    private MFriendZoneTopicVo mVO;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_settings_modifysign);
        setTitleBarText(R.string.title_friendsquare);
        setTitleBarRightBtnText(R.string.title_send);
        setTitleBarLeftBtnText();
        fsDb = new FriendSquareDBHelper();
    }

    @Override
    protected void findViewById() {
        et_sign_ = (EditText) findViewById(R.id.et_sign);
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
            if (!MarketApp.network_available) {
                Utils.showToast(context, "网络不可用,请连接网络！");
                return;
            }
            sign = et_sign_.getText().toString().trim();
            if (!TextUtils.isEmpty(sign)) {
                sendMessage(sign);
            } else {
                Utils.showToast(context, "信息不能为空！");
            }
            break;
        }
    }

    private void sendMessage(final String content) {
        final long currentTimeMillis = System.currentTimeMillis();
        mVO = new MFriendZoneTopicVo(content, "1", "1", "", "", AdminUtils.getUserInfo(context).getAccount(), currentTimeMillis + "", AdminUtils.getUserInfo(context).getAccount());
        String id = Utils.getDeviceUUID();
        mVO.setId(id);
        fsDb.insertNewMessage(mVO);
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("id", id);
        maps.put("content", content);
        maps.put("setting", null);
        maps.put("isShare", null);
        maps.put("shareTitle", null);
        maps.put("shareUrl", null);
        maps.put("createUser", AdminUtils.getUserInfo(this).getAccount());
        maps.put("inputStr", null);
        NetUtils.startTask(new TaskListener() {

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
                    log.d("result--->" + result);
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        Utils.showToast(context, "发布成功！");
                    } else {
                        Utils.showToast(context, result);
                    }
                    finish();
                    android.os.Message updateMsg = new android.os.Message();
                    updateMsg.what = MarketApp.HANDLERMESS_ONE;
                    updateMsg.obj = mVO;
                    FriendsCircleActivity.handler.sendMessage(updateMsg);
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.FRIENDSQUARE_SENDMESSAGE, MarketApp.FRIENDSQUARE, TaskConstant.GET_DATA_14);
    }
}
