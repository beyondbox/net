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

import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseActivity;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.ui.home.equipment.EquipmentDetailsActivity;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.appjumper.silkscreen.view.MyLinearLayoutManger;
import com.appjumper.silkscreen.view.MyRecyclerView;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 设备出售
 */
public class EquipmentListviewAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final BaseActivity mContext;
    private final List<EquipmentList> list;

    public EquipmentListviewAdapter(BaseActivity context, List<EquipmentList> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_equipment_sell, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(final int position, ViewHolder viewHolder) {
        EquipmentList item = list.get(position);
        if (item.getEnterprise_logo() != null && !item.getEnterprise_logo().getSmall().equals("")) {
            Picasso.with(mContext).load(item.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.ivLogo);
        }
        viewHolder.tvCompanyName.setText(item.getEnterprise_name());
        viewHolder.tvEquipment.setText(item.getItems().get(0).getName());
        viewHolder.tvDate.setText(item.getCreate_time().substring(5, 16));
        MyLinearLayoutManger linearLayoutManager = new MyLinearLayoutManger(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewHolder.myRecyclerView.setLayoutManager(linearLayoutManager);
        if (item.getItems().get(0).getImg_list() != null && item.getItems().get(0).getImg_list().size() > 0) {
            GalleryAdapter adapter = new GalleryAdapter(mContext, item.getItems().get(0).getImg_list());
            viewHolder.myRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int pos) {
                    if (!mContext.checkLogined())
                        return;

                    mContext.start_Activity(mContext, EquipmentDetailsActivity.class, new BasicNameValuePair("id", list.get(position).getId()));
                }
            });

        }
        if (position == (list.size() - 1)) {
            viewHolder.llLine.setVisibility(View.GONE);
        } else {
            viewHolder.llLine.setVisibility(View.VISIBLE);
        }
    }

    static class ViewHolder {
        @Bind(R.id.recycler_view)
        MyRecyclerView myRecyclerView;
        @Bind(R.id.ll_line)
        LinearLayout llLine;
        @Bind(R.id.iv_logo)//公司图片
                ImageView ivLogo;
        @Bind(R.id.tv_company_name)//公司名称
                TextView tvCompanyName;
        @Bind(R.id.tv_equipment)//出售的设备
                TextView tvEquipment;
        @Bind(R.id.tv_date)//发布日期
                TextView tvDate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
