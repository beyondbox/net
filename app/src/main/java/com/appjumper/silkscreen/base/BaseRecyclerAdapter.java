package com.appjumper.silkscreen.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by Botx on 2017/4/5.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    public Context context;
    public List<T> list;
    public OnItemClickListener onItemClickListener;
    public OnItemLongClickListener onItemLongClickListener;
    public OnWhichClickListener onWhichClickListener;

    public BaseRecyclerAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }



    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    public interface OnWhichClickListener {
        public void onWhichClick(View view, int position, int tag);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnWhichClickListener(OnWhichClickListener onWhichClickListener) {
        this.onWhichClickListener = onWhichClickListener;
    }
}
