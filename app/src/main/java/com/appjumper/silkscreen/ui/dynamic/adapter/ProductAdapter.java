package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Product;
import com.appjumper.silkscreen.bean.Spec;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 产品adapter
 * Created by Botx on 2017/5/17.
 */

public class ProductAdapter extends BaseQuickAdapter<Product, BaseViewHolder> {

    public ProductAdapter(int layoutResId, List<Product> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Product item) {
        Picasso.with(mContext)
                .load(item.getImg_list().get(0).getSmall())
                .placeholder(R.mipmap.img_error)
                .error(R.mipmap.img_error)
                .into((ImageView) helper.getView(R.id.imgViHead));

        TextView txtName = helper.getView(R.id.txtName);
        if (item.getEnterprise_auth_status().equals("2"))
            txtName.setMaxWidth(DisplayUtil.dip2px(mContext, 98));
        else
            txtName.setMaxWidth(DisplayUtil.dip2px(mContext, 138));

        helper.setText(R.id.txtTitle, item.getProduct_name() + item.getService_type_name())
                .setText(R.id.txtName, item.getEnterprise_name())
                .setText(R.id.txtTime, item.getCreate_time().replaceAll("-", "\\.").substring(0, 16))
                .setVisible(R.id.imgViCertiGreen, item.getAuth_status().equals("2"))
                .setVisible(R.id.imgViCertiBlue, item.getEnterprise_auth_status().equals("2"))
                .setVisible(R.id.imgViCertiYellow, item.getEnterprise_productivity_auth_status().equals("2"));

        List<Spec> spec = item.getService_spec();
        String str="";
        for(int i=0;i<spec.size();i++){
            if(i<4){
                str+=spec.get(i).getName()+spec.get(i).getValue();
                if(i!=(spec.size()-1)){
                    str+=", ";
                }
            }
        }

        helper.setText(R.id.txtSubTitle, str);

    }

}
