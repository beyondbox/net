package com.appjumper.silkscreen.ui.my.enterprise;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.LineDetails;
import com.appjumper.silkscreen.bean.LineDetailsResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yc on 2016/11/7.
 * 我的物流详情
 */
public class MyLogisticsDetailsActivity extends BaseActivity {
    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;

    @Bind(R.id.tv_start)//起点
            TextView tv_start;

    @Bind(R.id.tv_end)//终点
            TextView tv_end;

    @Bind(R.id.tv_passby)//途经地
            TextView tv_passby;


    @Bind(R.id.tv_car_width)//车宽
            TextView tv_car_width;

    @Bind(R.id.tv_car_load)//载重
            TextView tv_car_load;

    @Bind(R.id.tv_car_length)//车长
            TextView tv_car_length;

    @Bind(R.id.tv_car_height)//车高
            TextView tv_car_height;

    @Bind(R.id.tv_remark)//备注
            TextView tv_remark;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylogistics_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        initBack();
        listerrefresh();
        initProgressDialog();
        progress.show();
        progress.setMessage("正在加载...");

        refresh();
        mPullRefreshScrollView.scrollTo(0, 0);
    }

    //初始化信息
    private void initView(LineDetails data){
        tv_start.setText(data.getFrom());
        tv_end.setText(data.getTo());
        tv_passby.setText(data.getPassby_name());
        tv_car_length.setText(data.getCar_length()+"米");
        tv_car_height.setText(data.getCar_height()+"米");
        tv_car_width.setText(data.getCar_width()+"米");
        tv_car_load.setText(data.getCar_load()+"吨");
        tv_remark.setText(data.getRemark());
    }
    private void refresh() {
        mPullRefreshScrollView.setRefreshing();
        new Thread(detailsRun).start();
    }

    private void listerrefresh() {
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ObservableScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ObservableScrollView> refreshView) {
                new Thread(detailsRun).start();
            }
        });
    }
    //详情
    private Runnable detailsRun = new Runnable() {
        private LineDetailsResponse response;
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("id",id);
                data.put("uid",getUserID());
                response = JsonParser.getLineDetailsResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), Url.LINEDETAILS));
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
            mPullRefreshScrollView.onRefreshComplete();
            if(progress!=null){
                progress.dismiss();
            }
            MyLogisticsDetailsActivity activity = (MyLogisticsDetailsActivity) reference.get();
            if(activity == null){
                return;
            }
            switch (msg.what) {
                case NETWORK_SUCCESS_PAGER_RIGHT://详情
                    LineDetailsResponse baseResponse = (LineDetailsResponse) msg.obj;
                    if(baseResponse.isSuccess()){
                        LineDetails data = baseResponse.getData();
                        initView(data);
                    }else{
                        showErrorToast(baseResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    activity.showErrorToast();
                    break;
            }
        }
    }

    @OnClick({R.id.tv_contact})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_contact://删除

                break;
            default:
                break;
        }
    }

}
