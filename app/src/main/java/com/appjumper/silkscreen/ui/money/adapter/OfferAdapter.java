package com.appjumper.silkscreen.ui.money.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Myoffer;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-10-11.
 * 报价适配器
 */
public class OfferAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<Myoffer> offerList;

    private List<CountDownTimer> timerList = new ArrayList<>();
    private DecimalFormat dFormat = new DecimalFormat("00");

    long hMs = 1000 * 60 * 60; //一小时的毫秒数
    long mMs = 1000 * 60; //一分钟的毫秒数
    long sMs = 1000; //一秒的毫秒数


    public OfferAdapter(Context context, List<Myoffer> offerList) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.offerList = offerList;
    }

    @Override
    public int getCount() {
        if (offerList != null) {
            return offerList.size();
        } else {
            return 0;
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_money_offer, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        Myoffer item = offerList.get(position);
        if (item.getEnterprise_logo() != null && !item.getEnterprise_logo().getSmall().equals("")) {
            Picasso.with(mContext).load(item.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.iv_logo);
        }
        viewHolder.tv_nickname.setText(item.getNickname());
        viewHolder.tv_company_name.setText(item.getEnterprise_name());
        viewHolder.tv_create_time.setText(item.getCreate_time());
        switch (item.getType()) {
            case "1":
                viewHolder.tv_service.setText("咨询" + item.getProduct_name() + "订做报价");
                break;
            case "2":
                viewHolder.tv_service.setText("咨询" + item.getProduct_name() + "加工报价");
                break;
            case "3":
                viewHolder.tv_service.setText("咨询" + item.getProduct_name() + "现货报价");
                break;
            default:
                break;
        }


        if (viewHolder.countDownTimer != null) {
            viewHolder.countDownTimer.cancel();
            viewHolder.countDownTimer = null;
        }


        if (item.getStatus().equals("0")) {
            viewHolder.tvHandle.setText("未报价");
            viewHolder.tvHandle.setTextColor(mContext.getResources().getColor(R.color.text_gray_color));
            viewHolder.tvStatus.setText("");
            viewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.text_gray_color));
            viewHolder.imgViInvalid.setVisibility(View.VISIBLE);
        } else if (item.getStatus().equals("1")) {
            viewHolder.imgViInvalid.setVisibility(View.GONE);
            if (item.getMoney() != null) {
                viewHolder.tvHandle.setText("报价金额：" + item.getMoney() + "元");
                viewHolder.tvHandle.setTextColor(mContext.getResources().getColor(R.color.green));
                viewHolder.tvStatus.setText("");
                viewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.theme_color));
            } else {
                viewHolder.tvHandle.setText("未报价");
                viewHolder.tvHandle.setTextColor(mContext.getResources().getColor(R.color.orange_color));
                viewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.red_color));

                long endTime = HttpUtil.getTimeLong(item.getExpiry_date());
                if (endTime <= System.currentTimeMillis()) {
                    viewHolder.tvStatus.setText("已截止");
                } else {
                    //viewHolder.tvStatus.setText("进行中");
                    viewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.orange_color));
                    startCountDown(viewHolder, endTime);
                }

            }
        }
    }


    /**
     * 开始倒计时
     */
    private void startCountDown(final ViewHolder vh, long endTime) {
        long countDownTime = endTime - System.currentTimeMillis();
        if (countDownTime > 0) {
            vh.countDownTimer = new CountDownTimer(countDownTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long hours = millisUntilFinished / hMs;
                    long minutes = millisUntilFinished % hMs / mMs;
                    long seconds = millisUntilFinished % mMs / sMs;
                    vh.tvStatus.setText(dFormat.format(hours) + " : "
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
        @Bind(R.id.iv_logo)
        ImageView iv_logo;

        @Bind(R.id.tv_nickname)
        TextView tv_nickname;

        @Bind(R.id.tv_company_name)
        TextView tv_company_name;

        @Bind(R.id.tv_service)
        TextView tv_service;

        @Bind(R.id.tv_handle)//未处理或报价金额
                TextView tvHandle;
        @Bind(R.id.tv_status)//是否失效
                TextView tvStatus;

        @Bind(R.id.tv_create_time)
        TextView tv_create_time;

        @Bind(R.id.imgViInvalid)
        ImageView imgViInvalid;

        private CountDownTimer countDownTimer;

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
