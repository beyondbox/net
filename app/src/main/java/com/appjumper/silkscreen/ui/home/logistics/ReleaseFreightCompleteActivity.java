package com.appjumper.silkscreen.ui.home.logistics;

import android.os.Bundle;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发布空车配货提交成功
 * Created by Botx on 2017/10/26.
 */

public class ReleaseFreightCompleteActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_freight_complete);
        ButterKnife.bind(context);
        initTitle("空车配货订单");
    }

    @OnClick({R.id.txtFreightList, R.id.txtCall, R.id.txtAdd, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (ReleaseFreightActivity.instance != null)
                    ReleaseFreightActivity.instance.finish();
                finish();
                break;
            case R.id.txtFreightList:
                if (ReleaseFreightActivity.instance != null)
                    ReleaseFreightActivity.instance.finish();
                finish();
                break;
            case R.id.txtCall:
                AppTool.dial(context, Const.SERVICE_PHONE_FREIGHT);
                break;
            case R.id.txtAdd: //继续发布
                finish();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (ReleaseFreightActivity.instance != null)
            ReleaseFreightActivity.instance.finish();
        super.onBackPressed();
    }
}
