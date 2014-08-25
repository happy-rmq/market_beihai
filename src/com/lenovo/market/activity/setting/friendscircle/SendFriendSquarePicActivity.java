package com.lenovo.market.activity.setting.friendscircle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.kobjects.base64.Base64;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendSquareDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.BitmapUtil;
import com.lenovo.market.util.ImageDownloader;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.MFriendZoneImageVo;
import com.lenovo.market.vo.server.MFriendZoneTopicVo;
import com.lenovo.market.vo.server.ResultVo;

/**
 * 好友圈发布照片
 * 
 * @author muqiang
 * 
 */
@SuppressWarnings("deprecation")
public class SendFriendSquarePicActivity extends BaseActivity implements OnClickListener {

    public static String filePathT;
    public static boolean bleanCancel;

    private EditText et_sign_;// 名字文本框
    private LinearLayout friend_square_pic_fl;
    private FriendSquareDBHelper fsDb;
    private String filePath;// 照片的路径
    private int width;// 屏幕的宽度
    private int imageWidth;// 每一个控件的大小
    private int num;// 显示多少个子控件
    private MFriendZoneTopicVo mVo;
    private StringBuffer sb;
    private ArrayList<String> paths;
    private ArrayList<String> ios;
    private LinearLayout ll;
    private LinearLayout layou;
    private int index = 0;
    private boolean blean;
    private boolean bRecult;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_friend_square_pic);
        setTitleBarRightBtnText(R.string.title_send);
        setTitleBarLeftBtnText();
        fsDb = new FriendSquareDBHelper();
        imageWidth = Utils.dip2px(context, 70);
        filePath = getIntent().getExtras().getString("filePath");
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
        ios = new ArrayList<String>();
        paths = new ArrayList<String>();
        sb = new StringBuffer();
    }

    @Override
    protected void findViewById() {
        et_sign_ = (EditText) findViewById(R.id.friend_square_pic_speak);
        friend_square_pic_fl = (LinearLayout) findViewById(R.id.friend_square_pic_fl);
        ll = new LinearLayout(context);
        ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.LEFT);
        friend_square_pic_fl.addView(ll);

        layou = new LinearLayout(context);
        layou.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, Utils.dip2px(context, 5), 0, 0);
        layou.setLayoutParams(lp);
        layou.setOrientation(LinearLayout.HORIZONTAL);
        layou.setGravity(Gravity.LEFT);
        friend_square_pic_fl.addView(layou);

        width = width - Utils.dip2px(context, 20);
        num = width / imageWidth;
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
            String sign = et_sign_.getText().toString().trim();
            // new FileUploadTask(filePath, whichPage).execute(url, FileUploadTask.FILE_TYPE_IMAGE, uid);
            sendMessage(sign);
            break;
        }
    }

    private void getBitmap() {
        if (bRecult) {
            if (layou.getChildCount() < 1) {
                ll.removeViewAt(ll.getChildCount() - 1);
            } else {
                layou.removeViewAt(layou.getChildCount() - 1);
            }
            index--;
            bRecult = false;
        }

        if (!TextUtils.isEmpty(filePathT)) {
            filePath = filePathT;
            filePathT = null;
        }

        Bitmap bitmap = BitmapUtil.getBitmap(filePath, context);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        byte[] b = baos.toByteArray();
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        bitmap = BitmapFactory.decodeStream(stream);
        String toBase64 = Base64.encode(b);
        ios.add(toBase64);

        String strPath = "com.lenovo.market";
        if (!filePath.contains(strPath)) {
            FileOutputStream fos = null;
            String imageName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
            try {
                File f = new File(Utils.getCacheDir(context, "pictures") + File.separator + imageName + ".jpg");
                if (!f.exists()) {

                    if (Utils.hasSDCard()) {
                        fos = new FileOutputStream(f);
                    } else {
                        fos = context.openFileOutput(imageName + ".jpg", Context.MODE_PRIVATE);
                    }
                    fos.write(b);
                    fos.flush();
                    if (fos != null) {
                        fos.close();
                    }
                }
                filePath = f.getAbsolutePath();
                Bitmap bitmap1 = ImageDownloader.decodeSampledBitmapFromFile(filePath, 100, 100);
                Bitmap compressBitmap = BitmapUtil.compressImage(bitmap1);
                String cachePath = Utils.getCacheDir(context, "picture");
                ImageDownloader.setBitmapToFile(cachePath, imageName + ".jpg", compressBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        paths.add(filePath);

        for (int i = 0; i < 2; i++) {
            final ImageView iv = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
            params.leftMargin = Utils.dip2px(context, 10);
            params.width = Utils.dip2px(context, 60);
            params.height = Utils.dip2px(context, 60);
            iv.setLayoutParams(params);
            iv.setTag(index);
            index++;
            iv.setScaleType(ScaleType.CENTER_CROP);
            iv.setImageResource(R.drawable.sl_btn_roominfo_add);
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if ((Integer) iv.getTag() == (ll.getChildCount() + layou.getChildCount()) - 1) {
                        Intent intent = new Intent(context, DialogTwoActivity.class);
                        startActivity(intent);
                        bRecult = true;
                    }
                }
            });
            if (num == ll.getChildCount()) {
                if (!blean) {
                    iv.setImageResource(R.drawable.sl_btn_roominfo_add);
                    blean = true;
                } else {
                    if (i == 0) {
                        iv.setImageBitmap(bitmap);
                    } else {
                        iv.setImageResource(R.drawable.sl_btn_roominfo_add);
                    }
                }
                if (layou.getChildCount() < num) {
                    layou.addView(iv);
                }
            } else {
                if (i == 0) {
                    iv.setImageBitmap(bitmap);
                } else {
                    iv.setImageResource(R.drawable.sl_btn_roominfo_add);
                }
                ll.addView(iv);
            }
        }
    }

    private void sendMessage(String content) {
        final long currentTimeMillis = System.currentTimeMillis();
        mVo = new MFriendZoneTopicVo(content, "1", "1", "", "", AdminUtils.getUserInfo(context).getAccount(), currentTimeMillis + "", AdminUtils.getUserInfo(context).getAccount());
        String id = Utils.getDeviceUUID();
        mVo.setId(id);
        fsDb.insertNewMessage(mVo);
        for (int i = 0; i < paths.size(); i++) {
            MFriendZoneImageVo mIo = new MFriendZoneImageVo(mVo.getId(), "", "", paths.get(i));
            String idImg = Utils.getDeviceUUID();
            mIo.setId(idImg);
            mIo.setLoginUser(AdminUtils.getUserInfo(context).getAccount());
            fsDb.insertFriendSquareImg(mIo);
            mVo.getImages().add(mIo);
            sb.append(ios.get(i) + "__" + mIo.getId() + "___");
        }

        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("id", id);
        maps.put("content", content);
        maps.put("setting", null);
        maps.put("isShare", null);
        maps.put("shareTitle", null);
        maps.put("shareUrl", null);
        maps.put("createUser", AdminUtils.getUserInfo(this).getAccount());
        maps.put("inputStr", sb.toString());
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
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.FRIENDSQUARE_SENDMESSAGE, MarketApp.FRIENDSQUARE, TaskConstant.GET_DATA_16);
        finish();
        android.os.Message updateMsg = new android.os.Message();
        updateMsg.what = MarketApp.HANDLERMESS_ONE;
        updateMsg.obj = mVo;
        FriendsCircleActivity.handler.sendMessage(updateMsg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bleanCancel) {
            getBitmap();
        }
    }
}
