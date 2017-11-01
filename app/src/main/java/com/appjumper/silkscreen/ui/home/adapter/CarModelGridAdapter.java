package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.CarModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 车型下拉菜单adapter
 */
public class CarModelGridAdapter extends BaseAdapter {

    private Context context;
    private List<CarModel> list;


    public CarModelGridAdapter(Context context, List<CarModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_car_model_select, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        CarModel carModel = list.get(position);
        viewHolder.mText.setText(carModel.toString());
        if (carModel.isSelected())
            viewHolder.mText.setSelected(true);
        else
            viewHolder.mText.setSelected(false);
    }

    static class ViewHolder {
        @Bind(R.id.txtName)
        TextView mText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
