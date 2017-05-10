package com.appjumper.silkscreen.ui.trend;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.PriceDetails;
import com.appjumper.silkscreen.bean.PriceDetailsResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.trend.adapter.DetailsListViewAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.BaseFundChartView;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-09-19.
 * 原材价格具体界面
 */
public class DetailsFragment extends BaseFragment {
    private View mView;

    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;

    @Bind(R.id.list_view)
    MyListView listView;

    @Bind(R.id.tv_avg)
    TextView tv_avg;//均价

    @Bind(R.id.tv_avg_diff)
    TextView tv_avg_diff;//涨跌值

    @Bind(R.id.tv_count)
    TextView tv_count;//报价公司数

    @Bind(R.id.l_count)
    LinearLayout l_count;//查看更多公司

    @Bind(R.id.l_avg_list)
    LinearLayout l_avg_list;//曲线图

    @Bind(R.id.rl_company)
    RelativeLayout rl_company;//查看所有公司

    @Bind(R.id.recyclerArticle)
    RecyclerView recyclerArticle;

    private String type;
    private Context context;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getContext();
        mView = inflater.inflate(R.layout.fragment_details, null);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    protected void initData() {
        type = getArguments().getString("type");
        refresh();
        mPullRefreshScrollView.scrollTo(0, 0);
        listView.setFocusable(false);
    }


    private void initView(final PriceDetails data) {
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
        max += 200;
        List<Float> datas = new ArrayList<>();
        datas.add((float) 0);
        datas.add(max / 5 * 1);
        datas.add(max / 5 * 2);
        datas.add(max / 5 * 3);
        datas.add(max / 5 * 4);
        datas.add(max);
        v_avg_list.setDateY(datas);

        l_avg_list.addView(v_avg_list);

        listView.setAdapter(new DetailsListViewAdapter(context, data.getOffer_list()));
        if (data.getOffer_list() != null && data.getOffer_list().size() > 0) {
            tv_avg.setText(data.getAvg() + data.getOffer_list().get(0).getOffer_unit());
        } else {
            tv_avg.setText(data.getAvg() + "元/吨");
        }
        tv_avg_diff.setText(data.getAvg_diff());
        if (data.getCount() != null && !data.getCount().equals("")) {
            if (Integer.parseInt(data.getCount()) > 8) {
                l_count.setVisibility(View.VISIBLE);
                rl_company.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        start_Activity(getActivity(), TrendMoreActivity.class, new BasicNameValuePair("product_id", type), new BasicNameValuePair("title", data.getOffer_list().get(0).getMaterial_name())
                                , new BasicNameValuePair("count", data.getCount()), new BasicNameValuePair("avg", data.getAvg()), new BasicNameValuePair("unit", data.getOffer_list().get(0).getOffer_unit()),
                                new BasicNameValuePair("avg_diff", data.getAvg_diff()));
                    }
                });

            } else {
                l_count.setVisibility(View.GONE);
            }
        }

        tv_count.setText(data.getCount() + "家");

    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(context);
        super.onDestroy();
    }

    private void refresh() {
        new Thread(run).start();
        mPullRefreshScrollView.setOnRefreshListener(new com.appjumper.silkscreen.view.scrollView.PullToRefreshBase.OnRefreshListener2<ObservableScrollView>() {

            @Override
            public void onPullDownToRefresh(com.appjumper.silkscreen.view.scrollView.PullToRefreshBase<ObservableScrollView> refreshView) {
                new Thread(run).start();
                getArticle();
            }

            @Override
            public void onPullUpToRefresh(com.appjumper.silkscreen.view.scrollView.PullToRefreshBase<ObservableScrollView> refreshView) {
                mPullRefreshScrollView.onRefreshComplete();
            }


        });
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

    private MyHandler handler = new MyHandler(getActivity());

    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            mPullRefreshScrollView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    PriceDetailsResponse response = (PriceDetailsResponse) msg.obj;
                    if (response.isSuccess()) {
                        initView(response.getData());
                    } else {
                        showErrorToast(response.getError_desc());
                    }
                    break;
                default:
                    if (getActivity() != null) {
                        showErrorToast("数据获取失败");
                    }
                    break;
            }
        }
    }


    /**
     * 获取分析文章
     */
    private void getArticle() {
        RequestParams params = MyHttpClient.getApiParam("tender", "analysis_list");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        recyclerArticle.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                recyclerArticle.setVisibility(View.GONE);
            }
        });
    }

}
