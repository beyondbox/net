package com.appjumper.silkscreen.ui.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.Avatar;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends
        RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

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
    private List<Avatar> list;

    public GalleryAdapter(Context context, List<Avatar> img_list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.list = img_list;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }
        ImageView mImg;
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }else{
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_rv_gallary,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mImg = (ImageView) view.findViewById(R.id.iv_logo);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        Picasso.with(mContext).load(list.get(i).getSmall()).error(R.mipmap.img_error).into(viewHolder.mImg);
        if (i == 0) {
            viewHolder.mImg.setPadding(12, 0, 6, 0);
        } else if (i == (list.size() - 1)) {
            viewHolder.mImg.setPadding(6, 0, 12, 0);
        } else {
            viewHolder.mImg.setPadding(6, 0, 6, 0);
        }
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
