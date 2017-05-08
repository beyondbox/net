/*
 *
 *  *
 *  *  *
 *  *  *  * ===================================
 *  *  *  * Copyright (c) 2016.
 *  *  *  * 作者：安卓猴
 *  *  *  * 微博：@安卓猴
 *  *  *  * 博客：http://sunjiajia.com
 *  *  *  * Github：https://github.com/opengit
 *  *  *  *
 *  *  *  * 注意**：如果您使用或者修改该代码，请务必保留此版权信息。
 *  *  *  * ===================================
 *  *  *
 *  *  *
 *  *
 *
 */

package com.appjumper.silkscreen.ui.my.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appjumper.silkscreen.bean.ServiceMyList;

import java.util.List;

/**
 * Created by Monkey on 2015/6/29.
 */
public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

  private List<ServiceMyList> mTitles;
  private List<Fragment> mFragments;

  public MyViewPagerAdapter(FragmentManager fm, List<ServiceMyList> mTitles, List<Fragment> mFragments) {
    super(fm);
    this.mTitles = mTitles;
    this.mFragments = mFragments;
  }

  public void setData( List<ServiceMyList> mTitles, List<Fragment> mFragments) {
    this.mTitles = mTitles;
    this.mFragments = mFragments;
  }

  @Override public CharSequence getPageTitle(int position) {
    return mTitles.get(position).getName();
  }

  @Override public Fragment getItem(int position) {
    return mFragments.get(position);
  }

  @Override public int getCount() {
    return mFragments.size();
  }
  @Override
  public int getItemPosition(Object object) {
    return POSITION_NONE;
  }
}
