package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.EquipmentList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 周边--厂房adapter
 * Created by Botx on 2017/10/19.
 */

public class RelatedWorkshopAdapter extends MyBaseAdapter<EquipmentList> {


    public RelatedWorkshopAdapter(Context context, List<EquipmentList> list) {
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

        EquipmentList item = list.get(position);
        vh.txtTitle.setText(item.getTitle());
        vh.txtRemark.setText(item.getArea() + "平米");

        if (item.getLease_mode().equals("出售")) {
            vh.txtPrice.setText(item.getPrice() + "元");
        } else {
            vh.txtPrice.setText(item.getPrice() + "元/年");
        }

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
