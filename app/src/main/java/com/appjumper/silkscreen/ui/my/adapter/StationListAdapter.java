package com.appjumper.silkscreen.ui.my.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.LineList;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 选择货站列表adapter
 * Created by botx on 2017/9/30.
 */

public class StationListAdapter extends BaseQuickAdapter<LineList, BaseViewHolder> {

    public StationListAdapter(@LayoutRes int layoutResId, @Nullable List<LineList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LineList item) {
        helper.setText(R.id.tv_name, item.getOfficial_name())
                .setText(R.id.txtSubTitle, item.getOfficial_address());
    }

}
