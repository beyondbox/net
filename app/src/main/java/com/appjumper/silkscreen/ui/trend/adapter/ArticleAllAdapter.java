package com.appjumper.silkscreen.ui.trend.adapter;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.TrendArticle;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Botx on 2017/5/11.
 */

public class ArticleAllAdapter extends BaseQuickAdapter<TrendArticle, BaseViewHolder> {

    public ArticleAllAdapter(int layoutResId, List<TrendArticle> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TrendArticle item) {
        helper.setText(R.id.txtTitle, item.getTitle())
                .setText(R.id.txtDate, item.getCreate_time());
    }

}
