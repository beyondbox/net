package com.appjumper.silkscreen.ui.my.driver;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Freight;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.MultiSelectPhotoActivity;
import com.appjumper.silkscreen.ui.my.adapter.TransportListAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Applibrary;
import com.appjumper.silkscreen.util.CommonUtil;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.ImageUtil;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 运输途中--司机端
 * Created by Botx on 2017/10/28.
 */

public class TransportingDriverActivity extends MultiSelectPhotoActivity {

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

    @Bind(R.id.txtPremium)
    TextView txtPremium;
    @Bind(R.id.btn1)
    TextView btn1;
    @Bind(R.id.btn2)
    TextView btn2;


    private String id;
    private Freight data;
    private AlertDialog arriveDialog;
    private ImageView imgViUpload;
    private TextView txtConfirm;
    private File tempImageFile;
    private String arriveImgUrl = "";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporting_driver);
        ButterKnife.bind(context);
        initTitle("详情");
        initBack();
        initProgressDialog(false, null);

        setCropSingleImage(false);
        setSingleImage(true);
        setCropTaskPhoto(false);

        initDialog();

        id = getIntent().getStringExtra("id");
        progress.show();
        getData();
    }


    private void initDialog() {
        arriveDialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_arrived, null);
        arriveDialog.setView(view);

        imgViUpload = (ImageView) view.findViewById(R.id.imgViUpload);
        txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        imgViUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWindowSelectList(view);
            }
        });
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(arriveImgUrl)) {
                    showErrorToast("请上传货主签字照片");
                    return;
                }
                arriveDialog.dismiss();
                confirmArrived();
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

            progress.dismiss();
            switch (msg.what) {
                case 3://上传图片
                    ImageResponse imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        arriveImgUrl = imgResponse.getData().get(0).getOrigin();
                        Glide.with(context).load(arriveImgUrl).placeholder(R.mipmap.img_error).into(imgViUpload);
                    } else {
                        showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    showErrorToast("网络超时，请稍候");
                    break;
            }
        }
    };



    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "details_car_product");
        params.put("id", id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

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
        String arriveState = data.getConfirm_arrive();
        if (arriveState.equals("0") || arriveState.equals("1") || arriveState.equals("2")) {
            btn1.setVisibility(View.GONE);
            btn2.setVisibility(View.GONE);
        } else {
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
        }

        txtTitle.setText(data.getFrom_name() + " - " + data.getTo_name());
        txtTime.setText(data.getCreate_time().substring(5, 16));
        txtOrderId.setText("订单编号 : " + data.getOrder_id());
        txtCarNum.setText("已发车" + data.getDepart_num() + "次");
        txtCarModel.setText(data.getLengths_name() + "/" + data.getModels_name());
        txtProduct.setText(data.getWeight() + data.getProduct_name());
        txtLoadTime.setText(data.getExpiry_date().substring(5, 16) + "装车");

        txtState.setText("运输途中");

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

        ListView lvTransport = (ListView) findViewById(R.id.lvTransport);
        lvTransport.setFocusable(false);
        TransportListAdapter transportAdapter = new TransportListAdapter(context, data.getTransport_list());
        lvTransport.setAdapter(transportAdapter);
    }


    /**
     * 确认送达
     */
    private void confirmArrived() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "driver_confirm_arrive");
        params.put("car_product_id", id);
        params.put("uid", getUserID());
        params.put("confirm_arrive_img", arriveImgUrl);

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
                        showErrorToast("提交成功，请等待官方确认");
                        getData();
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
     * 更新位置
     */
    private void updateLocation(double lat, double lng, String address) {
        RequestParams params = MyHttpClient.getApiParam("purchase", "driver_position");
        params.put("uid", getUserID());
        params.put("driver_lat", lat + "");
        params.put("driver_lng", lng + "");
        params.put("place_name", address);
        params.put("car_product_id", id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        showErrorToast("更新成功");
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




    @OnClick({R.id.txtCall, R.id.btn0, R.id.btn1, R.id.btn2})
    public void onClick(View view) {
        if (data == null)
            return;

        switch (view.getId()) {
            case R.id.txtCall: //联系客服
                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                break;
            case R.id.btn0: //联系厂家
                if (data.getCar_product_type().equals(Const.INFO_TYPE_OFFICIAL + ""))
                    AppTool.dial(context, data.getEnterprise_mobile());
                else
                    AppTool.dial(context, data.getMobile());
                break;
            case R.id.btn1: //更新位置
                CommonUtil.getLocation(new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        if (aMapLocation.getErrorCode() == 0) {
                            updateLocation(aMapLocation.getLatitude(), aMapLocation.getLongitude(), aMapLocation.getAddress());
                        } else {
                            showErrorToast("定位失败: " + aMapLocation.getErrorInfo());
                        }
                    }
                });
                break;
            case R.id.btn2: //确认送达
                arriveDialog.show();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tempImageFile != null) {
            if (tempImageFile.exists())
                tempImageFile.delete();
        }
    }

}
