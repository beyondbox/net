package com.appjumper.silkscreen.ui.trend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.ui.common.adapter.ViewPagerFragAdapter;
import com.appjumper.silkscreen.util.Const;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 文章详情
 * Created by Botx on 2017/5/11.
 */

public class ArticleDetailActivity extends BaseActivity {

    @Bind(R.id.pagerArticle)
    ViewPager pagerArticle;
    @Bind(R.id.llBottomBar)
    LinearLayout llBottomBar;

    private int total;
    private int currId;

    private List<Fragment> fragList;
    private ViewPagerFragAdapter pagerAdapter;

    private int currItem;  //当前viewPager的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        total = Integer.parseInt(intent.getStringExtra(Const.KEY_TOTAL));
        currId = Integer.parseInt(intent.getStringExtra("id"));

        initViewPager();
    }


    private void initViewPager() {
        fragList = new ArrayList<>();
        int mark = -1;
        for (int i = total; i > 0; i--) {
            mark++;
            ArticleDetailFragment fragment = new ArticleDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", i);
            fragment.setArguments(bundle);
            fragList.add(fragment);

            if (i == currId)
                currItem = mark;
        }

        pagerAdapter = new ViewPagerFragAdapter(getSupportFragmentManager(), fragList, null);
        pagerArticle.setOffscreenPageLimit(9);
        pagerArticle.setAdapter(pagerAdapter);

        pagerArticle.setCurrentItem(currItem);
    }


    @OnClick({R.id.txtPrevious, R.id.txtNext, R.id.txtCatalogue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtPrevious: //上一篇
                if (pagerArticle.getCurrentItem() == 0)
                    showErrorToast("已经是第一篇了");
                pagerArticle.setCurrentItem(pagerArticle.getCurrentItem() - 1);
                break;

            case R.id.txtNext: //下一篇
                if (pagerArticle.getCurrentItem() == total - 1)
                    showErrorToast("已经是最后一篇了");
                pagerArticle.setCurrentItem(pagerArticle.getCurrentItem() + 1);
                break;

            case R.id.txtCatalogue: //目录
                if (TrendArticleAllActivity.instance == null)
                    start_Activity(context, TrendArticleAllActivity.class, new BasicNameValuePair("type", "1"));
                finish();
                break;
            default:
                break;
        }
    }

}
