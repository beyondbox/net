package com.appjumper.silkscreen.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.bean.UnRead;
import com.appjumper.silkscreen.bean.Update;
import com.appjumper.silkscreen.bean.UpdateResponse;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.DynamicFragment;
import com.appjumper.silkscreen.ui.home.HomeFragment;
import com.appjumper.silkscreen.ui.my.LoginActivity;
import com.appjumper.silkscreen.ui.my.MyFragment;
import com.appjumper.silkscreen.ui.shop.StockShopFragment;
import com.appjumper.silkscreen.util.Applibrary;
import com.appjumper.silkscreen.util.Configure;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.SPUtil;
import com.appjumper.silkscreen.util.morewindow.MoreWindow;
import com.appjumper.silkscreen.view.PopupRelease;
import com.appjumper.silkscreen.view.UpdateDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.QBadgeView;


/**
 * Created by yc on 2016/11/7.
 * 主界面
 */
public class MainActivity extends FragmentActivity {

    public static MainActivity instance = null;

    List<Fragment> mTab = new ArrayList<>();

    @Bind(R.id.rg_tab)
    public RadioGroup bottom_lly;

    @Bind(R.id.id_view_pager)
    public ViewPager idViewPager;

    @Bind(R.id.img_release)
    ImageView imgViRelease;

    @Bind(R.id.markTrend)
    TextView markTrend;
    @Bind(R.id.markDynamic)
    TextView markDynamic;

    private QBadgeView badgeTrend; //走势小红点
    private QBadgeView badgeDynamic; //动态小红点

    private MoreWindow mMoreWindow;
    private AMapLocationClient mLocationClient;

    private long lastClickTime = 0;
    private int pushType;

    private File downloadFile;
    private AsyncHttpClient downloadClient;
    private int widthAngel;

    private Dialog downloadDialog;
    private PopupRelease popupRelease;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        instance = this;

        setupViews();
        initUnread();

        //注册推送
        if (getUser() == null)
            XGPushManager.registerPush(getApplicationContext());
        else
            XGPushManager.registerPush(getApplicationContext(), getUser().getMobile());


