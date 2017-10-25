package com.appjumper.silkscreen.ui.my.adapter;

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
 * 采纳报价adapter
 * Created by Botx on 2017/10/19.
 */

public class ChooseOfferAdapter extends MyBaseAdapter<AskBuyOffer> {

    public ChooseOfferAdapter(Context context, List<AskBuyOffer> list) {
        super(context, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_offer_record_handle, null);
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        AskBuyOffer offer = list.get(position);
        vh.txtPrice.setText(offer.getMoney() + offer.getPrice_unit());

        if (offer.getOffer_user_type().equals("0")) {
            vh.txtName.setText("平台报价");
        } else {
            if (list.get(0).getOffer_user_type().equals("0")) {
                vh.txtName.setText("企业报价" + position);
            } else {
                vh.txtName.setText("企业报价" + (position + 1));
            }
        }

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
        @Bind(R.id.txtHandle)
        TextView txtHandle;
    }
}
