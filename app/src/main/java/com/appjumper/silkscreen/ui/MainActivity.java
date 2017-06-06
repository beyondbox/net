package com.appjumper.silkscreen.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.appjumper.silkscreen.ui.trend.TrendFragment;
import com.appjumper.silkscreen.util.Configure;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.morewindow.MoreWindow;
import com.appjumper.silkscreen.view.SureOrCancelVersionDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.QBadgeView;


/**
 * Created by yc on 2016/11/7.
 * 首页
 */
public class MainActivity extends FragmentActivity {
    List<Fragment> mTab = new ArrayList<>();

    @Bind(R.id.rg_tab)
    public RadioGroup bottom_lly;

    @Bind(R.id.id_view_pager)
    public ViewPager idViewPager;

    @Bind(R.id.markTrend)
    TextView markTrend;
    @Bind(R.id.markDynamic)
    TextView markDynamic;

    private QBadgeView badgeTrend; //走势小红点
    private QBadgeView badgeDynamic; //动态小红点

    private MoreWindow mMoreWindow;

    private long lastClickTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //安卓6.0以后需要手动请求写入权限，才能在存储设备上创建文件夹
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.REQUEST_CODE_PERMISSION);

        setupViews();
        checkNewVersion();

        initUnread();
    }


    /**
     * 初始化未读小红点
     */
    private void initUnread() {
        badgeTrend = new QBadgeView(this);
        badgeDynamic = new QBadgeView(this);

        badgeTrend.bindTarget(markTrend).setGravityOffset(11, 1, true);
        badgeDynamic.bindTarget(markDynamic).setGravityOffset(12, 1, true);
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
    private static class MyHandler extends Handler {
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
                                activity.downLoadNewVersion(data.getUrl(),data.getContent());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    // 跳转下载新版本界面
    public void downLoadNewVersion(final String url, String content) {
        SureOrCancelVersionDialog followDialog = new SureOrCancelVersionDialog(this, content, new SureOrCancelVersionDialog.SureButtonClick() {
            @Override
            public void onSureButtonClick() {
                Uri uri = Uri.parse(url);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });
        followDialog.show();
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
                    intent = new Intent(MainActivity.this, PlusActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.bottom_to_top, R.anim.no_change);
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
        mTab.add(new TrendFragment());
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

    private class ViewPagerAdapter extends FragmentPagerAdapter {

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

            badgeTrend.setBadgeNumber(0);
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
                        unRead.setExpoNum(dataObj.getJSONArray("expoNum").optInt(0));
                        unRead.setNewsNum(dataObj.getJSONArray("newsNum").optInt(0));
                        unRead.setAnalysisNum(dataObj.getJSONArray("analysisNum").optInt(0));
                        unRead.setCollectionNum(dataObj.getJSONArray("collectionNum").optInt(0));

                        if (unRead.getAnalysisNum() > 0)
                            badgeTrend.setBadgeNumber(-1);
                        else
                            badgeTrend.setBadgeNumber(0);

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
                    if (isSuccess)
                        Configure.init(this);
                    else
                        Toast.makeText(this, "请开启存储权限，否则将无法使用上传图片功能！", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
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

}

