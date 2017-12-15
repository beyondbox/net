package com.appjumper.silkscreen.ui.home.logistics;

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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.KeyWorks;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.HotGridViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.LineListAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 货站搜索
 * Created by Botx on 2017/9/30.
 */

public class LineSearchActivity extends BaseActivity {

    @Bind(R.id.llHot)
    LinearLayout llHot;
    @Bind(R.id.gridViHot)
    GridView gridViHot;
    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerData)
    RecyclerView recyclerData;
    @Bind(R.id.edtTxtSearch)
    EditText edtTxtSearch;


    private List<LineList> dataList;
    private LineListAdapter adapter;
    private List<KeyWorks> hotList;
    private HotGridViewAdapter hotAdapter;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;

    private String keyword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_search);
        ButterKnife.bind(context);

        initTitle("货站搜索");
        initBack();
        initEditText();

        initRecyclerView();
        initRefreshLayout();
        getHot();
    }


    private void initEditText() {
        edtTxtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        keyword = edtTxtSearch.getText().toString().trim();
                        llHot.setVisibility(View.GONE);
                        ptrLayt.setVisibility(View.VISIBLE);
                        AppTool.hideSoftInput(context);
                        ptrLayt.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ptrLayt.autoRefresh();
                            }
                        }, 50);
                        addHot();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        edtTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(edtTxtSearch.getText().toString().trim())) {
                    ptrLayt.setVisibility(View.GONE);
                    llHot.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void initRecyclerView() {
        dataList = new ArrayList<>();
        adapter = new LineListAdapter(R.layout.item_recycler_line, dataList);
        recyclerData.setLayoutManager(new LinearLayoutManager(context));
        adapter.bindToRecyclerView(recyclerData);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (checkLogined()) {
                    start_Activity(context, LineDetailsActivity.class, new BasicNameValuePair("id", dataList.get(position).getId()));
                }
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


        /**
         * 热门搜索
         */
        hotList = new ArrayList<>();
        hotAdapter = new HotGridViewAdapter(context, hotList);
        gridViHot.setAdapter(hotAdapter);
        gridViHot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                keyword = hotList.get(position).getName();
                llHot.setVisibility(View.GONE);
                ptrLayt.setVisibility(View.VISIBLE);
                ptrLayt.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrLayt.autoRefresh();
                    }
                }, 50);

                edtTxtSearch.setText(keyword);
                edtTxtSearch.setSelection(edtTxtSearch.getText().toString().length());
                addHot();
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
                page = 1;
                getData();
            }
        });
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("line", "line_list");
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("type", 1);
        params.put("from", "");
        params.put("to", "");
        params.put("keyworks", keyword);

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
                        List<LineList> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), LineList.class);
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
     * 获取热门搜索
     */
    private void getHot() {
        RequestParams params = MyHttpClient.getApiParam("line", "line_keywords");
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
                        List<KeyWorks> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), KeyWorks.class);
                        hotList.clear();
                        hotList.addAll(list);
                        hotAdapter.notifyDataSetChanged();
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
     * 记录搜索内容
     */
    private void addHot() {
        RequestParams params = MyHttpClient.getApiParam("line", "add_line_keywords");
        params.put("name", keyword);
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }

        });
    }

}
