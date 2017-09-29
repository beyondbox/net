package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.Province;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 货站——省份列表adapter
 * Created by botx on 2017/9/30.
 */

public class ProvinceAdapter extends MyBaseAdapter<Province> {

    private int selectedPosition = 0;

    public ProvinceAdapter(Context context, List<Province> list) {
        super(context, list);
    }

    public void changeSelected(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_province_line, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Province province = list.get(position);
        vh.txtName.setText(province.getProvince_name() + "(" + province.getShuling() + ")");

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.txtName)
        TextView txtName;
    }
}
