package com.appjumper.silkscreen.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.util.DisplayUtil;

/**
 * RecyclerView, Grid布局的Item间隔
 * Created by Botx on 2017/4/5.
 */

public class ItemSpaceDecorationGrid extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int horizontalSpace;
    private int verticalSpace;
    private Context context;

    public ItemSpaceDecorationGrid(int spanCount, int horizontalSpace, int verticalSpace) {
        this.spanCount = spanCount;
        this.horizontalSpace = horizontalSpace;
        this.verticalSpace = verticalSpace;
        context = MyApplication.appContext;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        /*if (position % spanCount == 0) {
            outRect.left = 0;
        } else {
            outRect.left = DisplayUtil.dip2px(context, horizontalSpace - column * horizontalSpace / spanCount);
        }

        if (position % spanCount == spanCount - 1) {
            outRect.right = 0;
        } else {
            outRect.right = DisplayUtil.dip2px(context, (column + 1) * horizontalSpace / spanCount);
        }*/

        outRect.left = DisplayUtil.dip2px(context, horizontalSpace - column * horizontalSpace / spanCount);
        outRect.right = DisplayUtil.dip2px(context, (column + 1) * horizontalSpace / spanCount);

        /*if (position < spanCount) { // top edge
            outRect.top = DisplayUtil.dip2px(context, verticalSpace);
        }*/
        outRect.bottom = DisplayUtil.dip2px(context, verticalSpace);
    }
}
