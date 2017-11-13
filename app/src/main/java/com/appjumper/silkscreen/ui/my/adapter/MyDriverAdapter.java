package com.appjumper.silkscreen.ui.my.adapter;

import android.os.CountDownTimer;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.FreightOffer;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.ui.common.adapter.CountDownViewHolder;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.appjumper.silkscreen.R.id.txtTitle;

/**
 * 我的界面司机订单adapter
 * Created by Botx on 2017/10/27.
 */

public class MyDriverAdapter extends BaseQuickAdapter<Freight, CountDownViewHolder> {

    private List<CountDownTimer> timerList = new ArrayList<>();
    private DecimalFormat dFormat = new DecimalFormat("00");

    long dMs = 1000 * 60 * 60 * 24; //一天的毫秒数
    long hMs = 1000 * 60 * 60; //一小时的毫秒数
    long mMs = 1000 * 60; //一分钟的毫秒数
    long sMs = 1000; //一秒的毫秒数


    public MyDriverAdapter(@LayoutRes int layoutResId, @Nullable List<Freight> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(CountDownViewHolder helper, Freight item) {
        if (helper.countDownTimer != null) {
            helper.countDownTimer.cancel();
            helper.countDownTimer = null;
        }


        helper.setText(txtTitle, item.getFrom_name() + " - " + item.getTo_name())
                .setText(R.id.txtCarModel, item.getLengths_name() + "/" + item.getModels_name())
                .setText(R.id.txtProduct, item.getWeight() + item.getProduct_name())
                .setText(R.id.txtTime, item.getExpiry_date().substring(5, 16) + "装车");

        if (item.getCar_product_type().equals(Const.INFO_TYPE_OFFICIAL + "")) {
            String endName = "";
            String fullName = item.getTo_name();
            String [] arr = fullName.split(",");
            String province = arr[1];
            if (province.contains("省"))
                endName = province.substring(0, province.length() - 1) + arr[2];
            else
                endName = province + arr[2];

            if (endName.contains("市"))
                endName = endName.substring(0, endName.length() - 1);

            helper.setText(txtTitle, item.getFrom_name() + " - " + endName);
        }


        TextView txtState = helper.getView(R.id.txtState);
        TextView txtContent = helper.getView(R.id.txtContent);
        int state = Integer.valueOf(item.getExamine_status());
        switch (state) {
            case Const.FREIGHT_AUDIT_PASS:
                List<FreightOffer> offerList = item.getOffer_list();
                if (offerList != null && offerList.size() > 0) {
                    txtState.setText("已报价");
                    txtContent.setText("当前报价\n" + item.getOffer_num());
                } else {
                    txtState.setText("收到询价");
                    txtContent.setText("当前报价\n" + item.getOffer_num());
                }
                break;
            case Const.FREIGHT_DRIVER_PAYING:
                txtState.setText("等待支付");
                long endTime = HttpUtil.getTimeLong(item.getExpiry_driver_pay_date());
                if (System.currentTimeMillis() < endTime) {
                    startCountDown(helper, endTime, state);
                } else {
                    txtContent.setText("已超时");
                }
                break;
            case Const.FREIGHT_GOTO_LOAD:
                txtState.setText("前往厂家");
                long endTime2 = HttpUtil.getTimeLong(item.getExpiry_date());
                if (System.currentTimeMillis() < endTime2) {
                    startCountDown(helper, endTime2, state);
                } else {
                    txtContent.setText("已超时");
                }
                break;
            case Const.FREIGHT_LOADING:
                txtState.setText("装货中");
                txtContent.setText("");
                break;
            case Const.FREIGHT_TRANSPORTING:
                txtState.setText("运输途中");
                long endTime3 = HttpUtil.getTimeLong(item.getEnterprise_expect_date());
                if (System.currentTimeMillis() < endTime3) {
                    startCountDown(helper, endTime3, state);
                } else {
                    txtContent.setText("已超时");
                }
                break;
            case Const.FREIGHT_TRANSPORT_FINISH:
                txtState.setText("运输完成");
                if (item.getOffer_list() != null && item.getOffer_list().size() > 0)
                    txtContent.setText("运费\n" + item.getOffer_list().get(0).getMoney());
                break;
            case Const.FREIGHT_ORDER_FINISH:
                txtState.setText("订单完成");
                txtContent.setText("");
                break;
            case Const.FREIGHT_APPLY_AHEAD_CHARGE:
                txtState.setText("正在申请运费垫付");
                txtContent.setText("");
                break;
            case Const.FREIGHT_INVALID:
                txtState.setText("已失效");
                txtContent.setText("");
                break;
        }

    }


    /**
     * 开始倒计时
     */
    private void startCountDown(final CountDownViewHolder vh, long endTime, final int state) {
        long countDownTime = endTime - System.currentTimeMillis();
        if (countDownTime > 0) {
            vh.countDownTimer = new CountDownTimer(countDownTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long days = 0;
                    long hours = 0;
                    long minutes = 0;
                    long seconds = 0;
                    switch (state) {
                        case Const.FREIGHT_DRIVER_PAYING:
                            minutes = millisUntilFinished / mMs;
                            seconds = millisUntilFinished % mMs / sMs;
                            vh.setText(R.id.txtContent, "支付倒计时\n" + dFormat.format(minutes) + " : " + dFormat.format(seconds));
                            break;
                        case Const.FREIGHT_GOTO_LOAD:
                            days = millisUntilFinished / dMs;
                            hours = millisUntilFinished % dMs / hMs;
                            minutes = millisUntilFinished % hMs / mMs;
                            vh.setText(R.id.txtContent, "装车倒计时\n" + days + "天" + hours + "小时" + dFormat.format(minutes) + "分");
                            break;
                        case Const.FREIGHT_TRANSPORTING:
                            days = millisUntilFinished / dMs;
                            hours = millisUntilFinished % dMs / hMs;
                            minutes = millisUntilFinished % hMs / mMs;
                            vh.setText(R.id.txtContent, "运达倒计时\n" + days + "天" + hours + "小时" + dFormat.format(minutes) + "分");
                            break;
                    }

                }

                @Override
                public void onFinish() {
                    notifyDataSetChanged();
                }
            }.start();

            timerList.add(vh.countDownTimer);
        }
    }


    /**
     * 取消所有的计时器，释放资源
     */
    public void cancelAllTimers() {
        for (CountDownTimer countDownTimer : timerList) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        }
    }


}
