package com.appjumper.silkscreen.ui.dynamic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.HotInquiry;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.AskBuyFilterAdapter;
import com.appjumper.silkscreen.ui.dynamic.adapter.AskBuyListAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 求购
 * Created by Botx on 2017/10/17.
 */

public class AskBuyFragment extends BaseFragment {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;
    @Bind(R.id.llAll)
    LinearLayout llAll;
    @Bind(R.id.markAll)
    View markAll;
    @Bind(R.id.recyclerFilter)
    RecyclerView recyclerFilter;

    private List<AskBuy> dataList;
    private AskBuyListAdapter adapter;

    private List<HotInquiry> filterList;
    private AskBuyFilterAdapter filterAdapter;

    private int page = 1;
    private int pageSize = 30;
    private int totalSize;
    private String productId = "";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_askbuy, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        initRecyclerView();
        initRefreshLayout();
        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 50);
    }


    private void initRecyclerView() {
        dataList = new ArrayList<>();
        filterList = new ArrayList<>();

        adapter = new AskBuyListAdapter(R.layout.item_recycler_ask_buy_list, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                start_Activity(context, AskBuyDetailActivity.class, new BasicNameValuePair("id", dataList.get(position).getId()));
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.txtOffer:
                        if (checkLogined()) {
                            if (!MyApplication.appContext.checkMobile(context)) return;
                            start_Activity(context, ReleaseOfferActivity.class, new BasicNameValuePair("id", dataList.get(position).getId()), new BasicNameValuePair(Const.KEY_UNIT, dataList.get(position).getPurchase_unit()));
                        }
                        break;
                }
            }
        });

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getData();
            }
        }, recyclerData);

        adapter.setEnableLoadMore(false);


        filterAdapter = new AskBuyFilterAdapter(R.layout.item_recycler_askbuy_filter, filterList);
        recyclerFilter.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        filterAdapter.bindToRecyclerView(recyclerFilter);
        filterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                filterAdapter.changeSelected(position);
                recyclerFilter.smoothScrollToPosition(position);
                llAll.setSelected(false);
                markAll.setVisibility(View.INVISIBLE);
                productId = filterList.get(position).getProduct_id();
                ptrLayt.autoRefresh();
            }
        });
        llAll.setSelected(true);
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrLayt.disableWhenHorizontalMove(true);
        ptrLayt.setLastUpdateTimeRelateObject(this);
        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getFilter();
                page = 1;
                getData();
            }
        });
    }


    /**
     * 获取顶部筛选数据
     */
    private void getFilter() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "purchase_product_list");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<HotInquiry> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), HotInquiry.class);
                        filterList.clear();
                        filterList.addAll(list);
                        filterAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "purchase_list");
        params.put("product_id", productId);
        params.put("page", page);
        params.put("pagesize", pageSize);
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
                        List<AskBuy> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), AskBuy.class);
                        totalSize = dataObj.optInt("total");

                        if (page == 1) {
                            dataList.clear();
                            recyclerData.smoothScrollToPosition(0);
                        }
                        dataList.addAll(list);
                        adapter.notifyDataSetChanged();

                        if (dataList.size() < totalSize)
                            adapter.setEnableLoadMore(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (!isViewCreated) return;
                showFailTips(getResources().getString(R.string.requst_fail));
                if (page > 1)
                    page--;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isViewCreated) return;

                ptrLayt.refreshComplete();
                adapter.loadMoreComplete();
                if (totalSize == dataList.size())
                    adapter.loadMoreEnd();

                adapter.setEmptyView(R.layout.layout_empty_view_common);
            }
        });
    }


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_ADD_READ_NUM);
        filter.addAction(Const.ACTION_RELEASE_SUCCESS);
        getActivity().registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_ADD_READ_NUM)) {
                String askId = intent.getStringExtra("id");
                for (int i = 0; i < dataList.size(); i++) {
                    AskBuy askBuy = dataList.get(i);
                    if (askId.equals(askBuy.getId())) {
                        dataList.get(i).setConsult_num((Integer.valueOf(askBuy.getConsult_num()) + 1) + "");
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else if (action.equals(Const.ACTION_RELEASE_SUCCESS)) {
                getFilter();
                page = 1;
                getData();
            }
        }
    };


    @OnClick({R.id.llAll, R.id.imgViCall})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llAll: //全部
                llAll.setSelected(true);
                markAll.setVisibility(View.VISIBLE);
                filterAdapter.changeSelected(-1);
                productId = "";
                ptrLayt.autoRefresh();
                break;
            case R.id.imgViCall: //联系客服
                AppTool.dial(context, Const.SERVICE_PHONE_ASKBUY);
                break;
        }
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(myReceiver);
    }

}
