package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.appjumper.silkscreen.R.id.txtState;

/**
 * 求购管理adapter
 * Created by Botx on 2017/12/11.
 */

public class AskBuyManageAdapter extends BaseQuickAdapter<AskBuy, BaseViewHolder> {

    public AskBuyManageAdapter(@LayoutRes int layoutResId, @Nullable List<AskBuy> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AskBuy item) {
        Picasso.with(mContext)
                .load(item.getImg())
                .resize(DisplayUtil.dip2px(mContext, 60), DisplayUtil.dip2px(mContext, 60))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into((ImageView) helper.getView(R.id.imgViHead));

        helper.setText(R.id.txtTime, item.getCreate_time().substring(5, 16))
                .setText(R.id.txtOfferNum, "报价" + "（" + item.getOffer_num() + "）")
                .addOnClickListener(R.id.txtHandle)
                .addOnClickListener(R.id.txtHandle1);

        if (item.getPurchase_num().equals("0"))
            helper.setText(R.id.txtTitle, item.getProduct_name());
        else
            helper.setText(R.id.txtTitle, item.getProduct_name() + item.getPurchase_num() + item.getPurchase_unit());

        TextView txtOfferNum = helper.getView(R.id.txtOfferNum);
        if (item.getOffer_num_read().equals("0"))
            txtOfferNum.setTextColor(mContext.getResources().getColor(R.color.red_color));
        else
            txtOfferNum.setTextColor(mContext.getResources().getColor(R.color.text_black_color2));


        int status = Integer.valueOf(item.getExamine_status());
        switch (status) {
            case Const.ASKBUY_AUDITING: //审核中
                helper.setVisible(R.id.txtOfferNum, false)
                        .setVisible(R.id.txtHandle1, false)
                        .setText(txtState, "审核中")
                        .setText(R.id.txtHandle, "取消求购")
                        .setText(R.id.txtContent, item.getExpiry_date().substring(5, 16) + "截止");
                break;
            case Const.ASKBUY_REFUSE: //审核失败
                helper.setVisible(R.id.txtOfferNum, false)
                        .setVisible(R.id.txtHandle1, false)
                        .setText(txtState, "审核失败")
                        .setText(R.id.txtHandle, "编辑重发")
                        .setText(R.id.txtContent, "原因: " + item.getExamine_refusal_reason());
                break;
            case Const.ASKBUY_OFFERING: //报价中和报价结束
                helper.setVisible(R.id.txtOfferNum, true);
                long expiryTime = AppTool.getTimeMs(item.getExpiry_date(), "yy-MM-dd HH:mm:ss");
                if (System.currentTimeMillis() < expiryTime) {
                    helper.setText(txtState, "报价中")
                            .setVisible(R.id.txtHandle1, false)
                            .setText(R.id.txtHandle, "查看详情")
                            .setText(R.id.txtContent, item.getExpiry_date().substring(5, 16) + "截止");
                } else {
                    helper.setText(txtState, "报价结束")
                            .setVisible(R.id.txtHandle1, true)
                            .setText(R.id.txtHandle, "重新发布")
                            .setText(R.id.txtContent, "已截止");
                }
                break;
            default:
                helper.setText(txtState, "")
                        .setVisible(R.id.txtHandle1, false)
                        .setText(R.id.txtHandle, "查看详情")
                        .setText(R.id.txtContent, item.getExpiry_date().substring(5, 16) + "截止");
                break;
        }

    }

}
