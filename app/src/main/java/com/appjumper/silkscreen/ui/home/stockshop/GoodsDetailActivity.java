package com.appjumper.silkscreen.ui.home.stockshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 商品详情
 * Created by Botx on 2017/8/28.
 */

public class GoodsDetailActivity extends BaseActivity {

    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.txtPrice)
    TextView txtPrice;
    @Bind(R.id.txtSurplus)
    TextView txtSurplus;
    @Bind(R.id.webView)
    WebView webView;


    private StockGoods goods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ButterKnife.bind(context);
        initTitle("商品详情");
        initBack();

        goods = (StockGoods) getIntent().getSerializableExtra(Const.KEY_OBJECT);
        setData();
    }


    /**
     * 渲染数据
     */
    private void setData() {
        Picasso.with(context)
                .load(goods.getCover_img())
                .resize(DisplayUtil.dip2px(context, 320), DisplayUtil.dip2px(context, 240))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageView);

        txtTitle.setText(goods.getTitle());
        txtSurplus.setText("剩余" + goods.getStock() + goods.getStock_unit());
        if (TextUtils.isEmpty(goods.getUnit_price()))
            txtPrice.setText("时价");
        else
            txtPrice.setText("¥" + goods.getUnit_price() + "/" + goods.getPrice_unit());

        WebSettings settings = webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);

        String sHead= "<html><head><meta name=\"viewport\" content=\"width=device-width, " +
                "initial-scale=1.0, minimum-scale=0.5, maximum-scale=2.0, user-scalable=yes\" />"+
                "<style>img{max-width:100% !important;height:auto !important;}</style>"
                +"<style>body{max-width:100% !important;}</style>"+"</head><body>";

        webView.loadDataWithBaseURL(null, sHead + goods.getContent() + "</body></html>", "text/html", "utf-8", null);
    }







    @OnClick({R.id.txtCall, R.id.imageView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCall: //电话咨询
                if (!TextUtils.isEmpty(goods.getMobile())) {
                    AppTool.dial(context, goods.getMobile());
                    addCount();
                }
                break;
            case R.id.imageView: //封面
                if (!TextUtils.isEmpty(goods.getCover_img())) {
                    Intent intent = new Intent(context, GalleryActivity.class);
                    ArrayList<String> urls = new ArrayList<>();
                    urls.add(goods.getCover_img());
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 增加咨询次数
     */
    private void addCount() {
        RequestParams params = MyHttpClient.getApiParam("goods", "consult_num");
        params.put("id", goods.getId());
        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }
}
