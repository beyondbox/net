package com.appjumper.silkscreen.ui.my.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.Transport;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 空车配货--运输情况adapter
 * Created by Botx on 2017/4/19.
 */

public class TransportListAdapter extends MyBaseAdapter<Transport> {

    public TransportListAdapter(Context context, List<Transport> list) {
        super(context, list);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_transport, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Transport transport = list.get(position);
        vh.txtTime.setText(transport.getCreate_time().substring(5, transport.getCreate_time().length()));
        vh.txtContent.setText(transport.getContent());

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.txtTime)
        TextView txtTime;
        @Bind(R.id.txtContent)
        TextView txtContent;
    }
}
