package com.appjumper.silkscreen.ui.dynamic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.bean.RecruitList;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.dynamic.adapter.RelatedDeviceAdapter;
import com.appjumper.silkscreen.ui.dynamic.adapter.RelatedJobAdapter;
import com.appjumper.silkscreen.ui.dynamic.adapter.RelatedWorkshopAdapter;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentActivity;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentDetailsActivity;
import com.appjumper.silkscreen.ui.home.recruit.RecruitActivity;
import com.appjumper.silkscreen.ui.home.recruit.RecruitDetailsActivity;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopActivity;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopDetailsActivity;
import com.appjumper.silkscreen.util.Const;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 周边
 * Created by Botx on 2018/1/12.
 */

public class RelatedFragment extends BaseFragment {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.gridWorkShop)
    GridView gridWorkShop;
    @Bind(R.id.gridJob)
    GridView gridJob;
    @Bind(R.id.gridDevice)
    GridView gridDevice;

    private List<EquipmentList> workshopList;
    private List<RecruitList> jobList;
    private List<EquipmentList> deviceList;

    private RelatedWorkshopAdapter workshopAdapter;
    private RelatedJobAdapter jobAdapter;
    private RelatedDeviceAdapter deviceAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_related, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        initGridView();
        initRefreshLayout();

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 50);
    }


    private void initGridView() {
        workshopList = new ArrayList<>();
        jobList = new ArrayList<>();
        deviceList = new ArrayList<>();

        workshopAdapter = new RelatedWorkshopAdapter(context, workshopList);
        gridWorkShop.setAdapter(workshopAdapter);
        gridWorkShop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkLogined())
                    start_Activity(context, WorkshopDetailsActivity.class, new BasicNameValuePair("id", workshopList.get(i).getId()));
            }
        });

        jobAdapter = new RelatedJobAdapter(context, jobList);
        gridJob.setAdapter(jobAdapter);
        gridJob.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkLogined())
                    start_Activity(context, RecruitDetailsActivity.class, new BasicNameValuePair("id", jobList.get(i).getId()));
            }
        });

        deviceAdapter = new RelatedDeviceAdapter(context, deviceList);
        gridDevice.setAdapter(deviceAdapter);
        gridDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkLogined())
                    start_Activity(context, EquipmentDetailsActivity.class, new BasicNameValuePair("id", deviceList.get(i).getId()));
            }
        });
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrLayt.setLastUpdateTimeRelateObject(this);
        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getData();
            }
        });
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("workshop", "periphery_list");

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<EquipmentList> wList = GsonUtil.getEntityList(dataObj.getJSONArray("workshop").toString(), EquipmentList.class);
                        List<RecruitList> jList = GsonUtil.getEntityList(dataObj.getJSONArray("recruit").toString(), RecruitList.class);
                        List<EquipmentList> dList = GsonUtil.getEntityList(dataObj.getJSONArray("equipment").toString(), EquipmentList.class);

                        workshopList.clear();
                        workshopList.addAll(wList);
                        workshopAdapter.notifyDataSetChanged();

                        jobList.clear();
                        jobList.addAll(jList);
                        jobAdapter.notifyDataSetChanged();

                        deviceList.clear();
                        deviceList.addAll(dList);
                        deviceAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (!isViewCreated) return;
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isViewCreated) return;
                ptrLayt.refreshComplete();
            }
        });
    }


    @OnClick({R.id.txtMore0, R.id.txtMore1, R.id.txtMore2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtMore0:
                start_Activity(getActivity(), WorkshopActivity.class);
                break;
            case R.id.txtMore1:
                start_Activity(getActivity(), RecruitActivity.class);
                break;
            case R.id.txtMore2:
                start_Activity(getActivity(), EquipmentActivity.class);
                break;
        }
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

}
