package com.appjumper.silkscreen.ui.home.stockshop;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
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
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 现货商城列表
 * Created by Botx on 2017/8/24.
 */

public class StockShopListFragment extends BaseFragment {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;
    @Bind(R.id.txtProduct)
    TextView txtProduct;
    @Bind(R.id.rdoGroup)
    RadioGroup rdoGroup;
    @Bind(R.id.rdoBtnPrice)
    RadioButton rdoBtnPrice;
    @Bind(R.id.rdoBtnConsult)
    RadioButton rdoBtnConsult;

    private final int DEFAULT = 0; //默认排序
    private final int PRICE_ASC = 1; //价格升序
    private final int PRICE_DESC = 2; //价格降序
    private final int CONSULT_ASC = 3; //咨询升序
    private final int CONSULT_DESC = 4; //咨询降序

    private boolean isCheckedChanged = true; //标记是否切换了RadioButton

    private List<StockGoods> dataList;
    private StockShopListAdapter adapter;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;

    private int orderby = 0;
    private String pid = "";
    private long lastClickTime = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_shop_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        initRecyclerView();
        initRefreshLayout();

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
               ptrLayt.autoRefresh();
            }
        }, 50);
    }


    private void initRecyclerView() {
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
        RequestParams params = MyHttpClient.getApiParam("goods", "goods_list");
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("type", orderby);
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
                showFailTips(getResources().getString(R.string.requst_fail));
                if (page > 1)
                    page--;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDetached())
                    return;

                ptrLayt.refreshComplete();
                adapter.loadMoreComplete();
                if (totalSize == dataList.size())
                    adapter.loadMoreEnd();

                adapter.setEmptyView(R.layout.layout_empty_view_common);
            }
        });
    }


    @OnCheckedChanged({R.id.rdoBtnPrice, R.id.rdoBtnConsult})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            isCheckedChanged = true;
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




    @OnClick({R.id.rdoBtnPrice, R.id.rdoBtnConsult, R.id.txtProduct})
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
            case R.id.txtProduct: //选择产品
                Intent intent = new Intent(context, ProductSelectActivity.class);
                intent.putExtra(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_STOCK);
                intent.putExtra(Const.KEY_IS_FILTER_MODE, true);
                intent.putExtra(Const.KEY_IS_STOCK_SHOP, true);
                startActivityForResult(intent, Const.REQUEST_CODE_SELECT_PRODUCT);
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
                ServiceProduct product = (ServiceProduct) data.getSerializableExtra(Const.KEY_OBJECT);
                pid = product.getId();
                orderby = DEFAULT;
                txtProduct.setText(product.getName());

                rdoGroup.check(R.id.rdoBtnHolder);
                setDrawableRight(rdoBtnPrice, R.mipmap.order_default);
                setDrawableRight(rdoBtnConsult, R.mipmap.order_default);

                ptrLayt.autoRefresh();
                break;
            default:
                break;
        }
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
