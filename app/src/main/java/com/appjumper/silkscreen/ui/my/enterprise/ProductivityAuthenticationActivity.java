package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.AuthInfoResponse;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.ImageResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/12/5.
 * 生产力认证
 */
public class ProductivityAuthenticationActivity extends BaseActivity{
    @Bind(R.id.web_view)
    WebView web_view;

    @Bind(R.id.tv_consult)
    TextView tv_consult;

    private String enterprise_productivity_auth_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productivity_authentication);
        initBack();
        ButterKnife.bind(this);
        initTitle("生产力认证");
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        new Thread(new CompanyAuthInfoRun()).start();
    }

    @OnClick({R.id.tv_consult})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_consult://提交申请
                if(enterprise_productivity_auth_status.equals("0")||enterprise_productivity_auth_status.equals("3")){
                    initProgressDialog();
                    progress.show();
                    progress.setMessage("正在提交审核...");
                    new Thread(new CompanyAuthRun()).start();
                }
                break;
            default:
                break;
        }

    }

    //获取企业认证
    private class CompanyAuthInfoRun implements Runnable {
        private AuthInfoResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid",getUserID());
                response = JsonParser.getAuthInfoResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.AUTHINFO));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        4, response));
            } else {
                handler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    };

    //生产力认证
    private class CompanyAuthRun implements Runnable {
        private BaseResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("uid",getUserID());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.PRODUCTVITYAUTH));
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
    };

    private MyHandler handler = new MyHandler(this);
    private  class MyHandler extends Handler {
        private WeakReference<Context> reference;
        private ImageResponse imgResponse;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            ProductivityAuthenticationActivity activity = (ProductivityAuthenticationActivity) reference.get();
            if(activity == null){
                return;
            }

            if (isDestroyed())
                return;

            if(progress!=null){
                activity.progress.dismiss();
            }

            switch (msg.what) {
                case 4://获取企业认证
                    AuthInfoResponse authinforesponse = (AuthInfoResponse) msg.obj;
                    if(authinforesponse.isSuccess()){
                        tv_consult.setText(authinforesponse.getData().getEnterprise_productivity_auth_status_name());
                        enterprise_productivity_auth_status = authinforesponse.getData().getEnterprise_productivity_auth_status();
                        if(enterprise_productivity_auth_status.equals("0")||enterprise_productivity_auth_status.equals("3")){
                            tv_consult.setTextColor(activity.getResources().getColor(R.color.theme_color));
                        }else{
                            tv_consult.setTextColor(activity.getResources().getColor(R.color.red_color));
                        }
                        /*WebSettings mWebSettings = web_view.getSettings();
                        mWebSettings.setJavaScriptEnabled(true);
                        mWebSettings.setBuiltInZoomControls(true);
                        mWebSettings.setLightTouchEnabled(true);
                        mWebSettings.setSupportZoom(true);
                        web_view.loadUrl(Url.IP+"/index.php?g=portal&m=page&a=index&id=4");

                        web_view.setWebViewClient(new WebViewClient() {
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return true;
                            }
                        });*/
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT://生产力认证
                    BaseResponse baseresponse = (BaseResponse) msg.obj;
                    if(baseresponse.isSuccess()){
                        showErrorToast("生产力认证提交成功");
                        finish();
                    }else{
                        activity.showErrorToast(baseresponse.getError_desc());
                    }
                    break;
                default:
                    activity.showErrorToast("数据返回错误");
                    break;
            }
        }
    };
}
