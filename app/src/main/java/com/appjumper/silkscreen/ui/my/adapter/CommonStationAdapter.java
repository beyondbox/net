package com.appjumper.silkscreen.ui.my.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.LineList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 常用货站adapter
 * Created by Botx on 2017/12/13.
 */

public class CommonStationAdapter extends MyBaseAdapter<LineList> {

    public CommonStationAdapter(Context context, List<LineList> list) {
        super(context, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_common_address, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        LineList station = list.get(position);
        vh.txtName.setText(station.getOfficial_name() + " " + station.getOfficial_address());
        vh.imgViDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onWhichClickListener.onWhichClick(view, position, 0);
            }
        });

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.imgViDelete)
        ImageView imgViDelete;
        @Bind(R.id.txtName)
        TextView txtName;
    }
}
