package com.appjumper.silkscreen.ui.home.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.ProductResponse;
import com.appjumper.silkscreen.bean.ProductType;
import com.appjumper.silkscreen.bean.ProductTypeResponse;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.ServiceProductResponse;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.home.adapter.GirdDropDownAdapter;
import com.appjumper.silkscreen.ui.home.adapter.OrderListviewAdapter;
import com.appjumper.silkscreen.ui.home.process.ProcessingDetailsActivity;
import com.appjumper.silkscreen.ui.inquiry.InquirySpecificationActivity;
import com.appjumper.silkscreen.ui.my.adapter.ChoiceRecyclerAdapter;
import com.appjumper.silkscreen.ui.my.adapter.ProductionListViewAdapter;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.ui.spec.InquiryDaoPianActivity;
import com.appjumper.silkscreen.ui.spec.InquiryHuLanActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.appjumper.silkscreen.view.MyViewGroup;
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
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/11.
 * 丝网订做
 */
public class OrderActivity extends BaseActivity {
    @Bind(R.id.dropDownMenu)
    DropDownMenu mDropDownMenu;
    @Bind(R.id.txtProductSelect)
    TextView txtProductSelect;
    @Bind(R.id.drawerLayt)
    DrawerLayout drawerLayt;
    @Bind(R.id.txtSpecSelect)
    TextView txtSpecSelect;

    @Bind(R.id.l_screening1)
    LinearLayout l_screening1;
    @Bind(R.id.l_screening2)
    LinearLayout l_screening2;

    private String headers[] = {"全部产品", "全部公司", "筛选"};
    private String company[] = {"全部公司", "认证公司", "未认证公司"};
    private List<View> popupViews = new ArrayList<>();
    private GirdDropDownAdapter agesAdapter;


    private PullToRefreshPagedListView pullToRefreshView;
    private String pagesize = "20";
    private String type = "1";
    private String product_id = "";//产品id
    private String auth = "";//公司 0全部公司1认证公司2未认证公司
    private int pageNumber = 1;
    Map<String, String> fieldmap = new HashMap<String, String>();
    private PagedListView listView;
    private View mEmptyLayout;
    private List<Product> listData;
    private OrderListviewAdapter adapter;

    private String productType = "";
    private ChoiceRecyclerAdapter choiceadapter;
    private View productView;
    private ListView list_view;
    private MyRecyclerView recyclerView;
    private List<Spec> spec = new ArrayList<>();
    //private LinearLayout l_screening1;
    //private LinearLayout l_screening2;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        ButterKnife.bind(this);
        initBack();
        initProgressDialog();
        initLocation();
        //new Thread(serviceTypelistRun).start();
        initView();

