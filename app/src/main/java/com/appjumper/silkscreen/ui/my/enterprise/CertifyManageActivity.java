package com.appjumper.silkscreen.ui.my.enterprise;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.bean.UserResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.my.PersonalAuthenticationActivity;
import com.appjumper.silkscreen.ui.my.driver.DriverAuthFirstActivity;
import com.appjumper.silkscreen.util.Const;

import org.apache.http.message.BasicNameValuePair;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 认证管理
 * Created by Botx on 2017/8/3.
 */

public class CertifyManageActivity extends BaseActivity {

    @Bind(R.id.txtStatePerson)
    TextView txtStatePerson;
    @Bind(R.id.txtStateCompany)
    TextView txtStateCompany;
    @Bind(R.id.txtStateProductivity)
    TextView txtStateProductivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certify_manage);
        ButterKnife.bind(context);

        initTitle("认证管理");
        initBack();

        handleAuthState();
    }


    /**
     * 认证状态的显示
     */
    private void handleAuthState() {
        User user = getUser();

        int statePerson = Integer.valueOf(user.getAuth_status());
        switch (statePerson) {
            case Const.AUTH_ING: //认证中
                txtStatePerson.setText("认证中");
                txtStatePerson.setEnabled(false);
                break;
            case Const.AUTH_SUCCESS: //认证通过
                txtStatePerson.setText("已认证");
                txtStatePerson.setEnabled(false);
                break;
            default:
                break;
        }


        Enterprise enterprise = user.getEnterprise();
        if (enterprise == null)
            return;

        int stateCompany = Integer.valueOf(enterprise.getEnterprise_auth_status());
        switch (stateCompany) {
            case Const.AUTH_ING: //认证中
                txtStateCompany.setText("认证中");
                txtStateCompany.setEnabled(false);
                break;
            case Const.AUTH_SUCCESS: //认证通过
                txtStateCompany.setText("已认证");
                txtStateCompany.setEnabled(false);
                break;
            default:
                break;
        }

        int stateProductivity = Integer.valueOf(enterprise.getEnterprise_productivity_auth_status());
        switch (stateProductivity) {
            case Const.AUTH_ING: //认证中
                txtStateProductivity.setText("认证中");
                txtStateProductivity.setEnabled(false);
                break;
            case Const.AUTH_SUCCESS: //认证通过
                txtStateProductivity.setText("已认证");
                txtStateProductivity.setEnabled(false);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new UserInfoRun()).start();
    }


    private class UserInfoRun implements Runnable {
        private UserResponse response;

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                response = JsonParser.getUserResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.USERINFO));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    UserResponse userResponse = (UserResponse) msg.obj;
                    if (userResponse.isSuccess()) {
                        User user = userResponse.getData();
                        getMyApplication().getMyUserManager().storeUserInfo(user);
                        handleAuthState();
                    }
                    break;
                default:
                    break;
            }
        }
    };




    @OnClick({R.id.txtStatePerson, R.id.txtStateCompany, R.id.txtStateProductivity, R.id.txtStateDriver})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtStatePerson: //个人认证
                start_Activity(context, PersonalAuthenticationActivity.class);
                break;
            case R.id.txtStateCompany: //企业认证
                if(getUser().getEnterprise() == null) {
                    start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    //finish();
                    return;
                }

                start_Activity(context, EnterpriseAuthFirstepActivity.class);
                break;
            case R.id.txtStateProductivity: //生产力认证
                if(getUser().getEnterprise() == null) {
                    start_Activity(context, EnterpriseCreateActivity.class, new BasicNameValuePair("type", "0"));
                    //finish();
                    return;
                }

                if (getUser().getEnterprise().getEnterprise_auth_status().equals("2"))
                    start_Activity(context, ProductivityAuthenticationActivity.class);
                else
                    showErrorToast("完成企业认证后才可以申请生产力认证");
                break;
            case R.id.txtStateDriver: //司机认证
                start_Activity(context, DriverAuthFirstActivity.class);
                break;
            default:
                break;
        }
    }
}
