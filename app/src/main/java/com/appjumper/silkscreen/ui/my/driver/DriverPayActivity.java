package com.appjumper.silkscreen.ui.my.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.AlipayHelper;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 司机支付
 * Created by Botx on 2017/10/28.
 */

public class DriverPayActivity extends BaseActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;

    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.txtName)
    TextView txtName;
    @Bind(R.id.txtTime)
    TextView txtTime;
    @Bind(R.id.txtState)
    TextView txtState;
    @Bind(R.id.txtOrderId)
    TextView txtOrderId;
    @Bind(R.id.txtCarNum)
    TextView txtCarNum;
    @Bind(R.id.txtCarModel)
    TextView txtCarModel;
    @Bind(R.id.txtProduct)
    TextView txtProduct;
    @Bind(R.id.txtLoadTime)
    TextView txtLoadTime;
    @Bind(R.id.txtPayedType)
    TextView txtPayedType;
    @Bind(R.id.llRemark)
    LinearLayout llRemark;
    @Bind(R.id.txtRemark)
    TextView txtRemark;

    @Bind(R.id.txtPayState)
    TextView txtPayState;

    @Bind(R.id.llCounting)
    LinearLayout llCounting;
    @Bind(R.id.llCountFinish)
    LinearLayout llCountFinish;
    @Bind(R.id.txtCountTime)
    TextView txtCountTime;
    @Bind(R.id.txtPremium)
    TextView txtPremium;


    private String id;
    private Freight data;
    private CountDownTimer countDownTimer;

    private PopupWindow popupPay;
    private TextView txtAccount;
    private TextView txtConfirm;

    private DecimalFormat dFormat = new DecimalFormat("00");
    private long mMs = 1000 * 60; //一分钟的毫秒数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_pay);
        ButterKnife.bind(context);
        initTitle("详情");
        initBack();
        initProgressDialog(false, null);
        initDialog();
        registerBroadcastReceiver();

        id = getIntent().getStringExtra("id");
        getData();
    }


    private void initDialog() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_pay, null);
        popupPay = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        txtAccount = (TextView) contentView.findViewById(R.id.txtAccount);
        txtConfirm = (TextView) contentView.findViewById(R.id.txtConfirm);

        String account = getUser().getMobile();
        txtAccount.setText(account.substring(0, 3) + "***" + account.substring(account.length() - 4, account.length()));
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupPay.dismiss();
                getOrderInfo();
            }
        });

        popupPay.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                AppTool.setBackgroundAlpha(context, 1.0f);
            }
        });

        popupPay.setAnimationStyle(R.style.PopupAnimBottom);
        popupPay.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupPay.setOutsideTouchable(true);
        popupPay.setFocusable(true);
    }



    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details_car_product");
        params.put("id", id);

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
                        llContent.setVisibility(View.VISIBLE);
                        data = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), Freight.class);
                        setData();
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


    /**
     * 渲染数据
     */
    private void setData() {
        txtTitle.setText(data.getFrom_name() + " - " + data.getTo_name());
        txtTime.setText(data.getCreate_time().substring(5, 16));
        txtOrderId.setText("订单编号 : " + data.getOrder_id());
        txtCarNum.setText("已发车" + data.getDepart_num() + "次");
        txtCarModel.setText(data.getLengths_name() + "/" + data.getModels_name());
        txtProduct.setText(data.getWeight() + data.getProduct_name());
        txtLoadTime.setText(data.getExpiry_date().substring(5, 16) + "装车");

        txtState.setText("等待支付");

        String uid = data.getUser_id();
        String newName = "";
        switch (uid.length()) {
            case 1:
                newName = "发货厂家000" + uid;
                break;
            case 2:
                newName = "发货厂家00" + uid;
                break;
            case 3:
                newName = "发货厂家0" + uid;
                break;
            case 4:
                newName = "发货厂家" + uid;
                break;
            default:
                newName = "发货厂家" + uid.substring(0, 1) + uid.substring(uid.length() - 2, uid.length());
                break;
        }
        txtName.setText("来自 : " + newName);


        if (TextUtils.isEmpty(data.getRemark())) {
            llRemark.setVisibility(View.GONE);
        } else {
            txtRemark.setText(data.getRemark());
            llRemark.setVisibility(View.VISIBLE);
        }

        if (data.getPay_type().equals("0"))
            txtPayedType.setText("发货厂家支付运费");
        else
            txtPayedType.setText("货主支付运费");


        if (data.getCar_product_type().equals(Const.INFO_TYPE_OFFICIAL + "")) {
            String endName = "";
            String fullName = data.getTo_name();
            String [] arr = fullName.split(",");
            String province = arr[1];
            if (province.contains("省"))
                endName = province.substring(0, province.length() - 1) + arr[2];
            else
                endName = province + arr[2];

            if (endName.contains("市"))
                endName = endName.substring(0, endName.length() - 1);

            txtTitle.setText(data.getFrom_name() + " - " + endName);
            txtName.setText("来自 : 丝网加物流专员-" + data.getAdmin_name());
        }


        txtPremium.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtPremium.getPaint().setAntiAlias(true);


        long endTime = AppTool.getTimeMs(data.getExpiry_driver_pay_date(), "yyyy-MM-dd HH:mm:ss");
        if (System.currentTimeMillis() < endTime) {
            llCounting.setVisibility(View.VISIBLE);
            llCountFinish.setVisibility(View.GONE);
            startCountDown(endTime);
        } else {
            llCounting.setVisibility(View.GONE);
            llCountFinish.setVisibility(View.VISIBLE);
            txtPayState.setText("已过支付期限");
        }

    }


    /**
     * 开始倒计时
     * @param endTime
     */
    private void startCountDown(long endTime) {
        long millisInFuture = endTime - System.currentTimeMillis();
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {

            @Override
            public void onTick(long l) {
                long minutes = l / mMs;
                long seconds = l % mMs / 1000;
                txtCountTime.setText(dFormat.format(minutes) + " : " + dFormat.format(seconds));
            }

            @Override
            public void onFinish() {
                llCounting.setVisibility(View.GONE);
                llCountFinish.setVisibility(View.VISIBLE);
                txtPayState.setText("已过支付期限");
            }
        }.start();
    }


    /**
     * 获取订单信息
     */
    private void getOrderInfo() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "driver_pay");
        params.put("uid", getUserID());
        params.put("car_product_id", id);
        params.put("pay_money", 0.01);

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
                        String orderInfo = jsonObj.getString("data");
                        AlipayHelper alipayHelper = new AlipayHelper(context, id);
                        alipayHelper.payV2(orderInfo);
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


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_PAY_SUCCESS);
        filter.addAction(Const.ACTION_PAY_FAIL);
        registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_PAY_SUCCESS)) {
                showErrorToast("支付成功");
                Intent intent2 = new Intent(context, GoToDeliverActivity.class);
                intent2.putExtra("id", id);
                startActivity(intent2);
                finish();
            } else if (action.equals(Const.ACTION_PAY_FAIL)) {
                showErrorToast("支付失败");
            }
        }
    };


    @OnClick({R.id.txtCall, R.id.txtPay})
    public void onClick(View view) {
        if (data == null)
            return;

        switch (view.getId()) {
            case R.id.txtCall: //联系客服
                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                break;
            case R.id.txtPay: //支付
                popupPay.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                AppTool.setBackgroundAlpha(context, 0.4f);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
