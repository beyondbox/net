package com.appjumper.silkscreen.ui.inquiry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Enterprise;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/***
 * 公司选择
 */
public class SelectCompanyListViewAdapter extends BaseAdapter {
    private Context mContext;
    private ListView mListView;
    private List<Enterprise> list;

    public SelectCompanyListViewAdapter(Context context, ListView listView, List<Enterprise> list) {
        mContext = context;
        mListView = listView;
        this.list = list;
    }

    public int getCount() {
        return list.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_select_company, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        Enterprise item = list.get(position);
        if (mListView.isItemChecked(position)) {
            viewHolder.ivChoice.setImageResource(R.mipmap.icon_selected);
        } else {
            viewHolder.ivChoice.setImageResource(R.mipmap.icon_unselected);
        }
        viewHolder.tvName.setText(item.getEnterprise_name());
        viewHolder.tvDate.setText(item.getCreate_time().substring(0, 10));
    }

    static class ViewHolder {
        @Bind(R.id.tv_company_name)
        TextView tvName;
        @Bind(R.id.tv_date)
        TextView tvDate;
        @Bind(R.id.iv_choice)
        ImageView ivChoice;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
