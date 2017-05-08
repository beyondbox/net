package com.appjumper.silkscreen.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.util.DisplayUtil;

import java.util.List;

public class IndexSideBar extends View {

	/**
	 * 隐藏索引提示
	 */
	public static final int WHAT_HIDE_HINT = 11;
	/**
	 * 显示索引提示
	 */
	public static final int WHAT_SHOW_HINT = 12;
	
	private String[] b = {"#","A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z"};
	
	private Paint paint = new Paint();
	//当前选中的索引字母的位置
	private int choose = -1;
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	private List<String> actualCitySections; //listview中实际存在的索引字母
	private Handler hintHandler;


	//new的方式时调用
	public IndexSideBar(Context context) {
		super(context);
		init();
	}
	
	//XML方式使用时调用
	public IndexSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setActualCitySections(List<String> actualCitySections) {
		this.actualCitySections = actualCitySections;
		invalidate();
	}

	private void init() {
		paint.setColor(Color.parseColor("#c8c8c8"));
		paint.setTypeface(Typeface.DEFAULT);
		paint.setAntiAlias(true);
		paint.setTextSize(DisplayUtil.sp2px(getContext(), 12));
	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int width = getWidth(); //sidebar的宽度
		int height = getHeight(); //sidebar的高度
		int singleHeight = height / b.length; //每个字母的高度

		for (int i = 0; i < b.length; i++) {
			//如果listview的实际索引字母中包含当前字母，则画笔设置蓝色
			if (actualCitySections != null && actualCitySections.contains(b[i])) {
				paint.setColor(getResources().getColor(R.color.theme_color));
				//如果当前字母为选中状态，则画笔设置橙色并加粗显示
				/*if (i == choose) {
					paint.setColor(Color.parseColor("#FF4C06"));
					paint.setFakeBoldText(true);
				}*/
			}
			
			//X坐标为字母的左边缘到父窗体左边的位置，Y坐标为字母的baseline到父窗体顶端的位置（大写字母都在baseline之上）
			float xPos = width/2 - paint.measureText(b[i])/2;
			float yPos = singleHeight*i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			//画完当前字母后，取消加粗显示，并重新设置颜色
			paint.setColor(Color.parseColor("#c8c8c8"));
			paint.setFakeBoldText(false);
		}
	}
	

	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:  
			hintHandler.sendEmptyMessageDelayed(WHAT_HIDE_HINT, 1000);
			//setBackgroundDrawable(new ColorDrawable(0x00ffffff));
			//choose = -1;
			//更新视图
			invalidate();
			break;
		default:
			hintHandler.removeMessages(WHAT_HIDE_HINT);
			//setBackgroundResource(R.drawable.sidebar_bg);
			float y = event.getY();
			//计算当前触摸位置所对应的数组b的下标
			int currentLetterIndex = (int) (y / (getHeight() / b.length));   // 视频中： y / getHeight() * b.length
			
			//定位到指定位置
			//此处不考虑位置0，因为它是# 
			if (currentLetterIndex > 0 && currentLetterIndex < b.length) {
				//显示字母提示框
				Message msg = hintHandler.obtainMessage();
				msg.what = WHAT_SHOW_HINT;
				msg.obj = b[currentLetterIndex];
				hintHandler.sendMessage(msg);
				
				if (actualCitySections != null && actualCitySections.contains(b[currentLetterIndex])) {
					if (onTouchingLetterChangedListener != null) {
						onTouchingLetterChangedListener.onTouchingLetterChanged(b[currentLetterIndex]);
					}
				}
				choose = currentLetterIndex;
			}
			
			//更新视图
			invalidate();
			break;
		}
		return true;
	}


	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public void setHintHandler(Handler hintHandler) {
		this.hintHandler = hintHandler;
	}
}