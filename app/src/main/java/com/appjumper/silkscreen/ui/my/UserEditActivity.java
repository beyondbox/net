package com.appjumper.silkscreen.ui.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/23.
 * 编辑输入框
 */
public class UserEditActivity extends BaseActivity{
    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.et_text)
    EditText et_text;

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useredit);
        ButterKnife.bind(this);
        initBack();
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String hinttitle = intent.getStringExtra("hinttitle");
        key = intent.getStringExtra("key");
        if(key.equals("nickname")){
            et_text.setText(getUser().getUser_nicename());
        }
        tv_title.setText(title);
        et_text.setHint(hinttitle);
    }

    @OnClick({R.id.tv_submit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit://完成
                if(et_text.getText().toString().trim().length()<1){
                    showErrorToast("修改内容不能为空");
                    return;
                }
                hideKeyboard();
                initProgressDialog();
                progress.show();
                progress.setMessage("正在修改...");
                new Thread(editRun).start();
                break;
            default:
                break;
        }
    }

    //修改
    private Runnable editRun = new Runnable() {
        private BaseResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put(key, et_text.getText().toString().trim());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.USEREDIT));
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
            UserEditActivity activity = (UserEditActivity) reference.get();
            if(activity == null){
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://修改
                    progress.dismiss();
                    BaseResponse userResponse = (BaseResponse) msg.obj;
                    if(userResponse.isSuccess()){
                        showErrorToast("修改成功");
                        User user = getUser();
                        if(key.equals("nickname")){
                            user.setUser_nicename(et_text.getText().toString().trim());
                        }
                        getMyApplication().getMyUserManager()
                                .storeUserInfo(user);
                        finish();
                    }else if(userResponse.getError_code()==500){
                        showErrorToast("修改用户名不能重复");
                    }else{
                        showErrorToast(userResponse.getError_desc());

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
