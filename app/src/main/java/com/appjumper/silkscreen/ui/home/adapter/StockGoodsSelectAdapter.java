package com.appjumper.silkscreen.ui.home.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 现货商城商品选择列表adapter
 * Created by Botx on 2017/9/12.
 */

public class StockGoodsSelectAdapter extends BaseQuickAdapter<Product, BaseViewHolder> {


    public StockGoodsSelectAdapter(@LayoutRes int layoutResId, @Nullable List<Product> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Product item) {
        Picasso.with(mContext)
                .load(item.getImg())
                .resize(DisplayUtil.dip2px(mContext, 120), DisplayUtil.dip2px(mContext, 95))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into((ImageView) helper.getView(R.id.imageView));

        helper.setText(R.id.txtTitle, item.getProduct_name())
                .setText(R.id.txtSubTitle, item.getDescription());
    }
}
