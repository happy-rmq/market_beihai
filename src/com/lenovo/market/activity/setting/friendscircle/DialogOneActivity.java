package com.lenovo.market.activity.setting.friendscircle;

import java.io.File;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.util.BitmapUtil;
import com.lenovo.market.util.ImageDownloader;
import com.lenovo.market.util.Utils;

/**
 * 朋友圈选择照片
 * 
 * @author muqiang
 * 
 */
@SuppressWarnings("deprecation")
public class DialogOneActivity extends BaseActivity implements OnClickListener {

    private Button dialog_taking_bt;
    private Button dialog_select_bt;
    private Button btn_cancel;
    private File file;
    private long timeMillis;

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
        timeMillis = System.currentTimeMillis();
        file = new File(Utils.getCacheDir(this, "pictures") + File.separator + timeMillis + ".jpg");
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
        String filePath;
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
        } else if (requestCode == 100) {
            Bitmap bitmap1 = ImageDownloader.decodeSampledBitmapFromFile(file.getAbsolutePath(), 100, 100);
            Bitmap compressBitmap = BitmapUtil.compressImage(bitmap1);
            String cachePath = Utils.getCacheDir(context, "picture");
            ImageDownloader.setBitmapToFile(cachePath, timeMillis + ".jpg", compressBitmap);
        }
        if (file != null && resultCode < 0) {
            filePath = file.getAbsolutePath();
            Intent intent = new Intent(this, SendFriendSquarePicActivity.class);
            intent.putExtra("filePath", filePath);
            startActivity(intent);
        } else if (data != null) {
            filePath = data.getData().getPath();
            Intent intent = new Intent(this, SendFriendSquareActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
