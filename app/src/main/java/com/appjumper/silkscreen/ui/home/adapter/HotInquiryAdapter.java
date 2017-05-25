package com.appjumper.silkscreen.ui.home.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.HotInquiry;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 热门产品询价adapter
 * Created by Botx on 2017/4/17.
 */

public class HotInquiryAdapter extends BaseQuickAdapter<HotInquiry, BaseViewHolder> {

    public HotInquiryAdapter(int layoutResId, List<HotInquiry> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HotInquiry item) {
        ImageView imageView = helper.getView(R.id.imgViProduct);
        if (TextUtils.isEmpty(item.getProductimg())) {
            imageView.setImageResource(R.mipmap.img_error);
        } else {
            Picasso.with(mContext)
                    .load(item.getProductimg())
                    .placeholder(R.mipmap.img_error)
                    .error(R.mipmap.img_error)
                    .into(imageView);
        }

        helper.setText(R.id.txtName, item.getProductname())
                .setText(R.id.txtCount, "今日报价" + item.getShuliang() + "次");
    }


}
