package com.appjumper.silkscreen.ui.home.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.EquipmentCategory;
import com.appjumper.silkscreen.bean.EquipmentCategoryResponse;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.DeviceAdapter;
import com.appjumper.silkscreen.ui.home.adapter.GirdDropDownAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SelectRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SubListViewAdapter;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentDetailsActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.MyRecyclerView;
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

/**
 * 搜索结果——设备
 * Created by Botx on 2017/4/10.
 */

public class SearchDeviceFragment extends BaseFragment {

    @Bind(R.id.dropDownMenu)
    DropDownMenu dropDownMenu;

    private PtrClassicFrameLayout ptrResult;
    private RecyclerView recyclerResult;

    private DeviceAdapter resultAdapter;
    private List<EquipmentList> resultList;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;
    private SearchResultsActivity activity;

    private LocalBroadcastManager broadcastManager;


    private String headers[] = {"全部设备", "新旧程度"};
    private String old_rate[] = {"全部", "全新", "二手"};
    private String name = "";//筛选 设备名称
    private String new_old_rate = "";//筛选 新旧

    private GirdDropDownAdapter oldAdapter;
    private SelectRecyclerAdapter choiceadapter;
    private View productView;
    private MyRecyclerView recyclerView;
    private ListView list_view;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_device, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        activity = (SearchResultsActivity) getActivity();
        initDropDownMenu();
        initRecyclerView();
        initRefreshLayout();

        refresh();
        new Thread(choiceRun).start();
    }


    /**
     * 初始化下拉菜单
     */
    private void initDropDownMenu() {
        //全部设备
        final LinearLayout layoutProduct = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutProduct.setOrientation(LinearLayout.VERTICAL);
        layoutProduct.setBackgroundResource(R.color.while_color);
        layoutProduct.setLayoutParams(params);
        layoutProduct.setOnClickListener(null);
        productView = LayoutInflater.from(context).inflate(R.layout.layout_choice, null);
        recyclerView = (MyRecyclerView) productView.findViewById(R.id.recycler_view);
        list_view = (ListView) productView.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        layoutProduct.addView(productView);


        //新旧程度
        final ListView oldView = new ListView(context);
        oldAdapter = new GirdDropDownAdapter(context, Arrays.asList(old_rate));
        oldView.setDividerHeight(0);
        oldView.setAdapter(oldAdapter);
        oldView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                oldAdapter.setCheckItem(i);
                dropDownMenu.setTabText(old_rate[i]);
                dropDownMenu.closeMenu();
                new_old_rate = old_rate[i];
                refresh();
            }
        });

        List<View> popupViews = new ArrayList<>();
        popupViews.add(layoutProduct);
        popupViews.add(oldView);

        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_content_search_result, null);
        ptrResult = (PtrClassicFrameLayout) contentView.findViewById(R.id.ptrResult);
        recyclerResult = (RecyclerView) contentView.findViewById(R.id.recyclerResult);

        dropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
    }


    private void initRecyclerView() {
        resultList = new ArrayList<>();
        recyclerResult.setLayoutManager(new LinearLayoutManager(context));

        resultAdapter = new DeviceAdapter(R.layout.item_recycler_dynamic_device, resultList);
        resultAdapter.bindToRecyclerView(recyclerResult);
        resultAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        resultAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!checkLogined())
                    return;
                start_Activity(context, EquipmentDetailsActivity.class, new BasicNameValuePair("id", resultList.get(position).getId()));
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
        params.put("type", 5);
        params.put("keyworks", activity.keyworks);
        params.put("uid", getUserID());
        params.put("lat", getLat());
        params.put("lng", getLng());
        params.put("name", name);
        params.put("new_old_rate", new_old_rate);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<EquipmentList> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), EquipmentList.class);
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



    private Runnable choiceRun = new Runnable() {
        private EquipmentCategoryResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                response = JsonParser.getEquipmentCategoryResponse(HttpUtil.getMsg(
                        Url.EQUIPMENT_CATEGORY));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_DATA_ERROR, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };



    private Handler handler = new Handler()  {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isViewCreated) return;

            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_ERROR:
                    EquipmentCategoryResponse choiceResponse = (EquipmentCategoryResponse) msg.obj;
                    if (choiceResponse.getData() != null && choiceResponse.getData().size() > 0) {
                        initRecyclerViewChoice(choiceResponse.getData());
                        initListView(choiceResponse.getData().get(0).getSublist());
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private void initRecyclerViewChoice(final List<EquipmentCategory> data) {
        choiceadapter = new SelectRecyclerAdapter(context, data);
        recyclerView.setAdapter(choiceadapter);
        choiceadapter.setOnItemClickLitener(new SelectRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                initListView(data.get(position).getSublist());
            }
        });
    }

    private void initListView(final List<EquipmentCategory> data) {
        final SubListViewAdapter subListViewAdapter = new SubListViewAdapter(context, data);
        list_view.setAdapter(subListViewAdapter);
        subListViewAdapter.setCurrentSelectPosition(-1);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                name = data.get(position).getName();
                subListViewAdapter.setCurrentSelectPosition(position);
                subListViewAdapter.notifyDataSetChanged();
                dropDownMenu.setTabText(data.get(position).getName());
                dropDownMenu.closeMenu();
                refresh();
            }
        });
    }



    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(myReceiver);
        super.onDestroy();
    }


}
