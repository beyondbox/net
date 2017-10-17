package com.appjumper.silkscreen.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.ui.my.LoginActivity;
import com.appjumper.silkscreen.util.AppToast;
import com.appjumper.silkscreen.util.MProgressDialog;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.message.BasicNameValuePair;

import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/6/29.
 */
public class BaseActivity extends FragmentActivity {
    public final static int NETWORK_SUCCESS_DATA_RIGHT = 0x01;
    public final static int NETWORK_SUCCESS_PAGER_RIGHT = 0x02;
    public final static int NETWORK_OTHER = 0x19;
    public final static int NETWORK_SUCCESS_DATA_ERROR = 0x06;
    public final static int NETWORK_FAIL = 0x05;
    public MProgressDialog progress;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mlocationClient;
    public double latitude;
    public double longitude;
    public float accuracy;

    protected FragmentActivity context;

    public void initLocation() {
        mlocationClient = new AMapLocationClient(this);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();
//设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        latitude = aMapLocation.getLatitude();//获取纬度
                        longitude = aMapLocation.getLongitude();//获取经度
                        accuracy = aMapLocation.getAccuracy();//获取精度信息

                        mlocationClient.stopLocation();
                    }
                }

            }
        });

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mlocationClient!=null){
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
            mlocationClient = null;
        }

        ButterKnife.unbind(this);
    }

    /**
     * 打开Activity
     *
     * @param activity
     * @param cls
     * @param name
     */
    public static void start_Activity(Activity activity, Class<?> cls,
                                      BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        if (name != null)
            for (int i = 0; i < name.length; i++) {
                intent.putExtra(name[i].getName(), name[i].getValue());
            }
        activity.startActivity(intent);
        //activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 打开ForResultActivity
     *
     * @param activity
     * @param cls
     * @param name
     */
    public static void startForResult_Activity(Activity activity, Class<?> cls, int requestCode,
                                               BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        if (name != null)
            for (int i = 0; i < name.length; i++) {
                intent.putExtra(name[i].getName(), name[i].getValue());
            }
        activity.startActivityForResult(intent, requestCode);
        //activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    // 获取当前版本号
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);

            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    // 获取当前版本号名称
    public String getVersionName() {
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);

            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "1.0";
        }
    }



    //返回按钮
    public void initBack() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //初始化标题
    public void initTitle(String title) {
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(title);
    }

    //初始右键
    public void initRightButton(String btnName, final RightButtonListener listener) {
        TextView tv = (TextView) findViewById(R.id.right);
        tv.setText(btnName);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.click();
            }
        });
    }

    public void initRightButton(int resId, final RightButtonListener listener) {
        ImageView iv = (ImageView) findViewById(R.id.right);
        iv.setImageResource(resId);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.click();
            }
        });
    }



    //回调接口
    public interface RightButtonListener {
        void click();
    }

    /**
     * 不带文字转转框
     */
    public void initProgressDialog() {
        initProgressDialog(true, null);
    }

    /**
     * 带文字转转框
     *
     * @param cancel
     * @param message
     */
    public void initProgressDialog(boolean cancel, String message) {
        progress = new MProgressDialog(this, cancel, message);
        //initProgressDialog(this, cancel, message);
    }

    public void initProgressDialog(Context mContext, boolean cancel,
                                   String message) {
        progress = new MProgressDialog(mContext, cancel, message);
//        progress.setMessage(message);
    }

    /**
     * 关闭软键盘
     */
    public void hideKeyboard(View v) {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 关闭软键盘
     */
    public void hideKeyboard() {
        hideKeyboard(getWindow().getDecorView());
    }


    /**
     * 如果登录了，返回true，否则返回false并去登录
     *
     * @return
     */
    public boolean checkLogined() {
        if ("".equals(getUserID()) || getUserID() == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            return false;
        } else {
            return true;
        }
    }


    /**
     * 获取Application
     *
     * @return
     */
    public MyApplication getMyApplication() {
        return (MyApplication) getApplication();
    }

    /**
     * 获取个人资料
     *
     * @return
     */
    public User getUser() {
        User instanceUser = getMyApplication()
                .getMyUserManager().getInstance();
        return instanceUser;
    }

    /**
     * 获取userId
     *
     * @return
     */
    public String getUserID() {
        User instanceUser = getUser();
        return instanceUser == null ? "" : instanceUser.getId();
    }

    /**
     * 网络连接失败提示
     */
    public void showErrorToast() {
        showFailTips("无法连接到网络\n请稍后再试");
    }

    /**
     * 自定义提示
     *
     * @param content
     */
    public void showFailTips(String content) {
        AppToast tipsToast = AppToast.makeText(getApplication().getBaseContext(), content, AppToast.LENGTH_SHORT);
        tipsToast.show();
    }

    /**
     * 提交成功提示
     *
     * @param content
     */
    public void showSuccessTips(String content) {
        AppToast tipsToast = AppToast.makeText(getApplication().getBaseContext(), content, AppToast.LENGTH_SHORT);
        tipsToast.setIcon(R.mipmap.tips_success);
        tipsToast.show();
    }


    /**
     * 普通toast
     *
     * @param err
     */
    public void showErrorToast(String err) {
        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
    }

    public void setPullLvHeight(ListView pull) {
        int totalHeight = 0;
        ListAdapter adapter = pull.getAdapter();
        for (int i = 0, len = adapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = adapter.getView(i, null, pull);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = pull.getLayoutParams();
        params.height = totalHeight + (pull.getDividerHeight() * (pull.getCount() - 1));
        pull.setLayoutParams(params);
    }


    /**
     * @param context
     * @param pxValue
     * @return
     */
    public int dip(Context context, int pxValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxValue, context.getResources().getDisplayMetrics());
    }

    /**
     * 点击屏幕其他区域关闭软键盘
     */
    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }*/

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }



}
