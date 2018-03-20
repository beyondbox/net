package com.appjumper.silkscreen.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.askbuy.AskBuyActivity;
import com.appjumper.silkscreen.ui.home.stockshop.ShopActivity;
import com.appjumper.silkscreen.ui.home.stockshop.StockConsignActivity;
import com.appjumper.silkscreen.ui.shop.adapter.CategoryAdapter;
import com.appjumper.silkscreen.ui.shop.adapter.ShopProductGridAdapter;
import com.appjumper.silkscreen.util.Const;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 买货
 * Created by Botx on 2018/1/17.
 */

public class StockShopFragment extends BaseFragment {

    @Bind(R.id.lvCategory)
    ListView lvCategory;
    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.grid0)
    GridView grid0;
    @Bind(R.id.grid1)
    GridView grid1;

    private String [] categoryArr = {"库存处理", "厂家委托"};
    private CategoryAdapter categoryAdapter;

    private List<StockGoods> list0;
    private ShopProductGridAdapter adapter0;

    private List<StockGoods> list1;
    private ShopProductGridAdapter adapter1;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_shop, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        initListView();
        initRefreshLayout();

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 50);
    }


    private void initListView() {
        categoryAdapter = new CategoryAdapter(context, Arrays.asList(categoryArr));
        lvCategory.setDividerHeight(0);
        lvCategory.setAdapter(categoryAdapter);
        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryAdapter.changeSelected(i);
            }
        });

        list0 = new ArrayList<>();
        adapter0 = new ShopProductGridAdapter(context, list0);
        grid0.setAdapter(adapter0);
        grid0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, ShopActivity.class);
                intent.putExtra(Const.KEY_TYPE, Const.SHOP_TYPE_STOCK);
                intent.putExtra(Const.KEY_OBJECT, list0.get(i));
                startActivity(intent);
            }
        });

        list1 = new ArrayList<>();
        adapter1 = new ShopProductGridAdapter(context, list1);
        grid1.setAdapter(adapter1);
        grid1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, ShopActivity.class);
                intent.putExtra(Const.KEY_TYPE, Const.SHOP_TYPE_COMPANY);
                intent.putExtra(Const.KEY_OBJECT, list1.get(i));
                startActivity(intent);
            }
        });
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrLayt.setLastUpdateTimeRelateObject(this);
        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getData();
            }
        });
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("goods", "goods_type");

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<StockGoods> temp0 = GsonUtil.getEntityList(dataObj.getJSONArray("stock").toString(), StockGoods.class);
                        List<StockGoods> temp1 = GsonUtil.getEntityList(dataObj.getJSONArray("enterprise").toString(), StockGoods.class);

                        list0.clear();
                        list0.addAll(temp0);
                        adapter0.notifyDataSetChanged();

                        list1.clear();
                        list1.addAll(temp1);
                        adapter1.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (!isViewCreated) return;
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isViewCreated) return;
                ptrLayt.refreshComplete();
            }
        });
    }


    @OnClick({R.id.txtMore0, R.id.txtMore1, R.id.left, R.id.right})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txtMore0:
                intent = new Intent(context, ShopActivity.class);
                intent.putExtra(Const.KEY_TYPE, Const.SHOP_TYPE_STOCK);
                startActivity(intent);
                break;
            case R.id.txtMore1:
                intent = new Intent(context, ShopActivity.class);
                intent.putExtra(Const.KEY_TYPE, Const.SHOP_TYPE_COMPANY);
                startActivity(intent);
                break;
            case R.id.left:
                if (checkLogined())
                    start_Activity(context, StockConsignActivity.class);
                break;
            case R.id.right:
                start_Activity(context, AskBuyActivity.class);
                break;
        }
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

}
