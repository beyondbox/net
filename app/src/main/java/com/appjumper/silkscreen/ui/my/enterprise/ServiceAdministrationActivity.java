package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.ServiceMyList;
import com.appjumper.silkscreen.bean.ServiceMyListResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.my.adapter.MyViewPagerAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-21.
 * 服务管理
 */
public class ServiceAdministrationActivity extends BaseActivity {
    @Bind(R.id.id_appbarlayout)
    AppBarLayout mAppBarLayout;

    //    @Bind(R.id.id_tablayout)
    TabLayout mTabLayout;

    //    @Bind(R.id.id_viewpager)
    ViewPager mViewPager;

    //    @Bind(R.id.ll_null)//没有服务时的显示
    LinearLayout ll_null;

    //    @Bind(R.id.ll_have)//有服务时的显示
    LinearLayout ll_have;

    private ArrayList<Fragment> mFragments;
    private List<ServiceMyList> mTitles = new ArrayList<>() ;
    private MyViewPagerAdapter mViewPagerAdapter;
    private int isRefresh=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_administration);
        initBack();
        ButterKnife.bind(this);
        initTitle("服务管理");
        initRightButton("添加", new RightButtonListener() {
            @Override
            public void click() {
                start_Activity(ServiceAdministrationActivity.this, AddServiceActivity.class);
            }
        });

//        new Thread(servicelistRun).start();


    }

    @Override
    protected void onResume() {
        initProgressDialog();
        progress.show();
        new Thread(servicelistRun).start();
        super.onResume();
    }

    @OnClick({R.id.tv_add})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add://立即添加
                start_Activity(this, AddServiceActivity.class);
                break;
            default:
                break;
        }
    }

    //获取标签
    private Runnable servicelistRun = new Runnable() {
        private ServiceMyListResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                response = JsonParser.getServiceMyListResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.SERVICEMYLIST));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {

        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            ServiceAdministrationActivity activity = (ServiceAdministrationActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://服务列表标签
                    progress.dismiss();
                    ServiceMyListResponse listResponse = (ServiceMyListResponse) msg.obj;
                    ll_null  = (LinearLayout)findViewById(R.id.ll_null);
                    ll_have  = (LinearLayout)findViewById(R.id.ll_have);
                    if (listResponse.isSuccess()) {
                        List<ServiceMyList> mTitle = listResponse.getData();
                        if (mTitle.size() > 0) {
                            ll_null.setVisibility(View.GONE);
                            ll_have.setVisibility(View.VISIBLE);
                            if(mTitles!=null){
                                mTitles.clear();
                            }
                            mTitles.addAll(mTitle);
                            initData();
                            if(isRefresh==0){
                                configViews();
                            }
                            mViewPagerAdapter.notifyDataSetChanged();
                        } else {
                            ll_null.setVisibility(View.VISIBLE);
                            ll_have.setVisibility(View.GONE);
                        }
                    } else {
                        showErrorToast(listResponse.getError_desc());
                    }

                    break;
                case NETWORK_FAIL:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
            }
        }
    }

    private void initData() {
        //初始化填充到ViewPager中的Fragment集合
        if (mFragments == null) {
            mFragments = new ArrayList<>();
        } else {
            mFragments.clear();
        }
        for (int i = 0; i < mTitles.size(); i++) {
            Bundle mBundle = new Bundle();
            mBundle.putString("type", mTitles.get(i).getId());
            if (mTitles.get(i).getId().equals("4")) {
                ServiceLogisticsFragment mFragment = new ServiceLogisticsFragment();
                mFragment.setArguments(mBundle);
                mFragments.add(i, mFragment);
            } else {
                ServiceFragment mFragment = new ServiceFragment();
                mFragment.setArguments(mBundle);
                mFragments.add(i, mFragment);
            }
        }
    }

    private void configViews() {
        isRefresh=1;
        mViewPager = (ViewPager)findViewById(R.id.id_viewpager);
        mTabLayout = (TabLayout)findViewById(R.id.id_tablayout);
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        if (mTitles.size() >= 2) {
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }
}
