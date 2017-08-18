package com.appjumper.silkscreen.ui.money;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Myoffer;
import com.appjumper.silkscreen.bean.MyofferResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.money.adapter.InquiryListViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.pulltorefresh.PagedListView;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshBase;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshPagedListView;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/11/11.
 * 企业收藏
 */
public class OfferMoreActivity extends BaseActivity {


    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;

    private PagedListView listView;
    private View mEmptyLayout;
    private String pagesize = "30";
    private int pageNumber = 1;
    private List<Myoffer> list;
    private InquiryListViewAdapter adapter;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_collection);
        initBack();
        ButterKnife.bind(this);
        String count = getIntent().getStringExtra("count");
        initTitle("报价公司" + "（" + count + "家）");
        id = getIntent().getStringExtra("id");
        listView = pullToRefreshView.getRefreshableView();
        listView.onFinishLoading(false);
        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);

        initListener();
        refresh();
    }

    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                start_Activity(OfferMoreActivity.this, CompanyDetailsActivity.class, new BasicNameValuePair("from", "2"), new BasicNameValuePair("id", list.get(i-1).getUser_id()));
            }
        });
        listView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

            @Override
            public void onLoadMoreItems() {
                new Thread(pageRun).start();
            }
        });
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                new Thread(run).start();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
    }


    private Runnable run = new Runnable() {

        public void run() {
            MyofferResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("pagesize", pagesize);
                data.put("page", "1");
                response = JsonParser.getMyofferResponse(HttpUtil.getMsg(Url.INQUIRY_MORE + "?" + HttpUtil.getData(data)));
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

    private Runnable pageRun = new Runnable() {
        @SuppressWarnings("unchecked")
        public void run() {
            MyofferResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("id", id);
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                response = JsonParser.getMyofferResponse(HttpUtil.getMsg(Url.INQUIRY_MORE + "?" + HttpUtil.getData(data)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                handler.sendMessage(handler.obtainMessage(NETWORK_SUCCESS_PAGER_RIGHT, response));
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
            OfferMoreActivity activity = (OfferMoreActivity) reference.get();
            if (activity == null) {
                return;
            }

            if (isDestroyed())
                return;

            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    MyofferResponse response = (MyofferResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new InquiryListViewAdapter(OfferMoreActivity.this, list);
                        activity.listView.onFinishLoading(response.getData().hasMore());
                        activity.listView.setAdapter(adapter);
                        activity.pageNumber = 2;
                        activity.pullToRefreshView.setEmptyView(activity.list.isEmpty() ? activity.mEmptyLayout : null);
                    } else {
                        activity.listView.onFinishLoading(false);
                        activity.showErrorToast(response.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    MyofferResponse pageResponse = (MyofferResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<Myoffer> tempList = pageResponse.getData()
                                .getItems();
                        activity.list.addAll(tempList);
                        activity.adapter.notifyDataSetChanged();
                        activity.listView.onFinishLoading(pageResponse.getData().hasMore());
                        activity.pageNumber++;
                    } else {
                        activity.listView.onFinishLoading(false);
                        activity.showErrorToast(pageResponse.getError_desc());
                    }
                    break;
                default:
                    activity.showErrorToast();
                    activity.listView.onFinishLoading(false);
                    break;
            }
        }
    }


}
