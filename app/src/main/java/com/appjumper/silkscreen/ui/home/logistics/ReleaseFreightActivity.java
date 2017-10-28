package com.appjumper.silkscreen.ui.home.logistics;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.AreaBean;
import com.appjumper.silkscreen.bean.CarLength;
import com.appjumper.silkscreen.bean.CarModel;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.StartPlace;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 发布空车配货订单
 * Created by Botx on 2017/10/25.
 */

public class ReleaseFreightActivity extends BaseActivity {

    @Bind(R.id.txtStart)
    TextView txtStart;
    @Bind(R.id.txtEnd)
    TextView txtEnd;
    @Bind(R.id.edtTxtName)
    EditText edtTxtName;
    @Bind(R.id.txtCarLength)
    TextView txtCarLength;
    @Bind(R.id.txtCarModel)
    TextView txtCarModel;
    @Bind(R.id.txtWeight)
    TextView txtWeight;
    @Bind(R.id.txtTime)
    TextView txtTime;
    @Bind(R.id.chkProtocol)
    CheckBox chkProtocol;
    @Bind(R.id.txtConfirm)
    TextView txtConfirm;
    @Bind(R.id.rdoGroupPayType)
    RadioGroup rdoGroupPayType;

    public static ReleaseFreightActivity instance = null;

    private String startId = ""; //起始地id
    private String endId = ""; //目的地id
    private int payType = 0; //我来支付0  货主支付1

    private List<String> weightList; //货物重量
    private OptionsPickerView pvWeight;
    private List<StartPlace> startPlaceList; //起始地
    private OptionsPickerView pvStartPlace;
    private TimePickerView pvTime;

    private OptionsPickerView pvLength;
    private OptionsPickerView pvModel;
    private List<CarLength> lengthList;
    private List<CarModel> modelList;

    private String lengthId = ""; //车长id
    private String modelId = ""; //车型id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_freight);
        ButterKnife.bind(context);
        instance = this;
        initTitle("空车配货订单");
        initBack();
        initLocation();
        initProgressDialog(false, "正在发布....");

        getStartPlace();
        getCarLength();
        getCarModel();
        initPickerWeight();
        initPickerTime();

