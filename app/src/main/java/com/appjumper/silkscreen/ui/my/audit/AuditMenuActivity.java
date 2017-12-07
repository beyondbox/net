package com.appjumper.silkscreen.ui.my.audit;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 快速审核菜单
 * Created by Botx on 2017/12/5.
 */

public class AuditMenuActivity extends BaseActivity {

    @Bind(R.id.unReadFreight)
    TextView unReadFreight;
    @Bind(R.id.unReadAskbuy)
    TextView unReadAskbuy;
    @Bind(R.id.unReadPerson)
    TextView unReadPerson;
    @Bind(R.id.unReadDriver)
    TextView unReadDriver;

    public static AuditMenuActivity instance = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_menu);
        ButterKnife.bind(context);
        instance = this;

        initTitle("快速审核");
        initBack();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getUnread();
    }


    /**
     * 获取未处理数
     */
    private void getUnread() {
        RequestParams params = MyHttpClient.getApiParam("collection", "fast_examine_num");
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
                        int numFreight = dataObj.optInt("car_product_num");
                        int numAskbuy = dataObj.optInt("purchase_num");
                        int numPerson = dataObj.optInt("auth_status_num");
                        int numDriver = dataObj.optInt("driver_status_num");

                        if (isDestroyed()) return;

                        unReadFreight.setText(numFreight + "");
                        unReadAskbuy.setText(numAskbuy + "");
                        unReadPerson.setText(numPerson + "");
                        unReadDriver.setText(numDriver + "");

                        unReadFreight.setVisibility(numFreight > 0 ? View.VISIBLE : View.GONE);
                        unReadAskbuy.setVisibility(numAskbuy > 0 ? View.VISIBLE : View.GONE);
                        unReadPerson.setVisibility(numPerson > 0 ? View.VISIBLE : View.GONE);
                        unReadDriver.setVisibility(numDriver > 0 ? View.VISIBLE : View.GONE);
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


    @OnClick({R.id.rlFreight, R.id.rlAskBuy, R.id.rlPerson, R.id.rlDriver})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlFreight:
                start_Activity(context, AuditFreightActivity.class);
                break;
            case R.id.rlAskBuy:
                start_Activity(context, AuditAskBuyActivity.class);
                break;
            case R.id.rlPerson:
                start_Activity(context, AuditPersonActivity.class);
                break;
            case R.id.rlDriver:
                start_Activity(context, AuditDriverActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
