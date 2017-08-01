package com.appjumper.silkscreen.ui.my.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.appjumper.silkscreen.bean.Product;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 我的发布adapter
 * Created by Botx on 2017/7/31.
 */

public class MyReleaseAdapter extends BaseQuickAdapter<Product, BaseViewHolder> {

    public MyReleaseAdapter(@LayoutRes int layoutResId, @Nullable List<Product> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Product item) {

    }

}
