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
import com.appjumper.silkscreen.bean.NewPublic;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Monkey on 2015/6/29.
 */
public class HomeListview2Adapter extends BaseAdapter {

  private final LayoutInflater mInflater;
  private final Context mContext;
  private final List<NewPublic> newPublic;


  public HomeListview2Adapter(Context context, List<NewPublic> newPublic) {
    this.mInflater = LayoutInflater.from(context);
    this.mContext = context;
    this.newPublic = newPublic;
  }

  @Override
  public int getCount() {
    if(newPublic!=null){
      return newPublic.size();
    }else {
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
    final ViewHolder holder;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.item_home_listview2, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    fillValue(position, holder);
    return convertView;
  }
  private void fillValue(int position, ViewHolder viewHolder) {
    NewPublic item = newPublic.get(position);
    //Picasso.with(mContext).load(item.getImg().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_enterprise_logo);
    viewHolder.tv_title.setText(item.getTitle());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      Date date = sdf.parse(item.getCreate_time());
      viewHolder.tv_create_time.setText((new SimpleDateFormat("MM.dd HH:mm")).format(date));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    viewHolder.tv_subtitle.setText(item.getSubtitle());
    viewHolder.tv_enterprise_name.setText(item.getEnterprise_name());

    if(item.getIsad()!=null&&!item.getIsad().equals("")){
      int isad = Integer.parseInt(item.getIsad());
      if(isad>0){
        viewHolder.iv_isad.setVisibility(View.VISIBLE);
      }else{
        viewHolder.iv_isad.setVisibility(View.GONE);
      }
    }else{
      viewHolder.iv_isad.setVisibility(View.GONE);
    }

    switch (item.getType()){
      case "1"://订做
        viewHolder.iv_type.setImageResource(R.mipmap.icon_home_order);
        if (item.getImg_list() != null)
          Picasso.with(mContext).load(item.getImg_list().get(0).getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_enterprise_logo);
        break;
      case "2"://加工
        viewHolder.iv_type.setImageResource(R.mipmap.icon_home_machine);
        if (item.getImg_list() != null)
          Picasso.with(mContext).load(item.getImg_list().get(0).getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_enterprise_logo);
        break;
      case "3"://现货
        viewHolder.iv_type.setImageResource(R.mipmap.icon_home_spot);
        if (item.getImg_list() != null)
          Picasso.with(mContext).load(item.getImg_list().get(0).getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_enterprise_logo);
        break;
      case "4"://物流
        viewHolder.iv_type.setImageResource(R.mipmap.icon_home_circuit);
        if (item.getImg() != null)
          Picasso.with(mContext).load(item.getImg().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_enterprise_logo);
        break;
      case "5"://设备
        viewHolder.iv_type.setImageResource(R.mipmap.icon_home_facility);
        if (item.getImg() != null)
          Picasso.with(mContext).load(item.getImg().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_enterprise_logo);
        break;
      case "6"://找车
        viewHolder.iv_type.setImageResource(R.mipmap.icon_home_car);
        if (item.getImg() != null)
          Picasso.with(mContext).load(item.getImg().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_enterprise_logo);
        break;
      case "7"://招聘
        viewHolder.iv_type.setImageResource(R.mipmap.icon_home_recruit);
        if (item.getImg() != null)
          Picasso.with(mContext).load(item.getImg().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_enterprise_logo);
        break;
      case "8"://厂房
        viewHolder.iv_type.setImageResource(R.mipmap.icon_home_plant_information);
        if (item.getImg() != null)
          Picasso.with(mContext).load(item.getImg().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_enterprise_logo);
        break;
    }
  }

  public  class ViewHolder {
    @Bind(R.id.iv_enterprise_logo)//公司logo
            ImageView iv_enterprise_logo;

    @Bind(R.id.tv_title)//深圳-宝安
            TextView tv_title;

    @Bind(R.id.iv_type)//物流路线
            ImageView iv_type;

    @Bind(R.id.tv_create_time)//时间
            TextView tv_create_time;

    @Bind(R.id.tv_subtitle)//西乡-盐田-后海-宝安-南山
            TextView tv_subtitle;

    @Bind(R.id.tv_enterprise_name)//河北省衡水市安平县红旗街东头
            TextView tv_enterprise_name;

    @Bind(R.id.iv_isad)//推广
            ImageView iv_isad
            ;
    ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }

  }


}
