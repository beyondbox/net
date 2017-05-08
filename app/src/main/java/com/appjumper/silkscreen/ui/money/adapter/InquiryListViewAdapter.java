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

package com.appjumper.silkscreen.ui.money.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Myoffer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 询价详情企业列表
 */
public class InquiryListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Myoffer> list;

    public InquiryListViewAdapter(Context context, List<Myoffer> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_inquiry, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        Myoffer item = list.get(position);
        viewHolder.tv_company_name.setText(item.getEnterprise_name());
        if (item.getEnterprise_auth_status() != null && item.getEnterprise_auth_status().equals("2")) {
            viewHolder.tv_enterprise_auth_status.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_enterprise_auth_status.setVisibility(View.GONE);
        }

        if (item.getEnterprise_productivity_auth_status() != null && item.getEnterprise_productivity_auth_status().equals("2")) {
            viewHolder.tv_enterprise_productivity_auth_status.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_enterprise_productivity_auth_status.setVisibility(View.GONE);
        }
        viewHolder.tvCreateTime.setText(item.getCreate_time().substring(5, 16));
        viewHolder.tvOffer.setText("￥" + item.getMoney());

    }

    static class ViewHolder {
        @Bind(R.id.tv_company_name)//公司名称
                TextView tv_company_name;

        @Bind(R.id.tv_enterprise_auth_status)//企
                ImageView tv_enterprise_auth_status;

        @Bind(R.id.tv_enterprise_productivity_auth_status)//力
                ImageView tv_enterprise_productivity_auth_status;
        @Bind(R.id.tv_offer)//报价
                TextView tvOffer;
        @Bind(R.id.tv_create_time)//发布时间
                TextView tvCreateTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
