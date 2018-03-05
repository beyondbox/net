package com.appjumper.silkscreen.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.util.Const;


public class WebViewActivity extends BaseActivity {
    private WebView mWebView;
    private String url = "";
    private String title;

    private boolean isLocalMode = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initBack();
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		title = intent.getStringExtra("title");
        isLocalMode = intent.getBooleanExtra(Const.KEY_IS_LOCAL_MODE, false);
		TextView title_text = (TextView)findViewById(R.id.title);
        if(title == null){
            title_text.setText("丝网");
        }else {
            title_text.setText(title);
        }
        init();
    }

    public void init() {
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLightTouchEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setDisplayZoomControls(false);

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        if (isLocalMode) {
            String sHead= "<html><head><meta name=\"viewport\" content=\"width=device-width, " +
                    "initial-scale=1.0, minimum-scale=0.5, maximum-scale=2.0, user-scalable=yes\" />"+
                    "<style>img{max-width:100% !important;height:auto !important;}</style>"
                    +"<style>body{max-width:100% !important;}</style>"+"</head><body>";

            mWebView.loadDataWithBaseURL(null, sHead + url + "</body></html>", "text/html", "utf-8", null);
        } else {
            mWebView.loadUrl(url);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
    }


}
