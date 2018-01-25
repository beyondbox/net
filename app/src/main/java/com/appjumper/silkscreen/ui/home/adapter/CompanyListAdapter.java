package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.Enterprise;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.ui.dynamic.adapter.ImageAdapter;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * 厂家列表adapter
 * Created by Botx on 2018/1/12.
 */

public class CompanyListAdapter extends BaseQuickAdapter<Enterprise, BaseViewHolder> {

    private int serviceType = Const.SERVICE_TYPE_ORDER;


    public CompanyListAdapter(@LayoutRes int layoutResId, @Nullable List<Enterprise> data, int serviceType) {
        super(layoutResId, data);
        this.serviceType = serviceType;
    }

    @Override
    protected void convert(BaseViewHolder helper, Enterprise item) {
        Picasso.with(mContext)
                .load(item.getEnterprise_logo().getSmall())
                .placeholder(R.mipmap.icon_company)
                .error(R.mipmap.icon_company)
                .into((ImageView) helper.getView(R.id.imgViHead));

        helper.setText(R.id.txtTitle, item.getEnterprise_name())
                .setVisible(R.id.imgViTag, false)
                .setVisible(R.id.imgViCertiGreen, item.getUser_auth_status().equals("2"))
                .setVisible(R.id.imgViCertiBlue, item.getEnterprise_auth_status().equals("2"))
                .setVisible(R.id.imgViCertiYellow, item.getEnterprise_productivity_auth_status().equals("2"));


        TextView txtTitle = helper.getView(R.id.txtTitle);
        if (item.getEnterprise_auth_status().equals("2"))
            txtTitle.setMaxWidth(DisplayUtil.dip2px(mContext, 170));
        else if (item.getUser_auth_status().equals("2"))
            txtTitle.setMaxWidth(DisplayUtil.dip2px(mContext, 210));
        else
            txtTitle.setMaxWidth(DisplayUtil.dip2px(mContext, 250));


        TextView txtSubTitle = helper.getView(R.id.txtSubTitle);
        List<Product> serviceList = item.getService();
        if (serviceList != null && serviceList.size() > 0) {
            String service = "主营产品: ";
            for (int i = 0; i < serviceList.size(); i++) {
                Product product = serviceList.get(i);
                String typeStr = "";
                int typeInt = Integer.valueOf(product.getType());
                switch (typeInt) {
                    case Const.SERVICE_TYPE_ORDER:
                        typeStr = "订做";
                        break;
                    case Const.SERVICE_TYPE_PROCESS:
                        typeStr = "加工";
                        break;
                    case Const.SERVICE_TYPE_STOCK:
                        typeStr = "现货";
                        break;
                }
                service += product.getProduct_name() + typeStr;
                if (i != serviceList.size() - 1) service += "、";
            }

            txtSubTitle.setText(service);
        } else {
            txtSubTitle.setText("");
        }


        ImageView imgViTag = helper.getView(R.id.imgViTag);
        boolean isTop = false;
        if (serviceType == Const.SERVICE_TYPE_ORDER)
            isTop = item.getIs_top_custom().equals("1");
        else
            isTop = item.getIs_top_machining().equals("1");

        if (isTop) { //置顶标签
            imgViTag.setImageResource(R.mipmap.tag_top);
            imgViTag.setVisibility(View.VISIBLE);
        } else { //新入驻标签
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(calendar.HOUR_OF_DAY);
            long mill = 1000 * 60 * 60 * (hour + 24);
            long target = calendar.getTimeInMillis() - mill;
            if (AppTool.getTimeMs(item.getCreate_time(), "yyyy-MM-dd HH:mm:ss") > target) {
                imgViTag.setImageResource(R.mipmap.tag_new);
                imgViTag.setVisibility(View.VISIBLE);
            }
        }



        final List<Avatar> imgList = item.getImg_list();
        if (imgList != null && imgList.size() > 0) {
            helper.setVisible(R.id.recyclerImage, true);
            RecyclerView recyclerImage = helper.getView(R.id.recyclerImage);
            recyclerImage.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            ImageAdapter adapter = new ImageAdapter(R.layout.item_recycler_company_image, imgList);
            recyclerImage.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    ArrayList<String> urls = new ArrayList<String>();
                    for (Avatar avatar : imgList) {
                        urls.add(avatar.getOrigin());
                    }
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
                    intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
                    mContext.startActivity(intent);
                }
            });
        } else {
            helper.setVisible(R.id.recyclerImage, false);
        }

    }

}
