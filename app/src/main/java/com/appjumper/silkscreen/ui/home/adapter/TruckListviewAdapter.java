/*
 *
 *  *
 *  *  *
 *  *  *  * ===================================
 *  *  *  * Copyright (c) 2016.
 *  *  *  * 作者：安卓猴
 *  *  *  * 微博：@安卓猴
 *  *  *  * 博客：http://sunjiajia.com
 *  *  *  * Github：https://github.com/opengit
 *  *  *  *
 *  *  *  * 注意**：如果您使用或者修改该代码，请务必保留此版权信息。
 *  *  *  * ===================================
 *  *  *
 *  *  *
 *  *
 *
 */

package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 找车详情
 */
public class TruckListviewAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private List<LineList> list;

    public TruckListviewAdapter(Context context, List<LineList> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_truck, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        LineList item = list.get(position);

        if (item.getEnterprise_name().equals("")) {
            if (item.getAvatar() != null && !item.getAvatar().getSmall().equals("")) {
                Picasso.with(mContext).load(item.getAvatar().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.ivLogo);
            }
            viewHolder.tvName.setText(item.getUser_nicename());
            viewHolder.img_enterprise_auth_status.setVisibility(View.GONE);
            viewHolder.img_enterprise_productivity_auth_status.setVisibility(View.GONE);
        } else {
            if (item.getEnterprise_logo() != null && !item.getEnterprise_logo().getSmall().equals("")) {
                Picasso.with(mContext).load(item.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.ivLogo);
            }
            viewHolder.tvName.setText(item.getEnterprise_name());
            if (item.getEnterprise_auth_status() != null && item.getEnterprise_auth_status().equals("2")) {
                viewHolder.img_enterprise_auth_status.setVisibility(View.VISIBLE);
            } else {
                viewHolder.img_enterprise_auth_status.setVisibility(View.GONE);
            }

            if (item.getEnterprise_productivity_auth_status() != null && item.getEnterprise_productivity_auth_status().equals("2")) {
                viewHolder.img_enterprise_productivity_auth_status.setVisibility(View.VISIBLE);
            } else {
                viewHolder.img_enterprise_productivity_auth_status.setVisibility(View.GONE);
            }
        }

//        if (item.getAvatar() != null && !item.getAvatar().getSmall().equals("")) {
//            Picasso.with(mContext).load(item.getAvatar().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.ivLogo);
//        }
//        viewHolder.tvName.setText(item.getUser_nicename());

        viewHolder.tvDate.setText(item.getCreate_time());
        viewHolder.tvPathway.setText( item.getFrom() + "-" + item.getTo());

        viewHolder.tvDetail.setText("数量：" + item.getNumber()+"  重量："+item.getWeight()+"  装货时间："+item.getDate().substring(5));
    }

    static class ViewHolder {
        @Bind(R.id.iv_logo)//图片
                ImageView ivLogo;
        @Bind(R.id.tv_name)//车主或企业
                TextView tvName;

        @Bind(R.id.img_enterprise_auth_status)
        ImageView img_enterprise_auth_status;

        @Bind(R.id.img_enterprise_productivity_auth_status)
        ImageView img_enterprise_productivity_auth_status;
        @Bind(R.id.tv_pathway)//途径地
                TextView tvPathway;
        @Bind(R.id.tv_detail)//装载详情
                TextView tvDetail;
        @Bind(R.id.tv_date)//日期
                TextView tvDate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
