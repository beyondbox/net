package com.appjumper.silkscreen.ui.home.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.appjumper.silkscreen.bean.LineList;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 物流货站列表adapter
 * Created by botx on 2017/9/30.
 */

public class LineListAdapter extends BaseQuickAdapter<LineList, BaseViewHolder> {

    public LineListAdapter(@LayoutRes int layoutResId, @Nullable List<LineList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LineList item) {

    }

}
