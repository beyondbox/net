package com.appjumper.silkscreen.ui.home.logistics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.AreaBean;
import com.appjumper.silkscreen.bean.AreaBeanResponse;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.bean.LineListResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;
import com.appjumper.silkscreen.ui.home.adapter.AddRessRecyclerAdapter;
import com.appjumper.silkscreen.ui.home.adapter.CityListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.LogisticsStandingListviewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.TruckListviewAdapter;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.appjumper.silkscreen.view.pulltorefresh.PagedListView;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshBase;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshPagedListView;
import com.yyydjk.library.DropDownMenu;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
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
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/11.
 * 物流 (已废弃)
 */
public class LogisticsActivity extends BaseActivity {
    private String headers[] = {"出发地", "目的地", "装货时间"};
    private List<View> popupViews = new ArrayList<>();

    @Bind(R.id.rg)
    RadioGroup rg;

    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;

    @Bind(R.id.l_hz_gr)
    LinearLayout l_hz_gr;

    @Bind(R.id.right)//发布按钮
            TextView tvRight;

    @Bind(R.id.tv_start)//出发地
            TextView tv_start;

    @Bind(R.id.tv_end)//终点
            TextView tv_end;

    @Bind(R.id.iv_dzqh)//起点终点
            ImageView iv_dzqh;

    @Bind(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;

    private PagedListView listView;
    private View mEmptyLayout;
    private String pagesize = "20";
    private int pageNumber = 1;
    private String type = "1";
    private List<LineList> list;
    private List<LineList> list2;
    private LogisticsStandingListviewAdapter adapter;
    private SureOrCancelDialog comCreateDialog;

    private String start_id = "";
    private String end_id = "";
    private String to_id = "";
    private String from_id = "";
    private String date = "";
    private TruckListviewAdapter mAdapter;
    private PagedListView listView2;
    private PullToRefreshPagedListView pullToRefreshView2;
    private List<AreaBean> areaItems;
    private List<AreaBean> cityItems;
    private String pid;
    public static final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics);
        initBack();
        ButterKnife.bind(this);

