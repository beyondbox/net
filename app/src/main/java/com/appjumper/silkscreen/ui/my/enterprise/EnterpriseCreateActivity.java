package com.appjumper.silkscreen.ui.my.enterprise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.MultiSelectPhotoActivity;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/18.
 * 创建企业（基本信息）
 */
public class EnterpriseCreateActivity extends MultiSelectPhotoActivity {
    @Bind(R.id.iv_logo)
    ImageView iv_logo;

    @Bind(R.id.et_enterprise_name)
    EditText et_enterprise_name;//企业名称
    @Bind(R.id.et_number_employees)
    EditText et_number_employees;//员工人数
    @Bind(R.id.tv_enterprise_create_time)
    TextView tv_enterprise_create_time;//注册时间
    @Bind(R.id.et_create_money)
    EditText et_create_money;//注册资本

    @Bind(R.id.et_enterprise_contacts)//联系人
    EditText et_enterprise_contacts;
    @Bind(R.id.et_enterprise_tel)//联系电话
    EditText et_enterprise_tel;
    @Bind(R.id.et_enterprise_address)//企业地址
    EditText et_enterprise_address;

    @Bind(R.id.editSms)
    EditText editText;//企业简介

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private String startdata = "";
    private String enterprise_name;
    private String create_money;
    private String number_employees;
    private String type = "";
    private Enterprise enterprise;
    private String enterprise_contacts;
    private String enterprise_tel;

    private String enterprise_address;
    private String enterprise_intro;


    private String logoUrl = ""; //已保存过的logo的URL
    private String logoPath = ""; //现在待上传的logo图片路径
    private String finalLogo = ""; //最终确定的logo路径

