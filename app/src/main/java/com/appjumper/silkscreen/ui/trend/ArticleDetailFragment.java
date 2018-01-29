package com.appjumper.silkscreen.ui.trend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import butterknife.OnClick;

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
    @Bind(R.id.llPraise)
    LinearLayout llPraise;
    @Bind(R.id.webView)
    WebView webView;

    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.imgViUp)
    ImageView imgViUp;
    @Bind(R.id.imgViDown)
    ImageView imgViDown;
    @Bind(R.id.txtUpNum)
    TextView txtUpNum;
    @Bind(R.id.txtDownNum)
    TextView txtDownNum;

    private int id;
    private TrendArticle article;

    private int upNum; //点赞数
    private int downNum; //点踩数



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
        setWebView();

        progress.show();
        getData();
    }


    private void setWebView() {
        WebSettings settings = webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
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
                //progress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (!isViewCreated) return;

                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        llPraise.setVisibility(View.VISIBLE);
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
                if (!isViewCreated) return;
                showFailTips(getResources().getString(R.string.requst_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!isViewCreated) return;
                progress.dismiss();
            }
        });
    }


    /**
     * 渲染数据
     */
    private void setData() {
        if (!TextUtils.isEmpty(article.getImg())) {
            Picasso.with(context)
                    .load(article.getImg())
                    .into(imageView);
        }

        txtTitle.setText(article.getTitle());
        txtDate.setText(article.getCreate_time());
        //txtContent.setText(Html.fromHtml(article.getContent()));
        webView.loadDataWithBaseURL(null, article.getContent(), "text/html", "utf-8", null);
        txtUpNum.setText("(" + article.getUp() + ")");
        txtDownNum.setText("(" + article.getDown() + ")");

        upNum = Integer.parseInt(article.getUp());
        downNum = Integer.parseInt(article.getDown());
    }


    /**
     * 点赞
     */
    private void up() {
        RequestParams params = MyHttpClient.getApiParam("tender", "analysis_up");
        params.put("uid", getUserID());
        params.put("analysis_id", id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        imgViUp.setImageResource(R.mipmap.praise_blue);
                        imgViDown.setImageResource(R.mipmap.tread_black);
                        getData();
                        CommonApi.addLiveness(getUserID(), 12);
                    } else {
                        showErrorToast(jsonObj.getString("error_desc"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

        });
    }



    /**
     * 点踩
     */
    private void down() {
        RequestParams params = MyHttpClient.getApiParam("tender", "analysis_down");
        params.put("uid", getUserID());
        params.put("analysis_id", id);

        MyHttpClient.getInstance().get(Url.HOST, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int state = jsonObj.getInt(Const.KEY_ERROR_CODE);
                    if (state == Const.HTTP_STATE_SUCCESS) {
                        imgViUp.setImageResource(R.mipmap.praise_black);
                        imgViDown.setImageResource(R.mipmap.tread_blue);
                        getData();
                        CommonApi.addLiveness(getUserID(), 12);
                    } else {
                        showErrorToast(jsonObj.getString("error_desc"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showFailTips(getResources().getString(R.string.requst_fail));
            }

        });
    }



    @OnClick({R.id.imgViUp, R.id.imgViDown})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgViUp:
                up();
                break;
            case R.id.imgViDown:
                down();
                break;
            default:
                break;
        }
    }

}
