package com.appjumper.silkscreen.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Botx on 2017/8/11.
 */

public class NoScrollViewPager extends ViewPager {

    private boolean isScrollable = false;

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isScrollable && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isScrollable && super.onInterceptTouchEvent(event);
    }

    public void setScrollable(boolean scrollable) {
        isScrollable = scrollable;
    }
}
