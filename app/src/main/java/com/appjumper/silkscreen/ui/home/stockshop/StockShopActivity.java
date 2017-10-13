package com.appjumper.silkscreen.ui.home.stockshop;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.ShopProductSelectAdapter;
import com.appjumper.silkscreen.ui.home.adapter.StockShopListAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 现货商城
 * Created by Botx on 2017/8/24.
 */

public class StockShopActivity extends BaseActivity {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;
    @Bind(R.id.txtDefault)
    TextView txtDefault;
    @Bind(R.id.rdoGroup)
    RadioGroup rdoGroup;
    @Bind(R.id.rdoBtnPrice)
    RadioButton rdoBtnPrice;
    @Bind(R.id.rdoBtnConsult)
    RadioButton rdoBtnConsult;
    @Bind(R.id.gridProduct)
    GridView gridProduct;
    @Bind(R.id.edtTxtSearch)
    EditText edtTxtSearch;
    @Bind(R.id.imgViClose)
    ImageView imgViClose;


    private final int DEFAULT = 0; //默认排序
    private final int PRICE_ASC = 1; //价格升序
    private final int PRICE_DESC = 2; //价格降序
    private final int CONSULT_ASC = 3; //咨询升序
    private final int CONSULT_DESC = 4; //咨询降序

    private boolean isCheckedChanged = true; //标记是否切换了RadioButton

    private List<StockGoods> dataList;
    private StockShopListAdapter adapter;

    private List<Product> productList;
    private ShopProductSelectAdapter productAdapter;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;

