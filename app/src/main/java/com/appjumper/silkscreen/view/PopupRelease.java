package com.appjumper.silkscreen.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.home.logistics.ReleaseFreightActivity;
import com.appjumper.silkscreen.ui.my.enterprise.CertifyManageActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.util.LogHelper;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.width;

/**
 * 首页加号弹出菜单
 * Created by Botx on 2018/1/19.
 */

public class PopupRelease extends PopupWindow implements View.OnClickListener{

    private Activity context;
    private List<View> menuList;
    private float radius; //外围大圆形半径
    private float radiusInside; //里边小圆形半径
    private float centerX; //圆心的X坐标
    private float centerY; //圆心的Y坐标
    private int padding; //左右两端的边距
    private int itemWidth; //每个菜单的宽度

    private int [] iconArr = {R.mipmap.r_order, R.mipmap.r_process, R.mipmap.r_station, R.mipmap.r_workshop, R.mipmap.r_job, R.mipmap.r_device, R.mipmap.r_askbuy, R.mipmap.r_freight};
    private String [] nameArr = {"产品订做", "加工户", "物流线路", "厂房出租", "招聘", "机器出售", "求购", "空车配货"};

    private SureOrCancelDialog certifyPerDialog;
    private SureOrCancelDialog certifyComDialog;
    private SureOrCancelDialog comCreateDialog;



