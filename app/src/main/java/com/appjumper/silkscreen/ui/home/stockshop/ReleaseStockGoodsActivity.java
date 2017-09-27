package com.appjumper.silkscreen.ui.home.stockshop;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.home.adapter.StockGoodsSpecGridAdapter;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.LogHelper;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发布现货商品
 * Created by Botx on 2017/9/21.
 */

public class ReleaseStockGoodsActivity extends BasePhotoGridActivity {

    @Bind(R.id.llContent)
    LinearLayout llContent;

    GridView gridTexture;
    StockGoodsSpecGridAdapter textureAdapter;

    private String htmlStr = "";
    private String title = "";
    private String coverImg = "";
    private RequestParams params;

    private Product product;
    private String unit;

    private String [] arrCisheng = {"高锌热镀丝", "包塑冷镀丝", "一般冷镀", "普锌热镀", "包塑热镀丝", "其他"};
    private String [] arrJinGangWang = {"304", "316", "201", "碳钢", "不锈钢", "其他"};
    private String [] arrJianZhuWangPian = {"Q195黑铁丝", "其他"};
    private String [] arrDianHanWang = {"热镀锌丝", "改拔丝", "冷镀锌", "其他"};
    private String [] arrHeLanWang = {"浸塑", "其他"};
    private String [] arrHeiTieSi = {"Q195", "其他"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_stock_goods);
        ButterKnife.bind(context);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        product = (Product) getIntent().getSerializableExtra(Const.KEY_OBJECT);

        initTitle(product.getProduct_name());
        initBack();
        initView();
        initProgressDialog(false, "正在发布....");

