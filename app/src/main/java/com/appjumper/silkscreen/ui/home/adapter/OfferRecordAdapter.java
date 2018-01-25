package com.appjumper.silkscreen.ui.home.adapter;

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

    public boolean isPrivateMode = true;
    private String uid = "";
    private String askBuyUid = "";
    private boolean isOffering = false;

    public OfferRecordAdapter(Context context, List<AskBuyOffer> list, String loginUid, String askBuyUid, boolean isOffering) {
        super(context, list);
        this.uid = loginUid;
        this.askBuyUid = askBuyUid;
        this.isOffering = isOffering;
    }

    public void setPrivateMode(boolean privateMode) {
        isPrivateMode = privateMode;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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
        vh.txtWeight.setText(offer.getWeight() + offer.getWeight_unit());
        vh.txtDelete.setVisibility(View.GONE);

        if (isPrivateMode)
            vh.txtPrice.setText("***" + offer.getPrice_unit());
        else
            vh.txtPrice.setText(offer.getMoney() + offer.getPrice_unit());


        if (uid.equals(offer.getUser_id())) {
            vh.txtName.setText("我的报价");
            vh.txtName.setTextColor(context.getResources().getColor(R.color.red_color));
            vh.txtPrice.setTextColor(context.getResources().getColor(R.color.red_color));

            if (isOffering) {
                vh.txtDelete.setVisibility(View.VISIBLE);
                vh.txtDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onWhichClickListener.onWhichClick(view, position, 0);
                    }
                });
            }
        } else {
            vh.txtName.setText("厂家报价" + (position + 1));
            vh.txtName.setTextColor(context.getResources().getColor(R.color.text_black_color));
            vh.txtPrice.setTextColor(context.getResources().getColor(R.color.text_black_color));
        }


        if (askBuyUid.equals(uid)) {
            vh.txtTime.setVisibility(View.GONE);
            vh.txtHandle.setVisibility(View.VISIBLE);
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
        @Bind(R.id.txtWeight)
        TextView txtWeight;
        @Bind(R.id.txtTime)
        TextView txtTime;
        @Bind(R.id.txtHandle)
        TextView txtHandle;
        @Bind(R.id.txtDelete)
        TextView txtDelete;
    }
}
