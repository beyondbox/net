package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/18.
 * 创建企业（联系方式）（没用着）
 */
public class EnterpriseContactActivity extends BaseActivity {
    private Intent intent;

    @Bind(R.id.et_enterprise_contacts)//联系人
            EditText et_enterprise_contacts;

    @Bind(R.id.et_enterprise_mobile)//手机号
            EditText et_enterprise_mobile;

    @Bind(R.id.et_enterprise_qq)//QQ
            EditText et_enterprise_qq;

    @Bind(R.id.et_enterprise_website)//企业网站
            EditText et_enterprise_website;

    @Bind(R.id.et_enterprise_tel)//联系电话
            EditText et_enterprise_tel;

    @Bind(R.id.et_enterprise_address)//企业地址
            EditText et_enterprise_address;

    private String enterprise_contacts;
    private String enterprise_mobile;
    private String enterprise_qq;
    private String enterprise_address;
    private String enterprise_website;
    private String enterprise_tel;

    private String enterprise_logo;
    private String enterprise_name;
    private String enterprise_reg_date;
    private String enterprise_reg_money;
    private String enterprise_area;
    private String enterprise_staff_num;
    private String enterprise_machine_num;
    private String enterprise_capacity_num;
    private String enterprise_intro;
    private String enterprise_imgs;
    private String type = "";
    private Enterprise enterprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_contact);
        ActivityTaskManager.getInstance().putActivity(this);
        hideKeyboard();
        initLocation();
        ButterKnife.bind(this);
        initBack();
        intent = getIntent();
        enterprise_logo = intent.getStringExtra("imgPath");
        enterprise_name = intent.getStringExtra("enterprise_name");
        enterprise_reg_date = intent.getStringExtra("startdata");
        enterprise_reg_money = intent.getStringExtra("create_money");
        enterprise_area = intent.getStringExtra("plant_area");
        enterprise_staff_num = intent.getStringExtra("number_employees");
        enterprise_machine_num = intent.getStringExtra("number_machine");
        enterprise_capacity_num = intent.getStringExtra("capacity");
        enterprise_intro = intent.getStringExtra("enterprise_intro");
        enterprise_imgs = intent.getStringExtra("thumbPictures");
        type = intent.getStringExtra("type");
        if (type.equals("1")) {
            enterprise = (Enterprise) intent.getSerializableExtra("enterprise");
            et_enterprise_contacts.setText(enterprise.getEnterprise_contacts());
            et_enterprise_mobile.setText(enterprise.getEnterprise_mobile());
            et_enterprise_qq.setText(enterprise.getEnterprise_qq());
            et_enterprise_website.setText(enterprise.getEnterprise_website());
            et_enterprise_tel.setText(enterprise.getEnterprise_tel());
            et_enterprise_address.setText(enterprise.getEnterprise_address());
        }
    }

    @OnClick({R.id.next_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn://完成
                enterprise_contacts = et_enterprise_contacts.getText().toString();
                enterprise_mobile = et_enterprise_mobile.getText().toString();
                enterprise_qq = et_enterprise_qq.getText().toString();
                enterprise_address = et_enterprise_address.getText().toString();
                enterprise_website = et_enterprise_website.getText().toString();
                enterprise_tel = et_enterprise_tel.getText().toString();
                if (enterprise_contacts.length() <= 0) {
                    showErrorToast("联系人不能为空");
                    return;
                }
                if (enterprise_mobile.length() <= 0) {
                    showErrorToast("手机号不能为空");
                    return;
                }
                if (enterprise_qq.length() <= 0) {
                    showErrorToast("QQ不能为空");
                    return;
                }
                if (enterprise_address.length() <= 0) {
                    showErrorToast("企业地址不能为空");
                    return;
                }
                if (enterprise_tel.length() <= 0) {
                    showErrorToast("联系电话不能为空");
                    return;
                }
                if (enterprise_website.length() <= 0) {
                    showErrorToast("企业网站不能为空");
                    return;
                }
                initProgressDialog();
                progress.show();
                progress.setMessage("正在创建企业...");
                new Thread(new SubmitRun()).start();
                break;
            default:
                break;
        }
    }

    private class SubmitRun implements Runnable {

        @Override
        public void run() {
            BaseResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("enterprise_name", enterprise_name);
                data.put("enterprise_logo", enterprise_logo);
                data.put("enterprise_imgs", enterprise_imgs);
                data.put("enterprise_reg_date", enterprise_reg_date);
                data.put("enterprise_reg_money", enterprise_reg_money);
                data.put("enterprise_area", enterprise_area);
                data.put("enterprise_staff_num", enterprise_staff_num);
                data.put("enterprise_machine_num", enterprise_machine_num);
                data.put("enterprise_capacity_num", enterprise_capacity_num);
                data.put("enterprise_intro", enterprise_intro);
                data.put("enterprise_contacts", enterprise_contacts);
                data.put("enterprise_mobile", enterprise_mobile);
                data.put("enterprise_qq", enterprise_qq);
                data.put("enterprise_website", enterprise_website);
                data.put("enterprise_tel", enterprise_tel);
                data.put("enterprise_address", enterprise_address);
                data.put("lat", enterprise_tel);
                data.put("lng", longitude + "");
                data.put("lat", latitude + "");

                // 一次http请求将所有图片+参数上传
                String url;
                if (type.equals("1")) {
                    url = Url.ENTERPRISEADD;
                } else {
                    url = Url.ENTERPRISEA_EDIT;
                }
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), url + "?XDEBUG_SESSION_START=1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
            }
        }
    }

    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {

        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            EnterpriseContactActivity activity = (EnterpriseContactActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://提交
                    progress.dismiss();
                    BaseResponse userResponse = (BaseResponse) msg.obj;
                    if (userResponse.isSuccess()) {
                        ActivityTaskManager.getInstance().getActivity(EnterpriseCreateActivity.class).finish();
                        ActivityTaskManager.getInstance().getActivity(EnterpriseIntroduceActivity.class).finish();
                        activity.finish();
                        showErrorToast("创建成功");
                    } else {
                        showErrorToast(userResponse.getError_desc());
                    }

                    break;
                case NETWORK_FAIL:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
            }
        }
    }
}
