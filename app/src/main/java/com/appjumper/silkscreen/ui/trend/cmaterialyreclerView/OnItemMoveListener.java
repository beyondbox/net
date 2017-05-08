package com.appjumper.silkscreen.ui.trend.cmaterialyreclerView;

/**
 * Item移动后 触发
 */
public interface OnItemMoveListener {
    void onItemMove(int fromPosition, int toPosition);
    boolean onItemStick(int position);
}
