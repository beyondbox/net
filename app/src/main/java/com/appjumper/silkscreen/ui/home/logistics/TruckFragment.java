package com.appjumper.silkscreen.ui.home.logistics;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.AreaBean;
import com.appjumper.silkscreen.bean.AreaBeanResponse;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.bean.LineListResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.AddRessRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.CityListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.TruckListviewAdapter;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.appjumper.silkscreen.view.pulltorefresh.PagedListView;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshBase;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshPagedListView;
import com.yyydjk.library.DropDownMenu;

import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 物流-找车
 * Created by Botx on 2017/9/29.
 */

public class TruckFragment extends BaseFragment {

    @Bind(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;

    private TruckListviewAdapter mAdapter;
    private PagedListView listView2;
    private List<LineList> list2;
    private PullToRefreshPagedListView pullToRefreshView2;
    private List<AreaBean> areaItems;
    private List<AreaBean> cityItems;

    MyRecyclerView addRecyclerView;
    ListView addResslistView;
    MyRecyclerView toRecyclerView;
    ListView toResslistView;
    AddRessRecyclerAdapter addRessRecycleradapter;

    private String pagesize = "20";
    private int pageNumber = 1;
    private String type = "3";

    private String pid;
    private String to_id = "";
    private String from_id = "";
    private String date = "";
    private String headers[] = {"出发地", "目的地", "装货时间"};
    private List<View> popupViews = new ArrayList<>();
    public static final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truck, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        initView();
        new Thread(addressrun).start();
        onRefresh();
    }


    private void onRefresh() {
        pullToRefreshView2.setRefreshing();
        new Thread(truckRun).start();
    }


