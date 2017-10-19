package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.Avatar;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 求购-图纸图片adapter
 * Created by Botx on 2017/10/19.
 */

public class AskBuyImageAdapter extends MyBaseAdapter<Avatar> {

    public AskBuyImageAdapter(Context context, List<Avatar> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_askbuy_image, null);
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Picasso.with(context)
                .load(list.get(position).getSmall())
                .placeholder(R.mipmap.img_error)
                .error(R.mipmap.img_error)
                .into(vh.imageView);

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.imageView)
        ImageView imageView;
    }
}
