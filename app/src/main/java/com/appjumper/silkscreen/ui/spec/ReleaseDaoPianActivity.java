package com.appjumper.silkscreen.ui.spec;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.DPChild;
import com.appjumper.silkscreen.bean.DPGroup;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.my.adapter.SpecChoiceAdapter;
import com.appjumper.silkscreen.ui.my.enterprise.AddServiceCompleteActivity;
import com.appjumper.silkscreen.ui.spec.adapter.ReleaseDaoPianAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.NoScrollExpandableListView;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 刀片刺绳添加服务(订做或者加工)
 * Created by Botx on 2017-6-19.
 */

public class ReleaseDaoPianActivity extends BasePhotoGridActivity {
    @Bind(R.id.ll_specification)//规格布局
            LinearLayout llSpecification;

    @Bind(R.id.et_remark)//描述
            EditText et_remark;

    private ServiceProduct service;
    private List<Spec> list;
    private String type;
    private String productType;
    private ImageResponse imgResponse;
    private String json;

    private Map<DPGroup, List<DPChild>> guigeMap;
    private List<DPGroup> groupList;
    private List<List<DPChild>> childList;
    private ReleaseDaoPianAdapter adapter;

    private List<DPChild> childList0;
    private List<DPChild> childList1;
    private List<DPChild> childList2;
    private List<DPChild> childList3;
    private List<DPChild> childList4;

