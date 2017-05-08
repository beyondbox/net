package com.appjumper.silkscreen.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;


/**
 * Created by Administrator on 2016/7/7.
 * 所有固定信息选择
 */
public class InformationListAdapter extends BaseAdapter {
    private BaseActivity mContext;
    private LayoutInflater mInflater;
    private String[] list;
    public InformationListAdapter(BaseActivity mContext, String[] list) {
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
            convertView = mInflater.inflate(R.layout.item_address, null);
//                holder.layout_btn = (LinearLayout) convertView.findViewById(R.id.layout_btn);

            holder.tv_index = (TextView) convertView.findViewById(R.id.tv_index);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String item_title = list[position];
        holder.tv_index.setVisibility(View.GONE);
        holder.tv_name.setText(item_title);
        return convertView;
    }

    public  class ViewHolder {
        private TextView tv_index;
        private TextView tv_name;
//            private LinearLayout layout_btn;

    }
}
