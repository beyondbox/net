package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 找车的货物adapter
 * Created by Botx on 2017/5/17.
 */

public class FindCarAdapter extends BaseQuickAdapter<LineList, BaseViewHolder> {

    public FindCarAdapter(int layoutResId, List<LineList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LineList item) {
        helper.setText(R.id.txtTime, item.getCreate_time().replaceAll("-", "\\.").substring(0, 16))
                .setText(R.id.txtTitle, item.getFrom() + " - " + item.getTo())
                .setText(R.id.txtSubTitle, "数量: " + item.getNumber()+"  重量: "+item.getWeight()+"  装货时间: " + item.getDate().replaceAll("-", "\\.").substring(5, 16))
                .setVisible(R.id.unRead, !item.is_read());

        TextView txtName = helper.getView(R.id.txtName);

        if (TextUtils.isEmpty(item.getEnterprise_name())) { //个人
            Picasso.with(mContext)
                    .load(item.getAvatar().getSmall())
                    .placeholder(R.mipmap.img_error_head)
                    .error(R.mipmap.img_error_head)
                    .into((ImageView) helper.getView(R.id.imgViHead));

            txtName.setMaxWidth(DisplayUtil.dip2px(mContext, 138));

            helper.setText(R.id.txtName, item.getUser_nicename())
                    .setVisible(R.id.imgViCertiGreen, item.getAuth_status().equals("2"))
                    .setVisible(R.id.imgViCertiBlue, false)
                    .setVisible(R.id.imgViCertiYellow, false);


        } else { //企业
            Picasso.with(mContext)
                    .load(item.getEnterprise_logo().getSmall())
                    .placeholder(R.mipmap.icon_logo_image61)
                    .error(R.mipmap.icon_logo_image61)
                    .into((ImageView) helper.getView(R.id.imgViHead));

            if (item.getEnterprise_auth_status().equals("2"))
                txtName.setMaxWidth(DisplayUtil.dip2px(mContext, 98));
            else
                txtName.setMaxWidth(DisplayUtil.dip2px(mContext, 138));

            helper.setText(R.id.txtName, item.getEnterprise_name())
                    .setVisible(R.id.imgViCertiGreen, item.getAuth_status().equals("2"))
                    .setVisible(R.id.imgViCertiBlue, item.getEnterprise_auth_status().equals("2"))
                    .setVisible(R.id.imgViCertiYellow, item.getEnterprise_productivity_auth_status().equals("2"));
        }

    }

}
