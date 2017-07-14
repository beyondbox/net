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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.News;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import q.rorbin.badgeview.QBadgeView;


/**
 * Created by yc on 2015/6/29.
 * 行业新闻
 */
public class NewsListViewAdapter extends BaseAdapter {

    private final List<News> list;
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean once;

    public NewsListViewAdapter(Context context, List<News> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_news, null);
            viewHolder = new ViewHolder(convertView);
            viewHolder.badgeView = new QBadgeView(mContext);
            viewHolder.badgeView.bindTarget(viewHolder.tv_title).setBadgeGravity(Gravity.START | Gravity.TOP).setGravityOffset(0, true).setBadgePadding(3, true);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        News item = list.get(position);

        if (item.is_read())
            viewHolder.badgeView.setBadgeNumber(0);
        else
            viewHolder.badgeView.setBadgeNumber(-1);

        if(item.getImg()!=null){
            Picasso.with(mContext).load(item.getImg().getOrigin()).placeholder(R.mipmap.img_error).error(R.mipmap.img_error).into(viewHolder.iv_img);
        }
        viewHolder.tv_title.setText(item.getTitle());
        viewHolder.tv_create_time.setText(item.getCreate_time());
    }

    static class ViewHolder {

        @Bind(R.id.iv_img)
        ImageView iv_img;

        @Bind(R.id.tv_title)
        TextView tv_title;

        @Bind(R.id.tv_create_time)
        TextView tv_create_time;

        QBadgeView badgeView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
