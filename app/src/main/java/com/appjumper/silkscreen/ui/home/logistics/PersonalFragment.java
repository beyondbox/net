package com.appjumper.silkscreen.ui.home.logistics;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.bean.LineListResponse;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;
import com.appjumper.silkscreen.ui.home.adapter.LogisticsStandingListviewAdapter;
import com.appjumper.silkscreen.view.pulltorefresh.PagedListView;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshBase;
import com.appjumper.silkscreen.view.pulltorefresh.PullToRefreshPagedListView;

import org.apache.http.message.BasicNameValuePair;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 物流-个人
 * Created by Botx on 2017/9/29.
 */

public class PersonalFragment extends BaseFragment {

    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;
    @Bind(R.id.tv_start)//出发地
    TextView tv_start;
    @Bind(R.id.tv_end)//终点
    TextView tv_end;

    private PagedListView listView;
    private LogisticsStandingListviewAdapter adapter;
    private View mEmptyLayout;
    private List<LineList> list;
    private String pagesize = "20";
    private int pageNumber = 1;
    private String type = "2";

    private String start_id = "";
    private String end_id = "";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initData() {
        listView = pullToRefreshView.getRefreshableView();
        listView.onFinishLoading(false);
        mEmptyLayout = LayoutInflater.from(context).inflate(R.layout.pull_listitem_empty_padding, null);
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
                if (!checkLogined())
                    return;

                start_Activity(context, LogisticsDetailsActivity.class, new BasicNameValuePair("id", list.get((i - 1)).getId()), new BasicNameValuePair("type", type));
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
                data.put("type", type);
                data.put("pagesize", pagesize);
                data.put("page", "1");
                //if (!end_id.equals("") && !start_id.equals("")) {
                data.put("from", start_id);
                data.put("to", end_id);
                //}
                response = JsonParser.getLineListResponse(HttpUtil.getMsg(Url.LINELIST + "?" + HttpUtil.getData(data)));
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
                //if (!end_id.equals("") && !start_id.equals("")) {
                data.put("from", start_id);
                data.put("to", end_id);
                //}
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                data.put("type", type);
                response = JsonParser.getLineListResponse(HttpUtil.getMsg(Url.LINELIST + "?" + HttpUtil.getData(data)));
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


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isViewCreated) return;

            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    LineListResponse response = (LineListResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new LogisticsStandingListviewAdapter(context, list);
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
    };



    @OnClick({R.id.tv_start, R.id.tv_end})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start:
                startForResult_Activity(context, AddressSelectActivity.class, 1, new BasicNameValuePair("code", "1"), new BasicNameValuePair("type", "1"));
                break;
            case R.id.tv_end:
                startForResult_Activity(context, AddressSelectActivity.class, 2, new BasicNameValuePair("code", "2"), new BasicNameValuePair("type", "1"));
                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1://出发地
                start_id = data.getStringExtra("id");
                String start_name = data.getStringExtra("name");
                tv_start.setText(start_name);
                tv_start.setCompoundDrawables(null, null, null, null);
                refresh();
                break;
            case 2://目的地
                end_id = data.getStringExtra("id");
                String end_name = data.getStringExtra("name");
                tv_end.setText(end_name);
                tv_end.setCompoundDrawables(null, null, null, null);
                refresh();
                break;
            default:
                break;
        }
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

}