    private String [] nameArr = {"guige", "xingzhuang", "banhou", "sijing", "daochang", "daokuan", "daojian"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_specifiaction);
        initBack();
        initView();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        productType = intent.getStringExtra("productType");
        service = (ServiceProduct)intent.getSerializableExtra("service");
        list = service.getProduct_spec();
        initTitle(service.getName());
        initProgressDialog(false, "正在添加服务...");

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               initViewSpecification();
               et_remark.setHint(service.getRemark());
           }
       }, 50);

    }


    /**
     * 初始化规格
     */
    private void initViewSpecification() {
        for (int i = 0; i < list.size(); i++) {
            String fildType = list.get(i).getFieldinput();

            if (fildType.equals("radio")) {
                initChoiceData(i);
            } else if (fildType.equals("text")) {
                if (!list.get(i).getFieldname().equals("cunliang"))
                    initInputRange(i);
            }
        }
    }


    /**
     * 渲染多选选项
     * @param i
     */
    private void initChoiceData(int i) {
        Spec spec = list.get(i);

        if (spec.getFieldname().equals("guige")) {
            initGuiGeLayout(i);
            return;
        }

        if (Arrays.asList(nameArr).contains(spec.getFieldname()))
            return;


        View choiceView = LayoutInflater.from(context).inflate(R.layout.layout_spec_choice, null);
        llSpecification.addView(choiceView);
        choiceView.setTag("choice" + i);

        TextView txtRequired = (TextView) choiceView.findViewById(R.id.txtRequired);
        txtRequired.setVisibility(spec.getRequire().equals("1") ? View.VISIBLE : View.INVISIBLE);

        TextView txtName = (TextView) choiceView.findViewById(R.id.txtName);//规格名字
        txtName.setText(spec.getName());

        final GridView gridChoice = (GridView) choiceView.findViewById(R.id.gridChoice);
        String [] arr = spec.getUnit().split(",");
        SpecChoiceAdapter choiceAdapter = new SpecChoiceAdapter(context, Arrays.asList(arr));
        gridChoice.setAdapter(choiceAdapter);

        String defaultChoice = spec.getUnit_default();
        if (!TextUtils.isEmpty(defaultChoice)) {
            final int position = Integer.parseInt(defaultChoice);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((CheckBox)((LinearLayout)gridChoice.getChildAt(position)).getChildAt(0)).setChecked(true);
                }
            }, 100);
        }

    }



    /**
     * 渲染输入范围
     * @param i
     */
    private void initInputRange(int i) {
        Spec spec = list.get(i);

        View editView = LayoutInflater.from(this).inflate(R.layout.layout_spec_input_range, null);
        llSpecification.addView(editView);
        editView.setTag("edit" + i);

        TextView txtRequired = (TextView) editView.findViewById(R.id.txtRequired);
        txtRequired.setVisibility(spec.getRequire().equals("1") ? View.VISIBLE : View.INVISIBLE);

        EditText etLow = (EditText) editView.findViewById(R.id.etLow);//范围低值
        EditText etHigh = (EditText) editView.findViewById(R.id.etHigh);//范围高值
        TextView txtName = (TextView) editView.findViewById(R.id.txtName);//规格名字
        txtName.setText(list.get(i).getName());
        etLow.setHint(spec.getMin_value());
        etHigh.setHint(spec.getMax_value());
        etLow.setTag(i + "low");
        etHigh.setTag(i + "high");

        String datatype = list.get(i).getDatatype();
        switch (datatype){
            case "integer"://'数字  1 或者 1-1',
                etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                etHigh.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case "currency":// '货币 1.00',
                etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                etHigh.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case "require":// '文本',
                etLow.setInputType(InputType.TYPE_CLASS_TEXT);
                etHigh.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "english"://'英文字母',
                etLow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                etLow .setImeOptions(EditorInfo.IME_ACTION_DONE);
                etHigh.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                etHigh .setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
            case "number*number":// '数字*数字  1*1',
                etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                etHigh.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            default:
                break;
        }
    }




    /**
     * 渲染特定规格布局
     */
    private void initGuiGeLayout(int position) {
        guigeMap = new HashMap<>();
        groupList =  new ArrayList<>();
        childList = new ArrayList<>();

        childList0 = new ArrayList<>();
        childList1 = new ArrayList<>();
        childList2 = new ArrayList<>();
        childList3 = new ArrayList<>();
        childList4 = new ArrayList<>();

        Spec spec = list.get(position);
        String [] guigeArr = spec.getUnit().split(",");
        for (int i = 0; i < guigeArr.length; i++) {
            DPGroup group = null;
            if (i == 0)
                group = new DPGroup(true, guigeArr[i]);
            else
                group = new DPGroup(false, guigeArr[i]);
            groupList.add(group);
        }


        childList0.add(new DPChild("xingzhuang", "http://115.28.148.207/data/upload/2016-12-21/xingzhuang1.jpg", "形状"));
        childList0.add(new DPChild("banhou", "0.5±0.05", "板厚(mm)"));
        childList0.add(new DPChild("sijing", "2.5±0.1", "丝径(mm)"));
        childList0.add(new DPChild("daochang", "12±1", "刀长(mm)"));
        childList0.add(new DPChild("daokuan", "15±1", "刀宽(mm)"));
        childList0.add(new DPChild("daojian", "26±1", "刀间(mm)"));

        childList1.add(new DPChild("xingzhuang", "http://115.28.148.207/data/upload/2016-12-21/xingzhuang2.jpg", "形状"));
        childList1.add(new DPChild("banhou", "0.5±0.05", "板厚(mm)"));
        childList1.add(new DPChild("sijing", "2.5±0.1", "丝径(mm)"));
        childList1.add(new DPChild("daochang", "22±1", "刀长(mm)"));
        childList1.add(new DPChild("daokuan", "15±1", "刀宽(mm)"));
        childList1.add(new DPChild("daojian", "34±1", "刀间(mm)"));

        childList2.add(new DPChild("xingzhuang", "http://115.28.148.207/data/upload/2016-12-21/xingzhuang3.jpg", "形状"));
        childList2.add(new DPChild("banhou", "0.5±0.05", "板厚(mm)"));
        childList2.add(new DPChild("sijing", "2.5", "丝径(mm)"));
        childList2.add(new DPChild("daochang", "28", "刀长(mm)"));
        childList2.add(new DPChild("daokuan", "15", "刀宽(mm)"));
        childList2.add(new DPChild("daojian", "45±1", "刀间(mm)"));

        childList3.add(new DPChild("xingzhuang", "http://115.28.148.207/data/upload/2016-12-21/xingzhuang4.jpg", "形状"));
        childList3.add(new DPChild("banhou", "0.6±0.05", "板厚(mm)"));
        childList3.add(new DPChild("sijing", "2.5±0.1", "丝径(mm)"));
        childList3.add(new DPChild("daochang", "60±2", "刀长(mm)"));
        childList3.add(new DPChild("daokuan", "32±1", "刀宽(mm)"));
        childList3.add(new DPChild("daojian", "100±2", "刀间(mm)"));

        childList4.add(new DPChild("xingzhuang", "http://115.28.148.207/data/upload/2016-12-21/xingzhuang5.jpg", "形状"));
        childList4.add(new DPChild("banhou", "0.5±0.05", "板厚(mm)"));
        childList4.add(new DPChild("sijing", "2.5±0.1", "丝径(mm)"));
        childList4.add(new DPChild("daochang", "65±2", "刀长(mm)"));
        childList4.add(new DPChild("daokuan", "21±1", "刀宽(mm)"));
        childList4.add(new DPChild("daojian", "100±2", "刀间(mm)"));

        childList.add(childList0);
        childList.add(childList1);
        childList.add(childList2);
        childList.add(childList3);
        childList.add(childList4);

        for (int i = 0; i < groupList.size(); i++) {
            guigeMap.put(groupList.get(i), childList.get(i));
        }

        final NoScrollExpandableListView exListView = new NoScrollExpandableListView(this);
        exListView.setGroupIndicator(null);
        exListView.setDividerHeight(0);
        adapter = new ReleaseDaoPianAdapter(this, guigeMap, groupList);
        exListView.setAdapter(adapter);

        exListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (exListView.isGroupExpanded(groupPosition))
                    exListView.collapseGroup(groupPosition);
                else
                    exListView.expandGroup(groupPosition, true);

                return true;
            }
        });

        exListView.expandGroup(0, true);
        llSpecification.addView(exListView);
    }





    @OnClick({R.id.tv_confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm://确定
                JSONArray jsonArray = new JSONArray();
                List<Map<String,String>> listval = new ArrayList<>();
                JSONObject jsonObject = new JSONObject();

                getSelectedGuiGe(jsonObject);

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getFieldinput().equals("radio")) {

                        if (Arrays.asList(nameArr).contains(list.get(i).getFieldname()))
                            continue;

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
                        jsonObject.put(list.get(i).getFieldname(), checkedResult);

                    } else if (list.get(i).getFieldinput().equals("text")) {
                        if (!list.get(i).getFieldname().equals("cunliang")) {
                            View editView = llSpecification.findViewWithTag("edit" + i);
                            EditText etLow = (EditText) editView.findViewWithTag(i + "low");
                            EditText etHigh = (EditText) editView.findViewWithTag(i + "high");

                            String low = etLow.getText().toString().trim();
                            String high = etHigh.getText().toString().trim();

                            if(list.get(i).getRequire().equals("1")) {
                                if (TextUtils.isEmpty(low)) {
                                    showErrorToast("请输入" + list.get(i).getName() + "最小值");
                                    return;
                                }
                                if (TextUtils.isEmpty(high)) {
                                    showErrorToast("请输入" + list.get(i).getName() + "最大值");
                                    return;
                                }
                            }

                            String minValue = list.get(i).getMin_value();
                            String maxValue = list.get(i).getMax_value();
                            if (!TextUtils.isEmpty(minValue) || !TextUtils.isEmpty(maxValue)) {
                                if (!TextUtils.isEmpty(low) || !TextUtils.isEmpty(high)) {
                                    if (TextUtils.isEmpty(low) || TextUtils.isEmpty(high)) {
                                        showErrorToast(list.get(i).getName() + "的范围为" + list.get(i).getMin_value() + "-" + list.get(i).getMax_value());
                                        return;
                                    } else if (Float.valueOf(low) < Float.valueOf(minValue) || Float.valueOf(high) > Float.valueOf(maxValue)) {
                                        showErrorToast(list.get(i).getName() + "的范围为" + list.get(i).getMin_value() + "-" + list.get(i).getMax_value());
                                        return;
                                    }
                                }
                            }


                            if (!TextUtils.isEmpty(low) && !TextUtils.isEmpty(high)) {
                                if (list.get(i).getDatatype().equals("number*number")) {
                                    jsonObject.put(list.get(i).getFieldname(), low + "*" + high);
                                } else {
                                    jsonObject.put(list.get(i).getFieldname(), low + "-" + high);
                                }
                            } else {
                                jsonObject.put(list.get(i).getFieldname(), "");
                            }

                        }

                    }
                }
                jsonArray.add(jsonObject);
                json = jsonArray.toJSONString();
                Log.e("Log",jsonArray.toJSONString()+"-----");

                if (selectedPicture.size() == 0) {
                    showErrorToast("请上传产品图片");
                    return;
                }
                progress.show();
                new Thread(new UpdateStringRun(thumbPictures)).start();
                break;
            default:
                break;
        }
    }



    /**
     * 取出所选的特定规格
     */
    private void getSelectedGuiGe(JSONObject jsonObj) {
        List<Integer> positionList = new ArrayList<>();
        for (int i = 0; i < groupList.size(); i++) {
            DPGroup group = groupList.get(i);
            if (group.isChecked()) {
                positionList.add(i);
            }
        }


        for (int i = 0; i < nameArr.length; i++) {
            String result = "";

            if (nameArr[i].equals("guige")) {
                for (int position : positionList) {
                    result += groupList.get(position).getName() + ",";
                }
            } else {
                for (int position : positionList) {
                    List<DPChild> childList = guigeMap.get(groupList.get(position));
                    for (DPChild child : childList) {
                        String key = child.getKey();
                        if (key.equals(nameArr[i])) {
                            result += child.getValue() + ",";
                            break;
                        }
                    }
                }
            }

            if (!TextUtils.isEmpty(result))
                result = result.substring(0, result.length() - 1);
            jsonObj.put(nameArr[i], result);
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
            // TODO Auto-generated method stub
            ImageResponse retMap = null;
            try {
                // 如果不是切割的upLoadBitmap就很大,在这里压缩
                retMap = JsonParser.getImageResponse(HttpUtil.upload(thumbPictures,Url.UPLOADIMAGE));
            } catch (Exception e) {
                // TODO Auto-generated catch block XDEBUG_SESSION_START=1
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

    private MyHandler handler = new MyHandler(this);
    private String urls;
    private  class MyHandler extends Handler {
        private WeakReference<Context> reference;


        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            ReleaseDaoPianActivity activity = (ReleaseDaoPianActivity) reference.get();
            if(activity == null){
                return;
            }

            switch (msg.what) {
                case 3://上传图片
                    imgResponse = (ImageResponse) msg.obj;
                    if (imgResponse.isSuccess()) {
                        new Thread(serviceAddRun).start();
                    } else {
                        progress.dismiss();
                        activity.showErrorToast(imgResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT://添加服务
                    progress.dismiss();
                    BaseResponse listResponse = (BaseResponse) msg.obj;
                    if(listResponse.isSuccess()){
                        //showErrorToast("添加服务成功");
                        start_Activity(context, AddServiceCompleteActivity.class, new BasicNameValuePair(Const.KEY_SERVICE_TYPE, type));
                        CommonApi.addLiveness(getUserID(), 19);
                        finish();
                    }else{
                        showErrorToast(listResponse.getError_desc());
                    }
                    break;
                default:
                    progress.dismiss();
                    activity.showFailTips(getResources().getString(R.string.requst_fail));
                    break;
            }
        }
    };

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


    //添加服务接口
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
                data.put("type", type);
                //data.put("product_type", productType);
                data.put("product_id", service.getId());
                data.put("spec1", json);
                data.put("spec_num", "1");
                data.put("remark", et_remark.getText().toString());
                data.put("imgs", imags(imgResponse.getData()));

                //response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICEADD));
                //response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.HOST));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode){
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
        }
    }


}
