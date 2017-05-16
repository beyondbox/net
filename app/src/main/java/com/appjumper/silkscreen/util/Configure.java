package com.appjumper.silkscreen.util;

import android.content.Context;

public class Configure {

    public static String APP_DIR = SDCardUtil.getExternalStoragePath() + "/SilkScreen";
    public static String IMAGE_CACHE_DIR = APP_DIR + "/image_cache";
    public static final boolean RECORD_TRASH_EX = true; // 开关是否记录异常崩溃错误记录
    public static final boolean RECORD_NORMAL_EX = false; // 开关是否记录异常错误记录



    public static void init(Context context){
        try {
            ApplibraryConfig config = new ApplibraryConfig(
                    APP_DIR,
                    IMAGE_CACHE_DIR,
                    RECORD_NORMAL_EX,
                    RECORD_TRASH_EX);
            Applibrary.init(context,config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
