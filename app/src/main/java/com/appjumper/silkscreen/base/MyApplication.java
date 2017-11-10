package com.appjumper.silkscreen.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.ui.my.BindMobileActivity;
import com.appjumper.silkscreen.ui.my.enterprise.CertifyManageActivity;
import com.appjumper.silkscreen.util.manager.MyUserManager;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;


/**
 * Created by Administrator on 2016/6/29.
 */
public class MyApplication extends Application{
    public boolean hadInit; // 如果为false
    private MyUserManager myUserBeanManager; // 说明Application以及被系统回收了，需要重新初始化一遍所有的Manager


    // 微信
    public static String WXappId="wx6993e67a6c7d8942";
    public static String WXappSecret="018922987fcf474e853a496d17ed9d35";

    // qq
    public static String QQappId="1105733775";
    public static String QQappKey="c7394704798a158208a74ab60104f0ba";

    public static MyApplication appContext;


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        UMShareAPI.get(this);
        // 初始化配置信息
        //Configure.init(this);
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



    /**
     * 验证微信用户是否绑定了手机号
     * @param context
     */
    public boolean checkMobile(final Context context) {
        User user = myUserBeanManager.getInstance();
        if (user != null) {
            if (!TextUtils.isEmpty(user.getOpenid()) && user.getIs_wechat().equals("0")) {
                SureOrCancelDialog dialog = new SureOrCancelDialog(context,
                        "提示",
                        "为了保障您的账号安全，请绑定手机号，绑定后可以直接用手机号登录",
                        "立即绑定",
                        "暂不绑定",
                        new SureOrCancelDialog.SureButtonClick() {
                            @Override
                            public void onSureButtonClick() {
                                Intent intent = new Intent(context, BindMobileActivity.class);
                                context.startActivity(intent);
                            }
                        });

                dialog.show();
                return false;
            }
        }

        return true;
    }



    /**
     * 验证用户是否完成了个人认证
     * @param context
     */
    public boolean checkCertifyPer(final Context context) {
        User user = myUserBeanManager.getInstance();
        if (user != null) {
            if (!user.getAuth_status().equals("2")) {
                SureOrCancelDialog dialog = new SureOrCancelDialog(context,
                        "提示",
                        "您尚未完成个人认证，暂时不能在该板块发布信息，请完成个人认证后再继续操作",
                        "确定",
                        "取消",
                        new SureOrCancelDialog.SureButtonClick() {
                            @Override
                            public void onSureButtonClick() {
                                Intent intent = new Intent(context, CertifyManageActivity.class);
                                context.startActivity(intent);
                            }
                        });

                dialog.show();
                return false;
            }
        }

        return true;
    }


    /**
     * 验证用户是否完成了司机认证
     * @param context
     */
    public boolean checkCertifyDriver(final Context context) {
        User user = myUserBeanManager.getInstance();
        if (user != null) {
            if (!user.getDriver_status().equals("2")) {
                SureOrCancelDialog dialog = new SureOrCancelDialog(context,
                        "提示",
                        "您尚未完成司机认证，是否前往认证？",
                        "是",
                        "否",
                        new SureOrCancelDialog.SureButtonClick() {
                            @Override
                            public void onSureButtonClick() {
                                Intent intent = new Intent(context, CertifyManageActivity.class);
                                context.startActivity(intent);
                            }
                        });

                dialog.show();
                return false;
            }
        }

        return true;
    }

}
