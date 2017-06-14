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
 * Created by Monkey on 2015/6/29.
 */
public class HomeListview1Adapter extends BaseAdapter {

  private final LayoutInflater mInflater;
  private final Context mContext;
  private final List<Enterprise> recommend;


  public HomeListview1Adapter(Context context, List<Enterprise> recommend) {
    this.mInflater = LayoutInflater.from(context);
    this.mContext = context;
    this.recommend = recommend;
  }

  @Override
  public int getCount() {
    return recommend.size();
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
    final ViewHolder holder;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.item_home_listview1, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    fillValue(position, holder);
    return convertView;
  }
  private void fillValue(int position, ViewHolder viewHolder) {
    Enterprise item = recommend.get(position);
    if (item.getEnterprise_logo() != null && !item.getEnterprise_logo().getSmall().equals("")) {
      Picasso.with(mContext).load(item.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.iv_enterprise_logo);
    }
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
    viewHolder.tv_enterprise_address.setText(item.getEnterprise_address());
    viewHolder.tv_company_name.setText(item.getEnterprise_name());
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
      }else{
        viewHolder.tv_service.setText("无服务");
      }
    }
  }
  public  class ViewHolder {
    @Bind(R.id.iv_enterprise_logo)//公司logo
            ImageView iv_enterprise_logo;

    @Bind(R.id.tv_company_name)//公司名称
            TextView tv_company_name;

    @Bind(R.id.tv_enterprise_auth_status)//企
            ImageView tv_enterprise_auth_status;

    @Bind(R.id.tv_enterprise_productivity_auth_status)//力
            ImageView tv_enterprise_productivity_auth_status;

    @Bind(R.id.tv_enterprise_address)//地址
            TextView tv_enterprise_address;

    @Bind(R.id.tv_service)//服务
            TextView tv_service
            ;
    ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }

  }

}
