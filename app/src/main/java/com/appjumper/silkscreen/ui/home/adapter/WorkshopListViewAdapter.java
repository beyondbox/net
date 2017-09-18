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
import android.text.TextUtils;
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
import com.appjumper.silkscreen.ui.home.workshop.WorkshopDetailsActivity;
import com.appjumper.silkscreen.util.Const;
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
 * 厂房出租
 */
public class WorkshopListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private BaseActivity mContext;
    private List<EquipmentList> list;

    public WorkshopListViewAdapter(BaseActivity context, List<EquipmentList> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_workshop, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(final int position, ViewHolder viewHolder) {
        final EquipmentList item = list.get(position);
        MyLinearLayoutManger linearLayoutManager = new MyLinearLayoutManger(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewHolder.myRecyclerView.setLayoutManager(linearLayoutManager);
        if (item.getImg_list() != null && item.getImg_list().size() > 0) {
            GalleryAdapter adapter = new GalleryAdapter(mContext, item.getImg_list());
            viewHolder.myRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int pos) {
                    if (!mContext.checkLogined())
                        return;
                    mContext.start_Activity(mContext, WorkshopDetailsActivity.class, new BasicNameValuePair("id", item.getId()));
                }
            });
        }
        if (position == (list.size() - 1)) {
            viewHolder.llLine.setVisibility(View.GONE);
        } else {
            viewHolder.llLine.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(item.getEnterprise_logo().getSmall()))
            viewHolder.ivLogo.setImageResource(R.mipmap.icon_logo_image61);
        else
            Picasso.with(mContext).load(item.getEnterprise_logo().getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(viewHolder.ivLogo);
        viewHolder.tvDate.setText(item.getCreate_time().substring(5, 16));
        viewHolder.tvEnterpriseArea.setText(item.getArea() + "m²");
        viewHolder.tvPrice.setText(item.getPrice() + "元/年");
        viewHolder.tvPosition.setText(item.getPosition() + " | " + item.getDistance() + "km");
        viewHolder.tvLocation.setText(item.getTitle() + "/" + item.getPosition());

        if (TextUtils.isEmpty(item.getEnterprise_id()))
            viewHolder.tvCompanyName.setText(item.getUser_nicename());
        else
            viewHolder.tvCompanyName.setText(item.getEnterprise_name());


        if (item.getAuth_status() != null && item.getAuth_status().equals("2"))
            viewHolder.imgViCertiGreen.setVisibility(View.VISIBLE);
        else
            viewHolder.imgViCertiGreen.setVisibility(View.GONE);
        if (item.getEnterprise_auth_status() != null && item.getEnterprise_auth_status().equals("2"))
            viewHolder.imgViCertiBlue.setVisibility(View.VISIBLE);
        else
            viewHolder.imgViCertiBlue.setVisibility(View.GONE);
        if (item.getEnterprise_productivity_auth_status() != null && item.getEnterprise_productivity_auth_status().equals("2"))
            viewHolder.imgViCertiYellow.setVisibility(View.VISIBLE);
        else
            viewHolder.imgViCertiYellow.setVisibility(View.GONE);



        if (!TextUtils.isEmpty(item.getWorkshop_type())) {
            int infoType = Integer.valueOf(item.getWorkshop_type());
            switch (infoType) {
                case Const.INFO_TYPE_PER:
                    viewHolder.txtMark.setText("个人");
                    viewHolder.txtMark.setBackgroundResource(R.drawable.shape_mark_person_bg);
                    break;
                case Const.INFO_TYPE_COM:
                    viewHolder.txtMark.setText("企业");
                    viewHolder.txtMark.setBackgroundResource(R.drawable.shape_mark_enterprise_bg);
                    break;
                case Const.INFO_TYPE_OFFICIAL:
                    viewHolder.txtMark.setText("官方");
                    viewHolder.txtMark.setBackgroundResource(R.drawable.shape_mark_official_bg);
                    viewHolder.tvCompanyName.setText(item.getOfficial_name());
                    break;
                default:
                    break;
            }
        }

    }

    static class ViewHolder {
        @Bind(R.id.recycler_view)
        MyRecyclerView myRecyclerView;
        @Bind(R.id.ll_line)
        LinearLayout llLine;
        @Bind(R.id.tv_company_name)
        TextView tvCompanyName;
        @Bind(R.id.iv_logo)
        ImageView ivLogo;
        @Bind(R.id.tv_position)
        TextView tvPosition;
        @Bind(R.id.tv_date)
        TextView tvDate;
        @Bind(R.id.tv_enterprise_area)
        TextView tvEnterpriseArea;
        @Bind(R.id.tv_price)
        TextView tvPrice;
        @Bind(R.id.tv_location)
        TextView tvLocation;
        @Bind(R.id.imgViCertiGreen)
        ImageView imgViCertiGreen;
        @Bind(R.id.imgViCertiBlue)
        ImageView imgViCertiBlue;
        @Bind(R.id.imgViCertiYellow)
        ImageView imgViCertiYellow;
        @Bind(R.id.txtMark)
        TextView txtMark;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
