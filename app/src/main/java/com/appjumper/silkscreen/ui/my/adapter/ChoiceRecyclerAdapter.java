package com.appjumper.silkscreen.ui.my.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.ProductType;

import java.util.List;

/**
 * 选择adapter
 */
public class ChoiceRecyclerAdapter extends RecyclerView.Adapter<ChoiceRecyclerAdapter.ViewHolder> {
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
    private List<ProductType> choiceList;

    public ChoiceRecyclerAdapter(Context context, List<ProductType> choiceList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.choiceList = choiceList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        TextView tvChoice;
        LinearLayout l_choice;
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
        viewHolder.l_choice = (LinearLayout) view
                .findViewById(R.id.l_choice);
        return viewHolder;
    }

    public String getCurrentSelectItem() {
        return choiceList.get(currentSelectPosition).getId();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if (currentSelectPosition == position) {
            viewHolder.tvChoice.setTextColor(mContext.getResources().getColor(R.color.while_color));
            viewHolder.l_choice.setBackgroundResource(R.color.theme_color);
        } else {
            viewHolder.tvChoice.setTextColor(mContext.getResources().getColor(R.color.black_color));
            viewHolder.l_choice.setBackgroundResource(R.color.while_color);
        }
        viewHolder.tvChoice.setText(choiceList.get(position).getName());

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
