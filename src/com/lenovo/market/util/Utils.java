package com.lenovo.market.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.graphics.Canvas;
import android.graphics.Matrix;
import org.kobjects.base64.Base64;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lenovo.market.common.MarketApp;

public class Utils {

    private static Handler handler;
    public static int flag = 0;
    private static ImageDownloader loader;

    public static void showToast(final Context context, String text) {
        if (null == context) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        String str = (String) msg.obj;
                        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                        super.handleMessage(msg);
                    }
                };
            }
            Message message = handler.obtainMessage();
            message.obj = text;
            message.sendToTarget();
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToast(Context context, int id) {
        if (null != context) {
            Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建进度条
     * 
     * @param context
     * @return
     */
    public static ProgressDialog createProgressDialog(Context context, String msg) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("提示框");
        pd.setMessage(msg);
        return pd;
    }

    /**
     * @方法描述 dip单位转为px单位
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * @方法描述 px单位转为dip单位
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 文件转为base64字符串
     * 
     * @return
     * @throws IOException
     */
    public static String fileToBase64(File file) throws IOException {
        String data = null;
        if (null == file)
            return data;
        if (file.exists()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int n = 0;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            byte[] bytes = out.toByteArray();
            out.close();
            in.close();
            data = Base64.encode(bytes);
        }
        return data;
    }

    /** 得到显示的名字 */
    public static String getUsernameFromJid(String jid) {
        String username = jid;
        if (TextUtils.isEmpty(jid)) {
            return "";
        }
        if (jid.contains("@lenovo-137")) {
            username = jid.split("@lenovo-137")[0];
            if (username.contains("\\40")) {
                username = username.replace("\\40", "@");
            }
        } else if (jid.contains("\\40")) {
            return jid.replace("\\40", "@");

        }
        return username;
    }

    /** 得到注册的名字 */
    public static String getSendName(String name) {
        String[] username;
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        if (name.endsWith("@lenovo-137")) {
            username = name.split("@");
            return username[0];
        } else if (name.contains("@")) {
            return name.replace("@", "\\40");
        } else {
            return name;
        }
    }

    public static String getEmailFromJid(String email) {
        if (email.contains("@")) {
            String[] name = email.split("@");
            if (name[0].contains("\40")) {
                return name[0].replace("\\40", "@");
            } else {
                return name[0];
            }
        }

        return null;
    }

    /** 得到jid */
    public static String getJidFromUsername(String username) {
        if (username == null) {
            return null;
        }
        if (username.contains("@")) {

            if (!username.endsWith("@" + MarketApp.OPENFIRE_SERVER_NAME)) {
                return username.replace("@", "\\40") + "@" + MarketApp.OPENFIRE_SERVER_NAME;
            } else {
                return username;
            }

        } else {
            return username + "@" + MarketApp.OPENFIRE_SERVER_NAME;
        }
    }

    /**
     * 将base64串转为bitmap
     * 
     * @param str
     * @return
     */
    public static Bitmap base64ToBitmap(String str) {
        Bitmap bitmap = null;
        if (null == str)
            return bitmap;
        byte[] data = Base64.decode(str);
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }

    /**
     * bitmap转为base64串
     * 
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encode(bytes);
    }

    /**
     * 判断是否有sdcard
     * 
     * @return
     */
    public static boolean hasSDCard() {
        boolean b = false;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            b = true;
        }
        return b;
    }

    /**
     * 得到sdcard路径
     * 
     * @return
     */
    public static String getExtPath() {
        String path = "";
        if (hasSDCard()) {
            path = Environment.getExternalStorageDirectory().getPath();
        }
        return path;
    }

    /**
     * 得到/data/data/yanbin.imagedownload目录
     * 
     * @param context
     * @return
     */
    public static String getPackagePath(Context context) {
        return context.getFilesDir().toString();
    }

    /**
     * 根据url得到图片名
     * 
     * @param url
     * @return
     */
    public static String getImageName(String url) {
        String imageName = "";
        if (url != null) {
            imageName = url.substring(url.lastIndexOf("/") + 1);
        }
        return imageName;
    }

    /**
     * 获取图片缓存路径
     * 
     * @param context
     * @return
     */
    public static String getCacheDir(Context context, String dirName) {
        if (null == context) {
            return null;
        }
        String path = null;
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取sd卡根目录
            path = sdDir.getPath() + "/Android/data/" + context.getPackageName() + "/files/" + dirName;
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
        } else {
            path = context.getFilesDir().getAbsolutePath() + "/" + dirName;
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        return path;
    }

    /**
     * 删除缓存目录中指定文件
     * @param context
     * @param dirName
     * @param fileName
     * @return
     */
    public static boolean deleteCacheFile(Context context, String dirName,String fileName){
        boolean success = false;
        String cacheDir = getCacheDir(context, dirName);
        File file = new File(cacheDir + "/" + fileName);
        if(file.exists()){
            success = file.delete();
        }else{
            MyLogger.commLog().e(fileName + "不存在");
        }
        return success;
    }

    /**
     * 获取更新安装包文件夹路径
     * 
     * @param context
     * @return
     */
    public static String getUpdateCacheDir(Context context) {
        if (null == context) {
            return null;
        }
        String path = null;
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取sd卡根目录
            path = sdDir.getPath() + "/Android/data/" + context.getPackageName() + "/files/update";
        }
        return path;
    }

    /**
     * 下载图片
     * @param isRoundedCorner 是否圆角
     * 
     * @param context
     * @param img
     *            imageview
     * @param url
     *            图片路径
     * @param defaultImg
     *            默认图片
     * @param save2sdcard
     *            是否保存到sdcard
     */
    public static void downloadImg(boolean isRoundedCorner, Context context, ImageView img, String url, final int defaultImg, boolean save2sdcard, View listView) {
        if (null == img)
            return;
        if (defaultImg != Integer.MIN_VALUE) {
            img.setImageResource(defaultImg);
        }
        if (loader == null) {
            loader = new ImageDownloader(context);
        }
        img.setTag(url);
        loader.imageDownload(isRoundedCorner,url,img,defaultImg,context,listView);
    }

    /**
     * 下载图片并将下载好的的图片存到sdcard
     * 
     * @param context
     * @param img
     *            下载完图片后要更新的ImageView
     * @param url
     *            图片路径
     * @param defaultImg
     *            下载失败的话使用默认图片
     * 
     */
    public static void downloadImg(boolean blean, Context context, ImageView img, String url, final int defaultImg, View listView) {
        downloadImg(blean, context, img, url, defaultImg, true, listView);
    }

    /**
     * 手机来信息震动
     * 
     */
    public static void Vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    public static void Vibrate(long milliseconds) {
        Vibrator vib = (Vibrator) MarketApp.app.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    // public static void AudioSytemShake(Context ct) {
    // Vibrator vibrator = (Vibrator) ct.getSystemService(Context.VIBRATOR_SERVICE);
    // long[] pattern = { 30, 50, 1, 5 };// lax 400----200
    // vibrator.vibrate(pattern, -1);
    // vibrator.cancel();
    // }

    /**
     * 获取UUID
     */
    public static String getDeviceUUID() {
        UUID uuid = UUID.randomUUID();
        String result = uuid.toString().replaceAll("\\-", "");
        return result;
    }

    /**
     * MD5 加密
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    /**
     * @param jid
     * @return -1 jid为null <br/>
     *         0 jid不存在 <br/>
     *         1 在线 <br/>
     *         2 不在线
     */
    public static short isUserOnLine(String jid) {
        if (jid == null)
            return -1;
        String strUrl = "http://" + MarketApp.OPENFIRE_SERVER + ":9090/plugins/presence/status?type=xml&jid=" + jid;
        short shOnLineState = 0; // -不存在-

        try {
            URL oUrl = new URL(strUrl);
            URLConnection oConn = oUrl.openConnection();
            if (oConn != null) {
                BufferedReader oIn = new BufferedReader(new InputStreamReader(oConn.getInputStream()));
                if (null != oIn) {
                    String strFlag = oIn.readLine();
                    oIn.close();
                    System.err.println(strFlag);
                    if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
                        shOnLineState = 2;
                    }
                    if (strFlag.indexOf("type=\"error\"") >= 0) {
                        shOnLineState = 0;
                    } else if (strFlag.indexOf("priority") >= 0 || strFlag.indexOf("id=\"") >= 0) {
                        shOnLineState = 1;
                    }
                }
            }
        } catch (Exception e) {
        }

        return shOnLineState;
    }


    public static String saveBitmap2File(Context context,Bitmap photo, String name) {
        String path = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            path = getCacheDir(context,"pictures");
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
                path = file.getAbsolutePath();
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
                return path;
            }
        }
        return path;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newBitmap;
    }

    /**
     *  生成群组头像
     * @param context
     * @param bitmaps
     * @return
     */
    public static Bitmap createGroupBitmap(Context context,Bitmap[] bitmaps) {
        int width = Utils.dip2px(context, 60f);
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(200,224,224,224);
        if (bitmaps.length>2 && bitmaps.length <= 4) {
            // 个数为3或者4的 两行两列显示
            int totalMargin = Utils.dip2px(context, 6f);
            int scaledWidth = (width - totalMargin) / 2;
            Bitmap[] scaledBitmaps = new Bitmap[bitmaps.length];
            for (int i = 0; i < bitmaps.length; i++) {
                float scale = (float) scaledWidth / (float) bitmaps[i].getWidth();
                scaledBitmaps[i] = Utils.scaleBitmap(bitmaps[i], scale);
            }

            float margin = (float) totalMargin / 3f;

            float x0, x1, x2, x3, y1, y2, y3;
            switch (bitmaps.length) {
                case 3:// 共两行居中显示，第一行中间显示一个；第二行显示两个.
                    // 第一行
                    x0 = bitmap.getWidth() / 2 - scaledBitmaps[0].getWidth() / 2;
                    canvas.drawBitmap(scaledBitmaps[0], x0, margin, null);

                    // 第二行
                    y1 = margin + scaledBitmaps[0].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[1], margin, y1, null);

                    x2 = margin + scaledBitmaps[1].getWidth() + margin;
                    y2 = margin + scaledBitmaps[0].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[2], x2, y2, null);
                    break;
                case 4:// 共两行居中显示，第一行中间显示两个；第二行显示两个.
                    // 第一行
                    canvas.drawBitmap(scaledBitmaps[0], margin, margin, null);

                    x1 = margin + scaledBitmaps[0].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[1], x1, margin, null);

                    // 第二行
                    y2 = margin + scaledBitmaps[0].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[2], margin, y2, null);

                    x3 = margin + scaledBitmaps[2].getWidth() + margin;
                    y3 = margin + scaledBitmaps[0].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[3], x3, y3, null);
                    break;
            }
        } else if (bitmaps.length <= 9) {
            // // 个数为5或者6的两行三列显示;  7,8或9的三行三列显示
            int totalMargin = Utils.dip2px(context, 8f);
            int scaledWidth = (width - totalMargin) / 3;
            Bitmap[] scaledBitmaps = new Bitmap[bitmaps.length];
            for (int i = 0; i < bitmaps.length; i++) {
                float scale = (float) scaledWidth / (float) bitmaps[i].getWidth();
                scaledBitmaps[i] = Utils.scaleBitmap(bitmaps[i], scale);
            }

            float margin = (float) totalMargin / 4f;

            float x0, x1, x2, x3, x4, x5, x6, x7, x8, y0, y1, y2, y3, y4, y5, y6;
            switch (bitmaps.length) {
                case 5:// 共两行居中显示，第一行中间显示两个；第二行显示三个.
                    // 第一行
                    x0 = bitmap.getWidth() / 2.0f - margin / 2.0f - scaledBitmaps[0].getWidth();
                    y0 = bitmap.getHeight() / 2.0f - margin / 2.0f - scaledBitmaps[0].getHeight();
                    canvas.drawBitmap(scaledBitmaps[0], x0, y0, null);

                    x1 = bitmap.getWidth() / 2.0f + margin / 2.0f;
                    canvas.drawBitmap(scaledBitmaps[1], x1, y0, null);

                    // 第二行
                    y2 = bitmap.getHeight() / 2.0f + margin / 2.0f;
                    canvas.drawBitmap(scaledBitmaps[2], margin, y2, null);

                    x3 = margin + scaledBitmaps[2].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[3], x3, y2, null);

                    x4 = margin + scaledBitmaps[2].getWidth() + margin + scaledBitmaps[3].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[4], x4, y2, null);

                    break;
                case 6:// 共两行居中显示，第一行中间显示三个；第二行显示三个.
                    // 第一行
                    y0 = bitmap.getHeight() / 2.0f - margin / 2.0f - scaledBitmaps[0].getHeight();
                    canvas.drawBitmap(scaledBitmaps[0], margin, y0, null);

                    x1 = margin + scaledBitmaps[0].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[1], x1, y0, null);

                    x2 = x1 + scaledBitmaps[1].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[2], x2, y0, null);

                    // 第二行
                    y3 = bitmap.getHeight() / 2.0f + margin / 2.0f;
                    canvas.drawBitmap(scaledBitmaps[3], margin, y3, null);

                    x4 = margin + scaledBitmaps[3].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[4], x4, y3, null);

                    x5 = margin + scaledBitmaps[3].getWidth() + margin + scaledBitmaps[4].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[5], x5, y3, null);
                    break;
                case 7:// 共三行，第一行中间显示一个；第二行显示三个，第三行显示三个
                    // 第一行中间显示
                    x0 = bitmap.getWidth() / 2 - scaledBitmaps[0].getWidth() / 2;
                    canvas.drawBitmap(scaledBitmaps[0], x0, margin, null);

                    // 第二行
                    y1 = margin + scaledBitmaps[0].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[1], margin, y1, null);

                    x2 = margin + scaledBitmaps[1].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[2], x2, y1, null);

                    x3 = x2 + scaledBitmaps[2].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[3], x3, y1, null);

                    // 第三行
                    y4 = y1 + scaledBitmaps[1].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[4], margin, y4, null);

                    x5 = margin + scaledBitmaps[4].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[5], x5, y4, null);

                    x6 = x5 + scaledBitmaps[5].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[6], x6, y4, null);
                    break;
                case 8:// 共三行，第一行中间显示两个；第二行显示三个，第三行显示三个
                    // 第一行
                    x0 = bitmap.getWidth() / 2.0f - margin / 2.0f - scaledBitmaps[0].getWidth();
                    canvas.drawBitmap(scaledBitmaps[0], x0, margin, null);

                    x1 = bitmap.getWidth() / 2.0f + margin / 2.0f;
                    canvas.drawBitmap(scaledBitmaps[1], x1, margin, null);

                    // 第二行
                    y2 = margin + scaledBitmaps[0].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[2], margin, y2, null);

                    x3 = margin + scaledBitmaps[2].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[3], x3, y2, null);

                    x4 = x3 + scaledBitmaps[3].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[4], x4, y2, null);

                    // 第三行
                    y5 = y2 + scaledBitmaps[2].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[5], margin, y5, null);

                    x6 = margin + scaledBitmaps[5].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[6], x6, y5, null);

                    x7 = x6 + scaledBitmaps[6].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[7], x7, y5, null);
                    break;
                case 9:// 共三行，第一行中间显示两个；第二行显示三个，第三行显示三个
                    // 第一行
                    canvas.drawBitmap(scaledBitmaps[0], margin, margin, null);

                    x1 = margin + scaledBitmaps[0].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[1], x1, margin, null);

                    x2 = x1 + scaledBitmaps[1].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[2], x2, margin, null);

                    // 第二行
                    y3 = margin + scaledBitmaps[0].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[3], margin, y3, null);

                    x4 = margin + scaledBitmaps[3].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[4], x4, y3, null);

                    x5 = x4 + scaledBitmaps[4].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[5], x5, y3, null);

                    // 第三行
                    y6 = y3 + scaledBitmaps[3].getHeight() + margin;
                    canvas.drawBitmap(scaledBitmaps[6], margin, y6, null);

                    x7 = margin + scaledBitmaps[6].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[7], x7, y6, null);

                    x8 = x7 + scaledBitmaps[7].getWidth() + margin;
                    canvas.drawBitmap(scaledBitmaps[8], x8, y6, null);
                    break;
            }
        }
        canvas.save();
        canvas.restore();
        return bitmap;
    }

}
