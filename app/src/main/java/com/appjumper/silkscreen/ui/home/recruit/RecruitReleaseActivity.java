package com.appjumper.silkscreen.ui.home.recruit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectCityActivity;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.ui.home.equipment.SelectActivity;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-18.
 * 发布招聘
 */
public class RecruitReleaseActivity extends BaseActivity {
    @Bind(R.id.tv_job_position)
    TextView tvJobPosition;
    @Bind(R.id.tv_experience)//学历
            TextView tvEducation;
    @Bind(R.id.tv_gender)
    TextView tvGender;
    @Bind(R.id.et_experience)
    EditText etExperience;
    @Bind(R.id.tv_job_form)
    TextView tvJobForm;
    @Bind(R.id.et_salary)
    EditText etSalary;
    @Bind(R.id.tv_work_address)
    TextView tvWorkAddress;
    @Bind(R.id.tv_info_length)
    TextView tvInfoLength;
    @Bind(R.id.et_responsibilities)
    EditText etResponsibilities;

    private long expiry_datatime = 3600;

    private String[] expiry = {"1小时", "5小时", "12小时", "1天", "2天", "3天"};//信息时长

    private String[] experiences = {"不限", "本科", "大专", "高中"};//学历
    private String[] genders = {"不限","男", "女"};//性别
    private String[] forms = {"兼职", "全职"};//工作类型
    private String address_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_recruit_release);
        initBack();
        ButterKnife.bind(this);
        initTitle("招聘");
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if (tvJobPosition.getText().toString().length() < 1) {
                    showErrorToast("请选择招聘职位");
                    return;
                }
                if (tvEducation.getText().toString().length() < 1) {
                    showErrorToast("请选择学历");
                    return;
                }
                if (tvGender.getText().toString().length() < 1) {
                    showErrorToast("请选择性别");
                    return;
                }
                if (etExperience.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入工作经验");
                    return;
                }
                if (tvJobForm.getText().toString().length() < 1) {
                    showErrorToast("请选择职位类型");
                    return;
                }
                if (etSalary.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入薪资范围");
                    return;
                }
                if (tvWorkAddress.getText().toString().length() < 1) {
                    showErrorToast("请选择工作地点");
                    return;
                }
                if (etResponsibilities.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入岗位职责");
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
                data.put("uid", getUserID());
                data.put("name", tvJobPosition.getText().toString());
                data.put("education", tvEducation.getText().toString());
                data.put("expiry_date", expiry_datatime + "");
                data.put("gender", tvGender.getText().toString());
                data.put("experience", etExperience.getText().toString().trim());
                data.put("salary", etSalary.getText().toString().trim());
                data.put("place", address_id);
                data.put("responsibilities", etResponsibilities.getText().toString().trim());
                data.put("remark", tvJobForm.getText().toString());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.RECRUIT_RELEASE));
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
            RecruitReleaseActivity activity = (RecruitReleaseActivity) reference.get();
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

    @OnClick({R.id.tv_experience, R.id.tv_info_length, R.id.tv_work_address, R.id.tv_gender, R.id.tv_job_form, R.id.tv_job_position})
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tv_experience:
                intent = new Intent(RecruitReleaseActivity.this, InformationSelectActivity.class);
                bundle = new Bundle();
                bundle.putStringArray("val", experiences);
                intent.putExtra("title", "出租形式");
                intent.putExtras(bundle);
                startActivityForResult(intent, 11);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case R.id.tv_info_length:
                intent = new Intent(RecruitReleaseActivity.this, InformationSelectActivity.class);
                Bundle b = new Bundle();
                b.putStringArray("val", expiry);
                intent.putExtras(b);
                intent.putExtra("title", "信息时长");
                startActivityForResult(intent, 12);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case R.id.tv_work_address:
                startForResult_Activity(this,AddressSelectCityActivity.class,1,new BasicNameValuePair("type","2"),new BasicNameValuePair("id","208"),new BasicNameValuePair("code","1"));
//                startForResult_Activity(this, AddressSelectActivity.class, 1, new BasicNameValuePair("code", "1"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.tv_gender:
                intent = new Intent(RecruitReleaseActivity.this, InformationSelectActivity.class);
                bundle = new Bundle();
                bundle.putStringArray("val", genders);
                intent.putExtras(bundle);
                intent.putExtra("title", "性别");
                startActivityForResult(intent, 10);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case R.id.tv_job_form:
                intent = new Intent(RecruitReleaseActivity.this, InformationSelectActivity.class);
                bundle = new Bundle();
                bundle.putStringArray("val", forms);
                intent.putExtras(bundle);
                intent.putExtra("title", "职位类型");
                startActivityForResult(intent, 9);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            case R.id.tv_job_position:
                startForResult_Activity(RecruitReleaseActivity.this, SelectActivity.class, 13, new BasicNameValuePair("title", "招聘职位"), new BasicNameValuePair("type", "2"));
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 12://信息时长
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
                tvInfoLength.setText(expiry[expiry_date]);
                break;
            case 11:
                int selectPosition = Integer.parseInt(data.getStringExtra("val"));
                tvEducation.setText(experiences[selectPosition]);
                break;
            case 1://地点
                address_id = data.getStringExtra("id");
                String address_name = data.getStringExtra("name");
                tvWorkAddress.setText(address_name);
                break;
            case 10:
                int selectGender = Integer.parseInt(data.getStringExtra("val"));
                tvGender.setText(genders[selectGender]);
                break;
            case 9:
                int selectForm = Integer.parseInt(data.getStringExtra("val"));
                tvJobForm.setText(forms[selectForm]);
                break;
            case 13:
                tvJobPosition.setText(data.getStringExtra("name"));
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
