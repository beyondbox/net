package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.Enterprise;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 推荐企业adapter
 * Created by Botx on 2017/4/19.
 */

public class RecommendAdapter extends MyBaseAdapter<Enterprise> {

    public RecommendAdapter(Context context, List<Enterprise> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_enterprise, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Enterprise enterprise = list.get(position);

        if (enterprise.getEnterprise_logo() != null && !TextUtils.isEmpty(enterprise.getEnterprise_logo().getSmall())) {
            Picasso.with(context)
                    .load(enterprise.getEnterprise_logo().getSmall())
                    .placeholder(R.mipmap.icon_logo_image61)
                    .error(R.mipmap.icon_logo_image61)
                    .into(vh.imageView);
        } else {
            vh.imageView.setImageResource(R.mipmap.icon_logo_image61);
        }

        vh.txtName.setText(enterprise.getEnterprise_name());
        vh.txtTime.setText("入驻时间 : " + enterprise.getCreate_time().substring(0,10));
        if (TextUtils.isEmpty(enterprise.getDistance()))
            vh.txtDistance.setText("");
        else
            vh.txtDistance.setText(enterprise.getDistance() + "km");


        if (enterprise.getAuth_status() != null && enterprise.getAuth_status().equals("2")) {
            vh.imgViCertiGreen.setVisibility(View.VISIBLE);
        } else {
            vh.imgViCertiGreen.setVisibility(View.GONE);
        }

        if (enterprise.getEnterprise_auth_status() != null && enterprise.getEnterprise_auth_status().equals("2")) {
            vh.imgViCertiBlue.setVisibility(View.VISIBLE);
        } else {
            vh.imgViCertiBlue.setVisibility(View.GONE);
        }

        if (enterprise.getEnterprise_productivity_auth_status() != null && enterprise.getEnterprise_productivity_auth_status().equals("2")) {
            vh.imgViCertiYellow.setVisibility(View.VISIBLE);
        } else {
            vh.imgViCertiYellow.setVisibility(View.GONE);
        }


        String jiagong = enterprise.getJiagong();
        String service = "";
        List<String> list = new ArrayList<>();

        if(!jiagong.equals("0")) {
            list.add("加工");
        }
        if(!enterprise.getDingzuo().equals("0")) {
            list.add("订做");
        }
        if(list.size() > 0) {
            for (int i =0; i<list.size(); i++) {
                if(i != 0) {
                    service += "、";
                }
                service += list.get(i);
            }
            if(!enterprise.getXianhuo().equals("0")) {
                vh.txtService.setText("提供" + service + "服务、有现货");
            } else {
                vh.txtService.setText("提供" + service + "服务");
            }
        } else {
            if(!enterprise.getXianhuo().equals("0")) {
                vh.txtService.setText("有现货");
            }
        }

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.imageView)
        ImageView imageView;
        @Bind(R.id.txtName)
        TextView txtName;
        @Bind(R.id.txtTime)
        TextView txtTime;
        @Bind(R.id.txtService)
        TextView txtService;
        @Bind(R.id.txtDistance)
        TextView txtDistance;
        @Bind(R.id.imgViCertiBlue)
        ImageView imgViCertiBlue;
        @Bind(R.id.imgViCertiYellow)
        ImageView imgViCertiYellow;
        @Bind(R.id.imgViCertiGreen)
        ImageView imgViCertiGreen;
    }

}
