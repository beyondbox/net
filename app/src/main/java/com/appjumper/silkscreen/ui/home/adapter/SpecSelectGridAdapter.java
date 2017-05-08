package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 产品规格选择adapter
 * Created by Botx on 2017/4/14.
 */

public class SpecSelectGridAdapter extends MyBaseAdapter<String> {

    private int currSelected = 0;

    public SpecSelectGridAdapter(Context context, List<String> list) {
        super(context, list);
    }

    public void changeSelected(int position){
        currSelected = position;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_spec_select, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (currSelected == position) {
            vh.txtName.setSelected(true);
        } else {
            vh.txtName.setSelected(false);
        }

        vh.txtName.setText(list.get(position));

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.txtName)
        TextView txtName;
    }
}
