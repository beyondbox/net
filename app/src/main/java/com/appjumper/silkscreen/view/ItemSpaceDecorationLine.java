package com.appjumper.silkscreen.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.util.DisplayUtil;

/**
 * RecyclerView的Item间隔
 * Created by Botx on 2017/3/29.
 */

public class ItemSpaceDecorationLine extends RecyclerView.ItemDecoration {
    private int leftSpaceDP;
    private int topSpaceDP;
    private int rightSpaceDP;
    private int bottomSpaceDP;

    private Context context;

    public ItemSpaceDecorationLine(int leftSpaceDP, int topSpaceDP, int rightSpaceDP, int bottomSpaceDP) {
        this.leftSpaceDP = leftSpaceDP;
        this.topSpaceDP = topSpaceDP;
        this.rightSpaceDP = rightSpaceDP;
        this.bottomSpaceDP = bottomSpaceDP;

        context = MyApplication.appContext;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = DisplayUtil.dip2px(context, leftSpaceDP);
        outRect.right = DisplayUtil.dip2px(context, rightSpaceDP);
        outRect.top = DisplayUtil.dip2px(context, topSpaceDP);
        outRect.bottom = DisplayUtil.dip2px(context, bottomSpaceDP);
    }
}
