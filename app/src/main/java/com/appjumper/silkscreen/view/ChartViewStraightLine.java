package com.appjumper.silkscreen.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.util.DisplayUtil;

import java.util.List;

/**
 * 走势图（直线型）
 * Created by Botx on 2018/1/5.
 */

public class ChartViewStraightLine extends View {

    private Context context;
    private float gridX, gridY; //坐标原点
    private List<List<Float>> data; //总数据
    private List<String> dateX; //x轴数据
    private List<Float> dateY; //y轴数据

    private float xSpace, ySpace; //x轴和y轴的间隔

    private Path pathBackX, pathBackY; //Path--背景灰色线
    private Path pathLine; //Path--走势线
    private Path pathShadow; //Path--渐变

    private int paddingT; //y轴末端到顶部的间距
    private int paddingR; //x轴末端到最右边的间距

    private Paint paintAxis; //画笔--xy轴
    private Paint paintTextX, paintTextY; //画笔--xy轴上的刻度
    private Paint paintLineBack; //画笔--背景灰色线
    private Paint paintLine; //画笔--走势线
    private Paint paintShadow; //画笔--渐变
    private Paint paintXValuePath; //画笔--x轴刻度斜向路径





    public ChartViewStraightLine(Context context) {
        this(context, null);
    }

    public ChartViewStraightLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartViewStraightLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    private void init() {
        paddingT = DisplayUtil.dip2px(context, 10);
        paddingR = DisplayUtil.dip2px(context, 10);

        pathBackX = new Path();
        pathBackY = new Path();
        pathLine = new Path();
        pathShadow = new Path();

        //xy轴
        paintAxis = new Paint();
        paintAxis.setStyle(Paint.Style.STROKE);
        paintAxis.setStrokeWidth((float) 1.0);
        paintAxis.setColor(getResources().getColor(R.color.new_gray_color));
        paintAxis.setAntiAlias(true);

        //x轴刻度
        paintTextX = new Paint();
        paintTextX.setStyle(Paint.Style.FILL);
        paintTextX.setStrokeWidth(1);
        paintTextX.setColor(getResources().getColor(R.color.new_gray_light_color));
        paintTextX.setAntiAlias(true);
        paintTextX.setTextAlign(Paint.Align.CENTER);
        paintTextX.setTextSize(DisplayUtil.sp2px(context, 9));

        //y轴刻度
        paintTextY = new Paint();
        paintTextY.setStyle(Paint.Style.FILL);
        paintTextY.setStrokeWidth(1);
        paintTextY.setColor(0xFFFD8282);
        paintTextY.setAntiAlias(true);
        paintTextY.setTextAlign(Paint.Align.CENTER);
        paintTextY.setTextSize(DisplayUtil.sp2px(context, 9));

        //走势线
        paintLine = new Paint();
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(2);
        paintLine.setColor(getResources().getColor(R.color.theme_color));
        paintLine.setAntiAlias(true);

        //背景灰色线
        paintLineBack = new Paint();
        paintLineBack.setStyle(Paint.Style.STROKE);
        paintLineBack.setStrokeWidth(2);
        paintLineBack.setColor(Color.parseColor("#F1EFEF"));
        paintLineBack.setAntiAlias(true);

        //渐变
        paintShadow = new Paint();
        paintShadow.setStyle(Paint.Style.FILL);
        paintShadow.setStrokeWidth(2);
        paintShadow.setAntiAlias(true);

        //x轴刻度斜向路径
        paintXValuePath = new Paint();
        paintXValuePath.setStyle(Paint.Style.STROKE);
        paintXValuePath.setStrokeWidth(1);
        paintXValuePath.setColor(Color.TRANSPARENT);
        paintXValuePath.setTextSize(DisplayUtil.sp2px(getContext(), 9));
    }

}
