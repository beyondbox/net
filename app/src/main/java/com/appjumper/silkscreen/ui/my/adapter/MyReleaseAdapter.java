package com.appjumper.silkscreen.ui.my.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.MyRelease;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.PicassoRoundTransform;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static com.appjumper.silkscreen.R.id.txtState;

/**
 * 我的发布adapter
 * Created by Botx on 2017/7/31.
 */

public class MyReleaseAdapter extends BaseQuickAdapter<MyRelease, BaseViewHolder> {

    private String [] markArr = {"1", "2", "3"};


    public MyReleaseAdapter(@LayoutRes int layoutResId, @Nullable List<MyRelease> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyRelease item) {
        helper.setText(R.id.txtTitle, item.getTitle())
                .addOnClickListener(R.id.txtRefresh)
                .addOnClickListener(R.id.txtDelete)
                .addOnClickListener(R.id.txtEdit)
                .addOnClickListener(R.id.txtShare);

        /*
         * 图片
         */
        String imgUrl = "";
        if (Arrays.asList(markArr).contains(item.getType())) {
            List<Avatar> imgList = item.getImg_list();
            if (imgList != null && imgList.size() > 0)
                imgUrl = imgList.get(0).getSmall();
        } else {
            Avatar img = item.getImg();
            if (img != null)
                imgUrl = img.getSmall();
        }

        Picasso.with(mContext)
                .load(imgUrl)
                .placeholder(R.mipmap.img_error)
                .error(R.mipmap.img_error)
                .into((ImageView) helper.getView(R.id.imgViHead));

        /*
         * 刷新时间
         */
        if (!TextUtils.isEmpty(item.getRenovate_time()))
            //helper.setText(R.id.txtTime, item.getRenovate_time().replaceAll("-", "\\.").substring(5, 16));
            helper.setText(R.id.txtTime, item.getRenovate_time());
        else
            helper.setText(R.id.txtTime, "");

        /*
         * 展示状态
         */
        TextView txtState = helper.getView(R.id.txtState);
        if (!TextUtils.isEmpty(item.getExpiry_date())) {
            if (System.currentTimeMillis() < AppTool.getTimeMs(item.getExpiry_date(), "yy-MM-dd HH:mm:ss")) {
                txtState.setText("展示中");
                txtState.setBackgroundColor(mContext.getResources().getColor(R.color.theme_color));
            } else {
                txtState.setText("已到期");
                txtState.setBackgroundColor(mContext.getResources().getColor(R.color.red_color));
            }
        } else {
            txtState.setText("展示中");
            txtState.setBackgroundColor(mContext.getResources().getColor(R.color.theme_color));
        }

        /*
         * 分类标签
         */
        ImageView imgViTag = helper.getView(R.id.imgViTag);
        switch (item.getType()) {
            case "1"://订做
                imgViTag.setImageResource(R.mipmap.icon_home_order);
                break;
            case "2"://加工
                imgViTag.setImageResource(R.mipmap.icon_home_machine);
                break;
            case "3"://现货
                imgViTag.setImageResource(R.mipmap.icon_home_spot);
                break;
            case "4"://物流
                imgViTag.setImageResource(R.mipmap.icon_home_circuit);
                break;
            case "5"://设备
                imgViTag.setImageResource(R.mipmap.icon_home_facility);
                break;
            case "6"://找车
                imgViTag.setImageResource(R.mipmap.icon_home_car);
                break;
            case "7"://招聘
                imgViTag.setImageResource(R.mipmap.icon_home_recruit);
                break;
            case "8"://厂房
                imgViTag.setImageResource(R.mipmap.icon_home_plant_information);
                break;
            default:
                imgViTag.setImageDrawable(null);
                break;
        }

    }

}
