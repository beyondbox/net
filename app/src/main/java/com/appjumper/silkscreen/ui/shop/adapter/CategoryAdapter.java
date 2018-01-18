package com.appjumper.silkscreen.ui.shop.adapter;

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
 * 商城分类adapter
 * Created by botx on 2017/9/30.
 */

public class CategoryAdapter extends MyBaseAdapter<String> {

    private int selectedPosition = 0;

    public CategoryAdapter(Context context, List<String> list) {
        super(context, list);
    }

    public void changeSelected(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_shop_category, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (position == selectedPosition)
            vh.txtName.setSelected(true);
        else
            vh.txtName.setSelected(false);

        vh.txtName.setText(list.get(position));

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.txtName)
        TextView txtName;
    }
}
