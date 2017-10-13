package com.appjumper.silkscreen.ui.my;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.IntegralList;
import com.appjumper.silkscreen.bean.IntegralListResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.my.adapter.PointListViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.pulltorefresh.PagedListView;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshBase;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshPagedListView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yc on 2016/10/9.
 * 我的积分
 */
public class MyPointActivity extends BaseActivity {
    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;
    @Bind(R.id.rl_title)
    LinearLayout rlTitle;

    private int height;

    private PagedListView listView;
    private View mEmptyLayout;
    private String pagesize = "20";
    private int pageNumber = 1;

    private PointListViewAdapter listAdapter;
    View pullView;

    private TextView tv_integral;
    private List<IntegralList> list;
    private PointListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_point);
        ButterKnife.bind(this);
        initBack();
        initTitle("我的积分");
//        initRightButton("规则说明", new RightButtonListener() {
//            @Override
//            public void click() {
//
//            }
//        });
        rlTitle.setBackgroundResource(android.R.color.transparent);
        listView = pullToRefreshView.getRefreshableView();
        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        listView.onFinishLoading(false);

        pullView = LayoutInflater.from(this).inflate(
                R.layout.layout_my_point_header, null);
        tv_integral = (TextView)pullView.findViewById(R.id.tv_integral);
        tv_integral.setText(getUser().getScore());

        listView.addHeaderView(pullView);
        height = dip(this, 210);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                int y = 0;
                if (pullView.getParent() == null) {
                    y = pullView.getHeight();
                } else {
                    y = -pullView.getTop();
                }
                Log.e("Log", y + "----");
                if (y <= 0) {
                    rlTitle.setBackgroundColor(Color.argb((int) 0, 0, 160, 233));
                } else if (y > 0 && y <= height) {
                    float scale = (float) y / height;
                    float alpha = (255 * scale);
                    rlTitle.setBackgroundColor(Color.argb((int) alpha, 0, 160, 233));
                } else {
                    rlTitle.setBackgroundColor(Color.argb((int) 255, 0, 160, 233));
                }
            }
        });
        initListener();
        refresh();

    }

    public void initListener() {
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

    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    private Runnable pageRun = new Runnable() {
        @SuppressWarnings("unchecked")
        public void run() {
            IntegralListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                data.put("uid", getUserID());
                response = JsonParser.getIntegralListResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.INTEGRALLIST));
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

    private Runnable run = new Runnable() {

        public void run() {
            IntegralListResponse response = null;
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("g", "api");
                data.put("m", "user");
                data.put("a", "integral_list");

                data.put("pagesize", pagesize);
                data.put("page", "1");
                data.put("uid", getUserID());

                //response = JsonParser.getIntegralListResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.INTEGRALLIST));
                response = JsonParser.getIntegralListResponse(HttpUtil.getMsg(Url.HOST + "?" + HttpUtil.getData(data)));
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

    private MyHandler handler = new MyHandler(this);


    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (isDestroyed())
                return;

            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    IntegralListResponse response = (IntegralListResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        pageNumber = 2;
                        adapter = new PointListViewAdapter(MyPointActivity.this, list);
                        listView.setAdapter(adapter);
                        listView.onFinishLoading(response.getData().hasMore());
//                        pullToRefreshView.setEmptyView(list.isEmpty() ? mEmptyLayout : null);
                    } else {
                        listView.onFinishLoading(false);
                        showErrorToast(response.getError_desc());
                    }
                    break;
                case NETWORK_SUCCESS_PAGER_RIGHT:
                    IntegralListResponse pageResponse = (IntegralListResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<IntegralList> tempList = pageResponse.getData()
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
