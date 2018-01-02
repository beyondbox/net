package com.appjumper.silkscreen.ui.home.logistics;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.bean.CarInfo;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.FreightOffer;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.FreightOfferPublicAdapter;
import com.appjumper.silkscreen.ui.my.driver.OfferedActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 空车配货详情--找车中
 * Created by Botx on 2017/10/27.
 */

public class FreightDetailOfferingActivity extends BaseActivity {

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

    @Bind(R.id.llRecord)
    LinearLayout llRecord;
    @Bind(R.id.lvRecord)
    ListView lvRecord;
    @Bind(R.id.txtOffer)
    TextView txtOffer;
    @Bind(R.id.txtRecord)
    TextView txtRecord;

    public static FreightDetailOfferingActivity instance = null;
    private String id;
    private Freight data;
    private AlertDialog offerDialog;
    private EditText edtTxtPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freight_detail_offering);
        ButterKnife.bind(context);
        instance = this;
        initTitle("详情");
        initBack();
        initProgressDialog(false, null);

        id = getIntent().getStringExtra("id");
        getData();
    }


    private void initDialog() {
        offerDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_freight_offer, null);
        offerDialog.setView(view);

        edtTxtPrice = (EditText) view.findViewById(R.id.edtTxtPrice);
        TextView txtPlace = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtSubmitOffer = (TextView) view.findViewById(R.id.txtOffer);

        txtPlace.setText(data.getFrom_name() + " - " + data.getTo_name());
        txtSubmitOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money = edtTxtPrice.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    showErrorToast("请输入报价金额");
                    return;
                }
                offerDialog.dismiss();
                offer(money);
            }
        });

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
                        data = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), Freight.class);
                        setData();
                        llContent.setVisibility(View.VISIBLE);
                        initDialog();
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


        List<FreightOffer> offerList = data.getOffer_list();
        if (offerList != null && offerList.size() > 0) {
            llRecord.setVisibility(View.VISIBLE);
            FreightOfferPublicAdapter recordAdapter = new FreightOfferPublicAdapter(context, offerList, getUserID());
            if (getUser() != null) {
                if (getUser().getAdmin_car_product().equals("1"))
                    recordAdapter.setPrivateMode(false);
            }
            if (getUserID().equals(data.getUser_id())) {
                recordAdapter.setPrivateMode(false);
                txtOffer.setVisibility(View.GONE);
            }

            lvRecord.setAdapter(recordAdapter);
            txtRecord.setText("报价列表（" + offerList.size() + "）");

            if (getUser() != null && getUser().getDriver_status().equals(Const.AUTH_SUCCESS + "")) {
                for (FreightOffer offer : offerList) {
                    if (offer.getUser_id().equals(getUserID())) {
                        txtOffer.setText("您已报价");
                        txtOffer.setEnabled(false);
                        break;
                    }
                }
            }
        } else {
            llRecord.setVisibility(View.GONE);
        }

    }


    /**
     * 获取司机车辆信息
     */
    private void getCarInfo() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "car_info");
        params.put("uid", getUserID());

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
                        CarInfo carInfo = GsonUtil.getEntity(jsonObj.getJSONArray("data").getJSONObject(0).toString(), CarInfo.class);
                        if (carInfo.getCar_length_id().equals(data.getLengths_id()) && carInfo.getCar_model_id().equals(data.getModels_id())) {
                            offerDialog.show();
                        } else {
                            showErrorToast("您的车长车型不符合该订单的要求");
                        }
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
     * 报价
     */
    private void offer(String money) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "offer_driver");
        params.put("order_id", data.getOrder_id());
        params.put("uid", getUserID());
        params.put("car_product_id", id);
        params.put("money", money);

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
                        Intent intent = new Intent(context, OfferedActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
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


    @OnClick({R.id.txtOffer, R.id.txtCall})
    public void onClick(View view) {
        if (data == null)
            return;

        if (!checkLogined())
            return;

        switch (view.getId()) {
            case R.id.txtOffer: //报价
                if (!MyApplication.appContext.checkMobile(context)) return;
                if (!MyApplication.appContext.checkCertifyDriver(context)) return;
                getCarInfo();
                break;
            case R.id.txtCall: //联系客服
                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

}