    private int orderby = DEFAULT;
    private Product incomingProduct;
    private String pid = "";
    private String keywords = "";
    private long lastClickTime = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_shop);
        ButterKnife.bind(context);

        incomingProduct = (Product) getIntent().getSerializableExtra(Const.KEY_OBJECT);
        pid = incomingProduct.getProduct_id();
        initTitle("现货商城");
        initBack();

        initRecyclerView();
        initRefreshLayout();
        recyclerData.requestFocus();

        edtTxtSearch.addTextChangedListener(new SearchWatcher());
        edtTxtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        AppTool.hideSoftInput(context);
                        String content = edtTxtSearch.getText().toString().trim();
                        keywords = content;
                        setDefaultOrder();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        txtDefault.setSelected(true);
        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFastClick();
                getRecommendProduct();
                ptrLayt.autoRefresh();
            }
        }, 80);
    }



    private void initRecyclerView() {
        /**
         * 顶部推荐产品
         */
        productList = new ArrayList<>();
        productAdapter = new ShopProductSelectAdapter(context, productList);
        gridProduct.setAdapter(productAdapter);

        gridProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = productList.get(position);
                if (product.getProduct_name().equals("更多")) {
                    /*Intent intent = new Intent(context, ProductSelectActivity.class);
                    intent.putExtra(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_STOCK);
                    intent.putExtra(Const.KEY_IS_FILTER_MODE, true);
                    intent.putExtra(Const.KEY_IS_STOCK_SHOP, true);
                    startActivityForResult(intent, Const.REQUEST_CODE_SELECT_PRODUCT);*/
                    finish();
                } else {
                    if (isFastClick())
                        return;
                    productAdapter.changeSelected(position);
                    pid = product.getProduct_id();
                    setDefaultOrder();
                }
            }
        });


        /**
         * 商品数据
         */
        dataList = new ArrayList<>();
        adapter = new StockShopListAdapter(R.layout.item_recycler_line_stock_shop, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(context, GoodsDetailActivity.class);
                intent.putExtra(Const.KEY_OBJECT, dataList.get(position));
                startActivity(intent);
            }
        });

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getData();
            }
        }, recyclerData);

        adapter.setEnableLoadMore(false);
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 1;
                getData();
            }
        });
    }


    private class SearchWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String content = edtTxtSearch.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                imgViClose.setVisibility(View.GONE);
            } else {
                imgViClose.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("goods", "goods_list");
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("type", orderby);
        params.put("product_id", pid);
        params.put("keyworks", keywords);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (isDestroyed())
                    return;

                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<StockGoods> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), StockGoods.class);
                        totalSize = dataObj.optInt("total");

                        if (page == 1) {
                            dataList.clear();
                            recyclerData.smoothScrollToPosition(0);
                        }
                        dataList.addAll(list);
                        adapter.notifyDataSetChanged();

                        if (dataList.size() < totalSize)
                            adapter.setEnableLoadMore(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (isDestroyed())
                    return;
                showFailTips(getResources().getString(R.string.requst_fail));
                if (page > 1)
                    page--;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestroyed())
                    return;

                ptrLayt.refreshComplete();
                adapter.loadMoreComplete();
                if (totalSize == dataList.size())
                    adapter.loadMoreEnd();

                adapter.setEmptyView(R.layout.layout_empty_view_common);
            }
        });
    }


    /**
     * 获取顶部推荐的产品
     */
    private void getRecommendProduct() {
        RequestParams params = MyHttpClient.getApiParam("collection", "productByGoods");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<Product> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), Product.class);
                        if (list.size() > 0)
                            gridProduct.setVisibility(View.VISIBLE);
                        else
                            gridProduct.setVisibility(View.GONE);

                        productList.clear();

                        Product product1 = new Product();
                        product1.setProduct_id("");
                        product1.setProduct_name("全部");
                        productList.add(product1);

                        if (list.size() >= 9)
                            productList.addAll(list.subList(0, 8));
                        else
                            productList.addAll(list);

                        Product product2 = new Product();
                        product2.setProduct_id("");
                        product2.setProduct_name("更多");
                        productList.add(product2);

                        productAdapter.notifyDataSetChanged();
                        changeProduct(pid, incomingProduct.getProduct_name());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }



    @OnCheckedChanged({R.id.rdoBtnPrice, R.id.rdoBtnConsult})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            isCheckedChanged = true;
            txtDefault.setSelected(false);
            switch (buttonView.getId()) {
                case R.id.rdoBtnPrice:
                    orderby = PRICE_ASC;
                    setDrawableRight(rdoBtnPrice, R.mipmap.order_asc);
                    setDrawableRight(rdoBtnConsult, R.mipmap.order_default);
                    break;
                case R.id.rdoBtnConsult:
                    orderby = CONSULT_DESC;
                    setDrawableRight(rdoBtnConsult, R.mipmap.order_desc);
                    setDrawableRight(rdoBtnPrice, R.mipmap.order_default);
                    break;
                default:
                    break;
            }

            ptrLayt.autoRefresh();
        }
    }




    @OnClick({R.id.rdoBtnPrice, R.id.rdoBtnConsult, R.id.txtDefault, R.id.imgViClose})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rdoBtnPrice: //价格排序
                if (isFastClick())
                    return;
                if (orderby == PRICE_DESC) {
                    orderby = PRICE_ASC;
                    setDrawableRight(rdoBtnPrice, R.mipmap.order_asc);
                    ptrLayt.autoRefresh();
                } else {
                    if (isCheckedChanged == true) {
                        isCheckedChanged = false;
                    } else {
                        orderby = PRICE_DESC;
                        setDrawableRight(rdoBtnPrice, R.mipmap.order_desc);
                        ptrLayt.autoRefresh();
                    }
                }
                break;
            case R.id.rdoBtnConsult: //咨询排序
                if (isFastClick())
                    return;
                if (orderby == CONSULT_ASC) {
                    orderby = CONSULT_DESC;
                    setDrawableRight(rdoBtnConsult, R.mipmap.order_desc);
                    ptrLayt.autoRefresh();
                } else {
                    if (isCheckedChanged == true) {
                        isCheckedChanged = false;
                    } else {
                        orderby = CONSULT_ASC;
                        setDrawableRight(rdoBtnConsult, R.mipmap.order_asc);
                        ptrLayt.autoRefresh();
                    }
                }
                break;
            case R.id.txtDefault: //默认排序
                if (isFastClick())
                    return;
                setDefaultOrder();
                break;
            case R.id.imgViClose:
                edtTxtSearch.setText("");
                keywords = "";
                break;
            default:
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        switch (requestCode) {
            case Const.REQUEST_CODE_SELECT_PRODUCT:
                ServiceProduct serviceProduct = (ServiceProduct) data.getSerializableExtra(Const.KEY_OBJECT);
                pid = serviceProduct.getId();
                changeProduct(pid, serviceProduct.getName());
                break;
            default:
                break;
        }
    }


    /**
     * 设置为默认排序
     */
    private void setDefaultOrder() {
        txtDefault.setSelected(true);
        orderby = DEFAULT;
        rdoGroup.check(R.id.rdoBtnHolder);
        setDrawableRight(rdoBtnPrice, R.mipmap.order_default);
        setDrawableRight(rdoBtnConsult, R.mipmap.order_default);
        ptrLayt.autoRefresh();
    }


    /**
     * 变更所选产品
     */
    private void changeProduct(String pid, String pname) {
        if (TextUtils.isEmpty(pid)) {
            productAdapter.changeSelected(0);
            setDefaultOrder();
            return;
        }

        boolean result = false;
        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            if (pid.equals(product.getProduct_id())) {
                result = true;
                productAdapter.changeSelected(i);
                break;
            }
        }
        if (!result) {
            Product product = productList.get(productList.size() - 2);
            product.setProduct_id(pid);
            product.setProduct_name(pname);
            productAdapter.changeSelected(productList.size() - 2);
        }

        setDefaultOrder();
    }


    private void setDrawableRight(RadioButton rdoBtn, int resourceId) {
        Drawable drawable = getResources().getDrawable(resourceId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        rdoBtn.setCompoundDrawables(null, null, drawable, null);
    }


    private boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }



}
