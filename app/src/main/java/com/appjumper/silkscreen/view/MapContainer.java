package com.appjumper.silkscreen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * 解决地图在主scrollview中滑动冲突的问题由于MapView被定义成final class，所以只能在容器中操作了
 * Created by Botx on 2017/11/10.
 */

public class MapContainer extends RelativeLayout {
    private ScrollView scrollView;
    public MapContainer(Context context) {
        super(context);
    }

    public MapContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            scrollView.requestDisallowInterceptTouchEvent(false);
        } else {
            scrollView.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
