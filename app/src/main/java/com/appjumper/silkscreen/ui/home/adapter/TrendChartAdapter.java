package com.appjumper.silkscreen.ui.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appjumper.silkscreen.bean.MaterProduct;

import java.util.List;

/**
 * 走势图Adapter
 * Created by Botx on 2017/3/24.
 */

public class TrendChartAdapter extends FragmentStatePagerAdapter {

    private List<MaterProduct> productList;
    private List<Fragment> fragList;


    public TrendChartAdapter(FragmentManager fm, List<MaterProduct> productList, List<Fragment> fragList) {
        super(fm);
        this.productList = productList;
        this.fragList = fragList;
    }


    @Override
    public Fragment getItem(int position) {
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return productList.get(position).getName();
    }
}
