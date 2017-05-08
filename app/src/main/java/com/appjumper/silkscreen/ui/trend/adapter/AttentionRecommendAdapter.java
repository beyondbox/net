package com.appjumper.silkscreen.ui.trend.adapter;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.MaterProduct;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 关注推荐adapter
 * Created by Botx on 2017/4/25.
 */

public class AttentionRecommendAdapter extends BaseQuickAdapter<MaterProduct, BaseViewHolder> {

    public AttentionRecommendAdapter(int layoutResId, List<MaterProduct> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaterProduct item) {
        helper.setText(R.id.tv, item.getName())
                .setVisible(R.id.img_edit, false);
    }

}
