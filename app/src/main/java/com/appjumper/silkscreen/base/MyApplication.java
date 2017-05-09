package com.appjumper.silkscreen.base;

import android.app.Application;

import com.appjumper.silkscreen.util.Configure;
import com.appjumper.silkscreen.util.manager.MyUserManager;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import im.fir.sdk.FIR;


/**
 * Created by Administrator on 2016/6/29.
 */
public class MyApplication extends Application{
    public static boolean hadInit; // 如果为false
    private MyUserManager myUserBeanManager; // 说明Application以及被系统回收了，需要重新初始化一遍所有的Manager


    // 微信
    public static String WXappId="wx6993e67a6c7d8942";
    public static String WXappSecret="926caf71e41137a4478a08cd24f29f1a";

    // qq
    public static String QQappId="1105733775";
    public static String QQappKey="c7394704798a158208a74ab60104f0ba";

    public static MyApplication appContext;


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        UMShareAPI.get(this);
        FIR.init(this);
        // 初始化配置信息
        Configure.init(this);
        checkInit();
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        //微信
        PlatformConfig.setWeixin(WXappId, WXappSecret);
        //qq
        PlatformConfig.setQQZone(QQappId, QQappKey);
    }
    public void checkInit() {
        if (!hadInit) {
            myUserBeanManager = new MyUserManager(this);
            myUserBeanManager.checkUserInfo();
            myUserBeanManager.checkHomeInfo();
            hadInit = true;
        }
    }

    public MyUserManager getMyUserManager() {
        return myUserBeanManager;
    }
}
