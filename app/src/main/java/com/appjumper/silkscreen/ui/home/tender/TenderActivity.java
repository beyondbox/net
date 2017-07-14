package com.appjumper.silkscreen.ui.home.tender;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Tender;
import com.appjumper.silkscreen.bean.TenderListResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.adapter.TenderListViewAdapter;
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
 * 招标
 */
public class TenderActivity extends BaseActivity {

    @Bind(R.id.rg)
    RadioGroup rg;

    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;

    private PagedListView listView;
    private View mEmptyLayout;
    private String pagesize = "30";
    private int pageNumber = 1;
    private String type = "1";
    private List<Tender> list;
    private String url=Url.TENDERLIST;
    private TenderListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender);
        initBack();
        ButterKnife.bind(this);

        listView = pullToRefreshView.getRefreshableView();
        listView.onFinishLoading(false);
        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);

        initListener();
        refresh();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                selectRadioBtn();
            }
        });
    }

    private void selectRadioBtn() {
        RadioButton radioButton = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
        switch (radioButton.getId()) {
            case R.id.rb0:
                type = "1";
                if (list != null) {
                    list.clear();
                }
                url=Url.TENDERLIST;
                refresh();
                break;
            case R.id.rb2:
                type = "2";
                if (list != null) {
                    list.clear();
                }
                url=Url.SELECTLIST;
                refresh();
                break;
        }
    }

    private void refresh() {
        pullToRefreshView.setRefreshing();
        new Thread(run).start();
    }

    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                i=i-1;

                if (!checkLogined())
                    return;

                if (type.equals("1")) {
                    start_Activity(TenderActivity.this, TenderDetailsActivity.class,new BasicNameValuePair("id",list.get(i).getId()));
                } else if (type.equals("2")) {
                    start_Activity(TenderActivity.this, GidDetailsActivity.class,new BasicNameValuePair("id",list.get(i).getId()));
                }

                list.get(i).setIs_read(true);
                adapter.notifyDataSetChanged();
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
            TenderListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("pagesize", pagesize);
                data.put("page", "1");
                response = JsonParser.getTenderListResponse(HttpUtil.getMsg(url + "?" + HttpUtil.getData(data)));
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
            TenderListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                response = JsonParser.getTenderListResponse(HttpUtil.getMsg(url + "?" + HttpUtil.getData(data)));
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
            final TenderActivity activity = (TenderActivity) reference.get();
            if (activity == null) {
                return;
            }
            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    TenderListResponse response = (TenderListResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new TenderListViewAdapter(activity, list,type);
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
                    TenderListResponse pageResponse = (TenderListResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<Tender> tempList = pageResponse.getData()
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
