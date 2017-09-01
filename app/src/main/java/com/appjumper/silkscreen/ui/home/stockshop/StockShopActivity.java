package com.appjumper.silkscreen.ui.home.stockshop;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * 现货商城
 * Created by Botx on 2017/8/24.
 */

public class StockShopActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_shop);
        ButterKnife.bind(context);

        initTitle("现货商城");
        initBack();

        FragmentTransaction ftr = getSupportFragmentManager().beginTransaction();
        ftr.replace(R.id.frameContent, new StockShopListFragment());
        ftr.commit();
    }

}
