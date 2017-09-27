package com.appjumper.silkscreen.ui.home.stockshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 发布现货商品选择
 * Created by Botx on 2017/9/26.
 */

public class ReleaseGoodsSelectActivity extends BaseActivity {

    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;

    private List<Product> dataList;
    private GoodsSelectAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_goods_select);
        ButterKnife.bind(context);

        initTitle("发布现货信息");
        initBack();
        initRecyclerView();
        createData();
    }


    private void initRecyclerView () {
        dataList = new ArrayList<>();
        adapter = new GoodsSelectAdapter(R.layout.item_recycler_release_goods_select, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(context, ReleaseStockGoodsActivity.class);
                intent.putExtra(Const.KEY_OBJECT, dataList.get(position));
                startActivity(intent);
            }
        });
    }


    /**
     * 数据
     */
    private void createData() {
        Product product = new Product();
        product.setProduct_id("26");
        product.setProduct_name("刺绳");
        dataList.add(product);

        Product product1 = new Product();
        product1.setProduct_id("98");
        product1.setProduct_name("建筑网片");
        dataList.add(product1);

        Product product2 = new Product();
        product2.setProduct_id("4");
        product2.setProduct_name("金刚网");
        dataList.add(product2);

        Product product3 = new Product();
        product3.setProduct_id("22");
        product3.setProduct_name("电焊网");
        dataList.add(product3);

        Product product4 = new Product();
        product4.setProduct_id("30");
        product4.setProduct_name("荷兰网");
        dataList.add(product4);

        Product product5 = new Product();
        product5.setProduct_id("117");
        product5.setProduct_name("黑铁丝");
        dataList.add(product5);

        adapter.notifyDataSetChanged();
    }


    /**
     * 适配器
     */
    private class GoodsSelectAdapter extends BaseQuickAdapter<Product, BaseViewHolder> {

        public GoodsSelectAdapter(@LayoutRes int layoutResId, @Nullable List<Product> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Product item) {
            helper.setText(R.id.txtName, item.getProduct_name());
        }
    }


}
