package com.appjumper.silkscreen.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.ui.common.AddressSelectActivity;


/**
 * Created by Administrator on 2016/7/7.
 */
public class GridViewAdapter extends BaseAdapter {
        private AddressSelectActivity mContext;
        private LayoutInflater mInflater;
        private String[] list;
        public GridViewAdapter(AddressSelectActivity mContext, String[] list) {
            this.list = list;
            this.mInflater = LayoutInflater.from(mContext);
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_address_gridview, null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String item = list[position];
            holder.tv_name.setText(item);
            return convertView;
        }

        public  class ViewHolder {
            private TextView tv_name;

        }
}
