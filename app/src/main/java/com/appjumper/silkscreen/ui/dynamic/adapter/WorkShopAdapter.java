package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.EquipmentList;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.view.phonegridview.GalleryActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * 出租厂房adapter
 * Created by Botx on 2017/5/17.
 */

public class WorkShopAdapter extends BaseQuickAdapter<EquipmentList, BaseViewHolder> {

    public WorkShopAdapter(int layoutResId, List<EquipmentList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EquipmentList item) {
        helper.setText(R.id.txtTime, item.getCreate_time().replaceAll("-", "\\.").substring(0, 16))
                .setText(R.id.txtTitle, item.getTitle() + "/" + item.getPosition())
                .setText(R.id.txtPrice, item.getPrice() + "元/年")
                .setText(R.id.txtLocation, item.getArea() + "m²,  " + item.getPosition() + "  " + item.getDistance() + "km");

        TextView txtName = helper.getView(R.id.txtName);

        if (TextUtils.isEmpty(item.getEnterprise_name())) { //个人

            ImageView imageView = helper.getView(R.id.imgViHead);
            if (TextUtils.isEmpty(item.getAvatar().getSmall())) {
                imageView.setImageResource(R.mipmap.img_error_head);
            } else {
                Picasso.with(mContext)
                        .load(item.getAvatar().getSmall())
                        .placeholder(R.mipmap.img_error_head)
                        .error(R.mipmap.img_error_head)
                        .into(imageView);
            }

            txtName.setMaxWidth(DisplayUtil.dip2px(mContext, 138));

            helper.setText(R.id.txtName, item.getUser_nicename())
                    .setVisible(R.id.imgViCertiGreen, item.getAuth_status().equals("2"))
                    .setVisible(R.id.imgViCertiBlue, false)
                    .setVisible(R.id.imgViCertiYellow, false);

        } else { //企业
            Picasso.with(mContext)
                    .load(item.getEnterprise_logo().getSmall())
                    .placeholder(R.mipmap.icon_logo_image61)
                    .error(R.mipmap.icon_logo_image61)
                    .into((ImageView) helper.getView(R.id.imgViHead));

            if (item.getEnterprise_auth_status().equals("2"))
                txtName.setMaxWidth(DisplayUtil.dip2px(mContext, 98));
            else
                txtName.setMaxWidth(DisplayUtil.dip2px(mContext, 138));

            helper.setText(R.id.txtName, item.getEnterprise_name())
                    .setVisible(R.id.imgViCertiGreen, item.getAuth_status().equals("2"))
                    .setVisible(R.id.imgViCertiBlue, item.getEnterprise_auth_status().equals("2"))
                    .setVisible(R.id.imgViCertiYellow, item.getEnterprise_productivity_auth_status().equals("2"));
        }



        final List<Avatar> imgList = item.getImg_list();
        if (imgList != null && imgList.size() > 0) {
            helper.setVisible(R.id.recyclerImage, true);
            RecyclerView recyclerImage = helper.getView(R.id.recyclerImage);
            recyclerImage.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            ImageAdapter adapter = new ImageAdapter(R.layout.item_recycler_line_dynamic_image, imgList);
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
