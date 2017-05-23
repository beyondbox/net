package com.appjumper.silkscreen.ui.home.tender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.Tender;
import com.appjumper.silkscreen.bean.TenderDetailsResponse;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.view.ObservableScrollView;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-11-18.
 * 中标详情
 */
public class GidDetailsActivity extends BaseActivity {
    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;
    private String id;
    @Bind(R.id.tv_tender_title)
    TextView tv_tender_title;

    @Bind(R.id.tv_tender_cerate_time)
    TextView tv_tender_cerate_time;

    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.tv_money)
    TextView tv_money;
    @Bind(R.id.tv_data)
    TextView tv_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gid_details);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        initBack();
        ButterKnife.bind(this);
        mPullRefreshScrollView.scrollTo(0, 0);
        refresh();

        CommonApi.addLiveness(getUserID(), 7);
    }
    private void initView(Tender data){
        tv_tender_title.setText(data.getTitle());
        tv_tender_cerate_time.setText(data.getCom_name()+"  "+data.getCreate_time());
        tv_name.setText("中标人："+data.getName());
        tv_money.setText("中标金额："+data.getMoney());
        tv_data.setText("中标时间："+data.getDate());
    }

    private void refresh() {
        new Thread(run).start();
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
            TenderDetailsResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                response = JsonParser.getTenderDetailsResponse(HttpUtil.getMsg(Url.SELECTDETAILS + "?" + HttpUtil.getData(data)));
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

    public MyHandler handler = new MyHandler(this);

    public class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            final GidDetailsActivity activity = (GidDetailsActivity) reference.get();
            if (activity == null) {
                return;
            }

            if (isDestroyed())
                return;

            mPullRefreshScrollView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    TenderDetailsResponse response = (TenderDetailsResponse) msg.obj;
                    if (response.isSuccess()) {
                        initView(response.getData());
                    } else {
                        activity.showErrorToast(response.getError_desc());
                    }
                    break;
                default:
                    activity.showErrorToast();
                    break;
            }
        }
    }

}
