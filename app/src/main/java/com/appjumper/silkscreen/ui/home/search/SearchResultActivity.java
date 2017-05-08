package com.appjumper.silkscreen.ui.home.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.ProductResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.adapter.GirdDropDownAdapter;
import com.appjumper.silkscreen.ui.home.adapter.ProcessingListviewAdapter;
import com.appjumper.silkscreen.ui.home.process.ProcessingDetailsActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
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
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/11.
 * 丝网加工
 */
public class SearchResultActivity extends BaseActivity {
    @Bind(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    @Bind(R.id.et_search)
    EditText etSearch;
    private String key;

    private String headers[] = {"全部服务", "全部公司"};
    private String citys[] = {"全部服务", "订做", "加工", "现货"};
    private String ages[] = {"全部公司", "认证公司", "未认证公司"};
    private List<View> popupViews = new ArrayList<>();
    private GirdDropDownAdapter cityAdapter;
    private GirdDropDownAdapter agesAdapter;

    private PullToRefreshPagedListView pullToRefreshView;
    private PagedListView listView;
    private View mEmptyLayout;
    private String pagesize = "20";
    private int pageNumber = 1;
    private String type = "0";//服务类型ID 1订做2加工3现货
    private String product_id = "";//产品id
    private String auth = "0";//公司 0全部公司1认证公司2未认证公司
    private String fieldname = "";//规格使用product_spec的fieldname作为参数名
    private String fieldname_max = "";//规格最大值
    private String fieldname_min = "";//规格最小值
    private List<Product> list;
    private ProcessingListviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        initView();
        key = getIntent().getStringExtra("key");
        etSearch.setText(key);
        etSearch.setSelection(key.length());
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 3) {
                /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (etSearch.getText().toString().trim().length() < 1 || etSearch.getText().toString().trim().equals("")) {
                        showErrorToast("搜索内容不能为空");
                    } else {
                        if (inputMethodManager.isActive()) {
                            inputMethodManager.hideSoftInputFromWindow(SearchResultActivity.this.getCurrentFocus().getWindowToken(), 0);
                        }
                        key = etSearch.getText().toString().trim();
                        getMyApplication().getMyUserManager().saveHistory(key);
                        refresh();
                        return true;
                    }

                }
                return false;
            }
        });
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
        initListener();
    }

    @OnClick({R.id.tv_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                hideKeyboard();
                finish();
                break;
            default:
                break;
        }
    }

    private void initView() {
        //init city menu
        final ListView cityView = new ListView(this);
        cityAdapter = new GirdDropDownAdapter(this, Arrays.asList(citys));
        cityView.setDividerHeight(0);
        cityView.setAdapter(cityAdapter);

        final ListView agesView = new ListView(this);
        agesAdapter = new GirdDropDownAdapter(this, Arrays.asList(ages));
        agesView.setDividerHeight(0);
        agesView.setAdapter(agesAdapter);


        //init popupViews
        popupViews.add(cityView);
        popupViews.add(agesView);

        //add item click event
        cityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : citys[position]);
                mDropDownMenu.closeMenu();
                type=position+"";
                new Thread(run).start();
            }
        });
        //add item click event
        agesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                agesAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : ages[position]);
                mDropDownMenu.closeMenu();
                auth=position+"";
                new Thread(run).start();
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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                start_Activity(SearchResultActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", list.get((i-1)).getProduct_name()+list.get((i-1)).getService_type_name()),new BasicNameValuePair("id",list.get((i-1)).getId()));
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
            ProductResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("page", "1");
                data.put("pagesize", pagesize);
                data.put("type", type);
                data.put("keyworks", key);
                data.put("uid", getUserID());
                data.put("lat", latitude+"");
                data.put("lng", longitude+"");
                data.put("auth", auth);
                response = JsonParser.getProductResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICELIST));
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
            ProductResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                data.put("type", type);
                data.put("keyworks", key);
                data.put("uid", getUserID());
                data.put("lat", latitude+"");
                data.put("lng", longitude+"");
                data.put("auth", auth);
                response = JsonParser.getProductResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICELIST));
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
    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    ProductResponse response = (ProductResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new ProcessingListviewAdapter(SearchResultActivity.this, list);
                        listView.onFinishLoading(response.getData().hasMore());
                        listView.setAdapter(adapter);
                        pageNumber = 2;
                        pullToRefreshView.setEmptyView(list.isEmpty() ? mEmptyLayout : null);
                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(response.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    ProductResponse pageResponse = (ProductResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<Product> tempList = pageResponse.getData()
                                .getItems();
                        list.addAll(tempList);
                        adapter.notifyDataSetChanged();
                        listView.onFinishLoading(pageResponse.getData().hasMore());
                        pageNumber++;
                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(pageResponse.getError_desc());
                    }
                    break;
                default:
                    showErrorToast();
                    listView.onFinishLoading(false);
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
