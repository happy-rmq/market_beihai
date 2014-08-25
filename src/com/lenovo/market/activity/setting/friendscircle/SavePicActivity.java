package com.lenovo.market.activity.setting.friendscircle;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.ContentResolver;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.util.Utils;
import com.lenovo.market.view.PictureLoader;

/**
 * 朋友圈保存图片
 * 
 * @author muqiang
 * 
 */
public class SavePicActivity extends BaseActivity implements OnClickListener {

    private Button btn_cancel_;
    private Button btn_delete_;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_contacts_frienddetails_menudialog);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void findViewById() {
        btn_delete_ = (Button) findViewById(R.id.btn_delete);
        btn_delete_.setText("保存到手机");
        btn_cancel_ = (Button) findViewById(R.id.btn_cancel);
    }

    @Override
    protected void setListener() {
        btn_cancel_.setOnClickListener(this);
        btn_delete_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_cancel:
            finish();
            break;
        case R.id.btn_delete:
            File file = new File(PictureLoader.dataResult.get(0));
            ContentResolver cr = getContentResolver();
            try {
                MediaStore.Images.Media.insertImage(cr, file.getAbsolutePath(), "myPhoto", "this is a Photo");
                Utils.showToast(context, "图片保存成功！");
            } catch (FileNotFoundException e) {
                Utils.showToast(context, "图片保存失败！");
                e.printStackTrace();
            }
            finish();
            break;
        }
    }
}
