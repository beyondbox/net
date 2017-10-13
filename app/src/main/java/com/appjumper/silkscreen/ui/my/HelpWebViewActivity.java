package com.appjumper.silkscreen.ui.my;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.ConfigResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * 帮助
 */
public class HelpWebViewActivity extends BaseActivity {
    private WebView mWebView;
    private String url = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initBack();
        TextView title_text = (TextView)findViewById(R.id.title);
        title_text.setText("帮助");

        new Thread(new HelpRun()).start();
    }

    //帮助
    private class HelpRun implements Runnable {
        private ConfigResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                response = JsonParser.getConfigResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.CONFIG));
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

    private MyHandler handler = new MyHandler();
    private  class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://帮助
                    ConfigResponse userResponse = (ConfigResponse) msg.obj;
                    if(userResponse.isSuccess()){
                         url = userResponse.getData().getHelp_url();
                        init();
                    }else{
                        showErrorToast(userResponse.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    showErrorToast();
                    break;
                default:
                    showErrorToast();
                    break;
            }
        }
    };

    public void init() {
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLightTouchEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebView.loadUrl(url);

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) {
        handler.proceed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
