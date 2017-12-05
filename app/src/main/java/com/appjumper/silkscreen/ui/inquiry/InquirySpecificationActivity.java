package com.appjumper.silkscreen.ui.inquiry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.ui.home.process.ProcessingDetailsActivity;
import com.appjumper.silkscreen.ui.my.UserEditActivity;
import com.appjumper.silkscreen.ui.my.adapter.SpecChoiceAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.appjumper.silkscreen.R.id.et_remark;

/**
 * Created by Administrator on 2016-11-22.
 */
public class InquirySpecificationActivity extends BasePhotoGridActivity {
    @Bind(R.id.ll_specification)//规格布局
            LinearLayout llSpecification;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.rg_tab)
    RadioGroup rgTab;
    @Bind(R.id.rb_all)//全部企业
            RadioButton rbAll;
    @Bind(R.id.rb_auth)//认证企业
            RadioButton rbAuth;
    @Bind(R.id.rb_noauth)//未认证企业
            RadioButton rbNoauth;
    @Bind(R.id.ll_fit)//符合企业
            LinearLayout llFit;
    @Bind(R.id.tv_enterprise_info)//符合厂家信息
            TextView tvEnterpriseInfo;
    @Bind(R.id.tv_info_length)//信息时长
            TextView tvInfoLength;
    @Bind(et_remark)//备注
            EditText etRemark;

    private String type;
    private String productType;
    private ServiceProduct service;
    private List<Spec> list;
    private ImageResponse imgResponse;
    private String user_ids = "0";
    HashMap<String, String> map = new HashMap<>();
    private String identity;

    private long expiry_datatime = 3600;
    private String[] expiry = {"1小时", "5小时", "12小时", "1天", "2天", "3天"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_specifiaction_inquiry2);
        ButterKnife.bind(context);
        ActivityTaskManager.getInstance().putActivity(this);
        initBack();
        initView();
        initProgressDialog(false, "正在发布...");

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        identity = getIntent().getStringExtra("identity");
        if (identity.equals("2")) {
            rgTab.setVisibility(View.GONE);
            llFit.setVisibility(View.VISIBLE);
        } else if (identity.equals("3")) {
            rgTab.setVisibility(View.VISIBLE);
            llFit.setVisibility(View.GONE);
        }else if(identity.equals("4")){
            rgTab.setVisibility(View.GONE);
            llFit.setVisibility(View.GONE);
            user_ids = intent.getStringExtra("eid");
        }

        productType = intent.getStringExtra("productType");
        service = (ServiceProduct) intent.getSerializableExtra("service");
        list = service.getProduct_spec();
        initTitle(service.getName());
        tvName.setText(service.getName());

        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_all:
                        user_ids = "0";
                        break;
                    case R.id.rb_auth:
                        user_ids = "1";
                        break;
                    case R.id.rb_noauth:
                        user_ids = "2";
                        break;
                    default:
                        break;
                }
            }
        });

        getEnterpriseNum();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initViewSpecification();
                etRemark.setHint(service.getRemark());
            }
        }, 150);
    }



    /**
     * 初始化规格
     */
    private void initViewSpecification() {
        for (int i = 0; i < list.size(); i++) {
            String fildType = list.get(i).getFieldinput();

            if (fildType.equals("radio")) {
                initChoiceData(i);
            } else if(fildType.equals("text")) {
                initInputValue(i);
            }
        }
    }



    /**
     * 渲染单选选项
     * @param i
     */
    private void initChoiceData(int i) {
        Spec spec = list.get(i);

        View choiceView = LayoutInflater.from(context).inflate(R.layout.layout_spec_choice, null);
        llSpecification.addView(choiceView);
        choiceView.setTag("choice" + i);

        TextView txtRequired = (TextView) choiceView.findViewById(R.id.txtRequired);
        txtRequired.setVisibility(spec.getRequire().equals("1") ? View.VISIBLE : View.INVISIBLE);

        TextView txtName = (TextView) choiceView.findViewById(R.id.txtName);//规格名字
        txtName.setText(spec.getName());

        GridView gridChoice = (GridView) choiceView.findViewById(R.id.gridChoice);
        String [] arr = spec.getUnit().split(",");
        final SpecChoiceAdapter choiceAdapter = new SpecChoiceAdapter(context, Arrays.asList(arr));
        choiceAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        gridChoice.setAdapter(choiceAdapter);

        gridChoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choiceAdapter.changeSelected(position);
            }
        });

        String defaultChoice = spec.getUnit_default();
        if (!TextUtils.isEmpty(defaultChoice)) {
            int position = Integer.parseInt(defaultChoice);
            choiceAdapter.changeSelected(position);
        }
    }



    /**
     * 渲染输入值
     * @param i
     */
    private void initInputValue(int i) {
        Spec spec = list.get(i);

        View editView = LayoutInflater.from(this).inflate(R.layout.layout_spec_input_inquiry, null);
        llSpecification.addView(editView);
        editView.setTag("edit" + i);

        TextView txtRequired = (TextView) editView.findViewById(R.id.txtRequired);
        txtRequired.setVisibility(spec.getRequire().equals("1") ? View.VISIBLE : View.INVISIBLE);

        TextView txtName = (TextView) editView.findViewById(R.id.txtName);//规格名字
        EditText etValue = (EditText) editView.findViewById(R.id.etValue);//输入
        txtName.setText(spec.getName());
        etValue.setTag(i + "value");
        if (!TextUtils.isEmpty(spec.getMin_value()) && !TextUtils.isEmpty(spec.getMax_value()))
            etValue.setHint(spec.getMin_value() + " - " + spec.getMax_value());

        String datatype = spec.getDatatype();
        switch (datatype){
            case "integer"://'数字  1 或者 1-1',
                etValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case "currency":// '货币 1.00',
                etValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case "require":// '文本',
                etValue.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "english"://'英文字母',
                etValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                etValue .setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
            case "number*number":// '数字*数字  1*1',
                etValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            default:
                break;
        }
    }




    @OnClick({R.id.tv_next, R.id.ll_fit, R.id.tv_info_length})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next://发布询价

                if (TextUtils.isEmpty(getUser().getUser_nicename())) {
                    showErrorToast("您尚未设置昵称");
                    start_Activity(context, UserEditActivity.class,new BasicNameValuePair("title","昵称"),new BasicNameValuePair("hinttitle","点击输入昵称"),new BasicNameValuePair("key","nickname"));
                    return;
                }

                map.clear();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getFieldinput().equals("radio")) {
                        View choiceView = llSpecification.findViewWithTag("choice" + i);
                        GridView gridChoice = (GridView) choiceView.findViewById(R.id.gridChoice);
                        String [] valueArr = list.get(i).getUnit().split(",");

                        String checkedResult = "";
                        for (int j = 0; j < valueArr.length; j++) {
                            LinearLayout linearLayout = (LinearLayout) gridChoice.getChildAt(j);
                            if (((CheckBox)linearLayout.getChildAt(0)).isChecked()) {
                                checkedResult += valueArr[j] + ",";
                            }
                        }

                        if (list.get(i).getRequire().equals("1")) {
                            if (TextUtils.isEmpty(checkedResult)) {
                                showErrorToast("请选择" + list.get(i).getName());
                                return;
                            }
                        }

                        if (!TextUtils.isEmpty(checkedResult))
                            checkedResult = checkedResult.substring(0, checkedResult.length() - 1);
                        map.put(list.get(i).getFieldname(), checkedResult);

                    } else if (list.get(i).getFieldinput().equals("text")) {
                        View editView = llSpecification.findViewWithTag("edit" + i);
                        EditText etValue = (EditText) editView.findViewWithTag(i + "value");
                        String value = etValue.getText().toString().trim();

                        if(list.get(i).getRequire().equals("1")) {
                            if (TextUtils.isEmpty(value)) {
                                showErrorToast("请输入" + list.get(i).getName());
                                return;
                            }
                        }

                        String minValue = list.get(i).getMin_value();
                        String maxValue = list.get(i).getMax_value();
                        if (!TextUtils.isEmpty(minValue) || !TextUtils.isEmpty(maxValue)) {
                            if (!TextUtils.isEmpty(value)) {
                                if (Float.valueOf(value) < Float.valueOf(minValue) || Float.valueOf(value) > Float.valueOf(maxValue)) {
                                    showErrorToast(list.get(i).getName() + "的范围为" + list.get(i).getMin_value() + "-" + list.get(i).getMax_value());
                                    return;
                                }
                            }
                        }

                        map.put(list.get(i).getFieldname(), value);

                    }
                }

                if (identity.equals("2") && user_ids.equals("0")) {
                    showErrorToast("请选择符合厂家");
                    return;
                }


                if (service.getImg_require().equals("1")) {
                    if (selectedPicture.size() == 0) {
                        showErrorToast("请上传图纸信息");
                        return;
                    }
                    progress.show();
                    new Thread(new UpdateStringRun(thumbPictures)).start();

                } else {
                    if (selectedPicture.size() > 0) {
                        progress.show();
                        new Thread(new UpdateStringRun(thumbPictures)).start();
                    } else {
                        progress.show();
                        new Thread(submitRun).start();
                    }
                }

                break;
            case R.id.ll_fit://符合厂家
                startForResult_Activity(this, SelectCompanyActivity.class, 11, new BasicNameValuePair("type", type), new BasicNameValuePair("product_id", service.getId()), new BasicNameValuePair("product_type", ""));
                break;
            case R.id.tv_info_length://信息时长
                Intent intent = new Intent(context, InformationSelectActivity.class);
                Bundle b = new Bundle();
                b.putStringArray("val", expiry);
                intent.putExtras(b);
                intent.putExtra("title", "信息时长");
                startActivityForResult(intent, 3);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                break;
            default:
                break;
        }
    }



    // 如果不是切割的upLoadBitmap就很大
    public class UpdateStringRun implements Runnable {
        private ArrayList<String> thumbPictures;

        // thumbPictures 是 List<压缩图路径>
        public UpdateStringRun(ArrayList<String> thumbPictures) {
            this.thumbPictures = new ArrayList<String>();
            for (String str : thumbPictures) {
                if (!str.equals("" + BasePhotoGridActivity.PICTURE_UPDATE_ICON)) {
                    //去掉最后一个 +图片
                    this.thumbPictures.add(str);
                }
            }
        }

        @Override
        public void run() {
            ImageResponse retMap = null;
            try {
                // 如果不是切割的upLoadBitmap就很大,在这里压缩
                retMap = JsonParser.getImageResponse(HttpUtil.upload(thumbPictures, Url.UPLOADIMAGE));
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



    private Runnable submitRun = new Runnable() {
        private BaseResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                map.put("g", "api");
                map.put("m", "inquiry");
                map.put("a", "add");

                map.put("uid", getUserID());
                map.put("remark", etRemark.getText().toString().trim());
                map.put("expiry_date", expiry_datatime + "");
                //map.put("product_type", productType);
                map.put("product_id", service.getId());
                map.put("user_ids", user_ids);
                map.put("type", type);

                if (imgResponse != null)
                    map.put("imgs", imags(imgResponse.getData()));

                //response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(map), Url.INQUIRY_ADD));
                response = JsonParser.getBaseResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(map)));
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
            InquirySpecificationActivity activity = (InquirySpecificationActivity) reference.get();
            if (activity == null) {
                return;
            }

            switch (msg.what) {
                case 3://上传图片
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        new Thread(submitRun).start();
                    } else {
                        progress.dismiss();
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT://发布
                    progress.dismiss();
                    BaseResponse baseResponse = (BaseResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        start_Activity(context, InquiryCompleteActivity.class);
                        CommonApi.addLiveness(getUserID(), 5);
                        finish();

                        if (identity.equals("4")) {
                            if (ProcessingDetailsActivity.instance != null)
                                ProcessingDetailsActivity.instance.finish();
                        }
                        /*if (identity.equals("3")||identity.equals("4")) {
                            ActivityTaskManager.getInstance().removeActivity(ChoiceActivity.class);
                        }*/
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }

                    break;
                case NETWORK_FAIL:
                    progress.dismiss();
                    activity.showFailTips(getResources().getString(R.string.requst_fail));
                    break;
                default:
                    progress.dismiss();
                    activity.showFailTips(getResources().getString(R.string.requst_fail));
                    break;
            }
        }
    }


    private String imags(List<ImageResponse.Data> data) {
        String str = "";
        for (int i = 0; i < data.size(); i++) {
            str += data.get(i).getImg_id();
            if (i < (data.size() - 1)) {
                str += ",";
            }
        }
        return str;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        List<Enterprise> enterpriseList;
        switch (requestCode) {
            case 10:
                ArrayList<String> resultList = data.getStringArrayListExtra("list");
                String fieldname = data.getStringExtra("fieldname");
                String result = "";
                for (int i = 0; i < resultList.size(); i++) {
                    result += "," + resultList.get(i);
                }
                result = result.substring(1);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getFieldname().equals(fieldname)) {
                        View choiceView = llSpecification.findViewWithTag("choice" + i);
                        TextView tvMaterial = (TextView) choiceView.findViewWithTag("material" + i);
                        tvMaterial.setText(result);
                    }
                }
                break;
            case 11:
                enterpriseList = (List<Enterprise>) data.getSerializableExtra("list");
                tvEnterpriseInfo.setText(enterpriseList.get(0).getEnterprise_name() + "等" + enterpriseList.size() + "家企业");
                String ids = "";
                for (int i = 0; i < enterpriseList.size(); i++) {
                    ids += "," + enterpriseList.get(i).getEnterprise_id();
                }
                ids = ids.substring(1);
                user_ids = ids;
                break;
            case 12:
                String fiedname = data.getStringExtra("fieldname");
                String resultValue = data.getStringExtra("choice");
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getFieldname().equals(fiedname)) {
                        View choiceView = llSpecification.findViewWithTag("choice" + i);
                        TextView tvMaterial = (TextView) choiceView.findViewWithTag("material" + i);
                        tvMaterial.setText(resultValue);
                    }
                }
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
                tvInfoLength.setText(expiry[expiry_date]);
                break;
            default:
                break;
        }
    }



    /**
     * 获取企业数量
     */
    private void getEnterpriseNum() {
        RequestParams params = MyHttpClient.getApiParam("inquiry", "enterprise_list");
        params.put("type", type);
        params.put("product_id", service.getId());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (isDestroyed())
                    return;

                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<Enterprise> authList = GsonUtil.getEntityList(dataObj.getJSONArray("auth").toString(), Enterprise.class);
                        List<Enterprise> noAuthList = GsonUtil.getEntityList(dataObj.getJSONArray("noauth").toString(), Enterprise.class);
                        rbAll.setText("全部企业 (" + (authList.size() + noAuthList.size()) + "家)");
                        rbAuth.setText("认证企业 (" + authList.size() + "家)");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }















    /**
     * 初始化规格(旧版)
     */
    /*private void initViewSpecification2() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFieldinput().equals("radio")) {

                View choiceView = LayoutInflater.from(this).inflate(R.layout.layout_specification_choice, null);
                llSpecification.addView(choiceView);
                choiceView.setTag("choice" + i);
                TextView tvName = (TextView) choiceView.findViewById(R.id.tv_name);//规格名字
                tvName.setText(list.get(i).getName());
                final int finalI = i;
                choiceView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("title", list.get(finalI).getName());
                        intent.putExtra("list", list.get(finalI).getUnit());
                        intent.putExtra("fieldname", list.get(finalI).getFieldname());
                        if (type.equals("3")) {
                            intent.setClass(InquirySpecificationActivity.this, SingleChoiceActivity.class);
                            startActivityForResult(intent, 12);
                        } else {
                            intent.setClass(InquirySpecificationActivity.this, MultipleChoiceActivity.class);
                            startActivityForResult(intent, 10);
                        }

                        overridePendingTransition(R.anim.push_left_in,
                                R.anim.push_left_out);
                    }
                });
                TextView tvMaterial = (TextView) choiceView.findViewById(R.id.tv_material);//材质
                tvMaterial.setHint("请选择" + list.get(i).getName());
                tvMaterial.setTag("material" + i);
            } else {
                View editView = LayoutInflater.from(this).inflate(R.layout.layout_specification_edit_one2, null);
                llSpecification.addView(editView);
                editView.setTag("edit" + i);
                EditText etLow = (EditText) editView.findViewById(R.id.et_low);//输入
                TextView tvName = (TextView) editView.findViewById(R.id.tv_name);//规格名字
                TextView tvUnit = (TextView) editView.findViewById(R.id.tv_unit);//单位
                String datatype = list.get(i).getDatatype();
                switch (datatype) {
                    case "integer"://'数字  1 或者 1-1',
                        etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        break;
                    case "currency":// '货币 1.00',
                        etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        break;
                    case "require":// '文本',
                        etLow.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case "english"://'英文字母',
                        etLow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        etLow.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        break;
                    case "number*number":// '数字*数字  1*1',
                        etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        break;
                    default:
                        break;
                }
                etLow.setHint("输入" + list.get(i).getName());
                tvName.setText(list.get(i).getName());
                etLow.setTag(i + "low");
                tvUnit.setText(list.get(i).getUnit());
                if (list.get(i).getName().equals("存量") && !type.equals("3")) {
                    editView.setVisibility(View.GONE);
                }

            }
        }
    }*/




    /**
     * 发布询价（旧版）
     */
    /*private void submit() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFieldinput().equals("radio")) {
                View choiceView = llSpecification.findViewWithTag("choice" + i);
                GridView gridChoice = (GridView) choiceView.findViewById(R.id.gridChoice);
                String [] valueArr = list.get(i).getUnit().split(",");

                String checkedResult = "";
                for (int j = 0; j < valueArr.length; j++) {
                    LinearLayout linearLayout = (LinearLayout) gridChoice.getChildAt(j);
                    if (((CheckBox)linearLayout.getChildAt(0)).isChecked()) {
                        checkedResult += valueArr[j] + ",";
                    }
                }

                if (TextUtils.isEmpty(checkedResult)) {
                    showErrorToast("请选择" + list.get(i).getName());
                    return;
                }
                checkedResult = checkedResult.substring(0, checkedResult.length() - 1);
                map.put(list.get(i).getFieldname(), checkedResult);



                        *//*View choiceView = llSpecification.findViewWithTag("choice" + i);
                        TextView tvMaterial = (TextView) choiceView.findViewWithTag("material" + i);
                        if (tvMaterial.getText().toString().length() < 1) {
                            showErrorToast("请选择" + list.get(i).getName());
                            return;
                        }
                        map.put(list.get(i).getFieldname(), tvMaterial.getText().toString());*//*
            } else {
                View editView = llSpecification.findViewWithTag("edit" + i);
                EditText etLow = (EditText) editView.findViewWithTag(i + "value");
                if (editView.getVisibility() == View.VISIBLE) {
                    if (etLow.getText().toString().trim().length() < 1) {
                        showErrorToast("请输入" + list.get(i).getName());
                        return;
                    }
                    map.put(list.get(i).getFieldname(), etLow.getText().toString().trim());
                }

            }
        }
        if (identity.equals("2") && user_ids.equals("0")) {
            showErrorToast("请选择符合厂家");
            return;
        }
        if (thumbPictures.size() <= 1) {
            progress.show();
            progress.setMessage("正在发布...");
            new Thread(submitRun).start();
        } else {

            progress.show();
            progress.setMessage("正在上传图片...");
            new Thread(new UpdateStringRun(thumbPictures)).start();
        }
    }*/

}
