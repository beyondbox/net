package com.appjumper.silkscreen.ui.dynamic.adapter;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.AttentProduct;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 关注的产品adapter
 * Created by Botx on 2017/4/5.
 */

public class AttentProductAdapter extends BaseQuickAdapter<AttentProduct, BaseViewHolder> {


    public AttentProductAdapter(int layoutResId, List<AttentProduct> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, AttentProduct item) {
        helper.setText(R.id.txtName, item.getProduct_name() + item.getService_name());
    }

}
