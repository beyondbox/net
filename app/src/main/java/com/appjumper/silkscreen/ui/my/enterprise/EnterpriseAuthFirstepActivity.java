package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.util.Const;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 企业认证第一步
 * Created by Botx on 2017/8/3.
 */

public class EnterpriseAuthFirstepActivity extends BaseActivity {

    public static EnterpriseAuthFirstepActivity instance = null;

    @Bind(R.id.txtCompanyName)
    TextView txtCompanyName;
    @Bind(R.id.txtRegistrID)
    EditText txtRegistrID;
    @Bind(R.id.txtPersonName)
    EditText txtPersonName;
    @Bind(R.id.txtID)
    EditText txtID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_auth_firstep);
        ButterKnife.bind(context);
        instance = this;

        initTitle("企业认证");
        initBack();

        txtCompanyName.setText(getUser().getEnterprise().getEnterprise_name());
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                txtRegistrID.requestFocus();
                AppTool.showSoftInput(context, txtRegistrID);
            }
        }, 500);*/
    }


    @OnClick({R.id.txtConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtConfirm:
                String registerID = txtRegistrID.getText().toString().trim();
                String personName = txtPersonName.getText().toString().trim();
                String ID = txtID.getText().toString().trim();
                if (TextUtils.isEmpty(registerID)) {
                    showErrorToast("请输入营业执照注册号");
                    return;
                }
                if (TextUtils.isEmpty(personName)) {
                    showErrorToast("请输入法人代表姓名");
                    return;
                }
                if (TextUtils.isEmpty(ID)) {
                    showErrorToast("请输入身份证号");
                    return;
                }
                if (ID.length() != 18) {
                    showErrorToast("请输入正确的身份证号");
                    return;
                }

                Intent intent = new Intent(context, EnterpriseAuthenticationActivity.class);
                intent.putExtra(Const.KEY_REGISTER_ID, registerID);
                intent.putExtra(Const.KEY_NAME, personName);
                intent.putExtra(Const.KEY_PID, ID);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
