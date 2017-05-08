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
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.ui.home.process.ProcessingActivity;
import com.appjumper.silkscreen.util.CircleTransform;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 丝网加工listview
 */
public class ProcessingListviewAdapter extends BaseAdapter {

  private final LayoutInflater mInflater;
  private final Context mContext;
  private final List<Product> list;


  public ProcessingListviewAdapter(Context context, List<Product> list) {
    this.mInflater = LayoutInflater.from(context);
    this.mContext = context;
    this.list = list;
  }



  @Override
  public int getCount() {
    if(list!=null){
      return list.size();
    }else{
      return 0;

    }
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
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_processing_listview, null);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(viewHolder);
    }
    fillValue(position, viewHolder);
    return convertView;
  }

  private void fillValue(int position, ViewHolder viewHolder) {
    Product item = list.get(position);
    Picasso.with(mContext).load(item.getImg_list().get(0).getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.iv_logo);
//    if (item.getEnterprise_logo() != null && !item.getEnterprise_logo().getSmall().equals("")) {
//      Picasso.with(mContext).load(item.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.iv_logo);
//    }
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

    viewHolder.tv_distance.setText(item.getDistance()+"km");
    viewHolder.tv_aliasname.setText("（"+item.getProduct_alias()+"）");
    viewHolder.tv_name.setText(item.getProduct_name());
    viewHolder.tv_company_name.setText(item.getEnterprise_name());
    viewHolder.tv_service.setText("提供"+item.getService_type_name()+"服务");

  }

  static class ViewHolder {
    @Bind(R.id.iv_logo)//logo
            ImageView iv_logo;

    @Bind(R.id.tv_distance)//距离
            TextView tv_distance;

    @Bind(R.id.tv_name)//产品名称
            TextView tv_name;

    @Bind(R.id.tv_aliasname)//产品别名
            TextView tv_aliasname;

    @Bind(R.id.tv_service)//服务
            TextView tv_service;

    @Bind(R.id.tv_company_name)//公司名称
            TextView tv_company_name;

    @Bind(R.id.img_enterprise_auth_status)//企
            ImageView img_enterprise_auth_status;

    @Bind(R.id.img_enterprise_productivity_auth_status)//力
            ImageView img_enterprise_productivity_auth_status;

    ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }


}
