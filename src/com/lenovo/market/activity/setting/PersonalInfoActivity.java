package com.lenovo.market.activity.setting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;

public class PersonalInfoActivity extends BaseActivity implements OnClickListener {

    public static final String key = "user";

    private RelativeLayout modifyname_layout_;// 个人信息
    private RelativeLayout avatar_layout_;// 头像
    private RelativeLayout sex_layout_;// 性别
    private RelativeLayout sign_layout_;// 个性签名

    private TextView account_;// 账号
    private TextView name_;
    private TextView sex_;
    private TextView area_;
    private TextView sign_;

    private UserVo user_;
    private String userName_;
    private ImageView avatar_;
    /**
     * 头像文件名
     */
    private String avatarName_ = "avatar.png";

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_settings_personalsettings);
        setTitleBarText(R.string.title_personalsettings);
        setTitleBarLeftBtnText();
        user_ = (UserVo) getIntent().getSerializableExtra(key);
    }

    @Override
    protected void findViewById() {
        modifyname_layout_ = (RelativeLayout) findViewById(R.id.settings_modifyname);
        avatar_layout_ = (RelativeLayout) findViewById(R.id.settings_avatar);
        sex_layout_ = (RelativeLayout) findViewById(R.id.settings_sex);
        sign_layout_ = (RelativeLayout) findViewById(R.id.settings_sign);

        account_ = (TextView) findViewById(R.id.account);
        name_ = (TextView) findViewById(R.id.name);
        sex_ = (TextView) findViewById(R.id.sex);
        area_ = (TextView) findViewById(R.id.area);
        sign_ = (TextView) findViewById(R.id.sign);
        avatar_ = (ImageView) findViewById(R.id.user_avatar);
        if (null != user_) {
            userName_ = user_.getUserName();
            String sex = user_.getSex();
            String area = user_.getArea();
            String sign = user_.getSign();
            String picture = user_.getPicture();
            account_.setText(AdminUtils.getUserInfo(this).getAccount());
            if (!TextUtils.isEmpty(userName_)) {
                name_.setText(userName_);
            }
            if (!TextUtils.isEmpty(sex)) {
                sex_.setText(sex.equals("0") ? "男" : "女");
            }
            if (!TextUtils.isEmpty(area)) {
                area_.setText(area);
            }
            if (!TextUtils.isEmpty(sign)) {
                sign_.setText(sign);
            }
            Utils.downloadImg(true, context, avatar_, picture, R.drawable.icon, avatar_);
        }
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        modifyname_layout_.setOnClickListener(this);
        avatar_layout_.setOnClickListener(this);
        sex_layout_.setOnClickListener(this);
        sign_layout_.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.btn_left:
            finish();
            break;
        case R.id.settings_modifyname:
            modifyName();
            break;
        case R.id.settings_avatar:
            showAvatarPickDialog();
            break;
        case R.id.settings_sex:
            showSexSelectDialog();
            break;
        case R.id.settings_sign:
            modifySign();
            break;
        }
    }

    private void modifySign() {
        String sign = sign_.getText().toString().trim();
        Intent intent = new Intent(this, ModifyUserSignActivity.class);
        if (!TextUtils.isEmpty(sign)) {
            intent.putExtra(ModifyUserSignActivity.M_USER_SIGN, sign);
        }
        startActivityForResult(intent, MarketApp.HANDLERMESS_FIVE);
    }

    private void showSexSelectDialog() {
        String str = sex_.getText().toString().trim();
        Intent intent = new Intent(this, ModifySexActivity.class);
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra(ModifySexActivity.USER_SEX, str);
        }
        startActivityForResult(intent, MarketApp.HANDLERMESS_FOUR);
    }

    /**
     * 修改名字
     */
    private void modifyName() {
        Intent intent = new Intent(this, ModifyNameActivity.class);
        if (!TextUtils.isEmpty(userName_)) {
            intent.putExtra(ModifyNameActivity.USER_NAME, userName_);
        }
        startActivityForResult(intent, MarketApp.HANDLERMESS_ZERO);
    }

    /**
     * 修改用户头像选择dialog
     */
    private void showAvatarPickDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setItems(R.array.string_array_picselect, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch (which) {
                case 0:
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, MarketApp.HANDLERMESS_ONE);
                    break;
                case 1:
                    Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(openAlbumIntent, MarketApp.HANDLERMESS_TWO);
                    break;
                }
            }
        }).create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int output = Utils.dip2px(this, 60);// 裁剪60*60 dp的图片
            switch (requestCode) {
            // 修改名字
            case MarketApp.HANDLERMESS_ZERO:
                String name = data.getStringExtra(ModifyNameActivity.USER_NAME);
                userName_ = name;
                name_.setText(name);
                break;
            // 照相
            case MarketApp.HANDLERMESS_ONE:
                cropImage(data.getData(), output, output, MarketApp.HANDLERMESS_THREE);
                break;
            // 从相册选取
            case MarketApp.HANDLERMESS_TWO:
                cropImage(data.getData(), output, output, MarketApp.HANDLERMESS_THREE);
                break;
            // 裁剪图片
            case MarketApp.HANDLERMESS_THREE:
                setUserAvatar(data);
                break;
            // 修改名字
            case MarketApp.HANDLERMESS_FOUR:
                String sex = data.getStringExtra(ModifySexActivity.USER_SEX);
                sex_.setText(sex);
                break;
            // 修改名字
            case MarketApp.HANDLERMESS_FIVE:
                String sign = data.getStringExtra(ModifyUserSignActivity.M_USER_SIGN);
                sign_.setText(sign);
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUserAvatar(Intent data) {
        Bitmap photo = null;
        Uri photoUri = data.getData();
        if (photoUri != null) {
            photo = BitmapFactory.decodeFile(photoUri.getPath());
        }
        if (photo == null) {
            Bundle extra = data.getExtras();
            if (extra != null) {
                photo = (Bitmap) extra.get("data");
            }
        }
        if (null != photo) {
            saveAndUploadAvatar(photo, avatarName_);
            log.d(avatarName_);
            avatar_.setImageBitmap(photo);
        }
    }

    private void saveAndUploadAvatar(Bitmap photo, String name) {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File dir = Environment.getExternalStorageDirectory();
            String path = dir.getPath();
            path = path + "/Android/data/" + getPackageName() + "/files/pictures";
            File pictureDir = new File(path);
            FileOutputStream out = null;
            try {
                if (!pictureDir.exists()) {
                    pictureDir.mkdirs();
                }
                File file = new File(path, name);
                file.createNewFile();
                out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                uploadAvatar(Utils.bitmapToBase64(photo));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != out) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 截取图片
    public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, requestCode);
    }

    ProgressDialog pd;

    /**
     * 上传用户头像
     * 
     * @param input
     */
    private void uploadAvatar(String data) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        maps.put("fileName", avatarName_);// 头像文件名格式：账号.png eg:zhangsan.png
        maps.put("input", data);
        boolean startTask = NetUtils.startTask(new TaskListener() {

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
                        Utils.showToast(context, "上传头像成功！");
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.SAVEUSERPHOTO_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_7);
        if (startTask) {
            pd = Utils.createProgressDialog(PersonalInfoActivity.this, "正在上传头像");
            pd.show();
        }
    }
}
