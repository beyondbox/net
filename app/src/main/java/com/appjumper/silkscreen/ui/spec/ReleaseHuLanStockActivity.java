package com.appjumper.silkscreen.ui.spec;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.my.adapter.SpecChoiceAdapter;
import com.appjumper.silkscreen.ui.my.enterprise.AddServiceCompleteActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.LogHelper;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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

import static com.appjumper.silkscreen.R.id.chkKuangJia;
import static com.appjumper.silkscreen.R.id.chkLiZhu;
import static com.appjumper.silkscreen.R.id.choiceChiCun;
import static com.appjumper.silkscreen.R.id.choiceXZGuan;
import static com.appjumper.silkscreen.R.id.choiceXZLiZhu;
import static com.appjumper.silkscreen.R.id.choiceZhiJing;
import static com.appjumper.silkscreen.R.id.etNumber;
import static com.appjumper.silkscreen.R.id.llFangZhu;
import static com.appjumper.silkscreen.R.id.llSpecKuangJia;
import static com.appjumper.silkscreen.R.id.llSpecLiZhu;
import static com.appjumper.silkscreen.R.id.llTaoXingZhu;
import static com.appjumper.silkscreen.R.id.llYuanZhu;
import static com.appjumper.silkscreen.R.id.lowBiHou;
import static com.appjumper.silkscreen.R.id.lowGuanHou;
import static com.appjumper.silkscreen.R.id.lowHeight;
import static com.appjumper.silkscreen.R.id.lowKongChang;
import static com.appjumper.silkscreen.R.id.lowKongKuan;
import static com.appjumper.silkscreen.R.id.lowLiZhuChang;
import static com.appjumper.silkscreen.R.id.lowLiZhuKuan;
import static com.appjumper.silkscreen.R.id.lowMianChang;
import static com.appjumper.silkscreen.R.id.lowMianKuan;
import static com.appjumper.silkscreen.R.id.lowPianChang;
import static com.appjumper.silkscreen.R.id.lowPianKuan;
import static com.appjumper.silkscreen.R.id.lowSuHou;
import static com.appjumper.silkscreen.R.id.lowSuQian;
import static com.appjumper.silkscreen.R.id.lowWeightLiZhu;
import static com.appjumper.silkscreen.R.id.lowWeightWangPian;
import static com.appjumper.silkscreen.R.id.map;
import static com.appjumper.silkscreen.R.id.view;

/**
 * 护栏网添加现货
 * Created by Botx on 2017/5/23.
 */

public class ReleaseHuLanStockActivity extends BasePhotoGridActivity {

    @Bind(R.id.ll_specification)
    LinearLayout llSpecification;
    @Bind(R.id.et_remark)
    EditText et_remark;


    private String [] xzGuanArr = {"方管", "圆管"};
    private String [] xzLiZhuArr = {"圆柱", "方柱", "桃形柱"};
    private String [] zhiJingArr = {"48", "60", "75", "其它"};
    private String [] chiCunArr = {"5*10", "7*10", "7*15", "其它"};

    private ImageResponse imgResponse;

