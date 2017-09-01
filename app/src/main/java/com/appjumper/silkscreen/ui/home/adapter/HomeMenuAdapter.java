package com.appjumper.silkscreen.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;


/**
 * 首页顶部菜单adapter
 * Created by Botx on 2017/8/22.
 */

public class HomeMenuAdapter extends BaseAdapter {

    private String [] nameArr = {"订做", "加工", "现货供应", "行业新闻", "招/投标", "物流", "设备", "厂房", "招聘", "展会信息"};

    private int [] iconArr = {R.mipmap.home_supply_order,
            R.mipmap.home_supply_process,
            R.mipmap.home_supply_stock,
            R.mipmap.home_supply_news,
            R.mipmap.home_supply_tender,
            R.mipmap.home_supply_logistics,
            R.mipmap.home_supply_device,
            R.mipmap.home_supply_workshop,
            R.mipmap.home_supply_job,
            R.mipmap.home_supply_expo};


    @Override
    public int getCount() {
        return nameArr.length;
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
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_home_menu, null);
            vh = new ViewHolder();
            vh.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            vh.txtName = (TextView) convertView.findViewById(R.id.txtName);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.imageView.setImageResource(iconArr[position]);
        vh.txtName.setText(nameArr[position]);

        return convertView;
    }


    class ViewHolder {
        ImageView imageView;
        TextView txtName;
    }

}
