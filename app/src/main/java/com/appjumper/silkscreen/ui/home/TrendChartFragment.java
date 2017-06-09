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

    @Bind(R.id.l_avg_list)
    LinearLayout l_avg_list;
    @Bind(R.id.tv_avg)
    TextView tv_avg;
    @Bind(R.id.tv_avg_diff)
    TextView tv_avg_diff;

    private String type;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trend_chart, container, false);
        ButterKnife.bind(this, view);
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
                data.put("product_id", type);
                response = JsonParser.getPriceDetailsResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.PRICEDETAILS));
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
        BaseFundChartView v_avg_list = new BaseFundChartView(getContext());

        List<String> l_x = new ArrayList<>();
        for (int i = -6; i <= 0; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, i);
            java.text.SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            String time = sdf.format(c.getTime());
            l_x.add(time);
        }
        v_avg_list.setDateX(l_x);

        List<Float> l_y = data.getAvg_list();
        List<List<Float>> dataXy = new ArrayList<>();
        dataXy.add(l_y);
        v_avg_list.setData(dataXy);

        float max = 0;
        for (int i = 0; i < l_y.size(); i++) {
            float val = l_y.get(i);
            if (max < val) {
                max = val;
            }
        }
        //max += 200;

        int temp = (int) max;
        String str = temp + "";
        str = str.substring(0, str.length() - 2);
        str = str + "00";
        max = Integer.parseInt(str) + 200;


        List<Float> datas = new ArrayList<>();
        datas.add((float) 0);
        datas.add(max / 5 * 1);
        datas.add(max / 5 * 2);
        datas.add(max / 5 * 3);
        datas.add(max / 5 * 4);
        datas.add(max);
        v_avg_list.setDateY(datas);

        l_avg_list.addView(v_avg_list);

        if (data.getOffer_list() != null && data.getOffer_list().size() > 0) {
            tv_avg.setText(data.getAvg() + data.getOffer_list().get(0).getOffer_unit());
        } else {
            tv_avg.setText(data.getAvg() + "元/吨");
        }
        tv_avg_diff.setText(data.getAvg_diff());
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
