package com.appjumper.silkscreen.ui.my;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.BaseResponse;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.EnterpriseCollectionResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.home.CompanyDetailsActivity;
import com.appjumper.silkscreen.ui.home.adapter.EnterpriseListListViewAdapter;
import com.appjumper.silkscreen.net.HttpUtil;
import com.appjumper.silkscreen.net.JsonParser;
import com.appjumper.silkscreen.view.SureOrCancelDialog;
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
public class EnterpriseCollectionActivity extends BaseActivity {


    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;

    private PagedListView listView;
    private View mEmptyLayout;
    private String pagesize = "30";
    private int pageNumber = 1;
    private List<Enterprise> list;
    private EnterpriseListListViewAdapter adapter;
    private String enterprise_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_collection);
        initBack();
        initTitle("企业收藏");
        ButterKnife.bind(this);

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
                start_Activity(EnterpriseCollectionActivity.this, CompanyDetailsActivity.class, new BasicNameValuePair("id", list.get(i - 1).getEnterprise_id()), new BasicNameValuePair("from", "2"));
            }
        });
        listView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

            @Override
            public void onLoadMoreItems() {
                new Thread(pageRun).start();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                enterprise_id = list.get(position - 1).getEnterprise_id();
                SureOrCancelDialog followDialog = new SureOrCancelDialog(EnterpriseCollectionActivity.this, "是否删除？", new SureOrCancelDialog.SureButtonClick() {
                    @Override
                    public void onSureButtonClick() {
                        new Thread(deleteRun).start();
                    }
                });
                followDialog.show();

                return true;
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
            EnterpriseCollectionResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("pagesize", pagesize);
                data.put("page", "1");
                response = JsonParser.getEnterpriseCollectionResponse(HttpUtil.getMsg(Url.ENTERPRISE_COLLECTION + "?" + HttpUtil.getData(data)));
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
            EnterpriseCollectionResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                response = JsonParser.getEnterpriseCollectionResponse(HttpUtil.getMsg(Url.ENTERPRISE_COLLECTION + "?" + HttpUtil.getData(data)));
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
    //删除企业收藏
    private Runnable deleteRun = new Runnable() {

        public void run() {
            BaseResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uid", getUserID());
                data.put("enterprise_id", enterprise_id);
                response = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data), Url.DELETE_COLLECTION));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null) {
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_OTHER, response));
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
            EnterpriseCollectionActivity activity = (EnterpriseCollectionActivity) reference.get();
            if (activity == null) {
                return;
            }
            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    EnterpriseCollectionResponse response = (EnterpriseCollectionResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                        adapter = new EnterpriseListListViewAdapter(activity, list);
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
                    EnterpriseCollectionResponse pageResponse = (EnterpriseCollectionResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<Enterprise> tempList = pageResponse.getData()
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
                case NETWORK_OTHER:
                    BaseResponse base = (BaseResponse) msg.obj;
                    if (base.isSuccess()) {
                        showSuccessTips("删除成功");
                        new Thread(run).start();
                    } else {
                        showErrorToast(base.getError_desc());
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
