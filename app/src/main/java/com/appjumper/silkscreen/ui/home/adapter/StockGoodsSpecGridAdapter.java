package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 发布现货商品--商品材质规格adapter
 * Created by Botx on 2017/9/21.
 */

public class StockGoodsSpecGridAdapter extends MyBaseAdapter<String> {

    private int selectedPosition = 0;

    public StockGoodsSpecGridAdapter(Context context, List<String> list) {
        super(context, list);
    }


    public void changeSelected(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_goods_spec_choice, null);
            vh = new ViewHolder();
            ButterKnife.bind(vh, convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.chkSpec.setText(list.get(position));

        if (position == list.size() - 1)
            vh.rlEdit.setVisibility(View.VISIBLE);
        else
            vh.rlEdit.setVisibility(View.GONE);

        if (selectedPosition == position)
            vh.chkSpec.setSelected(true);
        else
            vh.chkSpec.setSelected(false);


        vh.edtTxtContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((ViewGroup)(v.getParent().getParent())).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                return false;
            }
        });

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((ViewGroup)v).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                return false;
            }
        });


        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.chkSpec)
        TextView chkSpec;
        @Bind(R.id.rlEdit)
        RelativeLayout rlEdit;
        @Bind(R.id.edtTxtContent)
        EditText edtTxtContent;
    }
}
