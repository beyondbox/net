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

package com.appjumper.silkscreen.ui.my.adapter;

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
 * 我的物流货站listview
 */
public class MyLogisticsListviewAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<LineList> list;


    public MyLogisticsListviewAdapter(Context context, List<LineList> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if(list==null){
            return 0;
        }else{
            return list.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_logistics_listview, null);
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
                Picasso.with(mContext).load(item.getAvatar().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error_head).error(R.mipmap.img_error_head).into(viewHolder.img_logo);
            }
        } else {
            if (item.getEnterprise_logo() != null && !item.getEnterprise_logo().getSmall().equals("")) {
                Picasso.with(mContext).load(item.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.img_error_head).error(R.mipmap.img_error_head).into(viewHolder.img_logo);
            }

        }

        viewHolder.tv_form_to.setText("线路：" + item.getFrom() + "-" + item.getTo());
        viewHolder.tv_passby.setText("途径：" + item.getPassby_name());
    }

    static class ViewHolder {
        @Bind(R.id.img_logo)
        ImageView img_logo;


        @Bind(R.id.tv_form_to)
        TextView tv_form_to;

        @Bind(R.id.tv_passby)
        TextView tv_passby;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
