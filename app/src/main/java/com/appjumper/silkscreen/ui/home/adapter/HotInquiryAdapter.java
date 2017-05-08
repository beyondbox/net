package com.appjumper.silkscreen.ui.home.adapter;

import com.appjumper.silkscreen.bean.MyInquiry;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 热门产品询价adapter
 * Created by Botx on 2017/4/17.
 */

public class HotInquiryAdapter extends BaseQuickAdapter<MyInquiry, BaseViewHolder> {

    public HotInquiryAdapter(int layoutResId, List<MyInquiry> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyInquiry item) {

    }
}
