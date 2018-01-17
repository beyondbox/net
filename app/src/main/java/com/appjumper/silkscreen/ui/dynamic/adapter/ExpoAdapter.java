package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Exhibition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Botx on 2018/1/15.
 */

public class ExpoAdapter extends BaseQuickAdapter<Exhibition, BaseViewHolder> {

    public ExpoAdapter(@LayoutRes int layoutResId, @Nullable List<Exhibition> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Exhibition item) {
        helper.setText(R.id.txtTitle, item.getTitle());

        Picasso.with(mContext)
                .load(item.getImg().getSmall())
                .placeholder(R.mipmap.img_error)
                .error(R.mipmap.img_error)
                .into((ImageView) helper.getView(R.id.imageView));
    }

}
