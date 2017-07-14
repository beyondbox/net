package com.appjumper.silkscreen.ui.my.enterprise;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.bean.LineListResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.my.adapter.MyLogisticsListviewAdapter;
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
 * Created by Administrator on 2016-09-19.
 * 我的物流
 */
public class ServiceLogisticsFragment extends BaseFragment {
    private View mView;

    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;
    private PagedListView listView;
    private View mEmptyLayout;

    private String pagesize = "20";
    private int pageNumber = 1;
    private String type;
    private Context context;
    private List<LineList> list;
    private MyLogisticsListviewAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getContext();
        mView = inflater.inflate(R.layout.fragment_service, null);
        ButterKnife.bind(this, mView);
        return mView;
    }


    @Override
    protected void initData() {
        type = getArguments().getString("type");
        listView = pullToRefreshView.getRefreshableView();
        listView.onFinishLoading(false);
        mEmptyLayout = LayoutInflater.from(context).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);
        initListener();
        refresh();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(context);
        super.onDestroy();
    }


    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                start_Activity(getActivity(),MyLogisticsDetailsActivity.class,new BasicNameValuePair("id",list.get((i-1)).getId()));

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
            LineListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("page", "1");
                data.put("pagesize", pagesize);
                data.put("uid", getUserID());
                data.put("type", "4");
                response = JsonParser.getLineListResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICEMY));
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
            LineListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                data.put("uid", getUserID());
                data.put("type", "4");
                response = JsonParser.getLineListResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.SERVICEMY));
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
    private MyHandler handler = new MyHandler(getActivity());

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
                    LineListResponse response = (LineListResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new MyLogisticsListviewAdapter(getContext(), list);
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
                    LineListResponse pageResponse = (LineListResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<LineList> tempList = pageResponse.getData()
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
                default:
                    showErrorToast();
                    listView.onFinishLoading(false);
                    break;
            }
        }
    }
}
