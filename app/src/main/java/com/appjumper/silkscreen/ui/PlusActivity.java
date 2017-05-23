package com.appjumper.silkscreen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentReleaseActivity;
import com.appjumper.silkscreen.ui.home.logistics.PersonalReleaseActivity;
import com.appjumper.silkscreen.ui.home.logistics.TruckReleaseActivity;
import com.appjumper.silkscreen.ui.home.recruit.RecruitReleaseActivity;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopReleaseActivity;
import com.appjumper.silkscreen.ui.inquiry.InquirySpecificationActivity;
import com.appjumper.silkscreen.ui.my.PersonalAuthenticationActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseAuthenticationActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.ui.my.enterprise.SpecificationActivity;
import com.appjumper.silkscreen.ui.my.enterprise.SpecificationStockActivity;
import com.appjumper.silkscreen.ui.spec.ReleaseHuLanActivity;
import com.appjumper.silkscreen.util.Const;

import org.apache.http.message.BasicNameValuePair;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * "+"界面
 * Created by Botx on 2017/4/11.
 */

public class PlusActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus);
        ButterKnife.bind(context);
    }


    @OnClick({R.id.imgViClose, R.id.txtInquiryOrder, R.id.txtInquiryStock, R.id.llReleaseProcess, R.id.llReleaseOrder, R.id.llReleaseStock,
    R.id.llReleaseStation, R.id.llReleaseTruck, R.id.llReleaseFindTruck, R.id.llReleaseWorkshop, R.id.llReleasePost, R.id.llReleaseDevice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgViClose:
                finish();
                break;

            case R.id.llReleaseProcess: //提供代加工
                if (getUser().getEnterprise() == null) {
                    start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    return;
                }
                goToProductSelect(Const.SERVICE_TYPE_PROCESS, Const.REQUEST_CODE_RELEASE_PROCESS);
                break;

            case R.id.llReleaseOrder: //接受丝网订做
                if (getUser().getEnterprise() == null) {
                    start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    return;
                }
                goToProductSelect(Const.SERVICE_TYPE_ORDER, Const.REQUEST_CODE_RELEASE_ORDER);
                break;

            case R.id.llReleaseStock: //现货库存供应
                if (!getUser().getAuth_status().equals("2")) {
                    Toast.makeText(context, "您尚未通过实名认证", Toast.LENGTH_SHORT).show();
                    start_Activity(context, PersonalAuthenticationActivity.class);
                    return;
                }
                if (getUser().getEnterprise() == null) {
                    start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    return;
                }
                if (!getUser().getEnterprise().getEnterprise_auth_status().equals("2")) {
                    Toast.makeText(context, "您的企业尚未通过认证", Toast.LENGTH_SHORT).show();
                    start_Activity(context, EnterpriseAuthenticationActivity.class);
                    return;
                }
                goToProductSelect(Const.SERVICE_TYPE_STOCK, Const.REQUEST_CODE_RELEASE_STOCK);
                break;

            case R.id.llReleaseStation: //货站线路发布
                if (getUser().getEnterprise() == null) {
                    start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    return;
                }
                start_Activity(context, PersonalReleaseActivity.class, new BasicNameValuePair("type", "1"));
                break;

            case R.id.llReleaseTruck: //个人车辆拉货
                start_Activity(context, PersonalReleaseActivity.class, new BasicNameValuePair("type", "2"));
                break;

            case R.id.llReleaseFindTruck: //货物运输需求
                start_Activity(context, TruckReleaseActivity.class);
                break;

            case R.id.llReleaseWorkshop: //厂房出租
                start_Activity(context, WorkshopReleaseActivity.class);
                break;

            case R.id.llReleasePost: //招聘
                start_Activity(context, RecruitReleaseActivity.class);
                break;

            case R.id.llReleaseDevice: //机器出售
                start_Activity(context, EquipmentReleaseActivity.class);
                break;

            case R.id.txtInquiryOrder: //订做询价
                goToProductSelect(Const.SERVICE_TYPE_ORDER, Const.REQUEST_CODE_INQUIRY_ORDER);
                break;

            case R.id.txtInquiryStock: //现货询价
                goToProductSelect(Const.SERVICE_TYPE_STOCK, Const.REQUEST_CODE_INQUIRY_STOCK);
                break;

            default:
                break;
        }
    }


    /**
     * 跳转到产品选择界面
     */
    private void goToProductSelect(int serviceType, int requestCode) {
        Intent intent = new Intent(context, ProductSelectActivity.class);
        intent.putExtra(Const.KEY_SERVICE_TYPE, serviceType);
        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        ServiceProduct product = (ServiceProduct) data.getSerializableExtra(Const.KEY_OBJECT);
        Intent intent = null;
        switch (requestCode) {
            case Const.REQUEST_CODE_RELEASE_PROCESS:
                intent = new Intent(context, SpecificationActivity.class);
                intent.putExtra("service", product);
                intent.putExtra("type", Const.SERVICE_TYPE_PROCESS + "");
                break;
            case Const.REQUEST_CODE_RELEASE_ORDER:
                if (product.getId().equals("104"))
                    intent = new Intent(context, ReleaseHuLanActivity.class);
                else
                    intent = new Intent(context, SpecificationActivity.class);
                intent.putExtra("service", product);
                intent.putExtra("type", Const.SERVICE_TYPE_ORDER + "");
                break;
            case Const.REQUEST_CODE_RELEASE_STOCK:
                intent = new Intent(context, SpecificationStockActivity.class);
                intent.putExtra("service", product);
                intent.putExtra("type", Const.SERVICE_TYPE_STOCK + "");
                break;
            case Const.REQUEST_CODE_INQUIRY_ORDER:
                intent = new Intent(context, InquirySpecificationActivity.class);
                intent.putExtra("identity", "3");
                intent.putExtra("service", product);
                intent.putExtra("type", Const.SERVICE_TYPE_ORDER + "");
                break;
            case Const.REQUEST_CODE_INQUIRY_STOCK:
                intent = new Intent(context, InquirySpecificationActivity.class);
                intent.putExtra("identity", "3");
                intent.putExtra("service", product);
                intent.putExtra("type", Const.SERVICE_TYPE_STOCK + "");
                break;
            default:
                break;
        }

        startActivity(intent);
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.top_to_bottom);
    }

}
