package com.appjumper.silkscreen.util;


import android.content.Context;
import android.text.TextUtils;

import java.io.File;

/**
 */
public class Applibrary {

    public static String APP_DIR;
    public static String IMAGE_CACHE_DIR;
    public static String NORMAL_EX = "normal_exception.txt"; // 记录正常异常文件名
    public static String CRASH_EX = "trash_exception.txt"; // 记录异常崩溃文件名
    public static boolean RECORD_TRASH_EX = false; // 开关是否记录异常崩溃错误记录
    public static boolean RECORD_NORMAL_EX = false; // 开关是否记录异常错误记录
    public static Context mContext;

    public static void init(Context context, ApplibraryConfig config){
        try {
            mContext = context;
            if(config != null) {
                if (!TextUtils.isEmpty(config.getAppDir())) {
                    APP_DIR = config.getAppDir();
                }else{
                    APP_DIR = SDCardUtil.getExternalStoragePath() + context.getPackageName();
                }
                if (!TextUtils.isEmpty(config.getImageCacheDir())) {
                    IMAGE_CACHE_DIR = config.getImageCacheDir();
                }else{
                    IMAGE_CACHE_DIR = APP_DIR + "/image_cache";
                }
                RECORD_NORMAL_EX = config.isRecordNormalEx();
                RECORD_TRASH_EX = config.isRecordTrashEx();
            }else{
                APP_DIR = SDCardUtil.getExternalStoragePath() + context.getPackageName();
                IMAGE_CACHE_DIR = APP_DIR + "/image_cache";
            }

            new File(APP_DIR).mkdirs();
            new File(IMAGE_CACHE_DIR).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
