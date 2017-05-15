package com.appjumper.silkscreen.ui.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.ui.dynamic.adapter.DynamicAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 动态
 * Created by Botx on 2017/3/21.
 */

public class DynamicFragment extends BaseFragment {

    @Bind(R.id.back)
    ImageView imgViBack;
    @Bind(R.id.title)
    TextView txtTitle;
    @Bind(R.id.right)
    TextView txtRight;
    @Bind(R.id.ptrDynamic)
    PtrClassicFrameLayout ptrDynamic;
    @Bind(R.id.recyclerDynamic)
    RecyclerView recyclerDynamic;

    private DynamicAdapter dynamicAdapter;
    private List<String> dynamicList;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        ButterKnife.bind(this, view);
        imgViBack.setVisibility(View.GONE);
        txtTitle.setText("动态");
        txtRight.setText("管理");
        return view;
    }


    @Override
    protected void initData() {
        initRecyclerView();
        setRefreshLayout();
        CommonApi.addLiveness(getUserID(), 15);
    }


    private void initRecyclerView() {
        dynamicList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dynamicList.add("");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerDynamic.setLayoutManager(layoutManager);

        dynamicAdapter = new DynamicAdapter(context, dynamicList);
        recyclerDynamic.setAdapter(dynamicAdapter);
    }

    /**
     * 设置下拉刷新
     */
    private void setRefreshLayout() {
        ptrDynamic.setLastUpdateTimeRelateObject(this);
        ptrDynamic.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrDynamic.refreshComplete();
                    }
                }, 1500);
            }
        });
    }

    @OnClick({R.id.right})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.right: //管理
                if (checkLogined()) {
                    intent = new Intent(context, DynamicManageActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
     }

}
