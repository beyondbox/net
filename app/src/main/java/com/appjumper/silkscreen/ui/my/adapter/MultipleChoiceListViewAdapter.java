package com.appjumper.silkscreen.ui.my.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 * 多选
 */
public class MultipleChoiceListViewAdapter extends BaseAdapter {
    private Context mContext;
    private ListView mListView;
    private String[] list;

    public MultipleChoiceListViewAdapter(Context context, ListView listView, String[] list) {
        mContext = context;
        mListView = listView;
        this.list = list;
    }

    public int getCount() {
        return list.length;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_material, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        viewHolder.tvChoice.setText(list[position]);
        if (mListView.isItemChecked(position)) {
            viewHolder.ivChoice.setImageResource(R.mipmap.icon_pitch_on);
        } else {
            viewHolder.ivChoice.setImageResource(R.mipmap.icon_unchecked);
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_choice)
        TextView tvChoice;
        @Bind(R.id.iv_choice)
        ImageView ivChoice;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
