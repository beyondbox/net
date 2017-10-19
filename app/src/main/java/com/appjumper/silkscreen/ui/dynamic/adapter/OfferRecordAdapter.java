package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.AskBuyOffer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 报价记录adapter
 * Created by Botx on 2017/10/19.
 */

public class OfferRecordAdapter extends MyBaseAdapter<AskBuyOffer> {

    public OfferRecordAdapter(Context context, List<AskBuyOffer> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_offer_record, null);
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        AskBuyOffer offer = list.get(position);
        vh.txtTime.setText(offer.getCreate_time().substring(5));

        if (offer.getOffer_user_type().equals("0")) {
            vh.txtName.setText("平台报价");
        } else {
            if (list.get(0).getOffer_user_type().equals("0")) {
                vh.txtName.setText("企业报价" + position);
            } else {
                vh.txtName.setText("企业报价" + (position + 1));
            }
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
    }
}
