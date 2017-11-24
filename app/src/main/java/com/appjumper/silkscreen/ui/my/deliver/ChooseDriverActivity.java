package com.appjumper.silkscreen.ui.my.deliver;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.DriverInfo;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.FreightOffer;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.my.adapter.ChooseDriverAdapter;
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
 * 选择司机
 * Created by Botx on 2017/10/28.
 */

public class ChooseDriverActivity extends BaseActivity {

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
    @Bind(R.id.txtRecord)
    TextView txtRecord;

    private String id;
    private Freight data;
    private FreightOffer selectedOffer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_driver);
        ButterKnife.bind(context);
        initTitle("详情");
        initBack();
        initProgressDialog(false, null);

        id = getIntent().getStringExtra("id");
        getData();
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

        txtState.setText("等待司机报价");

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


        final List<FreightOffer> offerList = data.getOffer_list();
        if (offerList != null && offerList.size() > 0) {
            llRecord.setVisibility(View.VISIBLE);
            ChooseDriverAdapter recordAdapter = new ChooseDriverAdapter(context, offerList);
            lvRecord.setAdapter(recordAdapter);
            txtRecord.setText("报价列表（" + offerList.size() + "）");

            lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    getDriverInfo(offerList.get(i));
                }
            });

            recordAdapter.setOnWhichClickListener(new MyBaseAdapter.OnWhichClickListener() {
                @Override
                public void onWhichClick(View view, int position, int tag) {
                    switch (view.getId()) {
                        case R.id.txtHandle:
                            selectedOffer = offerList.get(position);
                            showConfirmDialog();
                            break;
                    }
                }
            });
        } else {
            llRecord.setVisibility(View.GONE);
        }

    }


    /**
     * 获取司机信息
     */
    private void getDriverInfo(FreightOffer offer) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details_driver");
        params.put("id", offer.getId());
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        DriverInfo driver = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), DriverInfo.class);
                        showDriverInfoDialog(driver);
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
        });
    }


    /**
     * 司机信息对话框
     */
    private void showDriverInfoDialog(DriverInfo driver) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_driver_info, null);
        dialog.setView(view);

        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        TextView txtCarModel  = (TextView) view.findViewById(R.id.txtCarModel);
        TextView txtYear = (TextView) view.findViewById(R.id.txtYear);
        TextView txtMileage = (TextView) view.findViewById(R.id.txtMileage);
        TextView txtOfferNum = (TextView) view.findViewById(R.id.txtOfferNum);

        txtName.setText(driver.getName().substring(0, 1) + "司机");
        txtCarModel.setText(driver.getVehicle_brand());
        txtYear.setText(driver.getLicense_years());
        txtMileage.setText(driver.getLicense_kilometers());
        txtOfferNum.setText(driver.getOffer_num());

        dialog.show();
    }


    /**
     * 确认选择对话框
     */
    private void showConfirmDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_choose_driver, null);
        dialog.setView(view);

        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        TextView txtPrice = (TextView) view.findViewById(R.id.txtPrice);
        TextView txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);

        txtName.setText(selectedOffer.getName().substring(0, 1) + "司机");
        txtPrice.setText(selectedOffer.getMoney() + selectedOffer.getMoney_unit());
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getDriverState();
            }
        });

        dialog.show();
    }


    /**
     * 获取司机状态
     */
    private void getDriverState() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "select_driver");
        params.put("driver_id", selectedOffer.getUser_id());
        params.put("offer_id", selectedOffer.getId());

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
                        chooseDriver();
                    } else {
                        progress.dismiss();
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    progress.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progress.dismiss();
                showFailTips(getResources().getString(R.string.requst_fail));
            }
        });
    }


    /**
     * 确认选择司机
     */
    private void chooseDriver() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "confirm_driver");
        params.put("driver_id", selectedOffer.getUser_id());
        params.put("offer_id", selectedOffer.getId());
        params.put("car_product_id", id);
        params.put("driver_offer", selectedOffer.getMoney());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        Intent intent = new Intent(context, WaitDriverPayActivity.class);
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
                progress.dismiss();
            }
        });
    }


    @OnClick({R.id.txtCall})
    public void onClick(View view) {
        if (data == null)
            return;

        switch (view.getId()) {
            case R.id.txtCall: //联系客服
                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                break;
            default:
                break;
        }
    }

}
