package com.appjumper.silkscreen.ui.dynamic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.ui.my.LoginActivity;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.QBadgeView;

/**
 * 关注
 * Created by Botx on 2017/3/21.
 */

public class AttentionFragment extends BaseFragment {

    @Bind(R.id.tabLayt)
    TabLayout tabLayt;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.llLogin)
    LinearLayout llLogin;
    @Bind(R.id.llEmpty)
    LinearLayout llEmpty;

    private ViewPagerFragAdapter pagerAdapter;
    private List<Fragment> fragList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private List<QBadgeView> badgeList = new ArrayList<>();

    //全部title
    private String [] titleAllArr = {"产品", "物流", "找车", "设备", "厂房", "招聘"};
    //未读数，与title对应的key值
    private String [] unReadKeyArr = {"productNum", "areaNum", "carNum", "equipmentNum", "workshopNum", "jobNum"};

    //标记是否已加载关注的版块
    private boolean isModuleLoaded = false;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attention, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {

        if (getUser() == null) {
            llLogin.setVisibility(View.VISIBLE);
        } else {
            initViewPager2();
            getAttentModule();
            CommonApi.addLiveness(getUserID(), 15);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (isDataInited && getUser() != null && isModuleLoaded) {
            getUnread();
        }
    }


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_LOGIN_SUCCESS);
        getActivity().registerReceiver(myReceiver, filter);
    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_LOGIN_SUCCESS)) {
                if (isDataInited) {
                    llLogin.setVisibility(View.GONE);
                    initViewPager2();
                    getAttentModule();
                    CommonApi.addLiveness(getUserID(), 15);
                }
            }
        }
    };



    private void initViewPager2() {
        pagerAdapter = new ViewPagerFragAdapter(getChildFragmentManager(), fragList, titleList);
        viewPager.setOffscreenPageLimit(titleAllArr.length - 1);
        viewPager.setAdapter(pagerAdapter);
        tabLayt.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayt.setupWithViewPager(viewPager);
    }



    /**
     * 获取用户已经关注的版块
     */
    private void getAttentModule() {
        RequestParams params = MyHttpClient.getApiParam("service", "collectionNum");
        params.put("uid", getUserID());
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                initProgressDialog(false, null);
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");

                        /*
                         * 释放掉之前创建的fragment
                         */
                        if (fragList.size() > 0) {
                            FragmentTransaction ftr = getChildFragmentManager().beginTransaction();
                            for (int i = 0; i < fragList.size(); i++) {
                                Fragment fragment = fragList.get(i);
                                pagerAdapter.destroyItem(viewPager, i, fragment);
                                ftr.remove(fragment);
                            }
                            ftr.commit();
                        }

                        titleList.clear();
                        fragList.clear();
                        if (badgeList.size() > 0) {
                            ((ViewGroup)tabLayt.getChildAt(0)).removeAllViews();
                            badgeList.clear();
                        }

                        /*
                         * 重新设定适配器，实现数据彻底刷新
                         */
                        pagerAdapter = null;
                        pagerAdapter = new ViewPagerFragAdapter(getChildFragmentManager(), fragList, titleList);
                        viewPager.setAdapter(pagerAdapter);


                        if (Integer.parseInt((String)dataObj.getJSONArray("productNum").get(0)) > 0) {
                            titleList.add("产品");
                            fragList.add(new ProductFragment());
                        }

                        if (Integer.parseInt((String)dataObj.getJSONArray("areaNum").get(0)) > 0) {
                            titleList.add("物流");
                            fragList.add(new LogisticsFragment());
                        }

                        if (Integer.parseInt((String)dataObj.getJSONArray("carNum").get(0)) > 0) {
                            titleList.add("找车");
                            fragList.add(new FindCarFragment());
                        }

                        if (Integer.parseInt((String)dataObj.getJSONArray("equipmentNum").get(0)) > 0) {
                            titleList.add("设备");
                            fragList.add(new DeviceFragment());
                        }

                        if (Integer.parseInt((String)dataObj.getJSONArray("workshopNum").get(0)) > 0) {
                            titleList.add("厂房");
                            fragList.add(new WorkShopFragment());
                        }

                        if (Integer.parseInt((String)dataObj.getJSONArray("jobNum").get(0)) > 0) {
                            titleList.add("招聘");
                            fragList.add(new JobFragment());
                        }

                        pagerAdapter.notifyDataSetChanged();
                        isModuleLoaded = true;


                        if (fragList.size() > 0) {
                            getUnread();
                            llEmpty.setVisibility(View.GONE);
                            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
                            broadcastManager.sendBroadcast(new Intent(Const.ACTION_DYNAMIC_REFRESH));
                        } else {
                            llEmpty.setVisibility(View.VISIBLE);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }



    /**
     * 获取未读数
     */
    private void getUnread() {
        RequestParams params = MyHttpClient.getApiParam("inquiry", "noReadNum");
        params.put("uid", getUserID());
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (!isViewCreated) return;

                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");

                        //清掉之前绑定的小红点
                        if (badgeList.size() > 0) {
                            ((ViewGroup)tabLayt.getChildAt(0)).removeAllViews();
                            pagerAdapter.notifyDataSetChanged();
                            badgeList.clear();
                        }

                        //重新绑定小红点
                        for (int i = 0; i < titleList.size(); i++) {
                            int position = 0;
                            for (int j = 0; j < titleAllArr.length; j++) {
                                if (titleList.get(i).equals(titleAllArr[j])) {
                                    position = j;
                                    break;
                                }
                            }

                            int number = dataObj.getJSONArray(unReadKeyArr[position]).optInt(0);
                            QBadgeView badgeView = new QBadgeView(context);
                            badgeView.bindTarget(((ViewGroup)tabLayt.getChildAt(0)).getChildAt(i))
                                    .setBadgeTextSize(8, true)
                                    .setBadgePadding(3, true)
                                    .setGravityOffset(10, 8, true)
                                    .setBadgeNumber(number);

                            /*badgeView.bindTarget(((ViewGroup)tabLayt.getChildAt(0)).getChildAt(i))
                                    .setBadgeTextSize(9, true)
                                    .setBadgeGravity(Gravity.CENTER | Gravity.END)
                                    .setBadgePadding(3, true)
                                    //.setGravityOffset(1, 0, true)
                                    .setBadgeNumber(number);*/

                            badgeList.add(badgeView);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    /**
     * 打开动态管理界面
     */
    public void attentionManage() {
        isModuleLoaded = false;
        Intent intent = new Intent(context, DynamicManageActivity.class);
        startActivityForResult(intent, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Const.RESULT_CODE_NEED_REFRESH) {
            boolean hasChanged = data.getBooleanExtra(Const.KEY_HAS_CHANGED, false);
            if (hasChanged)
                getAttentModule();
            else
                isModuleLoaded = true;
        }
    }



    @OnClick({R.id.txtLogin, R.id.llEmpty})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txtLogin: //立即登录
                intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.llEmpty:
                break;
            default:
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(myReceiver);
    }
}
