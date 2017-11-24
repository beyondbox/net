package com.appjumper.silkscreen.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.PayResult;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 支付宝支付
 * Created by Botx on 2017/11/22.
 */

public class AlipayHelper {

    private static final int SDK_PAY_FLAG = 1;
    private Activity context;
    private String id;
    private MProgressDialog progress;

    public AlipayHelper(Activity context, String id) {
        this.context = context;
        this.id = id;
        progress = new MProgressDialog(this.context, false);
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();

                    /*if (!TextUtils.equals(resultStatus, "6001")) { //6001是取消支付
                        checkResult();
                    }*/

                    if (!TextUtils.equals(resultStatus, "6001")) { //6001是取消支付
                        if (TextUtils.equals(resultStatus, "9000"))
                            updateOrder();
                        else
                            context.sendBroadcast(new Intent(Const.ACTION_PAY_FAIL));
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };


    /**
     * 支付宝支付业务
     */
    public void payV2(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                LogHelper.e("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    /**
     * 获取服务端支付结果
     */
    private void checkResult() {
        RequestParams params = MyHttpClient.getApiParam("alipaynotify", "driver_pay_result");
        params.put("car_product_id", id);

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
                        boolean result = jsonObj.optBoolean("msg");
                        if (result)
                            context.sendBroadcast(new Intent(Const.ACTION_PAY_SUCCESS));
                        else
                            context.sendBroadcast(new Intent(Const.ACTION_PAY_FAIL));
                    } else {
                        context.sendBroadcast(new Intent(Const.ACTION_PAY_FAIL));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, context.getResources().getString(R.string.requst_fail), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 更新订单状态
     */
    private void updateOrder() {
        RequestParams params = MyHttpClient.getApiParam("alipaynotify", "driver_pay_result");
        params.put("car_product_id", id);

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
                        context.sendBroadcast(new Intent(Const.ACTION_PAY_SUCCESS));
                    } else {
                        context.sendBroadcast(new Intent(Const.ACTION_PAY_FAIL));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, context.getResources().getString(R.string.requst_fail), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }

}
