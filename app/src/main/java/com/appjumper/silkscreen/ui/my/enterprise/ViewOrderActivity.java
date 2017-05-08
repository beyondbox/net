package com.appjumper.silkscreen.ui.my.enterprise;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.ProductDetailsResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.my.adapter.ViewOrderListViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.MyListView;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.appjumper.silkscreen.view.banner.CycleView2Pager;
import com.appjumper.silkscreen.view.banner.ViewFactory;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/7.
 * 查看订做／加工／现货 详情
 */
public class ViewOrderActivity extends BaseActivity {

    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;

    @Bind(R.id.listview)
    MyListView listview;

    @Bind(R.id.tv_remark)
    TextView tv_remark;//备注

    private CycleView2Pager cycleViewPager;
    private String id;
    private List<Avatar> imglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        ButterKnife.bind(this);
        initBack();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        initTitle(intent.getStringExtra("title"));
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");
        new Thread(run).start();
        refresh();
    }

    private void initView(Product data){
        imglist = data.getImg_list();
        tv_remark.setText(data.getRemark());
        initialize(data.getImg_list());
        listview.setAdapter(new ViewOrderListViewAdapter(this,data.getService_spec()));
    }

    /**
     * 轮播图
     */
    private List<ImageView> views = new ArrayList<ImageView>();
    private void initialize(List<Avatar> banner) {
        if (views.size() > 0) {
            views.clear();
        }
        cycleViewPager = (CycleView2Pager) getFragmentManager()
                .findFragmentById(R.id.fragment_cycle_viewpager_content);

        // 将最后一个ImageView添加进来
        if (banner.size() > 0) {
            views.add(ViewFactory.getImageView(this, banner.get(banner.size() - 1).getSmall()));
            for (int i = 0; i < banner.size(); i++) {
                views.add(ViewFactory.getImageView(this, banner.get(i).getSmall()));
            }
            // 将第一个ImageView添加进来
            views.add(ViewFactory.getImageView(this, banner.get(0).getSmall()));

            // 设置循环，在调用setData方法前调用
            cycleViewPager.setCycle(true);

            // 在加载数据前设置是否循环
            cycleViewPager.setData(views, banner, mAdCycleViewListener);
            //设置轮播
            cycleViewPager.setWheel(true);

            // 设置轮播时间，默认5000ms
            cycleViewPager.setTime(3000);
            //设置圆点指示图标组居中显示，默认靠右
            cycleViewPager.setIndicatorCenter();
        }
    }


    private void refresh() {
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ObservableScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ObservableScrollView> refreshView) {
                new Thread(run).start();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ObservableScrollView> refreshView) {
                mPullRefreshScrollView.onRefreshComplete();
            }
        });
    }

    private Runnable run = new Runnable() {

        public void run() {
            ProductDetailsResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("uid", getUserID());
                response = JsonParser.getProductDetailsResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICEDETAILS));
            } catch (Exception e) {
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

    @OnClick({R.id.tv_delete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete://删除
                SureOrCancelDialog followDialog = new SureOrCancelDialog(this, "是否删除服务", new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        new Thread(deleteRun).start();
                    }
                });
                followDialog.show();
                break;
            default:
                break;
        }
    }

    private Runnable deleteRun = new Runnable() {

        public void run() {
            BaseResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("uid", getUserID());
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICEDELETE));
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

    private MyHandler handler = new MyHandler(this);
    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            mPullRefreshScrollView.onRefreshComplete();
            if(progress!=null){
                progress.dismiss();
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    ProductDetailsResponse response = (ProductDetailsResponse) msg.obj;
                    if (response.isSuccess()) {
                        Product data = response.getData();
                        initView(data);
                    } else {
                        showErrorToast(response.getError_desc());
                    }
                    break;
                case 4://删除
                    BaseResponse baseresponse = (BaseResponse) msg.obj;
                    if(baseresponse.isSuccess()){
                        showErrorToast("删除成功");
                        finish();
                    }else{
                        showErrorToast(baseresponse.getError_desc());
                    }
                    break;
                default:
                    showErrorToast();
                    break;
            }
        }
    }

    private CycleView2Pager.ImageCycleViewListener mAdCycleViewListener = new CycleView2Pager.ImageCycleViewListener() {

        @Override
        public void onImageClick(Avatar info, int position, View imageView) {
            if (cycleViewPager.isCycle()) {
                position = position - 1;
                Intent intent = new Intent(ViewOrderActivity.this, GalleryActivity.class);
                ArrayList<String> urls = new ArrayList<String>();
                for (Avatar string : imglist) {
                    urls .add(string.getOrigin());
                }
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(intent);
            }
        }
    };
}
