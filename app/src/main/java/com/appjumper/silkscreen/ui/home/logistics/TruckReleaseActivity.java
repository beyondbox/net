package com.appjumper.silkscreen.ui.home.logistics;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.util.Const;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-18.
 * 发布求车详情
 */
public class TruckReleaseActivity extends BaseActivity {
    @Bind(R.id.tv_start)//货运起点
            TextView tvStart;
    @Bind(R.id.tv_end)//货运终点
            TextView tvEnd;
    @Bind(R.id.et_goods_name)//货物名称
            EditText etGoodsName;
    @Bind(R.id.et_goods_number)//货物数量
            EditText etGoodsNumber;
    @Bind(R.id.txtNumberUnit) //数量的单位
    TextView txtNumberUnit;
    @Bind(R.id.et_goods_weight)//货物重量
            EditText etGoodsWeight;
    @Bind(R.id.tv_load_time)//装货时间
            TextView tvLoadTime;
    @Bind(R.id.et_remark)//备注
            EditText etRemark;
    @Bind(R.id.tv_info_length)//信息时长
            TextView tvInfoLength;

    private String start_id;
    private String end_id;

    private ServiceProduct selectedProduct; //选择的货物

    private long expiry_datatime = 3600 * 6;

    private String[] expiry = {"6小时", "12小时", "1天", "2天", "3天"};

    private String startdata = "";
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_truck_release);
        initBack();
        ButterKnife.bind(this);
        initTitle("求车详情");
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if (tvStart.getText().toString().length() < 1) {
                    showErrorToast("请选择货运起点");
                    return;
                }
                if (tvEnd.getText().toString().length() < 1) {
                    showErrorToast("请选择货运终点");
                    return;
                }
                if (etGoodsName.getText().toString().trim().length() < 1) {
                    showErrorToast("请选择货物");
                    return;
                }
                if (etGoodsNumber.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入货物数量");
                    return;
                }
                if (etGoodsWeight.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入货物重量");
                    return;
                }
                if (tvLoadTime.getText().toString().length() < 1) {
                    showErrorToast("请选择装货时间");
                    return;
                }
                hideKeyboard();
                initProgressDialog();
                progress.show();
                progress.setMessage("正在发布...");
                new Thread(submitRun).start();
            }
        });

    }

    private Runnable submitRun = new Runnable() {
        private BaseResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid",getUserID());
                data.put("from", start_id);
                data.put("to", end_id);
                data.put("name", selectedProduct.getId());
                data.put("productName", selectedProduct.getName());
                data.put("number", etGoodsNumber.getText().toString().trim());
                data.put("weight", etGoodsWeight.getText().toString().trim());
                data.put("remark", etRemark.getText().toString().trim());
                data.put("expiry_date", expiry_datatime + "");
                data.put("date", startdata);
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.TRUCK_RELEASE));
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
           TruckReleaseActivity activity = (TruckReleaseActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://发布
                    progress.dismiss();
                    BaseResponse baseResponse = (BaseResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        showErrorToast("发布成功");
                        CommonApi.addLiveness(getUserID(), 19);
                        finish();
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

    @OnClick({R.id.tv_start, R.id.tv_end, R.id.tv_load_time, R.id.tv_info_length, R.id.et_goods_name})
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_start://起点
                startForResult_Activity(this, AddressSelectActivity.class, 1, new BasicNameValuePair("code", "1"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.tv_end://终点
                startForResult_Activity(this, AddressSelectActivity.class, 2, new BasicNameValuePair("code", "2"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.tv_load_time://装货时间
                viewData();
                break;
            case R.id.tv_info_length://信息时长
                intent = new Intent(TruckReleaseActivity.this, InformationSelectActivity.class);
                Bundle b = new Bundle();
                b.putStringArray("val", expiry);
                intent.putExtras(b);
                intent.putExtra("title", "信息时长");
                startActivityForResult(intent, 3);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case R.id.et_goods_name:
                intent = new Intent(context, ProductSelectActivity.class);
                intent.putExtra(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_STOCK);
                intent.putExtra(Const.KEY_ACTION, Const.ACTION_ADD_PRODUCT);
                startActivityForResult(intent, Const.REQUEST_CODE_SELECT_PRODUCT);
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
                        tvLoadTime.setText(startdata);
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
                start_id = data.getStringExtra("id");
                String start_name = data.getStringExtra("name");
                tvStart.setText(start_name);
                break;
            case 2://货运终点
                end_id = data.getStringExtra("id");
                String end_name = data.getStringExtra("name");
                tvEnd.setText(end_name);
                break;
            case 3://信息时长
                int expiry_date = Integer.parseInt(data.getStringExtra("val"));
                switch (expiry_date) {
                    case 0://6小时
                        expiry_datatime = 3600 * 6;
                        break;
                    case 1://12小时
                        expiry_datatime = 3600 * 12;
                        break;
                    case 2://一天
                        expiry_datatime = 3600 * 24;
                        break;
                    case 3://两天
                        expiry_datatime = 3600 * 48;
                        break;
                    case 4://三天
                        expiry_datatime = 3600 * 72;
                        break;
                }
                tvInfoLength.setText(expiry[expiry_date]);
                break;
            case Const.REQUEST_CODE_SELECT_PRODUCT: //选择货物
                selectedProduct = (ServiceProduct) data.getSerializableExtra(Const.KEY_OBJECT);
                etGoodsName.setText(selectedProduct.getName());
                List<Spec> specList = selectedProduct.getProduct_spec();
                if (specList != null && specList.size() > 0) {
                    Spec spec = specList.get(specList.size() - 1);
                    if (spec.getFieldname().equals("cunliang"))
                        txtNumberUnit.setText(spec.getUnit());
                    else
                        txtNumberUnit.setText("");
                } else {
                    txtNumberUnit.setText("");
                }
                break;
            default:
                break;
        }
    }
}
