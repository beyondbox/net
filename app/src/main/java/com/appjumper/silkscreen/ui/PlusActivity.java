package com.appjumper.silkscreen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.my.enterprise.CertifyManageActivity;
import com.appjumper.silkscreen.ui.my.enterprise.EnterpriseCreateActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.SureOrCancelDialog;

import org.apache.http.message.BasicNameValuePair;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * "+"界面
 * Created by Botx on 2017/4/11.
 */

public class PlusActivity extends BaseActivity {

    private SureOrCancelDialog certifyPerDialog;
    private SureOrCancelDialog certifyComDialog;
    private SureOrCancelDialog comCreateDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus2);
        ButterKnife.bind(context);
        initDialog();
    }


    @OnClick({R.id.imgViClose, R.id.llReleaseProcess, R.id.llReleaseOrder, R.id.llReleaseStock, R.id.llReleaseStation,
            R.id.llReleaseTruck, R.id.llReleaseFindTruck, R.id.llReleaseWorkshop, R.id.llReleasePost, R.id.llReleaseDevice, R.id.txtInquiryOrder, R.id.txtInquiryStock})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgViClose:
                finish();
                break;

            case R.id.llReleaseProcess: //提供代加工
                if (getUser().getEnterprise() == null) {
                    comCreateDialog.show();
                    return;
                }
                //goToProductSelect(Const.SERVICE_TYPE_PROCESS, ProductSelectActivity.MOTION_RELEASE_SERVICE);
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_PROCESS);
                break;

            case R.id.llReleaseOrder: //接受丝网订做
                if (getUser().getEnterprise() == null) {
                    comCreateDialog.show();
                    return;
                }
                //goToProductSelect(Const.SERVICE_TYPE_ORDER, ProductSelectActivity.MOTION_RELEASE_SERVICE);
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_ORDER);
                break;

            case R.id.llReleaseStock: //现货库存供应
                if (!getUser().getAuth_status().equals("2")) {
                    certifyPerDialog.show();
                    return;
                }
                if (getUser().getEnterprise() == null) {
                    comCreateDialog.show();
                    return;
                }
                if (!getUser().getEnterprise().getEnterprise_auth_status().equals("2")) {
                    certifyComDialog.show();
                    return;
                }
                //goToProductSelect(Const.SERVICE_TYPE_STOCK, ProductSelectActivity.MOTION_RELEASE_SERVICE);
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_STOCK);
                break;

            case R.id.llReleaseStation: //货站线路发布
                if (getUser().getEnterprise() == null) {
                    comCreateDialog.show();
                    return;
                }
                //start_Activity(context, PersonalReleaseActivity.class, new BasicNameValuePair("type", "1"));
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_LOGISTICS);
                break;

            case R.id.llReleaseTruck: //个人车辆拉货
                //start_Activity(context, PersonalReleaseActivity.class, new BasicNameValuePair("type", "2"));
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_LOGISTICS_PER);
                break;

            case R.id.llReleaseFindTruck: //货物运输需求
                //start_Activity(context, TruckReleaseActivity.class);
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_LOGISTICS_CAR);
                break;

            case R.id.llReleaseWorkshop: //厂房出租
                //start_Activity(context, WorkshopReleaseActivity.class);
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_WORKSHOP);
                break;

            case R.id.llReleasePost: //招聘
                //start_Activity(context, RecruitReleaseActivity.class);
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_JOB);
                break;

            case R.id.llReleaseDevice: //机器出售
                //start_Activity(context, EquipmentReleaseActivity.class);
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_DEVICE);
                break;

            case R.id.txtInquiryOrder: //订做询价
                goToProductSelect(Const.SERVICE_TYPE_ORDER, ProductSelectActivity.MOTION_RELEASE_INQUIRY);
                break;

            case R.id.txtInquiryStock: //现货询价
                goToProductSelect(Const.SERVICE_TYPE_STOCK, ProductSelectActivity.MOTION_RELEASE_INQUIRY);
                break;

            default:
                break;
        }
    }


    /**
     * 初始化对话框
     */
    private void initDialog() {
        certifyPerDialog = new SureOrCancelDialog(context, "提示", "您尚未完成个人认证，暂时不能在该板块发布信息，请完成个人认证后再继续操作", "确定", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        start_Activity(context, CertifyManageActivity.class);
                    }
                });

        certifyComDialog = new SureOrCancelDialog(context, "提示", "您尚未完成企业认证，暂时不能在该板块发布信息，请完成企业认证后再继续操作", "确定", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        start_Activity(context, CertifyManageActivity.class);
                    }
                });

        comCreateDialog = new SureOrCancelDialog(context, "提示", "您尚未完善企业信息，暂时不能在该板块发布信息，请完善企业信息后再继续操作", "确定", "取消",
                new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    }
                });
    }


    /**
     * 跳转到产品选择界面
     */
    private void goToProductSelect(int serviceType, int motion) {
        Intent intent = new Intent(context, ProductSelectActivity.class);
        intent.putExtra(Const.KEY_SERVICE_TYPE, serviceType);
        intent.putExtra(Const.KEY_MOTION, motion);
        startActivity(intent);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.top_to_bottom);
    }

}
