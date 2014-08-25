package com.lenovo.market.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.lenovo.market.R;
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.activity.circle.friends.PublicChatActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.BitmapUtil;
import com.lenovo.market.util.ImageDownloader;
import com.lenovo.market.util.Utils;

/**
 * 发送图片
 * 
 * @author muqiang
 */
@SuppressWarnings("deprecation")
public class DialogThreeActivity extends BaseActivity implements OnClickListener {

    private Button dialog_taking_bt;
    private Button dialog_select_bt;
    private Button btn_cancel;
    private File file;

    @Override
    protected void setContentView() {
        setContentView(R.layout.alert_dialog_menu_layout);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void findViewById() {
        dialog_taking_bt = (Button) findViewById(R.id.dialog_taking_bt);
        dialog_taking_bt.setText("拍照");
        dialog_select_bt = (Button) findViewById(R.id.dialog_select_bt);
        dialog_select_bt.setText("从手机相册选择");
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
    }

    @Override
    protected void setListener() {
        dialog_taking_bt.setOnClickListener(this);
        dialog_select_bt.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.dialog_taking_bt:
            getCamera();
            break;
        case R.id.dialog_select_bt:
            getPhotoAlbum();
            break;
        case R.id.btn_cancel:
            finish();
            break;
        }
    }

    // 照相机调用
    private void getCamera() {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定保存的图片的位置.
        file = new File(Utils.getCacheDir(this, "pictures") + File.separator + AdminUtils.getUserInfo(context).getAccount() + "_" + System.currentTimeMillis() + ".jpg");
        Uri fileUri = Uri.fromFile(file);

        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set
        // the image file
        startActivityForResult(imageCaptureIntent, 100);
    }

    // 调用系统相册，并从中选择一张照片
    private void getPhotoAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = "";
        if (requestCode == 101) {
            if (data != null) {
                Uri uri = data.getData();
                String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = managedQuery(uri, proj, // Which columns to return
                        null, // WHERE clause; which rows to return (all rows)
                        null, // WHERE clause selection arguments (none)
                        null); // Order-by clause (ascending by name)
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                file = new File(cursor.getString(column_index));
            }
        }
        if (file != null && file.exists()) {
            Bitmap bitmap = BitmapUtil.getBitmap(file.getAbsolutePath(), context);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            }
            byte[] b = baos.toByteArray();

            FileOutputStream fos = null;
            String imageName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1, file.getAbsolutePath().lastIndexOf("."));
            if (!imageName.startsWith(AdminUtils.getUserInfo(context).getAccount())) {
                imageName = AdminUtils.getUserInfo(context).getAccount() + "_" + imageName;
            }
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

            Message updateMsg = new Message();
            updateMsg.what = MarketApp.HANDLERMESS_SEVEN;
            Bundle bundle = new Bundle();
            bundle.putSerializable("filePath", filePath);
            updateMsg.setData(bundle);
            switch (MarketApp.whichPage) {
            case 0:
                ChatActivity.handler.sendMessage(updateMsg);
                break;
            case 1:
                GroupChatActivity.handler.sendMessage(updateMsg);
                break;
            case 2:
                PublicChatActivity.handler.sendMessage(updateMsg);
                break;
            case 3:
                HomePageFragment.handler.sendMessage(updateMsg);
                break;
            }
        }
        finish();
    }
}
