package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Enterprise;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 关注的企业adapter
 * Created by Botx on 2017/3/29.
 */

public class AttentCompanyAdapter extends BaseQuickAdapter<Enterprise, BaseViewHolder> {


    public AttentCompanyAdapter(int layoutResId, List<Enterprise> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Enterprise item) {
        helper.addOnClickListener(R.id.txtCancelAttent)
                .setText(R.id.txtTitle, item.getEnterprise_name())
                .setText(R.id.txtDate, "入驻时间: " + item.getCreate_time().substring(0, 10))
                .setText(R.id.txtDistance, item.getDistance() + "km")
                .setVisible(R.id.imgViCertiBlue, item.getEnterprise_auth_status().equals("2"))
                .setVisible(R.id.imgViCertiYellow, item.getEnterprise_productivity_auth_status().equals("2"));

        Picasso.with(mContext)
                .load(item.getEnterprise_logo().getSmall())
                .placeholder(R.mipmap.icon_logo_image61)
                .error(R.mipmap.icon_logo_image61)
                .into((ImageView) helper.getView(R.id.imgViCom));
    }


}
