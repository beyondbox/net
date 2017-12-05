package com.appjumper.silkscreen.ui.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.Const;
import com.bigkoo.pickerview.OptionsPickerView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    @Bind(R.id.txtUnit)
    TextView txtUnit;

    private DecimalFormat df = new DecimalFormat("0.00");
    private String inquiryId = "";
    private OptionsPickerView pvUnits;
    private String [] unitArr = {"元/平米", "元/卷", "元/吨", "元/片", "元/套", "元/根", "元/个", "元/捆", "元/米"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_offer);
        ButterKnife.bind(context);

        initBack();
        initTitle("我要报价");
        initPickerView();
        initProgressDialog(false, "正在发布....");

        inquiryId = getIntent().getStringExtra("id");
        edtTxtPrice.addTextChangedListener(textWatcher);
    }


    private void initPickerView() {
        pvUnits = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                txtUnit.setText(unitArr[options1]);
            }
        }).build();
        pvUnits.setPicker(Arrays.asList(unitArr));
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String content = edtTxtPrice.getText().toString().trim();
            String [] arr = content.split("\\.");
            if (arr.length > 1) {
                String decimal = arr[1];
                if (decimal.length() > 2) {
                    String newDecimal = decimal.substring(0, 2);
                    String newContent = arr[0] + "." + newDecimal;
                    edtTxtPrice.setText(newContent);
                    edtTxtPrice.setSelection(newContent.length());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    /**
     * 发布报价
     */
    private void submit() {
        RequestParams params = MyHttpClient.getApiParam("purchase", "offer");
        params.put("uid", getUserID());
        params.put("purchase_id", inquiryId);
        params.put("money", df.format(Double.valueOf(edtTxtPrice.getText().toString().trim())));
        params.put("price_unit", txtUnit.getText().toString().trim());
        params.put("offer_content", edtTxtContent.getText().toString().trim());
        params.put("offer_type", 1);

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
                        if (AskBuyDetailActivity.instance != null)
                            AskBuyDetailActivity.instance.finish();
                        sendBroadcast(new Intent(Const.ACTION_RELEASE_SUCCESS));

                        Intent intent = new Intent(context, ReleaseCompleteActivity.class);
                        intent.putExtra(Const.KEY_TYPE, ReleaseCompleteActivity.TYPE_OFFER);
                        startActivity(intent);
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


    @OnClick({R.id.txtConfirm, R.id.txtUnit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm: //发布
                if (TextUtils.isEmpty(edtTxtPrice.getText().toString().trim())) {
                    showErrorToast("请输入产品单价");
                    return;
                }
                submit();
                break;
            case R.id.txtUnit: //选择单位
                pvUnits.show();
                break;
            default:
                break;
        }
    }


}
