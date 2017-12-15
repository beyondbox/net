package com.appjumper.silkscreen.ui.my.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
 * 求购订单列表adapter
 * Created by Botx on 2017/12/14.
 */

public class AskBuyOrderListAdapter extends BaseQuickAdapter<AskBuy, BaseViewHolder> {

    public AskBuyOrderListAdapter(@LayoutRes int layoutResId, @Nullable List<AskBuy> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AskBuy item) {
        Picasso.with(mContext)
                .load(item.getProduct_img())
                .resize(DisplayUtil.dip2px(mContext, 60), DisplayUtil.dip2px(mContext, 60))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into((ImageView) helper.getView(R.id.imgViHead));

        helper.setText(R.id.txtProduct, item.getProduct_name())
                .setText(R.id.txtContent, item.getPurchase_content())
                .setText(R.id.txtPrice, item.getOffer_money() + "元/" + item.getPurchase_unit())
                .setText(R.id.txtNum, "x " + item.getPurchase_num() + item.getPurchase_unit())
                .setText(R.id.txtTotal, "¥" + item.getPay_money())
                .addOnClickListener(R.id.txtHandle0)
                .addOnClickListener(R.id.txtHandle1)
                .addOnClickListener(R.id.txtHandle2)
                .addOnClickListener(R.id.txtHandle3);

        if (TextUtils.isEmpty(item.getOrder_id()))
            helper.setText(R.id.txtOrderId, "无");
        else
            helper.setText(R.id.txtOrderId, item.getOrder_id());

        int state = Integer.valueOf(item.getExamine_status());
        switch (state) {
            case Const.ASKBUY_ORDER_AUDITING: //待审核
                helper.setText(R.id.txtState, "待审核")
                        .setVisible(R.id.llHandle, false)
                        .setVisible(R.id.llRemark, true)
                        .setVisible(R.id.txtHandle0, true)
                        .setText(R.id.txtRemark, "请耐心等待官方确认货物供应")
                        .setText(R.id.txtHandle0, "取消订单");
                break;
            case Const.ASKBUY_ORDER_REFUSE: //审核拒绝
                helper.setText(R.id.txtState, "审核失败")
                        .setVisible(R.id.llHandle, false)
                        .setVisible(R.id.llRemark, true)
                        .setVisible(R.id.txtHandle0, true)
                        .setText(R.id.txtRemark, "原因: " + item.getExamine_refusal_reason())
                        .setText(R.id.txtHandle0, "删除订单");
                break;
            case Const.ASKBUY_ORDER_PAYING: //待付款
                helper.setText(R.id.txtState, "待付款")
                        .setVisible(R.id.llHandle, true)
                        .setVisible(R.id.llRemark, false);
                break;
            case Const.ASKBUY_ORDER_RECEIPTING: //待完成
                helper.setText(R.id.txtState, "待完成")
                        .setVisible(R.id.llHandle, false)
                        .setVisible(R.id.llRemark, true)
                        .setVisible(R.id.txtHandle0, false)
                        .setText(R.id.txtRemark, "请耐心等待官方确认收到款项");
                break;
            case Const.ASKBUY_ORDER_FINISH: //交易完成
                helper.setText(R.id.txtState, "交易完成")
                        .setVisible(R.id.llHandle, false)
                        .setVisible(R.id.llRemark, true)
                        .setVisible(R.id.txtHandle0, true)
                        .setText(R.id.txtRemark, "")
                        .setText(R.id.txtHandle0, "删除订单");
                break;
        }
    }

}
