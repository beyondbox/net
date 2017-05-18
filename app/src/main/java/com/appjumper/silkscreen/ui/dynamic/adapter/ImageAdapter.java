package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 图片adapter
 * Created by Botx on 2017/5/18.
 */

public class ImageAdapter extends BaseQuickAdapter<Avatar, BaseViewHolder> {

    public ImageAdapter(int layoutResId, List<Avatar> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Avatar item) {
        Picasso.with(mContext)
                .load(item.getSmall())
                .placeholder(R.mipmap.img_error)
                .error(R.mipmap.img_error)
                .into((ImageView)helper.getView(R.id.imageView));
    }

}
