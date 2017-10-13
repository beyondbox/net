package com.appjumper.silkscreen.ui.my.enterprise;

import android.os.Bundle;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-21.
 * 认证管理（废弃）
 */
public class AuthenticationAdministrationActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_administration);
        initBack();
        ButterKnife.bind(this);
        initTitle("认证管理");
    }

    @OnClick({R.id.tv_enterprise_authentication, R.id.tv_production_authentication})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_enterprise_authentication://企业认证
                start_Activity(this, EnterpriseAuthenticationActivity.class);
                break;
            case R.id.tv_production_authentication://生产力认证
                start_Activity(this,ProductivityAuthenticationActivity.class);
                break;
            default:
                break;
        }

    }
}
