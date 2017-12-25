package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.AskBuyOffer;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.appjumper.silkscreen.R.id.txtState;

/**
 * 求购管理--报价adapter
 * Created by Botx on 2017/12/11.
 */

public class AskBuyManageOfferAdapter extends BaseQuickAdapter<AskBuyOffer, BaseViewHolder> {

    public AskBuyManageOfferAdapter(@LayoutRes int layoutResId, @Nullable List<AskBuyOffer> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AskBuyOffer item) {
        Picasso.with(mContext)
                .load(item.getProduct_img())
                .resize(DisplayUtil.dip2px(mContext, 60), DisplayUtil.dip2px(mContext, 60))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into((ImageView) helper.getView(R.id.imgViHead));

        helper.setText(R.id.txtTime, item.getCreate_time().substring(5, 16))
                .setText(R.id.txtOfferNum, "报价" + "(" + item.getOffer_num() + ")")
                .setText(R.id.txtHint, item.getExpiry_date().substring(5, 16) + "截止")
                .setText(R.id.txtPrice, "我的报价: " + item.getMoney() + "元");

        if (item.getPruchase_type().equals(Const.INFO_TYPE_OFFICIAL + ""))
            helper.setText(R.id.txtTitle, "求购G" + item.getId());
        else
            helper.setText(R.id.txtTitle, "求购C" + item.getId());

        if (item.getPurchase_num().equals("0"))
            helper.setText(R.id.txtContent, "求购" + item.getProduct_name());
        else
            helper.setText(R.id.txtContent, "求购" +  item.getProduct_name() + " " + item.getPurchase_num() + item.getPurchase_unit());


        long expiryTime = AppTool.getTimeMs(item.getExpiry_date(), "yy-MM-dd HH:mm:ss");
        if (System.currentTimeMillis() < expiryTime) {
            helper.setText(txtState, "报价中");
        } else {
            helper.setText(txtState, "报价结束");
        }

    }

}
