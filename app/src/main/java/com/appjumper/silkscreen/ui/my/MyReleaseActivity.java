package com.appjumper.silkscreen.ui.my;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.ui.dynamic.adapter.ProductAdapter;
import com.appjumper.silkscreen.ui.home.process.ProcessingDetailsActivity;
import com.appjumper.silkscreen.ui.home.stock.StockDetailActivity;
import com.appjumper.silkscreen.ui.my.adapter.MyReleaseAdapter;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的发布
 * Created by Botx on 2017/7/31.
 */

public class MyReleaseActivity extends BaseActivity {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;

    private List<Product> dataList;
    private MyReleaseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_release);
        ButterKnife.bind(context);

        initTitle("我的发布");
        initBack();
        initRightButton("发布信息", new RightButtonListener() {
            @Override
            public void click() {

            }
        });

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

        adapter = new MyReleaseAdapter(R.layout.item_recycler_my_release, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setHeaderView(LayoutInflater.from(context).inflate(R.layout.layout_header_view_my_release, null));

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        test();
                        ptrLayt.refreshComplete();
                    }
                }, 2000);
            }
        });
    }


    private void test() {
        dataList.clear();
        for (int i = 0; i < 20; i++) {
            Product product = new Product();
            dataList.add(product);
        }
        adapter.notifyDataSetChanged();
    }

}
