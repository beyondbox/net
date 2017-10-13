package com.appjumper.silkscreen.ui.my;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/11/18.
 * 意见反馈
 */
public class FeedbackActivity extends BaseActivity {
    @Bind(R.id.editSms)
    EditText etSms;
    @Bind(R.id.et_mobile)
    EditText etMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initBack();
        ButterKnife.bind(this);
        initRightButton("提交", new RightButtonListener() {
            @Override
            public void click() {
                if (etSms.getText().toString().trim().length() < 15) {
                    showErrorToast("请输入至少15字");
                    return;
                }
                if (etMobile.getText().toString().trim().length() < 11) {
                    showErrorToast("请输入正确的手机号码");
                    return;
                }
                initProgressDialog();
                progress.show();
                progress.setMessage("正在提交...");
                new Thread(submitRun).start();
            }
        });

    }

    //意见反馈
    private Runnable submitRun = new Runnable() {

        public void run() {
            BaseResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("mobile", etMobile.getText().toString().trim());
                data.put("content", etSms.getText().toString().trim());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.FEEDBACK));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_OTHER, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };
    public MyHandler handler = new MyHandler(this);

    public class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            FeedbackActivity activity = (FeedbackActivity) reference.get();
            if (activity == null) {
                return;
            }
            activity.progress.dismiss();
            switch (msg.what) {
                case NETWORK_OTHER:
                    BaseResponse base = (BaseResponse) msg.obj;
                    if (base.isSuccess()) {
                        showSuccessTips("提交成功");
                        CommonApi.addLiveness(getUserID(), 18);
                        finish();
                    } else {
                        showErrorToast(base.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    activity.showErrorToast();
                    break;
                default:
                    activity.showErrorToast();
                    break;
            }
        }
    }
}
