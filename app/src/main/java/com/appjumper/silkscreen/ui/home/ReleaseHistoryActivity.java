package com.appjumper.silkscreen.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.NewPublic;
import com.appjumper.silkscreen.bean.NewPublicResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.adapter.HomeListview2Adapter;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentDetailsActivity;
import com.appjumper.silkscreen.ui.home.logistics.LogisticsDetailsActivity;
import com.appjumper.silkscreen.ui.home.logistics.TruckDetailsActivity;
import com.appjumper.silkscreen.ui.home.process.ProcessingDetailsActivity;
import com.appjumper.silkscreen.ui.home.recruit.RecruitDetailsActivity;
import com.appjumper.silkscreen.ui.home.workshop.WorkshopDetailsActivity;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.pulltorefresh.PagedListView;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshBase;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshPagedListView;

import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/12/6.
 * 发布历史
 */
public class ReleaseHistoryActivity extends BaseActivity{
    private String pagesize = "20";
    private int pageNumber = 1;
    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;
    private PagedListView listView;
    private View mEmptyLayout;
    private List<NewPublic> list;
    private HomeListview2Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_history);
        ButterKnife.bind(this);
        initBack();
        initTitle("发布历史");
        listView = pullToRefreshView.getRefreshableView();
        listView.onFinishLoading(false);
        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);
        refresh();
        initListener();

    }
    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewPublic item = list.get(i);
                if (!checkLogined())
                    return;

                switch (item.getType()){
                    case "1"://定做
                        start_Activity(ReleaseHistoryActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", item.getTitle()+item.getSubtitle()),new BasicNameValuePair("id",item.getInfo_id()));
                        break;
                    case "2"://加工
                        start_Activity(ReleaseHistoryActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", item.getTitle()+item.getSubtitle()),new BasicNameValuePair("id",item.getInfo_id()));
                        break;
                    case "3"://现货
                        start_Activity(ReleaseHistoryActivity.this, ProcessingDetailsActivity.class, new BasicNameValuePair("title", item.getTitle()+item.getSubtitle()),new BasicNameValuePair("id",item.getInfo_id()));
                        break;
                    case "4"://物流
                        start_Activity(ReleaseHistoryActivity.this, LogisticsDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()), new BasicNameValuePair("type", "1"));
                        break;
                    case "5"://设备
                        start_Activity(ReleaseHistoryActivity.this, EquipmentDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                    case "6"://找车
                        start_Activity(ReleaseHistoryActivity.this, TruckDetailsActivity.class, new BasicNameValuePair("title", "详情"),new BasicNameValuePair("id",item.getInfo_id()));
                        break;
                    case "7"://招聘
                        start_Activity(ReleaseHistoryActivity.this, RecruitDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                    case "8"://厂房
                        start_Activity(ReleaseHistoryActivity.this, WorkshopDetailsActivity.class, new BasicNameValuePair("id", item.getInfo_id()));
                        break;
                }
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
            NewPublicResponse response = null;
            try {
                Map<String,String> map = new HashMap<>();
                map.put("page", "1");
                map.put("pagesize", pagesize);
                response = JsonParser.getNewPublicResponse(HttpUtil.postMsg(HttpUtil.getData(map), Url.NEWPUBLIC));
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
            NewPublicResponse response = null;
            try {
                Map<String,String> map = new HashMap<>();
                map.put("page", pageNumber+"");
                map.put("pagesize", pagesize);
                response = JsonParser.getNewPublicResponse(HttpUtil.postMsg(HttpUtil.getData(map), Url.NEWPUBLIC));
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
    private MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    NewPublicResponse response = (NewPublicResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new HomeListview2Adapter(ReleaseHistoryActivity.this, list);
                        listView.setAdapter(adapter);
                        listView.onFinishLoading(response.getData().hasMore());
                        listView.setAdapter(adapter);
                        pageNumber = 2;
                        pullToRefreshView.setEmptyView(list.isEmpty() ? mEmptyLayout : null);
                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(response.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    NewPublicResponse pageResponse = (NewPublicResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<NewPublic> tempList = pageResponse.getData()
                                .getItems();
                        list.addAll(tempList);
                        adapter.notifyDataSetChanged();
                        listView.onFinishLoading(pageResponse.getData().hasMore());
                        pageNumber++;
                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(pageResponse.getError_desc());
                    }
                    break;
                case NETWORK_FAIL:
                    showErrorToast("返回数据有误");
                    break;
                default:
                    showErrorToast();
                    listView.onFinishLoading(false);
                    break;
            }
        }
    }
}
