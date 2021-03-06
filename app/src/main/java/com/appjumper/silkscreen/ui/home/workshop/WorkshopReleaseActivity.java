package com.appjumper.silkscreen.ui.home.workshop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectCityActivity;
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
 * 发布厂房出租
 */
public class WorkshopReleaseActivity extends BasePhotoGridActivity {
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_area)
    EditText etArea;
    @Bind(R.id.et_price)
    EditText etPrice;
    @Bind(R.id.tv_position)
    TextView tvPosition;
    @Bind(R.id.et_remark)
    EditText etRemark;
    @Bind(R.id.tv_info_length)
    TextView tvInfoLength;
    @Bind(R.id.et_address)//地址
    EditText etAddress;
    @Bind(R.id.txtPriceUnit)
    TextView txtPriceUnit;
    @Bind(R.id.edtTxtTransfer)
    EditText edtTxtTransfer;
    @Bind(R.id.llTransfer)
    LinearLayout llTransfer;

    private long expiry_datatime = 3600 * 72;
    private String[] expiry = {"3天", "5天", "10天", "30天"};//信息时长

    private String workshopType = "出租";
    private ImageResponse imgResponse;
    private String address_id = "214";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_workshop_release2);
        initBack();
        ButterKnife.bind(this);
        initView();
        initTitle("厂房出租");
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if (etTitle.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入标题");
                    return;
                }
                if (etArea.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入占地面积");
                    return;
                }
                if (etPrice.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入厂房价格");
                    return;
                }
                if (workshopType.equals("转让") && TextUtils.isEmpty(edtTxtTransfer.getText().toString().trim())) {
                    showErrorToast("请输入转让费");
                    return;
                }
                if (etAddress.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入详细地址");
                    return;
                }
                if (tvPosition.getText().toString().length() < 1) {
                    showErrorToast("请输入厂房地段");
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


    @OnCheckedChanged({R.id.rdoBtnType0, R.id.rdoBtnType1, R.id.rdoBtnType2})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rdoBtnType0:
                    workshopType = "出租";
                    txtPriceUnit.setText("元/年");
                    llTransfer.setVisibility(View.GONE);
                    break;
                case R.id.rdoBtnType1:
                    workshopType = "转让";
                    txtPriceUnit.setText("元/年");
                    llTransfer.setVisibility(View.VISIBLE);
                    break;
                case R.id.rdoBtnType2:
                    workshopType = "出售";
                    txtPriceUnit.setText("元");
                    llTransfer.setVisibility(View.GONE);
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

                data.put("workshop_type", infoType + "");
                data.put("uid", getUserID());
                data.put("title", etTitle.getText().toString().trim());
                data.put("remark", etRemark.getText().toString().trim());
                data.put("expiry_date", expiry_datatime + "");
                data.put("area", etArea.getText().toString().trim());
                data.put("lease_mode", workshopType);
                data.put("position", tvPosition.getText().toString());
                data.put("location", address_id);
                data.put("price", etPrice.getText().toString().trim());
                data.put("transfer_fee", edtTxtTransfer.getText().toString().trim());
                data.put("address", etAddress.getText().toString().trim());
                data.put("imgs", imags(imgResponse.getData()));
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.WORKSHOP_RELEASE));
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
            WorkshopReleaseActivity activity = (WorkshopReleaseActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case 8://上传图片
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
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
                case NETWORK_FAIL:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
            }
        }
    }

    @OnClick({R.id.tv_info_length,R.id.tv_position})
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tv_info_length:
                intent = new Intent(WorkshopReleaseActivity.this, InformationSelectActivity.class);
                Bundle b = new Bundle();
                b.putStringArray("val", expiry);
                intent.putExtras(b);
                intent.putExtra("title", "信息时长");
                startActivityForResult(intent, 12);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case R.id.tv_position:
                startForResult_Activity(this,AddressSelectCityActivity.class,1,new BasicNameValuePair("type","2"),new BasicNameValuePair("id","208"),new BasicNameValuePair("code","1"));
//                startForResult_Activity(this, AddressSelectActivity.class, 1, new BasicNameValuePair("code", "1"), new BasicNameValuePair("type", "2"));
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
            case 1://地段
                address_id = data.getStringExtra("id");
                String address_name = data.getStringExtra("name");
                tvPosition.setText(address_name);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
