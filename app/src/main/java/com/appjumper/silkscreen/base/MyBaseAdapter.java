package com.appjumper.silkscreen.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Botx on 2017/3/20.
 */

public class MyBaseAdapter<T> extends BaseAdapter {

    public Context context;
    public List<T> list;
    public OnWhichClickListener onWhichClickListener;

    public MyBaseAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;

        if (this.list == null) {
            this.list = new ArrayList<T>();
        }
    }

    public void refresh(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public interface OnWhichClickListener {
        public void onWhichClick(View view, int position, int tag);
    }

    public void setOnWhichClickListener(OnWhichClickListener onWhichClickListener) {
        this.onWhichClickListener = onWhichClickListener;
    }

}
