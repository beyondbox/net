package com.appjumper.silkscreen.ui.my.driver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.CarLength;
import com.appjumper.silkscreen.bean.CarModel;
import com.appjumper.silkscreen.bean.DriverAuth;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.MultiSelectPhotoActivity;
import com.appjumper.silkscreen.util.Applibrary;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.ImageUtil;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 司机认证第三步
 * Created by Botx on 2017/10/24.
 */

public class DriverAuthThirdActivity extends MultiSelectPhotoActivity {

    @Bind(R.id.edtTxtBrand)
    EditText edtTxtBrand;
    @Bind(R.id.edtTxtYear)
    EditText edtTxtYear;
    @Bind(R.id.edtTxtMileage)
    EditText edtTxtMileage;
    @Bind(R.id.txtWeight)
    TextView txtWeight;
    @Bind(R.id.txtCarLength)
    TextView txtCarLength;
    @Bind(R.id.txtCarModel)
    TextView txtCarModel;

    @Bind(R.id.imgVi0)
    ImageView imgVi0;
    @Bind(R.id.imgVi1)
    ImageView imgVi1;
    @Bind(R.id.imgVi2)
    ImageView imgVi2;

    private File tempImageFile;
    private int mark = 0;
    private List<CarLength> lengthList;
    private List<CarModel> modelList;
    private DriverAuth driverAuth;

    private List<String> weightList; //载重
    private OptionsPickerView pvWeight;

