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
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.util.Const;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 物流货站相关推荐adapter
 */
public class LineRecommendAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<LineList> list;


    public LineRecommendAdapter(Context context, List<LineList> list) {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_line, null);
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

        if (!TextUtils.isEmpty(item.getLine_type())) {
            int infoType = Integer.valueOf(item.getLine_type());
            switch (infoType) {
                case Const.INFO_TYPE_PER:
                    viewHolder.txtMark.setText("个人");
                    viewHolder.txtMark.setBackgroundResource(R.drawable.shape_mark_person_bg);
                    break;
                case Const.INFO_TYPE_COM:
                    viewHolder.txtMark.setText("企业");
                    viewHolder.txtMark.setBackgroundResource(R.drawable.shape_mark_enterprise_bg);
                    break;
                case Const.INFO_TYPE_OFFICIAL:
                    viewHolder.txtMark.setText("官方");
                    viewHolder.txtMark.setBackgroundResource(R.drawable.shape_mark_official_bg);
                    break;
                default:
                    break;
            }
        }

        viewHolder.tv_name.setText(item.getOfficial_name());
        viewHolder.tv_form_to.setText("线路：" + item.getFrom() + "-" + item.getTo());
        viewHolder.tv_passby.setText("途径：" + item.getPassby_name());
    }


    class ViewHolder {
        @Bind(R.id.tv_name)
        TextView tv_name;

        @Bind(R.id.tv_form_to)
        TextView tv_form_to;

        @Bind(R.id.tv_passby)
        TextView tv_passby;

        @Bind(R.id.txtMark)
        TextView txtMark;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
