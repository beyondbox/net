package com.appjumper.silkscreen.ui.my.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;

import java.util.List;

/**
 * 材质adapter
 */
public class MaterialRecyclerAdapter extends RecyclerView.Adapter<MaterialRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private int currentSelectPosition;
    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();
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
    private List<String> choiceList;

    public MaterialRecyclerAdapter(Context context, List<String> choiceList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.choiceList = choiceList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        TextView tvChoice;
    }

    @Override
    public int getItemCount() {
        return choiceList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_rv_choice,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvChoice = (TextView) view
                .findViewById(R.id.tv_choice);
        return viewHolder;
    }

    public String getCurrentSelectItem() {
        return choiceList.get(currentSelectPosition);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if (currentSelectPosition == position) {
            viewHolder.tvChoice.setTextColor(mContext.getResources().getColor(R.color.while_color));
            viewHolder.tvChoice.setBackgroundResource(R.drawable.choice_background);
        } else {
            viewHolder.tvChoice.setTextColor(mContext.getResources().getColor(R.color.black_color));
            viewHolder.tvChoice.setBackgroundResource(R.drawable.choice_white_background);
        }
        viewHolder.tvChoice.setText(choiceList.get(position));

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
