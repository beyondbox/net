package com.appjumper.silkscreen.ui.my;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.tencent.android.tpush.XGPushManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/18.
 * 系统设置
 */
public class SystemSettingActivity extends BaseActivity {
    @Bind(R.id.ll_exit)
    LinearLayout ll_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
        ActivityTaskManager.getInstance().putActivity(this);
        ButterKnife.bind(this);
        initBack();
        User user = getUser();
        if (user == null) {
            ll_exit.setVisibility(View.GONE);
        } else {
            ll_exit.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.rl_updatepassword, R.id.rl_aboutus,
            R.id.rl_contact_customer_service, R.id.rl_Logout})
    public void onClick(View v) {
        SureOrCancelDialog followDialog;
        switch (v.getId()) {

            case R.id.rl_updatepassword://修改密码
                start_Activity(this, ValidationMobileActivity.class);
                break;
            case R.id.rl_aboutus://关于我们
                start_Activity(this, AboutUsActivity.class);
                break;
            case R.id.rl_contact_customer_service://联系客服
                 followDialog = new SureOrCancelDialog(this, "是否联系客服？", new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "13373080101"));//跳转到拨号界面，同时传递电话号码
                        startActivity(dialIntent);
                    }
                });
                followDialog.show();
                break;
            case R.id.rl_Logout://退出登录
                 followDialog = new SureOrCancelDialog(this, "是否退出登录？", new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        XGPushManager.registerPush(SystemSettingActivity.this, "*");
                        getMyApplication().getMyUserManager().clean();
                        finish();
                    }
                });
                followDialog.show();
                break;
            default:
                break;
        }
    }
}
