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
import com.appjumper.silkscreen.bean.EquipmentCategory;
import com.appjumper.silkscreen.bean.EquipmentCategoryResponse;
import com.appjumper.silkscreen.bean.RecruitList;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.JobAdapter;
import com.appjumper.silkscreen.ui.home.adapter.AddRessRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.CityListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SelectRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SubListViewAdapter;
import com.appjumper.silkscreen.ui.home.recruit.RecruitDetailsActivity;
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
 * 搜索结果——招聘
 * Created by Botx on 2017/4/10.
 */

public class SearchJobFragment extends BaseFragment {

    @Bind(R.id.dropDownMenu)
    DropDownMenu dropDownMenu;

    private PtrClassicFrameLayout ptrResult;
    private RecyclerView recyclerResult;

    private JobAdapter resultAdapter;
    private List<RecruitList> resultList;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;
    private SearchResultsActivity activity;

    private LocalBroadcastManager broadcastManager;


    private String headers[] = {"职位", "地区"};
    private String name = "";//筛选 职位名称
    private String location = "";//筛选 地区名称
    private View productView;
    private MyRecyclerView recyclerView;
    private ListView list_view;
    private MyRecyclerView addRecyclerView;
    private ListView addResslistView;
    private SelectRecyclerAdapter positionAdapter;
    private AddRessRecyclerAdapter addRessRecycleradapter;
    private String pid;
    private String from_id;





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
        new Thread(positionRun).start();
    }


    /**
     * 初始化下拉菜单
     */
    private void initDropDownMenu() {
        //职位
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

        //地区
        final LinearLayout layoutAddress = new LinearLayout(context);
        layoutAddress.setOrientation(LinearLayout.VERTICAL);
        layoutAddress.setBackgroundResource(R.color.while_color);
        layoutAddress.setLayoutParams(params);
        layoutAddress.setOnClickListener(null);
        View formView = LayoutInflater.from(context).inflate(R.layout.layout_choice, null);
        addRecyclerView = (MyRecyclerView) formView.findViewById(R.id.recycler_view);
        addResslistView = (ListView) formView.findViewById(R.id.list_view);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addRecyclerView.setLayoutManager(LayoutManager);
        layoutAddress.addView(formView);

        List<View> popupViews = new ArrayList<>();
        popupViews.add(layoutProduct);
        popupViews.add(layoutAddress);

        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_content_search_result, null);
        ptrResult = (PtrClassicFrameLayout) contentView.findViewById(R.id.ptrResult);
        recyclerResult = (RecyclerView) contentView.findViewById(R.id.recyclerResult);

        dropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
    }


    private void initRecyclerView() {
        resultList = new ArrayList<>();
        recyclerResult.setLayoutManager(new LinearLayoutManager(context));

        resultAdapter = new JobAdapter(R.layout.item_recycler_dynamic_job, resultList);
        resultAdapter.bindToRecyclerView(recyclerResult);
        resultAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        resultAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!checkLogined())
                    return;
                start_Activity(context, RecruitDetailsActivity.class, new BasicNameValuePair("id", resultList.get(position).getId()));
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
        params.put("type", 7);
        params.put("keyworks", activity.keyworks);
        params.put("uid", getUserID());
        params.put("lat", activity.latitude + "");
        params.put("lng", activity.longitude + "");
        params.put("name", name);
        params.put("location", from_id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<RecruitList> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), RecruitList.class);
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


    private Runnable positionRun = new Runnable() {
        private EquipmentCategoryResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                response = JsonParser.getEquipmentCategoryResponse(HttpUtil.getMsg(
                        Url.JOB_TYPE));
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
                response = JsonParser.getAreaBeanResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SUB));
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
            if (isDetached())
                return;

            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_ERROR://职位选择
                    EquipmentCategoryResponse positionResponse = (EquipmentCategoryResponse) msg.obj;
                    if (positionResponse.getData() != null && positionResponse.getData().size() > 0) {
                        initRecyclerViewChoice(positionResponse.getData());
                        initListView(positionResponse.getData().get(0).getSublist());
                    } else {
                        showErrorToast(positionResponse.getError_desc());
                    }
                    break;
                case 7://市选择
                    AreaBeanResponse areaResponse = (AreaBeanResponse) msg.obj;
                    if (areaResponse.isSuccess()) {
                        List<AreaBean> areaItems = areaResponse.getData();
                        initRecyclerAddress(areaItems);
                        pid = areaResponse.getData().get(0).getId();
                        new Thread(addressCityRun).start();
                    } else {
                        activity.showErrorToast(areaResponse.getError_desc());
                    }
                    break;
                case 8://区选择
                    AreaBeanResponse cityResponse = (AreaBeanResponse) msg.obj;
                    if (cityResponse.isSuccess()) {
                        List<AreaBean> cityItems = cityResponse.getData();
                        initListAddress(cityItems);
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
     * 初始化职位RecyclerView
     *
     * @param data
     */
    private void initRecyclerViewChoice(final List<EquipmentCategory> data) {
        positionAdapter = new SelectRecyclerAdapter(context, data);
        recyclerView.setAdapter(positionAdapter);
        positionAdapter.setOnItemClickLitener(new SelectRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                initListView(data.get(position).getSublist());
            }
        });
    }

    /**
     * 初始化职位右边Listview
     *
     * @param data
     */
    private void initListView(final List<EquipmentCategory> data) {
        final SubListViewAdapter subListViewAdapter = new SubListViewAdapter(context, data);
        list_view.setAdapter(subListViewAdapter);
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

    /**
     * 初始化地址左边RecyclerView
     *
     * @param data
     */
    private void initRecyclerAddress(final List<AreaBean> data) {
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
     *
     * @param data
     */
    private void initListAddress(final List<AreaBean> data) {
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
