package com.appjumper.silkscreen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.util.DisplayUtil;
import com.appjumper.silkscreen.util.LogHelper;

import java.util.List;

/**
 * 走势图（直线型）
 * Created by Botx on 2018/1/5.
 */

public class ChartViewStraightLine extends View {

    private Context context;
    private float gridX, gridY; //坐标原点
    private List<List<Float>> data; //走势数据
    private List<String> dataX; //x轴刻度值
    private List<Float> dataY; //y轴刻度值

    private float xSpace, ySpace; //x轴和y轴的间隔
    private float spaceYT; //y轴两个刻度的差值
    private float yStart; //坐标原点的y轴刻度值

    private Path pathBackX, pathBackY; //Path--背景灰色线

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
        paddingR = DisplayUtil.dip2px(context, 12);

        pathBackX = new Path();
        pathBackY = new Path();

        //xy轴
        paintAxis = new Paint();
        paintAxis.setStyle(Paint.Style.STROKE);
        paintAxis.setStrokeWidth(1);
        paintAxis.setColor(getResources().getColor(R.color.new_gray_color));
        paintAxis.setAntiAlias(true);

        //x轴刻度
        paintTextX = new Paint();
        paintTextX.setStyle(Paint.Style.FILL);
        paintTextX.setStrokeWidth(1);
        paintTextX.setColor(getResources().getColor(R.color.new_gray_light_color));
        paintTextX.setAntiAlias(true);
        paintTextX.setTextAlign(Paint.Align.CENTER);
        paintTextX.setTextSize(DisplayUtil.sp2px(context, 8));

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
        paintLineBack.setStrokeWidth(1);
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
        paintXValuePath.setTextSize(DisplayUtil.sp2px(getContext(), 8));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //宽高
        int width = getWidth();
        int height = getHeight();

        //坐标原点
        gridX = DisplayUtil.dip2px(context, 32);
        gridY = height - DisplayUtil.dip2px(context, 22);

        //XY间隔
        if (dataX != null && dataX.size() > 0)
            xSpace = (width - gridX - paddingR) / (dataX.size() - 1);

        if (dataY != null && dataY.size() > 0) {
            ySpace = (gridY - paddingT) / (dataY.size() - 1);
            spaceYT = Math.abs(dataY.get(1) - dataY.get(0));
            yStart = dataY.get(0);
        }


        if (dataX != null && dataX.size() > 0) {
            for (int i = 0; i < dataX.size(); i++) {
                //X刻度坐标
                float x = xSpace * i + gridX;
                //画X轴具体刻度值
                Path pathXValue = new Path();
                pathXValue.moveTo(x - DisplayUtil.dip2px(context, 8), gridY + DisplayUtil.dip2px(context, 10));
                pathXValue.lineTo(x + DisplayUtil.dip2px(context, 8), gridY + DisplayUtil.dip2px(context, 20));
                canvas.drawPath(pathXValue, paintXValuePath);
                canvas.drawTextOnPath(dataX.get(i), pathXValue, 0, 0, paintTextX);
                //画背景竖直线
                if (x != gridX) {
                    pathBackY.moveTo(x, gridY);
                    pathBackY.lineTo(x, paddingT);
                    canvas.drawPath(pathBackY, paintLineBack);
                }
            }
        }


        if (dataY != null && dataY.size() > 0) {
            for (int i = 0; i < dataY.size(); i++) {
                //Y刻度坐标
                float y = gridY - ySpace * i;
                //画Y轴具体刻度值
                float value = dataY.get(i);
                canvas.drawText((int)value + "", gridX - DisplayUtil.dip2px(context, 15), y, paintTextY);
                //画背景水平线
                if (y != gridY) {
                    pathBackX.moveTo(gridX, y);
                    pathBackX.lineTo(width - paddingR, y);
                    canvas.drawPath(pathBackX, paintLineBack);
                }
            }
        }


        //画X轴
        canvas.drawLine(gridX, gridY, width - paddingR, gridY, paintAxis);
        //画Y轴
        canvas.drawLine(gridX, gridY, gridX, paddingT, paintAxis);


        //画走势线
        if (data != null && data.size() > 0) {
            for (int n = 0; n < data.size(); n++) {
                List<Float> list = data.get(n);
                Path pathLine = new Path(); //走势线
                Path pathShadow = new Path(); //渐变
                pathShadow.moveTo(gridX, gridY);

                for (int i = 0; i < list.size(); i++) {
                    //计算XY坐标
                    float x = xSpace * i + gridX;
                    float y = gridY - (ySpace * (list.get(i) - yStart) / spaceYT);

                    LogHelper.e("坐标坐标", x + " , " + y);

                    if (i == 0) {
                        pathLine.moveTo(gridX, y);
                    } else {
                        pathLine.lineTo(x, y);
                    }

                    pathShadow.lineTo(x, y);
                    if (i == list.size() - 1)
                        pathShadow.lineTo(x, gridY);
                }

                canvas.drawPath(pathLine, paintLine);

                Shader shader = new LinearGradient(gridX, paddingT, gridX, gridY, new int[] {0xBFA8C6F8, 0xBFC7D9F8, 0x00EFF3FA}, null, Shader.TileMode.REPEAT);
                paintShadow.setShader(shader);
                canvas.drawPath(pathShadow, paintShadow);
            }
        }

    }


    public List<List<Float>> getData() {
        return data;
    }

    public void setData(List<List<Float>> data) {
        this.data = data;
    }

    public List<String> getDataX() {
        return dataX;
    }

    public void setDataX(List<String> dataX) {
        this.dataX = dataX;
    }

    public List<Float> getDataY() {
        return dataY;
    }

    public void setDataY(List<Float> dataY) {
        this.dataY = dataY;
    }
}