    private String imgsUrl = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_create2);
        ActivityTaskManager.getInstance().putActivity(this);
        hideKeyboard();
        ButterKnife.bind(this);
        initBack();
        initLocation();
        initProgressDialog();
        type = getIntent().getStringExtra("type");
        if (type.equals("1")) {
            enterprise = (Enterprise) getIntent().getSerializableExtra("enterprise");
            startdata=enterprise.getEnterprise_reg_date();
            et_enterprise_name.setText(enterprise.getEnterprise_name());
            tv_enterprise_create_time.setText(startdata);
            et_create_money.setText(enterprise.getEnterprise_reg_money());
            et_number_employees.setText(enterprise.getEnterprise_staff_num());
            logoUrl = enterprise.getEnterprise_logo().getSmall();
            imgsUrl = enterprise.getEnterprise_imgs();

            et_enterprise_contacts.setText(enterprise.getEnterprise_contacts());
            et_enterprise_tel.setText(enterprise.getEnterprise_tel());
            et_enterprise_address.setText(enterprise.getEnterprise_address());
            editText.setText(enterprise.getEnterprise_intro());

            Picasso.with(this).load(logoUrl).placeholder(R.mipmap.icon_logo_image61).transform(new PicassoRoundTransform()).resize(60, 60)
                    .centerCrop().into(iv_logo);
        }
    }

    @OnClick({R.id.next_btn, R.id.rl_logo, R.id.lv_enterprise_create_time})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_enterprise_create_time://注册时间
                hideKeyboard();
                viewData();
                break;
            case R.id.next_btn://创建企业
                enterprise_name = et_enterprise_name.getText().toString().trim();
                create_money = et_create_money.getText().toString().trim();
                number_employees = et_number_employees.getText().toString().trim();
                enterprise_contacts = et_enterprise_contacts.getText().toString();
                enterprise_tel = et_enterprise_tel.getText().toString();
                enterprise_address = et_enterprise_address.getText().toString();
                enterprise_intro = editText.getText().toString().trim();

                if (enterprise_name.length() <= 0) {
                    showErrorToast("企业名称不能为空");
                    return;
                }
                if (startdata.length() <= 0) {
                    showErrorToast("注册时间不能为空");
                    return;
                }
                if (create_money.length() <= 0) {
                    showErrorToast("注册资本不能为空");
                    return;
                }
                if (number_employees.length() <= 0) {
                    showErrorToast("员工人数不能为空");
                    return;
                }
                if (enterprise_contacts.length() <= 0) {
                    showErrorToast("联系人不能为空");
                    return;
                }
                if (enterprise_tel.length() <= 0) {
                    showErrorToast("联系电话不能为空");
                    return;
                }
                if (enterprise_address.length() <= 0) {
                    showErrorToast("企业地址不能为空");
                    return;
                }
                if (enterprise_intro.length() <= 0) {
                    showErrorToast("企业简介不能为空");
                    return;
                }
                if (type.equals("1")) {
                    if (logoUrl.equals("") && logoPath.equals("")) {
                        showErrorToast("请上传公司logo");
                        return;
                    }
                } else {
                    if (logoPath.length() <= 0) {
                        showErrorToast("请上传公司logo");
                        return;
                    }
                }
                if (!logoPath.equals("")) {
                    progress.show();
                    new Thread(new UpdateStringRun(logoPath)).start();
                } else {
                    finalLogo = logoUrl;
                    progress.show();
                    progress.setMessage("正在创建企业...");
                    new Thread(new SubmitRun()).start();
                }
                break;
            case R.id.rl_logo://上传logo
                setCropSingleImage(true);
                setSingleImage(true);
                setCropTaskPhoto(true);
                showWindowSelectList(v);
                break;
            default:
                break;
        }
    }

    private void viewData() {
        final Calendar time = Calendar.getInstance(Locale.CHINA);
        LinearLayout dateTimeLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_date_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.new_act_date_picker);
        DatePicker.OnDateChangedListener dateListener = new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                time.set(Calendar.YEAR, year);
                time.set(Calendar.MONTH, monthOfYear);
                time.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            }
        };

        datePicker.init(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH), dateListener);

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择注册时间").setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        time.set(Calendar.YEAR, datePicker.getYear());
                        time.set(Calendar.MONTH, datePicker.getMonth());
                        time.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                        Date datatime = time.getTime();
                        startdata = format.format(datatime);
                        tv_enterprise_create_time.setText(startdata);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    @Override
    protected void requestImage(String[] path) {
        if (!logoPath.equals("")) {
            // 删除临时的100K左右的图片
            File thumbnailPhoto = new File(logoPath);
            thumbnailPhoto.deleteOnExit();
        }
        logoPath = path[0];
        logoUrl = "";
        Log.e("Log", logoPath);
        // 加载图片
        Picasso.with(this).load(new File(logoPath)).placeholder(R.mipmap.icon_logo_image61).transform(new PicassoRoundTransform()).resize(60, 60)
                .centerCrop().into(iv_logo);

    }

    // 如果不是切割的upLoadBitmap就很大
    public class UpdateStringRun implements Runnable {
        private String newPicturePath;
        private File upLoadBitmapFile;

        public UpdateStringRun(String newPicturePath) {
            this.newPicturePath = newPicturePath;
            this.upLoadBitmapFile = new File(newPicturePath);
        }

        @Override
        public void run() {
            ImageResponse retMap = null;
            try {
                String url = Url.UPLOADIMAGE;
                // 如果不是切割的upLoadBitmap就很大,在这里压缩
                retMap = JsonParser.getImageResponse(HttpUtil.uploadFile(url,
                        upLoadBitmapFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (retMap != null) {
                handler.sendMessage(handler.obtainMessage(
                        3, retMap));
            } else {
                handler.sendMessage(handler
                        .obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
            }
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
                data.put("enterprise_logo", finalLogo);
                data.put("enterprise_imgs", imgsUrl);
                data.put("enterprise_reg_date", startdata);
                data.put("enterprise_reg_money", create_money);
                data.put("enterprise_staff_num", number_employees);
                data.put("enterprise_intro", enterprise_intro);
                data.put("enterprise_contacts", enterprise_contacts);
                data.put("enterprise_tel", enterprise_tel);
                data.put("enterprise_address", enterprise_address);
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
    private String urls;

    private class MyHandler extends Handler {
        private WeakReference<Context> reference;
        private ImageResponse imgResponse;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            EnterpriseCreateActivity activity = (EnterpriseCreateActivity) reference.get();
            if (activity == null) {
                return;
            }
            activity.progress.dismiss();
            switch (msg.what) {
                case 3://上传头像回调
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        finalLogo = imgResponse.getData().get(0).getOrigin();
                        progress.show();
                        progress.setMessage("正在创建企业...");
                        new Thread(new SubmitRun()).start();
                    } else {
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT://创建企业回调
                    BaseResponse userResponse = (BaseResponse) msg.obj;
                    if (userResponse.isSuccess()) {
                        activity.finish();
                        showErrorToast("创建成功");
                    } else {
                        showErrorToast(userResponse.getError_desc());
                    }

                    break;
                case NETWORK_FAIL:
                    activity.showErrorToast();
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    activity.showErrorToast();
                    break;

                default:
                    activity.showErrorToast();
                    break;
            }
        }
    }

}
