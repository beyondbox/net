package com.appjumper.silkscreen.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.util.Const;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择产品服务
 * Created by Botx on 2017/4/6.
 */

public class ServiceSelectActivity extends BaseActivity {

    private boolean isMultiMode = false;
    private String action = "";
    private int serviceType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_select);
        ButterKnife.bind(this);

        initBack();
        initTitle("选择服务");

        Intent intent = getIntent();
        isMultiMode = intent.getBooleanExtra(Const.KEY_IS_MULTI_MODE, false);
        if (intent.hasExtra(Const.KEY_ACTION)) {
            action = intent.getStringExtra(Const.KEY_ACTION);
        }
    }

    @OnClick({R.id.txtOrder, R.id.txtProcess, R.id.txtStock})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtOrder: //订做
                serviceType = Const.SERVICE_TYPE_ORDER;
                break;
            case R.id.txtProcess: //加工
                serviceType = Const.SERVICE_TYPE_PROCESS;
                break;
            case R.id.txtStock: //现货
                serviceType = Const.SERVICE_TYPE_STOCK;
                break;
            default:
                break;
        }

        Intent intent = new Intent(context, ProductSelectActivity.class);
        intent.putExtra(Const.KEY_IS_MULTI_MODE, isMultiMode);
        intent.putExtra(Const.KEY_SERVICE_TYPE, serviceType);
        intent.putExtra(Const.KEY_ACTION, action);
        startActivityForResult(intent, Const.REQUEST_CODE_SELECT_PRODUCT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        if (requestCode == Const.REQUEST_CODE_SELECT_PRODUCT) {
            data.putExtra(Const.KEY_SERVICE_TYPE, serviceType);
            setResult(resultCode, data);
            finish();
        }
    }
}
