package com.appjumper.silkscreen.ui.my.askbuy;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.appjumper.silkscreen.util.ImageUtil;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * 求购订单支付
 * Created by Botx on 2017/10/19.
 */

public class AskBuyOrderPayActivity extends BaseActivity {

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
    @Bind(R.id.txtWeight)
    TextView txtWeight;

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

    @Bind(R.id.llBottomBar)
    LinearLayout llBottomBar;
    @Bind(R.id.txtHandle1)
    TextView txtHandle1;

    private ClipboardManager cm;
    private String id;
    private AskBuy data;
    private PopupWindow popupPay;
    private String payType = "";
    private SureOrCancelDialog cancelDialog;

    private String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + "picture";
    File wechatFile = new File(savePath, "pay_wechat.jpg");
    File alipayFile = new File(savePath, "pay_alipay.jpg");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_buy_order_pay);
        ButterKnife.bind(context);
        initBack();
        initTitle("订单详情");
        initProgressDialog(false, null);
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        id = getIntent().getStringExtra("id");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initDialog();
            }
        }, 200);
        getData();
    }


    private void initDialog() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_payed_confirm, null);
        popupPay = new PopupWindow(contentView, txtHandle1.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView btn0 = (TextView) contentView.findViewById(R.id.btn0);
        TextView btn1 = (TextView) contentView.findViewById(R.id.btn1);
        TextView btn2 = (TextView) contentView.findViewById(R.id.btn2);
        TextView btn3 = (TextView) contentView.findViewById(R.id.btn3);

        PayTypeClickListener listener = new PayTypeClickListener();
        btn0.setOnClickListener(listener);
        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);

        popupPay.setAnimationStyle(R.style.PopupAnimBottom);
        popupPay.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupPay.setOutsideTouchable(true);
        popupPay.setFocusable(true);


        cancelDialog = new SureOrCancelDialog(context, "提示", "确定要取消该订单吗？", "确定", "取消", new SureOrCancelDialog.SureButtonClick() {
            @Override
            public void onSureButtonClick() {
                cancelOrder();
            }
        });
    }


    private class PayTypeClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn0:
                    payType = "公户";
                    break;
                case R.id.btn1:
                    payType = "工行卡";
                    break;
                case R.id.btn2:
                    payType = "微信帐户";
                    break;
                case R.id.btn3:
                    payType = "支付宝帐户";
                    break;
            }

            payConfirm();
        }
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
        txtOrderId.setText(data.getOrder_id());
        txtWeight.setText(data.getWeight() + data.getWeight_unit());


        Picasso.with(context)
                .load(data.getAdviser_avatar())
                .placeholder(R.mipmap.img_error_head)
                .error(R.mipmap.img_error_head)
                .into(imgViAvatar);

        txtAdviserName.setText("丝网+官方交易顾问-" + data.getAdviser_nicename());

        txtConsigner.setText(data.getName());
        txtMobile.setText(data.getMobile());
        txtAddress.setText(data.getAddress());
        txtRemark.setText(data.getRemarks());
    }


    /**
     * 确认支付
     */
    private void payConfirm() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "confirm_purchase_order");
        params.put("uid", getUserID());
        params.put("pay_type", payType);
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
                        showErrorToast("确认付款成功");
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


    @OnClick({R.id.txtCall, R.id.txtHandle0, R.id.txtHandle1, R.id.txtCarNum0, R.id.txtCarNum1})
    public void onClick(View view) {
        if (data == null)
            return;

        switch (view.getId()) {
            case R.id.txtCall: //咨询顾问
                if (data != null && !TextUtils.isEmpty(data.getAdviser_mobile()))
                    AppTool.dial(context, data.getAdviser_mobile());
                break;
            case R.id.txtHandle0: //取消订单
                cancelDialog.show();
                break;
            case R.id.txtHandle1: //确认支付
                popupPay.showAtLocation(txtHandle1, Gravity.BOTTOM | Gravity.RIGHT, DisplayUtil.dip2px(context, 5), txtHandle1.getHeight() + DisplayUtil.dip2px(context, 7));
                break;
            case R.id.txtCarNum0: //复制民生卡号
                cm.setText("153743959");
                showErrorToast("已复制对公账户卡号");
                break;
            case R.id.txtCarNum1: //复制工行卡号
                cm.setText("6212260407008303635");
                showErrorToast("已复制工商银行卡号");
                break;
            default:
                break;
        }
    }


    @OnLongClick({R.id.imgViWeChat, R.id.imgViAlipay})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.imgViWeChat:
                if (wechatFile.exists()) wechatFile.delete();
                if (ImageUtil.saveBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.qrcode_wechat), 100, wechatFile))
                    showErrorToast("图片已保存到/sdcard/picture目录下");
                else
                    showErrorToast("图片保存失败");
                break;
            case R.id.imgViAlipay:
                if (alipayFile.exists()) alipayFile.delete();
                if (ImageUtil.saveBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.qrcode_alipay), 100, alipayFile))
                    showErrorToast("图片已保存到/sdcard/picture目录下");
                else
                    showErrorToast("图片保存失败");
                break;
        }
        return true;
    }

}
