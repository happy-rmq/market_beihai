package com.lenovo.xjpsd.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class ResultParser {

    private static Gson gson = new Gson();

    public static <T> T parseJSON(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json) || null == clazz) {
            return null;
        }
        T t = null;
        try {
            t = gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T parseJSON(String json, TypeToken<T> token) {
        if (TextUtils.isEmpty(json) || null == token) {
            return null;
        }
        T t = null;
        try {
            t = gson.fromJson(json, token.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return t;
    }
}
