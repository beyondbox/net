package com.appjumper.silkscreen.ui.my.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.FreightOffer;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 司机订单列表adapter
 * Created by Botx on 2017/10/27.
 */

public class DriverOrderListAdapter extends BaseQuickAdapter<Freight, BaseViewHolder> {

    public DriverOrderListAdapter(@LayoutRes int layoutResId, @Nullable List<Freight> data) {
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
            case Const.FREIGHT_AUDIT_PASS:
                List<FreightOffer> offerList = item.getOffer_list();
                if (offerList != null && offerList.size() > 0) {
                    txtState.setText("已报价");
                    txtContent.setText("当前报价\n" + item.getOffer_num());
                    setButtonVisibility(helper, false, true, true);
                    setButtonName(helper, "", "忽略订单", offerList.get(0).getMoney());
                } else {
                    txtState.setText("收到询价");
                    txtContent.setText("当前报价\n" + item.getOffer_num());
                    setButtonVisibility(helper, false, true, true);
                    setButtonName(helper, "", "忽略订单", "报价");
                }
                break;
            case Const.FREIGHT_DRIVER_PAYING:
                txtState.setText("等待支付");
                txtContent.setText("");
                setButtonVisibility(helper, false, true, true);
                setButtonName(helper, "", "放弃订单", "支付(200元)");
                break;
            case Const.FREIGHT_GOTO_LOAD:
                txtState.setText("前往厂家");
                txtContent.setText("");
                setButtonVisibility(helper, false, true, true);
                setButtonName(helper, "", "联系厂家", "导航去厂家");
                break;
            case Const.FREIGHT_LOADING:
                txtState.setText("装货中");
                txtContent.setText("");
                setButtonVisibility(helper, false, true, true);
                setButtonName(helper, "", "联系厂家", "联系客服");
                break;
            case Const.FREIGHT_TRANSPORTING:
                txtState.setText("运输途中");
                txtContent.setText("");
                setButtonVisibility(helper, false, true, true);
                setButtonName(helper, "", "更新位置", "确认送达");
                break;
            case Const.FREIGHT_TRANSPORT_FINISH:
                txtState.setText("运输完成");
                txtContent.setText("运费\n" + item.getOffer_list().get(0).getMoney());
                setButtonVisibility(helper, true, true, true);
                setButtonName(helper, "联系厂家", "联系客服", "确认收到运费");
                break;
            case Const.FREIGHT_ORDER_FINISH:
                txtState.setText("订单完成");
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