        initDrawerLayout();

        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if (checkLogined()) {
                    if (getUser().getEnterprise() == null) {
                        start_Activity(OrderActivity.this, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    } else {
                        Intent intent = new Intent(context, ProductSelectActivity.class);
                        intent.putExtra(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_ORDER);
                        intent.putExtra(Const.KEY_MOTION, ProductSelectActivity.MOTION_RELEASE_SERVICE);
                        startActivity(intent);
                    }
                }
            }
        });
    }


    /**
     * 初始化侧滑菜单
     */
    private void initDrawerLayout() {
        drawerLayt.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayt.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerLayt.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayt.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    //产品类型列表
    private Runnable serviceTypelistRun = new Runnable() {
        private ProductTypeResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("type", type);
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
     *
     * @param data
     */
    private void initRecyclerView(final List<ProductType> data) {
        choiceadapter = new ChoiceRecyclerAdapter(this, data);
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
        if (data != null && data.size() > 0) {
            data.get(0).getProduct_spec();
            initSpec();
        }
        final ProductionListViewAdapter listAdapter = new ProductionListViewAdapter(this, data);
        listAdapter.setCurrentSelectPosition(-1);
        list_view.setAdapter(listAdapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listAdapter.setCurrentSelectPosition(position);
                listAdapter.notifyDataSetChanged();
                ServiceProduct serviceProduct = data.get(position);
                product_id = serviceProduct.getId();
                spec = serviceProduct.getProduct_spec();
                mDropDownMenu.setTabText(serviceProduct.getName());
                mDropDownMenu.closeMenu();
                initSpec();
                refresh();
            }
        });
    }

    private void initView() {
        //筛选产品
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

        // 公司
        final ListView agesView = new ListView(this);
        agesAdapter = new GirdDropDownAdapter(this, Arrays.asList(company));
        agesView.setDividerHeight(0);
        agesView.setAdapter(agesAdapter);
        agesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                agesAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : company[position]);
                mDropDownMenu.closeMenu();
                auth = position + "";
                refresh();
            }
        });
        popupViews.add(agesView);


        //筛选规格
        View viewScreening = LayoutInflater.from(this).inflate(R.layout.layout_screening, null);
        //l_screening1 = (LinearLayout) viewScreening.findViewById(R.id.l_screening1);
        //l_screening2 = (LinearLayout) viewScreening.findViewById(R.id.l_screening2);
        popupViews.add(viewScreening);

        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_processing_listview, null);
        pullToRefreshView = (PullToRefreshPagedListView) contentView.findViewById(R.id.listview);
        listView = pullToRefreshView.getRefreshableView();
        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
        initListener();
        refresh();
    }

    private void initSpec() {
        if (l_screening2.getChildCount() > 0) {
            l_screening2.removeAllViews();
        }
        if (l_screening1.getChildCount() > 0) {
            l_screening1.removeAllViews();
        }
        for (int i = 0; i < spec.size(); i++) {
            if (!spec.get(i).getFieldinput().equals("radio")) {
                View editView = LayoutInflater.from(this).inflate(R.layout.layout_filter_edit, null);
                editView.setTag("edit" + i);
                EditText etLow = (EditText) editView.findViewById(R.id.et_low);//最小值
                EditText etHigh = (EditText) editView.findViewById(R.id.et_high);//最大值
                TextView tvName = (TextView) editView.findViewById(R.id.tv_name);//规格名字
                //tvName.setText(spec.get(i).getName()+"（"+spec.get(i).getUnit()+"）");
                tvName.setText(spec.get(i).getName());
                etLow.setTag(i + "low");
                etHigh.setTag(i + "high");
                l_screening2.addView(editView);
            }
        }
        for (int i = 0; i < spec.size(); i++) {
            if (spec.get(i).getFieldinput().equals("radio")) {
                View choiceView = LayoutInflater.from(this).inflate(R.layout.layout_filter_choice, null);
                choiceView.setTag("choice" + i);
                TextView tvName = (TextView) choiceView.findViewById(R.id.tv_name);//规格名字
                //tvName.setText(spec.get(i).getName()+"（"+spec.get(i).getUnit()+"）");
                tvName.setText(spec.get(i).getName());
                MyViewGroup radioGroup = (MyViewGroup) choiceView.findViewById(R.id.my_view_group);
                String[] sqlitunit = spec.get(i).getUnit().split(",");
                for (int j = 0; j < sqlitunit.length; j++) {
                    View radioView = LayoutInflater.from(this).inflate(R.layout.radio, null);
                    CheckBox radioButton = (CheckBox) radioView.findViewById(R.id.radio);
                    radioButton.setText(sqlitunit[j]);
                    radioButton.setId(i + j);
                    radioGroup.addView(radioView);

                }
                radioGroup.setTag("group" + i);
                l_screening2.addView(choiceView);
            }
        }
        if (spec.size() > 0) {
            View btnView = LayoutInflater.from(this).inflate(R.layout.layout_button, null);

            TextView tvReset = (TextView) btnView.findViewById(R.id.tv_reset);//重置
            TextView tvConfirm = (TextView) btnView.findViewById(R.id.tv_confirm);//确定
            tvReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < spec.size(); i++) {
                        if (spec.get(i).getFieldinput().equals("radio")) {
                            View choiceView = l_screening2.findViewWithTag("choice" + i);
                            MyViewGroup radioGroup = (MyViewGroup) choiceView.findViewWithTag("group" + i);
                            String[] sqlitunit = spec.get(i).getUnit().split(",");
                            for (int j = 0; j < sqlitunit.length; j++) {
                                CheckBox radioButton = (CheckBox) radioGroup.findViewById(i+j);
                                radioButton.setChecked(false);
                            }
                        } else {
                            View editView = l_screening2.findViewWithTag("edit" + i);
                            EditText etLow = (EditText) editView.findViewWithTag(i + "low");
                            EditText etHigh = (EditText) editView.findViewWithTag(i + "high");
                            etLow.setText("");
                            etHigh.setText("");
                        }
                    }
                }
            });
            tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < spec.size(); i++) {
                        if (spec.get(i).getFieldinput().equals("radio")) {
                            View choiceView = l_screening2.findViewWithTag("choice" + i);
                            MyViewGroup radioGroup = (MyViewGroup) choiceView.findViewWithTag("group" + i);
                            String[] sqlitunit = spec.get(i).getUnit().split(",");
                            String str = "";
                            for (int j = 0; j < sqlitunit.length; j++) {
                                CheckBox radioButton = (CheckBox) findViewById(i + j);
                                if (radioButton.isChecked()) {
                                    String selectValue = radioButton.getText().toString();
                                    str += "," + selectValue;
                                }
                            }
                            if(str.equals("")){
                                fieldmap.put(spec.get(i).getFieldname(), "");
                            }else{
                                fieldmap.put(spec.get(i).getFieldname(), str.substring(1));
                            }
                        } else {
                            View editView = l_screening2.findViewWithTag("edit" + i);
                            EditText etLow = (EditText) editView.findViewWithTag(i + "low");
                            EditText etHigh = (EditText) editView.findViewWithTag(i + "high");
//                            if (etLow.getText().toString().trim().length() < 1) {
//                                showErrorToast("请输入" + spec.get(i).getName() + "最小值");
//                                return;
//                            }
//                            if (etHigh.getText().toString().trim().length() < 1) {
//                                showErrorToast("请输入" + spec.get(i).getName() + "最大值");
//                                return;
//                            }
                            if (spec.get(i).getFieldinput().equals("number*number")) {
                                fieldmap.put(spec.get(i).getFieldname(), etLow.getText().toString() + "*" + etHigh.getText().toString());
                            } else {
                                fieldmap.put(spec.get(i).getFieldname(), etLow.getText().toString() + "-" + etHigh.getText().toString());
                            }
                        }

                    }

                    drawerLayt.closeDrawers();

                    new Thread(run).start();
                    mDropDownMenu.setTabText("已选规格");
                    mDropDownMenu.closeMenu();
                }
            });
            l_screening1.addView(btnView);
        }
    }

    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!checkLogined())
                    return;
                start_Activity(OrderActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", listData.get((i - 1)).getProduct_name() + listData.get((i - 1)).getService_type_name()), new BasicNameValuePair("id", listData.get((i - 1)).getId()));
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
                fieldmap.put("g", "api");
                fieldmap.put("m", "service");
                fieldmap.put("a", "service_list");


                fieldmap.put("page", "1");
                fieldmap.put("pagesize", pagesize);
                fieldmap.put("type", type);
                fieldmap.put("uid", getUserID());
                fieldmap.put("lat", latitude + "");
                fieldmap.put("lng", longitude + "");
                fieldmap.put("product_id", product_id);
                fieldmap.put("auth", auth);

                //response = JsonParser.getProductResponse(HttpUtil.postMsg(HttpUtil.getData(fieldmap), Url.SERVICELIST));
                response = JsonParser.getProductResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(fieldmap)));
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
                fieldmap.put("pagesize", pagesize);
                fieldmap.put("page", "" + pageNumber);
                fieldmap.put("type", type);
                fieldmap.put("uid", getUserID());
                fieldmap.put("lat", latitude + "");
                fieldmap.put("lng", longitude + "");
                fieldmap.put("product_id", product_id);
                fieldmap.put("auth", auth);
                response = JsonParser.getProductResponse(HttpUtil.postMsg(HttpUtil.getData(fieldmap), Url.SERVICELIST));
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
            if (isDestroyed())
                return;

            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    ProductResponse response = (ProductResponse) msg.obj;
                    if (response.isSuccess()) {
                        initTitle("丝网订做（" + response.getData().getTotal() + "）");
                        listData = response.getData().getItems();
                        if (listData != null && listData.size() > 0) {
                            product = listData.get(0);
                        }
                        adapter = new OrderListviewAdapter(OrderActivity.this, listData);
                        listView.onFinishLoading(response.getData().hasMore());
                        listView.setAdapter(adapter);
                        pageNumber = 2;
                        pullToRefreshView.setEmptyView(listData.isEmpty() ? mEmptyLayout : null);
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
                        listData.addAll(tempList);
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
                    if (listResponse.isSuccess()) {
                        if (listResponse.getData().size() > 0) {
                            initRecyclerView(listResponse.getData());
                            productType = listResponse.getData().get(0).getId();
                            new Thread(serviceProductlistRun).start();
                        }
                    } else {
                        showErrorToast(listResponse.getError_desc());
                    }
                    break;
                case 4://产品列表
                    ServiceProductResponse serviceResponse = (ServiceProductResponse) msg.obj;
                    if (serviceResponse.isSuccess()) {
                        initListView(serviceResponse.getData());
                    } else {
                        showErrorToast(serviceResponse.getError_desc());
                    }
                    break;
                default:
                    showErrorToast();
                    listView.onFinishLoading(false);
                    break;
            }
        }
    }

    @OnClick({R.id.tv_inquiry, R.id.txtProductSelect, R.id.txtSpecSelect})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_inquiry:
                if (checkLogined()) {
                    if (product_id == null || product_id.equals("") || product_id.length() < 1) {
                        showErrorToast("请先选择产品");
                        return;
                    }
                    if (listData.size() < 1) {
                        showErrorToast("暂无数据");
                        return;
                    }

                    Intent intent = null;
                    if (product.getProduct_id().equals("104"))
                        intent = new Intent(context, InquiryHuLanActivity.class);
                    else if (product.getProduct_id().equals("27"))
                        intent = new Intent(context, InquiryDaoPianActivity.class);
                    else
                        intent = new Intent(context, InquirySpecificationActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("identity", "2");
                    intent.putExtra("productType", "");
                    Bundle bundle = new Bundle();
                    ServiceProduct serviceProduct = new ServiceProduct();
                    serviceProduct.setName(product.getProduct_name());
                    serviceProduct.setId(product.getProduct_id());
                    serviceProduct.setProduct_spec(product.getService_spec());
                    bundle.putSerializable("service", serviceProduct);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in,
                            R.anim.push_left_out);
                }
                break;
            case R.id.txtProductSelect: //选择产品
                Intent intent = new Intent(context, ProductSelectActivity.class);
                intent.putExtra(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_ORDER);
                intent.putExtra(Const.KEY_IS_FILTER_MODE, true);
                startActivityForResult(intent, Const.REQUEST_CODE_INQUIRY_ORDER);
                break;
            case R.id.txtSpecSelect: //规格筛选
                if (product_id == null || product_id.equals("") || product_id.length() < 1) {
                    showErrorToast("请先选择产品");
                    return;
                }
                drawerLayt.openDrawer(Gravity.RIGHT);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        switch (requestCode) {
            case Const.REQUEST_CODE_INQUIRY_ORDER:
                ServiceProduct product = (ServiceProduct) data.getSerializableExtra(Const.KEY_OBJECT);
                product_id = product.getId();
                spec = product.getProduct_spec();
                txtProductSelect.setText(product.getName());
                initSpec();
                refresh();
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
            return;
        }
        if (drawerLayt.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayt.closeDrawers();
            return;
        }

        super.onBackPressed();
    }
}
