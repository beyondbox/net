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
                setButtonVisibility(helper, false, false, true);
                setButtonName(helper, "", "", "联系客服");
                break;
            case Const.FREIGHT_AUDIT_REFUSE:
                txtState.setText("审核不通过");
                if (TextUtils.isEmpty(item.getExamine_refusal_reason()))
                    txtContent.setText("");
                else
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
                txtContent.setText("确认运费\n" + item.getConfirm_driver_offer());
                setButtonVisibility(helper, false, false, true);
                setButtonName(helper, "", "", "联系客服");
                break;
            case Const.FREIGHT_GOTO_LOAD:
                txtState.setText("司机正在赶来");
                txtContent.setText("确认运费\n" + item.getConfirm_driver_offer());
                setButtonVisibility(helper, false, true, true);
                setButtonName(helper, "", "联系客服", "确认司机到达");
                break;
            case Const.FREIGHT_LOADING:
                txtState.setText("装货中");
                txtContent.setText("确认运费\n" + item.getConfirm_driver_offer());
                setButtonVisibility(helper, false, true, true);
                setButtonName(helper, "", "联系客服", "确认装货完成");
                break;
            case Const.FREIGHT_TRANSPORTING:
                txtState.setText("运输途中");
                txtContent.setText("");
                String arriveState = item.getConfirm_arrive();
                if (arriveState.equals("0")) {
                    setButtonVisibility(helper, true, true, true);
                    setButtonName(helper, "联系司机", "查看司机位置", "确认送达");
                } else {
                    setButtonVisibility(helper, true, true, false);
                    setButtonName(helper, "联系司机", "查看司机位置", "");
                }
                break;
            case Const.FREIGHT_TRANSPORT_FINISH:
                txtState.setText("运输完成");
                txtContent.setText("确认运费\n" + item.getConfirm_driver_offer());
                setButtonVisibility(helper, true, true, item.getPay_type().equals("0") ? true : false);
                setButtonName(helper, "联系司机", "联系客服", "支付运费");
                break;
            case Const.FREIGHT_APPLY_AHEAD_CHARGE:
                txtState.setText("正在申请运费垫付");
                txtContent.setText("");
                setButtonVisibility(helper, false, false, false);
                setButtonName(helper, "", "", "");
                break;
            case Const.FREIGHT_ORDER_FINISH:
                txtState.setText("订单完成");
                txtContent.setText("");
                setButtonVisibility(helper, false, false, true);
                setButtonName(helper, "", "", "发布新订单");
                break;
            case Const.FREIGHT_INVALID:
                txtState.setText("已失效");
                txtContent.setText("");
                setButtonVisibility(helper, false, false, false);
                setButtonName(helper, "", "", "");
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

        if (!btn0 && !btn1 && !btn2)
            helper.setVisible(R.id.llHandle, false);
        else
            helper.setVisible(R.id.llHandle, true);
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
