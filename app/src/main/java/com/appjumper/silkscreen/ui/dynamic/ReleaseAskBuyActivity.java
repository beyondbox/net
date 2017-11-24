package com.appjumper.silkscreen.ui.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;
import com.appjumper.silkscreen.view.phonegridview.SelectPictureActivity;
import com.bigkoo.pickerview.TimePickerView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发布求购
 * Created by Botx on 2017/10/18.
 */

public class ReleaseAskBuyActivity extends BasePhotoGridActivity {

    @Bind(R.id.txtProduct)
    TextView txtProduct;
    @Bind(R.id.txtTime)
    TextView txtTime;
    @Bind(R.id.edtTxtContent)
    EditText edtTxtContent;


    private ServiceProduct product;
    private TimePickerView pvTime;
    private String imgs = ""; //图纸图片


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_ask_buy);
        ButterKnife.bind(context);

        SelectPictureActivity.MAX_NUM = 3;
        initBack();
        initView();
        product = (ServiceProduct) getIntent().getSerializableExtra(Const.KEY_OBJECT);
        initTitle(product.getName() + "求购");
        txtProduct.setText(product.getName());

        initPickerView();
        initProgressDialog(false, "正在发布.....");
    }


    private void initPickerView() {
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
     * 上传图片
     */
    private void uploadImage() {
        File[] fileArr = new File[thumbPictures.size() - 1];
        for (int i = 0; i < thumbPictures.size(); i++) {
            String str = thumbPictures.get(i);
            //去掉最后一个 +图片
            if (!str.equals(""+ BasePhotoGridActivity.PICTURE_UPDATE_ICON)) {
                fileArr[i] = new File(str);
            }
        }

        RequestParams imgParam = new RequestParams();
        try {
            imgParam.put("file[]", fileArr);
            MyHttpClient.getInstance().post(Url.UPLOADIMAGE, imgParam, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String jsonStr = new String(responseBody);
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                        if (state == Const.HTTP_STATE_SUCCESS) {
                            List<Avatar> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), Avatar.class);
                            imgs = "";
                            for (int i = 0; i < list.size(); i++) {
                                imgs += list.get(i).getImg_id();
                                if (i < list.size() - 1)
                                    imgs += ",";
                            }

                            submit();
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    /**
     * 发布
     */
    private void submit() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "add");
        params.put("uid", getUserID());
        params.put("img", product.getImg());
        params.put("imgs", imgs);
        params.put("expiry_date", txtTime.getText().toString() + ":59");
        params.put("product_id", product.getId());
        params.put("product_name", product.getName());
        params.put("purchase_content", edtTxtContent.getText().toString().trim());
        params.put("mobile", getUser().getMobile());

        int infoType;
        Enterprise enterprise = getUser().getEnterprise();
        if (enterprise != null && enterprise.getEnterprise_auth_status().equals("2"))
            infoType = Const.INFO_TYPE_COM;
        else
            infoType = Const.INFO_TYPE_PER;

        params.put("pruchase_type", infoType);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        Intent intent = new Intent(context, ReleaseCompleteActivity.class);
                        intent.putExtra(Const.KEY_TYPE, ReleaseCompleteActivity.TYPE_ASK_BUY);
                        startActivity(intent);

                        if (ProductSelectActivity.instance != null)
                            ProductSelectActivity.instance.finish();
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


    @OnClick({R.id.txtTime, R.id.txtConfirm, R.id.txtProduct})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtTime: //选择截止时间
                pvTime.show();
                break;
            case R.id.txtConfirm: //立即发布
                if (TextUtils.isEmpty(edtTxtContent.getText().toString().trim())) {
                    showErrorToast("请输入求购说明");
                    return;
                }
                if (TextUtils.isEmpty(txtTime.getText().toString().trim())) {
                    showErrorToast("请选择截止时间");
                    return;
                }

                progress.show();
                if (selectedPicture.size() > 0)
                    uploadImage();
                else
                    submit();
                break;
            case R.id.txtProduct:
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SelectPictureActivity.MAX_NUM = 9;
    }
}
