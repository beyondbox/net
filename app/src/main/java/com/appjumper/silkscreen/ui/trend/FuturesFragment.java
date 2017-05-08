package com.appjumper.silkscreen.ui.trend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.net.CommonApi;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/11/7.
 * 期货走势
 */
public class FuturesFragment extends BaseFragment {
    @Bind(R.id.web_view)
    WebView webView;
    private String url = "https://www.baidu.com";



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_webview, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        init();
        CommonApi.addLiveness(getUserID(), 13);
    }


    private void init() {
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLightTouchEnabled(true);
        mWebSettings.setSupportZoom(true);
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
}
