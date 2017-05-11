package com.appjumper.silkscreen.ui.trend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/11/7.
 * 走势
 */
public class TrendFragment extends BaseFragment {
    @Bind(R.id.rg_tab)
    RadioGroup rgTab;
    List<Fragment> mTab = new ArrayList<>();
    @Bind(R.id.id_view_pager)
    public ViewPager idViewPager;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_trend, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        setupViews();
    }


    private void setupViews() {
        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int currIndex = 0;
                switch (checkedId) {
                    case R.id.rd_material://原材价格
                        currIndex = 0;
                        break;
                    case R.id.rd_futures://期货走势
                        currIndex = 1;
                        break;
                    case R.id.rd_market://大盘走势
                        currIndex = 2;
                        break;
                }
                idViewPager.setCurrentItem(currIndex, false);
            }
        });
        mTab.add(new MaterialFragment());
        mTab.add(new FuturesFragment());
        mTab.add(new MarketFragment());
        idViewPager.setOffscreenPageLimit(mTab.size() - 1);
        idViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
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
                            currIndex = R.id.rd_material;
                            break;
                        case 1:
                            currIndex = R.id.rd_futures;
                            break;
                        case 2:
                            currIndex = R.id.rd_market;
                            break;
                    }
                    rgTab.check(currIndex);
                }
            }
        });

    }

    public void selectpage(int position) {
        int currIndex = R.id.rd_material;
        switch (position) {
            case 0:
                currIndex = R.id.rd_material;
                break;
            case 1:
                currIndex = R.id.rd_futures;
                break;
            case 2:
                currIndex = R.id.rd_market;
                break;
        }
        rgTab.check(currIndex);
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
}
