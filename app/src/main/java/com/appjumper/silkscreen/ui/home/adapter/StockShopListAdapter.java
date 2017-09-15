package com.appjumper.silkscreen.ui.home.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 现货商城列表
 * Created by Botx on 2017/8/25.
 */

public class StockShopListAdapter extends BaseQuickAdapter<StockGoods, BaseViewHolder> {

    public StockShopListAdapter(@LayoutRes int layoutResId, @Nullable List<StockGoods> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StockGoods item) {
        String [] imgArr = item.getCover_img().split(",");
        Picasso.with(mContext)
                .load(imgArr[0])
                .resize(DisplayUtil.dip2px(mContext, 70), DisplayUtil.dip2px(mContext, 70))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into((ImageView) helper.getView(R.id.imageView));

        helper.setText(R.id.txtTitle, item.getTitle())
                .setText(R.id.txtCount, item.getConsult_num() + "人咨询")
                .setText(R.id.txtSurplus, "剩余：" + item.getStock() + item.getStock_unit())
                .setText(R.id.txtSales, "销售：" + item.getSale_num() + item.getStock_unit());

        TextView txtSales = helper.getView(R.id.txtSales);
        if (TextUtils.isEmpty(item.getSale_num()))
            txtSales.setVisibility(View.INVISIBLE);
        else
            txtSales.setVisibility(View.VISIBLE);


        if (TextUtils.isEmpty(item.getUnit_price()))
            helper.setText(R.id.txtPrice, "时价");
        else
            helper.setText(R.id.txtPrice, item.getUnit_price() + "元/" + item.getPrice_unit());
    }

}
