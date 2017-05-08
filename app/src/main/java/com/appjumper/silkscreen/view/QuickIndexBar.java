package com.appjumper.silkscreen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.appjumper.silkscreen.R;


/**
 * Created by wk on 2016/6/14.
 */
public class QuickIndexBar extends View {
    private static final String[] LETTERS = new String[]{
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z"};
    private Paint mPaint;
    private int mCellWidth;
    private int mHeight;
    private float mCellHeight;


    private OnLetterChangeListener mOnLetterChangeListener;

    public interface OnLetterChangeListener{
        void onLetterChange(String letter);
    }

    public void setOnLetterChangeListener(OnLetterChangeListener listener){
        this.mOnLetterChangeListener = listener;
    }

    /**
     * 控件在java中使用时调用一个参数的构造方法
     * @param context
     */
    public QuickIndexBar(Context context) {
        this(context,null);
    }

    /**
     * 控件在xml文件中使用时调用两个参数的构造方法
     * @param context
     * @param attrs
     */
    public QuickIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int pxValue = dip2px(context,11f);
        //画笔大小
        mPaint.setTextSize(pxValue);
        //画笔颜色
        mPaint.setColor(getResources().getColor(R.color.theme_color));
        //加粗
//        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    //onSizeChanged 先于onDraw 方法执行
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //获取单元格高度
        mCellWidth = getMeasuredWidth();

        //获取高度
        mHeight = getMeasuredHeight();
        //获取单元格的高度

        mCellHeight = mHeight *1f/LETTERS.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //把字母画出来
        for (int i=0;i<LETTERS.length;i++){
            String text = LETTERS[i]+"";

            //获取字母的宽度
            float letterWidth = mPaint.measureText(text);

            float x = mCellWidth*0.5f - letterWidth*0.5f;

            //获取字母的高度
            Rect bounds = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), bounds);
            float letterHeight = bounds.height();
            float y = mCellHeight*0.5f + letterHeight * 0.5f + mCellHeight * i;

            //如果当前绘制的字母和按压的索引一样则用灰色的画笔
            mPaint.setColor((index==i)? Color.GRAY:getResources().getColor(R.color.theme_color));
            // x默认是这个字符串的左边在屏幕的位置
            // y是指定这个字符baseline在屏幕上的位置
            canvas.drawText(text,x,y,mPaint);
        }
    }

    int index = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = -1;
        int currentIndex = -1;
        switch (MotionEventCompat.getActionMasked(event)){
            case MotionEvent.ACTION_DOWN:
                y = event.getY();
                currentIndex = (int)(y/mCellHeight);
                // 健壮性处理, 在正常范围内
                if(currentIndex>= 0&& currentIndex<LETTERS.length){
                    // 字母的索引发生了变化
                    if(index != currentIndex){

                        if(mOnLetterChangeListener != null){
                            String letter = LETTERS[currentIndex]+"";
                            mOnLetterChangeListener.onLetterChange(letter);
                        }
                        index = currentIndex;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                y = event.getY();
                currentIndex = (int)(y/mCellHeight);
                // 健壮性处理, 在正常范围内
                if(currentIndex>= 0&& currentIndex<LETTERS.length){
                    // 字母的索引发生了变化
                    if(index != currentIndex){

                        if(mOnLetterChangeListener != null){
                            String letter = LETTERS[currentIndex]+"";
                            mOnLetterChangeListener.onLetterChange(letter);
                        }
                        index = currentIndex;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                index = -1;
                break;
            default:
                break;
        }

        //使生效，重新调用onDraw方法
        invalidate();
        //返回true表示消费touch事件
        return true;
    }

}
