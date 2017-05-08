package com.appjumper.silkscreen.ui.home.workshop;

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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.AreaBean;
import com.appjumper.silkscreen.bean.AreaBeanResponse;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.bean.EquipmentListResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.adapter.AddRessRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.CityListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.GirdDropDownAdapter;
import com.appjumper.silkscreen.ui.home.adapter.WorkshopListViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
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
 * 厂房出租
 */
public class WorkshopActivity extends BaseActivity {
    @Bind(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    private String headers[] = {"全部位置", "全部面积", "全部价格"};
    private String citys[] = {"不限", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
    private String ages[] = {"不限", "0-2000m²", "2000-5000m²", "5000-10000m²", "10000-30000m²", "30000m²以上"};
    private String sexs[] = {"不限", "0-10000元/年", "10000-30000元/年", "30000-100000元/年", "100000元/年以上"};
    private List<View> popupViews = new ArrayList<>();
    private GirdDropDownAdapter cityAdapter;
    private GirdDropDownAdapter agesAdapter;
    private GirdDropDownAdapter sexsAdapter;

    private PullToRefreshPagedListView pullToRefreshView;
    private PagedListView listView;
    private View mEmptyLayout;
    private String pagesize = "30";
    private int pageNumber = 1;
    private List<EquipmentList> list;
    private WorkshopListViewAdapter adapter;
    private AddRessRecyclerAdapter addRessRecycleradapter;
    private String pid;
    private String from_id;
    private String max_area;
    private String min_area;
    private String min_price;
    private String max_price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);
        ButterKnife.bind(this);
        initBack();
        initTitle("厂房出租");
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if(checkLogined()){
                    start_Activity(WorkshopActivity.this, WorkshopReleaseActivity.class);
                }
            }
        });
        new Thread(addressrun).start();
        initView();
        initListener();
        initLocation();
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        latitude = aMapLocation.getLatitude();//获取纬度
                        longitude = aMapLocation.getLongitude();//获取经度
                        accuracy = aMapLocation.getAccuracy();//获取精度信息
                        refresh();
                        if(mlocationClient!=null){
                            mlocationClient.stopLocation();
                        }
                    }
                }
            }
        });
    }


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

    MyRecyclerView addRecyclerView;
    ListView addResslistView;
    private void initView() {
        //init city menu
        final ListView cityView = new ListView(this);
        cityAdapter = new GirdDropDownAdapter(this, Arrays.asList(citys));
        cityView.setDividerHeight(0);
        cityView.setAdapter(cityAdapter);

        final LinearLayout layoutProduct = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutProduct.setOrientation(LinearLayout.VERTICAL);
        layoutProduct.setBackgroundResource(R.color.while_color);
        layoutProduct.setLayoutParams(params);
        layoutProduct.setOnClickListener(null);
        View formView = LayoutInflater.from(this).inflate(R.layout.layout_choice, null);
        addRecyclerView = (MyRecyclerView)formView.findViewById(R.id.recycler_view);
        addResslistView = (ListView)formView.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addRecyclerView.setLayoutManager(linearLayoutManager);
        layoutProduct.addView(formView);
        popupViews.add(layoutProduct);

        final ListView agesView = new ListView(this);
        agesAdapter = new GirdDropDownAdapter(this, Arrays.asList(ages));
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
                mDropDownMenu.setTabText(position == 0 ? headers[1] : ages[position]);
                mDropDownMenu.closeMenu();
                new Thread(run).start();
            }
        });
        popupViews.add(agesView);

        final ListView sexsView = new ListView(this);
        sexsAdapter = new GirdDropDownAdapter(this, Arrays.asList(sexs));
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
                mDropDownMenu.setTabText(position == 0 ? headers[2] : sexs[position]);
                mDropDownMenu.closeMenu();
                new Thread(run).start();
            }
        });


        popupViews.add(sexsView);

        //add item click event
        cityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : citys[position]);
                mDropDownMenu.closeMenu();
            }
        });

        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_equipment_listview, null);
        pullToRefreshView = (PullToRefreshPagedListView) contentView.findViewById(R.id.listview);
        listView = pullToRefreshView.getRefreshableView();

        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);
        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
    }

    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                start_Activity(WorkshopActivity.this, WorkshopDetailsActivity.class, new BasicNameValuePair("id", list.get(position - 1).getId()));
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
            EquipmentListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("lat",  latitude+"");
                data.put("lng", longitude+"");
                data.put("min_area", min_area);
                data.put("max_area", max_area);
                data.put("min_price", min_price);
                data.put("max_price", max_price);
                data.put("pagesize", pagesize);
                data.put("page", "1");
                data.put("location",from_id);
                response = JsonParser.getEquipmentListResponse(HttpUtil.getMsg(Url.WORKSHOP_LIST + "?" + HttpUtil.getData(data)));
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
                data.put("lat", latitude+"");
                data.put("lng", longitude+"");
                data.put("min_area", min_area);
                data.put("max_area", max_area);
                data.put("min_price", min_price);
                data.put("max_price", max_price);
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                data.put("location",from_id);
                response = JsonParser.getEquipmentListResponse(HttpUtil.getMsg(Url.WORKSHOP_LIST + "?" + HttpUtil.getData(data)));
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

    /**
     * 初始化地址左边RecyclerView
     * @param data
     */
    private void initRecyclerView(final List<AreaBean> data) {
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
     * @param data
     */
    private void initListView(final List<AreaBean> data) {
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

    public MyHandler handler = new MyHandler(this);

    public class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final WorkshopActivity activity = (WorkshopActivity) reference.get();
            if (activity == null) {
                return;
            }
            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    EquipmentListResponse response = (EquipmentListResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new WorkshopListViewAdapter(activity,  list);
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
                case  7://市选择
                    AreaBeanResponse areaResponse = (AreaBeanResponse) msg.obj;
                    if (areaResponse.isSuccess()) {
                        List<AreaBean> areaItems = areaResponse.getData();
                        initRecyclerView(areaItems);
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
