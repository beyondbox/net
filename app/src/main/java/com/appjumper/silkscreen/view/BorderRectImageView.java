package com.appjumper.silkscreen.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.util.DisplayUtil;

public class BorderRectImageView extends ImageView {

	public BorderRectImageView(Context context) {
		super(context);
	}

	public BorderRectImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Path path = new Path();
		//int radius = getWidth() / 2;
		//path.addCircle(radius, radius, radius, Direction.CW);
		//path.addRect(0, 0, getWidth(), getHeight(), Direction.CW);
		path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), 8.0f, 8.0f, Direction.CW);
		canvas.clipPath(path);

		super.onDraw(canvas);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(DisplayUtil.dip2px(getContext(), 1));
		paint.setColor(getResources().getColor(R.color.border_gray_light));
		canvas.drawPath(path, paint);
        
	}

}
