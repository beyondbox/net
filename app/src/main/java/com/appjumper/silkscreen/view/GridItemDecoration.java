package com.appjumper.silkscreen.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.util.DisplayUtil;

/**
 * RecyclerView, Grid布局的Item间隔
 * Created by Botx on 2017/4/5.
 */

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int horizontalSpace;
    private int verticalSpace;
    private Context context;

    public GridItemDecoration(int spanCount, int horizontalSpace, int verticalSpace) {
        this.spanCount = spanCount;
        this.horizontalSpace = horizontalSpace;
        this.verticalSpace = verticalSpace;
        context = MyApplication.appContext;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position
        int childCount = parent.getAdapter().getItemCount(); // item count

       /* outRect.right = DisplayUtil.dip2px(context, horizontalSpace);
        outRect.bottom = DisplayUtil.dip2px(context, verticalSpace);*/


        if (isLastRaw(parent, position, childCount))// 如果是最后一行，则不需要绘制底部
        {
            outRect.set(0, 0, DisplayUtil.dip2px(context, horizontalSpace), 0);
        } else if (isLastColum(parent, position, childCount))// 如果是最后一列，则不需要绘制右边
        {
            outRect.set(0, 0, 0, DisplayUtil.dip2px(context, verticalSpace));
        } else
        {
            outRect.set(0, 0, DisplayUtil.dip2px(context, horizontalSpace), DisplayUtil.dip2px(context, verticalSpace));
        }
    }



    /**
     * 是否是最后一列
     * @param parent
     * @param pos
     * @param childCount
     * @return
     */
    private boolean isLastColum(RecyclerView parent, int pos, int childCount)
    {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else
            {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    /**
     * 是否是最后一行
     * @param parent
     * @param pos
     * @param childCount
     * @return
     */
    private boolean isLastRaw(RecyclerView parent, int pos, int childCount)
    {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
