package com.appjumper.silkscreen.ui.trend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.MaterProduct;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.ui.trend.adapter.MyViewPagerAdapter;
import com.appjumper.silkscreen.ui.trend.cmaterialyreclerView.ChannelAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.db.DBManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 走势
 * Created by Botx on 2018/1/17.
 */

public class TrendActivity extends BaseActivity {

    @Bind(R.id.id_appbarlayout)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.id_tablayout)
    TabLayout mTabLayout;

    @Bind(R.id.iv_add_service)
    ImageView iv_add_service;

    @Bind(R.id.id_viewpager)
    ViewPager mViewPager;

    @Bind(R.id.l_add)
    LinearLayout l_add;

    @Bind(R.id.l_dynamic)
    LinearLayout l_dynamic;

    @Bind(R.id.rv_mater)
    RecyclerView rv_mater;

    private ArrayList<Fragment> mFragments;
    private MyViewPagerAdapter mViewPagerAdapter;
    private DBManager dbHelper;
    private List<MaterProduct> mylist;
    private List<MaterProduct> otherlist;
    private ChannelAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);
        ButterKnife.bind(context);

        initTitle("走势");
        initBack();
        registerBroadcastReceiver();

        dbHelper = new DBManager(context);
        mylist = dbHelper.query();

        //new Thread(new AllproductRun()).start();
        l_add.setVisibility(View.GONE);
        l_dynamic.setVisibility(View.VISIBLE);
        initFragData();
        configViews();

        CommonApi.addLiveness(getUserID(), 10);
    }


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_ATTENTION_MATER_REFRESH);
        filter.addAction(Const.ACTION_CHART_DETAIL);
        registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_ATTENTION_MATER_REFRESH)) {
                mylist.clear();
                mylist.addAll(dbHelper.query());

                initFragData();
                mViewPagerAdapter.setData(mylist, mFragments);
                mViewPagerAdapter.notifyDataSetChanged();
                mViewPager.setOffscreenPageLimit(mylist.size() - 1);
            } else if (action.equals(Const.ACTION_CHART_DETAIL)) {
                String type = intent.getStringExtra("type");
                if (!TextUtils.isEmpty(type))
                    changePage(type);
            }
        }
    };


    /**
     * 根据产品ID跳转到对应位置
     * @param type
     */
    private void changePage(String type) {
        for (int i = 0; i < mylist.size(); i++) {
            MaterProduct product = mylist.get(i);
            if (product.getId().equals(type)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null)
            dbHelper.closeDB();
        unregisterReceiver(myReceiver);
    }


    @OnClick({R.id.iv_add_service,R.id.tv_add})
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_add_service://添加材料
                //l_add.setVisibility(View.VISIBLE);
                //l_dynamic.setVisibility(View.GONE);
                intent = new Intent(context, AttentionManageActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_add://添加
                l_add.setVisibility(View.GONE);
                l_dynamic.setVisibility(View.VISIBLE);
                List<MaterProduct> myChanne = adapter.getmMyChannelItems();
                dbHelper.deleteProduct();
                dbHelper.addProduct(myChanne);
                mylist.clear();
                mylist.addAll(dbHelper.query());

                initFragData();
                mViewPagerAdapter.setData(mylist, mFragments);
                mViewPagerAdapter.notifyDataSetChanged();
                mViewPager.setOffscreenPageLimit(mylist.size() - 1);
                break;

        }
    }


    private void initFragData() {
        //初始化填充到ViewPager中的Fragment集合
        if(mFragments==null){
            mFragments = new ArrayList<>();
        }else{
            mFragments.clear();
        }
        for (int i = 0; i < mylist.size(); i++) {
            Bundle mBundle = new Bundle();
            mBundle.putString("type", mylist.get(i).getId());
            DetailsFragment mFragment = new DetailsFragment();
            mFragment.setArguments(mBundle);
            mFragments.add(i, mFragment);
        }
    }


    private void configViews() {
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), mylist, mFragments);
        mViewPager.setOffscreenPageLimit(mylist.size() - 1);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
    }

}
