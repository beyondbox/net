package com.appjumper.silkscreen.ui.trend.adapter;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.MaterProduct;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 我的关注adapter
 * Created by Botx on 2017/4/25.
 */

public class AttentionMyAdapter extends BaseQuickAdapter<MaterProduct, BaseViewHolder> {

    public AttentionMyAdapter(int layoutResId, List<MaterProduct> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaterProduct item) {
        helper.setText(R.id.tv, item.getName())
                .addOnClickListener(R.id.img_edit);

        if (helper.getAdapterPosition() == 0) {
            helper.setVisible(R.id.img_edit, false);
            helper.setTextColor(R.id.tv, mContext.getResources().getColor(R.color.orange_color));
        } else {
            helper.setVisible(R.id.img_edit, true);
            helper.setTextColor(R.id.tv, mContext.getResources().getColor(R.color.text_black_color));
        }
    }

}
