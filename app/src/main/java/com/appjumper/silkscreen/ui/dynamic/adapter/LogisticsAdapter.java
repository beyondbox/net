package com.appjumper.silkscreen.ui.dynamic.adapter;

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
 * 物流adapter
 * Created by Botx on 2017/5/17.
 */

public class LogisticsAdapter extends BaseQuickAdapter<LineList, BaseViewHolder> {

    public LogisticsAdapter(int layoutResId, List<LineList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LineList item) {
        helper.setText(R.id.txtTime, item.getUpdate_time().replaceAll("-", "\\.").substring(0, 16))
                .setText(R.id.txtTitle, "线路: " + item.getFrom() + " - " + item.getTo())
                .setText(R.id.txtSubTitle, "途径: " + item.getPassby_name());

        TextView txtName = helper.getView(R.id.txtName);

        if (item.getType().equals("2")) { //个人
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
                    .placeholder(R.mipmap.img_error_head)
                    .error(R.mipmap.img_error_head)
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
