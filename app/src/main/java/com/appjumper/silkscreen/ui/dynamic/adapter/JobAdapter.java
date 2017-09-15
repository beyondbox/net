package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.RecruitList;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 招聘adapter
 * Created by Botx on 2017/5/17.
 */

public class JobAdapter extends BaseQuickAdapter<RecruitList, BaseViewHolder> {

    public JobAdapter(int layoutResId, List<RecruitList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecruitList item) {
        helper.setText(R.id.txtTime, item.getCreate_time().replaceAll("-", "\\.").substring(0, 16))
                .setText(R.id.txtTitle, item.getName())
                .setText(R.id.txtSubTitle, item.getExperience() + "年经验，学历" + item.getEducation() + "，性别" + item.getGender() + "，" + item.getRemark())
                .setText(R.id.txtPrice, item.getSalary().equals("面议") ? item.getSalary() : item.getSalary() + "元")
                .setText(R.id.txtLocation, item.getPlace())
                .setVisible(R.id.unRead, !item.is_read());

        TextView txtMark = helper.getView(R.id.txtMark);
        if (!TextUtils.isEmpty(item.getRecruit_type())) {
            int infoType = Integer.valueOf(item.getRecruit_type());
            switch (infoType) {
                case Const.INFO_TYPE_PER:
                    txtMark.setText("个人");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_person_bg);
                    break;
                case Const.INFO_TYPE_COM:
                    txtMark.setText("企业");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_enterprise_bg);
                    break;
                case Const.INFO_TYPE_OFFICIAL:
                    txtMark.setText("官方");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_official_bg);
                    break;
                default:
                    break;
            }
        }



        TextView txtName = helper.getView(R.id.txtName);

        if (TextUtils.isEmpty(item.getEnterprise_id())) { //个人
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
