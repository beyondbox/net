package com.appjumper.silkscreen.ui.my.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.CarInfo;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看司机认证信息
 * Created by Botx on 2017/12/26.
 */

public class DriverAuthInfoActivity extends BaseActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;
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

    private User data;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_auth_info);
        ButterKnife.bind(context);
        initTitle("司机认证");
        initBack();

        data = getUser();
        setData();
    }


    private void setData() {
        txtState.setText("已认证");

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
    }


    @OnClick({R.id.imgVi1, R.id.imgVi2, R.id.imgVi3, R.id.imgVi4, R.id.imgVi5})
    public void onClick(View view) {
        if (data == null)
            return;

        Intent intent = null;
        ArrayList<String> urls;
        switch (view.getId()) {
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
