package com.appjumper.silkscreen.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Botx on 2016/11/2.
 */

public class AppTool {

    public static DecimalFormat df = new DecimalFormat("0.00");
    public static DecimalFormat dfRound = new DecimalFormat("0.00");

    static {
        dfRound.setRoundingMode(RoundingMode.HALF_UP);
    }


    /**
     * 显示软键盘
     * @param context
     * @param view
     */
    public static void showSoftInput(Context context, View view) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
    }

    /**
     * 隐藏软键盘
     * @param activity
     */
    public static void hideSoftInput(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 获取当前版本号
     * @return String
     */
    public static String getVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    /**
     * 时间格式化
     * @param time
     * @param format
     * @return string
     */
    public static String dateFormat(long time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    /**
     * 字符串时间转换为毫秒
     * @param time
     * @param format
     * @return
     */
    public static long getTimeMs(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long result = 0;
        try {
            result = sdf.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 字符串时间转换为Date
     * @param time
     * @param format
     * @return
     */
    public static Date getDate(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 拨打电话
     * @param phone
     */
    public static void dial(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    /**
     * 中文字符过滤方法
     * @param str
     * @return String
     */
    public static String stringFilter(String str) {
        // 正则表达式匹配双字节字符（中文和中文标点符号）
        String regEx = "[^\\x00-\\xff]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 设置背景界面的透明度
     * @param activity
     * @param bgAlpha
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }

}
