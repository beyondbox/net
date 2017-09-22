package com.appjumper.silkscreen.ui.home.stockshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.StockGoodsSelectAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 现货商品选择列表
 * Created by Botx on 2017/9/19.
 */

public class StockGoodsSelectActivity extends BaseActivity {

    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;
    @Bind(R.id.edtTxtSearch)
    EditText edtTxtSearch;
    @Bind(R.id.imgViClose)
    ImageView imgViClose;


    private List<Product> origList; //原始数据
    private List<Product> filterList; //搜索后的数据
    private StockGoodsSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_goods_select);
        ButterKnife.bind(context);

        initTitle("现货商城");
        initBack();
        initProgressDialog();
        initRecyclerView();
        recyclerData.requestFocus();

        edtTxtSearch.addTextChangedListener(new SearchWatcher());
        edtTxtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        AppTool.hideSoftInput(context);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        getData();
    }


    private void initRecyclerView() {
        origList = new ArrayList<>();
        filterList = new ArrayList<>();

        adapter = new StockGoodsSelectAdapter(R.layout.item_recycler_stock_goods, filterList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(context, StockShopActivity.class);
                intent.putExtra(Const.KEY_OBJECT, filterList.get(position));
                startActivity(intent);
            }
        });
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("collection", "productByGoods");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (isDestroyed())
                    return;

                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        List<Product> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), Product.class);
                        origList.clear();
                        origList.addAll(list);
                        filterList.clear();
                        filterList.addAll(origList);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestroyed())
                    return;

                progress.dismiss();
                adapter.setEmptyView(R.layout.layout_empty_view_common);
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
            filterList.clear();

            if (TextUtils.isEmpty(content)) {
                imgViClose.setVisibility(View.GONE);
                filterList.addAll(origList);
            } else {
                imgViClose.setVisibility(View.VISIBLE);
                for (Product product : origList) {
                    if (product.getProduct_name().contains(content) || product.getDescription().contains(content))
                        filterList.add(product);
                }
            }

            adapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    @OnClick({R.id.imgViClose})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgViClose:
                edtTxtSearch.setText("");
                break;
            default:
                break;
        }
    }


}
