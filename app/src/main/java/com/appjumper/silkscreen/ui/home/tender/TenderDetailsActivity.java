package com.appjumper.silkscreen.ui.home.tender;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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
import com.appjumper.silkscreen.view.SureOrCancelDialog;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshBase;
import com.appjumper.silkscreen.view.scrollView.PullToRefreshScrollView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-11-18.
 * 招标详情
 */
public class TenderDetailsActivity extends BaseActivity {
    @Bind(R.id.pull_refresh_scrollview)
    PullToRefreshScrollView mPullRefreshScrollView;


    @Bind(R.id.tv_tender_title)
    TextView tv_tender_title;

    @Bind(R.id.tv_tender_cerate_time)
    TextView tv_tender_cerate_time;

    @Bind(R.id.web_view)
    WebView webView;

    private String id;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender_details);
        Intent intent = getIntent();
         id = intent.getStringExtra("id");
        initBack();
        ButterKnife.bind(this);
        mPullRefreshScrollView.scrollTo(0, 0);
        refresh();

        CommonApi.addLiveness(getUserID(), 7);
    }

    private void initView(Tender data){
        mobile =data.getMobile();
        tv_tender_title.setText(data.getName());
        tv_tender_cerate_time.setText(data.getCom_name()+" "+data.getCreate_time());
        webView.loadDataWithBaseURL(null, data.getContent(), "text/html", "utf-8", null);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
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
                response = JsonParser.getTenderDetailsResponse(HttpUtil.postMsg(HttpUtil.getData(data),Url.TENDERDETAILS));
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
            final TenderDetailsActivity activity = (TenderDetailsActivity) reference.get();
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
    @OnClick({R.id.tv_consult})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_consult://马上咨询
                SureOrCancelDialog followDialog = new SureOrCancelDialog(this, "拨打电话？", new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile));//跳转到拨号界面，同时传递电话号码
                        startActivity(dialIntent);
                    }
                });
                followDialog.show();
                break;
            default:
                break;
        }
    }
}