    private OptionsPickerView pvLength;
    private OptionsPickerView pvModel;
    private int lengthIndex = 0; //选择的车长位置
    private int modelIndex = 0; //选择的车型位置


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_auth_third);
        ButterKnife.bind(this);
        initTitle("司机认证-车辆信息");
        initBack();
        initProgressDialog(false, null);
        initPickerWeight();

        setCropSingleImage(false);
        setSingleImage(true);
        setCropTaskPhoto(false);

        driverAuth = (DriverAuth) getIntent().getSerializableExtra(Const.KEY_OBJECT);
        getCarLength();
        getCarModel();
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
                        txtCarLength.setText(lengthList.get(0).getCar_lengths_name());
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
                        txtCarModel.setText(modelList.get(0).getCar_models_name());
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


    private void initPickerLength() {
        pvLength = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                txtCarLength.setText(lengthList.get(options1).getCar_lengths_name());
                lengthIndex = options1;
            }
        }).build();
        pvLength.setPicker(lengthList);
    }


    private void initPickerModel() {
        pvModel = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                txtCarModel.setText(modelList.get(options1).getCar_models_name());
                modelIndex = options1;
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

    /**
     * 提交认证
     */
    private void submit() {
        RequestParams params = MyHttpClient.getApiParam("user", "driver_auth");
        params.put("uid", getUserID());
        params.put("vehicle_brand", edtTxtBrand.getText().toString().trim());
        params.put("license_years", edtTxtYear.getText().toString().trim() + "年");
        params.put("license_kilometers", edtTxtMileage.getText().toString().trim() + "万公里");
        params.put("weight", txtWeight.getText().toString());
        params.put("car_model_id", modelList.get(modelIndex).getId());
        params.put("car_model_name", modelList.get(modelIndex).getCar_models_name());
        params.put("car_length_id", lengthList.get(lengthIndex).getId());
        params.put("car_length_name", lengthList.get(lengthIndex).getCar_lengths_name());
        params.put("driver_img", driverAuth.getImgDriver());
        params.put("driver_img_back", driverAuth.getImgDriverBack());
        params.put("driving_license_img", driverAuth.getImgDriving());
        params.put("driving_license_img_back", driverAuth.getImgDrivingBack());
        params.put("driver_car_img", driverAuth.getImgGroup());
        params.put("driver_lng", getLng());
        params.put("driver_lat", getLat());
        params.put("name", driverAuth.getName());
        /*params.put("idcard", driverAuth.getID());
        params.put("idcard_img", driverAuth.getImgIDCard());
        params.put("idcard_img_back", driverAuth.getImgIDCardBack());*/

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
                        if (DriverAuthFirstActivity.instance != null)
                            DriverAuthFirstActivity.instance.finish();
                        if (DriverAuthSecondActivity.instance != null)
                            DriverAuthSecondActivity.instance.finish();

                        Intent intent = new Intent(context, DriverAuthCompleteActivity.class);
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


    @Override
    protected void requestImage(String[] path) {
        String origPath = path[0];

        if (tempImageFile != null) {
            if (tempImageFile.exists())
                tempImageFile.delete();
        }
        tempImageFile = new File(Applibrary.IMAGE_CACHE_DIR, System.currentTimeMillis() + ".jpg");
        boolean result = ImageUtil.saveBitmap(ImageUtil.compressImage(origPath, 1920, 1080), 80, tempImageFile);
        if (result) {
            initProgressDialog();
            progress.show();
            new Thread(new UpdateStringRun(tempImageFile.getPath())).start();
        }
    }


    public class UpdateStringRun implements Runnable {
        private File upLoadBitmapFile;

        public UpdateStringRun(String newPicturePath) {
            this.upLoadBitmapFile = new File(newPicturePath);
        }

        @Override
        public void run() {
            ImageResponse retMap = null;
            try {
                String url = Url.UPLOADIMAGE;
                retMap = JsonParser.getImageResponse(HttpUtil.uploadFile(url, upLoadBitmapFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (retMap != null) {
                handler.sendMessage(handler.obtainMessage(3, retMap));
            } else {
                handler.sendMessage(handler.obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDestroyed())
                return;

            if(progress!=null){
                progress.dismiss();
            }
            switch (msg.what) {
                case 3://上传图片
                    ImageResponse imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        switch (mark){
                            case 0:
                                driverAuth.setImgDriving(imgResponse.getData().get(0).getOrigin());
                                Glide.with(context).load(driverAuth.getImgDriving()).placeholder(R.mipmap.upload_driving_front).into(imgVi0);
                                break;
                            case 1:
                                driverAuth.setImgDrivingBack(imgResponse.getData().get(0).getOrigin());
                                Glide.with(context).load(driverAuth.getImgDrivingBack()).placeholder(R.mipmap.upload_driving_back).into(imgVi1);
                                break;
                            case 2:
                                driverAuth.setImgGroup(imgResponse.getData().get(0).getOrigin());
                                Glide.with(context).load(driverAuth.getImgGroup()).placeholder(R.mipmap.upload_group).into(imgVi2);
                                break;
                        }
                    } else {
                        showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    showErrorToast("网络超时，请稍候");
                    break;
                default:
                    showErrorToast();
                    break;
            }
        }
    };


    @OnClick({R.id.imgVi0, R.id.imgVi1, R.id.imgVi2, R.id.txtPrevious, R.id.txtConfirm, R.id.txtCarLength, R.id.txtCarModel, R.id.txtWeight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgVi0:
                mark = 0;
                showWindowSelectList(view);
                break;
            case R.id.imgVi1:
                mark = 1;
                showWindowSelectList(view);
                break;
            case R.id.imgVi2:
                mark = 2;
                showWindowSelectList(view);
                break;
            case R.id.txtPrevious:
                finish();
                break;
            case R.id.txtWeight:
                pvWeight.show();
                break;
            case R.id.txtCarLength:
                if (pvLength != null)
                    pvLength.show();
                break;
            case R.id.txtCarModel:
                if (pvModel != null)
                    pvModel.show();
                break;
            case R.id.txtConfirm:
                if (TextUtils.isEmpty(edtTxtBrand.getText().toString().trim())) {
                    showErrorToast("请输入车辆品牌");
                    return;
                }
                if (TextUtils.isEmpty(edtTxtYear.getText().toString().trim())) {
                    showErrorToast("请输入行驶年限");
                    return;
                }
                if (TextUtils.isEmpty(edtTxtMileage.getText().toString().trim())) {
                    showErrorToast("请输入行驶公里数");
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
                if (TextUtils.isEmpty(driverAuth.getImgDriving())) {
                    showErrorToast("请上传行驶证正面照片");
                    return;
                }
                if (TextUtils.isEmpty(driverAuth.getImgDrivingBack())) {
                    showErrorToast("请上传行驶证反面照片");
                    return;
                }
                if (TextUtils.isEmpty(driverAuth.getImgGroup())) {
                    showErrorToast("请上传手持证件与车辆合照");
                    return;
                }
                submit();
                break;
            default:
                break;
        }
    }

}
