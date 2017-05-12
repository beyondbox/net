package com.appjumper.silkscreen.ui.my.enterprise;

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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.my.adapter.SpecChoiceAdapter;
import com.appjumper.silkscreen.ui.my.adapter.SpecStockListAdapter;
import com.appjumper.silkscreen.util.Const;
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
 * Created by Administrator on 2016-11-22.
 */
public class SpecificationStockActivity extends BasePhotoGridActivity {
    @Bind(R.id.ll_specification)//规格布局
            LinearLayout llSpecification;

    @Bind(R.id.et_remark)//描述
            EditText et_remark;
    @Bind(R.id.lvSpec)
    ListView lvSpec;

    @Bind(R.id.txtSpecNumber)
    TextView txtSpecNumber;

    private ServiceProduct service;
    private List<Spec> list;
    private String type;
    private String productType;
    private ImageResponse imgResponse;
    private String json;

    private List<String> specList;
    private SpecStockListAdapter specAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_specifiaction_stock);
        initBack();
        initView();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        productType = intent.getStringExtra("productType");
        service = (ServiceProduct)intent.getSerializableExtra("service");
        list = service.getProduct_spec();
        initTitle(service.getName());

        initViewSpecification(llSpecification);
        initListView();
    }


    private void initListView() {
        specList = new ArrayList<>();

        specAdapter = new SpecStockListAdapter(context, specList);
        specAdapter.setOnCreateItemViewListener(new SpecStockListAdapter.OnCreateItemViewListener() {
            @Override
            public View onCreateView() {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_recycler_line_spec_stock, null);
                LinearLayout specLayout = (LinearLayout) itemView.findViewById(R.id.ll_specification);
                initViewSpecification(specLayout);
                return itemView;
            }
        });
        specAdapter.setOnWhichClickListener(new MyBaseAdapter.OnWhichClickListener() {
            @Override
            public void onWhichClick(View view, int position, int tag) {
                switch (tag) {
                    case SpecStockListAdapter.TAG_CLOSE:
                        specList.remove(position);
                        specAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        });

        lvSpec.setAdapter(specAdapter);
    }


    /**
     * 初始化规格
     */
    private void initViewSpecification(LinearLayout parentLayout) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFieldinput().equals("radio")) {
                initChoiceData(parentLayout, i);


                /*View choiceView = LayoutInflater.from(this).inflate(R.layout.layout_specification_choice, null);
                llSpecification.addView(choiceView);
                choiceView.setTag("choice" + i);
                TextView tvName = (TextView) choiceView.findViewById(R.id.tv_name);//规格名字
                tvName.setText(list.get(i).getName());
                final int finalI = i;
                choiceView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SpecificationActivity.this, MultipleChoiceActivity.class);
                        intent.putExtra("title", list.get(finalI).getName());
                        intent.putExtra("list", list.get(finalI).getUnit());
                        intent.putExtra("fieldname", list.get(finalI).getFieldname());
                        startActivityForResult(intent, 10);
                        overridePendingTransition(R.anim.push_left_in,
                                R.anim.push_left_out);
                    }
                });
                TextView tvMaterial = (TextView) choiceView.findViewById(R.id.tv_material);//材质
                tvMaterial.setHint("请选择" + list.get(i).getName());
                tvMaterial.setTag("material" + i);*/
            } else {
                if(type.equals("3")){
                    View editView = LayoutInflater.from(this).inflate(R.layout.layout_specification_edit_one, null);
                    parentLayout.addView(editView);
                    editView.setTag("edit" + i);
                    EditText etLow = (EditText) editView.findViewById(R.id.et_low);//输入
                    TextView tvName = (TextView) editView.findViewById(R.id.tv_name);//规格名字
                    TextView tvUnit = (TextView) editView.findViewById(R.id.tv_unit);//单位
                    String datatype = list.get(i).getDatatype();
                    switch (datatype){
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
                            etLow .setImeOptions(EditorInfo.IME_ACTION_DONE);
                            break;
                        case "number*number":// '数字*数字  1*1',
                            etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            break;
                        default:

                            break;
                    }
                    tvName.setText(list.get(i).getName());
                    etLow.setTag(i + "low");
                    tvUnit.setText(list.get(i).getUnit());
                }else{
                    if(!list.get(i).getFieldname().equals("cunliang")){
                        View editView = LayoutInflater.from(this).inflate(R.layout.layout_specification_edit, null);
                        parentLayout.addView(editView);
                        editView.setTag("edit" + i);
                        EditText etLow = (EditText) editView.findViewById(R.id.et_low);//范围低值
                        View v_horizontal_line = (View) editView.findViewById(R.id.v_horizontal_line);//横线
                        TextView tv_take = (TextView) editView.findViewById(R.id.tv_take);//*号
                        EditText etHigh = (EditText) editView.findViewById(R.id.et_high);//范围高值
                        TextView tvName = (TextView) editView.findViewById(R.id.tv_name);//规格名字
                        TextView tvUnit = (TextView) editView.findViewById(R.id.tv_unit);//单位
                        String datatype = list.get(i).getDatatype();
                        switch (datatype){
                            case "integer"://'数字  1 或者 1-1',
                                etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                etHigh.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                v_horizontal_line.setVisibility(View.VISIBLE);
                                tv_take.setVisibility(View.GONE);
                                break;
                            case "currency":// '货币 1.00',
                                etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                etHigh.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                v_horizontal_line.setVisibility(View.VISIBLE);
                                tv_take.setVisibility(View.GONE);
                                break;
                            case "require":// '文本',
                                etLow.setInputType(InputType.TYPE_CLASS_TEXT);
                                etHigh.setInputType(InputType.TYPE_CLASS_TEXT);
                                v_horizontal_line.setVisibility(View.VISIBLE);
                                tv_take.setVisibility(View.GONE);
                                break;
                            case "english"://'英文字母',
                                etLow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                etLow .setImeOptions(EditorInfo.IME_ACTION_DONE);
                                etHigh.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                etHigh .setImeOptions(EditorInfo.IME_ACTION_DONE);
                                v_horizontal_line.setVisibility(View.VISIBLE);
                                tv_take.setVisibility(View.GONE);
                                break;
                            case "number*number":// '数字*数字  1*1',
                                etLow.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                etHigh.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                v_horizontal_line.setVisibility(View.GONE);
                                tv_take.setVisibility(View.VISIBLE);
                                break;
                            default:
                                break;
                        }
                        tvName.setText(list.get(i).getName());
                        etLow.setTag(i + "low");
                        etHigh.setTag(i + "high");
                        tvUnit.setText(list.get(i).getUnit());
                    }
                }
            }
        }
    }


    /**
     * 渲染多选选项视图和数据
     * @param i
     */
    private void initChoiceData(LinearLayout parentLayout, int i) {
        View choiceView = LayoutInflater.from(context).inflate(R.layout.layout_specification_option, null);
        parentLayout.addView(choiceView);
        choiceView.setTag("choice" + i);

        TextView tvName = (TextView) choiceView.findViewById(R.id.tv_name);//规格名字
        tvName.setText(list.get(i).getName());

        GridView gridChoice = (GridView) choiceView.findViewById(R.id.gridChoice);
        String [] arr = list.get(i).getUnit().split(",");
        final SpecChoiceAdapter choiceAdapter = new SpecChoiceAdapter(context, Arrays.asList(arr));
        gridChoice.setAdapter(choiceAdapter);
    }



    @OnClick({R.id.tv_confirm, R.id.txtAddSpec})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm://确定
                JSONArray jsonArray = new JSONArray();
                List<Map<String,String>> listval = new ArrayList<>();
                JSONObject jsonObject = new JSONObject();
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
                        jsonObject.put(list.get(i).getFieldname(), checkedResult);



                        /*View choiceView = llSpecification.findViewWithTag("choice" + i);
                        TextView tvMaterial = (TextView) choiceView.findViewWithTag("material" + i);
                        if (tvMaterial.getText().toString().length() < 1) {
                            showErrorToast("请选择" + list.get(i).getName());
                            return;
                        }
                        jsonObject.put(list.get(i).getFieldname(),tvMaterial.getText().toString());*/
                    } else {
                        if(type.equals("3")){
                            View editView = llSpecification.findViewWithTag("edit" + i);
                            EditText etLow = (EditText) editView.findViewWithTag(i + "low");
                            if (etLow.getText().toString().trim().length() < 1) {
                                showErrorToast("请输入" + list.get(i));
                                return;
                            }
                            jsonObject.put(list.get(i).getFieldname(),etLow.getText().toString());
                        }else{
                            if(!list.get(i).getFieldname().equals("cunliang")){
                                View editView = llSpecification.findViewWithTag("edit" + i);
                                EditText etLow = (EditText) editView.findViewWithTag(i + "low");
                                EditText etHigh = (EditText) editView.findViewWithTag(i + "high");
                                if (etLow.getText().toString().trim().length() < 1) {
                                    showErrorToast("请输入" + list.get(i).getName() + "最小值");
                                    return;
                                }
                                if (etHigh.getText().toString().trim().length() < 1) {
                                    showErrorToast("请输入" + list.get(i).getName() + "最大值");
                                    return;
                                }
                                if(list.get(i).getDatatype().equals("number*number")){
                                    jsonObject.put(list.get(i).getFieldname(),etLow.getText().toString().trim() + "*" + etHigh.getText().toString().trim());
                                }else{
                                    jsonObject.put(list.get(i).getFieldname(),etLow.getText().toString().trim() + "-" + etHigh.getText().toString().trim());
                                }
                            }
                        }

                    }
                }
                jsonArray.add(jsonObject);
                json = jsonArray.toJSONString();
                Log.e("Log",jsonArray.toJSONString()+"-----");
                initProgressDialog();
                progress.show();
                progress.setMessage("正在添加服务...");
                new Thread(new UpdateStringRun(thumbPictures)).start();

                break;

            case R.id.txtAddSpec: //添加规格
                specList.add("1");
                specAdapter.notifyDataSetChanged();
                txtSpecNumber.setText("规格1");
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
            SpecificationStockActivity activity = (SpecificationStockActivity) reference.get();
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
                    activity.showErrorToast();
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
                data.put("uid", getUserID());
                data.put("type", type);
                //data.put("product_type",productType);
                data.put("product_id", service.getId());
                data.put("imgs", imags(imgResponse.getData()));
                data.put("spec", json);
                data.put("remark", et_remark.getText().toString());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.SERVICEADD));
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
