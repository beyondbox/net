package com.appjumper.silkscreen.ui.my.enterprise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.ui.home.adapter.PassbyAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.appjumper.silkscreen.view.MyRecyclerView;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-18.
 * 物流路线发布
 */
public class EnterpriseReleaseActivity extends BaseActivity {
    private String start_id = "214";
    private String end_id = "";

    @Bind(R.id.tv_start)//货运起点
            TextView tv_start;
    @Bind(R.id.tv_end)//货运终点
            TextView tv_end;
    @Bind(R.id.tv_start_time)//出发时间
            TextView tv_start_time;

    @Bind(R.id.et_car_length)//车辆长度
            EditText et_car_length;
    @Bind(R.id.et_car_height)//车辆高度
            EditText et_car_height;
    @Bind(R.id.et_car_load)//车辆载重
            EditText et_car_load;
    @Bind(R.id.et_remark)//备注信息
            EditText et_remark;
    @Bind(R.id.et_car_width)//车辆宽度
            EditText et_car_width;
    @Bind(R.id.tv_expiry_date)//信息时长
            TextView tv_expiry_date;
    @Bind(R.id.gv_passby)//途经地
            MyRecyclerView gv_passby;
    @Bind(R.id.tv_hint)//提示
            TextView tvHint;

    private ArrayList<String> addressList = new ArrayList<>();
    private PassbyAdapter adapter;
    private String ids = "";
    private String names = "";

    private long expiry_datatime = 3600;

    private String[] expiry = {"1小时", "5小时", "12小时", "1天", "2天", "3天"};

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String startdata;
    private String type = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_enterprise_release);
        initBack();
        ButterKnife.bind(this);
        initTitle("创建路线");
        initRecycler(addressList);
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if (TextUtils.isEmpty(start_id)) {
                    showErrorToast("请选择起点");
                    return;
                }
                if (TextUtils.isEmpty(end_id)) {
                    showErrorToast("请选择终点");
                    return;
                }
                if (ids.equals("")) {
                    showErrorToast("请添加途经地");
                    return;
                }
                if (TextUtils.isEmpty(tv_start_time.getText().toString().trim())) {
                    showErrorToast("请选择出发时间");
                    return;
                }
                if (et_car_length.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入车辆长度");
                    return;
                }
                if (et_car_height.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入车辆高度");
                    return;
                }
                if (et_car_load.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入车辆载重");
                    return;
                }
                if (et_car_width.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入车辆宽度");
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

    private void initRecycler(final ArrayList<String> addresses) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gv_passby.setLayoutManager(linearLayoutManager);
        adapter = new PassbyAdapter(this, addresses);
        gv_passby.setAdapter(adapter);
        adapter.setOnItemClickLitener(new PassbyAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                addresses.remove(addresses.get(position));
                adapter.notifyDataSetChanged();
                if (addresses.size() > 0) {
                    tvHint.setVisibility(View.GONE);

                } else {
                    tvHint.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    //发布
    private Runnable submitRun = new Runnable() {
        private BaseResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("from", start_id);
                data.put("to", end_id);
                data.put("from_name", tv_start.getText().toString());
                data.put("to_name", tv_end.getText().toString());
                data.put("type", type);
                data.put("uid", getUserID());
                data.put("passby", ids);//途经地 id
                data.put("passby_name", names);//途经地名称
                data.put("car_length", et_car_length.getText().toString().trim());
                data.put("car_width", et_car_width.getText().toString().trim());
                data.put("car_height", et_car_height.getText().toString().trim());
                data.put("car_height", et_car_height.getText().toString().trim());
                data.put("car_load", et_car_load.getText().toString().trim());
                data.put("remark", et_remark.getText().toString().trim());
                data.put("date", startdata);
                //data.put("expiry_date", expiry_datatime + "");
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.LINEADD));
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
            EnterpriseReleaseActivity activity = (EnterpriseReleaseActivity) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://发布
                    progress.dismiss();
                    BaseResponse baseResponse = (BaseResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        //showErrorToast("发布成功");
                        start_Activity(context, AddServiceCompleteActivity.class, new BasicNameValuePair(Const.KEY_MESSAGE, "线路添加完成"), new BasicNameValuePair(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_LOGISTICS + ""));
                        ActivityTaskManager.getInstance().getActivity(AddServiceActivity.class).finish();
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

    @OnClick({R.id.tv_start, R.id.tv_end, R.id.tv_start_time, R.id.l_expiry_date, R.id.iv_add})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.l_expiry_date://信息时长
                Intent intent = new Intent(EnterpriseReleaseActivity.this, InformationSelectActivity.class);
                Bundle b = new Bundle();
                b.putStringArray("val", expiry);
                intent.putExtras(b);
                intent.putExtra("title", "信息时长");
                startActivityForResult(intent, 3);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case R.id.tv_start_time://出发时间
                viewData();
                break;
            case R.id.tv_start://起点
                startForResult_Activity(this, AddressSelectActivity.class, 1, new BasicNameValuePair("code", "1"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.tv_end://终点
                startForResult_Activity(this, AddressSelectActivity.class, 2, new BasicNameValuePair("code", "2"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.iv_add://货运途经
                startForResult_Activity(this, AddressSelectActivity.class, 4, new BasicNameValuePair("code", "3"), new BasicNameValuePair("type", "1"));
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
        OnTimeChangedListener timeListener = new OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);

            }
        };
        timePicker.setOnTimeChangedListener(timeListener);
        OnDateChangedListener dateListener = new OnDateChangedListener() {
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
                        tv_start_time.setText(startdata);
//                        updateLabel();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                }).show();
    }

    /**
     * 启动页面
     */
    private void launch(int requestCode, String city) {
        Intent intent = new Intent(EnterpriseReleaseActivity.this, AddressSelectActivity.class);
        intent.putExtra("flags", requestCode);
        intent.putExtra("city", city);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1://出发地
                start_id = data.getStringExtra("id");
                String start_name = data.getStringExtra("name");
                tv_start.setText(start_name);
                tv_start.setCompoundDrawables(null, null, null, null);
                break;
            case 2://目的地
                end_id = data.getStringExtra("id");
                String end_name = data.getStringExtra("name");
                tv_end.setText(end_name);
                tv_end.setCompoundDrawables(null, null, null, null);
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
                    case 5://三天
                        expiry_datatime = 3600 * 72;
                        break;
                }
                tv_expiry_date.setText(expiry[expiry_date]);
                break;
            case 4:
                addressList.add(data.getStringExtra("name"));
                adapter.notifyDataSetChanged();
                if (addressList.size() <= 1) {
                    names += data.getStringExtra("name");
                    ids += data.getStringExtra("id");
                } else {
                    names += "," + data.getStringExtra("name");
                    ids += "," + data.getStringExtra("id");
                }
                if (addressList.size() > 0) {
                    tvHint.setVisibility(View.GONE);

                } else {
                    tvHint.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }
}
