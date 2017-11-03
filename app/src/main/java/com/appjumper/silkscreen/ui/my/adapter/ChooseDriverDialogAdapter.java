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
 * 选择司机对话框adapter
 * Created by Botx on 2017/10/19.
 */

public class ChooseDriverDialogAdapter extends MyBaseAdapter<FreightOffer> {

    public int selectedPosition = 0;

    public ChooseDriverDialogAdapter(Context context, List<FreightOffer> list) {
        super(context, list);
    }

    public void changeSelected(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_choose_driver_dialog, null);
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        FreightOffer offer = list.get(position);
        vh.txtName.setText(offer.getName().substring(0, 1) + "司机");
        vh.txtTime.setText(offer.getCreate_time().substring(5, 16));
        vh.txtPrice.setText(offer.getMoney() + offer.getMoney_unit());
        vh.txtState.setText(offer.getOffer_status().equals("1") ? "已失效" : "");

        if (selectedPosition == position)
            vh.txtName.setSelected(true);
        else
            vh.txtName.setSelected(false);

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
        @Bind(R.id.txtState)
        TextView txtState;
    }
}
