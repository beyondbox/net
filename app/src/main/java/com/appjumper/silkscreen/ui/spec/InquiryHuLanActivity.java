package com.appjumper.silkscreen.ui.spec;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.InformationSelectActivity;
import com.appjumper.silkscreen.ui.inquiry.InquiryCompleteActivity;
import com.appjumper.silkscreen.ui.inquiry.SelectCompanyActivity;
import com.appjumper.silkscreen.ui.my.adapter.SpecChoiceAdapter;
import com.appjumper.silkscreen.ui.my.enterprise.AddServiceCompleteActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.LogHelper;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 护栏网询价
 * Created by Botx on 2017/5/23.
 */

public class InquiryHuLanActivity extends BasePhotoGridActivity {

    @Bind(R.id.llSpecKuangJia)
    LinearLayout llSpecKuangJia;
    @Bind(R.id.llSpecLiZhu)
    LinearLayout llSpecLiZhu;

    @Bind(R.id.chkKuangJia)
    CheckBox chkKuangJia;
    @Bind(R.id.chkLiZhu)
    CheckBox chkLiZhu;

    @Bind(R.id.llYuanZhu)
    LinearLayout llYuanZhu;
    @Bind(R.id.llFangZhu)
    LinearLayout llFangZhu;
    @Bind(R.id.llTaoXingZhu)
    LinearLayout llTaoXingZhu;

    @Bind(R.id.choiceXZGuan)
    GridView choiceXZGuan;
    @Bind(R.id.choiceXZLiZhu)
    GridView choiceXZLiZhu;
    @Bind(R.id.choiceZhiJing)
    GridView choiceZhiJing;
    @Bind(R.id.choiceChiCun)
    GridView choiceChiCun;

    @Bind(R.id.lowSuHou)
    EditText lowSuHou;
    @Bind(R.id.lowSuQian)
    EditText lowSuQian;
    @Bind(R.id.lowKongKuan)
    EditText lowKongKuan;
    @Bind(R.id.lowKongChang)
    EditText lowKongChang;
    @Bind(R.id.lowPianKuan)
    EditText lowPianKuan;
    @Bind(R.id.lowPianChang)
    EditText lowPianChang;
    @Bind(R.id.lowWeightWangPian)
    EditText lowWeightWangPian;
    @Bind(R.id.lowMianKuan)
    EditText lowMianKuan;
    @Bind(R.id.lowMianChang)
    EditText lowMianChang;
    @Bind(R.id.lowGuanHou)
    EditText lowGuanHou;
    @Bind(R.id.lowLiZhuKuan)
    EditText lowLiZhuKuan;
    @Bind(R.id.lowLiZhuChang)
    EditText lowLiZhuChang;
    @Bind(R.id.lowBiHou)
    EditText lowBiHou;
    @Bind(R.id.lowHeight)
    EditText lowHeight;
    @Bind(R.id.lowWeightLiZhu)
    EditText lowWeightLiZhu;

    @Bind(R.id.etNumber)
    EditText etNumber;
    @Bind(R.id.et_remark)
    EditText et_remark;
    @Bind(R.id.tv_info_length)//信息时长
    TextView tvInfoLength;
    @Bind(R.id.tv_enterprise_info)//符合厂家信息
    TextView tvEnterpriseInfo;
    @Bind(R.id.rg_tab)
    RadioGroup rgTab;
    @Bind(R.id.ll_fit)//符合企业
    LinearLayout llFit;
    @Bind(R.id.txtPic)
    TextView txtPic;
    @Bind(R.id.llTime)
    LinearLayout llTime;
    @Bind(R.id.tv_next)
    TextView tv_next;

    @Bind(R.id.rb_all)//全部企业
            RadioButton rbAll;
    @Bind(R.id.rb_auth)//认证企业
            RadioButton rbAuth;


    private String [] xzGuanArr = {"方管", "圆管"};
    private String [] xzLiZhuArr = {"圆柱", "方柱", "桃形柱"};
    private String [] zhiJingArr = {"48", "60", "75", "其它"};
    private String [] chiCunArr = {"5*10", "7*10", "7*15", "其它"};

    private SpecChoiceAdapter xzGuanAdapter;
    private SpecChoiceAdapter xzLiZhuAdapter;
    private SpecChoiceAdapter zhiJingAdapter;
    private SpecChoiceAdapter chiCunAdapter;

    private Map<String, String> map = new HashMap<>();
    private ImageResponse imgResponse;

    private int action;
    private String type;
    private String user_ids = "0";
    private String identity;

    private long expiry_datatime = 3600;
    private String[] expiry = {"1小时", "5小时", "12小时", "1天", "2天"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_hulan);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        action = intent.getIntExtra(Const.KEY_ACTION, 0);
        type = intent.getStringExtra("type");
        if (action == Const.REQUEST_CODE_RELEASE_STOCK) {
            rgTab.setVisibility(View.GONE);
            txtPic.setText("产品图片");
            llTime.setVisibility(View.GONE);
            tv_next.setText("确定");
        } else {
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
        }


