package com.appjumper.silkscreen.ui.home.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.LogHelper;

import java.util.ArrayList;
import java.util.Arrays;
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

    private ViewPagerFragAdapter resultAdapter;
    private List<Fragment> fragList;

    public String keyworks;
    private String [] titleArr = {"订做", "现货", "加工", "物流", "设备", "厂家", "招聘"};

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
                        broadcastManager.sendBroadcast(new Intent(Const.ACTION_SEARCHING_REFRESH));
                        CommonApi.addLiveness(getUserID(), 3);
                        return true;
                    }

                }
                return false;
            }
        });


        initViewPager();

        initLocation();
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        latitude = aMapLocation.getLatitude();//获取纬度
                        longitude = aMapLocation.getLongitude();//获取经度
                        accuracy = aMapLocation.getAccuracy();//获取精度信息
                        mlocationClient.stopLocation();
                        broadcastManager.sendBroadcast(new Intent(Const.ACTION_SEARCHING_REFRESH));
                    }
                }
            }
        });

        CommonApi.addLiveness(getUserID(), 3);
    }


    private void initViewPager() {
        fragList = new ArrayList<>();
        fragList.add(new SearchOrderFragment());
        fragList.add(new SearchStockFragment());
        fragList.add(new SearchProcessFragment());
        for (int i = 0; i < 4; i++) {
            fragList.add(new SearchOrderFragment());
        }

        resultAdapter = new ViewPagerFragAdapter(getSupportFragmentManager(), fragList, Arrays.asList(titleArr));
        pagerResult.setAdapter(resultAdapter);
        pagerResult.setOffscreenPageLimit(titleArr.length - 1);

        tabLayt.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayt.setupWithViewPager(pagerResult);
    }


    @OnClick(R.id.tv_cancel)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                hideKeyboard();
                finish();
                break;
            default:
                break;
        }
    }


}
