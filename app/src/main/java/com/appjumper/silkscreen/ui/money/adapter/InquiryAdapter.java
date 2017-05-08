package com.appjumper.silkscreen.ui.money.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.MyInquiry;
import com.appjumper.silkscreen.net.HttpUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-10-11.
 * 询价适配器
 */
public class InquiryAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<MyInquiry> list;

    private List<CountDownTimer> timerList = new ArrayList<>();
    private DecimalFormat dFormat = new DecimalFormat("00");

    long hMs = 1000 * 60 * 60; //一小时的毫秒数
    long mMs = 1000 * 60; //一分钟的毫秒数
    long sMs = 1000; //一秒的毫秒数


    public InquiryAdapter(Context context, List<MyInquiry> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_money_inquiry, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        MyInquiry myInquiry = list.get(position);
        switch (myInquiry.getType()) {
            case "1":
                viewHolder.tv_product_name.setText("咨询" + myInquiry.getProduct_name() + "订做报价");
                break;
            case "2":
                viewHolder.tv_product_name.setText("咨询" + myInquiry.getProduct_name() + "加工报价");
                break;
            case "3":
                viewHolder.tv_product_name.setText("咨询" + myInquiry.getProduct_name() + "现货报价");
                break;
            default:
                break;
        }
        viewHolder.tv_datatime.setText(myInquiry.getCreate_time().substring(5, 16));
        if (myInquiry.getStatus().equals("0")) {//已取消
            if (myInquiry.getOffer_num() != null && !myInquiry.getOffer_num().equals("") && Integer.parseInt(myInquiry.getOffer_num()) > 0) {
                viewHolder.tv_yes_quotation.setVisibility(View.VISIBLE);
                viewHolder.tv_no_quotation.setVisibility(View.GONE);
                viewHolder.tv_offer.setText(myInquiry.getOffer_avg() + "元");
                viewHolder.tv_company.setText(myInquiry.getOffer_num() + "家");
                viewHolder.tv_state.setText("已取消");
                viewHolder.tv_state.setTextColor(mContext.getResources().getColor(R.color.text_gray_color));
            } else {
                viewHolder.tv_yes_quotation.setVisibility(View.GONE);
                viewHolder.tv_no_quotation.setVisibility(View.VISIBLE);
                viewHolder.tv_no_quotation.setText("无报价");
                viewHolder.tv_state.setText("已取消");
                viewHolder.tv_state.setTextColor(mContext.getResources().getColor(R.color.text_gray_color));
            }
        } else if (myInquiry.getStatus().equals("1")) {
            long endTime = HttpUtil.getTimeLong(myInquiry.getExpiry_date());
            if (System.currentTimeMillis() <= endTime) {
                if (myInquiry.getOffer_num() != null && !myInquiry.getOffer_num().equals("") && Integer.parseInt(myInquiry.getOffer_num()) > 0) {
                    viewHolder.tv_yes_quotation.setVisibility(View.VISIBLE);
                    viewHolder.tv_no_quotation.setVisibility(View.GONE);
                    viewHolder.tv_offer.setText(myInquiry.getOffer_avg() + "元");
                    viewHolder.tv_company.setText(myInquiry.getOffer_num() + "家");
                    //viewHolder.tv_state.setText("进行中");
                    viewHolder.tv_state.setTextColor(mContext.getResources().getColor(R.color.orange_color));
                    startCountDown(viewHolder, endTime);
                } else {
                    viewHolder.tv_yes_quotation.setVisibility(View.GONE);
                    viewHolder.tv_no_quotation.setVisibility(View.VISIBLE);
                    viewHolder.tv_no_quotation.setText("暂无报价");
                    //viewHolder.tv_state.setText("进行中");
                    viewHolder.tv_state.setTextColor(mContext.getResources().getColor(R.color.orange_color));
                    startCountDown(viewHolder, endTime);
                }
            }else {
                if (myInquiry.getOffer_num() != null && !myInquiry.getOffer_num().equals("") && Integer.parseInt(myInquiry.getOffer_num()) > 0) {
                    viewHolder.tv_yes_quotation.setVisibility(View.VISIBLE);
                    viewHolder.tv_no_quotation.setVisibility(View.GONE);
                    viewHolder.tv_offer.setText(myInquiry.getOffer_avg() + "元");
                    viewHolder.tv_company.setText(myInquiry.getOffer_num() + "家");
                    viewHolder.tv_state.setText("已截止");
                    viewHolder.tv_state.setTextColor(mContext.getResources().getColor(R.color.text_gray_color));
                } else {
                    viewHolder.tv_yes_quotation.setVisibility(View.GONE);
                    viewHolder.tv_no_quotation.setVisibility(View.VISIBLE);
                    viewHolder.tv_no_quotation.setText("无报价");
                    viewHolder.tv_state.setText("已截止");
                    viewHolder.tv_state.setTextColor(mContext.getResources().getColor(R.color.text_gray_color));
                }
            }
        }

    }

    /**
     * 开始倒计时
     */
    private void startCountDown(final ViewHolder vh, long endTime) {
        if (vh.countDownTimer != null) {
            vh.countDownTimer.cancel();
            vh.countDownTimer = null;
        }

        long countDownTime = endTime - System.currentTimeMillis();
        if (countDownTime > 0) {
            vh.countDownTimer = new CountDownTimer(countDownTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long hours = millisUntilFinished / hMs;
                    long minutes = millisUntilFinished % hMs / mMs;
                    long seconds = millisUntilFinished % mMs / sMs;
                    vh.tv_state.setText(dFormat.format(hours) + " : "
                            + dFormat.format(minutes) + " : "
                            + dFormat.format(seconds) + "后截止");
                }

                @Override
                public void onFinish() {
                    notifyDataSetChanged();
                }
            }.start();

            timerList.add(vh.countDownTimer);
        }
    }

    static class ViewHolder {

        @Bind(R.id.tv_no_quotation)//暂无报价
                TextView tv_no_quotation;

        @Bind(R.id.tv_state)//进行中／已截止
                TextView tv_state;

        @Bind(R.id.tv_product_name)//产品名称
                TextView tv_product_name;

        @Bind(R.id.tv_datatime)//时间
                TextView tv_datatime;

        @Bind(R.id.tv_offer)//平均报价
                TextView tv_offer;

        @Bind(R.id.tv_company)//报价公司
                TextView tv_company;

        @Bind(R.id.tv_yes_quotation)//有报价时显示
                LinearLayout tv_yes_quotation;

        private CountDownTimer countDownTimer;


        //
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
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
