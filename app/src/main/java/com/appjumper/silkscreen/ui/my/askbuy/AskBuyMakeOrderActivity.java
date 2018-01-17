package com.appjumper.silkscreen.ui.my.askbuy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Address;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.AskBuyOffer;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.home.askbuy.AskBuyDetailActivity;
import com.appjumper.silkscreen.ui.home.askbuy.AskBuyManageDetailActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 采购--提交订单
 * Created by Botx on 2017/10/21.
 */

public class AskBuyMakeOrderActivity extends BaseActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.imgViHead)
    ImageView imgViHead;
    @Bind(R.id.txtContent)
    TextView txtContent;

    @Bind(R.id.txtPrice)
    TextView txtPrice;
    @Bind(R.id.txtNum)
    TextView txtNum;
    @Bind(R.id.txtTotal)
    TextView txtTotal;

    @Bind(R.id.txtConsigner)
    TextView txtConsigner;
    @Bind(R.id.txtMobile)
    TextView txtMobile;
    @Bind(R.id.txtAddress)
    TextView txtAddress;
    @Bind(R.id.edtTxtRemark)
    EditText edtTxtRemark;
    @Bind(R.id.switchTrans)
    SwitchCompat switchTrans;
    @Bind(R.id.txtConfirm)
    TextView txtConfirm;
    @Bind(R.id.txtProtocol)
    TextView txtProtocol;


    private String id;
    private AskBuy data;
    private AskBuyOffer offer;
    private double payMoney;

    private Address address; //收货地址
    private int siwangjia_short = 0; //是否由丝网+进行短途运输（0-是，默认；1-否）


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_askbuy_make_order2);
        ButterKnife.bind(context);
        initBack();
        initTitle("创建订单");
        initProgressDialog(false, null);

        id = getIntent().getStringExtra("id");
        offer = (AskBuyOffer) getIntent().getSerializableExtra(Const.KEY_OBJECT);
        getData();
        getLastAddress();
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details");
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
     * 获取上次提交订单的地址
     */
    private void getLastAddress() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "select_purchase_address");
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        address = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), Address.class);
                        txtConsigner.setText(address.getName());
                        txtMobile.setText(address.getMobile());
                        txtAddress.setText(address.getAddress());
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
     * 渲染数据
     */
    private void setData() {
        Picasso.with(context)
                .load(data.getImg())
                .resize(DisplayUtil.dip2px(context, 60), DisplayUtil.dip2px(context, 60))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgViHead);

        txtContent.setText(data.getPurchase_content());
        txtPrice.setText("¥" + offer.getMoney() + offer.getPrice_unit());
        txtNum.setText(data.getProduct_name() + " " + data.getPurchase_num() + data.getPurchase_unit());
        calculatePayMoney();

        txtProtocol.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtProtocol.getPaint().setAntiAlias(true);
    }


    /**
     * 提交订单
     */
    private void makeOrder() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "purchase_commit_order");
        params.put("uid", getUserID());
        params.put("pay_money", AppTool.df.format(payMoney));
        params.put("product_id", data.getProduct_id());
        params.put("product_name", data.getProduct_name());
        params.put("product_img", data.getImg());
        params.put("purchase_id", data.getId());
        params.put("pay_type", Const.PAY_TYPE_ALL);
        params.put("purchase_num", data.getPurchase_num());
        params.put("offer_money", offer.getMoney());
        params.put("offer_id", offer.getUser_id());
        params.put("purchase_unit", data.getPurchase_unit());
        params.put("offer_id", offer.getUser_id());

        params.put("remarks", edtTxtRemark.getText().toString().trim());
        params.put("siwangjia_short", siwangjia_short);
        params.put("name", address.getName());
        params.put("mobile", address.getMobile());
        params.put("address_type", address.getAddress_type());
        params.put("purchase_content", data.getPurchase_content());
        params.put("address", address.getAddress());

        if (address.getAddress_type().equals("0")) {
            String [] arr = address.getAddress().split("\\s");
            params.put("station_name", arr[0]);
            params.put("station_address", arr[1]);
            params.put("station_id", address.getStation_id());
        }

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
                        saveAddress();
                        if (AskBuyManageDetailActivity.instance != null) AskBuyManageDetailActivity.instance.finish();
                        if (AskBuyDetailActivity.instance != null) AskBuyDetailActivity.instance.finish();
                        showErrorToast("提交订单成功");
                        start_Activity(context, AskBuyOrderDetailActivity.class, new BasicNameValuePair("id", (String) jsonObj.getJSONArray("data").get(0)));
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
                if (isDestroyed()) return;
                progress.dismiss();
            }
        });
    }


    /**
     * 计算需要支付的金额
     */
    private void calculatePayMoney() {
        double price = Double.valueOf(offer.getMoney());
        int num = Integer.valueOf(data.getPurchase_num());
        payMoney = price * num;

        payMoney = Double.valueOf(AppTool.dfRound.format(payMoney));
        txtTotal.setText("¥ " + AppTool.df.format(payMoney) + "元");
    }


    /**
     * 保存收货地址
     */
    private void saveAddress() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "add_purchase_address");
        params.put("uid", getUserID());
        params.put("mobile", address.getMobile());
        params.put("name", address.getName());
        params.put("address_type", address.getAddress_type());
        params.put("address", address.getAddress());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    @OnCheckedChanged({R.id.switchTrans, R.id.chkProtocol})
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switchTrans:
                if (b)
                    siwangjia_short = 0;
                else
                    siwangjia_short = 1;
                break;
            case R.id.chkProtocol:
                if (b)
                    txtConfirm.setEnabled(true);
                else
                    txtConfirm.setEnabled(false);
                break;
        }
    }


    @OnClick({R.id.txtConfirm, R.id.rlAddress, R.id.txtProtocol})
    public void onClick(View view) {
        if (data == null)
            return;

        Intent intent = null;
        switch (view.getId()) {
            case R.id.txtConfirm:
                if (TextUtils.isEmpty(txtAddress.getText().toString().trim())) {
                    showErrorToast("请选择收货地址");
                    return;
                }
                makeOrder();
                break;
            case R.id.rlAddress: //选择地址
                intent = new Intent(context, AddAddressActivity.class);
                intent.putExtra(Const.KEY_OBJECT, address);
                startActivityForResult(intent, Const.REQUEST_CODE_SELECT_ADDRESS);
                break;
            case R.id.txtProtocol: //协议
                User user = getUser();
                String name = TextUtils.isEmpty(user.getName()) ? "" : user.getName();

                String product = "";
                if (data.getPurchase_num().equals("0"))
                    product = data.getProduct_name();
                else
                    product = data.getProduct_name() + data.getPurchase_num() + data.getPurchase_unit();

                String url = Url.PROTOCOL_ASKBUY_ORDER + "?name=" + name + "&phone=" + user.getMobile() + "&goods=" + product + "&danjia=" + offer.getMoney() + offer.getPrice_unit() + "&time=24";
                start_Activity(context, WebViewActivity.class, new BasicNameValuePair("title", "协议内容"), new BasicNameValuePair("url", url));
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        if (requestCode == Const.REQUEST_CODE_SELECT_ADDRESS) {
            address = (Address) data.getSerializableExtra(Const.KEY_OBJECT);
            txtConsigner.setText(address.getName());
            txtMobile.setText(address.getMobile());
            txtAddress.setText(address.getAddress());
        }
    }
}
