package com.appjumper.silkscreen.ui.my.askbuy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 求购订单详情
 * Created by Botx on 2017/10/19.
 */

public class AskBuyOrderDetailActivity extends BaseActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.imgViHead)
    ImageView imgViHead;
    @Bind(R.id.txtContent)
    TextView txtContent;
    @Bind(R.id.txtProduct)
    TextView txtProduct;
    @Bind(R.id.txtState)
    TextView txtState;
    @Bind(R.id.txtPrice)
    TextView txtPrice;
    @Bind(R.id.txtNum)
    TextView txtNum;
    @Bind(R.id.txtOrderId)
    TextView txtOrderId;
    @Bind(R.id.txtTotal)
    TextView txtTotal;

    @Bind(R.id.imgViAvatar)
    ImageView imgViAvatar;
    @Bind(R.id.txtAdviserName)
    TextView txtAdviserName;
    @Bind(R.id.txtCall)
    TextView txtCall;

    @Bind(R.id.txtConsigner)
    TextView txtConsigner;
    @Bind(R.id.txtMobile)
    TextView txtMobile;
    @Bind(R.id.txtAddress)
    TextView txtAddress;

    @Bind(R.id.txtTrans)
    TextView txtTrans;
    @Bind(R.id.txtRemark)
    TextView txtRemark;
    @Bind(R.id.txtHint)
    TextView txtHint;

    @Bind(R.id.llBottomBar)
    LinearLayout llBottomBar;
    @Bind(R.id.btn0)
    TextView btn0;
    @Bind(R.id.btn1)
    TextView btn1;

    private String id;
    private AskBuy data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_buy_order_detail);
        ButterKnife.bind(context);
        initBack();
        initTitle("订单详情");
        initProgressDialog(false, null);

        id = getIntent().getStringExtra("id");
        getData();
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details_purchase_order");
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
                        data = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), AskBuy.class);
                        setData();
                        llContent.setVisibility(View.VISIBLE);
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (isDestroyed()) return;
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestroyed()) return;
                progress.dismiss();
            }
        });
    }


    /**
     * 渲染数据
     */
    private void setData() {
        Picasso.with(context)
                .load(data.getProduct_img())
                .resize(DisplayUtil.dip2px(context, 60), DisplayUtil.dip2px(context, 60))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgViHead);

        txtProduct.setText(data.getProduct_name());
        txtContent.setText(data.getPurchase_content());
        txtPrice.setText(data.getOffer_money() + "元/" + data.getPurchase_unit());
        txtNum.setText("x " + data.getPurchase_num() + data.getPurchase_unit());
        txtTotal.setText("¥" + data.getPay_money());

        if (TextUtils.isEmpty(data.getOrder_id()))
            txtOrderId.setText("无");
        else
            txtOrderId.setText(data.getOrder_id());


        Picasso.with(context)
                .load(data.getAdviser_avatar())
                .placeholder(R.mipmap.img_error_head)
                .error(R.mipmap.img_error_head)
                .into(imgViAvatar);

        txtAdviserName.setText("丝网+官方交易顾问-" + data.getAdviser_nicename());

        int state = Integer.valueOf(data.getExamine_status());
        switch (state) {
            case Const.ASKBUY_ORDER_AUDITING: //待审核
                txtState.setText("待审核");
                txtHint.setText(getResources().getString(R.string.askbuy_auditing));
                btn1.setText("取消订单");
                break;
            case Const.ASKBUY_ORDER_REFUSE: //审核拒绝
                txtState.setText("审核失败");
                txtHint.setText("拒绝原因: " + data.getExamine_refusal_reason());
                btn1.setText("删除订单");
                break;
            case Const.ASKBUY_ORDER_RECEIPTING: //待完成
                txtState.setText("待完成");
                txtHint.setText("已确认支付: " + data.getPay_type() + "\n" + getResources().getString(R.string.askbuy_receipting));
                llBottomBar.setVisibility(View.GONE);
                break;
            case Const.ASKBUY_ORDER_FINISH: //交易完成
                txtState.setText("交易完成");
                txtHint.setText("已确认支付: " + data.getPay_type() + "\n" + getResources().getString(R.string.askbuy_finish));
                btn1.setText("删除订单");
                break;
        }

        txtConsigner.setText(data.getName());
        txtMobile.setText(data.getMobile());
        txtAddress.setText(data.getAddress());
        txtRemark.setText(data.getRemarks());

        if (data.getSiwangjia_short().equals("0"))
            txtTrans.setText("由丝网+进行短途运输 (送到货站结运费)");
        else
            txtTrans.setText("自己负责运输");
    }


    /**
     * 取消订单
     */
    private void cancelOrder() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "cancel_purchase_order");
        params.put("uid", getUserID());
        params.put("purchase_id", data.getPurchase_id());

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
                        showErrorToast("取消订单成功");
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
                progress.dismiss();
            }
        });
    }


    /**
     * 删除订单
     */
    private void deleteOrder() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "hide_purchase_order");
        params.put("uid", getUserID());
        params.put("purchase_id", data.getPurchase_id());

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
                        showErrorToast("删除订单成功");
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
                progress.dismiss();
            }
        });
    }


    @OnClick({R.id.txtCall, R.id.btn1})
    public void onClick(View view) {
        if (data == null)
            return;

        switch (view.getId()) {
            case R.id.txtCall:
                if (data != null && !TextUtils.isEmpty(data.getAdviser_mobile()))
                    AppTool.dial(context, data.getAdviser_mobile());
                break;
            case R.id.btn1:
                if (data.getExamine_status().equals(Const.ASKBUY_ORDER_AUDITING + "")) {
                    new SureOrCancelDialog(context, "提示", "确定要取消该订单吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                        @Override
                        public void onSureButtonClick() {
                            cancelOrder();
                        }
                    }).show();
                } else {
                    new SureOrCancelDialog(context, "提示", "确定要删除该订单吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
                        @Override
                        public void onSureButtonClick() {
                            deleteOrder();
                        }
                    }).show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        if (AskBuyOrderListActivity.instance == null)
            start_Activity(context, AskBuyOrderListActivity.class);
    }
}
