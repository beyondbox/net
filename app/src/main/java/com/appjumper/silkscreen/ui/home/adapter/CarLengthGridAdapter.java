package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.CarLength;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 车长下拉菜单adapter
 */
public class CarLengthGridAdapter extends BaseAdapter {

    private Context context;
    private List<CarLength> list;


    public CarLengthGridAdapter(Context context, List<CarLength> list) {
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
        CarLength carLength = list.get(position);
        viewHolder.mText.setText(carLength.toString());
        if (carLength.isSelected())
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
