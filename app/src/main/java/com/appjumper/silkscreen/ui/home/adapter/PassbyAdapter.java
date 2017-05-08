package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjumper.silkscreen.R;

import java.util.List;

public class PassbyAdapter extends
        RecyclerView.Adapter<PassbyAdapter.ViewHolder> {

    private Context mContext;
    private int currentSelectPosition;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private LayoutInflater mInflater;
    private List<String> list;

    public PassbyAdapter(Context context, List<String> img_list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.list = img_list;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        TextView tvAddress;
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_rv_pass_by,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvAddress = (TextView) view.findViewById(R.id.tv_address_name);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.tvAddress.setText(list.get(i));
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectPosition = i;
                if (mOnItemClickLitener != null) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
                }
                notifyDataSetChanged();
            }
        });
    }

}
