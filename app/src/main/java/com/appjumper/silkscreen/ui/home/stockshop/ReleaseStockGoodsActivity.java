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
import com.appjumper.silkscreen.ui.home.adapter.StockGoodsSpecGridAdapter;
import com.appjumper.silkscreen.util.LogHelper;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;

import java.util.Arrays;

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

    private String [] arrCisheng = {"高锌热镀丝", "包塑冷镀丝", "一般冷镀", "普锌热镀", "包塑热镀丝", "其他"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_stock_goods);
        ButterKnife.bind(context);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initBack();
        initView();

        initSpecView();
    }


    private void initSpecView() {
        View contentView = getLayoutInflater().inflate(R.layout.layout_goods_spec_cisheng, null);
        llContent.addView(contentView);

        gridTexture = (GridView) contentView.findViewById(R.id.gridTexture);
        textureAdapter = new StockGoodsSpecGridAdapter(context, Arrays.asList(arrCisheng));
        gridTexture.setAdapter(textureAdapter);
        gridTexture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textureAdapter.changeSelected(position);
            }
        });
    }


    /**
     * 生成html字符串
     */
    private void getHtml() {
        htmlStr = "";

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
                        return;
                    } else {
                        htmlStr += "<p>材质：" + content + "</p>";
                    }
                } else {
                    htmlStr += "<p>材质：" + chkSpec.getText() + "</p>";
                }

                break;
            }
        }

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
                        return;
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
                        return;
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
                                    return;
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
                        return;
                    }
                    break;
                default:
                    break;
            }

            htmlStr += "<p>" + key + "：" + value + "</p>";
        }


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

            htmlStr += "<p>" + key + "：" + value + "</p>";
        }


        LogHelper.e("HTMLGoods", htmlStr);
    }


    @OnClick({R.id.txtConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm:
                getHtml();
                break;
            default:
                break;
        }
    }

}