        getLastData();
    }


    /**
     * 获取起始地
     */
    private void getStartPlace() {
        RequestParams params = MyHttpClient.getApiParam("area", "car_from_list");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        startPlaceList = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), StartPlace.class);
                        initPickerStart();
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
     * 获取车长
     */
    private void getCarLength() {
        RequestParams params = MyHttpClient.getApiParam("area", "car_lengths_list");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        lengthList = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), CarLength.class);
                        initPickerLength();
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
     * 获取车型
     */
    private void getCarModel() {
        RequestParams params = MyHttpClient.getApiParam("area", "car_models_list");
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        modelList = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), CarModel.class);
                        initPickerModel();
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


    private void initPickerStart() {
        pvStartPlace = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                txtStart.setText(startPlaceList.get(options1).getFrom_name());
                startId = startPlaceList.get(options1).getId();
            }
        }).build();
        pvStartPlace.setPicker(startPlaceList);
    }

    private void initPickerLength() {
        pvLength = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                txtCarLength.setText(lengthList.get(options1).getCar_lengths_name());
                lengthId = lengthList.get(options1).getId();
            }
        }).build();
        pvLength.setPicker(lengthList);
    }


    private void initPickerModel() {
        pvModel = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                txtCarModel.setText(modelList.get(options1).getCar_models_name());
                modelId = modelList.get(options1).getId();
            }
        }).build();
        pvModel.setPicker(modelList);
    }


    private void initPickerWeight() {
        weightList = new ArrayList<>();
        for (int i = 5; i < 51; i++) {
            weightList.add(i + "吨");
        }

        pvWeight = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                txtWeight.setText(weightList.get(options1));
            }
        }).build();
        pvWeight.setPicker(weightList);
    }

    private void initPickerTime() {
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                txtTime.setText(AppTool.dateFormat(date.getTime(), "yyyy-MM-dd HH:mm"));
            }
        })
                .setContentSize(17)
                .setType(new boolean[]{true, true, true, true, true, false})
                .build();
    }


    /**
     * 获取上次发布的数据
     */
    private void getLastData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "last_car_product");
        params.put("uid", getUserID());
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        Freight freight = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), Freight.class);
                        setLastData(freight);
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
     * 渲染上次发布的数据
     */
    private void setLastData(Freight freight) {
        startId = freight.getFrom_id();
        endId = freight.getTo_id();
        txtStart.setText(freight.getFrom_name());
        txtEnd.setText(freight.getTo_name());

        edtTxtName.setText(freight.getProduct_name());
        edtTxtName.setSelection(freight.getProduct_name().length());
        txtWeight.setText(freight.getWeight());
        lengthId = freight.getLengths_id();
        modelId = freight.getModels_id();
        txtCarLength.setText(freight.getLengths_name());
        txtCarModel.setText(freight.getModels_name());

        payType = Integer.valueOf(freight.getPay_type());
        if (payType == 0)
            rdoGroupPayType.check(R.id.rdoBtnMe);
        else
            rdoGroupPayType.check(R.id.rdoBtnReceiver);
    }


    /**
     * 提交订单
     */
    private void submit() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "add_car_product");
        params.put("uid", getUserID());
        params.put("from_id", startId);
        params.put("from_name", txtStart.getText().toString().trim());
        params.put("to_id", endId);
        params.put("to_name", txtEnd.getText().toString().trim());
        params.put("models_id", modelId);
        params.put("models_name", txtCarModel.getText().toString());
        params.put("lengths_id", lengthId);
        params.put("lengths_name", txtCarLength.getText().toString());
        params.put("weight", txtWeight.getText().toString());
        params.put("product_name", edtTxtName.getText().toString().trim());
        params.put("expiry_date", txtTime.getText().toString() + ":59");
        params.put("user_id", getUserID());
        params.put("pay_type", payType);
        params.put("consignor_lng", longitude);
        params.put("consignor_lat", latitude);

        int infoType;
        Enterprise enterprise = getUser().getEnterprise();
        if (enterprise != null && enterprise.getEnterprise_auth_status().equals("2"))
            infoType = Const.INFO_TYPE_COM;
        else
            infoType = Const.INFO_TYPE_PER;

        params.put("car_product_type", infoType);

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
                        Intent intent = new Intent(context, ReleaseFreightCompleteActivity.class);
                        startActivity(intent);
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


    @OnCheckedChanged(R.id.chkProtocol)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            txtConfirm.setEnabled(true);
        else
            txtConfirm.setEnabled(false);
    }


    @OnCheckedChanged({R.id.rdoBtnMe, R.id.rdoBtnReceiver})
    public void onCheckedChanged2(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rdoBtnMe:
                    payType = 0;
                    break;
                case R.id.rdoBtnReceiver:
                    payType = 1;
                    break;
            }
        }
    }


    @OnClick({R.id.txtStart, R.id.txtEnd, R.id.txtWeight, R.id.txtCarLength, R.id.txtCarModel, R.id.txtTime, R.id.txtConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtStart: //起始地
                if (pvStartPlace != null)
                    pvStartPlace.show();
                break;
            case R.id.txtEnd: //目的地
                Intent intent = new Intent(context, AddressSelectActivity.class);
                intent.putExtra("code", Const.REQUEST_CODE_SELECT_LOGISTICS + "");
                intent.putExtra("type", "0");
                startActivityForResult(intent, Const.REQUEST_CODE_SELECT_LOGISTICS);
                break;
            case R.id.txtWeight: //货物重量
                pvWeight.show();
                break;
            case R.id.txtCarLength: //车长
                if (pvLength != null)
                    pvLength.show();
                break;
            case R.id.txtCarModel: //车型
                if (pvModel != null)
                    pvModel.show();
                break;
            case R.id.txtTime: //装车时间
                pvTime.setDate(Calendar.getInstance());
                pvTime.show();
                break;
            case R.id.txtConfirm: //发布订单
                if (TextUtils.isEmpty(startId)) {
                    showErrorToast("请选择起始地");
                    return;
                }
                if (TextUtils.isEmpty(endId)) {
                    showErrorToast("请选择目的地");
                    return;
                }
                if (TextUtils.isEmpty(edtTxtName.getText().toString().trim())) {
                    showErrorToast("请输入货物名称");
                    return;
                }
                if (TextUtils.isEmpty(txtCarLength.getText().toString().trim())) {
                    showErrorToast("请选择车长");
                    return;
                }
                if (TextUtils.isEmpty(txtCarModel.getText().toString().trim())) {
                    showErrorToast("请选择车型");
                    return;
                }
                if (TextUtils.isEmpty(txtTime.getText().toString().trim())) {
                    showErrorToast("请选择装车时间");
                    return;
                }
                submit();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        switch (requestCode) {
            case Const.REQUEST_CODE_SELECT_LOGISTICS: //选择目的地
                AreaBean areaBean = (AreaBean) data.getSerializableExtra(Const.KEY_OBJECT);
                endId = areaBean.getId();
                String endName = "";
                String fullName = areaBean.getMerger_name();
                String [] arr = fullName.split(",");
                String province = arr[1];
                if (province.contains("省"))
                    endName = province.substring(0, province.length() - 1) + arr[2];
                else
                    endName = province + arr[2];

                if (endName.contains("市"))
                    endName = endName.substring(0, endName.length() - 1);

                txtEnd.setText(endName);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
