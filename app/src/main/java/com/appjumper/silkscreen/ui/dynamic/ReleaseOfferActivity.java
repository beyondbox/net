package com.appjumper.silkscreen.ui.dynamic;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.loopj.android.http.RequestParams;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_offer);
        ButterKnife.bind(context);

        initBack();
        initTitle("我要报价");

        edtTxtPrice.addTextChangedListener(textWatcher);
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
        //params.put("", );
    }


    @OnClick({R.id.txtConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm:
                if (TextUtils.isEmpty(edtTxtPrice.getText().toString().trim())) {
                    showErrorToast("请输入产品单价");
                    return;
                }
                if (TextUtils.isEmpty(edtTxtContent.getText().toString().trim())) {
                    showErrorToast("请输入报价说明");
                    return;
                }
                submit();
                break;
            default:
                break;
        }
    }

}
