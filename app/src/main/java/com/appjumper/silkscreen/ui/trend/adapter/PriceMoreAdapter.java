package com.appjumper.silkscreen.ui.trend.adapter;

import android.content.Context;
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

/**
 * 原材料报价-更多
 * Created by Botx on 2017/6/24.
 */

public class PriceMoreAdapter extends MyBaseAdapter<OfferList> {

    public PriceMoreAdapter(Context context, List<OfferList> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
