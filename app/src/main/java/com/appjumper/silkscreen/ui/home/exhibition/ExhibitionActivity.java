package com.appjumper.silkscreen.ui.home.exhibition;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.bean.Exhibition;
import com.appjumper.silkscreen.bean.ExhibitionListResponse;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.home.adapter.ExhibitionListViewAdapter;
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
 * 展会信息
 */
public class ExhibitionActivity extends BaseActivity {

    @Bind(R.id.listview)
    PullToRefreshPagedListView pullToRefreshView;

    private PagedListView listView;
    private View mEmptyLayout;
    private String pagesize = "30";
    private int pageNumber = 1;
    private List<Exhibition> list;
    private ExhibitionListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibition);
        initBack();
        initTitle("展会信息");
        ButterKnife.bind(this);

        listView = pullToRefreshView.getRefreshableView();
        mEmptyLayout = LayoutInflater.from(this).inflate(R.layout.pull_listitem_empty_padding, null);
        pullToRefreshView.setEmptyView(mEmptyLayout);

        refresh();

        initLocation();
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        latitude = aMapLocation.getLatitude();//获取纬度
                        longitude = aMapLocation.getLongitude();//获取经度
                        accuracy = aMapLocation.getAccuracy();//获取精度信息
                        refresh();
                        if(mlocationClient!=null){
                            mlocationClient.stopLocation();
                        }
                    }
                }

            }
        });
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
                start_Activity(ExhibitionActivity.this, WebViewActivity.class,new BasicNameValuePair("url",list.get(i-1).getUrl()),new BasicNameValuePair("title","详情"));
                CommonApi.addLiveness(getUserID(), 8);
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
            ExhibitionListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("pagesize", pagesize);
                data.put("page", "1");
                data.put("lat", latitude+"");
                data.put("lng", longitude+"");
                response = JsonParser.getExhibitionListResponse(HttpUtil.getMsg(Url.EXPOLIST + "?" + HttpUtil.getData(data)));
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
            ExhibitionListResponse response = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("pagesize", pagesize);
                data.put("page", "" + pageNumber);
                data.put("lat", latitude+"");
                data.put("lng", longitude+"");
                response = JsonParser.getExhibitionListResponse(HttpUtil.getMsg(Url.EXPOLIST + "?" + HttpUtil.getData(data)));
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
            final ExhibitionActivity activity = (ExhibitionActivity) reference.get();
            if (activity == null) {
                return;
            }
            pullToRefreshView.onRefreshComplete();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    ExhibitionListResponse response = (ExhibitionListResponse) msg.obj;
                    if (response.isSuccess()) {
                        list = response.getData().getItems();
                         adapter = new ExhibitionListViewAdapter(ExhibitionActivity.this,list);
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
                    ExhibitionListResponse pageResponse = (ExhibitionListResponse) msg.obj;
                    if (pageResponse.isSuccess()) {
                        List<Exhibition> tempList = pageResponse.getData()
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
