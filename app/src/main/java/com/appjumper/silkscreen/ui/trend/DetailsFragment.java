package com.appjumper.silkscreen.ui.trend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
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
import com.appjumper.silkscreen.bean.TrendArticle;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.trend.adapter.ArticleAdapter;
import com.appjumper.silkscreen.ui.trend.adapter.DetailsListViewAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.BaseFundChartView;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;
import com.chad.library.adapter.base.BaseQuickAdapter;
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

    private List<TrendArticle> articleList;
    private ArticleAdapter articleAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_details, null);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    protected void initData() {
        initArticleView();

        type = getArguments().getString("type");
        mPullRefreshScrollView.scrollTo(0, 0);
        listView.setFocusable(false);
        refresh();
    }


    private void initArticleView() {
        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(R.layout.item_recycler_line_article, articleList);
        recyclerArticle.setLayoutManager(new LinearLayoutManager(context));
        articleAdapter.bindToRecyclerView(recyclerArticle);
        articleAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        articleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startForResult_Activity(context, TrendArticleAllActivity.class, 6, new BasicNameValuePair("type", type));
            }
        });
    }


    private void initView(final PriceDetails data) {
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
            tv_avg_diff.setTextColor(context.getResources().getColor(R.color.red_color));
            l_y.remove(l_y.size() - 1);
        } else {
            l_y.remove(0);
            if (data.getOffer_list() != null && data.getOffer_list().size() > 0) {
                tv_avg.setText(data.getAvg() + data.getOffer_list().get(0).getOffer_unit());
            } else {
                tv_avg.setText(data.getAvg() + "元/吨");
            }

            float diff = Float.valueOf(data.getAvg_diff());
            if (diff < 0) {
                tv_avg_diff.setText(data.getAvg_diff());
                tv_avg_diff.setTextColor(context.getResources().getColor(R.color.green_color));
            } else if (diff == 0) {
                tv_avg_diff.setText(data.getAvg_diff());
                tv_avg_diff.setTextColor(context.getResources().getColor(R.color.red_color));
            } else if (diff > 0) {
                tv_avg_diff.setText("+" + data.getAvg_diff());
                tv_avg_diff.setTextColor(context.getResources().getColor(R.color.red_color));
            }
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

        /*List<Float> datas = new ArrayList<>();
        datas.add((float) 0);
        datas.add(max / 5 * 1);
        datas.add(max / 5 * 2);
        datas.add(max / 5 * 3);
        datas.add(max / 5 * 4);
        datas.add(max);
        v_avg_list.setDateY(datas);*/

        l_avg_list.removeAllViews();
        l_avg_list.addView(v_avg_list);

        listView.setAdapter(new DetailsListViewAdapter(context, data.getOffer_list()));

        if (data.getCount() != null && !data.getCount().equals("")) {
            if (Integer.parseInt(data.getCount()) > 0) {
                l_count.setVisibility(View.VISIBLE);
                rl_company.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*start_Activity(getActivity(), TrendMoreActivity.class, new BasicNameValuePair("product_id", type), new BasicNameValuePair("title", data.getOffer_list().get(0).getMaterial_name())
                                , new BasicNameValuePair("count", data.getCount()), new BasicNameValuePair("avg", data.getAvg()), new BasicNameValuePair("unit", data.getOffer_list().get(0).getOffer_unit()),
                                new BasicNameValuePair("avg_diff", data.getAvg_diff()));*/

                        start_Activity(context, PriceMoreActivity.class, new BasicNameValuePair("id", type), new BasicNameValuePair("title", data.getOffer_list().get(0).getMaterial_name()));
                    }
                });
            } else {
                l_count.setVisibility(View.GONE);
            }
        }

        tv_count.setText(data.getCount() + "家");

    }


    private void refresh() {
        getArticle();
        new Thread(run).start();

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(run).start();
            }
        }, 400);*/


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
                data.put("g", "api");
                data.put("m", "price");
                //data.put("m", "home");
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

    private MyHandler handler = new MyHandler(getActivity());

    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (isDetached())
                return;

            if (mPullRefreshScrollView != null)
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
        params.put("type", type);
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<TrendArticle> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), TrendArticle.class);
                        articleList.clear();
                        if (list.size() > 5)
                            articleList.addAll(list.subList(0, 5));
                        else
                            articleList.addAll(list);
                        articleAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (articleList.size() == 0)
                    recyclerArticle.setVisibility(View.GONE);
                else
                    recyclerArticle.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6)
            getArticle();
    }
}
