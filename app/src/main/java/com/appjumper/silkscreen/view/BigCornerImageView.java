package com.appjumper.silkscreen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BigCornerImageView extends ImageView {

	public BigCornerImageView(Context context) {
		super(context);
	}

	public BigCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		Path path = new Path();
		int width = getWidth();
		int height = getHeight();
		path.addRoundRect(new RectF(0, 0, width, height), 18.0f, 18.0f, Direction.CW);
		//canvas.drawColor(0xffffffff);
		canvas.clipPath(path);
		super.onDraw(canvas);
	}
	
}
