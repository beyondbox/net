package com.appjumper.silkscreen.ui.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.ui.common.adapter.FragAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 动态
 * Created by Botx on 2017/10/17.
 */

public class DynamicFragment extends BaseFragment {

    @Bind(R.id.left)
    TextView txtLeft;
    @Bind(R.id.right)
    TextView txtRight;
    @Bind(R.id.frameContent)
    FrameLayout frameContent;
    @Bind(R.id.rdoGroup)
    RadioGroup rdoGroup;

    private List<Fragment> fragList;
    private FragAdapter fragAdapter;
    private RelatedFragment relatedFragment;
    private NewsFragment newsFragment;
    private AttentionFragment attentionFragment;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        relatedFragment = new RelatedFragment();
        newsFragment = new NewsFragment();
        attentionFragment = new AttentionFragment();

        fragList = new ArrayList<>();
        fragList.add(relatedFragment);
        fragList.add(newsFragment);
        fragList.add(attentionFragment);

        fragAdapter = new FragAdapter(getChildFragmentManager(), fragList);

        rdoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb0:
                        switchFragment(0);
                        txtRight.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.rb1:
                        switchFragment(1);
                        txtRight.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.rb2:
                        switchFragment(2);
                        txtRight.setVisibility(View.VISIBLE);
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


    @OnClick({R.id.right})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.right:
                if (checkLogined()) attentionFragment.attentionManage();
                break;
            default:
                break;
        }
    }

}
