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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 丝网订做listview
 */
public class StockListviewAdapter extends BaseAdapter {

  private final LayoutInflater mInflater;
  private final Context mContext;
  private final List<Product> listData;


  public StockListviewAdapter(Context context, List<Product> listData) {
    this.mInflater = LayoutInflater.from(context);
    this.mContext = context;
    this.listData = listData;
  }

  @Override
  public int getCount() {
    return listData.size();
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
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_stock, null);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(viewHolder);
    }
    fillValue(position, viewHolder);
    return convertView;
  }

  private void fillValue(int position, ViewHolder viewHolder) {
    Product item = listData.get(position);
//    if (item.getEnterprise_logo() != null && !item.getEnterprise_logo().getSmall().equals("")) {
      Picasso.with(mContext).load(item.getImg_list().get(0).getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.iv_logo);
//    }
    viewHolder.tv_name.setText(item.getEnterprise_name());

    if (item.getAuth_status() != null && item.getAuth_status().equals("2")) {
      viewHolder.img_auth_status.setVisibility(View.VISIBLE);
    } else {
      viewHolder.img_auth_status.setVisibility(View.GONE);
    }

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

    if(!item.getProduct_alias().equals("")){
      viewHolder.tv_aliasname.setText(" （"+item.getProduct_alias()+"）");
    }else{
      viewHolder.tv_aliasname.setText("");
    }
    viewHolder.tv_name.setText(item.getProduct_name());
    viewHolder.tv_service_size.setText("现货存量：暂无");

    List<Spec> spec = item.getService_spec();
    for(int i=0;i<spec.size();i++){
      if(spec.get(i).getFieldname().equals("cunliang")){
        if (!TextUtils.isEmpty(spec.get(i).getValue()))
          viewHolder.tv_service_size.setText("现货存量："+spec.get(i).getValue()+spec.get(i).getUnit());
      }
    }

    viewHolder.tv_company_name.setText(item.getEnterprise_name());
  }

  static class ViewHolder {
    @Bind(R.id.iv_logo)//logo
            ImageView iv_logo;

    @Bind(R.id.tv_name)//产品名称
            TextView tv_name;

    @Bind(R.id.tv_aliasname)//产品别名
            TextView tv_aliasname;

    @Bind(R.id.tv_service_size)//现货数量
            TextView tv_service_size;

    @Bind(R.id.tv_company_name)//公司名称
            TextView tv_company_name;

    @Bind(R.id.img_auth_status) //个人认证
            ImageView img_auth_status;

    @Bind(R.id.img_enterprise_auth_status)//企
            ImageView img_enterprise_auth_status;

    @Bind(R.id.img_enterprise_productivity_auth_status)//力
            ImageView img_enterprise_productivity_auth_status;

    ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }


}
