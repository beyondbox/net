package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Enterprise;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
                .setVisible(R.id.imgViCertiGreen, item.getAuth_status().equals("2"))
                .setVisible(R.id.imgViCertiBlue, item.getEnterprise_auth_status().equals("2"))
                .setVisible(R.id.imgViCertiYellow, item.getEnterprise_productivity_auth_status().equals("2"));

        if (item.getEnterprise_logo() != null && !TextUtils.isEmpty(item.getEnterprise_logo().getSmall())) {
            Picasso.with(mContext)
                    .load(item.getEnterprise_logo().getSmall())
                    .placeholder(R.mipmap.icon_logo_image61)
                    .error(R.mipmap.icon_logo_image61)
                    .into((ImageView) helper.getView(R.id.imgViCom));
        } else {
            ((ImageView) helper.getView(R.id.imgViCom)).setImageResource(R.mipmap.icon_logo_image61);
        }


        String jiagong = item.getJiagong();
        String service = "";
        List<String> list = new ArrayList<>();

        if(!jiagong.equals("0")) {
            list.add("加工");
        }
        if(!item.getDingzuo().equals("0")) {
            list.add("订做");
        }
        if(list.size() > 0) {
            for (int i =0; i < list.size(); i++) {
                if(i != 0) {
                    service += "、";
                }
                service += list.get(i);
            }
            if(!item.getXianhuo().equals("0")) {
                helper.setText(R.id.txtSubTitle, "提供" + service + "服务、有现货");
            } else {
                helper.setText(R.id.txtSubTitle, "提供" + service + "服务");
            }
        } else {
            if(!item.getXianhuo().equals("0")) {
                helper.setText(R.id.txtSubTitle, "有现货");
            }
        }
    }


}
