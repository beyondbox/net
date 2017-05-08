package com.appjumper.silkscreen.ui.inquiry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.ui.my.enterprise.ChoiceActivity;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;

import org.apache.http.message.BasicNameValuePair;

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
 * Created by Administrator on 2016-11-24.
 * 其他信息
 */
public class OtherInfoActivity extends BaseActivity {
    @Bind(R.id.et_number)//供货数量
            EditText etNumber;
    @Bind(R.id.tv_date)//供货日期
            TextView tvDate;
    @Bind(R.id.cb_freight)//运费
            CheckBox cbFreight;
    @Bind(R.id.cb_tax)//税
            CheckBox cbTax;
    @Bind(R.id.tv_address)//收货地址
            TextView tvAddress;
    @Bind(R.id.et_address)//详细地址
            EditText etAddress;
    @Bind(R.id.et_remark)//备注
            EditText etRemark;
    @Bind(R.id.tv_info_length)//信息时长
            TextView tvInfoLength;
    @Bind(R.id.et_contacts)//联系人
            EditText etContacts;
    @Bind(R.id.et_mobile)//电话
            EditText etMobile;

    private String productType;
    private String product_id;
    private String user_ids;
    private String imgs;
    private HashMap<String, String> data;

    private long expiry_datatime = 3600;

    private String[] expiry = {"1小时", "5小时", "12小时", "1天", "2天"};

    private String startdata = "";
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String province_id = "0";
    private String city_id = "0";
    private String county_id;
    private String type;
    private String identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_one_key_inquiry);
        initBack();
        ButterKnife.bind(this);
        initTitle("其他信息");
        Intent intent = getIntent();
        identity = intent.getStringExtra("identity");
        productType = intent.getStringExtra("product_type");
        product_id = intent.getStringExtra("product_id");
        user_ids = intent.getStringExtra("user_ids");
        imgs = intent.getStringExtra("imgs");
        type = intent.getStringExtra("type");
        data = (HashMap<String, String>) intent.getSerializableExtra("map");
    }

    private Runnable submitRun = new Runnable() {
        private BaseResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                data.put("uid", getUserID());
                if (cbFreight.isChecked()) {
                    data.put("freight", "1");
                } else {
                    data.put("freight", "0");
                }
                if (cbTax.isChecked()) {
                    data.put("tax", "1");
                } else {
                    data.put("tax", "0");
                }
                data.put("num", etNumber.getText().toString().trim());
                data.put("province", province_id);
                data.put("city", city_id);
                data.put("district", county_id);
                data.put("address", etAddress.getText().toString().trim());
                data.put("contacts", "淡定点");
                data.put("mobile", "都是对的");
                data.put("remark", etRemark.getText().toString().trim());
                data.put("expiry_date", expiry_datatime + "");
                data.put("time", startdata);
                data.put("product_type", productType);
                data.put("product_id", product_id);
                data.put("user_ids", user_ids);
                data.put("type", type);
                data.put("imgs", imgs);
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.INQUIRY_ADD));
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

    private MyHandler handler = new MyHandler(this);
    private class MyHandler extends Handler {
        private WeakReference<Context> reference;
        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            OtherInfoActivity activity = (OtherInfoActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://发布
                    progress.dismiss();
                    BaseResponse baseResponse = (BaseResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        //showErrorToast("发布成功");
                        start_Activity(context, InquiryCompleteActivity.class);
                        CommonApi.addLiveness(getUserID(), 5);
                        finish();
                        ActivityTaskManager.getInstance().removeActivity(InquirySpecificationActivity.class);
                        if (identity.equals("3")||identity.equals("4")) {
                            ActivityTaskManager.getInstance().removeActivity(ChoiceActivity.class);
                        }
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

    @OnClick({R.id.tv_address, R.id.tv_date, R.id.tv_info_length, R.id.tv_inquiry})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_address://收货地址
                startForResult_Activity(this, AddressSelectActivity.class, 1, new BasicNameValuePair("code", "1"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.tv_date://供货日期
                viewData();
                break;
            case R.id.tv_info_length://信息时长
                Intent intent = new Intent(OtherInfoActivity.this, InformationSelectActivity.class);
                Bundle b = new Bundle();
                b.putStringArray("val", expiry);
                intent.putExtras(b);
                intent.putExtra("title", "信息时长");
                startActivityForResult(intent, 3);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case R.id.tv_inquiry:
                if (etNumber.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入货物数量");
                    return;
                }
                if (tvDate.getText().toString().length() < 1) {
                    showErrorToast("请选择日期");
                    return;
                }
                if (tvAddress.getText().toString().length() < 1) {
                    showErrorToast("请选择收货地址");
                    return;
                }
                if (etAddress.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入详细地址");
                    return;
                }
//                if (etContacts.getText().toString().trim().length() < 1) {
//                    showErrorToast("请输入联系人姓名");
//                    return;
//                }
//                if (etMobile.getText().toString().trim().length() < 11) {
//                    showErrorToast("请输入正确的手机号");
//                    return;
//                }
                hideKeyboard();
                initProgressDialog();
                progress.show();
                progress.setMessage("正在发布...");
                new Thread(submitRun).start();
                break;
            default:
                break;
        }
    }

    private void viewData() {
        final Calendar time = Calendar.getInstance(Locale.CHINA);
        LinearLayout dateTimeLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_date_time_picker, null);
        final DatePicker datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.new_act_date_picker);
        final TimePicker timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.new_act_time_picker);
        timePicker.setIs24HourView(true);
        TimePicker.OnTimeChangedListener timeListener = new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);

            }
        };
        timePicker.setOnTimeChangedListener(timeListener);
        DatePicker.OnDateChangedListener dateListener = new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                // TODO Auto-generated method stub
                time.set(Calendar.YEAR, year);
                time.set(Calendar.MONTH, monthOfYear);
                time.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            }
        };

        datePicker.init(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH), dateListener);
        timePicker.setCurrentHour(time.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(time.get(Calendar.MINUTE));


        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择日期时间").setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        time.set(Calendar.YEAR, datePicker.getYear());
                        time.set(Calendar.MONTH, datePicker.getMonth());
                        time.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                        time.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                        time.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                        Date datatime = time.getTime();
                        startdata = format.format(datatime);
                        tvDate.setText(startdata);
//                        updateLabel();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1://货运起点
                county_id = data.getStringExtra("id");
                String start_name = data.getStringExtra("name");
                tvAddress.setText(start_name);
                break;
            case 3://信息时长
                int expiry_date = Integer.parseInt(data.getStringExtra("val"));
                switch (expiry_date) {
                    case 0://一小时
                        expiry_datatime = 3600 * 1;
                        break;
                    case 1://5小时
                        expiry_datatime = 3600 * 5;
                        break;
                    case 2://12小时
                        expiry_datatime = 3600 * 12;
                        break;
                    case 3://一天
                        expiry_datatime = 3600 * 24;
                        break;
                    case 4://两天
                        expiry_datatime = 3600 * 48;
                        break;
                }
                tvInfoLength.setText(expiry[expiry_date]);
                break;
            default:
                break;
        }
    }
}
