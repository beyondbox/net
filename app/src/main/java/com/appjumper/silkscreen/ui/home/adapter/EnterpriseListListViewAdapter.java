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
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 企业列表
 */
public class EnterpriseListListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Enterprise> list;

    public EnterpriseListListViewAdapter(Context context, List<Enterprise> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_enterprise_list, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        Enterprise item = list.get(position);
        if (item.getEnterprise_logo() != null && !item.getEnterprise_logo().getSmall().equals("")) {
            Picasso.with(mContext).load(item.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.iv_enterprise_logo);
        }
        viewHolder.tv_company_name.setText(item.getEnterprise_name());

        if (item.getAuth_status() != null && item.getAuth_status().equals("2")) {
            viewHolder.tv_auth_status.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_auth_status.setVisibility(View.GONE);
        }


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
        String jiagong = item.getJiagong();
        String service="";
        List<String> list = new ArrayList<>();

        if(!jiagong.equals("0")) {
            list.add("加工");
        }
        if(!item.getDingzuo().equals("0")) {
            list.add("订做");
        }
        if(list.size()>0){
            for (int i =0;i<list.size();i++) {
                if(i!=0){
                    service +="、";
                }
                service +=list.get(i);
            }
            if(!item.getXianhuo().equals("0")) {
                viewHolder.tv_service.setText("提供"+service+"服务、有现货");
            }else {
                viewHolder.tv_service.setText("提供"+service+"服务");
            }
        }else{
            if(!item.getXianhuo().equals("0")) {
                viewHolder.tv_service.setText("有现货");
            }
        }
        viewHolder.tvDate.setText("入驻时间"+item.getCreate_time().substring(0,10));
        viewHolder.tvDistance.setText(item.getDistance()+"km");
    }

    static class ViewHolder {
        @Bind(R.id.iv_enterprise_logo)//公司logo
                ImageView iv_enterprise_logo;

        @Bind(R.id.tv_company_name)//公司名称
                TextView tv_company_name;

        @Bind(R.id.tv_auth_status)//个人认证
                ImageView tv_auth_status;

        @Bind(R.id.tv_enterprise_auth_status)//企
                ImageView tv_enterprise_auth_status;

        @Bind(R.id.tv_enterprise_productivity_auth_status)//力
                ImageView tv_enterprise_productivity_auth_status;


        @Bind(R.id.tv_service)//服务
                TextView tv_service;
        @Bind(R.id.tv_date)//入驻时间
                TextView tvDate;
        @Bind(R.id.tv_distance)//距离
                TextView tvDistance;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