        mLocationClient = new AMapLocationClient(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(new LocationListener());

        pushType = getIntent().getIntExtra(Const.KEY_TYPE, 0);
        if (pushType != 0) {
            switch (pushType) {

            }
        }


        //安卓6.0以后需要手动请求写入权限，才能在存储设备上创建文件夹, 还有定位权限的请求
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, Const.REQUEST_CODE_PERMISSION);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                popupRelease = new PopupRelease(MainActivity.this);
            }
        }, 300);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (instance == null)
            instance = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLocationClient.isStarted())
            mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }

    /**
     * 初始化未读小红点
     */
    private void initUnread() {
        badgeTrend = new QBadgeView(this);
        badgeDynamic = new QBadgeView(this);

        badgeTrend.bindTarget(markTrend).setBadgePadding(4.3f, true).setGravityOffset(12, 1, true);
        badgeDynamic.bindTarget(markDynamic).setBadgePadding(4.3f, true).setGravityOffset(13, 2, true);
    }


    /**
     * 定位监听
     */
    private class LocationListener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation.getErrorCode() == 0) {
                SPUtil.putString(null, "lat", aMapLocation.getLatitude() + "");
                SPUtil.putString(null, "lng", aMapLocation.getLongitude() + "");
            }
        }
    }


    /**
     * 版本更新
     */
    public void checkNewVersion() {
        new Thread(run).start();
    }

    private Runnable run = new Runnable() {

        @SuppressWarnings("unchecked")
        public void run() {
            UpdateResponse versionResponse = null;
            try {
                Thread.sleep(1000);
                versionResponse = JsonParser.getUpdateResponse(HttpUtil
                        .getMsg(Url.UPDATE));

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (versionResponse != null) {
                handler.sendMessage(handler.obtainMessage(
                        BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, versionResponse));
            } else {
                handler.sendEmptyMessage(1);
            }

        }
    };

    private MyHandler handler = new MyHandler(this);
    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public  MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            final MainActivity activity = (MainActivity) reference.get();
            switch (msg.what) {
                case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
                    UpdateResponse versionResponse = (UpdateResponse) msg.obj;
                    if(versionResponse == null) {
                        return;
                    }
                    if(versionResponse.isSuccess()){
                        Update data = versionResponse.getData();
                        if(data.getVersioncode()!=null&&!data.getVersioncode().equals("")){
                            double version = Double.parseDouble(data.getVersioncode());
                            if (version > activity.getVersionCode()) {
                                showUpdateDialog(data.getUrl(), data.getContent());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 版本更新对话框
     * @param url
     * @param content
     */
    public void showUpdateDialog(final String url, String content) {
        new UpdateDialog.Builder(this)
                .setMessage(content)
                .setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*Uri uri = Uri.parse(url);
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(it);*/
                        startDownload(url);
                    }
                }).create().show();
    }


    /**
     * 开始下载新版本
     * @param url
     */
    public void startDownload(String url) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "手机存储不可用，请检查存储状态", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * 弹出对话框
         */
        downloadDialog = new Dialog(this, R.style.CustomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_update_downloading, null);
        downloadDialog.setContentView(contentView);
        downloadDialog.setCancelable(false);
        downloadDialog.setCanceledOnTouchOutside(false);

        final LinearLayout llAngel = (LinearLayout) contentView.findViewById(R.id.llAngel);
        final ImageView imgViAngel = (ImageView) contentView.findViewById(R.id.imgViAngel);
        final ProgressBar proDownload = (ProgressBar) contentView.findViewById(R.id.proDownLoad);
        TextView txtCancel = (TextView) contentView.findViewById(R.id.txtCancel);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadClient.cancelRequests(instance, true);
                downloadDialog.dismiss();
                if (downloadFile != null && downloadFile.exists())
                    downloadFile.delete();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = downloadDialog.getWindow().getAttributes();
        params.width = (int) (display.getWidth() * 0.85);
        downloadDialog.getWindow().setAttributes(params);
        downloadDialog.show();

        downloadClient = new AsyncHttpClient();
        downloadFile = new File(Applibrary.APP_DIR, "siwangjia.apk");
        if (downloadFile.exists())
            downloadFile.delete();

        /**
         * 开始下载
         */
        downloadClient.get(instance, url, new FileAsyncHttpResponseHandler(downloadFile) {
            @Override
            public void onStart() {
                super.onStart();
                widthAngel = llAngel.getWidth();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                downloadDialog.dismiss();
                Toast.makeText(MainActivity.this, "网络超时，请稍候", Toast.LENGTH_SHORT).show();
                if (downloadFile != null && downloadFile.exists())
                    downloadFile.delete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    boolean b = getPackageManager().canRequestPackageInstalls();
                    LogHelper.e("install", String.valueOf(b));
                    if (b) {
                        installNewApk();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, Const.REQUEST_CODE_INSTALL_APK);
                    }
                } else {
                    installNewApk();
                }*/
                installNewApk();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int count = (int) (100 * (bytesWritten * 1.0 / totalSize));
                proDownload.setProgress(count);

                int angelX = (int) (widthAngel * (bytesWritten * 1.0 / totalSize));
                LinearLayout.LayoutParams layoutparam = (LinearLayout.LayoutParams) imgViAngel.getLayoutParams();
                layoutparam.leftMargin = angelX - (imgViAngel.getWidth() / 2);
                imgViAngel.setLayoutParams(layoutparam);
                imgViAngel.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * 安装新版本
     */
    private void installNewApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(instance, Const.FILE_PROVIDER, downloadFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(downloadFile);
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);

        downloadDialog.dismiss();
        finish();
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
            e.printStackTrace();
            return 0;
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
     * 如果登录了，返回true，否则返回false并去登录
     *
     * @return
     */
    public boolean checkLogined() {
        if ("".equals(getUser()) || getUser() == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            return false;
        } else {
            return true;
        }
    }



    @OnClick(R.id.img_release)
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.img_release:
                if(checkLogined()){
                    //showMoreWindow(v);
                    /*intent = new Intent(MainActivity.this, PlusActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.bottom_to_top, R.anim.no_change);*/

                    if (popupRelease != null)
                        popupRelease.show(imgViRelease);
                }
                break;
            default:
                break;
        }
    }

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this);
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view, dip(this,42));
    }

    public int dip(Context context, int pxValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxValue, context.getResources().getDisplayMetrics());
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        getUnRead();
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void setupViews() {
        bottom_lly.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int currIndex = 0;
                switch (checkedId) {
                    case R.id.rd_home:
                        currIndex = 0;
                        break;
                    case R.id.rd_trend:
                        currIndex = 1;
                        break;
                    case R.id.rd_dynamic:
                        currIndex = 2;
                        break;
                    case R.id.rd_my:
                        currIndex = 3;
                        break;
                }
                if(idViewPager!=null){
                    idViewPager.setCurrentItem(currIndex, false);
                }else{
                    idViewPager = (ViewPager)findViewById(R.id.id_view_pager);
                    idViewPager.setCurrentItem(currIndex, false);
                }
            }
        });

        mTab.add(new HomeFragment());
        mTab.add(new StockShopFragment());
        mTab.add(new DynamicFragment());
        mTab.add(new MyFragment());

        idViewPager.setOffscreenPageLimit(mTab.size() - 1);
        idViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        idViewPager.setCurrentItem(0);
        idViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    int currIndex = R.id.rd_home;
                    switch (currPosition) {
                        case 0:
                            currIndex = R.id.rd_home;
                            break;
                        case 1:
                            currIndex = R.id.rd_trend;
                            break;
                        case 2:
                            currIndex = R.id.rd_dynamic;
                            break;
                        case 3:
                            currIndex = R.id.rd_my;
                            break;
                    }
                    if(bottom_lly!=null){
                        bottom_lly.check(currIndex);
                    }else{
                        bottom_lly = (RadioGroup)findViewById(R.id.rg_tab);
                        bottom_lly.check(currIndex);
                    }
                }
            }
        });

    }


    public void selectpage(int position) {
        int currIndex = R.id.rd_home;
        switch (position) {
            case 0:
                currIndex = R.id.rd_home;
                break;
            case 1:
                currIndex = R.id.rd_trend;
                break;
            case 2:
                currIndex = R.id.rd_dynamic;
                break;
            case 3:
                currIndex = R.id.rd_my;
                break;
        }
        bottom_lly.check(currIndex);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mTab.get(position);
        }

        @Override
        public int getCount() {
            return mTab.size();
        }
    }


    /**
     * 获取消息未读数
     */
    private void getUnRead() {
        if (getUser() == null) {
            Intent intent = new Intent();
            intent.setAction(Const.ACTION_UNREAD_REFRESH);
            intent.putExtra(Const.KEY_OBJECT, new UnRead());
            sendBroadcast(intent);

            //badgeTrend.setBadgeNumber(0);
            badgeDynamic.setBadgeNumber(0);
            return;
        }

        RequestParams params = MyHttpClient.getApiParam("inquiry", "noReadNum");
        params.put("uid", getUser().getId());
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        UnRead unRead = new UnRead();
                        unRead.setReadNum(dataObj.getJSONArray("readNum").optInt(0));
                        unRead.setTenderNum(dataObj.getJSONArray("tenderNum").optInt(0));
                        unRead.setTenderSelectNum(dataObj.getJSONArray("tenderSelectNum").optInt(0));
                        unRead.setExpoNum(dataObj.getJSONArray("expoNum").optInt(0));
                        unRead.setNewsNum(dataObj.getJSONArray("newsNum").optInt(0));
                        unRead.setAnalysisNum(dataObj.getJSONArray("analysisNum").optInt(0));
                        unRead.setCollectionNum(dataObj.getJSONArray("collectionNum").optInt(0));

                        /*if (unRead.getAnalysisNum() < 3)
                            badgeTrend.setBadgeNumber(-1);
                        else
                            badgeTrend.setBadgeNumber(0);*/

                        if (unRead.getCollectionNum() > 0)
                            badgeDynamic.setBadgeNumber(-1);
                        else
                            badgeDynamic.setBadgeNumber(0);

                        Intent intent = new Intent();
                        intent.setAction(Const.ACTION_UNREAD_REFRESH);
                        intent.putExtra(Const.KEY_OBJECT, unRead);
                        sendBroadcast(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Const.REQUEST_CODE_PERMISSION:
                if (grantResults != null && grantResults.length > 0) {
                    boolean isSuccess = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (isSuccess) {
                        Configure.init(this);
                        checkNewVersion();
                    }
                    else {
                        Toast.makeText(this, "请开启存储权限，否则应用将无法正常使用！", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case Const.REQUEST_CODE_INSTALL_APK:
                if (grantResults != null && grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        installNewApk();
                    } else {
                        downloadDialog.dismiss();
                        finish();
                    }
                }
                break;
            default:
                break;
        }

        if (mLocationClient.isStarted())
            mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }



    @Override
    public void onBackPressed() {
        long currTime = System.currentTimeMillis();
        if ((currTime - lastClickTime) > 2000) {
            Toast.makeText(this, "再按一次退出丝网+", Toast.LENGTH_SHORT).show();
            lastClickTime = currTime;
        } else {
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        ButterKnife.unbind(this);
        mLocationClient.onDestroy();
    }
}

