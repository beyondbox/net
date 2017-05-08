package com.appjumper.silkscreen.ui.trend.cmaterialyreclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.MaterProduct;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lcc on 2016/11/7.
 */
public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemMoveListener {
    private Context context;
    private RecyclerView recyclerView;
    private List<MaterProduct> data;

    public void setData(List<MaterProduct> data) {
        this.data = data;
    }

    /**
     * CLICK 表示点击跑到第一位
     * DRAG 表示拖动
     */
    public static enum SlideStyle {
        BOTH, DRAG, CLICK
    }

    public SlideStyle slideStyle = SlideStyle.BOTH;
    // 我的频道 标题部分
    public static final int TYPE_MY_CHANNEL_HEADER = 0;
    // 我的频道
    public static final int TYPE_MY = 1;
    // 其他频道 标题部分
    public static final int TYPE_OTHER_CHANNEL_HEADER = 2;
    // 其他频道
    public static final int TYPE_OTHER = 3;

    // 我的频道之前的header数量  该demo中 即标题部分 为 1
    private static final int COUNT_PRE_MY_HEADER = 1;
    // 其他频道之前的header数量  该demo中 即标题部分 为 COUNT_PRE_MY_HEADER + 1
    private static final int COUNT_PRE_OTHER_HEADER = COUNT_PRE_MY_HEADER + 1;

    private static final long ANIM_TIME = 360L;

    // touch 点击开始时间
    private long startTime;
    // touch 间隔时间  用于分辨是否是 "点击"
    private static final long SPACE_TIME = 100;

    private LayoutInflater mInflater;
    private ItemTouchHelper mItemTouchHelper;

    // 是否为 编辑 模式  当前版本不需要管
    private boolean isEditMode = true;

    private List<MaterProduct> mMyChannelItems, mOtherChannelItems;

    // 我的频道点击事件
    private OnMyChannelItemClickListener mChannelItemClickListener;

    private ChannelAdapter(Context context, ItemTouchHelper helper, List<MaterProduct> mMyChannelItems, List<MaterProduct> mOtherChannelItems) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mItemTouchHelper = helper;
        this.mMyChannelItems = mMyChannelItems;
        this.mOtherChannelItems = mOtherChannelItems;
        if (mMyChannelItems.size() <= 3) {
            isEditMode = false;
        }
//        setOnMyChannelItemClickListener(new OnMyChannelItemClick());
    }

    public List<MaterProduct> getmOtherChannelItems() {
        return mOtherChannelItems;
    }

    public List<MaterProduct> getmMyChannelItems() {
        return mMyChannelItems;
    }

    /**
     * 目前使用这个
     *
     * @param context
     * @param helper
     * @param mMyChannelItems
     * @param slideStyle
     */
    public ChannelAdapter(Context context, ItemTouchHelper helper, List<MaterProduct> mMyChannelItems, SlideStyle slideStyle) {
        this(context, helper, mMyChannelItems, new ArrayList<MaterProduct>());
        this.slideStyle = slideStyle;
    }

    public ChannelAdapter(Context context, ItemTouchHelper helper, List<MaterProduct> mMyChannelItems, List<MaterProduct> mOtherChannelItems, SlideStyle slideStyle) {
        this(context, helper, mMyChannelItems, mOtherChannelItems);
        this.slideStyle = slideStyle;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {    // 我的频道 标题部分
            return TYPE_MY_CHANNEL_HEADER;
        } else if (position == mMyChannelItems.size() + 1) {    // 其他频道 标题部分
            return TYPE_OTHER_CHANNEL_HEADER;
        } else if (position > 0 && position < mMyChannelItems.size() + 1) {
            return TYPE_MY;
        } else {
            return TYPE_OTHER;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view;
        if (this.recyclerView == null) {
            this.recyclerView = (RecyclerView) parent;
        }
        switch (viewType) {
            case TYPE_MY_CHANNEL_HEADER:
                view = mInflater.inflate(R.layout.item_my_channel_header, parent, false);
                final MyChannelHeaderViewHolder holder = new MyChannelHeaderViewHolder(view);
//                holder.tvBtnEdit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!isEditMode) {
//                            startEditMode((RecyclerView) parent);
//                            holder.tvBtnEdit.setText(R.string.finish);
//                        } else {
//
//                            cancelEditMode((RecyclerView) parent);
//                            holder.tvBtnEdit.setText(R.string.edit);
//                        }
//                        if (context != null && context instanceof Activity) {
//                            ((Activity) context).finish();
//                        }
//                    }
//                });
                return holder;

            case TYPE_MY:
                view = mInflater.inflate(R.layout.item_my2, parent, false);
                final MyViewHolder myHolder = new MyViewHolder(view);
                View.OnClickListener myclick = new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        int position = myHolder.getAdapterPosition();
                        MaterProduct channel = (MaterProduct) myHolder.textView.getTag();
                        if (isEditMode && channel.isAllowDelete().equals("1")) {
                            RecyclerView recyclerView = ((RecyclerView) parent);
                            View targetView = recyclerView.getLayoutManager().findViewByPosition(mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER);
                            View currentView = recyclerView.getLayoutManager().findViewByPosition(position);
                            // 如果targetView不在屏幕内,则indexOfChild为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                            // 如果在屏幕内,则添加一个位移动画
                            if (recyclerView.indexOfChild(targetView) >= 0) {
                                int targetX, targetY;

                                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                                int spanCount = ((GridLayoutManager) manager).getSpanCount();

                                // 移动后 高度将变化 (我的频道Grid 最后一个item在新的一行第一个)
                                if ((mMyChannelItems.size() - COUNT_PRE_MY_HEADER) % spanCount == 0) {
                                    View preTargetView = recyclerView.getLayoutManager().findViewByPosition(mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER - 1);
                                    targetX = preTargetView.getLeft();
                                    targetY = preTargetView.getTop();
                                } else {
                                    targetX = targetView.getLeft();
                                    targetY = targetView.getTop();
                                }

                                moveMyToOther(myHolder);
                                startAnimation(recyclerView, currentView, targetX, targetY);

                            } else {
                                moveMyToOther(myHolder);
                            }
                        } else {
                            if (mChannelItemClickListener != null) {
                                mChannelItemClickListener.onItemClick(v, position - COUNT_PRE_MY_HEADER, myHolder);
                            }
                        }
                    }
                };
                myHolder.imgEdit.setOnClickListener(myclick);
                myHolder.textView.setOnClickListener(myclick);

                myHolder.textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        if (!isEditMode) {
                            RecyclerView recyclerView = ((RecyclerView) parent);
                            startEditMode(recyclerView);

//                            // header 按钮文字 改成 "完成"
//                            View view = recyclerView.getChildAt(0);
//                            if (view == recyclerView.getLayoutManager().findViewByPosition(0)) {
//                                TextView tvBtnEdit = (TextView) view.findViewById(R.id.tv_btn_edit);
//                                tvBtnEdit.setText("完成");
//                            }
                        }
                        int position = myHolder.getAdapterPosition() - COUNT_PRE_MY_HEADER;
                        MaterProduct target = mMyChannelItems.get(position);
                        if (target.getIsIndex()==false&&(slideStyle == SlideStyle.BOTH || slideStyle == SlideStyle.DRAG)) {
                            mItemTouchHelper.startDrag(myHolder);
                        }
                        if (slideStyle == SlideStyle.BOTH || slideStyle == SlideStyle.DRAG) {
                            mItemTouchHelper.startDrag(myHolder);
                        }
                        return true;
                    }
                });

