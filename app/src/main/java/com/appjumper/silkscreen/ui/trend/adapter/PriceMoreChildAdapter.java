package com.appjumper.silkscreen.ui.trend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.OfferList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 原材料报价-更多-childAdapter
 * Created by Botx on 2017/6/24.
 */

public class PriceMoreChildAdapter extends MyBaseAdapter<OfferList> {

    private int displayNum = 0;

    public PriceMoreChildAdapter(Context context, List<OfferList> list) {
        super(context, list);
    }

    public void setDisplayNum(int displayNum) {
        this.displayNum = displayNum;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return displayNum == 0 ? list.size() : displayNum;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_price_child, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        OfferList offer = list.get(position);

        vh.txtCompanyName.setText(offer.getCompany_name());
        vh.txtTime.setText(offer.getOffer_time().substring(0, 16));
        vh.txtTax.setText(offer.getOffer_value_tax() + offer.getOffer_unit());
        vh.txtNoTax.setText(offer.getOffer_value() + offer.getOffer_unit());

        if (position == 0) {
            vh.llCompany.setVisibility(View.VISIBLE);
            if (list.size() > 1)
                vh.imgViArrow.setVisibility(View.VISIBLE);
            else
                vh.imgViArrow.setVisibility(View.GONE);
        } else {
            vh.llCompany.setVisibility(View.GONE);
        }

        if (offer.getOffer_value_tax().equals("0"))
            vh.llTax.setVisibility(View.GONE);
        else
            vh.llTax.setVisibility(View.VISIBLE);

        if (offer.getOffer_value().equals("0"))
            vh.llNoTax.setVisibility(View.GONE);
        else
            vh.llNoTax.setVisibility(View.VISIBLE);


        final ViewHolder finalVh = vh;
        vh.imgViArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayNum > 1) {
                    finalVh.imgViArrow.setImageResource(R.mipmap.icon_arrows_down_01);
                    setDisplayNum(1);
                } else {
                    finalVh.imgViArrow.setImageResource(R.mipmap.icon_arrows_up_01);
                    setDisplayNum(list.size());
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.llCompany)
        LinearLayout llCompany;
        @Bind(R.id.txtCompanyName)
        TextView txtCompanyName;
        @Bind(R.id.imgViArrow)
        ImageView imgViArrow;
        @Bind(R.id.txtTime)
        TextView txtTime;
        @Bind(R.id.txtTax)
        TextView txtTax;
        @Bind(R.id.txtNoTax)
        TextView txtNoTax;
        @Bind(R.id.llTax)
        LinearLayout llTax;
        @Bind(R.id.llNoTax)
        LinearLayout llNoTax;
    }
}
