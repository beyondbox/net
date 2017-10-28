package com.appjumper.silkscreen.ui.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.bean.UserResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/7.
 * 登录
 */
public class LoginActivity extends BaseActivity{
    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.et_pwd)
    EditText et_pwd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initBack();
        initProgressDialog(false, null);
    }

    @OnClick({R.id.btn_register,R.id.login_btn,R.id.tv_repassword, R.id.txtLoginWechat})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_repassword://忘记密码
                start_Activity(this,ValidationMobileActivity.class);
                break;
            case R.id.btn_register://注册
                //start_Activity(this,RegistereActivity.class);
                startForResult_Activity(this, RegistereActivity.class, 0);
                break;
            case R.id.login_btn://登录
                if (et_name.getText().toString().trim().length() < 11) {
                    showErrorToast("请输入正确的手机号");
                    return;
                }
                if (et_pwd.getText().toString().trim().length()<6){
                    showErrorToast("密码长度不能少于6位");
                    return;
                }
                hideKeyboard();
                progress.show();
                new Thread(loginRun).start();
                break;
            case R.id.txtLoginWechat: //微信登录
                if (WXAPIFactory.createWXAPI(context, null).isWXAppInstalled())
                    UMShareAPI.get(context).getPlatformInfo(context, SHARE_MEDIA.WEIXIN, umAuthListener);
                else
                    showErrorToast("您还没有安装微信");
                break;
            default:
                break;
        }
    }

    //登录
    private Runnable loginRun = new Runnable() {
        private UserResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("g", "api");
                data.put("m", "user");
                data.put("a", "login");

                data.put("mobile", et_name.getText().toString().trim());
                data.put("password", et_pwd.getText().toString().trim());

                //response = JsonParser.getUserResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.LOGIN));
                response = JsonParser.getUserResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(data)));

                //String url = "http://192.168.1.192/index.php?g=api&m=user&a=login&mobile=15930812811&password=siwangjia";
                //response = JsonParser.getUserResponse(HttpUtil.getMsg(url));
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
             LoginActivity activity = (LoginActivity) reference.get();
            if(activity == null){
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://登录
                    progress.dismiss();
                    UserResponse userResponse = (UserResponse) msg.obj;
                    if(userResponse.isSuccess()){
                        final User user = userResponse.getData();
                        XGPushManager.registerPush(getApplicationContext(), "*");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                XGPushManager.registerPush(getApplicationContext(), user.getMobile());
                            }
                        }, 1000);

                        getMyApplication().getMyUserManager().storeUserInfo(user);
                        CommonApi.addLiveness(getUserID(), 1);
                        sendBroadcast(new Intent(Const.ACTION_LOGIN_SUCCESS));

                        finish();
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



    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            loginWeChat(data);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
        }
    };


    /**
     * 微信登录
     */
    private void loginWeChat(Map<String, String> data) {
        RequestParams params = MyHttpClient.getApiParam("user", "register_WeChat");
        params.put("openid", data.get("openid"));
        params.put("sex", data.get("gender"));
        params.put("avatar", data.get("profile_image_url"));
        params.put("user_nicename", data.get("screen_name"));

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        getUserInfo(jsonObj.getJSONObject("data").getString("id"));
                    } else {
                        progress.dismiss();
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    progress.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progress.dismiss();
                showErrorToast(getResources().getString(R.string.requst_fail));
            }
        });
    }


    /**
     * 微信登录后根据用户id获取用户信息
     * @param uid
     */
    private void getUserInfo(String uid) {
        RequestParams params = MyHttpClient.getApiParam("user", "info");
        params.put("uid", uid);
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        User user = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), User.class);
                        getMyApplication().getMyUserManager().storeUserInfo(user);
                        CommonApi.addLiveness(getUserID(), 1);
                        sendBroadcast(new Intent(Const.ACTION_LOGIN_SUCCESS));
                        finish();
                    } else {
                        showErrorToast(jsonObj.getString(Const.KEY_ERROR_DESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showErrorToast(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Const.RESULT_CODE_REGISTER_SUCCESS) {
            et_name.setText(data.getStringExtra(Const.KEY_MOBILE));
            et_pwd.setText(data.getStringExtra(Const.KEY_PASSWORD));
            progress.show();
            new Thread(loginRun).start();
        }
    }
}
