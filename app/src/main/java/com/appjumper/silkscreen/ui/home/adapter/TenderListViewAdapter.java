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
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Tender;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 招标中标
 */
public class TenderListViewAdapter extends BaseAdapter {

    private final List<Tender> list;
    private final String type;
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean once;

    public TenderListViewAdapter(Context context, List<Tender> list, String type) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = list;
        this.type = type;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_tender, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {

        Tender item = list.get(position);
        if(type.equals("2")){
            viewHolder.tv_title.setText(item.getTitle());
            viewHolder.tv_name.setText(item.getName());
            viewHolder.tv_create_time.setText(item.getCreate_time());
        }else{
            viewHolder.tv_title.setText(item.getName());
            viewHolder.tv_name.setText(item.getCom_name());
            viewHolder.tv_create_time.setText(item.getCreate_time());
        }

    }

    static class ViewHolder {

        @Bind(R.id.tv_title)
        TextView tv_title;//标题

        @Bind(R.id.tv_name)
        TextView tv_name;//发布者

        @Bind(R.id.tv_create_time)
        TextView tv_create_time;//发布时间

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
