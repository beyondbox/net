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
import com.appjumper.silkscreen.bean.AreaBean;
import com.appjumper.silkscreen.bean.AreaBeanResponse;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.WorkShopAdapter;
import com.appjumper.silkscreen.ui.home.adapter.AddRessRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.CityListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.GirdDropDownAdapter;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopDetailsActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 搜索结果——厂房
 * Created by Botx on 2017/4/10.
 */

public class SearchWorkshopFragment extends BaseFragment {

    @Bind(R.id.dropDownMenu)
    DropDownMenu dropDownMenu;

    private PtrClassicFrameLayout ptrResult;
    private RecyclerView recyclerResult;

    private WorkShopAdapter resultAdapter;
    private List<EquipmentList> resultList;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;
    private SearchResultsActivity activity;

    private LocalBroadcastManager broadcastManager;


    private String headers[] = {"全部位置", "全部面积", "全部价格"};
    private String citys[] = {"不限", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
    private String ages[] = {"不限", "0-2000m²", "2000-5000m²", "5000-10000m²", "10000-30000m²", "30000m²以上"};
    private String sexs[] = {"不限", "0-10000元/年", "10000-30000元/年", "30000-100000元/年", "100000元/年以上"};
    private GirdDropDownAdapter cityAdapter;
    private GirdDropDownAdapter agesAdapter;
    private GirdDropDownAdapter sexsAdapter;
    private AddRessRecyclerAdapter addRessRecycleradapter;
    private MyRecyclerView addRecyclerView;
    private ListView addResslistView;
    private String pid;
    private String from_id;
    private String max_area;
    private String min_area;
    private String min_price;
    private String max_price;



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
        new Thread(addressrun).start();
    }


