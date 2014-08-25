package com.lenovo.market.activity.home;

import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.setting.friendscircle.FriendsCircleActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendSquareDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.ImageDownloader;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.MFriendZoneImageVo;
import com.lenovo.market.vo.server.MFriendZoneTopicVo;
import com.lenovo.market.vo.server.ResultVo;

/**
 * 分享到朋友圈
 * 
 * @author muqiang
 * 
 */
public class ShareFriendsCircleActivity extends BaseActivity implements OnClickListener {

    public static String sharetitle_;// 分享的标题
    public static String shareurl_;// 分享的地址
    public static String sharefilepath_;// 分享的图片路径

    private EditText share_content_speak;
    private MFriendZoneTopicVo mVO;
    private FriendSquareDBHelper fsDb;
    private TextView share_content;
    private ImageView share_img;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_share_content);
        setTitleBarRightBtnText(R.string.send);
        setTitleBarLeftBtnText();
        fsDb = new FriendSquareDBHelper();
    }

    @Override
    protected void findViewById() {
        share_content_speak = (EditText) findViewById(R.id.share_content_speak);
        share_content = (TextView) findViewById(R.id.share_content);
        share_content.setText(sharetitle_);
        share_img = (ImageView) findViewById(R.id.share_img);
        Utils.downloadImg(true, context, share_img, sharefilepath_, R.drawable.albumshareurl_icon, share_img);
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
            String content = share_content_speak.getText().toString().trim();
            sendMessage(content);
            break;
        }
    }

    private void sendMessage(final String content) {
        final long currentTimeMillis = System.currentTimeMillis();

        Bitmap bitmap = ImageDownloader.getBitmapFromMemoryOrDisk(context, sharefilepath_, false);
        String toBase64 = null;
        if (null != bitmap) {
            toBase64 = Utils.bitmapToBase64(bitmap);
        }
        // 2 是分享
        mVO = new MFriendZoneTopicVo(content, "1", "2", sharetitle_, shareurl_, AdminUtils.getUserInfo(context).getAccount(), currentTimeMillis + "", AdminUtils.getUserInfo(context).getAccount());
        String id = Utils.getDeviceUUID();
        mVO.setId(id);
        fsDb.insertNewMessage(mVO);

        MFriendZoneImageVo mIo = new MFriendZoneImageVo(mVO.getId(), "", "", sharefilepath_);
        String idImg = Utils.getDeviceUUID();
        mIo.setId(idImg);
        mIo.setLoginUser(AdminUtils.getUserInfo(context).getAccount());
        fsDb.insertFriendSquareImg(mIo);
        mVO.getImages().add(mIo);
        if (!TextUtils.isEmpty(toBase64)) {
            toBase64 = toBase64 + "__" + mIo.getId() + "___";
        }

        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("id", id);
        maps.put("content", content);
        maps.put("setting", null);
        maps.put("isShare", "2");
        maps.put("shareTitle", sharetitle_);
        maps.put("shareUrl", shareurl_);
        maps.put("createUser", AdminUtils.getUserInfo(this).getAccount());
        maps.put("inputStr", toBase64);
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
                        Utils.showToast(context, "分享成功");
                    } else {
                        Utils.showToast(context, result);
                    }
                    finish();
                    if (null != FriendsCircleActivity.handler) {
                        android.os.Message updateMsg = new android.os.Message();
                        updateMsg.what = MarketApp.HANDLERMESS_ONE;
                        updateMsg.obj = mVO;
                        FriendsCircleActivity.handler.sendMessage(updateMsg);
                    }
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
