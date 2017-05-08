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
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.IntegralList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 我的积分
 */
public class PointListViewAdapter extends BaseAdapter {

    private final List<IntegralList> list;
    private LayoutInflater mInflater;
    private Context mContext;

    public PointListViewAdapter(Context context, List<IntegralList> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_point, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        IntegralList item = list.get(position);
        viewHolder.tvDate.setText(item.getCreate_time());
        switch (item.getType()){
            case "1"://签到
                viewHolder.tvTitle.setText("签到");
                viewHolder.tvPoint.setText("+"+item.getIntegral());
                viewHolder.tvDate.setText(stampToDate((Long.parseLong(item.getCreate_time())*1000)+""));
                viewHolder.tvPoint.setTextColor(mContext.getResources().getColor(R.color.orange_color));
                break;
        }

//        viewHolder.tvPoint.setTextColor(mContext.getResources().getColor(R.color.orange_color));
//        if (position % 2 == 0) {
//            viewHolder.tvTitle.setText("发布供求信息");
//            viewHolder.tvPoint.setText("+155");
//            viewHolder.tvPoint.setTextColor(mContext.getResources().getColor(R.color.orange_color));
//        } else {
//            viewHolder.tvTitle.setText("一键询价");
//            viewHolder.tvPoint.setText("-155");
//            viewHolder.tvPoint.setTextColor(mContext.getResources().getColor(R.color.green_color));
//        }
    }
    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    static class ViewHolder {
        @Bind(R.id.tv_title)//标题
                TextView tvTitle;
        @Bind(R.id.tv_date)//日期
                TextView tvDate;
        @Bind(R.id.tv_point)//积分
                TextView tvPoint;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
