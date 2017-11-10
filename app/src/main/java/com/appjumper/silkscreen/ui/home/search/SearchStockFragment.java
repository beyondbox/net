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
import android.widget.AdapterView;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.GirdDropDownAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SearchProductAdapter;
import com.appjumper.silkscreen.ui.home.stock.StockDetailActivity;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yyydjk.library.DropDownMenu;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索结果——现货
 * Created by Botx on 2017/4/10.
 */

public class SearchStockFragment extends BaseFragment {

    @Bind(R.id.dropDownMenu)
    DropDownMenu dropDownMenu;

    @Bind(R.id.menuHolder)
    View menuHolder;

    private PtrClassicFrameLayout ptrResult;
    private RecyclerView recyclerResult;

    private SearchProductAdapter resultAdapter;
    private List<Product> resultList;

    private int auth = 0;
    private String [] companyArr = {"全部公司", "认证公司", "未认证公司"};

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
        View view = inflater.inflate(R.layout.fragment_search_order, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        activity = (SearchResultsActivity) getActivity();
        initDropDownMenu();
        initRecyclerView();
        initRefreshLayout();

        ptrResult.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrResult.autoRefresh();
            }
        }, 50);
    }


    /**
     * 初始化下拉菜单
     */
    private void initDropDownMenu() {
        List<String> tabTexts = new ArrayList<>();
        tabTexts.add("全部公司");
        tabTexts.add("");
        tabTexts.add("");

        ListView companyView = new ListView(context);
        final GirdDropDownAdapter companyAdapter = new GirdDropDownAdapter(context, Arrays.asList(companyArr));
        companyView.setDividerHeight(0);
        companyView.setAdapter(companyAdapter);
        companyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                companyAdapter.setCheckItem(position);
                auth = position;
                dropDownMenu.setTabText(companyArr[position]);
                dropDownMenu.closeMenu();

                ptrResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrResult.autoRefresh();
                    }
                }, 50);
            }
        });

        List<View> popupViews = new ArrayList<>();
        popupViews.add(companyView);
        popupViews.add(new View(context));
        popupViews.add(new View(context));

        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_content_search_result, null);
        ptrResult = (PtrClassicFrameLayout) contentView.findViewById(R.id.ptrResult);
        recyclerResult = (RecyclerView) contentView.findViewById(R.id.recyclerResult);

        dropDownMenu.setDropDownMenu(tabTexts, popupViews, contentView);
    }


    private void initRecyclerView() {
        resultList = new ArrayList<>();
        recyclerResult.setLayoutManager(new LinearLayoutManager(context));

        resultAdapter = new SearchProductAdapter(R.layout.item_processing_listview, resultList);
        resultAdapter.bindToRecyclerView(recyclerResult);
        resultAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        resultAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!checkLogined())
                    return;
                start_Activity(context, StockDetailActivity.class,
                        new BasicNameValuePair("title", resultList.get(position).getProduct_name() + resultList.get(position).getService_type_name()),
                        new BasicNameValuePair("id", resultList.get(position).getId()));
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


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("service", "service_list");
        //RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("type", Const.SERVICE_TYPE_STOCK);
        params.put("keyworks", activity.keyworks);
        params.put("uid", getUserID());
        params.put("lat", getLat());
        params.put("lng", getLng());
        params.put("auth", auth);

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
    public void onDestroy() {
        broadcastManager.unregisterReceiver(myReceiver);
        super.onDestroy();
    }


    @OnClick(R.id.menuHolder)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuHolder:
                break;
            default:
                break;
        }
    }

}
