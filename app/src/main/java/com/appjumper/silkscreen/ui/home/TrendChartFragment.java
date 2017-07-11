package com.appjumper.silkscreen.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.PriceDetails;
import com.appjumper.silkscreen.bean.PriceDetailsResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.MainActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.BaseFundChartView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 走势图
 * Created by Botx on 2017/3/24.
 */

public class TrendChartFragment extends BaseFragment {

    /*@Bind(R.id.l_avg_list)
    LinearLayout l_avg_list;
    @Bind(R.id.tv_avg)
    TextView tv_avg;
    @Bind(R.id.tv_avg_diff)
    TextView tv_avg_diff;*/

    private LinearLayout l_avg_list;
    private TextView tv_avg;
    private TextView tv_avg_diff;

    private String type;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trend_chart, container, false);
        ButterKnife.bind(this, view);
        l_avg_list = (LinearLayout) view.findViewById(R.id.l_avg_list);
        tv_avg = (TextView) view.findViewById(R.id.tv_avg);
        tv_avg_diff = (TextView) view.findViewById(R.id.tv_avg_diff);
        return view;
    }


    @Override
    protected void initData() {
        type = getArguments().getString("type");
        new Thread(run).start();
    }

    private Runnable run = new Runnable() {

        public void run() {
            PriceDetailsResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("g", "api");
                //data.put("m", "price");
                data.put("m", "home");
                data.put("a", "details");
                data.put("product_id", type);

                //response = JsonParser.getPriceDetailsResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.PRICEDETAILS));
                response = JsonParser.getPriceDetailsResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(data)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_DATA_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    PriceDetailsResponse response = (PriceDetailsResponse) msg.obj;
                    if (response.isSuccess()) {
                        setChart(response.getData());
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private void setChart(PriceDetails data) {
        BaseFundChartView v_avg_list = new BaseFundChartView(context);

        //取横坐标日期的起始位置
        int start = -6;
        int end = 0;

        /*
         * 根据今日报价是否为0做出不同的判断
         */
        List<Float> l_y = data.getAvg_list();
        if (l_y.get(l_y.size() - 1) == 0) {
            start = -7;
            end = -1;
            tv_avg.setText("尚未出价");
            tv_avg_diff.setText("0");
            l_y.remove(l_y.size() - 1);
        } else {
            l_y.remove(0);
            if (data.getOffer_list() != null && data.getOffer_list().size() > 0) {
                tv_avg.setText(data.getAvg() + data.getOffer_list().get(0).getOffer_unit());
            } else {
                tv_avg.setText(data.getAvg() + "元/吨");
            }
            tv_avg_diff.setText(data.getAvg_diff());
        }

        List<List<Float>> dataXy = new ArrayList<>();
        dataXy.add(l_y);
        v_avg_list.setData(dataXy);

        /*
         * 计算横坐标日期
         */
        List<String> l_x = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, i);
            java.text.SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String time = sdf.format(c.getTime());
            l_x.add(time);
        }
        v_avg_list.setDateX(l_x);


        /*
         * 计算纵坐标的最小值和最大值
         */
        float max = 0;
        float min = 99999;
        for (int i = 0; i < l_y.size(); i++) {
            float val = l_y.get(i);
            if (max < val)
                max = val;

            if (min > val)
                min = val;
        }

        if (max == 0)
            max = Float.valueOf(data.getAvg());

        int offset = Integer.valueOf(data.getSpace_money());

        if (((int)max) % offset == 0) {
            max += offset;
        } else {
            String strMax = (int) max + "";
            strMax = strMax.substring(0, strMax.length() - 1);
            strMax = strMax + "0";
            int maxInt = Integer.valueOf(strMax);
            while (true) {
                maxInt += 10;
                if (maxInt % offset == 0)
                    break;
            }
            max = maxInt;
        }


        if (((int)min) % offset == 0) {
            min -= offset;
        } else {
            String strMin = (int) min + "";
            strMin = strMin.substring(0, strMin.length() - 1);
            strMin = strMin + "0";
            int minInt = Integer.valueOf(strMin);
            while (true) {
                if (minInt % offset == 0)
                    break;
                minInt -= 10;
            }
            min = minInt;
        }

        if (min < 0)
            min = 0;


        /*
         * 纵坐标分成5份，计算每份的值
         */
        int dif = (int) (max - min);
        int difAvg = dif / 5;

        List<Float> datas = new ArrayList<>();
        datas.add(min);
        datas.add(min + (difAvg * 1));
        datas.add(min + (difAvg * 2));
        datas.add(min + (difAvg * 3));
        datas.add(min + (difAvg * 4));
        datas.add(max);
        v_avg_list.setDateY(datas);

        l_avg_list.addView(v_avg_list);
    }



    @OnClick(R.id.txtDetail)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtDetail: //详情
                ((MainActivity)getActivity()).bottom_lly.check(R.id.rd_trend);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Const.ACTION_CHART_DETAIL);
                        intent.putExtra("type", type);
                        context.sendBroadcast(intent);
                    }
                }, 200);
                break;
            default:
                break;
        }
    }


}