    public PopupRelease(Activity context) {
        this.context = context;
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        //设置布局文件
        RelativeLayout contentView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.popup_release, null);
        ImageView imgViClose = contentView.findViewById(R.id.imgViClose);
        imgViClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAnimation();
            }
        });

        setContentView(contentView);
        setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        //setBackgroundDrawable(new ColorDrawable(0x00000000));
        //setOutsideTouchable(true);
        //setFocusable(true);

        //计算半径
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        padding = DisplayUtil.dip2px(context, 10);
        itemWidth = DisplayUtil.dip2px(context, 55);
        radius = width / 2f - padding - itemWidth / 2;
        radiusInside = radius / 3;
        //计算圆心坐标
        centerX = width / 2f - itemWidth / 2;
        centerY = context.getWindow().getDecorView().getHeight() - DisplayUtil.dip2px(context, 65);

        //初始化菜单view
        menuList = new ArrayList<>();
        for (int i = 0; i < iconArr.length; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_release_menu, null);
            view.setTag(i);
            view.setOnClickListener(this);

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            TextView txtName = (TextView) view.findViewById(R.id.txtName);

            imageView.setImageResource(iconArr[i]);
            txtName.setText(nameArr[i]);
            menuList.add(view);
            contentView.addView(view);
        }

        initDialog();
    }


    /**
     * 弹出
     */
    public void show(View view) {
        showAnimation();
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }


    /**
     * 弹出动画
     */
    private void showAnimation() {
        LogHelper.e("centerX", centerX + "");
        LogHelper.e("centerY", centerY + "");

        menuList.get(0).measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.UNSPECIFIED);
        int height = menuList.get(0).getMeasuredHeight();

        LogHelper.e("itemWidth", width + "");
        LogHelper.e("itemHeight", height + "");

        for (int i = 0; i < menuList.size(); i++) {
            PointF point = new PointF();
            float angle;

            if (i == menuList.size() - 2 || i == menuList.size() - 1) {
                angle = i == menuList.size() - 2 ? 180 : 0;
                point.x = (float) (centerX + Math.cos(angle * (Math.PI / 180)) * radiusInside);
            } else {
                float avgAngle = 180f / 5;
                angle = 180 - avgAngle * i;
                point.x = (float) (centerX + Math.cos(angle * (Math.PI / 180)) * radius);
            }

            point.y = (float) (centerY - Math.sin(angle * (Math.PI / 180)) * radius - height);

            LogHelper.e(i + "X", point.x + "");
            LogHelper.e(i + "Y", point.y + "");

            ObjectAnimator animatorX = ObjectAnimator.ofFloat(menuList.get(i), "translationX", centerX, point.x);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(menuList.get(i), "translationY", centerY, point.y);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(250);
            animatorSet.play(animatorX).with(animatorY);
            animatorSet.start();
        }
    }


    /**
     * 关闭动画
     */
    private void closeAnimation() {
        LogHelper.e("centerX", centerX + "");
        LogHelper.e("centerY", centerY + "");

        menuList.get(0).measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.UNSPECIFIED);
        int height = menuList.get(0).getMeasuredHeight();

        LogHelper.e("itemWidth", width + "");
        LogHelper.e("itemHeight", height + "");

        for (int i = 0; i < menuList.size(); i++) {
            PointF point = new PointF();
            float angle;

            if (i == menuList.size() - 2 || i == menuList.size() - 1) {
                angle = i == menuList.size() - 2 ? 180 : 0;
                point.x = (float) (centerX + Math.cos(angle * (Math.PI / 180)) * radiusInside);
            } else {
                float avgAngle = 180f / 5;
                angle = 180 - avgAngle * i;
                point.x = (float) (centerX + Math.cos(angle * (Math.PI / 180)) * radius);
            }

            point.y = (float) (centerY - Math.sin(angle * (Math.PI / 180)) * radius - height);

            LogHelper.e(i + "X", point.x + "");
            LogHelper.e(i + "Y", point.y + "");

            ObjectAnimator animatorX = ObjectAnimator.ofFloat(menuList.get(i), "translationX", point.x, centerX);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(menuList.get(i), "translationY", point.y, centerY);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(250);
            animatorSet.play(animatorX).with(animatorY);
            animatorSet.start();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 250);
    }


    @Override
    public void onClick(View view) {
        closeAnimation();
        if (!MyApplication.appContext.checkMobile(context)) return;
        int i = (int) view.getTag();

        switch (i) {
            case 0:
                if (getUser().getEnterprise() == null) {
                    comCreateDialog.show();
                    return;
                }
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_ORDER);
                break;
            case 1:
                if (getUser().getEnterprise() == null) {
                    comCreateDialog.show();
                    return;
                }
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_PROCESS);
                break;
            case 2:
                if (getUser().getEnterprise() == null) {
                    comCreateDialog.show();
                    return;
                }
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_LOGISTICS);
                break;
            case 3:
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_WORKSHOP);
                break;
            case 4:
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_JOB);
                break;
            case 5:
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_DEVICE);
                break;
            case 6:
                Intent intent = new Intent(context, ProductSelectActivity.class);
                intent.putExtra(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_STOCK);
                intent.putExtra(Const.KEY_MOTION, ProductSelectActivity.MOTION_RELEASE_ASKBUY);
                context.startActivity(intent);
                break;
            case 7:
                if (!MyApplication.appContext.checkCertifyPer(context)) return;
                start_Activity(context, ReleaseFreightActivity.class);
                break;
        }
    }


    /**
     * 初始化对话框
     */
    private void initDialog() {
        certifyPerDialog = new SureOrCancelDialog(context, "提示", "您尚未完成个人认证，暂时不能在该板块发布信息，请完成个人认证后再继续操作", "确定", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        start_Activity(context, CertifyManageActivity.class);
                    }
                });

        certifyComDialog = new SureOrCancelDialog(context, "提示", "您尚未完成企业认证，暂时不能在该板块发布信息，请完成企业认证后再继续操作", "确定", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        start_Activity(context, CertifyManageActivity.class);
                    }
                });

        comCreateDialog = new SureOrCancelDialog(context, "提示", "您尚未完善企业信息，暂时不能在该板块发布信息，请完善企业信息后再继续操作", "确定", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    }
                });
    }


    /**
     * 打开Activity
     *
     * @param activity
     * @param cls
     * @param name
     */
    public static void start_Activity(Activity activity, Class<?> cls, BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        if (name != null)
            for (int i = 0; i < name.length; i++) {
                intent.putExtra(name[i].getName(), name[i].getValue());
            }
        activity.startActivity(intent);
    }


    private User getUser() {
        return MyApplication.appContext.getMyUserManager().getInstance();
    }

    public String getUserID() {
        User instanceUser = getUser();
        return instanceUser == null ? "" : instanceUser.getId();
    }

}
