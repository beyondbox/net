package com.appjumper.silkscreen.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.ui.home.adapter.RecommendAdapter;
import com.appjumper.silkscreen.util.Const;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 推荐企业Fragment
 * Created by Botx on 2017/4/18.
 */

public class RecommendFragment extends Fragment {

    @Bind(R.id.lvData)
    ListView lvData;

    private RecommendAdapter dataAdapter;
    private List<Enterprise> dataList;

    private FragmentActivity context;
    private int serviceType;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        serviceType = getArguments().getInt(Const.KEY_SERVICE_TYPE);
        initListView();
    }


    private void initListView() {
        View emptyView = LayoutInflater.from(context).inflate(R.layout.pull_listitem_empty_padding, null);
        ViewGroup parentView = (ViewGroup)lvData.getParent();
        parentView.addView(emptyView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lvData.setEmptyView(emptyView);

        dataList = new ArrayList<>();
        dataAdapter = new RecommendAdapter(context, dataList);
        lvData.setAdapter(dataAdapter);

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, CompanyDetailsActivity.class);
                intent.putExtra("from", "2");
                intent.putExtra("id", dataList.get(i).getEnterprise_id());
                startActivity(intent);
            }
        });
    }


    /**
     * 刷新数据
     * @param list
     */
    public void refresh(List<Enterprise> list) {
        if (dataList != null) {
            dataList.clear();
            dataList.addAll(list);
            dataAdapter.notifyDataSetChanged();
        }

    }


    @OnClick({R.id.txtMore})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txtMore:
                intent  = new Intent(context, EnterpriseListActivity.class);
                intent.putExtra(Const.KEY_SERVICE_TYPE, serviceType);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
