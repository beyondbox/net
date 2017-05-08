package com.appjumper.silkscreen.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.ui.home.adapter.RecommendAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 推荐企业Fragment
 * Created by Botx on 2017/4/18.
 */

public class RecommendFragment extends BaseFragment {

    @Bind(R.id.lvData)
    ListView lvData;

    private RecommendAdapter dataAdapter;
    private List<Enterprise> dataList;

    @Override
    protected void initData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListView();
    }

    private void initListView() {
        dataList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Enterprise enterprise = new Enterprise();
            dataList.add(enterprise);
        }

        dataAdapter = new RecommendAdapter(context, dataList);
        lvData.setAdapter(dataAdapter);
    }

    @OnClick({R.id.txtMore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtMore:
                start_Activity(getActivity(), EnterpriseListActivity.class);
                break;
            default:
                break;
        }
    }
}
