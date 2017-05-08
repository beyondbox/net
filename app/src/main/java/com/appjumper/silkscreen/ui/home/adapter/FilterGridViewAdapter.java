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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 筛选
 */
public class FilterGridViewAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private int currentPosition = -1;
    private String[] list;

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public FilterGridViewAdapter(Context context, String[] list) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if(list==null){
            return 0;
        }else{
            return list.length;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gv_filter, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        if (currentPosition == position) {
            viewHolder.tvValue.setBackgroundResource(R.drawable.theme_background_login_btn);
            viewHolder.tvValue.setTextColor(mContext.getResources().getColor(R.color.while_color));
        } else {
            viewHolder.tvValue.setBackgroundResource(R.drawable.specifications_unselect_background);
            viewHolder.tvValue.setTextColor(mContext.getResources().getColor(R.color.black_color));
        }
        viewHolder.tvValue.setText(list[position]);
    }

    static class ViewHolder {
        @Bind(R.id.tv_value)
        TextView tvValue;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
