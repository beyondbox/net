package com.appjumper.silkscreen.ui.my.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 我的界面发货厂家订单adapter
 * Created by Botx on 2017/10/27.
 */

public class MyDeliverAdapter extends BaseQuickAdapter<Freight, BaseViewHolder> {

    public MyDeliverAdapter(@LayoutRes int layoutResId, @Nullable List<Freight> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Freight item) {
        helper.setText(R.id.txtTitle, item.getFrom_name() + " - " + item.getTo_name())
                .setText(R.id.txtCarModel, item.getLengths_name() + "/" + item.getModels_name())
                .setText(R.id.txtProduct, item.getWeight() + item.getProduct_name())
                .setText(R.id.txtTime, item.getExpiry_date().substring(5, 16) + "装车")
                .addOnClickListener(R.id.txtHandle0)
                .addOnClickListener(R.id.txtHandle1)
                .addOnClickListener(R.id.txtHandle2);

        TextView txtState = helper.getView(R.id.txtState);
        TextView txtContent = helper.getView(R.id.txtContent);
        int state = Integer.valueOf(item.getExamine_status());
        switch (state) {
            case Const.FREIGHT_AUDITING:
                txtState.setText("正在审核中");
                txtContent.setText("");
                break;
            case Const.FREIGHT_AUDIT_REFUSE:
                txtState.setText("审核不通过");
                if (TextUtils.isEmpty(item.getExamine_refusal_reason()))
                    txtContent.setText("");
                else
                    txtContent.setText("原因:" + item.getExamine_refusal_reason());
                break;
            case Const.FREIGHT_AUDIT_PASS:
                txtState.setText("司机报价中" + "(" + item.getOffer_num() + ")");
                if (TextUtils.isEmpty(item.getMin_money()))
                    txtContent.setText("");
                else
                    txtContent.setText("最低报价\n" + item.getMin_money());
                break;
            case Const.FREIGHT_DRIVER_PAYING:
                txtState.setText("等待司机支付");
                txtContent.setText("确认运费\n" + item.getConfirm_driver_offer());
                break;
            case Const.FREIGHT_GOTO_LOAD:
                txtState.setText("司机正在赶来");
                txtContent.setText("确认运费\n" + item.getConfirm_driver_offer());
                break;
            case Const.FREIGHT_LOADING:
                txtState.setText("装货中");
                txtContent.setText("确认运费\n" + item.getConfirm_driver_offer());
                break;
            case Const.FREIGHT_TRANSPORTING:
                txtState.setText("运输途中");
                txtContent.setText("");
                break;
            case Const.FREIGHT_TRANSPORT_FINISH:
                txtState.setText("运输完成");
                txtContent.setText("确认运费\n" + item.getConfirm_driver_offer());
                break;
            case Const.FREIGHT_ORDER_FINISH:
                txtState.setText("订单完成");
                txtContent.setText("");
                break;
            case Const.FREIGHT_INVALID:
                txtState.setText("已失效");
                txtContent.setText("");
                break;
        }

    }

}
