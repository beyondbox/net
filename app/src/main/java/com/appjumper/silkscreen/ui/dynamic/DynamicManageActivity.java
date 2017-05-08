package com.appjumper.silkscreen.ui.dynamic;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
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

/**
 * 动态管理
 * Created by Botx on 2017/3/28.
 */

public class DynamicManageActivity extends BaseActivity {

    @Bind(R.id.tabLaytAttention)
    TabLayout tabLaytAttention;
    @Bind(R.id.pagerAttention)
    ViewPager pagerAttention;

    private ViewPagerFragAdapter pagerAdapter;
    private List<Fragment> fragList;
    private List<String> titleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_manage);
        ButterKnife.bind(this);

        initBack();
        initTitle("动态管理");

        initViewPager();
        getAttentNum();
    }

    private void initViewPager() {
        fragList = new ArrayList<>();
        fragList.add(new AttentCompanyFragment());
        fragList.add(new AttentProductFragment());
        fragList.add(new AttentModuleFragment());

        titleList = new ArrayList<>();
        titleList.add("关注的企业");
        titleList.add("关注的产品");
        titleList.add("关注的版块");

        pagerAdapter = new ViewPagerFragAdapter(getSupportFragmentManager(), fragList, titleList);
        pagerAttention.setAdapter(pagerAdapter);
        pagerAttention.setOffscreenPageLimit(fragList.size() - 1);
        tabLaytAttention.setupWithViewPager(pagerAttention);
    }


    /**
     * 获取关注的数量
     */
    public void getAttentNum() {
        RequestParams params = MyHttpClient.getApiParam("collection", "collectionNum");
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
                        int companyNum = dataObj.getJSONArray("enterprise").optInt(0);
                        int productNum = dataObj.getJSONArray("product").optInt(0);
                        int moduleNum = dataObj.getJSONArray("collection").optInt(0);

                        if (companyNum > 0)
                            titleList.set(0, "关注的企业" + "(" + companyNum + ")");
                        else
                            titleList.set(0, "关注的企业");

                        if (productNum > 0)
                            titleList.set(1, "关注的产品" + "(" + productNum + ")");
                        else
                            titleList.set(1, "关注的产品");

                        if (moduleNum > 0)
                            titleList.set(2, "关注的版块" + "(" + moduleNum + ")");
                        else
                            titleList.set(2, "关注的版块");

                        pagerAdapter.notifyDataSetChanged();
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

}
