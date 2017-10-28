package com.appjumper.silkscreen.ui.home.adapter;

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
 * 报价记录adapter
 * Created by Botx on 2017/10/19.
 */

public class FreightOfferRecordAdapter extends MyBaseAdapter<FreightOffer> {

    public FreightOfferRecordAdapter(Context context, List<FreightOffer> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_freight_offer_record, null);
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        FreightOffer offer = list.get(position);
        vh.txtName.setText(offer.getName().substring(0, 1) + "司机报价");
        vh.txtTime.setText(offer.getCreate_time().substring(5, 16));
        vh.txtPrice.setText("***" + offer.getMoney_unit());

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
    }
}
