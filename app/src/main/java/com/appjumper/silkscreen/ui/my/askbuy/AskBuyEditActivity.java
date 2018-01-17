package com.appjumper.silkscreen.ui.my.askbuy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.askbuy.AskBuyManageDetailActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;
import com.appjumper.silkscreen.view.phonegridview.SelectPictureActivity;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 求购编辑
 * Created by Botx on 2017/10/18.
 */

public class AskBuyEditActivity extends BasePhotoGridActivity {

    @Bind(R.id.txtProduct)
    TextView txtProduct;
    @Bind(R.id.txtTime)
    TextView txtTime;
    @Bind(R.id.edtTxtContent)
    EditText edtTxtContent;
    @Bind(R.id.txtUnit)
    TextView txtUnit;
    @Bind(R.id.edtTxtNum)
    EditText edtTxtNum;
    @Bind(R.id.txtConfirm)
    TextView txtConfirm;


    private TimePickerView pvTime;
    private String imgs = ""; //图纸图片
    private AskBuy askBuy;
    private String id;

    private OptionsPickerView pvUnits;
    private String [] unitArr = {"平米", "卷", "吨", "片", "套", "根", "个", "捆", "米"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_ask_buy);
        ButterKnife.bind(context);

        SelectPictureActivity.MAX_NUM = 3;
        initBack();
        initView();
        txtConfirm.setText("提交");
        id = getIntent().getStringExtra("id");

        initPickerView();
        initProgressDialog(false, null);
        getDetail();
    }


    private void initPickerView() {
        pvUnits = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                txtUnit.setText(unitArr[options1]);
            }
        }).build();
        pvUnits.setPicker(Arrays.asList(unitArr));


        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, 24 * 60);
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                txtTime.setText(AppTool.dateFormat(date.getTime(), "yyyy-MM-dd HH:mm"));
            }
        })
                .setContentSize(17)
                .setType(new boolean[]{true, true, true, true, true, false})
                .setRangDate(Calendar.getInstance(), endTime)
                .build();
    }


    /**
     * 获取求购详情
     */
    private void getDetail() {
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
                        askBuy = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), AskBuy.class);
                        initTitle("求购" + askBuy.getProduct_name());
                        txtProduct.setText(askBuy.getProduct_name());
                        edtTxtNum.setText(askBuy.getPurchase_num());
                        txtUnit.setText(askBuy.getPurchase_unit());
                        edtTxtContent.setText(askBuy.getPurchase_content());
                        imgs = askBuy.getImgs();
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
     * 提交
     */
    private void submit() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "edit");
        params.put("uid", getUserID());
        params.put("info_id", id);
        //params.put("newpublic_id", myRelease.getId());
        params.put("img", askBuy.getImg());
        params.put("imgs", imgs);
        params.put("expiry_date", txtTime.getText().toString() + ":59");
        params.put("product_id", askBuy.getProduct_id());
        params.put("product_name", askBuy.getProduct_name());
        params.put("purchase_content", edtTxtContent.getText().toString().trim());
        params.put("mobile", getUser().getMobile());
        params.put("purchase_num", edtTxtNum.getText().toString().trim());
        params.put("purchase_unit", txtUnit.getText().toString().trim());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        if (AskBuyManageDetailActivity.instance != null) AskBuyManageDetailActivity.instance.finish();
                        sendBroadcast(new Intent(Const.ACTION_RELEASE_SUCCESS));
                        showErrorToast("提交成功");
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


    @OnClick({R.id.txtTime, R.id.txtConfirm, R.id.txtUnit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtTime: //选择截止时间
                AppTool.hideSoftInput(this);
                pvTime.show();
                break;
            case R.id.txtConfirm: //提交
                if (askBuy == null)
                    return;

                if (TextUtils.isEmpty(edtTxtNum.getText().toString().trim())) {
                    showErrorToast("请输入求购数量");
                    return;
                }
                if (TextUtils.isEmpty(txtUnit.getText().toString().trim())) {
                    showErrorToast("请选择求购单位");
                    return;
                }
                if (TextUtils.isEmpty(txtTime.getText().toString().trim())) {
                    showErrorToast("请选择截止时间");
                    return;
                }
                if (TextUtils.isEmpty(edtTxtContent.getText().toString().trim())) {
                    showErrorToast("请输入求购说明");
                    return;
                }

                progress.show();
                if (selectedPicture.size() > 0)
                    uploadImage();
                else
                    submit();
                break;
            case R.id.txtUnit: //选择单位
                AppTool.hideSoftInput(this);
                pvUnits.show();
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
