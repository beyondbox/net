package com.appjumper.silkscreen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Path path = new Path();
		int w = getWidth();
		int h = getHeight();
		int diameter = w > h ? h : w;
		int radius = diameter / 2;
		path.addCircle(w / 2, h / 2, radius, Direction.CW);
		canvas.clipPath(path);

		super.onDraw(canvas);
	}

}
