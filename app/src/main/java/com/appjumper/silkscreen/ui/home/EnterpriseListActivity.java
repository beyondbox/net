package com.appjumper.silkscreen.ui.home;

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
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.EnterpriseResponse;
import com.appjumper.silkscreen.bean.ProductType;
import com.appjumper.silkscreen.bean.ProductTypeResponse;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.ServiceProductResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.adapter.EnterpriseListListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.GirdDropDownAdapter;
import com.appjumper.silkscreen.ui.home.process.ProcessingDetailsActivity;
import com.appjumper.silkscreen.ui.my.adapter.ChoiceRecyclerAdapter;
import com.appjumper.silkscreen.ui.my.adapter.ProductionListViewAdapter;
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
 * 企业列表
 */
public class EnterpriseListActivity extends BaseActivity {
    @Bind(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    private String headers[] = {"全部公司", "全部服务", "全部产品"};
    private String auth[] = {"全部公司", "认证公司", "未认证公司"};
    private String service[] = {"全部服务", "加工", "订做", "现货"};
    private List<View> popupViews = new ArrayList<>();
    private GirdDropDownAdapter cityAdapter;
    private GirdDropDownAdapter agesAdapter;
    private EnterpriseListListViewAdapter adapter;

    private PullToRefreshPagedListView pullToRefreshView;
    private PagedListView listView;
    private View mEmptyLayout;
    private String auth_id;//公司 0全部公司1认证公司2未认证公司
    private String service_id;//
    private String product_id;//

    private String pagesize = "20";
    private int pageNumber = 1;
    private List<Enterprise> list;
    private ListView list_view;
    private MyRecyclerView recyclerView;
    private String productType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_list);
        initLocation();
        ButterKnife.bind(this);
        initBack();
        initView();

    }
    
    //产品类型列表
    private Runnable serviceTypelistRun = new Runnable() {
        private ProductTypeResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("type", service_id);
                response = JsonParser.getProductTypeResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.SERVICEPRODUCTTYPE));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        3, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };
    
    //产品列表接口
    private Runnable serviceProductlistRun = new Runnable() {
        private ServiceProductResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("type", productType);
                response = JsonParser.getServiceProductResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.SERVICEPRODUCT));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        4, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };
    
    /**
     * 初始化RecyclerView
     * @param data
     */
    private void initRecyclerView(final List<ProductType> data) {
        ChoiceRecyclerAdapter choiceadapter = new ChoiceRecyclerAdapter(this, data);
        recyclerView.setAdapter(choiceadapter);
        choiceadapter.setOnItemClickLitener(new ChoiceRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                productType = data.get(position).getId();
                new Thread(serviceProductlistRun).start();
            }
        });
    }


    private void initListView(final List<ServiceProduct> data) {
        list_view.setAdapter(new ProductionListViewAdapter(this,data));
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServiceProduct serviceProduct = data.get(position);
                product_id = serviceProduct.getId();
                mDropDownMenu.setTabText(serviceProduct.getName());
                mDropDownMenu.closeMenu();
                refresh();
            }
        });
    }

    private void initView() {
        //init city menu
        final ListView authView = new ListView(this);
        cityAdapter = new GirdDropDownAdapter(this, Arrays.asList(auth));
        authView.setDividerHeight(0);
        authView.setAdapter(cityAdapter);
        authView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : auth[position]);
                mDropDownMenu.closeMenu();
                auth_id=position+"";
                refresh();
            }
        });

        final ListView serviceView = new ListView(this);
        agesAdapter = new GirdDropDownAdapter(this, Arrays.asList(service));
        serviceView.setDividerHeight(0);
        serviceView.setAdapter(agesAdapter);
        serviceView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                agesAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : service[position]);
                mDropDownMenu.closeMenu();
                service_id=position+"";
                new Thread(serviceTypelistRun).start();
                refresh();
            }
        });

        //init popupViews
        popupViews.add(authView);
        popupViews.add(serviceView);
        
        //筛选产品
        final LinearLayout layoutProduct = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutProduct.setOrientation(LinearLayout.VERTICAL);
        layoutProduct.setBackgroundResource(R.color.while_color);
        layoutProduct.setLayoutParams(params);
        layoutProduct.setOnClickListener(null);
        View productView = LayoutInflater.from(this).inflate(R.layout.layout_choice, null);
        recyclerView = (MyRecyclerView) productView.findViewById(R.id.recycler_view);
        list_view = (ListView) productView.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        layoutProduct.addView(productView);
        popupViews.add(layoutProduct);

        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_equipment_listview, null);
        pullToRefreshView = (PullToRefreshPagedListView) contentView.findViewById(R.id.listview);
        listView = pullToRefreshView.getRefreshableView();
        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                start_Activity(EnterpriseListActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", "电镀锌片"));
            }
        });

        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
        refresh();
        initListener();
    }

    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                start_Activity(EnterpriseListActivity.this, CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"),new BasicNameValuePair("id",list.get((i-1)).getEnterprise_id()));
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
            EnterpriseResponse response = null;
            try {
                Map<String,String> map = new HashMap<>();
                map.put("page", "1");
                map.put("pagesize", pagesize);
                map.put("type", service_id);
                map.put("uid", getUserID());
                map.put("lat", latitude+"");
                map.put("lng", longitude+"");
                map.put("product_id", product_id);
                map.put("auth", auth_id);
                response = JsonParser.getEnterpriseResponse(HttpUtil.postMsg(HttpUtil.getData(map), Url.ENTERPRISELIST));
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
            EnterpriseResponse response = null;
            try {
                Map<String,String> map = new HashMap<>();
                map.put("page", "" + pageNumber);
                map.put("pagesize", pagesize);
                map.put("type", service_id);
                map.put("uid", getUserID());
                map.put("lat", latitude+"");
                map.put("lng", longitude+"");
                map.put("product_id", product_id);
                map.put("auth", auth_id);
                response = JsonParser.getEnterpriseResponse(HttpUtil.postMsg(HttpUtil.getData(map), Url.ENTERPRISELIST));
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
                    EnterpriseResponse response = (EnterpriseResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter =new EnterpriseListListViewAdapter(EnterpriseListActivity.this,list);
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
                    EnterpriseResponse pageResponse = (EnterpriseResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<Enterprise> tempList = pageResponse.getData()
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
                case 3://产品类型列表接口
                    ProductTypeResponse listResponse = (ProductTypeResponse) msg.obj;
                    if(listResponse.isSuccess()){
                        if(listResponse.getData().size()>0){
                            initRecyclerView(listResponse.getData());
                            productType = listResponse.getData().get(0).getId();
                            new Thread(serviceProductlistRun).start();
                        }
                    }else{
                        showErrorToast(listResponse.getError_desc());
                    }
                    break;
                case 4://产品列表
                    ServiceProductResponse serviceResponse = (ServiceProductResponse) msg.obj;
                    if(serviceResponse.isSuccess()){
                        initListView(serviceResponse.getData());
                    }else{
                        showErrorToast(serviceResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    showErrorToast("数据返回有误");
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
