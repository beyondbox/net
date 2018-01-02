package com.appjumper.silkscreen.ui.common.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;
import com.appjumper.silkscreen.bean.ServiceProduct;
import com.appjumper.silkscreen.util.Const;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 产品选择列表adapter
 * Created by Botx on 2017/3/20.
 */

public class ProductListAdapter extends MyBaseAdapter<ServiceProduct> implements SectionIndexer{

    private boolean isMultiMode = false;
    private String action = "";

    public ProductListAdapter(Context context, List<ServiceProduct> list, String action) {
        super(context, list);
        this.action = action;
    }

    public boolean isMultiMode() {
        return isMultiMode;
    }

    public void setMultiMode(boolean multiMode) {
        isMultiMode = multiMode;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_product_select, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        ServiceProduct product = list.get(position);

        Picasso.with(context)
                .load(product.getImg())
                .resize(DisplayUtil.dip2px(context, 70), DisplayUtil.dip2px(context, 70))
                .centerCrop()
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .placeholder(R.mipmap.img_error)
                .error(R.mipmap.img_error)
                .into(vh.imgViProduct);


        if (isMultiMode) {
            vh.chkSelect.setVisibility(View.VISIBLE);
            if (action.equals(Const.ACTION_ATTENT_PRODUCT_MANAGE))
                vh.chkSelect.setChecked(product.getIs_collection().equals("1"));
            else if (action.equals(Const.ACTION_ADD_PRODUCT))
                vh.chkSelect.setChecked(product.getIs_car().equals("1"));
        } else {
            vh.chkSelect.setVisibility(View.GONE);
        }


        if (getPositionForSection(getSectionForPosition(position)) == position) {
            vh.txtSection.setText(product.getSection().toUpperCase());
            vh.txtSection.setVisibility(View.VISIBLE);
        } else {
            vh.txtSection.setVisibility(View.GONE);
        }


        String name = product.getName();
        if (TextUtils.isEmpty(product.getAlias())) {
            vh.txtTitle.setText(name);
        } else {
            String title = name + " (" + product.getAlias() + ")";
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_gray_color)), name.length(), title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            vh.txtTitle.setText(spannableString);
        }

        if (onWhichClickListener != null) {
            vh.chkSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWhichClickListener.onWhichClick(v, position, 0);
                }
            });
        }

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            int sortkey = getSectionForPosition(i);
            if (sortkey == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int i) {
        return list.get(i).getSection().toUpperCase().charAt(0);
    }

    class ViewHolder {
        @Bind(R.id.txtSection)
        TextView txtSection;
        @Bind(R.id.chkSelect)
        CheckBox chkSelect;
        @Bind(R.id.imgViProduct)
        ImageView imgViProduct;
        @Bind(R.id.txtTitle)
        TextView txtTitle;
    }

}
