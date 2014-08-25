package com.lenovo.market.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;

/**
 * Created by zhouyang on 2014/5/21 0021.
 */
public class FtpDownLoadTask extends AsyncTask<String, Integer, Boolean> {

    private final Context context;
    private final View view;// 在listview中显示图片就穿listview，直接在imageview上显示就穿imageview
    private String fileName;
    private String localPath;
    private String url;

    /**
     *
     * @param context
     * @param url 下载路径
     * @param view 如果下载listview中的图片则传listview，如果下载图片在ImageView上显示则穿imageview本身。
     */
    public FtpDownLoadTask(Context context,String url, View view) {
        this.context = context;
        this.url = url;
        this.view = view;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        fileName = params[0];
        localPath = params[1];
        String remotePath = params[2];
        boolean isSuccess = FtpFile.downFile(fileName, localPath, remotePath);
        return isSuccess;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(success){
            if(view != null){
                ImageView imageView = (ImageView) view.findViewWithTag(url);
                Bitmap bitmap = getBitmapFromFile();
                imageView.setImageBitmap(bitmap);
            }
//            Bitmap bitmap = getBitmapFromFile();
//            img.setImageBitmap(bitmap);
        }
        super.onPostExecute(success);
    }

    private Bitmap getBitmapFromFile() {
        Bitmap bitmap = null;
        File file = null;
        if (fileName != null) {
            try {
                file = new File(localPath);
                if (file.exists()) {
                    bitmap = decodeSampledBitmapFromFile(localPath, 100, 100);
                }
            } catch (Exception e) {
                e.printStackTrace();
                bitmap = null;
            }
        }
        return bitmap;
    }

    public Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
