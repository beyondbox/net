package com.appjumper.silkscreen.ui.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.bean.UserResponse;
import com.appjumper.silkscreen.bean.VerifycodeResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.android.tpush.XGPushManager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/7.
 * 注册
 */
public class RegistereActivity extends BaseActivity{
    @Bind(R.id.et_name)
    EditText et_name;//手机号

    @Bind(R.id.et_code)
    EditText et_code;//验证码

    @Bind(R.id.et_pwd)//密码
            EditText et_pwd;

    @Bind(R.id.t_obtain_code)
    TextView t_obtain_code;

    private final int DELAYTIME = 60;
    private int CURRENTDELAYTIME;
    private TimerTask task;
    private Timer timer;

    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        context = this;
        timer = new Timer();
        initBack();
    }

    @OnClick({R.id.btn_register,R.id.t_obtain_code})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register://注册
                if (et_name.getText().toString().trim().length() < 11) {
                    showErrorToast("请输入正确的手机号");
                    return;
                }
                if(et_code.getText().toString().trim().length() < 4){
                    showErrorToast("请输入正确的验证码");
                    return;
                }
                if (et_pwd.getText().toString().trim().length()<6){
                    showErrorToast("密码长度不能少于6位");
                    return;
                }
                hideKeyboard();
                initProgressDialog();
                progress.show();
                progress.setMessage("正在注册...");
                new Thread(registerRun).start();
                break;
            case R.id.t_obtain_code://获取验证码
                if (et_name.getText().toString().trim().length() < 11) {
                    showErrorToast("请输入正确的手机号");
                    return;
                }
                initProgressDialog();
                progress.show();
                progress.setMessage("发送中...");
                new Thread(codeRun).start();
                break;
        }
    }


    //获取验证码
    private Runnable codeRun = new Runnable() {
        private VerifycodeResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("mobile", et_name.getText().toString().trim());
                response = JsonParser.getVerifycodeResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.VERIFYCODE));
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
    //注册
    private Runnable registerRun = new Runnable() {
        private UserResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("mobile", et_name.getText().toString().trim());
                data.put("mobile_code", et_code.getText().toString().trim());
                data.put("password", et_pwd.getText().toString().trim());
                response = JsonParser.getUserResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.REGISTER));
            } catch (Exception e) {
                progress.dismiss();
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_PAGER_RIGHT, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    private MyHandler handler = new MyHandler(this);
    private static class MyHandler extends Handler {

        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final RegistereActivity activity = (RegistereActivity) reference.get();
            if(activity == null){
                return;
            }
            switch (msg.what) {
                case NETWORK_OTHER: // 验证码倒计时
                    if (activity.CURRENTDELAYTIME <= 0) {
                        activity.cancelTime();
                    } else {
                        activity.CURRENTDELAYTIME--;
                        activity.t_obtain_code.setText(activity.CURRENTDELAYTIME + "秒后重获");
                    }
                    break;
                case NETWORK_SUCCESS_DATA_RIGHT://获取验证码
                    progress.dismiss();
                    VerifycodeResponse response = (VerifycodeResponse) msg.obj;
                    if(response.isSuccess()){
                        activity.showErrorToast("验证码已发送，请注意查收");
                        activity.startTime();
                        activity.t_obtain_code.setClickable(false);
                        activity.t_obtain_code.setTextColor(activity.getResources().getColor(R.color.text_gray_color));
                        activity.t_obtain_code.setText(activity.DELAYTIME + "秒后重获");
                    }else{
                        activity.showErrorToast(response.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT://注册
                    progress.dismiss();
                    UserResponse userResponse = (UserResponse) msg.obj;
                    if(userResponse.isSuccess()){
                        activity.showErrorToast("注册成功");
                        //activity.login();
                        Intent data = new Intent();
                        data.putExtra(Const.KEY_MOBILE, activity.et_name.getText().toString().trim());
                        data.putExtra(Const.KEY_PASSWORD, activity.et_pwd.getText().toString().trim());
                        activity.setResult(Const.RESULT_CODE_REGISTER_SUCCESS, data);
                        activity.finish();
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

    private void cancelTime() {
        task.cancel();
        t_obtain_code.setClickable(true);
        t_obtain_code.setTextColor(getResources().getColor(R.color.theme_color));
        t_obtain_code.setText("获取验证码");
    }
    private void startTime() {
        CURRENTDELAYTIME = DELAYTIME;
        task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(NETWORK_OTHER);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    /**
     * 登录
     */
    private void login() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", et_name.getText().toString().trim());
        map.put("password", et_pwd.getText().toString().trim());

        MyHttpClient.post(Url.LOGIN, map, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.setMessage("正在登录...");
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int errorCode = jsonObj.getInt("error_code");
                    if (errorCode == 0) {
                        User user = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), User.class);
                        if (user != null) {
                            XGPushManager.registerPush(context, "*");
                            XGPushManager.registerPush(context, user.getMobile());
                            getMyApplication().getMyUserManager().storeUserInfo(user);
                            CommonApi.addLiveness(getUserID(), 1);
                        }
                    } else {
                        Toast.makeText(context, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                    context.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                RegistereActivity.this.finish();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }
}
