package com.appjumper.silkscreen.ui.my.audit;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.CarInfo;
import com.appjumper.silkscreen.bean.DriverAuthInfo;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 快速审核 --司机认证
 * Created by Botx on 2017/12/5.
 */

public class AuditDriverFragment extends BaseFragment {

    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.llBottomBar)
    LinearLayout llBottomBar;
    @Bind(R.id.txtState)
    TextView txtState;

    @Bind(R.id.imgVi1)
    ImageView imgVi1;
    @Bind(R.id.imgVi2)
    ImageView imgVi2;
    @Bind(R.id.imgVi3)
    ImageView imgVi3;
    @Bind(R.id.imgVi4)
    ImageView imgVi4;
    @Bind(R.id.imgVi5)
    ImageView imgVi5;

    @Bind(R.id.txtName)
    TextView txtName;
    @Bind(R.id.txtMobile)
    TextView txtMobile;
    @Bind(R.id.txtBrand)
    TextView txtBrand;
    @Bind(R.id.txtCarModel)
    TextView txtCarModel;
    @Bind(R.id.txtYear)
    TextView txtYear;
    @Bind(R.id.txtWeight)
    TextView txtWeight;
    @Bind(R.id.txtMileage)
    TextView txtMileage;
    @Bind(R.id.txtCarLength)
    TextView txtCarLength;


    private AuditDriverActivity activity;
    private Dialog dialogRefuse;
    private EditText edtTxtContent;
    private DriverAuthInfo data;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audit_driver, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        activity = (AuditDriverActivity) getActivity();
        Bundle bundle = getArguments();
        data = (DriverAuthInfo) bundle.getSerializable(Const.KEY_OBJECT);
        setData();
        initDialog();
        initProgressDialog(false, null);
    }


    private void setData() {
        Picasso.with(context)
                .load(data.getDriver_img())
                .resize(512, 512)
                .centerInside()
                .placeholder(R.mipmap.front_driver)
                .error(R.mipmap.front_driver)
                .into(imgVi1);

        Picasso.with(context)
                .load(data.getDriver_img_back())
                .resize(512, 512)
                .centerInside()
                .placeholder(R.mipmap.back_driver)
                .error(R.mipmap.back_driver)
                .into(imgVi2);

        Picasso.with(context)
                .load(data.getDriving_license_img())
                .resize(512, 512)
                .centerInside()
                .placeholder(R.mipmap.front_driving)
                .error(R.mipmap.front_driving)
                .into(imgVi3);

        Picasso.with(context)
                .load(data.getDriving_license_img_back())
                .resize(512, 512)
                .centerInside()
                .placeholder(R.mipmap.back_driving)
                .error(R.mipmap.back_driving)
                .into(imgVi4);

        Picasso.with(context)
                .load(data.getDriver_car_img())
                .resize(512, 512)
                .centerInside()
                .placeholder(R.mipmap.upload_group)
                .error(R.mipmap.upload_group)
                .into(imgVi5);

        txtName.setText(data.getName());
        txtMobile.setText(data.getMobile());

        CarInfo carInfo = data.getCarinfo();
        txtBrand.setText(carInfo.getVehicle_brand());
        txtYear.setText(carInfo.getLicense_years());
        txtMileage.setText(carInfo.getLicense_kilometers());
        txtCarModel.setText(carInfo.getCar_model_name());
        txtWeight.setText(carInfo.getWeight());
        txtCarLength.setText(carInfo.getCar_length_name());

        llContent.setVisibility(View.VISIBLE);
    }


    private void initDialog() {
        dialogRefuse = new Dialog(context, R.style.CustomDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_audit_refuse, null);
        dialogRefuse.setContentView(view);

        Display display = context.getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = dialogRefuse.getWindow().getAttributes();
        params.width = (int) (display.getWidth() * 0.8);
        dialogRefuse.getWindow().setAttributes(params);

        edtTxtContent = (EditText) view.findViewById(R.id.edtTxtContent);
        TextView txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);

        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtTxtContent.getText().toString().trim())) {
                    showErrorToast("请输入拒绝理由");
                    return;
                }
                dialogRefuse.dismiss();
                refuse();
            }
        });
    }


    /**
     * 通过
     */
    private void pass() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "pass_driver_status");
        params.put("id", data.getId());
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
                        showErrorToast("审核成功");
                        txtState.setText("已通过");
                        txtState.setTextColor(getResources().getColor(R.color.green_color));
                        llBottomBar.setVisibility(View.GONE);

                        activity.updateState();
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


    /**
     * 拒绝
     */
    private void refuse() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "refusal_driver_status");
        params.put("id", data.getId());
        params.put("driver_status_reason", edtTxtContent.getText().toString().trim());

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
                        showErrorToast("审核成功");
                        txtState.setText("已拒绝");
                        txtState.setTextColor(getResources().getColor(R.color.red_color));
                        llBottomBar.setVisibility(View.GONE);

                        activity.updateState();
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


    @OnClick({R.id.txtRefuse, R.id.txtPass, R.id.imgViCall, R.id.imgVi1, R.id.imgVi2, R.id.imgVi3, R.id.imgVi4, R.id.imgVi5})
    public void onClick(View view) {
        if (data == null)
            return;

        Intent intent = null;
        ArrayList<String> urls;
        switch (view.getId()) {
            case R.id.txtRefuse: //拒绝
                dialogRefuse.show();
                break;
            case R.id.txtPass: //通过
                pass();
                break;
            case R.id.imgViCall: //打电话
                AppTool.dial(context, data.getMobile());
                break;
            case R.id.imgVi1:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getDriver_img());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
            case R.id.imgVi2:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getDriver_img_back());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
            case R.id.imgVi3:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getDriving_license_img());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
            case R.id.imgVi4:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getDriving_license_img_back());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
            case R.id.imgVi5:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getDriver_car_img());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
        }
    }

}
