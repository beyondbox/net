package com.appjumper.silkscreen.ui.common.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * ViewPager + Fragemnt组合的Adapter
 * Created by Botx on 2017/3/28.
 */

public class ViewPagerFragAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragList;
    private List<String> titleList;


    public ViewPagerFragAdapter(FragmentManager fm, List<Fragment> fragList, List<String> titleList) {
        super(fm);
        this.fragList = fragList;
        this.titleList = titleList;
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
        if (titleList != null && position < titleList.size()) {
            return titleList.get(position);
        } else {
            return "";
        }
    }
}