        initData();
        initView();
        initBack();
        initTitle("护栏网");
        initProgressDialog(false, "正在发布...");

        if (action != Const.REQUEST_CODE_RELEASE_STOCK)
            getEnterpriseNum();
    }


    private void initData() {
        xzGuanAdapter = new SpecChoiceAdapter(context, Arrays.asList(xzGuanArr));
        xzGuanAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        choiceXZGuan.setAdapter(xzGuanAdapter);
        choiceXZGuan.setOnItemClickListener(new MyItemClickImpl(xzGuanAdapter));

        zhiJingAdapter = new SpecChoiceAdapter(context, Arrays.asList(zhiJingArr));
        zhiJingAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        choiceZhiJing.setAdapter(zhiJingAdapter);
        choiceZhiJing.setOnItemClickListener(new MyItemClickImpl(zhiJingAdapter));

        chiCunAdapter = new SpecChoiceAdapter(context, Arrays.asList(chiCunArr));
        chiCunAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        choiceChiCun.setAdapter(chiCunAdapter);
        choiceChiCun.setOnItemClickListener(new MyItemClickImpl(chiCunAdapter));

        xzLiZhuAdapter = new SpecChoiceAdapter(context, Arrays.asList(xzLiZhuArr));
        xzLiZhuAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        choiceXZLiZhu.setAdapter(xzLiZhuAdapter);
        choiceXZLiZhu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                xzLiZhuAdapter.changeSelected(position);
                switch (position) {
                    case 0:
                        llYuanZhu.setVisibility(View.VISIBLE);
                        llFangZhu.setVisibility(View.GONE);
                        llTaoXingZhu.setVisibility(View.GONE);
                        break;
                    case 1:
                        llFangZhu.setVisibility(View.VISIBLE);
                        llYuanZhu.setVisibility(View.GONE);
                        llTaoXingZhu.setVisibility(View.GONE);
                        break;
                    case 2:
                        llTaoXingZhu.setVisibility(View.VISIBLE);
                        llYuanZhu.setVisibility(View.GONE);
                        llFangZhu.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });

        xzGuanAdapter.changeSelected(0);
        zhiJingAdapter.changeSelected(0);
        chiCunAdapter.changeSelected(0);
        xzLiZhuAdapter.changeSelected(0);

        llFangZhu.setVisibility(View.GONE);
        llTaoXingZhu.setVisibility(View.GONE);
        llSpecLiZhu.setVisibility(View.GONE);
        llSpecKuangJia.setVisibility(View.GONE);

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
    }


    /**
     * 发布
     */
    private void submit() {
        if (!isDataOK())
            return;

        map.clear();

        map.put("suhousijing", lowSuHou.getText().toString().trim());
        map.put("suqiansijing", lowSuQian.getText().toString().trim());
        map.put("wangkongkuan", lowKongKuan.getText().toString().trim());
        map.put("wangkongchang", lowKongChang.getText().toString().trim());
        map.put("wangpiankuan", lowPianKuan.getText().toString().trim());
        map.put("wangpianchang", lowPianChang.getText().toString().trim());
        map.put("wangpianzhongliang", lowWeightWangPian.getText().toString().trim());
        map.put("cunliang", etNumber.getText().toString().trim());

        if (chkKuangJia.isChecked()) {
            map.put("guanxingzhuang", getSelected(choiceXZGuan, xzGuanArr));
            map.put("guanhengjiemiankuan", lowMianKuan.getText().toString().trim());
            map.put("guanhengjiemianchang", lowMianChang.getText().toString().trim());
            map.put("guanghoudu", lowGuanHou.getText().toString().trim());
        } else {
            map.put("guanxingzhuang", "");
            map.put("guanhengjiemiankuan", "");
            map.put("guanhengjiemianchang", "");
            map.put("guanghoudu", "");
        }

        if (chkLiZhu.isChecked()) {
            map.put("lizhuxingzhuang", getSelected(choiceXZLiZhu, xzLiZhuArr));

            if (llYuanZhu.getVisibility() == View.VISIBLE)
                map.put("zhijing", getSelected(choiceZhiJing, zhiJingArr));
            else
                map.put("zhijing", "");

            if (llFangZhu.getVisibility() == View.VISIBLE) {
                map.put("kuan", lowLiZhuKuan.getText().toString().trim());
                map.put("chang", lowLiZhuChang.getText().toString().trim());
            } else {
                map.put("kuan", "");
                map.put("chang", "");
            }

            if (llTaoXingZhu.getVisibility() == View.VISIBLE)
                map.put("chicun", getSelected(choiceChiCun, chiCunArr));
            else
                map.put("chicun", "");

            map.put("bihou", lowBiHou.getText().toString().trim());
            map.put("gaodu", lowHeight.getText().toString().trim());
            map.put("lizhuzhongliang", lowWeightLiZhu.getText().toString().trim());

        } else {
            map.put("lizhuxingzhuang", "");
            map.put("zhijing", "");
            map.put("kuan", "");
            map.put("chang", "");
            map.put("chicun", "");
            map.put("bihou", "");
            map.put("gaodu", "");
            map.put("lizhuzhongliang", "");
        }

        progress.show();
        if (action == Const.REQUEST_CODE_RELEASE_STOCK) {
            new Thread(new UpdateStringRun(thumbPictures)).start();
        } else {
            if (selectedPicture.size() > 0) {
                new Thread(new UpdateStringRun(thumbPictures)).start();
            } else {
                new Thread(submitRun).start();
            }
        }

    }



    // 如果不是切割的upLoadBitmap就很大
    public class UpdateStringRun implements Runnable {
        private ArrayList<String> thumbPictures;

        // thumbPictures 是 List<压缩图路径>
        public UpdateStringRun(ArrayList<String> thumbPictures) {
            this.thumbPictures = new ArrayList<String>();
            for (String str :thumbPictures) {
                if (!str.equals(""+ BasePhotoGridActivity.PICTURE_UPDATE_ICON)) {
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


    private String imags(List<ImageResponse.Data> data){
        String str="";
        for(int i=0;i<data.size();i++){
            str+=data.get(i).getImg_id();
            if(i<(data.size()-1)){
                str+=",";
            }
        }
        return str;
    }


    //添加现货服务接口
    private Runnable serviceAddRun = new Runnable() {
        private BaseResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("g", "api");
                data.put("m", "service");
                data.put("a", "add");



                data.put("uid", getUserID());
                data.put("type", Const.SERVICE_TYPE_STOCK + "");
                //data.put("product_type",productType);
                data.put("product_id", "104");
                data.put("imgs", imags(imgResponse.getData()));
                data.put("spec", new JSONArray().put(new JSONObject(map)).toString());
                LogHelper.e("SpecJson", new JSONArray().put(new JSONObject(map)).toString());
                Thread.interrupted();
                data.put("remark", et_remark.getText().toString());
                //response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICEADD));
                response = JsonParser.getBaseResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(data)));
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


    //发布询价接口
    private Runnable submitRun = new Runnable() {
        private BaseResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                map.put("uid", getUserID());
                map.put("remark", et_remark.getText().toString().trim());
                map.put("expiry_date", expiry_datatime + "");
                //map.put("product_type", productType);
                map.put("product_id", "104");
                map.put("user_ids", user_ids);
                map.put("type", type);

                if (imgResponse != null)
                    map.put("imgs", imags(imgResponse.getData()));

                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(map), Url.INQUIRY_ADD));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler2.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler2.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };



    //添加现货服务handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDestroyed())
                return;

            switch (msg.what) {
                case 3://上传图片
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        if (action == Const.REQUEST_CODE_RELEASE_STOCK)
                            new Thread(serviceAddRun).start();
                        else
                            new Thread(submitRun).start();
                    } else {
                        progress.dismiss();
                        showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT://添加服务
                    progress.dismiss();
                    BaseResponse listResponse = (BaseResponse) msg.obj;
                    if(listResponse.isSuccess()){
                        //showErrorToast("添加服务成功");
                        start_Activity(context, AddServiceCompleteActivity.class, new BasicNameValuePair(Const.KEY_SERVICE_TYPE, Const.SERVICE_TYPE_STOCK + ""));
                        CommonApi.addLiveness(getUserID(), 19);
                        finish();
                    }else{
                        showErrorToast(listResponse.getError_desc());
                    }
                    break;
                default:
                    progress.dismiss();
                    showFailTips(getResources().getString(R.string.requst_fail));
                    break;
            }
        }
    };



    //发布询价handler
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDestroyed())
                return;

            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://发布
                    progress.dismiss();
                    BaseResponse baseResponse = (BaseResponse) msg.obj;
                    if (baseResponse.isSuccess()) {
                        start_Activity(context, InquiryCompleteActivity.class);
                        CommonApi.addLiveness(getUserID(), 5);
                        finish();
                        /*if (identity.equals("3")||identity.equals("4")) {
                            ActivityTaskManager.getInstance().removeActivity(ChoiceActivity.class);
                        }*/
                    } else {
                        showErrorToast(baseResponse.getError_desc());
                    }

                    break;
                case NETWORK_FAIL:
                    progress.dismiss();
                    showFailTips(getResources().getString(R.string.requst_fail));
                    break;
                default:
                    progress.dismiss();
                    showFailTips(getResources().getString(R.string.requst_fail));
                    break;
            }
        }
    };






    /**
     * 判断所有数据有效性
     */
    private boolean isDataOK() {
        if (!checkInputRequired(lowSuHou, "塑后丝径", "2.5", "8")) return false;
        if (!checkInput(lowSuQian, "塑前丝径", "2", "6")) return false;
        if (!checkInputRequired(lowKongKuan, "网孔宽度", "5", "20")) return false;
        if (!checkInputRequired(lowKongChang, "网孔长度", "5", "30")) return false;
        if (!checkInputRequired(lowPianKuan, "网片宽度", "0.1", "10")) return false;
        if (!checkInputRequired(lowPianChang, "网片长度", "0.1", "10")) return false;
        if (!checkInput(lowWeightWangPian, "网片重量", "1", "100")) return false;
        if (!checkInput(etNumber, "数量", "1", "1000")) return false;

        if (chkKuangJia.isChecked()) {
            if (!checkInput(lowMianKuan, "管横截面宽", "1", "10")) return false;
            if (!checkInput(lowMianChang, "管横截面长", "1", "10")) return false;
            if (!checkInput(lowGuanHou, "管厚度", "0.1", "10")) return false;
        }

        if (chkLiZhu.isChecked()) {
            if (llFangZhu.getVisibility() == View.VISIBLE) {
                if (!checkInput(lowLiZhuKuan, "立柱宽度", "1", "20")) return false;
                if (!checkInput(lowLiZhuChang, "立柱长度", "1", "20")) return false;
            }
            if (!checkInput(lowBiHou, "立柱壁厚", "0.1", "10")) return false;
            if (!checkInput(lowHeight, "立柱高度", "0.1", "10")) return false;
            if (!checkInput(lowWeightLiZhu, "立柱重量", "0.1", "100")) return false;
        }

        if (action == Const.REQUEST_CODE_RELEASE_STOCK) {
            if (selectedPicture.size() == 0) {
                showErrorToast("请上传产品图片");
                return false;
            }
        } else {
            if (identity.equals("2") && user_ids.equals("0")) {
                showErrorToast("请选择符合厂家");
                return false;
            }
        }

        return true;
    }


    /**
     * 判断输入值是否合理(必填项)
     */
    private boolean checkInputRequired(EditText editText, String name, String min, String max) {
        String value = editText.getText().toString().trim();
        if (TextUtils.isEmpty(value)) {
            showErrorToast("请输入" + name);
            return false;
        }
        if (Float.valueOf(value) < Float.valueOf(min) || Float.valueOf(value) > Float.valueOf(max)) {
            showErrorToast(name + "的范围为" + min + " - " + max);
            return false;
        }
        return true;
    }


    /**
     * 判断输入值是否合理(选填项)
     */
    private boolean checkInput(EditText editText, String name, String min, String max) {
        String value = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(value)) {
            if (Float.valueOf(value) < Float.valueOf(min) || Float.valueOf(value) > Float.valueOf(max)) {
                showErrorToast(name + "的范围为" + min + " - " + max);
                return false;
            }
        }
        return true;
    }


    /**
     * 获取用户的选项
     * @param gridView
     * @param arr
     * @return
     */
    private String getSelected(GridView gridView, String[] arr) {
        String result = "";
        for (int i = 0; i < arr.length; i++) {
            LinearLayout linearLayout = (LinearLayout) gridView.getChildAt(i);
            if (((CheckBox)linearLayout.getChildAt(0)).isChecked()) {
                result = arr[i];
                break;
            }
        }
        return result;
    }


    /**
     * GridView点击事件
     */
    private class MyItemClickImpl implements AdapterView.OnItemClickListener {

        private SpecChoiceAdapter adapter;

        public MyItemClickImpl(SpecChoiceAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            adapter.changeSelected(position);
        }
    }


    @OnClick({R.id.tv_next, R.id.ll_fit, R.id.tv_info_length})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_next:
                submit();
                break;
            case R.id.ll_fit://符合厂家
                startForResult_Activity(this, SelectCompanyActivity.class, 11, new BasicNameValuePair("type", type), new BasicNameValuePair("product_id", "104"), new BasicNameValuePair("product_type", ""));
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        List<Enterprise> enterpriseList;
        switch (requestCode) {
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



    @OnCheckedChanged({R.id.chkKuangJia, R.id.chkLiZhu})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.chkKuangJia:
                if (isChecked)
                    llSpecKuangJia.setVisibility(View.VISIBLE);
                else
                    llSpecKuangJia.setVisibility(View.GONE);
                break;
            case R.id.chkLiZhu:
                if (isChecked)
                    llSpecLiZhu.setVisibility(View.VISIBLE);
                else
                    llSpecLiZhu.setVisibility(View.GONE);
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
        params.put("product_id", "104");

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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




}
