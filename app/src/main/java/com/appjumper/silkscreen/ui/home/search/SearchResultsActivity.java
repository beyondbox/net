package com.appjumper.silkscreen.ui.home.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.util.Const;
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
 * 搜索结果
 * Created by Botx on 2017/4/10.
 */

public class SearchResultsActivity extends BaseActivity {

    @Bind(R.id.tabLayt)
    TabLayout tabLayt;
    @Bind(R.id.pagerResult)
    ViewPager pagerResult;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.llEmpty)
    LinearLayout llEmpty;

    private ViewPagerFragAdapter resultAdapter;
    private List<Fragment> fragList;
    private List<String> titleList;

    public String keyworks;
    private String [] titleALLArr = {"商城", "订做", "现货", "加工", "物流", "设备", "厂房", "招聘"};

    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(context);
        broadcastManager = LocalBroadcastManager.getInstance(this);

        keyworks = getIntent().getStringExtra("key");
        etSearch.setText(keyworks);
        etSearch.setSelection(keyworks.length());

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 3) {
                /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (etSearch.getText().toString().trim().length() < 1 || etSearch.getText().toString().trim().equals("")) {
                        showErrorToast("搜索内容不能为空");
                    } else {
                        if (inputMethodManager.isActive()) {
                            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
                        }
                        keyworks = etSearch.getText().toString().trim();
                        getMyApplication().getMyUserManager().saveHistory(keyworks);
                        getModules();
                        return true;
                    }

                }
                return false;
            }
        });


        initViewPager();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getModules();
            }
        }, 80);
    }


    private void initViewPager() {
        fragList = new ArrayList<>();
        titleList = new ArrayList<>();

        resultAdapter = new ViewPagerFragAdapter(getSupportFragmentManager(), fragList, titleList);
        pagerResult.setOffscreenPageLimit(titleALLArr.length - 1);
        pagerResult.setAdapter(resultAdapter);

        tabLayt.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayt.setupWithViewPager(pagerResult);
    }



    /**
     * 获取有搜索结果的模块
     */
    private void getModules() {
        RequestParams params = MyHttpClient.getApiParam("service", "service_num");
        params.put("keyworks", keyworks);
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                initProgressDialog();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");

                        /*
                         * 释放掉之前创建的fragment
                         */
                        if (fragList.size() > 0) {
                            FragmentTransaction ftr = getSupportFragmentManager().beginTransaction();
                            for (int i = 0; i < fragList.size(); i++) {
                                Fragment fragment = fragList.get(i);
                                resultAdapter.destroyItem(pagerResult, i, fragment);
                                ftr.remove(fragment);
                            }
                            ftr.commit();
                        }

                        titleList.clear();
                        fragList.clear();

                        /*
                         * 重新设定适配器，实现数据彻底刷新
                         */
                        resultAdapter = null;
                        resultAdapter = new ViewPagerFragAdapter(getSupportFragmentManager(), fragList, titleList);
                        pagerResult.setAdapter(resultAdapter);

                        if (dataObj.getBoolean("goods")) {
                            titleList.add("商城");
                            fragList.add(new SearchGoodsFragment());
                        }

                        if (dataObj.getBoolean("dingzuo")) {
                            titleList.add("订做");
                            fragList.add(new SearchOrderFragment());
                        }

                        /*if (dataObj.getBoolean("xianhuo")) {
                            titleList.add("现货");
                            fragList.add(new SearchStockFragment());
                        }*/

                        if (dataObj.getBoolean("jiagong")) {
                            titleList.add("加工");
                            fragList.add(new SearchProcessFragment());
                        }

                        if (dataObj.getBoolean("line")) {
                            titleList.add("物流");
                            fragList.add(new SearchLogisticsFragment());
                        }

                        if (dataObj.getBoolean("equipment")) {
                            titleList.add("设备");
                            fragList.add(new SearchDeviceFragment());
                        }

                        if (dataObj.getBoolean("workshop")) {
                            titleList.add("厂房");
                            fragList.add(new SearchWorkshopFragment());
                        }

                        if (dataObj.getBoolean("recruit")) {
                            titleList.add("招聘");
                            fragList.add(new SearchJobFragment());
                        }

                        resultAdapter.notifyDataSetChanged();

                        if (fragList.size() > 0) {
                            llEmpty.setVisibility(View.GONE);
                            broadcastManager.sendBroadcast(new Intent(Const.ACTION_SEARCHING_REFRESH));
                        } else {
                            llEmpty.setVisibility(View.VISIBLE);
                        }

                        CommonApi.addLiveness(getUserID(), 3);

                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
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
                    progress.dismiss();
            }
        });
    }



    @OnClick({R.id.tv_cancel, R.id.llEmpty})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                hideKeyboard();
                finish();
                break;
            case R.id.llEmpty:
                break;
            default:
                break;
        }
    }


}
