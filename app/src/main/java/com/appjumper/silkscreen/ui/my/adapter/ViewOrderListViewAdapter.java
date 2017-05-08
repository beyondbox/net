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

package com.appjumper.silkscreen.ui.my.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Spec;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 查看订做／加工／现货 详情 规格
 */
public class ViewOrderListViewAdapter extends BaseAdapter {

    private final List<Spec> service_spec;
    private LayoutInflater mInflater;
    private Context mContext;

    public ViewOrderListViewAdapter(Context context, List<Spec> service_spec) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.service_spec = service_spec;
    }

    @Override
    public int getCount() {
        return service_spec.size();
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
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_service_details, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        if (position  == 0) {
            viewHolder.ll_gg.setVisibility(View.VISIBLE);
            viewHolder.ll_ggval.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ll_gg.setVisibility(View.GONE);
            viewHolder.ll_ggval.setVisibility(View.VISIBLE);
        }
        viewHolder.tv_gg.setText(service_spec.get(position).getName()+": ");
        if(service_spec.get(position).getFieldinput().equals("radio")){
            viewHolder.tv_vul.setText(service_spec.get(position).getValue());
        }else{
            viewHolder.tv_vul.setText(service_spec.get(position).getValue()+" "+service_spec.get(position).getUnit());
        }
    }

    static class ViewHolder {
        @Bind(R.id.ll_gg)//规格
                LinearLayout ll_gg;
        @Bind(R.id.ll_ggval)//规格内容
                LinearLayout ll_ggval;
        @Bind(R.id.tv_gg)//规格
                TextView tv_gg;
        @Bind(R.id.tv_vul)//规格内容
                TextView tv_vul;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
