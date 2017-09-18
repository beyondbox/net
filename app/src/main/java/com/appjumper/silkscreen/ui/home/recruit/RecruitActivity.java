package com.appjumper.silkscreen.ui.home.recruit;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.AreaBean;
import com.appjumper.silkscreen.bean.AreaBeanResponse;
import com.appjumper.silkscreen.bean.EquipmentCategory;
import com.appjumper.silkscreen.bean.EquipmentCategoryResponse;
import com.appjumper.silkscreen.bean.RecruitList;
import com.appjumper.silkscreen.bean.RecruitListResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.adapter.AddRessRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.CityListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.RecruitListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SelectRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SubListViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.appjumper.silkscreen.view.pulltorefresh.PagedListView;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshBase;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshPagedListView;
import com.yyydjk.library.DropDownMenu;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/11/11.
 * 招聘
 */
public class RecruitActivity extends BaseActivity {
    @Bind(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    private String headers[] = {"职位", "地区"};
    private List<View> popupViews = new ArrayList<>();
    private SelectRecyclerAdapter positionAdapter;

    private PullToRefreshPagedListView pullToRefreshView;
    private PagedListView listView;
    private View mEmptyLayout;

    private View productView;
    private MyRecyclerView recyclerView;
    private ListView list_view;
    private String pagesize = "20";
    private int pageNumber = 1;
    private String name = "";//筛选 职位名称
    private String location = "";//筛选 地区名称

    private RecruitListViewAdapter adapter;
    private List<RecruitList> list;
    private MyRecyclerView addRecyclerView;
    private ListView addResslistView;
    private AddRessRecyclerAdapter addRessRecycleradapter;
    private String pid;
    private String from_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);
        ButterKnife.bind(this);
        initBack();
        initTitle("招聘");
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if(checkLogined()) {
                    //start_Activity(RecruitActivity.this, RecruitReleaseActivity.class);
                    CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_JOB);
                }
            }
        });
        new Thread(addressrun).start();
        new Thread(positionRun).start();
        initView();
    }

    private void initView() {
        //职位
        final LinearLayout layoutProduct = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutProduct.setOrientation(LinearLayout.VERTICAL);
        layoutProduct.setBackgroundResource(R.color.while_color);
        layoutProduct.setLayoutParams(params);
        layoutProduct.setOnClickListener(null);
        productView = LayoutInflater.from(this).inflate(R.layout.layout_choice, null);
        recyclerView = (MyRecyclerView) productView.findViewById(R.id.recycler_view);
        list_view = (ListView) productView.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        layoutProduct.addView(productView);
        popupViews.add(layoutProduct);

        //地区
        final LinearLayout layoutAddress = new LinearLayout(this);
        layoutAddress.setOrientation(LinearLayout.VERTICAL);
        layoutAddress.setBackgroundResource(R.color.while_color);
        layoutAddress.setLayoutParams(params);
        layoutAddress.setOnClickListener(null);
        View formView = LayoutInflater.from(this).inflate(R.layout.layout_choice, null);
        addRecyclerView = (MyRecyclerView) formView.findViewById(R.id.recycler_view);
        addResslistView = (ListView) formView.findViewById(R.id.list_view);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addRecyclerView.setLayoutManager(LayoutManager);
        layoutAddress.addView(formView);
        popupViews.add(layoutAddress);

        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_processing_listview, null);
        pullToRefreshView = (PullToRefreshPagedListView) contentView.findViewById(R.id.listview);
        listView = pullToRefreshView.getRefreshableView();
        listView.onFinishLoading(false);
        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);

        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
        initListener();
        refresh();

    }

    /**
     * 初始化职位RecyclerView
     *
     * @param data
     */
    private void initRecyclerView(final List<EquipmentCategory> data) {
        positionAdapter = new SelectRecyclerAdapter(this, data);
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
        final SubListViewAdapter subListViewAdapter = new SubListViewAdapter(this, data);
        list_view.setAdapter(subListViewAdapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                name = data.get(position).getName();
                subListViewAdapter.setCurrentSelectPosition(position);
                subListViewAdapter.notifyDataSetChanged();
                mDropDownMenu.setTabText(data.get(position).getName());
                mDropDownMenu.closeMenu();
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addRecyclerView.setLayoutManager(linearLayoutManager);
        addRessRecycleradapter = new AddRessRecyclerAdapter(this, data);
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
        final CityListViewAdapter adapter = new CityListViewAdapter(this, data);
        addResslistView.setAdapter(adapter);
        addResslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                from_id = data.get(position).getId();
                adapter.setCurrentSelectPosition(position);
                adapter.notifyDataSetChanged();
                new Thread(run).start();
                mDropDownMenu.setTabText(data.get(position).getShortname());
                mDropDownMenu.closeMenu();
            }
        });

    }

    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!checkLogined())
                    return;
                start_Activity(RecruitActivity.this, RecruitDetailsActivity.class, new BasicNameValuePair("id", list.get(position - 1).getId()));
            }
        });
        listView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

            @Override
            public void onLoadMoreItems() {
                new Thread(pageRun).start();
            }
        });
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                new Thread(run).start();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
    }

    private Runnable run = new Runnable() {

        public void run() {
            RecruitListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("name", name);
                data.put("location", from_id);
                data.put("pagesize", pagesize);
                data.put("page", "1");
                response = JsonParser.getRecruitListResponse(HttpUtil.getMsg(Url.RECRUIT_LIST + "?" + HttpUtil.getData(data)));
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

    private Runnable pageRun = new Runnable() {
        @SuppressWarnings("unchecked")
        public void run() {
            RecruitListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("name", name);
                data.put("location", from_id);
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                response = JsonParser.getRecruitListResponse(HttpUtil.getMsg(Url.RECRUIT_LIST + "?" + HttpUtil.getData(data)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
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
    public MyHandler handler = new MyHandler(this);

    public class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final RecruitActivity activity = (RecruitActivity) reference.get();
            if (activity == null) {
                return;
            }

            if (isDestroyed())
                return;

            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    RecruitListResponse response = (RecruitListResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new RecruitListViewAdapter(activity, list);
                        activity.listView.onFinishLoading(response.getData().hasMore());
                        activity.listView.setAdapter(activity.adapter);
                        activity.pageNumber = 2;
                        activity.pullToRefreshView.setEmptyView(activity.list.isEmpty() ? activity.mEmptyLayout : null);
                    } else {
                        activity.listView.onFinishLoading(false);
                        activity.showErrorToast(response.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    RecruitListResponse pageResponse = (RecruitListResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<RecruitList> tempList = pageResponse.getData()
                                .getItems();
                        activity.list.addAll(tempList);
                        activity.adapter.notifyDataSetChanged();
                        activity.listView.onFinishLoading(pageResponse.getData().hasMore());
                        activity.pageNumber++;
                    } else {
                        activity.listView.onFinishLoading(false);
                        activity.showErrorToast(pageResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR://职位选择
                    EquipmentCategoryResponse positionResponse = (EquipmentCategoryResponse) msg.obj;
                    if (positionResponse.getData() != null && positionResponse.getData().size() > 0) {
                        initRecyclerView(positionResponse.getData());
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
                    activity.showErrorToast();
                    activity.listView.onFinishLoading(false);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}