    private List<String> specJsonList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specifiaction_stock);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initTitle("护栏网现货");
        initView();
        addSpecView();
        initBack();
        initProgressDialog(false, "正在发布...");
    }


    /**
     * 添加一个规格视图
     */
    private void addSpecView() {
        final View specView = LayoutInflater.from(context).inflate(R.layout.item_recycler_line_spec_stock, null);
        ImageView imgViClose = (ImageView) specView.findViewById(R.id.imgViClose);
        LinearLayout parentLayout = (LinearLayout) specView.findViewById(R.id.ll_specification);

        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_spec_hulan_stock, null);
        ViewHolder vh = new ViewHolder();
        ButterKnife.bind(vh, contentView);
        initData(vh);
        contentView.setTag(vh);
        parentLayout.addView(contentView);

        if (llSpecification.getChildCount() > 0) {
            imgViClose.setVisibility(View.VISIBLE);
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


    private void initData(final ViewHolder vh) {
        SpecChoiceAdapter xzGuanAdapter = new SpecChoiceAdapter(context, Arrays.asList(xzGuanArr));
        xzGuanAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        vh.choiceXZGuan.setAdapter(xzGuanAdapter);
        vh.choiceXZGuan.setOnItemClickListener(new MyItemClickImpl(xzGuanAdapter));

        SpecChoiceAdapter zhiJingAdapter = new SpecChoiceAdapter(context, Arrays.asList(zhiJingArr));
        zhiJingAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        vh.choiceZhiJing.setAdapter(zhiJingAdapter);
        vh.choiceZhiJing.setOnItemClickListener(new MyItemClickImpl(zhiJingAdapter));

        SpecChoiceAdapter chiCunAdapter = new SpecChoiceAdapter(context, Arrays.asList(chiCunArr));
        chiCunAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        vh.choiceChiCun.setAdapter(chiCunAdapter);
        vh.choiceChiCun.setOnItemClickListener(new MyItemClickImpl(chiCunAdapter));

        final SpecChoiceAdapter xzLiZhuAdapter = new SpecChoiceAdapter(context, Arrays.asList(xzLiZhuArr));
        xzLiZhuAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        vh.choiceXZLiZhu.setAdapter(xzLiZhuAdapter);
        vh.choiceXZLiZhu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                xzLiZhuAdapter.changeSelected(position);
                switch (position) {
                    case 0:
                        vh.llYuanZhu.setVisibility(View.VISIBLE);
                        vh.llFangZhu.setVisibility(View.GONE);
                        vh.llTaoXingZhu.setVisibility(View.GONE);
                        break;
                    case 1:
                        vh.llFangZhu.setVisibility(View.VISIBLE);
                        vh.llYuanZhu.setVisibility(View.GONE);
                        vh.llTaoXingZhu.setVisibility(View.GONE);
                        break;
                    case 2:
                        vh.llTaoXingZhu.setVisibility(View.VISIBLE);
                        vh.llYuanZhu.setVisibility(View.GONE);
                        vh.llFangZhu.setVisibility(View.GONE);
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

        vh.llFangZhu.setVisibility(View.GONE);
        vh.llTaoXingZhu.setVisibility(View.GONE);
        vh.llSpecLiZhu.setVisibility(View.GONE);
        vh.llSpecKuangJia.setVisibility(View.GONE);


        vh.chkKuangJia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    vh.llSpecKuangJia.setVisibility(View.VISIBLE);
                else
                    vh.llSpecKuangJia.setVisibility(View.GONE);
            }
        });

        vh.chkLiZhu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    vh.llSpecLiZhu.setVisibility(View.VISIBLE);
                else
                    vh.llSpecLiZhu.setVisibility(View.GONE);
            }
        });
    }




    /**
     * 发布
     */
    private void submit() {
        int specCount = llSpecification.getChildCount();

        /*
         * 检验数据有效性
         */
        for (int i = 0; i < specCount; i++) {
            View contentView = llSpecification.getChildAt(i).findViewById(R.id.llSpecHulan);
            ViewHolder vh = (ViewHolder) contentView.getTag();

            String specName = "";
            if (specCount > 1)
                specName = "规格" + (i + 1);

            if (!isDataOK(vh, specName))
                return;
        }

        if (selectedPicture.size() == 0) {
            showErrorToast("请上传产品图片");
            return;
        }


        /*
         * 提取数据
         */
        specJsonList.clear();
        for (int i = 0; i < specCount; i++) {
            View contentView = llSpecification.getChildAt(i).findViewById(R.id.llSpecHulan);
            ViewHolder vh = (ViewHolder) contentView.getTag();

            Map<String, String> map = new HashMap<>();
            map.put("suhousijing", vh.lowSuHou.getText().toString().trim());
            map.put("suqiansijing", vh.lowSuQian.getText().toString().trim());
            map.put("wangkongkuan", vh.lowKongKuan.getText().toString().trim());
            map.put("wangkongchang", vh.lowKongChang.getText().toString().trim());
            map.put("wangpiankuan", vh.lowPianKuan.getText().toString().trim());
            map.put("wangpianchang", vh.lowPianChang.getText().toString().trim());
            map.put("wangpianzhongliang", vh.lowWeightWangPian.getText().toString().trim());
            map.put("cunliang", vh.etNumber.getText().toString().trim());

            if (vh.chkKuangJia.isChecked()) {
                map.put("guanxingzhuang", getSelected(vh.choiceXZGuan, xzGuanArr));
                map.put("guanhengjiemiankuan", vh.lowMianKuan.getText().toString().trim());
                map.put("guanhengjiemianchang", vh.lowMianChang.getText().toString().trim());
                map.put("guanghoudu", vh.lowGuanHou.getText().toString().trim());
            } else {
                map.put("guanxingzhuang", "");
                map.put("guanhengjiemiankuan", "");
                map.put("guanhengjiemianchang", "");
                map.put("guanghoudu", "");
            }

            if (vh.chkLiZhu.isChecked()) {
                map.put("lizhuxingzhuang", getSelected(vh.choiceXZLiZhu, xzLiZhuArr));

                if (vh.llYuanZhu.getVisibility() == View.VISIBLE)
                    map.put("zhijing", getSelected(vh.choiceZhiJing, zhiJingArr));
                else
                    map.put("zhijing", "");

                if (vh.llFangZhu.getVisibility() == View.VISIBLE) {
                    map.put("kuan", vh.lowLiZhuKuan.getText().toString().trim());
                    map.put("chang", vh.lowLiZhuChang.getText().toString().trim());
                } else {
                    map.put("kuan", "");
                    map.put("chang", "");
                }

                if (vh.llTaoXingZhu.getVisibility() == View.VISIBLE)
                    map.put("chicun", getSelected(vh.choiceChiCun, chiCunArr));
                else
                    map.put("chicun", "");

                map.put("bihou", vh.lowBiHou.getText().toString().trim());
                map.put("gaodu", vh.lowHeight.getText().toString().trim());
                map.put("lizhuzhongliang", vh.lowWeightLiZhu.getText().toString().trim());

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

            JSONArray jsonArr = new JSONArray();
            jsonArr.put(new JSONObject(map));
            LogHelper.e("SpecJson", jsonArr.toString());
            specJsonList.add(jsonArr.toString());
        }


        progress.show();
        new Thread(new UpdateStringRun(thumbPictures)).start();
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
                        new Thread(serviceAddRun).start();
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





    /**
     * 判断数据有效性
     */
    private boolean isDataOK(ViewHolder vh, String specName) {
        if (!checkInputRequired(specName, vh.lowSuHou, "塑后丝径", "2.5", "8")) return false;
        if (!checkInput(specName, vh.lowSuQian, "塑前丝径", "2", "6")) return false;
        if (!checkInputRequired(specName, vh.lowKongKuan, "网孔宽度", "5", "20")) return false;
        if (!checkInputRequired(specName, vh.lowKongChang, "网孔长度", "5", "30")) return false;
        if (!checkInputRequired(specName, vh.lowPianKuan, "网片宽度", "0.1", "10")) return false;
        if (!checkInputRequired(specName, vh.lowPianChang, "网片长度", "0.1", "10")) return false;
        if (!checkInput(specName, vh.lowWeightWangPian, "网片重量", "1", "100")) return false;
        if (!checkInput(specName, vh.etNumber, "数量", "1", "1000")) return false;

        if (vh.chkKuangJia.isChecked()) {
            if (!checkInput(specName, vh.lowMianKuan, "管横截面宽", "1", "10")) return false;
            if (!checkInput(specName, vh.lowMianChang, "管横截面长", "1", "10")) return false;
            if (!checkInput(specName, vh.lowGuanHou, "管厚度", "0.1", "10")) return false;
        }

        if (vh.chkLiZhu.isChecked()) {
            if (vh.llFangZhu.getVisibility() == View.VISIBLE) {
                if (!checkInput(specName, vh.lowLiZhuKuan, "立柱宽度", "1", "20")) return false;
                if (!checkInput(specName, vh.lowLiZhuChang, "立柱长度", "1", "20")) return false;
            }
            if (!checkInput(specName, vh.lowBiHou, "立柱壁厚", "0.1", "10")) return false;
            if (!checkInput(specName, vh.lowHeight, "立柱高度", "0.1", "10")) return false;
            if (!checkInput(specName, vh.lowWeightLiZhu, "立柱重量", "0.1", "100")) return false;
        }

        return true;
    }



    /**
     * 判断输入值是否合理(必填项)
     */
    private boolean checkInputRequired(String specName, EditText editText, String name, String min, String max) {
        String value = editText.getText().toString().trim();
        if (TextUtils.isEmpty(value)) {
            showErrorToast(specName + "请输入" + name);
            return false;
        }
        if (Float.valueOf(value) < Float.valueOf(min) || Float.valueOf(value) > Float.valueOf(max)) {
            showErrorToast(specName + name + "的范围为" + min + " - " + max);
            return false;
        }
        return true;
    }


    /**
     * 判断输入值是否合理(选填项)
     */
    private boolean checkInput(String specName, EditText editText, String name, String min, String max) {
        String value = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(value)) {
            if (Float.valueOf(value) < Float.valueOf(min) || Float.valueOf(value) > Float.valueOf(max)) {
                showErrorToast(specName + name + "的范围为" + min + " - " + max);
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



    @OnClick({R.id.tv_confirm, R.id.txtAddSpec})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm: //发布
                submit();
                break;
            case R.id.txtAddSpec: //添加规格
                addSpecView();
                refreshSpecName();
                break;
            default:
                break;
        }
    }



    class ViewHolder {
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
    }

}
