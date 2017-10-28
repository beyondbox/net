package com.appjumper.silkscreen.ui.my.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.FreightOffer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 选择司机adapter
 * Created by Botx on 2017/10/19.
 */

public class ChooseDriverAdapter extends MyBaseAdapter<FreightOffer> {

    public ChooseDriverAdapter(Context context, List<FreightOffer> list) {
        super(context, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_choose_driver, null);
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        FreightOffer offer = list.get(position);
        vh.txtName.setText(offer.getName().substring(0, 1) + "司机");
        vh.txtTime.setText(offer.getCreate_time().substring(5, 16));
        vh.txtPrice.setText(offer.getMoney() + offer.getMoney_unit());

        if (onWhichClickListener != null) {
            vh.txtHandle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onWhichClickListener.onWhichClick(view, position, 0);
                }
            });
        }

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.txtName)
        TextView txtName;
        @Bind(R.id.txtPrice)
        TextView txtPrice;
        @Bind(R.id.txtTime)
        TextView txtTime;
        @Bind(R.id.imgViCertiGreen)
        ImageView imgViCertiGreen;
        @Bind(R.id.imgViCertiDriver)
        ImageView imgViCertiDriver;
        @Bind(R.id.txtHandle)
        TextView txtHandle;
    }
}
