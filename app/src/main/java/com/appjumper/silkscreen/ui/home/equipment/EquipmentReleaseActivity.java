package com.appjumper.silkscreen.ui.home.equipment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.EquipmentCategoryResponse;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-18.
 * 发布设备出售
 */
public class EquipmentReleaseActivity extends BasePhotoGridActivity {
    @Bind(R.id.et_title)//标题
            EditText etTitle;
    @Bind(R.id.tv_equipment_name)//设备名称
            TextView tvEquipmentName;
    @Bind(R.id.et_equipment_brand)//设备品牌
            EditText etEquipmentBrand;
    @Bind(R.id.et_equipment_model)//设备型号
            EditText etEquipmentModel;
    @Bind(R.id.et_equipment_price)//设备价格
            EditText etEquipmentPrice;
    @Bind(R.id.et_remark)//备注
            EditText etRemark;
    @Bind(R.id.tv_info_length)//信息时长
            TextView tvInfoLength;

    private long expiry_datatime = 3600 * 72;
    private String[] expiry = {"3天", "5天", "10天", "30天"};//信息时长

    private String degree = "全新";
    private ImageResponse imgResponse;
    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_equipment_release);
        initBack();
        initView();
        ButterKnife.bind(this);
        initTitle("设备出售");
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if (etTitle.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入标题");
                    return;
                }
                if (tvEquipmentName.getText().toString().length() < 1) {
                    showErrorToast("请选择出售设备名称");
                    return;
                }
                if (etEquipmentBrand.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入设备品牌");
                    return;
                }
                if (etEquipmentModel.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入设备型号");
                    return;
                }
                if (etEquipmentPrice.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入设备价格");
                    return;
                }

                hideKeyboard();
                initProgressDialog();
                progress.show();
                progress.setMessage("正在发布...");
                new Thread(new UpdateStringRun(thumbPictures)).start();

            }
        });

    }


    @OnCheckedChanged({R.id.rdoBtnNew, R.id.rdoBtnUsed})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rdoBtnNew:
                    degree = "全新";
                    break;
                case R.id.rdoBtnUsed:
                    degree = "二手";
                    break;
                default:
                    break;
            }
        }
    }


    // 如果不是切割的upLoadBitmap就很大
    public class UpdateStringRun implements Runnable {
        private ArrayList<String> thumbPictures;

        // thumbPictures 是 List<压缩图路径>
        public UpdateStringRun(ArrayList<String> thumbPictures) {
            this.thumbPictures = new ArrayList<String>();
            for (String str : thumbPictures) {
                if (!str.equals("" + BasePhotoGridActivity.PICTURE_UPDATE_ICON)) {
                    //去掉最后一个 +图片
                    this.thumbPictures.add(str);
                }
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            ImageResponse retMap = null;
            try {
                // 如果不是切割的upLoadBitmap就很大,在这里压缩
                retMap = JsonParser.getImageResponse(HttpUtil.upload(thumbPictures, Url.UPLOADIMAGE));
            } catch (Exception e) {
                // TODO Auto-generated catch block XDEBUG_SESSION_START=1
                e.printStackTrace();
            }
            if (retMap != null) {
                handler.sendMessage(handler.obtainMessage(
                        8, retMap));
            } else {
                handler.sendMessage(handler
                        .obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
            }
        }
    }

    private Runnable submitRun = new Runnable() {
        private BaseResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();

                int infoType;
                Enterprise enterprise = getUser().getEnterprise();
                if (enterprise != null && enterprise.getEnterprise_auth_status().equals("2"))
                    infoType = Const.INFO_TYPE_COM;
                else
                    infoType = Const.INFO_TYPE_PER;

                data.put("equipment_type", infoType + "");
                data.put("uid", getUserID());
                data.put("title", etTitle.getText().toString().trim());
                data.put("items", json);
                data.put("remark", etRemark.getText().toString().trim());
                data.put("expiry_date", expiry_datatime + "");
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.EQUIPMENT_RELEASE));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    private String imags(List<ImageResponse.Data> data) {
        String str = "";
        for (int i = 0; i < data.size(); i++) {
            str += data.get(i).getImg_id();
            if (i < (data.size() - 1)) {
                str += ",";
            }
        }
        return str;
    }

    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {

        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            EquipmentReleaseActivity activity = (EquipmentReleaseActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case 8://上传图片
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", tvEquipmentName.getText().toString());
                        jsonObject.put("brand", etEquipmentBrand.getText().toString().trim());
                        jsonObject.put("model", etEquipmentModel.getText().toString().trim());
                        jsonObject.put("new_old_rate", degree);
                        jsonObject.put("price", etEquipmentPrice.getText().toString().trim());
                        jsonObject.put("imgs", imags(imgResponse.getData()));

                        jsonArray.add(jsonObject);
                        json = jsonArray.toJSONString();
                        new Thread(submitRun).start();
                    } else {
                        progress.dismiss();
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT://发布
                    progress.dismiss();
                    BaseResponse baseResponse = (BaseResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        showErrorToast("发布成功");
                        sendBroadcast(new Intent(Const.ACTION_RELEASE_SUCCESS));
                        CommonApi.addLiveness(getUserID(), 19);
                        finish();
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }

                    break;
                case NETWORK_SUCCESS_DATA_RIGHT:
                    progress.dismiss();
                    EquipmentCategoryResponse response = (EquipmentCategoryResponse) msg.obj;
                    if (response.isSuccess()) {

                    } else {
                        showErrorToast(response.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
            }
        }
    }



    @OnClick({R.id.tv_equipment_name, R.id.tv_info_length})
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tv_equipment_name:
                startForResult_Activity(EquipmentReleaseActivity.this, SelectActivity.class, 13, new BasicNameValuePair("title", "设备名称"),new BasicNameValuePair("type","1"));
                break;
            case R.id.tv_info_length:
                intent = new Intent(EquipmentReleaseActivity.this, InformationSelectActivity.class);
                Bundle b = new Bundle();
                b.putStringArray("val", expiry);
                intent.putExtras(b);
                intent.putExtra("title", "信息时长");
                startActivityForResult(intent, 12);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 12://信息时长
                int expiry_date = Integer.parseInt(data.getStringExtra("val"));
                switch (expiry_date) {
                    case 0://3天
                        expiry_datatime = 3600 * 72;
                        break;
                    case 1://5天
                        expiry_datatime = 3600 * 120;
                        break;
                    case 2://10天
                        expiry_datatime = 3600 * 240;
                        break;
                    case 3://30天
                        expiry_datatime = 3600 * 720;
                        break;
                }
                tvInfoLength.setText(expiry[expiry_date]);
                break;
            case 13:
                tvEquipmentName.setText(data.getStringExtra("name"));
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
