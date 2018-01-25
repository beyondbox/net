package com.appjumper.silkscreen.ui.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.ServiceProduct;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 热门产品adapter
 * Created by Botx on 2018/1/24.
 */

public class HotProductAdapter extends MyBaseAdapter<ServiceProduct> {

    public HotProductAdapter(Context context, List<ServiceProduct> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_hot_product, null);
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.txtName.setText(list.get(position).getName());

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.txtName)
        TextView txtName;
    }
}
