package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.Spec;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 产品规格adapter
 * Created by Botx on 2017/4/13.
 */

public class SpecGridAdapter extends MyBaseAdapter<Spec> {

    public SpecGridAdapter(Context context, List<Spec> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_spec, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Spec spec = list.get(position);
        vh.txtName.setText(spec.getName());
        vh.txtValue.setText(spec.getValue() + spec.getUnit());

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.txtName)
        TextView txtName;

        @Bind(R.id.txtValue)
        TextView txtValue;
    }
}