//                myHolder.textView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if (isEditMode) {
//                            switch (MotionEventCompat.getActionMasked(event)) {
//                                case MotionEvent.ACTION_DOWN:
//                                    startTime = System.currentTimeMillis();
//                                    break;
//                                case MotionEvent.ACTION_MOVE:
//                                    if (System.currentTimeMillis() - startTime > SPACE_TIME) {
//                                        mItemTouchHelper.startDrag(myHolder);
//                                    }
//                                    break;
//                                case MotionEvent.ACTION_CANCEL:
//                                case MotionEvent.ACTION_UP:
//                                    startTime = 0;
//                                    break;
//                            }
//
//                        }
//                        return false;
//                    }
//                });
                return myHolder;

            case TYPE_OTHER_CHANNEL_HEADER:
                view = mInflater.inflate(R.layout.item_other_channel_header, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };

            case TYPE_OTHER:
                view = mInflater.inflate(R.layout.item_other, parent, false);
                final OtherViewHolder otherHolder = new OtherViewHolder(view);
                otherHolder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (isEditMode) {
                        RecyclerView recyclerView = ((RecyclerView) parent);
                        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                        int currentPiosition = otherHolder.getAdapterPosition();
                        // 如果RecyclerView滑动到底部,移动的目标位置的y轴 - height
                        View currentView = manager.findViewByPosition(currentPiosition);
                        // 目标位置的前一个item  即当前MyChannel的最后一个
//                        int targetPosition = COUNT_PRE_MY_HEADER + mMyChannelItems.size();
//                        CharSequence text = otherHolder.textView.getText();
//                        for (int i = 0; i < data.size(); i++) {
//                            Channel item = data.get(i);
//                            if (text.equals(item.getName())) {
//                                break;
//                            }
//                            if(mOtherChannelItems.indexOf(item)==-1){
//                                targetPosition++;
//                            }
//                        }
//                        targetPosition+=COUNT_PRE_MY_HEADER;
                        int targetPosition = 0;
                        if (mOtherChannelItems.get(currentPiosition - COUNT_PRE_OTHER_HEADER - mMyChannelItems.size()).getIsIndex()) {
                            for (int i = 0; i < mMyChannelItems.size(); i++) {
                                MaterProduct item = mMyChannelItems.get(i);
                                if (!item.getIsIndex()) {
                                    break;
                                }
                                if (mOtherChannelItems.indexOf(item) == -1) {
                                    targetPosition++;
                                }
                            }
                            targetPosition += COUNT_PRE_MY_HEADER;
                        }else{
                            targetPosition = COUNT_PRE_MY_HEADER + mMyChannelItems.size();
                        }
                        View preTargetView = manager.findViewByPosition(targetPosition);
                        int targetX = preTargetView.getLeft();
                        int targetY = preTargetView.getTop();
//                        Log.e("Message"," targetPosition:"+targetPosition);
//                        Log.e("Message"," X:"+targetX);
//                        Log.e("Message"," Y:"+targetY);
//                        startAnimation(recyclerView, currentView, targetX, targetY);
//                        if(true){
//                            return;
//                        }
                        // 如果targetView不在屏幕内,则为-1  此时不需要添加动画,因为此时notifyItemMoved自带一个向目标移动的动画
                        // 如果在屏幕内,则添加一个位移动画
                        if (recyclerView.indexOfChild(preTargetView) >= 0) {
//                             targetX = preTargetView.getLeft();
//                             targetY = preTargetView.getTop();
//
//
                            GridLayoutManager gridLayoutManager = ((GridLayoutManager) manager);
                            int spanCount = gridLayoutManager.getSpanCount();
//                            // target 在最后一行第一个
//                            if ((targetPosition - COUNT_PRE_MY_HEADER) % spanCount == 0) {
//                                View targetView = manager.findViewByPosition(targetPosition);
//                                targetX = targetView.getLeft();
//                                targetY = targetView.getTop();
//                            } else {
//                                targetX += preTargetView.getWidth();
//
//                                // 最后一个item可见
//                                if (gridLayoutManager.findLastVisibleItemPosition() == getItemCount() - 1) {
//                                    // 最后的item在最后一行第一个位置
//                                    if ((getItemCount() - 1 - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER) % spanCount == 0) {
//                                        // RecyclerView实际高度 > 屏幕高度 && RecyclerView实际高度 < 屏幕高度 + item.height
//                                        int firstVisiblePostion = gridLayoutManager.findFirstVisibleItemPosition();
//                                        if (firstVisiblePostion == 0) {
//                                            // FirstCompletelyVisibleItemPosition == 0 即 内容不满一屏幕 , targetY值不需要变化
//                                            // // FirstCompletelyVisibleItemPosition != 0 即 内容满一屏幕 并且 可滑动 , targetY值 + firstItem.getTop
//                                            if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
//                                                int offset = (-recyclerView.getChildAt(0).getTop()) - recyclerView.getPaddingTop();
//                                                targetY += offset;
//                                            }
//                                        } else { // 在这种情况下 并且 RecyclerView高度变化时(即可见第一个item的 position != 0),
//                                            // 移动后, targetY值  + 一个item的高度
//                                            targetY += preTargetView.getHeight();
//                                        }
//                                    }
//                                } else {
//                                    System.out.println("current--No");
//                                }
//                            }

                            // 如果当前位置是otherChannel可见的最后一个
                            // 并且 当前位置不在grid的第一个位置
                            // 并且 目标位置不在grid的第一个位置

                            // 则 需要延迟250秒 notifyItemMove , 这是因为这种情况 , 并不触发ItemAnimator , 会直接刷新界面
                            // 导致我们的位移动画刚开始,就已经notify完毕,引起不同步问题
                            if (currentPiosition == gridLayoutManager.findLastVisibleItemPosition()
                                    && (currentPiosition - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER) % spanCount != 0
                                    && (targetPosition - COUNT_PRE_MY_HEADER) % spanCount != 0) {
                                moveOtherToMyWithDelay(otherHolder);
                            } else {
                                moveOtherToMy(otherHolder);
                            }
                            startAnimation(recyclerView, currentView, targetX, targetY);

                        } else {
                            moveOtherToMy(otherHolder);
                        }
//                        }
                    }
                });
                return otherHolder;
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            MyViewHolder myHolder = (MyViewHolder) holder;
            myHolder.textView.setText(mMyChannelItems.get(position - COUNT_PRE_MY_HEADER).getName());
            myHolder.textView.setTag(mMyChannelItems.get(position - COUNT_PRE_MY_HEADER));
            if (mMyChannelItems.get(position - COUNT_PRE_MY_HEADER).isAllowDelete().equals("1")) {
                myHolder.imgEdit.setTag(mMyChannelItems.get(position - COUNT_PRE_MY_HEADER));
            } else {
                myHolder.imgEdit.setTag(null);
            }
            if (isEditMode && mMyChannelItems.get(position - COUNT_PRE_MY_HEADER).isAllowDelete().equals("1")) {
                myHolder.imgEdit.setVisibility(View.VISIBLE);
            } else {
                myHolder.imgEdit.setVisibility(View.INVISIBLE);
            }

        } else if (holder instanceof OtherViewHolder) {

            ((OtherViewHolder) holder).textView.setText(mOtherChannelItems.get(position - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER).getName());

        } else if (holder instanceof MyChannelHeaderViewHolder) {

//            MyChannelHeaderViewHolder headerHolder = (MyChannelHeaderViewHolder) holder;
//            if (isEditMode) {
//                headerHolder.tvBtnEdit.setText(R.string.finish);
//            } else {
//                headerHolder.tvBtnEdit.setText(R.string.edit);
//            }
        }
    }

    @Override
    public int getItemCount() {
        // 我的频道  标题 + 我的频道.size + 其他频道 标题 + 其他频道.size
        return mMyChannelItems.size() + mOtherChannelItems.size() + COUNT_PRE_OTHER_HEADER;
//        return mMyChannelItems.size() + COUNT_PRE_MY_HEADER;
    }

    /**
     * 开始增删动画
     */
    private void startAnimation(RecyclerView recyclerView, final View currentView, float targetX, float targetY) {
        final ViewGroup viewGroup = (ViewGroup) recyclerView.getParent();
        final ImageView mirrorView = addMirrorView(viewGroup, recyclerView, currentView);

        Animation animation = getTranslateAnimator(
                targetX - currentView.getLeft(), targetY - currentView.getTop());
        currentView.setVisibility(View.INVISIBLE);
        mirrorView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewGroup.removeView(mirrorView);
                if (currentView.getVisibility() == View.INVISIBLE) {
                    currentView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 移动到第一个位置
     */
    private void moveToFristPosition(MyViewHolder myHolder) {
        int position = myHolder.getAdapterPosition();

        int startPosition = position - COUNT_PRE_MY_HEADER;
        if (startPosition > mMyChannelItems.size() - 1) {
            return;
        }
        MaterProduct item = mMyChannelItems.get(startPosition);
        mMyChannelItems.remove(startPosition);
//        mMyChannelItems.add(0, item);
        mOtherChannelItems.add(0, item);
        notifyItemMoved(position, mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER);
//        notifyItemMoved(position, COUNT_PRE_MY_HEADER);
    }

    /**
     * 我的频道 移动到 其他频道
     *
     * @param myHolder
     */
    private void moveMyToOther(MyViewHolder myHolder) {
        int position = myHolder.getAdapterPosition();

        int startPosition = position - COUNT_PRE_MY_HEADER;
        if (startPosition > mMyChannelItems.size() - 1) {
            return;
        }
        MaterProduct item = mMyChannelItems.get(startPosition);
        mMyChannelItems.remove(startPosition);
        mOtherChannelItems.add(0, item);
//        if (mMyChannelItems.size() == 3) {
//            cancelEditMode(recyclerView);
//        }
        notifyItemMoved(position, mMyChannelItems.size() + COUNT_PRE_OTHER_HEADER);
    }

    /**
     * 其他频道 移动到 我的频道
     *
     * @param otherHolder
     */
    private void moveOtherToMy(OtherViewHolder otherHolder) {
        int position = processItemRemoveAdd(otherHolder);
        if (position == -1) {
            return;
        }
        CharSequence text = otherHolder.textView.getText();
        int toPosition = 0;
        for (int i = 0; i < mMyChannelItems.size(); i++) {
            MaterProduct item = mMyChannelItems.get(i);
            if (text.equals(item.getName())) {
                toPosition = i + COUNT_PRE_MY_HEADER;
                break;
            }
        }
        if (isEditMode == false) {
            startEditMode(recyclerView);
        }
        Log.e("Message", "position" + toPosition);
        notifyItemMoved(position, toPosition);
    }

    /**
     * 其他频道 移动到 我的频道 伴随延迟
     *
     * @param otherHolder
     */
    private void moveOtherToMyWithDelay(OtherViewHolder otherHolder) {
        final int position = processItemRemoveAdd(otherHolder);
        if (position == -1) {
            return;
        }
        CharSequence text = otherHolder.textView.getText();
        int toPosition = 0;
        for (int i = 0; i < mMyChannelItems.size(); i++) {
            MaterProduct item = mMyChannelItems.get(i);
            if (text.equals(item.getName())) {
                toPosition = i + COUNT_PRE_MY_HEADER;
                break;
            }
        }
        final int finalToPosition = toPosition;
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startEditMode(recyclerView);

                notifyItemMoved(position, finalToPosition);
            }
        }, ANIM_TIME);
    }


    private Handler delayHandler = new Handler();

    private int processItemRemoveAdd(OtherViewHolder otherHolder) {
        int position = otherHolder.getAdapterPosition();

        int startPosition = position - mMyChannelItems.size() - COUNT_PRE_OTHER_HEADER;
        if (startPosition > mOtherChannelItems.size() - 1) {
            return -1;
        }
        MaterProduct item = mOtherChannelItems.get(startPosition);
        mOtherChannelItems.remove(startPosition);
//        mMyChannelItems.clear();
//        for (Channel channel : data) {
//            if (mOtherChannelItems.indexOf(channel) == -1) {
//                mMyChannelItems.add(channel);
//            }
//        }
        if (item.getIsIndex()) {
            int targetPosition = 0;
            for (int i = 0; i < mMyChannelItems.size(); i++) {
                MaterProduct itemTemp = mMyChannelItems.get(i);
                if (!itemTemp.getIsIndex()) {
                    break;
                }
                if (mOtherChannelItems.indexOf(item) == -1) {
                    targetPosition++;
                }
            }
            targetPosition += COUNT_PRE_MY_HEADER;
            mMyChannelItems.add(targetPosition-1, item);
        } else {
            mMyChannelItems.add(item);
        }
//        mMyChannelItems.add(item);
        return position;
    }


    /**
     * 添加需要移动的 镜像View
     */
    private ImageView addMirrorView(ViewGroup parent, RecyclerView recyclerView, View view) {
        /**
         * 我们要获取cache首先要通过setDrawingCacheEnable方法开启cache，然后再调用getDrawingCache方法就可以获得view的cache图片了。
         buildDrawingCache方法可以不用调用，因为调用getDrawingCache方法时，若果cache没有建立，系统会自动调用buildDrawingCache方法生成cache。
         若想更新cache, 必须要调用destoryDrawingCache方法把旧的cache销毁，才能建立新的。
         当调用setDrawingCacheEnabled方法设置为false, 系统也会自动把原来的cache销毁。
         */
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        final ImageView mirrorView = new ImageView(recyclerView.getContext());
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        mirrorView.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(false);
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        int[] parenLocations = new int[2];
        recyclerView.getLocationOnScreen(parenLocations);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        params.setMargins(locations[0], locations[1] - parenLocations[1], 0, 0);
        parent.addView(mirrorView, params);

        return mirrorView;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        MaterProduct item = mMyChannelItems.get(fromPosition - COUNT_PRE_MY_HEADER);
        MaterProduct item2 = mMyChannelItems.get(toPosition - COUNT_PRE_MY_HEADER);
        if (item.getIsIndex() != item2.getIsIndex()) {
        } else {
            mMyChannelItems.remove(fromPosition - COUNT_PRE_MY_HEADER);
            mMyChannelItems.add(toPosition - COUNT_PRE_MY_HEADER, item);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public boolean onItemStick(int position) {
//        Channel target = mMyChannelItems.get(position-COUNT_PRE_MY_HEADER);
//        if(target.getIsIndex()!=null && target.getIsIndex().equals("true")) return true;
        return false;
    }

    /**
     * 开启编辑模式
     *
     * @param parent
     */
    private void startEditMode(RecyclerView parent) {
        isEditMode = true;

        int visibleChildCount = parent.getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            View view = parent.getChildAt(i);
            ImageView imgEdit = (ImageView) view.findViewById(R.id.img_edit);
            if (imgEdit != null && imgEdit.getTag() != null) {
                imgEdit.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 完成编辑模式
     *
     * @param parent
     */
    private void cancelEditMode(RecyclerView parent) {
        isEditMode = false;

        int visibleChildCount = parent.getChildCount();
        for (int i = 0; i < visibleChildCount; i++) {
            View view = parent.getChildAt(i);
            ImageView imgEdit = (ImageView) view.findViewById(R.id.img_edit);
            if (imgEdit != null) {
                imgEdit.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 获取位移动画
     */
    private TranslateAnimation getTranslateAnimator(float targetX, float targetY) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetX,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, targetY);
        // RecyclerView默认移动动画250ms 这里设置360ms 是为了防止在位移动画结束后 remove(view)过早 导致闪烁
        translateAnimation.setDuration(ANIM_TIME);
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }

    public class OnMyChannelItemClick implements OnMyChannelItemClickListener {
        @Override
        public void onItemClick(View v, int position, MyViewHolder myHolder) {
            moveToFristPosition(myHolder);
        }
    }

    interface OnMyChannelItemClickListener {
        void onItemClick(View v, int position, MyViewHolder myHolder);
    }

    public void setOnMyChannelItemClickListener(OnMyChannelItemClickListener listener) {
        this.mChannelItemClickListener = listener;
    }

    /**
     * 我的频道
     */
    class MyViewHolder extends RecyclerView.ViewHolder implements OnDragVHListener {
        private TextView textView;
        private ImageView imgEdit;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv);
            imgEdit = (ImageView) itemView.findViewById(R.id.img_edit);
        }

        /**
         * item 被选中时
         */
        @Override
        public void onItemSelected() {
            //textView.setBackgroundResource(R.drawable.hot_press_background);
            textView.setBackgroundResource(R.drawable.attention_bg);
        }

        /**
         * item 取消选中时
         */
        @Override
        public void onItemFinish() {
            //textView.setBackgroundResource(R.drawable.hot_unpress_background);
            textView.setBackgroundResource(R.drawable.attention_bg);
        }
    }

    /**
     * 其他频道
     */
    class OtherViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public OtherViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv);
        }
    }

    /**
     * 我的频道  标题部分
     */
    class MyChannelHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBtnEdit;

        public MyChannelHeaderViewHolder(View itemView) {
            super(itemView);
//            tvBtnEdit = (TextView) itemView.findViewById(R.id.tv_btn_edit);
        }
    }
}
