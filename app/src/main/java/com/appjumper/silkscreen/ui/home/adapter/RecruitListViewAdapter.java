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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.RecruitList;
import com.appjumper.silkscreen.util.Const;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yc on 2015/6/29.
 * 招聘
 */
public class RecruitListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<RecruitList> list;

    public RecruitListViewAdapter(Context context, List<RecruitList> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_recruit, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        RecruitList item = list.get(position);
//        if(item.getLocation()!=null){
//        }
        viewHolder.tv_address.setText(item.getPlace());
        viewHolder.tvJobPosition.setText(item.getName());
        if (item.getSalary().equals("面议"))
            viewHolder.tvSalary.setText("￥"+item.getSalary());
        else
            viewHolder.tvSalary.setText("￥"+item.getSalary()+"元/月");
        viewHolder.tvExperience.setText(item.getExperience() + "年  " + item.getEducation());
        viewHolder.tvDate.setText(item.getCreate_time().substring(5, 16));
        if (item.getAuth_status() != null && item.getAuth_status().equals("2")) {
            viewHolder.img_auth_status.setVisibility(View.VISIBLE);
        } else {
            viewHolder.img_auth_status.setVisibility(View.GONE);
        }
        if (item.getEnterprise_auth_status() != null && item.getEnterprise_auth_status().equals("2")) {
            viewHolder.tv_enterprise_auth_status.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_enterprise_auth_status.setVisibility(View.GONE);
        }
        if (item.getEnterprise_productivity_auth_status() != null && item.getEnterprise_productivity_auth_status().equals("2")) {
            viewHolder.tv_enterprise_productivity_auth_status.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_enterprise_productivity_auth_status.setVisibility(View.GONE);
        }
        if (position == (list.size() - 1)) {
            viewHolder.llLine.setVisibility(View.GONE);
        } else {
            viewHolder.llLine.setVisibility(View.VISIBLE);
        }


        if (TextUtils.isEmpty(item.getEnterprise_id()))
            viewHolder.tvEnterpriseName.setText(item.getUser_nicename());
        else
            viewHolder.tvEnterpriseName.setText(item.getEnterprise_name());


        if (!TextUtils.isEmpty(item.getRecruit_type())) {
            int infoType = Integer.valueOf(item.getRecruit_type());
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
                    break;
                default:
                    break;
            }
        }

    }

    static class ViewHolder {
        @Bind(R.id.ll_line)
        LinearLayout llLine;
        @Bind(R.id.tv_address)//地址
                TextView tv_address;
        @Bind(R.id.tv_enterprise_name)//公司名称
                TextView tvEnterpriseName;
        @Bind(R.id.img_auth_status)//个人认证
        ImageView img_auth_status;
        @Bind(R.id.tv_enterprise_auth_status)//企
                ImageView tv_enterprise_auth_status;
        @Bind(R.id.tv_enterprise_productivity_auth_status)//力
                ImageView tv_enterprise_productivity_auth_status;
        @Bind(R.id.tv_date)//发布时间
                TextView tvDate;
        @Bind(R.id.tv_job_position)//职位
                TextView tvJobPosition;
        @Bind(R.id.tv_salary)//薪水
                TextView tvSalary;
        @Bind(R.id.tv_experience)
        TextView tvExperience;
        @Bind(R.id.txtMark)
                TextView txtMark;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
