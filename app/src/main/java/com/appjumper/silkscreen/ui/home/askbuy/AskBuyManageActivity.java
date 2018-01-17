package com.appjumper.silkscreen.ui.home.askbuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.MainActivity;
import com.appjumper.silkscreen.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 求购管理
 * Created by Botx on 2017/12/11.
 */

public class AskBuyManageActivity extends BaseActivity {

    @Bind(R.id.frameContent)
    FrameLayout frameContent;
    @Bind(R.id.rdoGroup)
    RadioGroup rdoGroup;

    private List<Fragment> fragList;
    private FragAdapter fragAdapter;

    private int position;
    private String pushId;
    private int pushType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_askbuy_manage);
        ButterKnife.bind(context);

        Intent intent = getIntent();
        position = intent.getIntExtra(Const.KEY_POSITION, 0);
        if (intent.hasExtra("id")) {
            pushId = intent.getStringExtra("id");
            pushType = intent.getIntExtra(Const.KEY_TYPE, 0);
        }

        initBack();
        initData();

        if (position == 1)
            rdoGroup.check(R.id.rb1);
        else
            rdoGroup.check(R.id.rb0);
    }


    private void initData() {
        fragList = new ArrayList<>();
        AskBuyManageFragment askBuyFrag = new AskBuyManageFragment();
        AskBuyManageOfferFragment offerFrag = new AskBuyManageOfferFragment();

        if (!TextUtils.isEmpty(pushId)) {
            Bundle bundle = new Bundle();
            bundle.putString("id", pushId);
            switch (pushType) {
                case Const.PUSH_ASKBUY_CHOOSE_OFFER:
                    askBuyFrag.setArguments(bundle);
                    break;
                case Const.PUSH_ASKBUY_PASS:
                    askBuyFrag.setArguments(bundle);
                    break;
            }
        }

        fragList.add(askBuyFrag);
        fragList.add(offerFrag);

        fragAdapter = new FragAdapter(getSupportFragmentManager());

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


    /**
     * Fragment管理适配器
     */
    private class FragAdapter extends FragmentStatePagerAdapter {

        public FragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
        }
    }


    @Override
    public void finish() {
        super.finish();
        if (MainActivity.instance == null)
            startActivity(new Intent(context, MainActivity.class));
    }

}
