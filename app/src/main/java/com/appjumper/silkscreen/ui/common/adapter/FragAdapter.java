package com.appjumper.silkscreen.ui.common.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * fragment管理适配器
 * Created by Botx on 2017/10/17.
 */

public class FragAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragList;

    public FragAdapter(FragmentManager fm, List<Fragment> fragList) {
        super(fm);
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

}
