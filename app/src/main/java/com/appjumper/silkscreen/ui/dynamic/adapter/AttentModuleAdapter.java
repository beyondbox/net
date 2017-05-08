package com.appjumper.silkscreen.ui.dynamic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.base.BaseRecyclerAdapter;
import com.appjumper.silkscreen.bean.AttentModule;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 关注的模块adapter
 * Created by Botx on 2017/4/5.
 */

public class AttentModuleAdapter extends BaseRecyclerAdapter<AttentModule> {
    /**
     * 删除标签
     */
    public static final int TAG_DELETE = 20;
    /**
     * 添加标签
     */
    public static final int TAG_ADD = 21;

    private boolean isEditMode = false;


    public AttentModuleAdapter(Context context, List<AttentModule> list) {
        super(context, list);
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (isEditMode()) {
            return super.getItemCount() + 1;
        }
        return super.getItemCount();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_recycler_attention, null);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder vh = (MyViewHolder) holder;

        if (isEditMode) {
            if (position == getItemCount() - 1) {
                vh.txtAdd.setVisibility(View.VISIBLE);
                vh.imgViClose.setVisibility(View.INVISIBLE);
            } else {
                vh.txtAdd.setVisibility(View.GONE);
                vh.imgViClose.setVisibility(View.VISIBLE);
                vh.txtName.setText(list.get(position).getName());
            }
        } else {
            vh.txtName.setText(list.get(position).getName());
            vh.imgViClose.setVisibility(View.INVISIBLE);
            vh.txtAdd.setVisibility(View.GONE);
        }

        if (onWhichClickListener != null) {
            vh.imgViClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWhichClickListener.onWhichClick(v, position, TAG_DELETE);
                }
            });

            vh.txtAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWhichClickListener.onWhichClick(v, position, TAG_ADD);
                }
            });
        }

    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.txtName)
        TextView txtName;
        @Bind(R.id.txtAdd)
        TextView txtAdd;
        @Bind(R.id.imgViClose)
        ImageView imgViClose;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
