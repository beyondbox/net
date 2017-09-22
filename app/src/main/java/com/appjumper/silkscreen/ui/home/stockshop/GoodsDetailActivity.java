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
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.view.banner.CycleView2Pager;
import com.appjumper.silkscreen.view.banner.ViewFactory;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @Bind(R.id.txtSales)
    TextView txtSales;
    @Bind(R.id.webView)
    WebView webView;

    private CycleView2Pager cycleViewPager;
    private StockGoods goods;
    private List<String> urlList = new ArrayList<>();


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
        /*Picasso.with(context)
                .load(goods.getCover_img())
                .resize(DisplayUtil.dip2px(context, 320), DisplayUtil.dip2px(context, 240))
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageView);*/
        setCycleViewPager();

        txtTitle.setText(goods.getTitle());
        txtSurplus.setText("剩余" + goods.getStock() + goods.getStock_unit());
        if (TextUtils.isEmpty(goods.getUnit_price()))
            txtPrice.setText("时价");
        else
            txtPrice.setText(goods.getUnit_price() + "元/" + goods.getPrice_unit());


        if (TextUtils.isEmpty(goods.getSale_num())) {
            txtSales.setVisibility(View.INVISIBLE);
        } else {
            txtSales.setVisibility(View.VISIBLE);
            txtSales.setText("销售" + goods.getSale_num() + goods.getStock_unit());
        }


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


    /**
     * 设置轮播图
     */
    private void setCycleViewPager() {
        cycleViewPager = (CycleView2Pager) getFragmentManager().findFragmentById(R.id.fragment_cycle_viewpager_content);
        List<ImageView> views = new ArrayList<>();

        String [] imgArr = goods.getCover_img().split(",");
        urlList.clear();
        urlList.addAll(Arrays.asList(imgArr));
        List<Avatar> avatarList = new ArrayList<>();
        for (String url : urlList) {
            Avatar avatar = new Avatar();
            avatar.setOrigin(url);
            avatarList.add(avatar);
        }

        // 将最后一个ImageView添加进来
        views.add(ViewFactory.getImageView(this, urlList.get(urlList.size() - 1)));
        for (int i = 0; i < urlList.size(); i++) {
            views.add(ViewFactory.getImageView(this, urlList.get(i)));
        }
        // 将第一个ImageView添加进来
        views.add(ViewFactory.getImageView(this, urlList.get(0)));

        // 设置循环，在调用setData方法前调用
        cycleViewPager.setCycle(true);

        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, avatarList, mAdCycleViewListener);
        //设置轮播
        cycleViewPager.setWheel(true);

        // 设置轮播时间，默认5000ms
        cycleViewPager.setTime(3000);
        //设置圆点指示图标组居中显示，默认靠右
        cycleViewPager.setIndicatorCenter();
    }

    private CycleView2Pager.ImageCycleViewListener mAdCycleViewListener = new CycleView2Pager.ImageCycleViewListener() {

        @Override
        public void onImageClick(Avatar info, int position, View imageView) {
            if (cycleViewPager.isCycle()) {
                position = position - 1;
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, (ArrayList)urlList);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(intent);
            }
        }

    };



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
