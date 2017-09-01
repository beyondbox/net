package com.appjumper.silkscreen.ui.my;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.VerifycodeResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.util.manager.ActivityTaskManager;

import org.apache.http.message.BasicNameValuePair;

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
 * 验证手机号
 */
public class ValidationMobileActivity extends BaseActivity{
    @Bind(R.id.et_name)
    EditText et_name;//手机号

    @Bind(R.id.et_code)
    EditText et_code;//验证码

    @Bind(R.id.t_obtain_code)
    TextView t_obtain_code;

    private final int DELAYTIME = 60;
    private int CURRENTDELAYTIME;
    private TimerTask task;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_mobile);
        ActivityTaskManager.getInstance().putActivity(this);
        ButterKnife.bind(this);
        initBack();
        timer = new Timer();
    }

    @OnClick({R.id.btn_register,R.id.t_obtain_code})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register://下一步
                if (et_name.getText().toString().trim().length() < 11) {
                    showErrorToast("请输入正确的手机号");
                    return;
                }
                if(et_code.getText().toString().trim().length() < 4){
                    showErrorToast("请输入正确的验证码");
                    return;
                }
                hideKeyboard();
                initProgressDialog();
                progress.show();
                progress.setMessage("正在验证...");
                new Thread(authRun).start();
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
    //验证手机号
    private Runnable authRun = new Runnable() {
        private BaseResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("mobile", et_name.getText().toString().trim());
                data.put("mobile_code", et_code.getText().toString().trim());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.AUTHCODE));
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
    private  class MyHandler extends Handler {

        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final ValidationMobileActivity activity = (ValidationMobileActivity) reference.get();
            if(activity == null){
                return;
            }

            if (isDestroyed())
                return;

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
                case NETWORK_SUCCESS_PAGER_RIGHT://验证手机号
                    progress.dismiss();
                    BaseResponse userResponse = (BaseResponse) msg.obj;
                    if(userResponse.isSuccess()){
                        activity.start_Activity(activity,RepasswordActivity.class,new BasicNameValuePair("mobile",et_name.getText().toString().trim()));
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

    @Override
    public void finish() {
        super.finish();
        if (task != null)
            task.cancel();
    }
}
