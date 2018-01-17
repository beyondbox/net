package com.appjumper.silkscreen.ui.home.company;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.common.adapter.FragAdapter;
import com.appjumper.silkscreen.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 厂家列表
 * Created by Botx on 2018/1/11.
 */

public class CompanyListActivity extends BaseActivity {

    @Bind(R.id.frameContent)
    FrameLayout frameContent;
    @Bind(R.id.rdoGroup)
    RadioGroup rdoGroup;

    private List<Fragment> fragList;
    private FragAdapter fragAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);
        ButterKnife.bind(context);

        CompanyListFragment orderFragment = new CompanyListFragment();
        Bundle bundle0 = new Bundle();
        bundle0.putInt(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_ORDER);
        orderFragment.setArguments(bundle0);

        CompanyListFragment processFragment = new CompanyListFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_PROCESS);
        processFragment.setArguments(bundle1);

        fragList = new ArrayList<>();
        fragList.add(orderFragment);
        fragList.add(processFragment);

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

}
