/*
 *
 *  *
 *  *  *
 *  *  *  * ===================================
 *  *  *  * Copyright (c) 2016.
 *  *  *  * 作者：安卓猴
 *  *  *  * 微博：@安卓猴
 *  *  *  * 博客：http://sunjiajia.com
 *  *  *  * Github：https://github.com/opengit
 *  *  *  *
 *  *  *  * 注意**：如果您使用或者修改该代码，请务必保留此版权信息。
 *  *  *  * ===================================
 *  *  *
 *  *  *
 *  *
 *
 */

package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.ui.common.adapter.SpecImageAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by botx on 2017/6/29.
 * 现货详情 规格
 */
public class StockSpecListViewAdapter extends BaseAdapter {

    private final List<Spec> service_spec;
    private Context mContext;

    public StockSpecListViewAdapter(Context context, List<Spec> service_spec) {
        this.mContext = context;
        this.service_spec = service_spec;
    }

    @Override
    public int getCount() {
        return service_spec.size();
    }

    @Override
    public Object getItem(int position) {
        return service_spec.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;

        Spec spec = service_spec.get(position);
        if (spec.getValue().matches("[hH][tT]{2}[pP]://[\\s\\S]+\\.[jJ][pP][gG]")) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_lv_spec_image_stock, null);
            TextView txtName = (TextView) view.findViewById(R.id.txtName);
            txtName.setText(spec.getName() + ":");

            ListView lvImage = (ListView) view.findViewById(R.id.lvImage);
            lvImage.setAdapter(new SpecImageAdapter(mContext, Arrays.asList(spec.getValue().split(","))));
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_lv_stock_spec, null);
            ViewHolder viewHolder = new ViewHolder(view);
            fillValue(position, viewHolder);
        }

        return view;
    }


    private void fillValue(int position, ViewHolder viewHolder) {
        viewHolder.tv_gg.setText(service_spec.get(position).getName()+":");
        viewHolder.tv_vul.setText(service_spec.get(position).getValue());
    }


    static class ViewHolder {
        @Bind(R.id.tv_gg)//规格
                TextView tv_gg;
        @Bind(R.id.tv_vul)//规格内容
                TextView tv_vul;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
