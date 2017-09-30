package com.appjumper.silkscreen.ui.home.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.LineList;
import com.appjumper.silkscreen.util.Const;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 物流货站列表adapter
 * Created by botx on 2017/9/30.
 */

public class LineListAdapter extends BaseQuickAdapter<LineList, BaseViewHolder> {

    public LineListAdapter(@LayoutRes int layoutResId, @Nullable List<LineList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LineList item) {
        TextView txtMark = helper.getView(R.id.txtMark);
        if (!TextUtils.isEmpty(item.getLine_type())) {
            int infoType = Integer.valueOf(item.getLine_type());
            switch (infoType) {
                case Const.INFO_TYPE_PER:
                    txtMark.setText("个人");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_person_bg);
                    break;
                case Const.INFO_TYPE_COM:
                    txtMark.setText("企业");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_enterprise_bg);
                    break;
                case Const.INFO_TYPE_OFFICIAL:
                    txtMark.setText("官方");
                    txtMark.setBackgroundResource(R.drawable.shape_mark_official_bg);
                    break;
                default:
                    break;
            }
        }

        helper.setText(R.id.tv_name, item.getOfficial_name())
                .setText(R.id.tv_form_to, "线路：" + item.getFrom() + "-" + item.getTo())
                .setText(R.id.tv_passby, "途径：" + item.getPassby_name());
    }

}
