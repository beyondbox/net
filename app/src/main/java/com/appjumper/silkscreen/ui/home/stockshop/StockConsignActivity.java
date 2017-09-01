package com.appjumper.silkscreen.ui.home.stockshop;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 现货寄售
 * Created by Botx on 2017/8/30.
 */

public class StockConsignActivity extends BaseActivity {

    @Bind(R.id.edtTxtContent)
    EditText edtTxtContent;
    @Bind(R.id.edtTxtCompany)
    EditText edtTxtCompany;
    @Bind(R.id.edtTxtContact)
    EditText edtTxtContact;
    @Bind(R.id.edtTxtPhone)
    EditText edtTxtPhone;
    @Bind(R.id.edtTxtAddress)
    EditText edtTxtAddress;
    @Bind(R.id.txtConfirm)
    TextView txtConfirm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_consign);
        ButterKnife.bind(context);
        txtConfirm.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initTitle("现货寄售");
        initBack();
        initProgressDialog(false, "正在提交，请稍候....");

        User user = getUser();
        if (user.getEnterprise() != null) {
            String companyName = user.getEnterprise().getEnterprise_name();
            edtTxtCompany.setText(companyName);
        }
        if (!TextUtils.isEmpty(user.getUser_nicename())) {
            edtTxtContact.setText(user.getUser_nicename());
        }
    }


    /**
     * 提交申请
     */
    private void submit() {
        RequestParams params = MyHttpClient.getApiParam("feedback", "add_product_sale");
        params.put("content", edtTxtContent.getText().toString().trim());
        params.put("enterprise_name", edtTxtCompany.getText().toString().trim());
        params.put("user_nicename", edtTxtContact.getText().toString().trim());
        params.put("enterprise_tel", edtTxtPhone.getText().toString().trim());
        params.put("enterprise_address", edtTxtAddress.getText().toString().trim());
        params.put("uid", getUserID());

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
                        showErrorToast("提交成功");
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
                if (!isDestroyed())
                    progress.dismiss();
            }
        });
    }


    @OnClick({R.id.txtConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm:
                if (TextUtils.isEmpty(edtTxtContent.getText().toString().trim())) {
                    showErrorToast("请输入产品信息");
                    return;
                }
                if (TextUtils.isEmpty(edtTxtPhone.getText().toString().trim())) {
                    showErrorToast("请输入联系电话");
                    return;
                }
                if (TextUtils.isEmpty(edtTxtAddress.getText().toString().trim())) {
                    showErrorToast("请输入现货存放地址");
                    return;
                }
                submit();
                break;
            default:
                break;
        }
    }

}
