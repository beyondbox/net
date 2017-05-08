package com.appjumper.silkscreen.ui.my.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.MyBaseAdapter;

import java.util.List;

/**
 * Created by Botx on 2017/4/20.
 */

public class SpecStockListAdapter extends MyBaseAdapter<String> {

    public static final int TAG_CLOSE = 1;


    private OnCreateItemViewListener onCreateItemViewListener;

    public SpecStockListAdapter(Context context, List<String> list) {
        super(context, list);
    }

    public void setOnCreateItemViewListener(OnCreateItemViewListener onCreateItemViewListener) {
        this.onCreateItemViewListener = onCreateItemViewListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;

        if (onCreateItemViewListener != null) {
            view = onCreateItemViewListener.onCreateView();
        }

        if (view != null) {
            TextView txtSpecNumber = (TextView) view.findViewById(R.id.txtSpecNumber);
            txtSpecNumber.setText("规格" + (position + 2));

            ImageView imgViClose = (ImageView) view.findViewById(R.id.imgViClose);
            if (onWhichClickListener != null) {
                imgViClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onWhichClickListener.onWhichClick(v, position, TAG_CLOSE);
                    }
                });
            }
        }

        return view;
    }


    public interface OnCreateItemViewListener {
        public View onCreateView();
    }
}
