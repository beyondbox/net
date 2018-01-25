package com.appjumper.silkscreen.ui.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.StockGoods;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 产品Grid adapter
 * Created by botx on 2017/9/30.
 */

public class ShopProductGridAdapter extends MyBaseAdapter<StockGoods> {


    public ShopProductGridAdapter(Context context, List<StockGoods> list) {
        super(context, list);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_shop_product, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }


        StockGoods item = list.get(position);
        vh.txtName.setText(item.getProduct_name());

        Picasso.with(context)
                .load(item.getImg())
                .resize(DisplayUtil.dip2px(context, 70), DisplayUtil.dip2px(context, 70))
                .centerCrop()
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(vh.imageView);

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.imageView)
        ImageView imageView;
        @Bind(R.id.txtName)
        TextView txtName;
    }
}
