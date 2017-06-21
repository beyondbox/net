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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.appjumper.silkscreen.R.id.view;


/**
 * 公司详情加工
 */
public class CompanyProcessListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Product> list;

    public CompanyProcessListViewAdapter(Context context, List<Product> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if(list==null){
            return 0;
        }else{
            return list.size();
        }
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
            convertView = mInflater.inflate(R.layout.item_lv_company_process, null);
            holder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);//服务名字
            holder.tvDiameter = (TextView) convertView.findViewById(R.id.tv_diameter);//丝径
            holder.tvMesh = (TextView) convertView.findViewById(R.id.tv_mesh);//网孔
            holder.view = convertView.findViewById(view);//线
            holder.ll_service = (LinearLayout) convertView.findViewById(R.id.ll_service);//规格布局
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Product unit = list.get(position);
        List<Spec> spec = unit.getService_spec();

        Picasso.with(mContext).load(unit.getImg_list().get(0).getSmall()).transform(new PicassoRoundTransform()).placeholder(R.mipmap.icon_logo_image61).error(R.mipmap.icon_logo_image61).into(holder.ivLogo);
        holder.tvName.setText(unit.getProduct_name()+unit.getService_type_name());
        if(spec!=null){
            if(spec.size()>=2){
                if(spec.get(0).getFieldinput().equals("radio")){
                    holder.tvDiameter.setText(spec.get(0).getName()+": "+spec.get(0).getValue());
                }else{
                    holder.tvDiameter.setText(spec.get(0).getName()+": "+spec.get(0).getValue()+" "+spec.get(0).getUnit());
                }
                if(spec.get(1).getFieldinput().equals("radio")){
                    holder.tvMesh.setText(spec.get(1).getName()+": "+spec.get(1).getValue());
                }else{
                    holder.tvMesh.setText(spec.get(1).getName()+": "+spec.get(1).getValue()+" "+spec.get(1).getUnit());
                }
            }else if(spec.size()>=1){
                if(spec.get(0).getFieldinput().equals("radio")){
                    holder.tvDiameter.setText(spec.get(0).getName()+": "+spec.get(0).getValue());
                }else{
                    holder.tvDiameter.setText(spec.get(0).getName()+": "+spec.get(0).getValue()+" "+spec.get(0).getUnit());
                }
                holder.tvMesh.setText("");
            }else{
                holder.tvDiameter.setText("");
                holder.tvMesh.setText("");
            }
        }

        if (position == (list.size()-1)) {
            holder.view.setVisibility(View.GONE);
        }else{
            holder.view.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public class ViewHolder {
        ImageView ivLogo;
        TextView tvName, tvDiameter, tvMesh;
        View view;
        LinearLayout ll_service;
    }

}
