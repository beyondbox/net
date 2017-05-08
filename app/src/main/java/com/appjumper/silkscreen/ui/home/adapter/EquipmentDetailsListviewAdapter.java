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
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.view.MyLinearLayoutManger;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 设备出售详情
 */
public class EquipmentDetailsListviewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<EquipmentList> list;

    public EquipmentDetailsListviewAdapter(Context context, List<EquipmentList> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_equipment_details, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        final EquipmentList item = list.get(position);
        viewHolder.tvEquipmentName.setText(item.getName());
        viewHolder.tvEquipmentBrand.setText(item.getBrand());
        viewHolder.tvEquipmentModel.setText(item.getModel());
        viewHolder.tvDegree.setText(item.getNew_old_rate());
        viewHolder.tvEquipmentPrice.setText("￥"+item.getPrice()+"元");
        MyLinearLayoutManger linearLayoutManager = new MyLinearLayoutManger(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewHolder.myRecyclerView.setLayoutManager(linearLayoutManager);

        if (item.getImg_list() != null && item.getImg_list().size() > 0) {
            GalleryAdapter adapter = new GalleryAdapter(mContext, item.getImg_list());
            viewHolder.myRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    ArrayList<String> urls = new ArrayList<String>();
                    for (Avatar string : item.getImg_list()) {
                        urls.add(string.getOrigin());
                    }
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    static class ViewHolder {
        @Bind(R.id.recycler_view)
        MyRecyclerView myRecyclerView;
        @Bind(R.id.tv_equipment_name)
        TextView tvEquipmentName;
        @Bind(R.id.tv_equipment_brand)
        TextView tvEquipmentBrand;
        @Bind(R.id.tv_equipment_model)
        TextView tvEquipmentModel;
        @Bind(R.id.tv_degree)
        TextView tvDegree;
        @Bind(R.id.tv_equipment_price)
        TextView tvEquipmentPrice;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
