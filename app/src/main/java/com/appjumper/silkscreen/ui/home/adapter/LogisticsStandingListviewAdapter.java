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
import com.appjumper.silkscreen.util.CircleTransform;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 物流货站listview
 */
public class LogisticsStandingListviewAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<LineList> list;


    public LogisticsStandingListviewAdapter(Context context, List<LineList> list) {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_logistics_standing_listview, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        LineList item = list.get(position);
        if (item.getType().equals("2")) {
            if (item.getAvatar() != null && !item.getAvatar().getSmall().equals("")) {
                Picasso.with(mContext).load(item.getAvatar().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.img_logo);
            }
            viewHolder.tv_name.setText(item.getUser_nicename());
            viewHolder.img_enterprise_auth_status.setVisibility(View.GONE);
            viewHolder.img_enterprise_productivity_auth_status.setVisibility(View.GONE);
        } else {
            if (item.getEnterprise_logo() != null && !item.getEnterprise_logo().getSmall().equals("")) {
                Picasso.with(mContext).load(item.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.img_logo);
            }
            viewHolder.tv_name.setText(item.getEnterprise_name());
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

        viewHolder.tv_update_time.setText(item.getUpdate_time());
        viewHolder.tv_form_to.setText("线路：" + item.getFrom() + "-" + item.getTo());
        viewHolder.tv_passby.setText("途径：" + item.getPassby_name());
    }

    static class ViewHolder {
        @Bind(R.id.img_logo)
        ImageView img_logo;

        @Bind(R.id.tv_name)
        TextView tv_name;

        @Bind(R.id.img_enterprise_auth_status)
        ImageView img_enterprise_auth_status;

        @Bind(R.id.img_enterprise_productivity_auth_status)
        ImageView img_enterprise_productivity_auth_status;

        @Bind(R.id.tv_update_time)
        TextView tv_update_time;

        @Bind(R.id.tv_form_to)
        TextView tv_form_to;

        @Bind(R.id.tv_passby)
        TextView tv_passby;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
