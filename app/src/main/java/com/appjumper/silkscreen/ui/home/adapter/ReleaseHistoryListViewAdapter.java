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

import java.util.List;


/**
 * 发布历史
 */
public class ReleaseHistoryListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> list;

    public ReleaseHistoryListViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 5;
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
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_lv_release_history, null);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
            holder.view1 = convertView.findViewById(R.id.view1);//线
            holder.view2 = convertView.findViewById(R.id.view2);//线
            holder.view = convertView.findViewById(R.id.view);//线
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvAddress.setText("河北省西贾庄多驾培大街");
        if (position == 0) {
            holder.view1.setVisibility(View.INVISIBLE);
            holder.view2.setVisibility(View.VISIBLE);
            holder.view.setVisibility(View.VISIBLE);
        } else if (position == 4) {
            holder.view1.setVisibility(View.VISIBLE);
            holder.view2.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);
        } else {
            holder.view1.setVisibility(View.VISIBLE);
            holder.view2.setVisibility(View.VISIBLE);
            holder.view.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public class ViewHolder {
        TextView tvAddress;
        View view1, view2, view;
    }

}
