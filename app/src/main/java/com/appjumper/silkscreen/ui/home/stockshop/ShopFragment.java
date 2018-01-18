package com.appjumper.silkscreen.ui.home.stockshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.ShopProductSelectAdapter;
import com.appjumper.silkscreen.ui.home.adapter.StockShopListAdapter;
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

/**
 * 现货商城
 * Created by Botx on 2018/1/16.
 */

public class ShopFragment extends BaseFragment {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;
    @Bind(R.id.gridProduct)
    GridView gridProduct;

    private List<StockGoods> dataList;
    private StockShopListAdapter adapter;

    private List<Product> productList;
    private ShopProductSelectAdapter productAdapter;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;

    private String pid = "";
    private long lastClickTime = 0;
    private int type;
    private StockGoods incomingProduct;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        type = bundle.getInt(Const.KEY_TYPE, Const.SHOP_TYPE_COMPANY);
        if (bundle.containsKey(Const.KEY_OBJECT)) {
            incomingProduct = (StockGoods) bundle.getSerializable(Const.KEY_OBJECT);
            pid = incomingProduct.getProduct_id();
        }

        initRecyclerView();
        initRefreshLayout();

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFastClick();
                getRecommendProduct();
                ptrLayt.autoRefresh();
            }
        }, 50);
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

                    startActivityForResult(new Intent(context, StockGoodsSelectActivity.class), Const.REQUEST_CODE_SELECT_PRODUCT);
                } else {
                    if (isFastClick()) return;
                    productAdapter.changeSelected(position);
                    pid = product.getProduct_id();
                    ptrLayt.autoRefresh();
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


    /**
     * 获取数据
     */
    private void getData() {
        String action = "";
        if (type == Const.SHOP_TYPE_STOCK)
            action = "goods_stock_list";
        else
            action = "goods_list";

        RequestParams params = MyHttpClient.getApiParam("goods", action);
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("product_id", pid);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<StockGoods> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), StockGoods.class);
                        totalSize = dataObj.optInt("total");
                        if (!isViewCreated) return;

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
                if (!isViewCreated) return;
                showFailTips(getResources().getString(R.string.requst_fail));
                if (page > 1)
                    page--;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isViewCreated) return;

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
                        changeProduct(pid, incomingProduct == null ? null : incomingProduct.getProduct_name(), false);
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


    /**
     * 变更所选产品
     */
    private void changeProduct(String pid, String pname, boolean needRefresh) {
        if (TextUtils.isEmpty(pid)) {
            productAdapter.changeSelected(0);
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

        if (needRefresh)
            ptrLayt.autoRefresh();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        switch (requestCode) {
            case Const.REQUEST_CODE_SELECT_PRODUCT:
                Product product = (Product) data.getSerializableExtra(Const.KEY_OBJECT);
                pid = product.getProduct_id();
                changeProduct(pid, product.getProduct_name(), true);
                break;
            default:
                break;
        }
    }


    private boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

}