    private void initView() {
        //出发地
        final LinearLayout layoutProduct = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutProduct.setOrientation(LinearLayout.VERTICAL);
        layoutProduct.setBackgroundResource(R.color.while_color);
        layoutProduct.setLayoutParams(params);
        layoutProduct.setOnClickListener(null);
        View formView = LayoutInflater.from(context).inflate(R.layout.layout_choice, null);
        addRecyclerView = (MyRecyclerView) formView.findViewById(R.id.recycler_view);
        addResslistView = (ListView) formView.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addRecyclerView.setLayoutManager(linearLayoutManager);
        layoutProduct.addView(formView);
        popupViews.add(layoutProduct);

        //目的地
        final LinearLayout layoutProduct2 = new LinearLayout(context);
        layoutProduct2.setOrientation(LinearLayout.VERTICAL);
        layoutProduct2.setBackgroundResource(R.color.while_color);
        layoutProduct2.setLayoutParams(params);
        layoutProduct2.setOnClickListener(null);
        View toView = LayoutInflater.from(context).inflate(R.layout.layout_choice, null);
        toRecyclerView = (MyRecyclerView) toView.findViewById(R.id.recycler_view);
        toResslistView = (ListView) toView.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        toRecyclerView.setLayoutManager(linearLayoutManager2);
        layoutProduct2.addView(toView);
        popupViews.add(layoutProduct2);

        //装货时间
        final LinearLayout layoutDate = new LinearLayout(context);
        layoutDate.setOrientation(LinearLayout.VERTICAL);
        layoutDate.setBackgroundResource(R.color.while_color);
        layoutDate.setLayoutParams(params);
        layoutDate.setOnClickListener(null);
        final Calendar time = Calendar.getInstance(Locale.CHINA);
        LinearLayout dateTimeLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_date_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.new_act_date_picker);
        DatePicker.OnDateChangedListener dateListener = new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                // TODO Auto-generated method stub
                time.set(Calendar.YEAR, year);
                time.set(Calendar.MONTH, monthOfYear);
                time.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            }
        };

        datePicker.init(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH), dateListener);
        layoutDate.addView(dateTimeLayout);
        View btnView = LayoutInflater.from(context).inflate(R.layout.layout_button, null);
        TextView tvReset = (TextView) btnView.findViewById(R.id.tv_reset);//重置
        TextView tvConfirm = (TextView) btnView.findViewById(R.id.tv_confirm);//确定
        tvReset.setText("取消");
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDropDownMenu.closeMenu();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time.set(Calendar.YEAR, datePicker.getYear());
                time.set(Calendar.MONTH, datePicker.getMonth());
                time.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                Date datatime = time.getTime();
                date = format1.format(datatime);
                onRefresh();
                mDropDownMenu.setTabText(format.format(datatime));
                mDropDownMenu.closeMenu();
            }
        });
        layoutDate.addView(btnView);

        popupViews.add(layoutDate);


        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_equipment_listview, null);
        pullToRefreshView2 = (PullToRefreshPagedListView) contentView.findViewById(R.id.listview);
        listView2 = pullToRefreshView2.getRefreshableView();
        View mEmptyLayout2 = LayoutInflater.from(context).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView2.setEmptyView(mEmptyLayout2);
        listView2.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

            @Override
            public void onLoadMoreItems() {
                new Thread(truckPage).start();
            }
        });
        pullToRefreshView2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                new Thread(truckRun).start();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!checkLogined())
                    return;

                start_Activity(context, TruckDetailsActivity.class, new BasicNameValuePair("title", "电镀锌片"), new BasicNameValuePair("id", list2.get(position - 1).getId()));
            }
        });

        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
    }


    private Runnable truckRun = new Runnable() {

        public void run() {
            LineListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("from", from_id);
                data.put("to", to_id);
                data.put("pagesize", pagesize);
                data.put("page", "1");
                data.put("date", date);
                response = JsonParser.getLineListResponse(HttpUtil.getMsg(Url.TRUCK_LINELIST + "?" + HttpUtil.getData(data)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_OTHER, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    private Runnable truckPage = new Runnable() {
        @SuppressWarnings("unchecked")
        public void run() {
            LineListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("date", date);
                data.put("from", from_id);
                data.put("to", to_id);
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                response = JsonParser.getLineListResponse(HttpUtil.getMsg(Url.LINELIST + "?" + HttpUtil.getData(data)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(NETWORK_SUCCESS_DATA_ERROR, response));
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
                case NETWORK_OTHER:
                    pullToRefreshView2.onRefreshComplete();
                    LineListResponse truck = (LineListResponse) msg.obj;
                    if (truck.isSuccess()) {
                        list2 = truck.getData().getItems();
                        mAdapter = new TruckListviewAdapter(context, list2);
                        listView2.onFinishLoading(truck.getData().hasMore());
                        listView2.setAdapter(mAdapter);
                        pageNumber = 2;
                    } else {
                        listView2.onFinishLoading(false);
                        showErrorToast(truck.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    pullToRefreshView2.onRefreshComplete();
                    LineListResponse truckPage = (LineListResponse) msg.obj;
                    if (truckPage.isSuccess()) {
                        List<LineList> tempList = truckPage.getData()
                                .getItems();
                        list2.addAll(tempList);
                        mAdapter.notifyDataSetChanged();
                        listView2.onFinishLoading(truckPage.getData().hasMore());
                        pageNumber++;
                    } else {
                        listView2.onFinishLoading(false);
                        showErrorToast(truckPage.getError_desc());
                    }
                    break;
                case 7://市选择
                    AreaBeanResponse areaResponse = (AreaBeanResponse) msg.obj;
                    if (areaResponse.isSuccess()) {
                        areaItems = areaResponse.getData();
                        initRecyclerView(areaItems);
                        pid = areaResponse.getData().get(0).getId();
                        new Thread(addressCityRun).start();
                    } else {
                        showErrorToast(areaResponse.getError_desc());
                    }
                    break;
                case 8://区选择
                    AreaBeanResponse cityResponse = (AreaBeanResponse) msg.obj;
                    if (cityResponse.isSuccess()) {
                        cityItems = cityResponse.getData();
                        initListView(cityItems);
                    } else {
                        showErrorToast(cityResponse.getError_desc());
                    }
                    break;
                default:
                    showErrorToast();
                    listView2.onFinishLoading(false);
                    break;
            }
        }
    };


    /**
     * 初始化地址左边RecyclerView
     *
     * @param data
     */
    private void initRecyclerView(final List<AreaBean> data) {
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
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        toRecyclerView.setLayoutManager(linearLayoutManager2);
        addRessRecycleradapter = new AddRessRecyclerAdapter(context, data);
        toRecyclerView.setAdapter(addRessRecycleradapter);
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
    private void initListView(final List<AreaBean> data) {
        addResslistView.setAdapter(new CityListViewAdapter(context, data));
        addResslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                from_id = data.get(position).getId();
                onRefresh();
                mDropDownMenu.setTabText(data.get(position).getShortname());
                mDropDownMenu.closeMenu();
            }
        });

        toResslistView.setAdapter(new CityListViewAdapter(context, data));
        toResslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                to_id = data.get(position).getId();
                onRefresh();
                mDropDownMenu.setTabText(data.get(position).getShortname());
                mDropDownMenu.closeMenu();
            }
        });
    }

    private Runnable addressrun = new Runnable() {
        public void run() {
            AreaBeanResponse response = null;
            try {
                response = JsonParser.getAreaBeanResponse(HttpUtil.getMsg(Url.CITYLIST));
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


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

}
