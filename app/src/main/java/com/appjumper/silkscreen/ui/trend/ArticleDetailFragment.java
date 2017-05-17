package com.appjumper.silkscreen.ui.trend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseFragment;
import com.appjumper.silkscreen.bean.TrendArticle;
import com.appjumper.silkscreen.net.CommonApi;
import com.appjumper.silkscreen.net.GsonUtil;
import com.appjumper.silkscreen.net.MyHttpClient;
import com.appjumper.silkscreen.net.Url;
import com.appjumper.silkscreen.util.Const;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 文章详情
 * Created by Botx on 2017/5/11.
 */

public class ArticleDetailFragment extends BaseFragment {

    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.txtDate)
    TextView txtDate;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.txtContent)
    TextView txtContent;

    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.title)
    TextView title;

    private int id;
    private TrendArticle article;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, view);
        back.setVisibility(View.GONE);
        title.setText("行情分析");
        return view;
    }


    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        id = bundle.getInt("id");

        initProgressDialog();
        getData();
    }


    /**
     * 获取文章详情
     */
    private void getData() {
        RequestParams params = MyHttpClient.getApiParam("tender", "analysis_details");
        params.put("uid", getUserID());
        params.put("analysis_id", id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        article = GsonUtil.getEntity(jsonObj.getJSONObject("data").toString(), TrendArticle.class);
                        setData();

                        CommonApi.addLiveness(getUserID(), 11);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progress.dismiss();
            }
        });
    }


    /**
     * 渲染数据
     */
    private void setData() {
        Picasso.with(context)
                .load(article.getImg())
                .into(imageView);

        txtTitle.setText(article.getTitle());
        txtDate.setText(article.getCreate_time());
        txtContent.setText(Html.fromHtml(article.getContent()));
    }

}
