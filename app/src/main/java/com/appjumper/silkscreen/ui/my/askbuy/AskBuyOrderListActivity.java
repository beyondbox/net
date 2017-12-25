package com.appjumper.silkscreen.ui.my.askbuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.MainActivity;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import q.rorbin.badgeview.QBadgeView;

/**
 * 求购订单列表
 * Created by Botx on 2017/12/14.
 */

public class AskBuyOrderListActivity extends BaseActivity {

    @Bind(R.id.tabLayt)
    TabLayout tabLayt;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    public static AskBuyOrderListActivity instance = null;

    private QBadgeView badgeAudit;
    private QBadgeView badgePaying;
    private QBadgeView badgeFinishing;

    private ViewPagerFragAdapter pagerAdapter;
    private List<Fragment> fragList;
    private String [] titleArr = {"全部", "待审核", "待付款", "待完成", "交易完成"};

    private String [] typeArr = {"",
            Const.ASKBUY_ORDER_AUDITING + "",
            Const.ASKBUY_ORDER_PAYING + "",
            Const.ASKBUY_ORDER_RECEIPTING + "",
            Const.ASKBUY_ORDER_FINISH + ""};

    private int position;
    private String pushId;
    private int pushType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_askbuy_orderlist);
        ButterKnife.bind(context);
        instance = this;
        initBack();
        initTitle("我的采购订单");

        Intent intent = getIntent();
        position = intent.getIntExtra(Const.KEY_POSITION, 0);
        if (intent.hasExtra("id")) {
            pushId = intent.getStringExtra("id");
            pushType = intent.getIntExtra(Const.KEY_TYPE, 0);
        }

        initViewPager();
        initUnread();

        viewPager.setCurrentItem(position);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getUnread();
    }


    private void initViewPager() {
        fragList = new ArrayList<>();
        for (int i = 0; i < titleArr.length; i++) {
            Fragment fragment = new AskBuyOrderListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Const.KEY_TYPE, typeArr[i]);

            if (i == 0) {
                if (!TextUtils.isEmpty(pushId)) {
                    bundle.putString("id", pushId);
                    bundle.putInt(Const.KEY_PUSH_TYPE, pushType);
                }
            }

            fragment.setArguments(bundle);
            fragList.add(fragment);
        }

        pagerAdapter = new ViewPagerFragAdapter(getSupportFragmentManager(), fragList, Arrays.asList(titleArr));
        viewPager.setOffscreenPageLimit(titleArr.length - 1);
        viewPager.setAdapter(pagerAdapter);

        tabLayt.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayt.setupWithViewPager(viewPager);

        for (int i = 0; i < titleArr.length; i++) {
            TabLayout.Tab tab = tabLayt.getTabAt(i);
            if (tab != null) {
                View customView = LayoutInflater.from(context).inflate(R.layout.layout_tab_askbuy_orderlist, null);
                TextView txtName = (TextView) customView.findViewById(R.id.txtName);
                txtName.setText(titleArr[i]);
                tab.setCustomView(customView);
            }
        }
    }


    private void initUnread() {
        badgeAudit = new QBadgeView(this);
        badgePaying = new QBadgeView(this);
        badgeFinishing = new QBadgeView(this);

        badgeAudit.bindTarget(tabLayt.getTabAt(1).getCustomView().findViewById(R.id.txtName))
                .setBadgeTextSize(9, true)
                .setBadgePadding(2, true)
                .setGravityOffset(-1, 2, true);

        badgePaying.bindTarget(tabLayt.getTabAt(2).getCustomView().findViewById(R.id.txtName))
                .setBadgeTextSize(9, true)
                .setBadgePadding(2, true)
                .setGravityOffset(-1, 2, true);

        badgeFinishing.bindTarget(tabLayt.getTabAt(3).getCustomView().findViewById(R.id.txtName))
                .setBadgeTextSize(9, true)
                .setBadgePadding(2, true)
                .setGravityOffset(-1, 2, true);

    }


    /**
     * 获取求购订单未处理数
     */
    private void getUnread() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "my_purchase_order_num");
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        int auditNum = dataObj.optInt("wait_examine");
                        int payNum = dataObj.optInt("wait_pay");
                        int finishingNum = dataObj.optInt("wait_confirm");

                        badgeAudit.setBadgeNumber(auditNum);
                        badgePaying.setBadgeNumber(payNum);
                        badgeFinishing.setBadgeNumber(finishingNum);
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


    @Override
    public void finish() {
        super.finish();
        if (MainActivity.instance == null)
            startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