        initSpecView();
    }


    private void initSpecView() {
        int layoutRes = 0;
        List<String> textureList = new ArrayList<>();

        switch (product.getProduct_id()) {
            case "26": //刺绳
                layoutRes = R.layout.layout_goods_spec_cisheng;
                textureList = Arrays.asList(arrCisheng);
                unit = "kg";
                break;
            case "4": //金刚网
                layoutRes = R.layout.layout_goods_spec_jingangwang;
                textureList = Arrays.asList(arrJinGangWang);
                unit = "平米";
                break;
            case "98": //建筑网片
                layoutRes = R.layout.layout_goods_spec_jianzhuwangpian;
                textureList = Arrays.asList(arrJianZhuWangPian);
                unit = "平米";
                break;
            case "22": //电焊网
                layoutRes = R.layout.layout_goods_spec_dianhanwang;
                textureList = Arrays.asList(arrDianHanWang);
                unit = "卷";
                break;
            case "30": //荷兰网
                layoutRes = R.layout.layout_goods_spec_dianhanwang;
                textureList = Arrays.asList(arrHeLanWang);
                unit = "卷";
                break;
            case "117": //黑铁丝
                layoutRes = R.layout.layout_goods_spec_heitiesi;
                textureList = Arrays.asList(arrHeiTieSi);
                unit = "吨";
                break;
            default:
                break;
        }

        View contentView = getLayoutInflater().inflate(layoutRes, null);
        llContent.addView(contentView);

        gridTexture = (GridView) contentView.findViewById(R.id.gridTexture);
        textureAdapter = new StockGoodsSpecGridAdapter(context, textureList);
        gridTexture.setAdapter(textureAdapter);
        gridTexture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textureAdapter.changeSelected(position);
            }
        });
    }


    /**
     * 生成产品属性html字符串和标题
     */
    private boolean getHtml() {
        htmlStr = "";
        title = "";

        /**
         * 规格
         */
        LinearLayout llSpec = (LinearLayout) llContent.findViewById(R.id.llSpec);
        for (int i = 0; i < llSpec.getChildCount(); i++) {
            LinearLayout layoutParent = (LinearLayout) llSpec.getChildAt(i);
            String key = "";
            String value = "";

            switch ((String)layoutParent.getTag()) {
                case "1": //输入单个值
                    key = ((TextView)layoutParent.getChildAt(0)).getText().toString();
                    EditText edtValue = (EditText) ((ViewGroup)layoutParent.getChildAt(1)).getChildAt(0);
                    String content = edtValue.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        showErrorToast("请输入" + key);
                        return false;
                    } else {
                        value = content;
                    }
                    break;
                case "2": //输入范围值
                    key = ((TextView)layoutParent.getChildAt(0)).getText().toString();
                    EditText edtStart = (EditText) ((ViewGroup)layoutParent.getChildAt(1)).getChildAt(0);
                    String start = edtStart.getText().toString().trim();
                    EditText edtEnd = (EditText) ((ViewGroup)layoutParent.getChildAt(1)).getChildAt(2);
                    String end = edtEnd.getText().toString().trim();
                    if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
                        showErrorToast("请输入" + key);
                        return false;
                    } else {
                        value = start + "*" + end;
                    }
                    break;
                case "3": //单选
                    key = ((TextView)layoutParent.getChildAt(0)).getText().toString();
                    RadioGroup rdoGroup = (RadioGroup) ((ViewGroup)layoutParent.getChildAt(1)).getChildAt(0);
                    boolean hasChecked = false;
                    for (int j = 0; j < rdoGroup.getChildCount(); j++) {
                        RadioButton rdoBtn = (RadioButton) rdoGroup.getChildAt(j);
                        if (rdoBtn.isChecked()) {
                            if (j == rdoGroup.getChildCount() - 1) {
                                EditText edtContent = (EditText) ((ViewGroup)((ViewGroup)layoutParent.getChildAt(1)).getChildAt(1)).getChildAt(0);
                                String text = edtContent.getText().toString().trim();
                                if (TextUtils.isEmpty(text)) {
                                    showErrorToast("请输入" + key + "的内容");
                                    return false;
                                } else {
                                    value = text;
                                }
                            } else {
                                value = rdoBtn.getText().toString();
                            }

                            hasChecked = true;
                            break;
                        }
                    }

                    if (!hasChecked) {
                        showErrorToast("请选择" + key);
                        return false;
                    }
                    break;
                default:
                    break;
            }

            htmlStr += "<p>" + key + "：" + value + "</p>";

            if (!key.contains("重量")) {
                String unit = "";
                Pattern p = Pattern.compile("\\([\\s\\S]+\\)");
                Matcher m = p.matcher(key);
                while (m.find()) {
                    String temp = m.group();
                    unit = temp.substring(1, temp.length() - 1);
                }

                title += value + unit + "+";
            }

        }



        /**
         * 材质
         */
        for (int i = 0; i < textureAdapter.getCount(); i++) {
            TextView chkSpec = (TextView) gridTexture.getChildAt(i).findViewById(R.id.chkSpec);
            if (chkSpec.isSelected()) {
                if (i == textureAdapter.getCount() - 1) {
                    EditText edtTxtContent = (EditText) gridTexture.getChildAt(i).findViewById(R.id.edtTxtContent);
                    String content = edtTxtContent.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        showErrorToast("请输入材质内容");
                        return false;
                    } else {
                        htmlStr += "<p>材质：" + content + "</p>";
                        title += content;
                    }
                } else {
                    htmlStr += "<p>材质：" + chkSpec.getText() + "</p>";
                    title += chkSpec.getText();
                }

                break;
            }
        }

        LogHelper.e("HTMLGoods", htmlStr);
        LogHelper.e("TITLEGoods", title);
        return true;
    }


    /**
     * 发布信息
     */
    private void submit() {
        params.put("content", htmlStr);
        params.put("title", title);
        params.put("cover_img", coverImg);
        params.put("product_id", product.getProduct_id());
        params.put("product_name", product.getProduct_name());
        params.put("price_unit", unit);
        params.put("stock_unit", unit);
        params.put("mobile", getUser().getMobile());
        params.put("user_id", getUserID());
        params.put("uid", getUserID());
        params.put("examine_type", 1);

        MyHttpClient.getInstance().post(Url.RELEASE_GOODS, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        showErrorToast("发布成功");
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });

    }



    /**
     * 上传图片
     * @param fileArr
     */
    private void uploadImage(File[] fileArr) {
        RequestParams imgParam = new RequestParams();
        try {
            imgParam.put("file[]", fileArr);
            MyHttpClient.getInstance().post(Url.UPLOADIMAGE, imgParam, new AsyncHttpResponseHandler() {
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
                            List<Avatar> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), Avatar.class);
                            coverImg = "";
                            for (int i = 0; i < list.size(); i++) {
                                String imgUrl = list.get(i).getOrigin();
                                htmlStr += "<p><img src=\"" + imgUrl + "\"/></p>";
                                coverImg += imgUrl;
                                if (i < list.size() - 1)
                                    coverImg += ",";
                            }

                            submit();
                        } else {
                            progress.dismiss();
                            showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                        }
                    } catch (JSONException e) {
                        progress.dismiss();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progress.dismiss();
                    showFailTips(getResources().getString(R.string.requst_fail));
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    @OnClick({R.id.txtConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm: //发布信息
                if (!getHtml())
                    return;

                params = new RequestParams();

                /**
                 * 报价
                 */
                LinearLayout llPrice = (LinearLayout) llContent.findViewById(R.id.llPrice);
                for (int i = 0; i < llPrice.getChildCount(); i++) {
                    LinearLayout layoutParent = (LinearLayout) llPrice.getChildAt(i);
                    String key = "";
                    String value = "";

                    key = ((TextView)layoutParent.getChildAt(0)).getText().toString();
                    EditText edtValue = (EditText) ((ViewGroup)layoutParent.getChildAt(1)).getChildAt(0);
                    String content = edtValue.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        showErrorToast("请输入" + key);
                        return;
                    } else {
                        value = content;
                    }

                    params.put((String) edtValue.getTag(), value);
                }

                if (selectedPicture.size() == 0) {
                    showErrorToast("请上传产品图片");
                    return;
                }

                File[] fileArr = new File[thumbPictures.size() - 1];
                for (int i = 0; i < thumbPictures.size(); i++) {
                    String str = thumbPictures.get(i);
                    //去掉最后一个 +图片
                    if (!str.equals(""+ BasePhotoGridActivity.PICTURE_UPDATE_ICON)) {
                        fileArr[i] = new File(str);
                    }
                }
                uploadImage(fileArr);
                break;
            default:
                break;
        }
    }

}
