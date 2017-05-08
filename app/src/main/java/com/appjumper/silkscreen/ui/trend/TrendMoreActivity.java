package com.appjumper.silkscreen.ui.trend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.PriceDetails;
import com.appjumper.silkscreen.bean.PriceDetailsResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.trend.adapter.DetailsListViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-12-14.
 */
public class TrendMoreActivity extends BaseActivity {
    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;
    @Bind(R.id.layout)
    LinearLayout layout;
    @Bind(R.id.list_view)
    MyListView listView;
    @Bind(R.id.tv_avg)
    TextView tv_avg;//均价

    @Bind(R.id.tv_avg_diff)
    TextView tv_avg_diff;//涨跌值

    @Bind(R.id.tv_count)
    TextView tv_count;//报价公司数
    private String product_id;
    private String count;
    private String avg;
    private String unit;
    private String avg_diff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_more);
        initBack();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        initTitle(intent.getStringExtra("title") + "报价");
        product_id = intent.getStringExtra("product_id");
        count = intent.getStringExtra("count");
        tv_count.setText(count + "家");
        avg = intent.getStringExtra("avg");
        unit = intent.getStringExtra("unit");
        tv_avg.setText(avg + unit);
        avg_diff = intent.getStringExtra("avg_diff");
        tv_avg_diff.setText(avg_diff);
        listerrefresh();
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        refresh();
    }

    private void initView(PriceDetails data) {
        listView.setAdapter(new DetailsListViewAdapter(TrendMoreActivity.this, data.getOffer_list()));
    }

    private void refresh() {
        mPullRefreshScrollView.setRefreshing();
        new Thread(run).start();
    }

    private void listerrefresh() {
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ObservableScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ObservableScrollView> refreshView) {
                new Thread(run).start();
            }
        });
    }

    //更多
    private Runnable run = new Runnable() {
        private PriceDetailsResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("product_id", product_id);
                response = JsonParser.getPriceDetailsResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.PRICE_MORE));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };
    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            mPullRefreshScrollView.onRefreshComplete();
            if (progress != null) {
                progress.dismiss();
            }
            TrendMoreActivity activity = (TrendMoreActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://更多
                    PriceDetailsResponse baseResponse = (PriceDetailsResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        layout.setVisibility(View.VISIBLE);
                        initView(baseResponse.getData());
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    activity.showErrorToast();
                    break;
            }
        }
    }
}
