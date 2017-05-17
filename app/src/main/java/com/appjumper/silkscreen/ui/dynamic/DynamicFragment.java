package com.appjumper.silkscreen.ui.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.ui.dynamic.adapter.DynamicAdapter;

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

    private ViewPagerFragAdapter pagerAdapter;
    private List<Fragment> fragList;

    private String [] titleArr = {"产品", "物流", "找车的货物", "设备", "出租厂房", "招聘"};

    private DynamicAdapter dynamicAdapter;
    private List<String> dynamicList;



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
        initViewPager();

        CommonApi.addLiveness(getUserID(), 15);
    }



    private void initViewPager() {
        fragList = new ArrayList<>();
        for (int i = 0; i < titleArr.length; i++) {
            fragList.add(new ProductFragment());
        }

        pagerAdapter = new ViewPagerFragAdapter(context.getSupportFragmentManager(), fragList, Arrays.asList(titleArr));
        viewPager.setOffscreenPageLimit(titleArr.length - 1);
        viewPager.setAdapter(pagerAdapter);

        tabLayt.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayt.setupWithViewPager(viewPager);
    }


    @OnClick({R.id.right})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.right: //管理
                if (checkLogined()) {
                    intent = new Intent(context, DynamicManageActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
     }

}
