package com.appjumper.silkscreen.ui.dynamic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.ui.dynamic.adapter.DynamicAdapter;
import com.appjumper.silkscreen.ui.my.LoginActivity;
import com.appjumper.silkscreen.util.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 动态
 * Created by Botx on 2017/3/21.
 */

public class DynamicFragment extends BaseFragment {

    @Bind(R.id.back)
    ImageView imgViBack;
    @Bind(R.id.title)
    TextView txtTitle;
    @Bind(R.id.right)
    TextView txtRight;
    @Bind(R.id.tabLayt)
    TabLayout tabLayt;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.llLogin)
    LinearLayout llLogin;

    private ViewPagerFragAdapter pagerAdapter;
    private List<Fragment> fragList;

    private String [] titleArr = {"产品", "物流", "找车", "设备", "厂房", "招聘"};

    private DynamicAdapter dynamicAdapter;
    private List<String> dynamicList;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        ButterKnife.bind(this, view);

        imgViBack.setVisibility(View.GONE);
        txtTitle.setText("动态");
        txtRight.setText("管理");
        return view;
    }


    @Override
    protected void initData() {
        if (getUser() == null) {
            llLogin.setVisibility(View.VISIBLE);
        } else {
            initViewPager();
            CommonApi.addLiveness(getUserID(), 15);
        }
    }


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_LOGIN_SUCCESS);
        getActivity().registerReceiver(myReceiver, filter);
    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_LOGIN_SUCCESS)) {
                if (isDataInited) {
                    llLogin.setVisibility(View.GONE);
                    initViewPager();
                    CommonApi.addLiveness(getUserID(), 15);
                }
            }
        }
    };


    private void initViewPager() {
        fragList = new ArrayList<>();
        fragList.add(new ProductFragment());
        fragList.add(new LogisticsFragment());
        fragList.add(new FindCarFragment());
        fragList.add(new DeviceFragment());
        fragList.add(new WorkShopFragment());
        fragList.add(new JobFragment());

        pagerAdapter = new ViewPagerFragAdapter(context.getSupportFragmentManager(), fragList, Arrays.asList(titleArr));
        viewPager.setOffscreenPageLimit(titleArr.length - 1);
        viewPager.setAdapter(pagerAdapter);

        tabLayt.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayt.setupWithViewPager(viewPager);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Const.RESULT_CODE_NEED_REFRESH) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
            broadcastManager.sendBroadcast(new Intent(Const.ACTION_DYNAMIC_REFRESH));
        }
    }



    @OnClick({R.id.right, R.id.txtLogin})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.right: //管理
                if (checkLogined()) {
                    intent = new Intent(context, DynamicManageActivity.class);
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.txtLogin: //立即登录
                intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
     }


    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(myReceiver);
    }
}
