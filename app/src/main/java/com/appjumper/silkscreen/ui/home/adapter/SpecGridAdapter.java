package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.ui.common.adapter.SpecImageAdapter;

import java.util.Arrays;
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
        View view = null;

        Spec spec = list.get(position);
        if (spec.getValue().matches("[hH][tT]{2}[pP]://[\\s\\S]+\\.[jJ][pP][gG]")) {
            view = LayoutInflater.from(context).inflate(R.layout.item_lv_spec_image, null);
            TextView txtName = (TextView) view.findViewById(R.id.txtName);
            txtName.setText(spec.getName());

            ListView lvImage = (ListView) view.findViewById(R.id.lvImage);
            lvImage.setAdapter(new SpecImageAdapter(context, Arrays.asList(spec.getValue().split(","))));
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_grid_spec, null);
            ViewHolder vh = new ViewHolder();
            ButterKnife.bind(vh, view);
            vh.txtName.setText(spec.getName());
            vh.txtValue.setText(spec.getValue());
        }

        return view;
    }

    class ViewHolder {
        @Bind(R.id.txtName)
        TextView txtName;

        @Bind(R.id.txtValue)
        TextView txtValue;
    }
}
