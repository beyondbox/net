package com.appjumper.silkscreen.ui.trend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.OfferList;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.trend.adapter.PriceMoreParentAdapter;
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
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 原材料报价-更多
 * Created by Botx on 2017/6/26.
 */

public class PriceMoreActivity extends BaseActivity {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.lvData)
    ListView lvData;

    private String product_id;

    private List<List<OfferList>> dataList;
    private PriceMoreParentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_all);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        initTitle(intent.getStringExtra("title") + "报价详情");
        product_id = intent.getStringExtra("id");

        initBack();
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
        dataList= new ArrayList<>();
        adapter = new PriceMoreParentAdapter(context, dataList);
        lvData.setAdapter(adapter);
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
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
        RequestParams params = new RequestParams();
        params.put("product_id", product_id);
        MyHttpClient.getInstance().get(Url.PRICE_MORE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        parseJson(dataObj);
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
                if (!isDestroyed())
                    ptrLayt.refreshComplete();
            }
        });
    }


    /**
     * 解析json数据
     */
    private void parseJson(JSONObject jsonObj) {
        try {
            dataList.clear();
            Iterator<String> it = jsonObj.keys();
            while (it.hasNext()) {
                String key = it.next();
                List<OfferList> childList = GsonUtil.getEntityList(jsonObj.getJSONArray(key).toString(), OfferList.class);
                dataList.add(childList);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
