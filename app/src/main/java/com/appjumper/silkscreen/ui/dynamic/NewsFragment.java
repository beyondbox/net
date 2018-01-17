package com.appjumper.silkscreen.ui.dynamic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.Exhibition;
import com.appjumper.silkscreen.bean.News;
import com.appjumper.silkscreen.bean.Tender;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.common.WebViewActivity;
import com.appjumper.silkscreen.ui.dynamic.adapter.ExpoAdapter;
import com.appjumper.silkscreen.ui.home.adapter.NewsListViewAdapter;
import com.appjumper.silkscreen.ui.home.adapter.TenderListViewAdapter;
import com.appjumper.silkscreen.ui.home.exhibition.ExhibitionActivity;
import com.appjumper.silkscreen.ui.home.news.NewsActivity;
import com.appjumper.silkscreen.ui.home.tender.TenderActivity;
import com.appjumper.silkscreen.ui.home.tender.TenderDetailsActivity;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 新闻
 * Created by Botx on 2018/1/12.
 */

public class NewsFragment extends BaseFragment {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerExpo)
    RecyclerView recyclerExpo;
    @Bind(R.id.lvTender)
    ListView lvTender;
    @Bind(R.id.lvNews)
    ListView lvNews;

    private List<Exhibition> expoList;
    private List<Tender> tenderList;
    private List<News> newsList;

    private ExpoAdapter expoAdapter;
    private TenderListViewAdapter tenderAdapter;
    private NewsListViewAdapter newsAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        initRecyclerView();
        initRefreshLayout();

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 50);
    }


    private void initRecyclerView() {
        expoList = new ArrayList<>();
        tenderList = new ArrayList<>();
        newsList = new ArrayList<>();

        expoAdapter = new ExpoAdapter(R.layout.item_recycler_line_expo, expoList);
        recyclerExpo.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        expoAdapter.bindToRecyclerView(recyclerExpo);
        expoAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        expoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (checkLogined()) {
                    getExpoDetail(expoList.get(position).getId());
                    start_Activity(context, WebViewActivity.class, new BasicNameValuePair("url", expoList.get(position).getUrl()), new BasicNameValuePair("title", "详情"));
                    CommonApi.addLiveness(getUserID(), 8);
                }
            }
        });

        tenderAdapter = new TenderListViewAdapter(context, tenderList, "1");
        lvTender.setDividerHeight(0);
        lvTender.setAdapter(tenderAdapter);
        lvTender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!checkLogined()) return;
                start_Activity(context, TenderDetailsActivity.class, new BasicNameValuePair("id", tenderList.get(i).getId()));

                tenderList.get(i).setIs_read(true);
                tenderAdapter.notifyDataSetChanged();
            }
        });

        newsAdapter = new NewsListViewAdapter(context, newsList);
        lvNews.setDividerHeight(0);
        lvNews.setAdapter(newsAdapter);
        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkLogined()) {
                    getNewsDetail(newsList.get(i).getId());
                    start_Activity(context, WebViewActivity.class, new BasicNameValuePair("url", newsList.get(i).getUrl()), new BasicNameValuePair("title", "详情"));

                    newsList.get(i).setIs_read(true);
                    newsAdapter.notifyDataSetChanged();

                    CommonApi.addLiveness(getUserID(), 9);
                }
            }
        });
    }


    /**
     * 设置下拉刷新
     */
    private void initRefreshLayout() {
        ptrLayt.disableWhenHorizontalMove(true);
        ptrLayt.setLastUpdateTimeRelateObject(this);
        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getData();
            }
        });
    }


    /**
     * 获取数据
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("expo", "news_list");
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        List<Exhibition> eList = GsonUtil.getEntityList(dataObj.getJSONArray("expo").toString(), Exhibition.class);
                        List<Tender> tList = GsonUtil.getEntityList(dataObj.getJSONArray("tender").toString(), Tender.class);
                        List<News> nList = GsonUtil.getEntityList(dataObj.getJSONArray("news").toString(), News.class);

                        newsList.clear();
                        newsList.addAll(nList);
                        newsAdapter.notifyDataSetChanged();

                        expoList.clear();
                        expoList.addAll(eList);
                        expoAdapter.notifyDataSetChanged();

                        tenderList.clear();
                        tenderList.addAll(tList);
                        tenderAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (!isViewCreated) return;
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isViewCreated) return;
                ptrLayt.refreshComplete();
            }
        });
    }


    /**
     * 展会详情接口，调一下消除未读
     */
    private void getExpoDetail(String id) {
        RequestParams params = MyHttpClient.getApiParam("expo", "details");
        params.put("id", id);
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    /**
     * 新闻详情接口，调一下消除未读
     */
    private void getNewsDetail(String id) {
        RequestParams params = MyHttpClient.getApiParam("news", "details");
        params.put("id", id);
        params.put("uid", getUserID());

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    @OnClick({R.id.txtMore0, R.id.txtMore1, R.id.txtMore2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtMore0:
                start_Activity(getActivity(), ExhibitionActivity.class);
                break;
            case R.id.txtMore1:
                start_Activity(getActivity(), TenderActivity.class);
                break;
            case R.id.txtMore2:
                start_Activity(getActivity(), NewsActivity.class);
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
