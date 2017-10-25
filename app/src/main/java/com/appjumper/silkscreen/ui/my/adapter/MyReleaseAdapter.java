package com.appjumper.silkscreen.ui.my.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.appjumper.silkscreen.bean.MyRelease;
import com.appjumper.silkscreen.util.AppTool;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

/**
 * 我的发布adapter
 * Created by Botx on 2017/7/31.
 */

public class MyReleaseAdapter extends BaseQuickAdapter<MyRelease, BaseViewHolder> {

    private String [] markArr = {"1", "2", "3", "9"};


    public MyReleaseAdapter(@LayoutRes int layoutResId, @Nullable List<MyRelease> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyRelease item) {
        helper.setText(R.id.txtTitle, item.getTitle())
                .addOnClickListener(R.id.txtRefresh)
                .addOnClickListener(R.id.txtDelete)
                .addOnClickListener(R.id.txtEdit)
                .addOnClickListener(R.id.txtShare)
                .addOnClickListener(R.id.txtHandle);


        TextView txtRefresh = helper.getView(R.id.txtRefresh);
        TextView txtDelete = helper.getView(R.id.txtDelete);
        TextView txtHandle = helper.getView(R.id.txtHandle);
        TextView txtState = helper.getView(R.id.txtState);
        if (item.getType().equals("9")) {
            txtRefresh.setText("编辑");
            int status = Integer.valueOf(item.getExamine_status());
            switch (status) {
                case Const.ASKBUY_AUDITING:
                    txtRefresh.setText("编辑");
                    txtRefresh.setVisibility(View.VISIBLE);
                    txtDelete.setVisibility(View.VISIBLE);
                    txtHandle.setVisibility(View.GONE);
                    txtState.setText("审核中");
                    txtState.setBackgroundColor(mContext.getResources().getColor(R.color.theme_color));
                    break;
                case Const.ASKBUY_REFUSE:
                    txtRefresh.setText("编辑");
                    txtRefresh.setVisibility(View.VISIBLE);
                    txtDelete.setVisibility(View.VISIBLE);
                    txtHandle.setVisibility(View.GONE);
                    txtState.setText("审核失败");
                    txtState.setBackgroundColor(mContext.getResources().getColor(R.color.red_color));
                    break;
                case Const.ASKBUY_OFFERING:
                    txtRefresh.setVisibility(View.INVISIBLE);
                    txtDelete.setVisibility(View.INVISIBLE);
                    txtHandle.setText("查看报价");
                    txtHandle.setVisibility(View.VISIBLE);
                    txtState.setText("报价中");
                    txtState.setBackgroundColor(mContext.getResources().getColor(R.color.theme_color));
                    break;
                case Const.ASKBUY_PAYED_SUB:
                    txtRefresh.setVisibility(View.INVISIBLE);
                    txtDelete.setVisibility(View.INVISIBLE);
                    txtHandle.setText("查看订单");
                    txtHandle.setVisibility(View.VISIBLE);
                    txtState.setText("已支付定金");
                    txtState.setBackgroundColor(mContext.getResources().getColor(R.color.theme_color));
                    break;
                case Const.ASKBUY_PAYED_ALL:
                    txtRefresh.setVisibility(View.INVISIBLE);
                    txtDelete.setVisibility(View.INVISIBLE);
                    txtHandle.setText("查看订单");
                    txtHandle.setVisibility(View.VISIBLE);
                    txtState.setText("已全额支付");
                    txtState.setBackgroundColor(mContext.getResources().getColor(R.color.theme_color));
                    break;
                default:
                    txtRefresh.setVisibility(View.INVISIBLE);
                    txtDelete.setVisibility(View.INVISIBLE);
                    txtHandle.setText("查看订单");
                    txtHandle.setVisibility(View.VISIBLE);
                    txtState.setText("已发货");
                    txtState.setBackgroundColor(mContext.getResources().getColor(R.color.theme_color));
                    break;
            }
        } else {
            txtRefresh.setText("刷新");
            txtRefresh.setVisibility(View.VISIBLE);
            txtDelete.setVisibility(View.VISIBLE);
            txtHandle.setVisibility(View.GONE);

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
        }


        /*
         * 图片
         */
        String imgUrl = "";
        if (Arrays.asList(markArr).contains(item.getType())) {
            List<Avatar> imgList = item.getImg_list();
            if (imgList != null && imgList.size() > 0) {
                imgUrl = imgList.get(0).getSmall();
            } else {
                Avatar img = item.getImg();
                if (img != null)
                    imgUrl = img.getSmall();
            }

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
            case "9"://求购
                imgViTag.setImageResource(R.mipmap.my_release_tag_askbuy);
                break;
            default:
                imgViTag.setImageDrawable(null);
                break;
        }

    }

}
