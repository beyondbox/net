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

package com.appjumper.silkscreen.ui.trend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.OfferList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 原材价格
 */
public class DetailsListViewAdapter extends BaseAdapter {

    private final List<OfferList> offer_list;
    private LayoutInflater mInflater;
    private Context mContext;

    public DetailsListViewAdapter(Context context, List<OfferList> offer_list) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.offer_list = offer_list;
    }

    @Override
    public int getCount() {
        return offer_list.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_details, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        OfferList item = offer_list.get(position);
        viewHolder.tv_company_name.setText(item.getCompany_name());
        viewHolder.tv_offer_time.setText(item.getOffer_time());
        if(item.getOffer_value_tax()!=null&&!item.getOffer_value_tax().equals("0")){
            viewHolder.tv_offer_value.setVisibility(View.VISIBLE);
            viewHolder.tv_offer_value.setText(item.getOffer_value_tax()+item.getOffer_unit()+"（含税价）");
        }else{
            viewHolder.tv_offer_value.setVisibility(View.GONE);
        }

        if(item.getOffer_value()!=null&&!item.getOffer_value().equals("0")){
            viewHolder.tv_offer_value_tax.setVisibility(View.VISIBLE);
            viewHolder.tv_offer_value_tax.setText(item.getOffer_value()+item.getOffer_unit()+"（不含税）");
        }else{
            viewHolder.tv_offer_value_tax.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_company_name)
        TextView tv_company_name;

        @Bind(R.id.tv_offer_time)
        TextView tv_offer_time;

        @Bind(R.id.tv_offer_value)
        TextView tv_offer_value;

        @Bind(R.id.tv_offer_value_tax)
        TextView tv_offer_value_tax;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
