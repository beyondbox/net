package com.appjumper.silkscreen.ui.my.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 发货厂家订单列表adapter
 * Created by Botx on 2017/10/27.
 */

public class DeliverOrderListAdapter extends BaseQuickAdapter<Freight, BaseViewHolder> {

    public DeliverOrderListAdapter(@LayoutRes int layoutResId, @Nullable List<Freight> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Freight item) {
        helper.setText(R.id.txtOrderId, "订单编号 : " + item.getOrder_id())
                .setText(R.id.txtTitle, item.getFrom_name() + " - " + item.getTo_name())
                .setText(R.id.txtCarModel, item.getLengths_name() + "/" + item.getModels_name())
                .setText(R.id.txtProduct, item.getWeight() + item.getProduct_name())
                .setText(R.id.txtTime, item.getExpiry_date().substring(5, 16) + "装车");

        TextView txtState = helper.getView(R.id.txtState);
        TextView txtContent = helper.getView(R.id.txtContent);
        int state = Integer.valueOf(item.getExamine_status());
        switch (state) {
            case Const.FREIGHT_AUDITING:
                txtState.setText("正在审核中");
                txtContent.setText("");
                setButtonVisibility(helper, false, false, true);
                setButtonName(helper, "", "", "联系客服");
                break;
            case Const.FREIGHT_AUDIT_REFUSE:
                txtState.setText("审核不通过");
                txtContent.setText("原因:" + item.getExamine_refusal_reason());
                setButtonVisibility(helper, false, true, true);
                setButtonName(helper, "", "重新发布", "联系客服");
                break;
            case Const.FREIGHT_AUDIT_PASS:
                txtState.setText("司机报价中" + "(" + item.getOffer_num() + ")");
                if (TextUtils.isEmpty(item.getMin_money()))
                    txtContent.setText("");
                else
                    txtContent.setText("最低报价\n" + item.getMin_money());
                setButtonVisibility(helper, false, true, true);
                setButtonName(helper, "", "联系客服", "选择司机");
                break;
            case Const.FREIGHT_DRIVER_PAYING:
                txtState.setText("等待司机支付");
                txtContent.setText("");
                setButtonVisibility(helper, false, false, true);
                setButtonName(helper, "", "", "联系客服");
                break;
        }

    }


    /**
     * 设置按钮是否可见
     * @param helper
     * @param btn0
     * @param btn1
     * @param btn2
     */
    private void setButtonVisibility(BaseViewHolder helper, boolean btn0, boolean btn1, boolean btn2) {
        TextView txtHandle0 = helper.getView(R.id.txtHandle0);
        TextView txtHandle1 = helper.getView(R.id.txtHandle1);
        TextView txtHandle2 = helper.getView(R.id.txtHandle2);

        txtHandle0.setVisibility(btn0 ? View.VISIBLE : View.INVISIBLE);
        txtHandle1.setVisibility(btn1 ? View.VISIBLE : View.INVISIBLE);
        txtHandle2.setVisibility(btn2 ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 设置按钮的名称
     * @param helper
     * @param name0
     * @param name1
     * @param name2
     */
    private void setButtonName(BaseViewHolder helper, String name0, String name1, String name2) {
        TextView txtHandle0 = helper.getView(R.id.txtHandle0);
        TextView txtHandle1 = helper.getView(R.id.txtHandle1);
        TextView txtHandle2 = helper.getView(R.id.txtHandle2);

        txtHandle0.setText(name0);
        txtHandle1.setText(name1);
        txtHandle2.setText(name2);
    }


}
