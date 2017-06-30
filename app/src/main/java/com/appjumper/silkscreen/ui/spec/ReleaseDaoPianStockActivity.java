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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
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
 * 刀片刺绳添加现货服务
 * Created by Botx on 2017-6-20.
 */
public class ReleaseDaoPianStockActivity extends BasePhotoGridActivity {
    @Bind(R.id.ll_specification)//规格布局
            LinearLayout llSpecification;

    @Bind(R.id.et_remark)//描述
            EditText et_remark;

    private ServiceProduct service;
    private List<Spec> list;
    private String type;
    private String productType;
    private ImageResponse imgResponse;

    private List<List<DPChild>> childList;
    private ReleaseDaoPianAdapter adapter;
    private List<String> specJsonList = new ArrayList<>();

    private List<DPChild> childList0;
    private List<DPChild> childList1;
    private List<DPChild> childList2;
    private List<DPChild> childList3;
    private List<DPChild> childList4;

    private String [] nameArr = {"guige", "xingzhuang", "banhou", "sijing", "daochang", "daokuan", "daojian"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_daopian_stock);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        productType = intent.getStringExtra("productType");
        service = (ServiceProduct)intent.getSerializableExtra("service");
        list = service.getProduct_spec();

        initTitle(service.getName());
        initBack();
        initView();
        initChildList();
        initProgressDialog(false, "正在添加服务...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addSpecView();
                et_remark.setHint(service.getRemark());
            }
        }, 50);

    }



    /**
     * 初始化特定规格
     */
    private void initChildList() {
        childList = new ArrayList<>();

        childList0 = new ArrayList<>();
        childList1 = new ArrayList<>();
        childList2 = new ArrayList<>();
        childList3 = new ArrayList<>();
        childList4 = new ArrayList<>();

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
    }



    /**
     * 添加一个规格视图
     */
    private void addSpecView() {
        final View specView = LayoutInflater.from(context).inflate(R.layout.item_recycler_line_spec_stock, null);
        ImageView imgViClose = (ImageView) specView.findViewById(R.id.imgViClose);
        LinearLayout parentLayout = (LinearLayout) specView.findViewById(R.id.ll_specification);

        if (llSpecification.getChildCount() > 0) {
            imgViClose.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < list.size(); i++) {
            String fildType = list.get(i).getFieldinput();
            if (fildType.equals("radio")) {
                initChoiceData(parentLayout, i);
            } else if(fildType.equals("text")) {
                initInputValue(parentLayout, i);
            }
        }

        imgViClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSpecification.removeView(specView);
                refreshSpecName();
            }
        });


        llSpecification.addView(specView);
    }




    /**
     * 渲染单选选项
     * @param i
     */
    private void initChoiceData(LinearLayout parentLayout, int i) {
        Spec spec = list.get(i);

        if (spec.getFieldname().equals("guige")) {
            initGuiGeLayout(parentLayout, i);
            return;
        }

        if (Arrays.asList(nameArr).contains(spec.getFieldname()))
            return;


        View choiceView = LayoutInflater.from(context).inflate(R.layout.layout_spec_choice, null);
        parentLayout.addView(choiceView);
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
    private void initInputValue(LinearLayout parentLayout, int i) {
        Spec spec = list.get(i);

        View editView = LayoutInflater.from(this).inflate(R.layout.layout_spec_input_release, null);
        parentLayout.addView(editView);
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




    /**
     * 渲染特定规格布局
     */
    private void initGuiGeLayout(LinearLayout parentLayout, int position) {
        Map<DPGroup, List<DPChild>> guigeMap = new HashMap<>();
        List<DPGroup> groupList =  new ArrayList<>();

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

        for (int i = 0; i < groupList.size(); i++) {
            guigeMap.put(groupList.get(i), childList.get(i));
        }

        final NoScrollExpandableListView exListView = new NoScrollExpandableListView(this);
        exListView.setGroupIndicator(null);
        exListView.setDividerHeight(0);
        adapter = new ReleaseDaoPianAdapter(this, guigeMap, groupList);
        adapter.setChoiceMode(ReleaseDaoPianAdapter.CHOICE_MODE_SINGLE);
        exListView.setAdapter(adapter);
        exListView.expandGroup(0, true);

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


        parentLayout.addView(exListView);
        parentLayout.setTag(R.id.tag_1, guigeMap);
        parentLayout.setTag(R.id.tag_2, groupList);
    }



    /**
     * 刷新规格名称
     */
    private void refreshSpecName() {
        int count = llSpecification.getChildCount();

        for (int i = 0; i < count; i++) {
            LinearLayout linearLayout = (LinearLayout) llSpecification.getChildAt(i);
            TextView txtSpecName = (TextView) linearLayout.findViewById(R.id.txtSpecName);

            if (i == 0 && count == 1)
                txtSpecName.setText("规格");
            else
                txtSpecName.setText("规格" + (i + 1));
        }
    }



    @OnClick({R.id.tv_confirm, R.id.txtAddSpec})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm: //确定
                specJsonList.clear();
                int specCount = llSpecification.getChildCount();

                for (int k = 0; k < specCount; k++) {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    LinearLayout parentLayout = (LinearLayout) llSpecification.getChildAt(k);
                    String specName = "";
                    if (specCount > 1) {
                        TextView txtSpecName = (TextView) parentLayout.findViewById(R.id.txtSpecName);
                        specName = txtSpecName.getText().toString().trim();
                    }


                    LinearLayout llSpec = (LinearLayout) parentLayout.findViewById(R.id.ll_specification);
                    getSelectedGuiGe(llSpec, jsonObject);

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getFieldinput().equals("radio")) {

                            if (Arrays.asList(nameArr).contains(list.get(i).getFieldname()))
                                continue;

                            View choiceView = parentLayout.findViewWithTag("choice" + i);
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
                                    showErrorToast(specName + "请选择" + list.get(i).getName());
                                    return;
                                }
                            }

                            if (!TextUtils.isEmpty(checkedResult))
                                checkedResult = checkedResult.substring(0, checkedResult.length() - 1);
                            jsonObject.put(list.get(i).getFieldname(), checkedResult);

                        } else if (list.get(i).getFieldinput().equals("text")) {
                            View editView = parentLayout.findViewWithTag("edit" + i);
                            EditText etValue = (EditText) editView.findViewWithTag(i + "value");
                            String value = etValue.getText().toString().trim();

                            if(list.get(i).getRequire().equals("1")) {
                                if (TextUtils.isEmpty(value)) {
                                    showErrorToast(specName + "请输入" + list.get(i).getName());
                                    return;
                                }
                            }

                            String minValue = list.get(i).getMin_value();
                            String maxValue = list.get(i).getMax_value();
                            if (!TextUtils.isEmpty(minValue) || !TextUtils.isEmpty(maxValue)) {
                                if (!TextUtils.isEmpty(value)) {
                                    if (Float.valueOf(value) < Float.valueOf(minValue) || Float.valueOf(value) > Float.valueOf(maxValue)) {
                                        showErrorToast(specName + list.get(i).getName() + "的范围为" + list.get(i).getMin_value() + "-" + list.get(i).getMax_value());
                                        return;
                                    }
                                }
                            }


                            jsonObject.put(list.get(i).getFieldname(), value);

                        }
                    }

                    jsonArray.add(jsonObject);
                    specJsonList.add(jsonArray.toJSONString());
                    Log.e("Log",jsonArray.toJSONString()+"-----");
                }



                if (selectedPicture.size() == 0) {
                    showErrorToast("请上传产品图片");
                    return;
                }
                progress.show();
                new Thread(new UpdateStringRun(thumbPictures)).start();
                break;

            case R.id.txtAddSpec: //添加规格
                addSpecView();
                refreshSpecName();
                break;
            default:
                break;
        }
    }



    /**
     * 取出所选的特定规格
     */
    private void getSelectedGuiGe(LinearLayout llSpec, JSONObject jsonObj) {
        Map<DPGroup, List<DPChild>> guigeMap = (Map<DPGroup, List<DPChild>>) llSpec.getTag(R.id.tag_1);
        List<DPGroup> groupList = (List<DPGroup>) llSpec.getTag(R.id.tag_2);

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
            ReleaseDaoPianStockActivity activity = (ReleaseDaoPianStockActivity) reference.get();
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
                //data.put("product_type",productType);
                data.put("product_id", service.getId());
                data.put("imgs", imags(imgResponse.getData()));
                data.put("remark", et_remark.getText().toString());

                for (int i = 0; i < specJsonList.size(); i++) {
                    data.put("spec" + (i + 1), specJsonList.get(i));
                }
                data.put("spec_num", specJsonList.size() + "");

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
