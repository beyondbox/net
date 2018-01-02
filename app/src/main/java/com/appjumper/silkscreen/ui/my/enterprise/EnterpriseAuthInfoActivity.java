package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看企业认证信息
 * Created by Botx on 2017/12/26.
 */

public class EnterpriseAuthInfoActivity extends BaseActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.txtState)
    TextView txtState;

    @Bind(R.id.imgVi0)
    ImageView imgVi0;
    @Bind(R.id.imgVi1)
    ImageView imgVi1;
    @Bind(R.id.imgVi2)
    ImageView imgVi2;
    @Bind(R.id.txtPersonName)
    TextView txtPersonName;
    @Bind(R.id.txtCompanyName)
    TextView txtCompanyName;
    @Bind(R.id.txtID)
    TextView txtID;
    @Bind(R.id.txtRegistrID)
    TextView txtRegistrID;

    private Enterprise data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_auth_info);
        ButterKnife.bind(context);
        initTitle("企业认证");
        initBack();

        data = getUser().getEnterprise();
        setData();
    }


    private void setData() {
        Picasso.with(context)
                .load(data.getEnterprise_legal_img())
                .resize(1024, 1024)
                .centerInside()
                .placeholder(R.mipmap.icon_front_01)
                .error(R.mipmap.icon_front_01)
                .into(imgVi0);

        Picasso.with(context)
                .load(data.getEnterprise_legal_img_back())
                .resize(1024, 1024)
                .centerInside()
                .placeholder(R.mipmap.icon_contrary_02)
                .error(R.mipmap.icon_contrary_02)
                .into(imgVi1);

        Picasso.with(context)
                .load(data.getEnterprise_licence_img())
                .resize(1024, 1024)
                .centerInside()
                .placeholder(R.mipmap.icon_business_license)
                .error(R.mipmap.icon_business_license)
                .into(imgVi2);

        txtCompanyName.setText(data.getEnterprise_name());
        txtPersonName.setText(data.getEnterprise_corporate_representative());
        txtRegistrID.setText(data.getEnterprise_registration_mark());
        txtID.setText(data.getEnterprise_corporate());
    }


    @OnClick({R.id.imgVi0, R.id.imgVi1, R.id.imgVi2})
    public void onClick(View view) {
        if (data == null)
            return;

        Intent intent = null;
        ArrayList<String> urls;
        switch (view.getId()) {
            case R.id.imgVi0:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getEnterprise_legal_img());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
            case R.id.imgVi1:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getEnterprise_legal_img_back());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
            case R.id.imgVi2:
                intent = new Intent(context, GalleryActivity.class);
                urls = new ArrayList<String>();
                urls.add(data.getEnterprise_licence_img());
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
                startActivity(intent);
                break;
        }
    }

}
