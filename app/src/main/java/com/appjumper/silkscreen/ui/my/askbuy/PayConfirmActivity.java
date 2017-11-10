package com.appjumper.silkscreen.ui.my.askbuy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 确认付款
 * Created by Botx on 2017/11/6.
 */

public class PayConfirmActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm);
        ButterKnife.bind(context);
        initTitle("确认付款");
        initBack();
        initProgressDialog(false, null);
    }


    /**
     * 确认付款
     */
    private void makeOrder() {
        Intent intent = getIntent();
        AskBuy data = (AskBuy) intent.getSerializableExtra(Const.KEY_OBJECT);

        RequestParams params = MyHttpClient.getApiParam("purchase", "order_pay");
        params.put("uid", getUserID());
        params.put("pay_money", intent.getStringExtra("pay_money"));
        params.put("product_id", data.getProduct_id());
        params.put("product_name", data.getProduct_name());
        params.put("purchase_id", data.getId());
        params.put("pay_type", intent.getStringExtra("pay_type"));
        params.put("purchase_num", intent.getStringExtra("purchase_num"));
        params.put("offer_money", intent.getStringExtra("offer_money"));
        params.put("purchase_unit", intent.getStringExtra("purchase_unit"));
        params.put("surplus_money", intent.getStringExtra("surplus_money"));

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
                        if (ChooseOfferActivity.instance != null)
                            ChooseOfferActivity.instance.finish();
                        if (AskBuyMakeOrderActivity.instance != null)
                            AskBuyMakeOrderActivity.instance.finish();
                        if (AskBuyOrderDetailActivity.instance != null)
                            AskBuyOrderDetailActivity.instance.finish();

                        sendBroadcast(new Intent(Const.ACTION_RELEASE_SUCCESS));
                        showErrorToast("请等待平台确认收款");
                        finish();
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
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
                if (isDestroyed())
                    return;

                progress.dismiss();
            }
        });
    }



    @OnClick({R.id.txtConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm:
                makeOrder();
                break;
        }
    }

}
