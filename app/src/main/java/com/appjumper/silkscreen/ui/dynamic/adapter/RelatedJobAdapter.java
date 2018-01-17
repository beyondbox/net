package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.RecruitList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 周边--招聘adapter
 * Created by Botx on 2017/10/19.
 */

public class RelatedJobAdapter extends MyBaseAdapter<RecruitList> {


    public RelatedJobAdapter(Context context, List<RecruitList> list) {
        super(context, list);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_related, null);
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        RecruitList item = list.get(position);
        vh.txtTitle.setText(TextUtils.isEmpty(item.getTitle()) ? item.getName() : item.getTitle());
        vh.txtRemark.setText(item.getName());

        if (item.getSalary().equals("面议") || item.getSalary().equals("面谈"))
            vh.txtPrice.setText("面议");
        else
            vh.txtPrice.setText(item.getSalary()+"元/月");


        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.txtTitle)
        TextView txtTitle;
        @Bind(R.id.txtPrice)
        TextView txtPrice;
        @Bind(R.id.txtRemark)
        TextView txtRemark;
    }
}
