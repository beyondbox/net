package com.appjumper.silkscreen.ui.my.audit;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.AuditUser;
import com.appjumper.silkscreen.net.GsonUtil;
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
 * 个人认证审核
 * Created by Botx on 2017/12/5.
 */

public class AuditPersonActivity extends BaseActivity {

    @Bind(R.id.txtCount)
    TextView txtCount;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private List<AuditUser> dataList = new ArrayList<>();
    private List<Fragment> fragList = new ArrayList<>();
    private ViewPagerFragAdapter pagerAdapter;

    private int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_freight);
        ButterKnife.bind(context);

        initTitle("个人认证");
        initBack();
        initProgressDialog(false, null);

        getData();
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "wait_auth_status");
        params.put("page", 1);
        params.put("pagesize", 50);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
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
                        List<AuditUser> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), AuditUser.class);
                        count = dataObj.optInt("total");
                        txtCount.setText("未审核信息" + count + "条");
                        dataList.clear();
                        dataList.addAll(list);

                        fragList.clear();
                        for (AuditUser user : dataList) {
                            AuditPersonFragment fragment = new AuditPersonFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Const.KEY_OBJECT, user);
                            fragment.setArguments(bundle);
                            fragList.add(fragment);
                        }

                        pagerAdapter = new ViewPagerFragAdapter(getSupportFragmentManager(), fragList, null);
                        viewPager.setOffscreenPageLimit(fragList.size() - 1);
                        viewPager.setAdapter(pagerAdapter);
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


    public void updateState() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                count--;
                txtCount.setText("未审核信息" + count + "条");

                int position = viewPager.getCurrentItem();
                if (position < fragList.size() - 1)
                    viewPager.setCurrentItem(position + 1, true);
            }
        }, 800);
    }

    @Override
    public void finish() {
        super.finish();
        if (AuditMenuActivity.instance == null)
            start_Activity(context, AuditMenuActivity.class);
    }
}
