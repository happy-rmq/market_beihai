package com.lenovo.market.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lenovo.market.R;

/**
 * 图片异步下载类，包括图片的软应用缓存以及将图片存放到SDCard或者文件中
 * 
 * @author muqiang
 * 
 */
@SuppressLint("NewApi")
public class ImageDownloader {
    private static final String TAG = "ImageDownloader";
    public static HashMap<String, MyAsyncTask> map = new HashMap<String, MyAsyncTask>();
    private static String cacheDirPath_;
    private static final String cacheDirName = "pictures";
    private static Context context;
    private static LruCache<String, Bitmap> mMemoryCache;

    public ImageDownloader(Context context) {
        super();

        ImageDownloader.context = context;
        cacheDirPath_ = Utils.getCacheDir(context, cacheDirName);
        if (!TextUtils.isEmpty(cacheDirPath_)) {
            File cachedir = new File(cacheDirPath_);
            if (!cachedir.exists()) {
                cachedir.mkdirs();
            }
        }

        if (mMemoryCache == null) {
            createLruCache();
        }
    }

    private static int getBitmapSize(Bitmap bitmap) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            return;
        }
        if (getBitmapFromMemoryCache(key) == null) {
            if (mMemoryCache == null) {
                createLruCache();
            }
            mMemoryCache.put(key, bitmap);
        }
    }

    private static void createLruCache() {
        if (mMemoryCache != null) {
            return;
        }
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        MyLogger.commLog().e("maxMemory = " + maxMemory);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 16;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return getBitmapSize(bitmap) / 1024;
            }
        };
    }

    /**
     *  从缓存中移除bitmap
     * @param key
     * @return
     */
    public static boolean removeBitmapFromMemoryCache(String key){
        boolean isSuccess = false;
        if (mMemoryCache == null) {
            return isSuccess;
        }
        Bitmap bitmap = mMemoryCache.remove(key);
        if(bitmap !=null){
            isSuccess= true;
        }
        return isSuccess;
    }

    public static Bitmap getBitmapFromMemoryCache(String key) {
        if (mMemoryCache == null) {
            return null;
        }
        return mMemoryCache.get(key);
    }

    /**
     * @param isRoundedCorner 是否为圆角
     * @param url
     *            该mImageView对应的url
     * @param mImageView
     * @param context
     *            OnImageDownload回调接口，在onPostExecute()中被调用
     */
    public void imageDownload(boolean isRoundedCorner, String url, ImageView mImageView,int defaultImg, Context context, View actualListView) {
        if (url == null)
            return;
        if (mImageView == null) {
            return;
        }
        // 先从缓存及文件中拿数据
        Bitmap chacheBitmap = getBitmapFromMemoryOrDisk(context, url, isRoundedCorner);

        if (chacheBitmap == null) {
            if (TextUtils.isEmpty(url)) {
                // url为空的话 直接退出此方法
                return;
            }
            // 文件中也没有，此时根据mImageView的tag，即url去判断该url对应的task是否已经在执行，如果在执行，本次操作不创建新的线程，否则创建新的线程。
            MyAsyncTask task = new MyAsyncTask(isRoundedCorner, url, context, actualListView,defaultImg);
            if (mImageView != null) {
                Log.i(TAG, "执行MyAsyncTask --> " + Utils.flag);
                Utils.flag++;
                task.execute();
                // 将对应的url对应的任务存起来
                map.put(url, task);
            }
        } else {
            // 获取圆角图片
            if (isRoundedCorner) {
                chacheBitmap = getRoundedCornerBitmap(chacheBitmap, 10.0f);
            }
            mImageView.setImageBitmap(chacheBitmap);
        }
    }

    /**
     * 先从LruCache中取，取不到再到缓存目录中取
     * 
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromMemoryOrDisk(Context con, String url, boolean blean) {
        Bitmap bitmap = null;
        if (url == null) {
            return bitmap;
        }
        bitmap = getBitmapFromMemoryCache(url);
        // 先从缓存中拿数据,如果没有则从文件中取
        if (bitmap == null) {
            bitmap = getBitmapFromFile(con, url, blean);
        }
        return bitmap;
    }

    /**
     * 删除map中该url的信息，这一步很重要，不然MyAsyncTask的引用会“一直”存在于map中
     * 
     * @param url
     */
    private void removeTaskFormMap(String url) {
        if (url != null && map != null && map.get(url) != null) {
            map.remove(url);
            System.out.println("当前map的大小==" + map.size());
        }
    }

    /**
     * 从文件中拿图片
     * 
     * @return
     */
    private static Bitmap getBitmapFromFile(Context con, String url, boolean blean) {
        String imageName = "";
        if (!url.startsWith("http")) {
            imageName = url.substring(url.lastIndexOf("/") + 1);
        } else {
            imageName = Utils.getMD5Str(url);
        }
        if(url.contains("ba57b8e1463b63cb01463b7a18eb0083")){
            MyLogger.commLog().e(imageName);
        }
        Bitmap bitmap = null;
        File file = null;
        String cacheDir = Utils.getCacheDir(con, "picture");
        if (imageName != null) {
            try {
                file = new File(cacheDir, imageName);
                if (file.exists()) {
                    // DisplayMetrics dm = context.getResources().getDisplayMetrics();
                    // int hh = dm.heightPixels;// 这里设置高度为800f
                    // int ww = dm.widthPixels;// 这里设置宽度为480f
                    bitmap = decodeSampledBitmapFromFile(cacheDir + "/" + imageName, 100, 100);
                }
            } catch (Exception e) {
                e.printStackTrace();
                bitmap = null;
            }
        }
        if (bitmap != null) {
            addBitmapToMemoryCache(url, bitmap);
            MyLogger.commLog().d(url);
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {

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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    /**
     * 将下载好的图片存放到文件中
     * 
     */
    private void setBitmapToFile(String imageName, InputStream is) {
        File file = null;
        FileOutputStream fos = null;
        try {
            file = new File(cacheDirPath_, imageName);
            if (!file.exists()) {
                File file2 = new File(cacheDirPath_);
                file2.mkdirs();
            }

            if (Utils.hasSDCard()) {
                fos = new FileOutputStream(file);
            } else {
                fos = context.openFileOutput(cacheDirName + "/" + imageName, Context.MODE_PRIVATE);
            }

            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 100, 100);
            if (null != bitmap) {
                Bitmap compressBitmap = BitmapUtil.compressImage(bitmap);
                String cachePath = Utils.getCacheDir(context, "picture");
                setBitmapToFile(cachePath, imageName, compressBitmap);
            }
        }
    }

    public static void setBitmapToFile(String cachePath, String imageName, Bitmap bitmap) {
        File file = null;
        FileOutputStream fos = null;
        try {
            file = new File(cachePath, imageName);
            if (!file.exists()) {
                File file2 = new File(cachePath);
                file2.mkdirs();
            }

            if (Utils.hasSDCard()) {
                fos = new FileOutputStream(file);
            } else {
                fos = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap roundCorners(final Bitmap source, final float radius) {
        int width = source.getWidth();
        int height = source.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(android.graphics.Color.WHITE);
        Bitmap clipped = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(clipped);
        canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        source.recycle();
        return clipped;
    }

    /**
     * 异步下载图片的方法
     * 
     * @author yanbin
     * 
     */
    public class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private final int defalutImg;
        private View actualListView;
        private String url;
        private Context context;
        private boolean blean;

        public MyAsyncTask(boolean blean, String url, Context context, View actualListView,int defalutImg) {
            this.url = url;
            this.context = context;
            this.actualListView = actualListView;
            this.blean = blean;
            this.defalutImg = defalutImg;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap data = null;
            if (url != null) {
                try {
                    URL c_url = new URL(url);
                    String imageName = Utils.getMD5Str(url);
                    setBitmapToFile(imageName, c_url.openStream());
                } catch (FileNotFoundException exception){
                    MyLogger.commLog().w("~~~~~~文件不存在~~~~~~~~·" + (url == null ? "" : url));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // 回调设置图片
            ImageView imageView = (ImageView) actualListView.findViewWithTag(url);
            if (imageView != null) {
                Bitmap bitmap = getBitmapFromMemoryOrDisk(context, url, blean);
                if (null != bitmap) {
                    addBitmapToMemoryCache(url, bitmap);
                    if (blean) {
                        bitmap = getRoundedCornerBitmap(bitmap, 10.0f);
                    }
                    imageView.setImageBitmap(bitmap);
                    // 该url对应的task已经下载完成，从map中将其删除
                    removeTaskFormMap(url);
                } else {
                    imageView.setImageResource(defalutImg);
//                    if (blean) {
//                        imageView.setImageResource(R.drawable.icon);
//                    } else {
//                        imageView.setImageResource(R.drawable.moren);
//                    }
                }
                imageView.setTag("");
            }
            super.onPostExecute(result);
        }
    }

    // 获得圆角图片的方法
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        if (null == bitmap) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