        listView = pullToRefreshView.getRefreshableView();
        listView.onFinishLoading(false);
        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);
        initView();
        initDialog();
        new Thread(addressrun).start();
        initListener();
        refresh();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                selectRadioBtn();
            }
        });
    }


    /**
     * 初始化对话框
     */
    private void initDialog() {
        comCreateDialog = new SureOrCancelDialog(context, "提示", "您尚未完善企业信息，暂时不能在该板块发布信息，请完善企业信息后再继续操作", "确定", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    }
                });
    }


    private void selectRadioBtn() {
        RadioButton radioButton = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
        switch (radioButton.getId()) {
            case R.id.rb0:
                pullToRefreshView.setVisibility(View.VISIBLE);
                l_hz_gr.setVisibility(View.VISIBLE);
                mDropDownMenu.setVisibility(View.GONE);
                type = "1";
                refresh();
                break;
            case R.id.rb1:
                pullToRefreshView.setVisibility(View.VISIBLE);
                l_hz_gr.setVisibility(View.VISIBLE);
                mDropDownMenu.setVisibility(View.GONE);
                type = "2";
                refresh();
                break;
            case R.id.rb2:
                pullToRefreshView.setVisibility(View.GONE);
                l_hz_gr.setVisibility(View.GONE);
                mDropDownMenu.setVisibility(View.VISIBLE);
                type = "3";
                onRefresh();
                break;
        }
    }

    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    /**
     * 找车
     */
    private void onRefresh() {
        pullToRefreshView2.setRefreshing();
        new Thread(truckRun).start();
    }

    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!checkLogined())
                    return;

                start_Activity(LogisticsActivity.this, LogisticsDetailsActivity.class, new BasicNameValuePair("id", list.get((i - 1)).getId()), new BasicNameValuePair("type", type));
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
            LineListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("type", type);
                data.put("pagesize", pagesize);
                data.put("page", "1");
                //if (!end_id.equals("") && !start_id.equals("")) {
                    data.put("from", start_id);
                    data.put("to", end_id);
                //}
                response = JsonParser.getLineListResponse(HttpUtil.getMsg(Url.LINELIST + "?" + HttpUtil.getData(data)));
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
            LineListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                //if (!end_id.equals("") && !start_id.equals("")) {
                    data.put("from", start_id);
                    data.put("to", end_id);
                //}
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                data.put("type", type);
                response = JsonParser.getLineListResponse(HttpUtil.getMsg(Url.LINELIST + "?" + HttpUtil.getData(data)));
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

    public MyHandler handler = new MyHandler(this);

    public class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final LogisticsActivity activity = (LogisticsActivity) reference.get();
            if (activity == null) {
                return;
            }

            if (isDestroyed())
                return;

            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    LineListResponse response = (LineListResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new LogisticsStandingListviewAdapter(activity, list);
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
                    LineListResponse pageResponse = (LineListResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<LineList> tempList = pageResponse.getData()
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
                case NETWORK_OTHER:
                    pullToRefreshView2.onRefreshComplete();
                    LineListResponse truck = (LineListResponse) msg.obj;
                    if (truck.isSuccess()) {
                        list2 = truck.getData().getItems();
                        mAdapter = new TruckListviewAdapter(activity, list2);
                        activity.listView2.onFinishLoading(truck.getData().hasMore());
                        activity.listView2.setAdapter(activity.mAdapter);
                        activity.pageNumber = 2;
                        activity.pullToRefreshView.setEmptyView(activity.list2.isEmpty() ? activity.mEmptyLayout : null);
                    } else {
                        activity.listView2.onFinishLoading(false);
                        activity.showErrorToast(truck.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    pullToRefreshView2.onRefreshComplete();
                    LineListResponse truckPage = (LineListResponse) msg.obj;
                    if (truckPage.isSuccess()) {
                        List<LineList> tempList = truckPage.getData()
                                .getItems();
                        activity.list2.addAll(tempList);
                        activity.mAdapter.notifyDataSetChanged();
                        activity.listView2.onFinishLoading(truckPage.getData().hasMore());
                        activity.pageNumber++;
                    } else {
                        activity.listView2.onFinishLoading(false);
                        activity.showErrorToast(truckPage.getError_desc());
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
                        activity.showErrorToast(areaResponse.getError_desc());
                    }
                    break;
                case 8://区选择
                    AreaBeanResponse cityResponse = (AreaBeanResponse) msg.obj;
                    if (cityResponse.isSuccess()) {
                        cityItems = cityResponse.getData();
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

    /**
     * 初始化地址左边RecyclerView
     *
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
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        toRecyclerView.setLayoutManager(linearLayoutManager2);
        addRessRecycleradapter = new AddRessRecyclerAdapter(this, data);
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
        addResslistView.setAdapter(new CityListViewAdapter(this, data));
        addResslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                from_id = data.get(position).getId();
                new Thread(truckRun).start();
                mDropDownMenu.setTabText(data.get(position).getShortname());
                mDropDownMenu.closeMenu();
            }
        });

        toResslistView.setAdapter(new CityListViewAdapter(this, data));
        toResslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                to_id = data.get(position).getId();
                new Thread(truckRun).start();
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

    MyRecyclerView addRecyclerView;
    ListView addResslistView;
    MyRecyclerView toRecyclerView;
    ListView toResslistView;
    AddRessRecyclerAdapter addRessRecycleradapter;

    private void initView() {
        //出发地
        final LinearLayout layoutProduct = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutProduct.setOrientation(LinearLayout.VERTICAL);
        layoutProduct.setBackgroundResource(R.color.while_color);
        layoutProduct.setLayoutParams(params);
        layoutProduct.setOnClickListener(null);
        View formView = LayoutInflater.from(this).inflate(R.layout.layout_choice, null);
        addRecyclerView = (MyRecyclerView) formView.findViewById(R.id.recycler_view);
        addResslistView = (ListView) formView.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addRecyclerView.setLayoutManager(linearLayoutManager);
        layoutProduct.addView(formView);
        popupViews.add(layoutProduct);

        //目的地
        final LinearLayout layoutProduct2 = new LinearLayout(this);
        layoutProduct2.setOrientation(LinearLayout.VERTICAL);
        layoutProduct2.setBackgroundResource(R.color.while_color);
        layoutProduct2.setLayoutParams(params);
        layoutProduct2.setOnClickListener(null);
        View toView = LayoutInflater.from(this).inflate(R.layout.layout_choice, null);
        toRecyclerView = (MyRecyclerView) toView.findViewById(R.id.recycler_view);
        toResslistView = (ListView) toView.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        toRecyclerView.setLayoutManager(linearLayoutManager2);
        layoutProduct2.addView(toView);
        popupViews.add(layoutProduct2);

        //装货时间
        final LinearLayout layoutDate = new LinearLayout(this);
        layoutDate.setOrientation(LinearLayout.VERTICAL);
        layoutDate.setBackgroundResource(R.color.while_color);
        layoutDate.setLayoutParams(params);
        layoutDate.setOnClickListener(null);
        final Calendar time = Calendar.getInstance(Locale.CHINA);
        LinearLayout dateTimeLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_date_picker, null);
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
        View btnView = LayoutInflater.from(this).inflate(R.layout.layout_button, null);
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
                new Thread(truckRun).start();
                mDropDownMenu.setTabText(format.format(datatime));
                mDropDownMenu.closeMenu();
            }
        });
        layoutDate.addView(btnView);

        popupViews.add(layoutDate);


        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_equipment_listview, null);
        pullToRefreshView2 = (PullToRefreshPagedListView) contentView.findViewById(R.id.listview);
        listView2 = pullToRefreshView2.getRefreshableView();
        View mEmptyLayout2 = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
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

                start_Activity(LogisticsActivity.this, TruckDetailsActivity.class, new BasicNameValuePair("title", "电镀锌片"), new BasicNameValuePair("id", list2.get(position - 1).getId()));
            }
        });

        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
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
    protected void onResume() {
        super.onResume();
        switch (rg.getCheckedRadioButtonId()) {
            case R.id.rb0:
                refresh();
                break;
            case R.id.rb1:
                refresh();
                break;
            case R.id.rb2:
                onRefresh();
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.tv_start, R.id.tv_end, R.id.right,R.id.iv_dzqh})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dzqh://起点终点切换

                break;
            case R.id.tv_start:
                startForResult_Activity(this, AddressSelectActivity.class, 1, new BasicNameValuePair("code", "1"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.tv_end:
                startForResult_Activity(this, AddressSelectActivity.class, 2, new BasicNameValuePair("code", "2"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.right:
                if (checkLogined()) {
                    if (type.equals("3")) {
                        //start_Activity(LogisticsActivity.this, TruckReleaseActivity.class);
                        CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_LOGISTICS_CAR);
                    } else if (type.equals("2")) {
                        //start_Activity(LogisticsActivity.this, PersonalReleaseActivity.class, new BasicNameValuePair("type", "2"));
                        CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_LOGISTICS_PER);
                    } else {
                        if (getUser().getEnterprise() == null) {
                            comCreateDialog.show();
                            return;
                        }
                        //start_Activity(LogisticsActivity.this, PersonalReleaseActivity.class, new BasicNameValuePair("type", "1"));
                        CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_LOGISTICS);
                    }
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
//                if (end_id != null && !end_id.equals("")) {
                new Thread(run).start();
//                }
                break;
            case 2://目的地
                end_id = data.getStringExtra("id");
                String end_name = data.getStringExtra("name");
                tv_end.setText(end_name);
                tv_end.setCompoundDrawables(null, null, null, null);
//                if (start_id != null && !start_id.equals("")) {
                new Thread(run).start();
//                }
                break;
            default:
                break;
        }
    }
}
