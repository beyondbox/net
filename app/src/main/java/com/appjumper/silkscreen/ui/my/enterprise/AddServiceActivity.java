package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.ui.common.ProductSelectActivity;
import com.appjumper.silkscreen.ui.my.PersonalAuthenticationActivity;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-21.
 * 添加服务
 */
public class AddServiceActivity extends BaseActivity {

    @Bind(R.id.llNeedCertify)
    LinearLayout llNeedCertify;
    @Bind(R.id.rl_spot1)
    RelativeLayout rl_spot1;
    @Bind(R.id.txtState)
    TextView txtState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        ActivityTaskManager.getInstance().putActivity(this);
        initBack();
        ButterKnife.bind(this);
        initTitle("添加服务");

        /*User user = getUser();
        if (!user.getAuth_status().equals("2")) {
            rl_spot1.setVisibility(View.GONE);
            llNeedCertify.setVisibility(View.VISIBLE);
        } else if (!user.getEnterprise().getEnterprise_auth_status().equals("2")) {
            rl_spot1.setVisibility(View.GONE);
            llNeedCertify.setVisibility(View.VISIBLE);
            txtState.setText("企业认证");
        }*/
    }



    @OnClick({R.id.rl_order,R.id.rl_processing, R.id.rl_spot1, R.id.rl_spot2, R.id.rl_logistics})
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rl_order: //丝网订做
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_ORDER);
                break;
            case R.id.rl_processing: //丝网加工
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_PROCESS);
                break;
            case R.id.rl_spot1: //丝网现货,通过认证
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_STOCK);
                break;
            case R.id.rl_spot2: //丝网现货，未通过认证
                if (!getUser().getAuth_status().equals("2")) {
                    start_Activity(context, PersonalAuthenticationActivity.class);
                } else if (!getUser().getEnterprise().getEnterprise_auth_status().equals("2")) {
                    start_Activity(context, EnterpriseAuthFirstepActivity.class);
                }
                break;
            case R.id.rl_logistics://物流路线
                CommonApi.releaseCheck(context, getUserID(), Const.SERVICE_TYPE_LOGISTICS);
                break;
            default:
                break;
        }

    }


    /**
     * 跳转到产品选择界面
     */
    private void goToProductSelect(int serviceType, int motion) {
        Intent intent = new Intent(context, ProductSelectActivity.class);
        intent.putExtra(Const.KEY_SERVICE_TYPE, serviceType);
        intent.putExtra(Const.KEY_MOTION, motion);
        startActivity(intent);
    }


}
