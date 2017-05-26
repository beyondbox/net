package com.appjumper.silkscreen.ui.home.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Product;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 搜索结果——产品
 * Created by Botx on 2017/4/10.
 */

public class SearchProductAdapter extends BaseQuickAdapter<Product, BaseViewHolder> {

    public SearchProductAdapter(int layoutResId, List<Product> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Product item) {
        Picasso.with(mContext)
                .load(item.getImg_list().get(0).getSmall())
                .placeholder(R.mipmap.img_error)
                .error(R.mipmap.img_error)
                .into((ImageView) helper.getView(R.id.iv_logo));

        helper.setText(R.id.tv_name, item.getProduct_name())
                .setText(R.id.tv_distance, item.getDistance()+"km")
                .setText(R.id.tv_company_name, item.getEnterprise_name())
                .setText(R.id.tv_service, "提供"+item.getService_type_name()+"服务");

        if (item.getAuth_status() != null && item.getAuth_status().equals("2")) {
            helper.setVisible(R.id.img_auth_status, true);
        }
        if (item.getEnterprise_auth_status() != null && item.getEnterprise_auth_status().equals("2")) {
            helper.setVisible(R.id.img_enterprise_auth_status, true);
        }
        if (item.getEnterprise_productivity_auth_status() != null && item.getEnterprise_productivity_auth_status().equals("2")) {
            helper.setVisible(R.id.img_enterprise_productivity_auth_status, true);
        }

        if (TextUtils.isEmpty(item.getProduct_alias())) {
            helper.setText(R.id.tv_aliasname, "");
        } else {
            helper.setText(R.id.tv_aliasname, "（"+item.getProduct_alias()+"）");
        }
    }
}
