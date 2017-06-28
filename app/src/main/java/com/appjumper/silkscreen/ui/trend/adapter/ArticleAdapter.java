package com.appjumper.silkscreen.ui.trend.adapter;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.TrendArticle;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Botx on 2017/5/11.
 */

public class ArticleAdapter extends BaseQuickAdapter<TrendArticle, BaseViewHolder> {

    public ArticleAdapter(int layoutResId, List<TrendArticle> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TrendArticle item) {
        helper.setText(R.id.txtTitle, item.getTitle())
                .setText(R.id.txtDate, item.getCreate_time().substring(5, 10))
                .setVisible(R.id.unRead, !item.is_read());
    }

}
