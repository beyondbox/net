package com.appjumper.silkscreen.ui.home.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 首页热门求购adapter
 * Created by Botx on 2017/10/18.
 */

public class HomeAskBuyListAdapter extends BaseQuickAdapter<AskBuy, BaseViewHolder> {

    public HomeAskBuyListAdapter(@LayoutRes int layoutResId, @Nullable List<AskBuy> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AskBuy item) {
        Picasso.with(mContext)
                .load(item.getImg())
                .resize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into((ImageView) helper.getView(R.id.imgViHead));


        if (item.getPruchase_type().equals(Const.INFO_TYPE_OFFICIAL + ""))
            helper.setText(R.id.txtTitle, "求购G" + item.getId() + " " + item.getProduct_name() + item.getPurchase_num() + item.getPurchase_unit());
        else
            helper.setText(R.id.txtTitle, "求购C" + item.getId() + " " + item.getProduct_name() + item.getPurchase_num() + item.getPurchase_unit());


        helper.setText(R.id.txtTime, item.getCreate_time().substring(5, 16))
                .setText(R.id.txtOfferNum, "报价" + "(" + item.getOffer_num() + ")");
    }

}