    /**
     * 初始化下拉菜单
     */
    private void initDropDownMenu() {
        //init city menu
        ListView cityView = new ListView(context);
        cityAdapter = new GirdDropDownAdapter(context, Arrays.asList(citys));
        cityView.setDividerHeight(0);
        cityView.setAdapter(cityAdapter);

        final LinearLayout layoutProduct = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutProduct.setOrientation(LinearLayout.VERTICAL);
        layoutProduct.setBackgroundResource(R.color.while_color);
        layoutProduct.setLayoutParams(params);
        layoutProduct.setOnClickListener(null);
        View formView = LayoutInflater.from(context).inflate(R.layout.layout_choice, null);
        addRecyclerView = (MyRecyclerView)formView.findViewById(R.id.recycler_view);
        addResslistView = (ListView)formView.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addRecyclerView.setLayoutManager(linearLayoutManager);
        layoutProduct.addView(formView);

        ListView agesView = new ListView(context);
        agesAdapter = new GirdDropDownAdapter(context, Arrays.asList(ages));
        agesView.setDividerHeight(0);
        agesView.setAdapter(agesAdapter);
        //add item click event
        agesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        min_area="";
                        max_area="";
                        break;
                    case 1:
                        min_area="0";
                        max_area="2000";
                        break;
                    case 2:
                        min_area="2000";
                        max_area="5000";
                        break;
                    case 3:
                        min_area="5000";
                        max_area="10000";
                        break;
                    case 4:
                        min_area="10000";
                        max_area="30000";
                        break;
                    case 5:
                        min_area="30000";
                        max_area="";
                        break;
                }
                agesAdapter.setCheckItem(position);
                dropDownMenu.setTabText(position == 0 ? headers[1] : ages[position]);
                dropDownMenu.closeMenu();
                refresh();
            }
        });


        ListView sexsView = new ListView(context);
        sexsAdapter = new GirdDropDownAdapter(context, Arrays.asList(sexs));
        sexsView.setDividerHeight(0);
        sexsView.setAdapter(sexsAdapter);
        sexsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        min_price="";
                        max_price="";
                        break;
                    case 1:
                        min_price="0";
                        max_price="10000";
                        break;
                    case 2:
                        min_price="10000";
                        max_price="30000";
                        break;
                    case 3:
                        min_price="30000";
                        max_price="100000";
                        break;
                    case 4:
                        min_price="100000元";
                        max_price="";
                        break;
                }

                sexsAdapter.setCheckItem(position);
                dropDownMenu.setTabText(position == 0 ? headers[2] : sexs[position]);
                dropDownMenu.closeMenu();
                refresh();
            }
        });


        List<View> popupViews = new ArrayList<>();
        popupViews.add(layoutProduct);
        popupViews.add(agesView);
        popupViews.add(sexsView);

        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_content_search_result, null);
        ptrResult = (PtrClassicFrameLayout) contentView.findViewById(R.id.ptrResult);
        recyclerResult = (RecyclerView) contentView.findViewById(R.id.recyclerResult);

        dropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
    }


    private void initRecyclerView() {
        resultList = new ArrayList<>();
        recyclerResult.setLayoutManager(new LinearLayoutManager(context));

        resultAdapter = new WorkShopAdapter(R.layout.item_recycler_dynamic_workshop, resultList);
        resultAdapter.bindToRecyclerView(recyclerResult);
        resultAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        resultAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!checkLogined())
                    return;
                start_Activity(context, WorkshopDetailsActivity.class, new BasicNameValuePair("id", resultList.get(position).getId()));
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
        params.put("type", 6);
        params.put("keyworks", activity.keyworks);
        params.put("uid", getUserID());
        params.put("lat", getLat());
        params.put("lng", getLng());
        params.put("min_area", min_area);
        params.put("max_area", max_area);
        params.put("min_price", min_price);
        params.put("max_price", max_price);
        params.put("location",from_id);

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



    private Runnable addressrun = new Runnable() {
        public void run() {
            AreaBeanResponse response = null;
            Map<String, String> data = new HashMap<String, String>();
            data.put("pid", "208");
            try {
                response = JsonParser.getAreaBeanResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SUB));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        7, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    private Runnable addressCityRun = new Runnable() {
        public void run() {
            AreaBeanResponse response = null;
            Map<String, String> data = new HashMap<String, String>();
            data.put("pid", pid);
            try {
                response = JsonParser.getAreaBeanResponse(HttpUtil.postMsg(HttpUtil.getData(data),Url.SUB ));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        8, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isViewCreated) return;

            switch (msg.what) {
                case  7://市选择
                    AreaBeanResponse areaResponse = (AreaBeanResponse) msg.obj;
                    if (areaResponse.isSuccess()) {
                        List<AreaBean> areaItems = areaResponse.getData();
                        initRecyclerViewChoice(areaItems);
                        pid = areaResponse.getData().get(0).getId();
                        new Thread(addressCityRun).start();
                    } else {
                        activity.showErrorToast(areaResponse.getError_desc());
                    }
                    break;
                case  8://区选择
                    AreaBeanResponse cityResponse = (AreaBeanResponse) msg.obj;
                    if (cityResponse.isSuccess()) {
                        List<AreaBean> cityItems = cityResponse.getData();
                        initListView(cityItems);
                    } else {
                        activity.showErrorToast(cityResponse.getError_desc());
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 初始化地址左边RecyclerView
     * @param data
     */
    private void initRecyclerViewChoice(final List<AreaBean> data) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addRecyclerView.setLayoutManager(linearLayoutManager);
        addRessRecycleradapter = new AddRessRecyclerAdapter(context, data);
        addRecyclerView.setAdapter(addRessRecycleradapter);
        addRessRecycleradapter.setOnItemClickLitener(new AddRessRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                pid = data.get(position).getId();
                new Thread(addressCityRun).start();
            }
        });
    }


    /**
     * 初始化地址右边Listview
     * @param data
     */
    private void initListView(final List<AreaBean> data) {
        final CityListViewAdapter adapter = new CityListViewAdapter(context, data);
        addResslistView.setAdapter(adapter);
        addResslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                from_id = data.get(position).getId();
                adapter.setCurrentSelectPosition(position);
                adapter.notifyDataSetChanged();
                dropDownMenu.setTabText(data.get(position).getShortname());
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
