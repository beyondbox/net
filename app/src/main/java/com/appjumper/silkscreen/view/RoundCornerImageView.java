package com.appjumper.silkscreen.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class RoundCornerImageView extends ImageView {
	
	public RoundCornerImageView(Context context) {
		super(context);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		Path path = new Path();
		int width = getWidth();
		int height = getHeight();
		path.addRoundRect(new RectF(0, 0, width, height), 8.0f, 8.0f, Direction.CW);
		//canvas.drawColor(0xffffffff);
		canvas.clipPath(path);
		super.onDraw(canvas);
	}
	
}
