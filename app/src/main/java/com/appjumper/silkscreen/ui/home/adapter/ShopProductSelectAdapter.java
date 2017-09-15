package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.Product;

import java.util.List;

/**
 * 商城列表选择产品adapter
 * Created by Botx on 2017/9/12.
 */

public class ShopProductSelectAdapter extends MyBaseAdapter<Product> {

    private int selectedPosition = -1;

    public ShopProductSelectAdapter(Context context, List<Product> list) {
        super(context, list);
    }

    public void changeSelected(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_shop_product_select, null);
        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        txtName.setText(list.get(position).getProduct_name());

        if (position == selectedPosition)
            txtName.setSelected(true);
        else
            txtName.setSelected(false);

        return view;
    }
}
