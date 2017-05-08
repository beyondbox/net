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
import com.appjumper.silkscreen.bean.EquipmentCategory;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 产品
 */
public class SubListViewAdapter extends BaseAdapter {

    private final List<EquipmentCategory> data;
    private LayoutInflater mInflater;
    private Context mContext;
    private int currentSelectPosition = -1;

    public SubListViewAdapter(Context context, List<EquipmentCategory> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.data = data;
    }

    public void setCurrentSelectPosition(int currentSelectPosition) {
        this.currentSelectPosition = currentSelectPosition;
    }

    @Override
    public int getCount() {
        return data.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_production, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        if (currentSelectPosition == position) {
            viewHolder.tv_name.setTextColor(mContext.getResources().getColor(R.color.theme_color));
        } else {
            viewHolder.tv_name.setTextColor(mContext.getResources().getColor(R.color.black_color));
        }
        viewHolder.tv_name.setText(data.get(position).getName());
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView tv_name;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
