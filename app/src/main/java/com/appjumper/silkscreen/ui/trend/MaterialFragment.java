package com.appjumper.silkscreen.ui.trend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.MaterProduct;
import com.appjumper.silkscreen.bean.MaterProductResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.trend.adapter.MyViewPagerAdapter;
import com.appjumper.silkscreen.ui.trend.cmaterialyreclerView.ChannelAdapter;
import com.appjumper.silkscreen.ui.trend.cmaterialyreclerView.ItemDragHelperCallback;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.db.DBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/7.
 * 原材价格
 */
public class MaterialFragment extends BaseFragment {
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_material, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        registerBroadcastReceiver();
        dbHelper = new DBManager(getContext());
        mylist = dbHelper.query();

        new Thread(new AllproductRun()).start();
        l_add.setVisibility(View.GONE);
        l_dynamic.setVisibility(View.VISIBLE);
        initFragData();
        configViews();

        CommonApi.addLiveness(getUserID(), 10);
    }



    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_ATTENTION_MATER_REFRESH);
        context.registerReceiver(myReceiver, filter);
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
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.closeDB();
        context.unregisterReceiver(myReceiver);
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

    //所有产品
    private class AllproductRun implements Runnable {
        private MaterProductResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                response = JsonParser.getMaterProductResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.PRICEALLPROFUVT));
            } catch (Exception e) {
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

    private MyHandler handler = new MyHandler();
    private  class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    MaterProductResponse detailsResponse = (MaterProductResponse) msg.obj;
                    if(detailsResponse.isSuccess()){
                        otherlist  = detailsResponse.getData();
                        init();
                    }else{
                        showErrorToast(detailsResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    //showErrorToast("数据返回错误");
                    showErrorToast();
                    break;
                default:
                    showErrorToast();
                    break;
            }
        }
    };

    private void init(){
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        rv_mater.setLayoutManager(manager);
        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv_mater);

        otherlist.removeAll(mylist);
        adapter = new ChannelAdapter(getActivity(), helper, mylist, otherlist, ChannelAdapter.SlideStyle.BOTH);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == ChannelAdapter.TYPE_MY || viewType == ChannelAdapter.TYPE_OTHER ? 1 : 3;
            }
        });
        rv_mater.setAdapter(adapter);
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
        mViewPagerAdapter = new MyViewPagerAdapter(getChildFragmentManager(), mylist, mFragments);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        mViewPager.setOffscreenPageLimit(mylist.size() - 1);

//        // 初始化ViewPager的适配器，并设置给它
//        mViewPagerAdapter = new MyViewPagerAdapter(getChildFragmentManager(), mylist, mFragments);
//        mViewPager.setAdapter(mViewPagerAdapter);
//        // 设置ViewPager最大缓存的页面个数
////        mViewPager.setOffscreenPageLimit(mylist.size());
//        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        // 将TabLayout和ViewPager进行关联，让两者联动起来
//        mTabLayout.setupWithViewPager(mViewPager);
//        // 设置Tablayout的Tab显示ViewPager的适配器中的getPageTitle函数获取到的标题
//        mTabLayout.setTabsFromPagerAdapter(mViewPagerAdapter);

    }

}
