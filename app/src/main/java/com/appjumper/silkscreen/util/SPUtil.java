package com.appjumper.silkscreen.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.appjumper.silkscreen.base.MyApplication;

/**
 * SharedPreferences操作类
 * Created by Botx on 2017/5/31.
 */

public class SPUtil {

    private static Context context;
    static {
        context = MyApplication.appContext;
    }

    /**
     * 参数为空字符串或者null时返回默认的SP，否则返回指定的SP
     * @param spName
     * @return
     */
    public static SharedPreferences getSharedPreferences(String spName) {
        if (TextUtils.isEmpty(spName)) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            return context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        }
    }

    public static void putString(String spName, String key, String value) {
        SharedPreferences sp = getSharedPreferences(spName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String spName, String key, String defValue) {
        return getSharedPreferences(spName).getString(key, defValue);
    }

    public static void putBoolean(String spName, String key, boolean value) {
        SharedPreferences sp = getSharedPreferences(spName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String spName, String key, boolean defValue) {
        return getSharedPreferences(spName).getBoolean(key, defValue);
    }

    public static void putInt(String spName, String key, int value) {
        SharedPreferences sp = getSharedPreferences(spName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(String spName, String key, int defValue) {
        return getSharedPreferences(spName).getInt(key, defValue);
    }

    public static void remove(String spName, String key) {
        SharedPreferences sp = getSharedPreferences(spName);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void clear(String spName) {
        SharedPreferences sp = getSharedPreferences(spName);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

}
