package com.appjumper.silkscreen.ui.trend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.OfferList;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 原材料报价-更多-parentAdapter
 * Created by Botx on 2017/6/26.
 */

public class PriceMoreParentAdapter extends MyBaseAdapter<List<OfferList>> {

    public PriceMoreParentAdapter(Context context, List<List<OfferList>> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_price_parent, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        List<OfferList> childList = list.get(position);
        PriceMoreChildAdapter childAdapter = new PriceMoreChildAdapter(context, childList);
        vh.lvData.setAdapter(childAdapter);

        if (childList.size() > 1) {
            childAdapter.setDisplayNum(1);
        }

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.lvData)
        ListView lvData;
    }

}
