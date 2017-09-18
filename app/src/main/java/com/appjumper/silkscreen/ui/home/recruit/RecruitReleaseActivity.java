package com.appjumper.silkscreen.ui.home.recruit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectCityActivity;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.ui.home.equipment.SelectActivity;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
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
    @Bind(R.id.et_experience)
    EditText etExperience;
    @Bind(R.id.et_salary)
    EditText etSalary;
    @Bind(R.id.tv_work_address)
    TextView tvWorkAddress;
    @Bind(R.id.tv_info_length)
    TextView tvInfoLength;
    @Bind(R.id.et_responsibilities)
    EditText etResponsibilities;
    @Bind(R.id.edtTxtTitle)
    EditText edtTxtTitle;
    @Bind(R.id.txtAdressDetail)
    EditText txtAdressDetail;
    @Bind(R.id.right)
    TextView txtRight;

    private long expiry_datatime = 3600 * 72;
    private String[] expiry = {"3天", "5天", "10天", "30天"};//信息时长

    private String[] experiences = {"不限", "本科", "大专", "高中"};//学历
    private String address_id = "3775"; //默认是安平县城

    private String sex = "不限";
    private String workType = "全职";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_recruit_release2);
        initBack();
        ButterKnife.bind(this);
        initTitle("招聘");
        initRightButton("发布", new RightButtonListener() {
            @Override
            public void click() {
                if (TextUtils.isEmpty(edtTxtTitle.getText().toString().trim())) {
                    showErrorToast("请输入标题");
                    return;
                }
                if (tvJobPosition.getText().toString().length() < 1) {
                    showErrorToast("请选择招聘职位");
                    return;
                }
                if (etSalary.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入薪资范围");
                    return;
                }
                if (etExperience.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入工作经验要求");
                    return;
                }
                if (TextUtils.isEmpty(txtAdressDetail.getText().toString().trim())) {
                    showErrorToast("请输入详细地址");
                    return;
                }
                if (etResponsibilities.getText().toString().trim().length() < 1) {
                    showErrorToast("请输入岗位职责");
                    return;
                }
                hideKeyboard();
                initProgressDialog(false, "");
                progress.show();
                progress.setMessage("正在发布...");
                new Thread(submitRun).start();
            }
        });

    }



    @OnCheckedChanged({R.id.rdoBtnfulltime, R.id.rdoBtnParttime, R.id.rdoBtnAll, R.id.rdoBtnMan, R.id.rdoBtnWoman})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rdoBtnfulltime:
                    workType = "全职";
                    break;
                case R.id.rdoBtnParttime:
                    workType = "兼职";
                    break;
                case R.id.rdoBtnAll:
                    sex = "不限";
                    break;
                case R.id.rdoBtnMan:
                    sex = "仅男士";
                    break;
                case R.id.rdoBtnWoman:
                    sex = "仅女士";
                    break;
                default:
                    break;
            }
        }
    }



    private Runnable submitRun = new Runnable() {
        private BaseResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();

                int infoType;
                Enterprise enterprise = getUser().getEnterprise();
                if (enterprise != null && enterprise.getEnterprise_auth_status().equals("2"))
                    infoType = Const.INFO_TYPE_COM;
                else
                    infoType = Const.INFO_TYPE_PER;

                data.put("recruit_type", infoType + "");
                data.put("uid", getUserID());
                data.put("title", edtTxtTitle.getText().toString().trim());
                data.put("name", tvJobPosition.getText().toString());
                data.put("education", tvEducation.getText().toString());
                data.put("expiry_date", expiry_datatime + "");
                data.put("gender", sex);
                data.put("experience", etExperience.getText().toString().trim());
                data.put("salary", etSalary.getText().toString().trim());
                data.put("place", address_id);
                data.put("responsibilities", etResponsibilities.getText().toString().trim());
                data.put("remark", workType);
                data.put("address", txtAdressDetail.getText().toString().trim());
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

            if (isDestroyed())
                return;

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

    @OnClick({R.id.tv_experience, R.id.tv_info_length, R.id.tv_work_address, R.id.tv_job_position, R.id.txtConfirm})
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tv_experience:
                intent = new Intent(RecruitReleaseActivity.this, InformationSelectActivity.class);
                bundle = new Bundle();
                bundle.putStringArray("val", experiences);
                intent.putExtra("title", "学历要求");
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
            case R.id.tv_job_position:
                startForResult_Activity(RecruitReleaseActivity.this, SelectActivity.class, 13, new BasicNameValuePair("title", "招聘职位"), new BasicNameValuePair("type", "2"));
                break;
            case R.id.txtConfirm:
                txtRight.performClick();
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
                    case 0://3天
                        expiry_datatime = 3600 * 72;
                        break;
                    case 1://5天
                        expiry_datatime = 3600 * 120;
                        break;
                    case 2://10天
                        expiry_datatime = 3600 * 240;
                        break;
                    case 3://30天
                        expiry_datatime = 3600 * 720;
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
            case 13:
                jobCheck(data.getStringExtra("name"));
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    /**
     * 检查是否已发布过该职位的招聘
     */
    private void jobCheck(final String name) {
        initProgressDialog(false, "");

        RequestParams params = MyHttpClient.getApiParam("service", "oneCheck");
        params.put("uid", getUserID());
        params.put("type", 5);
        params.put("product_id", name);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        tvJobPosition.setText(name);
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showErrorToast(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }

}
