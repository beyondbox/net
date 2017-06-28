package com.appjumper.silkscreen.ui.dynamic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.ProductAdapter;
import com.appjumper.silkscreen.ui.home.process.ProcessingDetailsActivity;
import com.appjumper.silkscreen.ui.home.stock.StockDetailActivity;
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

/**
 * 产品
 * Created by Botx on 2017/5/17.
 */

public class ProductFragment extends BaseFragment {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;

    private List<Product> dataList;
    private ProductAdapter adapter;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;

    private LocalBroadcastManager broadcastManager;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic_data, container, false);
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

        adapter = new ProductAdapter(R.layout.item_recycler_dynamic_product, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (dataList.get(position).getType().equals(Const.SERVICE_TYPE_STOCK + "")) {
                    start_Activity(context, StockDetailActivity.class, new BasicNameValuePair("title", dataList.get(position).getProduct_name() + dataList.get(position).getService_type_name()), new BasicNameValuePair("id", dataList.get(position).getId()));
                } else {
                    start_Activity(context, ProcessingDetailsActivity.class,
                            new BasicNameValuePair("title", dataList.get(position).getProduct_name() + dataList.get(position).getService_type_name()),
                            new BasicNameValuePair("id", dataList.get(position).getId()));
                }

                dataList.get(position).setIs_read(true);
                adapter.notifyDataSetChanged();
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
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrLayt.setLastUpdateTimeRelateObject(this);
        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 1;
                getData();
            }
        });
    }




    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_DYNAMIC_REFRESH);
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_DYNAMIC_REFRESH)) {
                if (isDataInited) {
                    ptrLayt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ptrLayt.autoRefresh();
                        }
                    }, 50);
                }
            }
        }
    };



    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(myReceiver);
        super.onDestroy();
    }





    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("service", "collection_product");
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
                        List<Product> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), Product.class);
                        totalSize = dataObj.optInt("total");

                        if (page == 1) {
                            dataList.clear();
                            recyclerData.smoothScrollToPosition(0);
                        }

                        dataList.addAll(list);
                        adapter.notifyDataSetChanged();

                        if (dataList.size() < totalSize) {
                            if (list.size() > 0)
                                adapter.setEnableLoadMore(true);
                            else
                                adapter.setEnableLoadMore(false);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
                if (page > 1)
                    page--;
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (context.isDestroyed())
                    return;

                ptrLayt.refreshComplete();
                adapter.loadMoreComplete();
                if (totalSize <= dataList.size())
                    adapter.loadMoreEnd();

                adapter.setEmptyView(R.layout.layout_empty_view_common);
            }
        });


    }


}
