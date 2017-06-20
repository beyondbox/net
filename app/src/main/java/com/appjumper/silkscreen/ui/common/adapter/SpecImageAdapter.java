package com.appjumper.silkscreen.ui.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Botx on 2017/6/20.
 */

public class SpecImageAdapter extends MyBaseAdapter<String> {

    public SpecImageAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lv_image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        Picasso.with(context)
                .load(list.get(position))
                .placeholder(R.mipmap.img_error)
                .error(R.mipmap.img_error)
                .into(imageView);

        return view;
    }
}
