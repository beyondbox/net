package com.appjumper.silkscreen.ui.trend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.TrendArticle;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.ui.trend.adapter.ArticleAllAdapter;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 行情分析分章——全部
 * Created by Botx on 2017/5/11.
 */

public class TrendArticleAllActivity extends BaseActivity {

    @Bind(R.id.ptrLayt)
    PtrClassicFrameLayout ptrLayt;
    @Bind(R.id.recyclerArticle)
    RecyclerView recyclerArticle;

    private List<TrendArticle> articleList;
    private ArticleAllAdapter articleAdapter;

    private int page = 1;
    private int pageSize = 20;
    private int totalSize;

    private String type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_article_all);
        ButterKnife.bind(this);

        type = getIntent().getStringExtra("type");

        initBack();
        initTitle("行情分析");
        initRecyclerView();

        ptrLayt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayt.autoRefresh();
            }
        }, 100);
    }


    private void initRecyclerView() {
        articleList = new ArrayList<>();
        articleAdapter = new ArticleAllAdapter(R.layout.item_recycler_line_article_all, articleList);
        recyclerArticle.setLayoutManager(new LinearLayoutManager(context));
        articleAdapter.bindToRecyclerView(recyclerArticle);
        articleAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        articleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (checkLogined()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra(Const.KEY_TOTAL, articleList.get(0).getId());
                    intent.putExtra("id", articleList.get(position).getId());
                    startActivity(intent);
                }
            }
        });

        articleAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getArticle();
            }
        }, recyclerArticle);

        articleAdapter.setEnableLoadMore(false);

        ptrLayt.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 1;
                getArticle();
            }
        });
    }


    /**
     * 获取分析文章
     */
    private void getArticle() {
        RequestParams params = MyHttpClient.getApiParam("tender", "analysis_list");
        params.put("page", page);
        params.put("pagesize", pageSize);
        params.put("type", type);
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
                        List<TrendArticle> list = GsonUtil.getEntityList(dataObj.getJSONArray("items").toString(), TrendArticle.class);
                        totalSize = dataObj.optInt("total");

                        if (page == 1)
                            articleList.clear();
                        articleList.addAll(list);
                        articleAdapter.notifyDataSetChanged();

                        if (articleList.size() < totalSize)
                            articleAdapter.setEnableLoadMore(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
                if (page > 1)
                    page--;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestroyed())
                    return;

                ptrLayt.refreshComplete();
                articleAdapter.loadMoreComplete();
                if (totalSize == articleList.size())
                    articleAdapter.loadMoreEnd();

                articleAdapter.setEmptyView(R.layout.layout_empty_view_common);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        getArticle();
    }
}
