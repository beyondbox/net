package com.appjumper.silkscreen.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.util.manager.MyUserManager;
import com.appjumper.silkscreen.view.SelectPicPopupWindow;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by yc on 2016/12/10.
 * 分享
 */
public  class ShareUtil {
    private static Activity contexts;
    private static String imgs="";

    public static  void intShare(Activity context, View view, final String subTitle, final String title, final String url){
        contexts = context;
        init(context,view,subTitle,title,url);
    }

    public static  void intShare2(Activity context, View view, final String subTitle, final String title, final String url,String img){
        contexts = context;
        imgs = img;
        init(context,view,subTitle,title,url);
    }

    public static void init(Activity context, View view, final String subTitle, final String title, final String url){
        //实例化SelectPicPopupWindow
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View mMenuView = inflater.inflate(R.layout.layout_share_dialog, null);
        TextView qq_btn = (TextView) mMenuView.findViewById(R.id.qq_btn);
        TextView qqspace_btn = (TextView) mMenuView.findViewById(R.id.qqspace_btn);
        qq_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShare(subTitle,title,url, SHARE_MEDIA.QQ);
            }
        });
        qqspace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShare(subTitle,title,url, SHARE_MEDIA.QZONE);
            }
        });
        TextView wx_btn = (TextView) mMenuView.findViewById(R.id.wx_btn);
        wx_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShare(subTitle,title,url, SHARE_MEDIA.WEIXIN);
            }
        });
        TextView wxpyq_btn = (TextView) mMenuView.findViewById(R.id.wxpyq_btn);
        wxpyq_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShare(subTitle,title,url, SHARE_MEDIA.WEIXIN_CIRCLE);
            }
        });

        final SelectPicPopupWindow menuWindow = new SelectPicPopupWindow(context, mMenuView);
        menuWindow.setAnimationStyle(R.style.BottomPopupAnimation);
        backgroundAlpha(50f);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        menuWindow.setBackgroundDrawable(dw);
        menuWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

        menuWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        menuWindow.setOnDismissListener(new poponDismissListener());
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        menuWindow.dismiss();
                    }
                }
                return true;
            }
        });
        mMenuView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow.dismiss();
            }
        });
        menuWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        menuWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //显示窗口
        menuWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }



    static class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            backgroundAlpha(1f);
        }
    }
    public static void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = contexts.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        contexts.getWindow().setAttributes(lp);
    }

    public static void getShare(final String content, final String title,  String url,SHARE_MEDIA platform) {
        UMImage image;
        if(imgs.equals("")){
            Bitmap bitmap = BitmapFactory.decodeResource(contexts.getResources(), R.mipmap.icon_logoshare);
            image = new UMImage(contexts, bitmap);
        }else{
            image = new UMImage(contexts,imgs);
        }
        new ShareAction(contexts).
                setPlatform(platform).
                setCallback(new UMShareListener() {

                    @Override
                    public void onResult(SHARE_MEDIA arg0) {
                    }

                    @Override
                    public void onError(SHARE_MEDIA arg0, Throwable arg1) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {

                    }
                }).
                withTitle(title).
                withText(content).
                withTargetUrl(url).
                withMedia(image).
                share();

        CommonApi.addLiveness(new MyUserManager(MyApplication.appContext).getUserId(), 17);
    }
}
