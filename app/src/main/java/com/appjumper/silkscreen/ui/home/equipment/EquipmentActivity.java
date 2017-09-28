package com.appjumper.silkscreen.ui.home.equipment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.EquipmentCategory;
import com.appjumper.silkscreen.bean.EquipmentCategoryResponse;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.bean.EquipmentListResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.EquipmentListviewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.GirdDropDownAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SelectRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.SubListViewAdapter;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/11/11.
 * 设备出售
 */
public class EquipmentActivity extends BaseActivity {
    @Bind(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    private String headers[] = {"全部设备", "新旧程度"};
    private String old_rate[] = {"全部", "全新", "二手"};
    private List<View> popupViews = new ArrayList<>();
    private GirdDropDownAdapter oldAdapter;
    private SelectRecyclerAdapter choiceadapter;


    private PullToRefreshPagedListView pullToRefreshView;

    private View productView;
    private MyRecyclerView recyclerView;
    private ListView list_view;
    private String name = "";//筛选 设备名称
    private String new_old_rate = "";//筛选 新旧
    private PagedListView listView;
    private View mEmptyLayout;
    private String pagesize = "20";
    private int pageNumber = 1;

    private EquipmentListviewAdapter adapter;
    private List<EquipmentList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);
        ButterKnife.bind(this);
        initBack();
        initTitle("设备出售");
        registerBroadcastReceiver();
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if(checkLogined()) {
                    //start_Activity(EquipmentActivity.this, EquipmentReleaseActivity.class);
                    CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_DEVICE);
                }
            }
        });
        new Thread(choiceRun).start();
        initView();

    }

    private void initView() {
        //全部设备
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

        //新旧程度
        final ListView oldView = new ListView(this);
        oldAdapter = new GirdDropDownAdapter(this, Arrays.asList(old_rate));
        oldView.setDividerHeight(0);
        oldView.setAdapter(oldAdapter);
        oldView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                oldAdapter.setCheckItem(i);
                mDropDownMenu.setTabText(old_rate[i]);
                mDropDownMenu.closeMenu();
                new_old_rate = old_rate[i];
                refresh();
            }
        });
        popupViews.add(oldView);

        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_equipment_listview, null);
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
     * 初始化RecyclerView
     *
     * @param data
     */
    private void initRecyclerView(final List<EquipmentCategory> data) {
        choiceadapter = new SelectRecyclerAdapter(this, data);
        recyclerView.setAdapter(choiceadapter);
        choiceadapter.setOnItemClickLitener(new SelectRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                initListView(data.get(position).getSublist());
            }
        });
    }

    private void initListView(final List<EquipmentCategory> data) {
        final SubListViewAdapter subListViewAdapter = new SubListViewAdapter(this, data);
        list_view.setAdapter(subListViewAdapter);
        subListViewAdapter.setCurrentSelectPosition(-1);
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

                start_Activity(EquipmentActivity.this, EquipmentDetailsActivity.class, new BasicNameValuePair("id", list.get(position - 1).getId()));
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


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_RELEASE_SUCCESS);
        registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_RELEASE_SUCCESS)) {
                refresh();
            }
        }
    };



    private Runnable run = new Runnable() {

        public void run() {
            EquipmentListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("name", name);
                data.put("new_old_rate", new_old_rate);
                data.put("pagesize", pagesize);
                data.put("page", "1");
                response = JsonParser.getEquipmentListResponse(HttpUtil.getMsg(Url.EQUIPMENTLIST + "?" + HttpUtil.getData(data)));
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
            EquipmentListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("name", name);
                data.put("new_old_rate", new_old_rate);
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                response = JsonParser.getEquipmentListResponse(HttpUtil.getMsg(Url.EQUIPMENTLIST + "?" + HttpUtil.getData(data)));
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
    private Runnable choiceRun = new Runnable() {
        private EquipmentCategoryResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                response = JsonParser.getEquipmentCategoryResponse(HttpUtil.getMsg(
                        Url.EQUIPMENT_CATEGORY));
            } catch (Exception e) {
                if (progress != null)
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


    public MyHandler handler = new MyHandler(this);

    public class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final EquipmentActivity activity = (EquipmentActivity) reference.get();
            if (activity == null) {
                return;
            }

            if (isDestroyed())
                return;

            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    EquipmentListResponse response = (EquipmentListResponse) msg.obj;
                    if (response.isSuccess()) {
                        initTitle("设备出售" + " (" + response.getData().getTotal() + ")");
                        list = response.getData().getItems();
                        adapter = new EquipmentListviewAdapter(activity, list);
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
                    EquipmentListResponse pageResponse = (EquipmentListResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<EquipmentList> tempList = pageResponse.getData()
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
                case NETWORK_SUCCESS_DATA_ERROR:
                    EquipmentCategoryResponse choiceResponse = (EquipmentCategoryResponse) msg.obj;
                    if (choiceResponse.getData() != null && choiceResponse.getData().size() > 0) {
                        initRecyclerView(choiceResponse.getData());
                        initListView(choiceResponse.getData().get(0).getSublist());
                    } else {
                        showErrorToast(choiceResponse.getError_desc());
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }
}
