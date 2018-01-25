package com.appjumper.silkscreen.ui.home.askbuy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.AskBuy;
import com.appjumper.silkscreen.bean.Unit;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.bigkoo.pickerview.OptionsPickerView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 发布报价
 * Created by Botx on 2017/10/18.
 */

public class ReleaseOfferActivity extends BaseActivity {

    @Bind(R.id.edtTxtPrice)
    EditText edtTxtPrice;
    @Bind(R.id.edtTxtContent)
    EditText edtTxtContent;
    @Bind(R.id.txtConfirm)
    TextView txtConfirm;
    @Bind(R.id.txtUnit)
    TextView txtUnit;
    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.txtContent)
    TextView txtContent;
    @Bind(R.id.txtProtocol)
    TextView txtProtocol;

    @Bind(R.id.edtTxtWeight)
    EditText edtTxtWeight;
    @Bind(R.id.txtWeightUnit)
    TextView txtWeightUnit;


    private DecimalFormat df = new DecimalFormat("0.00");
    private AskBuy data;
    private OptionsPickerView pvUnits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_offer);
        ButterKnife.bind(context);

        initBack();
        initTitle("我要报价");
        initProgressDialog(false, "正在发布....");
        edtTxtPrice.addTextChangedListener(new MyTextWatcher(edtTxtPrice));
        edtTxtWeight.addTextChangedListener(new MyTextWatcher(edtTxtWeight));

        data = (AskBuy) getIntent().getSerializableExtra(Const.KEY_OBJECT);
        setData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                edtTxtPrice.requestFocus();
                AppTool.showSoftInput(context, edtTxtPrice);
                getWeightUnits();
            }
        }, 80);
    }


    private void setData() {
        if (!TextUtils.isEmpty(data.getPurchase_unit()))
            txtUnit.setText("元/" + data.getPurchase_unit());

        if (data.getPurchase_num().equals("0"))
            txtTitle.setText("求购" + data.getProduct_name());
        else
            txtTitle.setText("求购" + data.getProduct_name() + " " + data.getPurchase_num() + data.getPurchase_unit());

        txtContent.setText(data.getPurchase_content());

        txtProtocol.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtProtocol.getPaint().setAntiAlias(true);
    }


    /**
     * 将输入框小数点控制在两位以内
     */
    private class MyTextWatcher implements TextWatcher {
        private EditText editText;

        public MyTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String content = editText.getText().toString().trim();
            String [] arr = content.split("\\.");
            if (arr.length > 1) {
                String decimal = arr[1];
                if (decimal.length() > 2) {
                    String newDecimal = decimal.substring(0, 2);
                    String newContent = arr[0] + "." + newDecimal;
                    editText.setText(newContent);
                    editText.setSelection(newContent.length());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    /**
     * 获取重量单位
     */
    private void getWeightUnits() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "purchase_unit_list");

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        final List<Unit> list = GsonUtil.getEntityList(jsonObj.getJSONArray("data").toString(), Unit.class);

                        pvUnits = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
                            @Override
                            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                                txtWeightUnit.setText(list.get(options1).getName());
                            }
                        }).build();
                        pvUnits.setPicker(list);

                        if (list.size() > 0)
                            txtWeightUnit.setText(list.get(0).getName());

                        getLastUnit();
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
     * 获取上次选择的单位
     */
    private void getLastUnit() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "purchase_offer_unit");
        params.put("uid", getUserID());
        params.put("product_id", data.getProduct_id());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        Unit unit = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), Unit.class);
                        if (!TextUtils.isEmpty(unit.getWeight_unit()))
                            txtWeightUnit.setText(unit.getWeight_unit());
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
     * 发布报价
     */
    private void submit() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "offer");
        params.put("uid", getUserID());
        params.put("purchase_id", data.getId());
        params.put("money", df.format(Double.valueOf(edtTxtPrice.getText().toString().trim())));
        params.put("offer_content", edtTxtContent.getText().toString().trim());
        params.put("offer_type", 1);
        params.put("weight", edtTxtWeight.getText().toString().trim());
        params.put("weight_unit", txtWeightUnit.getText().toString().trim());
        if (!TextUtils.isEmpty(data.getPurchase_unit()))
            params.put("price_unit", "元/" + data.getPurchase_unit());

        //平台业务员报价
        if (getUser().getMobile().equals("18531881110"))
            params.put("offer_user_type", 0);
        else
            params.put("offer_user_type", 1);

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
                        sendBroadcast(new Intent(Const.ACTION_RELEASE_SUCCESS));
                        start_Activity(context, AskBuyDetailActivity.class, new BasicNameValuePair("id", data.getId()));
                        finish();
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
                if (isDestroyed())
                    return;

                progress.dismiss();
            }
        });
    }


    @OnCheckedChanged(R.id.chkProtocol)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            txtConfirm.setEnabled(true);
        else
            txtConfirm.setEnabled(false);
    }


    @OnClick({R.id.txtConfirm, R.id.txtProtocol, R.id.txtWeightUnit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm: //发布
                if (TextUtils.isEmpty(edtTxtPrice.getText().toString().trim())) {
                    showErrorToast("请输入产品单价");
                    return;
                }
                if (TextUtils.isEmpty(edtTxtWeight.getText().toString().trim())) {
                    showErrorToast("请输入产品重量");
                    return;
                }
                if (TextUtils.isEmpty(txtWeightUnit.getText().toString().trim())) {
                    showErrorToast("请选择重量单位");
                    return;
                }
                submit();
                break;
            case R.id.txtProtocol: //协议
                User user = getUser();
                String name = TextUtils.isEmpty(user.getName()) ? "" : user.getName();

                String product = "";
                if (data.getPurchase_num().equals("0"))
                    product = data.getProduct_name();
                else
                    product = data.getProduct_name() + data.getPurchase_num() + data.getPurchase_unit();

                String url = Url.PROTOCOL_ASKBUY_OFFER + "?name=" + name + "&phone=" + user.getMobile() + "&goods=" + product + "&time=24&price=0.5";
                start_Activity(context, WebViewActivity.class, new BasicNameValuePair("title", "协议内容"), new BasicNameValuePair("url", url));
                break;
            case R.id.txtWeightUnit: //选择单位
                AppTool.hideSoftInput(context);
                pvUnits.show();
                break;
            default:
                break;
        }
    }


}
