package com.appjumper.silkscreen.ui.home.stockshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.ui.common.adapter.FragAdapter;
import com.appjumper.silkscreen.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 现货商城
 * Created by Botx on 2018/1/11.
 */

public class ShopActivity extends BaseActivity {

    @Bind(R.id.frameContent)
    FrameLayout frameContent;
    @Bind(R.id.rdoGroup)
    RadioGroup rdoGroup;

    private List<Fragment> fragList;
    private FragAdapter fragAdapter;
    private int type;
    private StockGoods incomingProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(context);
        initBack();
        Intent intent = getIntent();
        type = intent.getIntExtra(Const.KEY_TYPE, Const.SHOP_TYPE_STOCK);
        if (intent.hasExtra(Const.KEY_OBJECT))
            incomingProduct = (StockGoods) intent.getSerializableExtra(Const.KEY_OBJECT);

        //库存处理
        ShopFragment fragment0 = new ShopFragment();
        Bundle bundle0 = new Bundle();
        bundle0.putInt(Const.KEY_TYPE, Const.SHOP_TYPE_STOCK);
        if (incomingProduct != null && type == Const.SHOP_TYPE_STOCK)
            bundle0.putSerializable(Const.KEY_OBJECT, incomingProduct);
        fragment0.setArguments(bundle0);

        //厂家委托
        ShopFragment fragment1 = new ShopFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(Const.KEY_TYPE, Const.SHOP_TYPE_COMPANY);
        if (incomingProduct != null && type == Const.SHOP_TYPE_COMPANY)
            bundle1.putSerializable(Const.KEY_OBJECT, incomingProduct);
        fragment1.setArguments(bundle1);

        fragList = new ArrayList<>();
        fragList.add(fragment0);
        fragList.add(fragment1);

        fragAdapter = new FragAdapter(getSupportFragmentManager(), fragList);

        rdoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb0:
                        switchFragment(0);
                        break;
                    case R.id.rb1:
                        switchFragment(1);
                        break;
                    default:
                        break;
                }
            }
        });


        if (type == Const.SHOP_TYPE_COMPANY)
            rdoGroup.check(R.id.rb1);
        else
            rdoGroup.check(R.id.rb0);
    }


    /**
     * 切换Fragment
     * @param position
     */
    private void switchFragment(int position) {
        Fragment fragment = (Fragment) fragAdapter.instantiateItem(frameContent, position);
        fragAdapter.setPrimaryItem(frameContent, position, fragment);
        fragAdapter.finishUpdate(frameContent);
    }


    @OnClick({R.id.imgViSearch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgViSearch:
                Intent intent = new Intent(context, ShopSearchActivity.class);
                if (rdoGroup.getCheckedRadioButtonId() == R.id.rb0)
                    intent.putExtra(Const.KEY_TYPE, Const.SHOP_TYPE_STOCK);
                else
                    intent.putExtra(Const.KEY_TYPE, Const.SHOP_TYPE_COMPANY);

                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
