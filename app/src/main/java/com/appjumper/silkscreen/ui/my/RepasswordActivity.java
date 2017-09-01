package com.appjumper.silkscreen.ui.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/18.
 * 找回密码
 */
public class RepasswordActivity extends BaseActivity{
    private String mobile;

    @Bind(R.id.et_pwd)
    EditText et_pwd;

    @Bind(R.id.et_pwd2)
    EditText et_pw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repassword);
        initBack();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
    }

    @OnClick({R.id.btn_register})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register://提交
                if (et_pwd.getText().toString().trim().length() < 6) {
                    showErrorToast("密码长度不能少于6位");
                    return;
                }
                if(et_pw2.getText().toString().trim().length() < 6){
                    showErrorToast("密码长度不能少于6位");
                    return;
                }
                if(!et_pwd.getText().toString().trim().equals(et_pw2.getText().toString().trim())){
                    showErrorToast("两次密码输入不一致");
                    return;
                }
                hideKeyboard();
                initProgressDialog();
                progress.show();
                progress.setMessage("正在重置...");
                new Thread(repasswordRun).start();
                break;
        }
    }
    //重置密码
    private Runnable repasswordRun = new Runnable() {
        private BaseResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("mobile", mobile);
                data.put("password", et_pwd.getText().toString());
                data.put("repassword", et_pw2.getText().toString());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.REPASSWORD));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_DATA_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    private MyHandler handler = new MyHandler(this);
    private  class MyHandler extends Handler {

        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final RepasswordActivity activity = (RepasswordActivity) reference.get();
            if(activity == null){
                return;
            }

            if (isDestroyed())
                return;

            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT://重置密码
                    progress.dismiss();
                    BaseResponse userResponse = (BaseResponse) msg.obj;
                    if(userResponse.isSuccess()){
                        activity.showErrorToast("密码重置成功，请重新登录");
                        ActivityTaskManager.getInstance().getActivity(ValidationMobileActivity.class).finish();
                        SystemSettingActivity systemSteeing = (SystemSettingActivity)ActivityTaskManager.getInstance().getActivity(SystemSettingActivity.class);
                        if(systemSteeing!=null){
                            systemSteeing.finish();
                        }
                        finish();
                        getMyApplication().getMyUserManager().clean();
                    }else{
                        activity.showErrorToast(userResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    progress.dismiss();
                    activity.showErrorToast();
                    break;
            }
        }
    }
}
