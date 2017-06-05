package com.appjumper.silkscreen.ui.home.search;

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
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;
import com.appjumper.silkscreen.ui.dynamic.adapter.LogisticsAdapter;
import com.appjumper.silkscreen.ui.home.logistics.LogisticsDetailsActivity;
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
 * 搜索结果——物流
 * Created by Botx on 2017/4/10.
 */

public class SearchLogisticsFragment extends BaseFragment {

    @Bind(R.id.ptrResult)
    PtrClassicFrameLayout ptrResult;
    @Bind(R.id.recyclerResult)
    RecyclerView recyclerResult;
    @Bind(R.id.tv_start)//出发地
    TextView tv_start;
    @Bind(R.id.tv_end)//终点
    TextView tv_end;


    private LogisticsAdapter resultAdapter;
    private List<LineList> resultList;

    private String start_id = "";
    private String end_id = "";

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;
    private SearchResultsActivity activity;

    private LocalBroadcastManager broadcastManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_logistics, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        activity = (SearchResultsActivity) getActivity();
        initRecyclerView();
        initRefreshLayout();

        ptrResult.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrResult.autoRefresh();
            }
        }, 50);
    }


    private void initRecyclerView() {
        resultList = new ArrayList<>();
        recyclerResult.setLayoutManager(new LinearLayoutManager(context));

        resultAdapter = new LogisticsAdapter(R.layout.item_recycler_dynamic_product, resultList);
        resultAdapter.bindToRecyclerView(recyclerResult);
        resultAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        resultAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!checkLogined())
                    return;
                start_Activity(context, LogisticsDetailsActivity.class, new BasicNameValuePair("id", resultList.get(position).getId()),
                        new BasicNameValuePair("type", resultList.get(position).getType()));
            }
        });

        resultAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getData();
            }
        }, recyclerResult);

        resultAdapter.setEnableLoadMore(false);
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrResult.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 1;
                getData();
            }
        });
    }



    private void refresh() {
        ptrResult.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrResult.autoRefresh();
            }
        }, 50);
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("service", "service_list");
        //RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("type", 4);
        params.put("keyworks", activity.keyworks);
        params.put("uid", getUserID());
        params.put("lat", activity.latitude + "");
        params.put("lng", activity.longitude + "");
        params.put("from", start_id);
        params.put("to", end_id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<LineList> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), LineList.class);
                        totalSize = dataObj.optInt("total");

                        if (page == 1) {
                            resultList.clear();
                            recyclerResult.smoothScrollToPosition(0);
                        }
                        resultList.addAll(list);
                        resultAdapter.notifyDataSetChanged();

                        if (resultList.size() < totalSize)
                            resultAdapter.setEnableLoadMore(true);
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

                ptrResult.refreshComplete();
                resultAdapter.loadMoreComplete();
                if (totalSize == resultList.size())
                    resultAdapter.loadMoreEnd();

                resultAdapter.setEmptyView(R.layout.layout_empty_view_search);
            }
        });
    }


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_SEARCHING_REFRESH);
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_SEARCHING_REFRESH)) {
                if (isDataInited) {
                    ptrResult.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ptrResult.autoRefresh();
                        }
                    }, 50);
                }
            }
        }
    };




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        switch (requestCode) {
            case 1://出发地
                start_id = data.getStringExtra("id");
                String start_name = data.getStringExtra("name");
                tv_start.setText(start_name);
                tv_start.setCompoundDrawables(null, null, null, null);
                refresh();
                break;
            case 2://目的地
                end_id = data.getStringExtra("id");
                String end_name = data.getStringExtra("name");
                tv_end.setText(end_name);
                tv_end.setCompoundDrawables(null, null, null, null);
                refresh();
                break;
            default:
                break;
        }
    }





    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(myReceiver);
        super.onDestroy();
    }


    @OnClick({R.id.tv_start, R.id.tv_end})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start:
                startForResult_Activity(context, AddressSelectActivity.class, 1, new BasicNameValuePair("code", "1"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.tv_end:
                startForResult_Activity(context, AddressSelectActivity.class, 2, new BasicNameValuePair("code", "2"), new BasicNameValuePair("type", "1"));
                break;
            default:
                break;
        }
    }


}
