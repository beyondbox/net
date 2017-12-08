package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.HotInquiry;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 求购顶部筛选适配器
 * Created by Botx on 2017/12/7.
 */

public class AskBuyFilterAdapter extends BaseQuickAdapter<HotInquiry, BaseViewHolder> {

    private int seletedPosition = -1;

    public AskBuyFilterAdapter(@LayoutRes int layoutResId, @Nullable List<HotInquiry> data) {
        super(layoutResId, data);
    }

    public void changeSelected(int position) {
        seletedPosition = position;
        notifyDataSetChanged();
    }


    @Override
    protected void convert(BaseViewHolder helper, HotInquiry item) {
        helper.setText(R.id.txtName, item.getProduct_name() + "(" + item.getShuliang() + ")");

        LinearLayout llGroup = helper.getView(R.id.llGroup);
        View mark = helper.getView(R.id.mark);
        if (helper.getAdapterPosition() == seletedPosition) {
            llGroup.setSelected(true);
            mark.setVisibility(View.VISIBLE);
        } else {
            llGroup.setSelected(false);
            mark.setVisibility(View.INVISIBLE);
        }
    }

}
