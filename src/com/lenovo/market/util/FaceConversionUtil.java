package com.lenovo.market.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

import com.lenovo.market.R;
import com.lenovo.market.vo.local.ChatEmoticonsVo;

/**
 ****************************************** 
 * @文件描述 表情转换工具
 ****************************************** 
 */
public class FaceConversionUtil {

    /** 每一页表情的个数 */
    private int pageSize = 20;

    private static FaceConversionUtil mFaceConversionUtil;

    /** 保存于内存中的表情HashMap */
    private HashMap<String, String> emojiMap = new HashMap<String, String>();

    /** 保存于内存中的表情集合 */
    private ArrayList<ChatEmoticonsVo> emojis = new ArrayList<ChatEmoticonsVo>();

    /** 表情分页的结果集合 */
    public ArrayList<ArrayList<ChatEmoticonsVo>> emojiLists = new ArrayList<ArrayList<ChatEmoticonsVo>>();

    private FaceConversionUtil() {

    }

    public static FaceConversionUtil getInstace() {
        if (mFaceConversionUtil == null) {
            mFaceConversionUtil = new FaceConversionUtil();
        }
        return mFaceConversionUtil;
    }

    /**
     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
     * 
     * @param context
     * @param str
     * @return
     */
    public SpannableString getExpressionString(Context context, String str, int num) {
        SpannableString spannableString = new SpannableString(str);
        // 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
        String zhengze = "\\[[^\\]]+\\]";
        // 通过传入的正则表达式来生成一个pattern
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
        try {
            dealExpression(context, spannableString, sinaPatten, 0, num);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }

    /**
     * 添加表情
     * 
     * @param context
     * @param imgId
     * @param spannableString
     * @return
     */
    public SpannableString addFace(Context context, int imgId, String spannableString) {
        if (TextUtils.isEmpty(spannableString)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgId);
        int temp = Utils.dip2px(context, 25);
        bitmap = Bitmap.createScaledBitmap(bitmap, temp, temp, true);
        ImageSpan imageSpan = new ImageSpan(context, bitmap);
        SpannableString spannable = new SpannableString(spannableString);
        spannable.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
     * 
     * @param context
     * @param spannableString
     * @param patten
     * @param start
     * @throws Exception
     */
    private void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start, int num) throws Exception {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            String value = emojiMap.get(key);
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            int resId = context.getResources().getIdentifier(value, "drawable", context.getPackageName());
            // 通过上面匹配得到的字符串来生成图片资源id
            // Field field=R.drawable.class.getDeclaredField(value);
            // int resId=Integer.parseInt(field.get(null).toString());
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                int temp = Utils.dip2px(context, num);
                bitmap = Bitmap.createScaledBitmap(bitmap, temp, temp, true);
                // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                ImageSpan imageSpan = new ImageSpan(context, bitmap);
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                int end = matcher.start() + key.length();
                // 将该图片替换字符串中规定的位置中
                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if (end < spannableString.length()) {
                    // 如果整个字符串还未验证完，则继续。。
                    dealExpression(context, spannableString, patten, end, num);
                }
                break;
            }
        }
    }

    public void getFileText(Context context) {
        ParseData(getEmojiFile(context), context);
    }

    /**
     * 解析字符
     * 
     * @param data
     */
    private void ParseData(ArrayList<String> data, Context context) {
        if (data == null) {
            return;
        }
        emojis.clear();
        ChatEmoticonsVo emojEentry;
        try {
            for (String str : data) {
                String[] text = str.split(",");
                String fileName = text[0].substring(0, text[0].lastIndexOf("."));
                emojiMap.put(text[1], fileName);
                int resID = context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());

                if (resID != 0) {
                    emojEentry = new ChatEmoticonsVo();
                    emojEentry.setId(resID);
                    emojEentry.setCharacter(text[1]);
                    emojEentry.setFileName(fileName);
                    emojis.add(emojEentry);
                }
            }
            int pageCount = (int) Math.ceil(emojis.size() / 20 + 0.1);

            for (int i = 0; i < pageCount; i++) {
                emojiLists.add(getData(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取分页数据
     * 
     * @param page
     * @return
     */
    private ArrayList<ChatEmoticonsVo> getData(int page) {
        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;

        if (endIndex > emojis.size()) {
            endIndex = emojis.size();
        }
        // 不这么写，会在viewpager加载中报集合操作异常，我也不知道为什么
        ArrayList<ChatEmoticonsVo> list = new ArrayList<ChatEmoticonsVo>();
        list.addAll(emojis.subList(startIndex, endIndex));
        if (list.size() < pageSize) {
            for (int i = list.size(); i < pageSize; i++) {
                ChatEmoticonsVo object = new ChatEmoticonsVo();
                list.add(object);
            }
        }
        if (list.size() == pageSize) {
            ChatEmoticonsVo object = new ChatEmoticonsVo();
            object.setId(R.drawable.sl_btn_remove_expression);
            list.add(object);
        }
        return list;
    }

    /**
     * 读取表情配置文件
     * 
     * @param context
     * @return
     */
    public ArrayList<String> getEmojiFile(Context context) {
        try {
            ArrayList<String> list = new ArrayList<String>();
            InputStream in = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}