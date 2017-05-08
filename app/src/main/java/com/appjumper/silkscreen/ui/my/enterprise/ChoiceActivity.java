package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.ProductType;
import com.appjumper.silkscreen.bean.ProductTypeResponse;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.ServiceProductResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.inquiry.InquirySpecificationActivity;
import com.appjumper.silkscreen.ui.my.adapter.ChoiceRecyclerAdapter;
import com.appjumper.silkscreen.ui.my.adapter.ProductionListViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.appjumper.silkscreen.view.MyRecyclerView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-11-22.
 * 产品选择
 */
public class ChoiceActivity extends BaseActivity {
    @Bind(R.id.recycler_view)
    MyRecyclerView recyclerView;
    @Bind(R.id.list_view)
    ListView listView;

    private ChoiceRecyclerAdapter adapter;
    private String type;//类型 1订做2加工3现货
    private String productType;
    private String identity;//从哪个类跳转过来的1添加服务2订做、加工、现货3MoreWindow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        ActivityTaskManager.getInstance().putActivity(ChoiceActivity.this);
        initBack();
        ButterKnife.bind(this);
        type = getIntent().getStringExtra("from");
        identity = getIntent().getStringExtra("identity");
        initTitle(getIntent().getStringExtra("title"));

        initProgressDialog();
        progress.show();
        new Thread(serviceTypelistRun).start();
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
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
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
                        3, response));
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
            ChoiceActivity activity = (ChoiceActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://产品类型列表接口
                    progress.dismiss();
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
                case 3://产品列表
                    ServiceProductResponse serviceResponse = (ServiceProductResponse) msg.obj;
                    if (serviceResponse.isSuccess()) {
                        initListView(serviceResponse.getData());
                    } else {
                        showErrorToast(serviceResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    if (progress != null) {
                        progress.dismiss();
                    }
                    activity.showErrorToast();
                    break;
            }
        }
    }

    /**
     * 初始化RecyclerView
     *
     * @param data
     */
    private void initRecyclerView(final List<ProductType> data) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ChoiceRecyclerAdapter(this, data);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new ChoiceRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                productType = data.get(position).getId();
                new Thread(serviceProductlistRun).start();
            }
        });
    }

    private void initListView(final List<ServiceProduct> data) {
        final ProductionListViewAdapter adapter = new ProductionListViewAdapter(this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setCurrentSelectPosition(position);
                adapter.notifyDataSetChanged();
                ServiceProduct serviceProduct = data.get(position);
                Intent intent;
                Bundle bundle;
                intent = new Intent();
                if (identity.equals("1")) {
                    intent.setClass(ChoiceActivity.this, SpecificationActivity.class);
                    if (type.equals("3")) {
                        intent.setClass(ChoiceActivity.this, SpecificationStockActivity.class);
                    }
                } else {
                    intent.setClass(ChoiceActivity.this, InquirySpecificationActivity.class);
                    intent.putExtra("identity", identity);
                }

                bundle = new Bundle();
                bundle.putSerializable("service", serviceProduct);
                intent.putExtras(bundle);
                intent.putExtra("type", type);
                intent.putExtra("productType", productType);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
            }
        });
    }

}
