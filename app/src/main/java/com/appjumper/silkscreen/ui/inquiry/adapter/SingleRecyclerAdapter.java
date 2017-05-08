package com.appjumper.silkscreen.ui.inquiry.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;

/**
 * 我的简历选择adapter
 */
public class SingleRecyclerAdapter extends RecyclerView.Adapter<SingleRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private int currentSelectPosition;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void setCurrentSelectPosition(int currentSelectPosition) {
        this.currentSelectPosition = currentSelectPosition;
    }

    private LayoutInflater mInflater;
    private String[] choiceList;

    public SingleRecyclerAdapter(Context context, String[] choiceList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.choiceList = choiceList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        ImageView ivChoice;
        TextView tvChoice;
    }

    @Override
    public int getItemCount() {
        return choiceList.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_lv_material,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.ivChoice = (ImageView) view
                .findViewById(R.id.iv_choice);
        viewHolder.tvChoice = (TextView) view
                .findViewById(R.id.tv_choice);
        return viewHolder;
    }

    public String getCurrentSelectItem() {
        return choiceList[currentSelectPosition];
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if (currentSelectPosition == position) {
            viewHolder.ivChoice.setImageResource(R.mipmap.icon_pitch_on);
        } else {
            viewHolder.ivChoice.setImageResource(R.mipmap.icon_unchecked);
        }
        viewHolder.tvChoice.setText(choiceList[position]);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectPosition = position;
                if (mOnItemClickLitener != null) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, position);
                }
                notifyDataSetChanged();
            }
        });
    }

}
