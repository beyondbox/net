package com.appjumper.silkscreen.ui.my;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.MainActivity;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.android.tpush.XGPushManager;

import org.apache.http.Header;

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
    @Bind(R.id.llSwitch)
    LinearLayout llSwitch;
    @Bind(R.id.switchFreight)
    SwitchCompat switchFreight;
    @Bind(R.id.switchAskBuy)
    SwitchCompat switchAskBuy;

    private String freightValue = "0";
    private String askBuyValue = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
        ActivityTaskManager.getInstance().putActivity(this);
        ButterKnife.bind(this);
        initBack();
        initSwitch();
        User user = getUser();
        if (user == null) {
            llSwitch.setVisibility(View.GONE);
            ll_exit.setVisibility(View.GONE);
        } else {
            llSwitch.setVisibility(View.VISIBLE);
            ll_exit.setVisibility(View.VISIBLE);
        }
    }


    private void initSwitch() {
        if (getUser() != null) {
            freightValue = getUser().getIs_car_product_message();
            askBuyValue = getUser().getIs_purchase();
            switchFreight.setChecked(freightValue.equals("0"));
            switchAskBuy.setChecked(askBuyValue.equals("0"));
        }

        switchFreight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    freightValue = "0";
                else
                    freightValue = "1";

                switchPost(1);
            }
        });

        switchAskBuy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    askBuyValue = "0";
                else
                    askBuyValue = "1";

                switchPost(0);
            }
        });
    }


    /**
     * 短信开关接口
     */
    private void switchPost(int type) {
        RequestParams params = MyHttpClient.getApiParam("user", "open_close_short_message");
        params.put("uid", getUserID());
        params.put("message_type", type);
        params.put("is_purchase", askBuyValue);
        params.put("is_car_product_message", freightValue);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
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
                        //XGPushManager.registerPush(getApplicationContext(), "*");
                        XGPushManager.unregisterPush(getApplicationContext());
                        getMyApplication().getMyUserManager().clean();

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                followDialog.show();
                break;
            default:
                break;
        }
    }
}
