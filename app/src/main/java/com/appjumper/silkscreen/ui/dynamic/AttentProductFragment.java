package com.appjumper.silkscreen.ui.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.AttentProduct;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.ServiceSelectActivity;
import com.appjumper.silkscreen.ui.dynamic.adapter.AttentProductAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.ItemSpaceDecorationGrid;
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
 * 关注的产品
 * Created by Botx on 2017/3/28.
 */

public class AttentProductFragment extends BaseFragment {

    @Bind(R.id.recyclerAttentProduct)
    RecyclerView recyclerAttentProduct;

    private AttentProductAdapter productAdapter;
    private List<AttentProduct> productList;

    private DynamicManageActivity activity;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attent_product, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        activity = (DynamicManageActivity) getActivity();
        initProgressDialog();
        initRecyclerView();

        getMyAttention();
    }


    private void initRecyclerView() {
        productList = new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        recyclerAttentProduct.setLayoutManager(layoutManager);
        recyclerAttentProduct.addItemDecoration(new ItemSpaceDecorationGrid(2, 18, 20));

        productAdapter = new AttentProductAdapter(R.layout.item_recycler_grid_attent_product, productList);
        productAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        productAdapter.isFirstOnly(false);
        recyclerAttentProduct.setAdapter(productAdapter);
    }


    /**
     * 获取我关注的产品
     */
    private void getMyAttention() {
        RequestParams params = MyHttpClient.getApiParam("collection", "alreadyCollection");
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
                progress.setMessage("加载中...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        productList.clear();
                        productList.addAll(GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), AttentProduct.class));
                        productAdapter.notifyDataSetChanged();
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
                progress.dismiss();
            }
        });
    }


    /**
     * 保存我的关注
     */
    private void saveSelection(final List<ServiceProduct> list, int serviceType) {
        String selectedID = "";
        for (ServiceProduct product : list) {
            selectedID += product.getId() + ",";
        }

        RequestParams params = MyHttpClient.getApiParam("collection", "addProduct");
        params.put("uid", getUserID());
        params.put("product_id", selectedID);
        params.put("service_type", serviceType);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.setMessage("保存中...");
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        getMyAttention();
                        activity.getAttentNum();
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
                progress.dismiss();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        if (requestCode == Const.REQUEST_CODE_SELECT_PRODUCT) {
            List<ServiceProduct> list = (ArrayList<ServiceProduct>)data.getSerializableExtra("list");
            int serviceType = data.getIntExtra(Const.KEY_SERVICE_TYPE, 0);
            saveSelection(list, serviceType);
        }
    }


    @OnClick({R.id.txtManageAttent})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txtManageAttent: //管理关注产品
                intent = new Intent(context, ServiceSelectActivity.class);
                intent.putExtra(Const.KEY_IS_MULTI_MODE, true);
                intent.putExtra(Const.KEY_ACTION, Const.ACTION_ATTENT_PRODUCT_MANAGE);
                startActivityForResult(intent, Const.REQUEST_CODE_SELECT_PRODUCT);
                break;
            default:
                break;
        }
    }
}
