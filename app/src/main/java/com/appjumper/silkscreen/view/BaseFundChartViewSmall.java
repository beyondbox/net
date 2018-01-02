package com.appjumper.silkscreen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.util.DisplayUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yc on 2016/12/12.
 * 曲线图
 */
public class BaseFundChartViewSmall extends View {
    Paint linePaint;
    Paint textPaint;
    Paint textPaintY;
    Paint xyChartPaint;
    Paint pathXValuePaint;
    Paint chartLinePaint;
    Paint chartJianbianPaint;
    List<Point> points;

    private int paddingR;


    public BaseFundChartViewSmall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BaseFundChartViewSmall(Context context) {
        this(context, null);
    }

    public BaseFundChartViewSmall(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    PathEffect effect;
    Path path;
    Path pathY;

    private void init() {
        paddingR = DisplayUtil.dip2px(getContext(), 10);

        linePaint = new Paint();
        textPaint = new Paint();
        textPaintY = new Paint();
        xyChartPaint = new Paint();
        chartLinePaint = new Paint();
        chartJianbianPaint = new Paint();
        pathXValuePaint = new Paint();

        //设置绘制模式为-虚线作为背景线。
        //effect = new DashPathEffect(new float[] { 6, 6, 6, 6, 6}, 2);
        effect = new DashPathEffect(new float[] {6, 6, 6, 6}, 0);
        //背景虚线路径.
        path = new Path();
        pathY = new Path();
        //只是绘制的XY轴
        linePaint.setStyle(Paint.Style.STROKE);
//        linePaint.setStrokeWidth((float) 0.7);
        linePaint.setStrokeWidth((float) 1.0);             //设置线宽

        linePaint.setColor(getResources().getColor(R.color.new_gray_color));
        linePaint.setAntiAlias(true);// 锯齿不显示

        //X轴刻度上的字
        textPaint.setStyle(Paint.Style.FILL);// 设置非填充
        textPaint.setStrokeWidth(1);// 笔宽5像素
        textPaint.setColor(getResources().getColor(R.color.new_gray_light_color));// 设置为蓝笔
        textPaint.setAntiAlias(true);// 锯齿不显示
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(DisplayUtil.sp2px(getContext(), 9));

        //Y轴刻度上的字
        textPaintY.setStyle(Paint.Style.FILL);// 设置非填充
        textPaintY.setStrokeWidth(1);// 笔宽5像素
        textPaintY.setColor(0xFFFD8282);// 设置为蓝笔
        textPaintY.setAntiAlias(true);// 锯齿不显示
        textPaintY.setTextAlign(Paint.Align.CENTER);
        textPaintY.setTextSize(DisplayUtil.sp2px(getContext(), 9));

        //绘制XY轴上的字：Y开关状态、X时间
        xyChartPaint.setStyle(Paint.Style.FILL);
        xyChartPaint.setStrokeWidth(1);
        xyChartPaint.setColor(Color.BLUE);
        xyChartPaint.setAntiAlias(true);
        xyChartPaint.setTextAlign(Paint.Align.CENTER);
        xyChartPaint.setTextSize(18);

        //绘制的折线
        chartLinePaint.setStyle(Paint.Style.STROKE);
        chartLinePaint.setStrokeWidth(2);
        chartLinePaint.setColor(Color.BLUE);
        chartLinePaint.setAntiAlias(true);

        //绘制的折线
        chartJianbianPaint.setStyle(Paint.Style.FILL);
        chartJianbianPaint.setStrokeWidth(2);
        //chartJianbianPaint.setColor(Color.YELLOW);
        chartJianbianPaint.setAntiAlias(true);

        //x轴刻度path
        pathXValuePaint.setStyle(Paint.Style.STROKE);
        pathXValuePaint.setStrokeWidth(1);
        pathXValuePaint.setColor(Color.TRANSPARENT);
        pathXValuePaint.setTextSize(DisplayUtil.sp2px(getContext(), 9));
    }

    /**
     * 重要参数，两点之间分为几段描画，数字愈大分段越多，描画的曲线就越精细.
     */
    private static final int STEPS = 1;

    float gridX,gridY,xSpace = 0,ySpace = 0,spaceYT = 0,yStart=0;
    List<String> dateX = null;
    List<Float> dateY = null;

    List<List<Float>> data = null;

    List<Integer> colors = null;

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public List<Float> getDateY() {
        return dateY;
    }

    public void setDateY(List<Float> dateY) {
        this.dateY = dateY;
    }

    public List<List<Float>> getData() {
        return data;
    }

    public void setData(List<List<Float>> data) {
        this.data = data;
    }

    public List<String> getDateX() {
        return dateX;
    }

    public void setDateX(List<String> dateX) {
        this.dateX = dateX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //基准点。
        gridX = DisplayUtil.dip2px(getContext(), 32);
        gridY = getHeight() - DisplayUtil.dip2px(getContext(), 10);

        //XY间隔。
        if(dateX!=null&&dateX.size()>0){
            int offset = paddingR;
            xSpace = (getWidth() - gridX - offset)  /  (dateX.size() - 1);
        }

        if(dateY!=null&&dateY.size()>0){
            ySpace = (gridY - 70)/ (dateY.size() - 1);
            yStart = dateY.get(0);
            if(dateY.size()>2){
                spaceYT = Math.abs(dateY.get(1)-dateY.get(0));
            }
        }


        //画Y轴
        canvas.drawLine(gridX, gridY - 20 - 10-2, gridX, 30 +10, linePaint);

        float y = 0;
        //画X轴。
        y = gridY - 20;
        canvas.drawLine(gridX, y - 10, getWidth() - paddingR, y - 10, linePaint);//X轴.

        //绘制X刻度坐标。
        float x = 0;
        if(dateX!=null){
            for (int n = 0; n < dateX.size(); n++) {
                //取X刻度坐标.
                x = gridX + (n) * xSpace;//在原点(0,0)处也画刻度（不画的话就是n+1）,向右移动一个跨度。
                //画X轴具体刻度值。
                if (dateX.get(n) != null) {
                    //canvas.drawLine(x, gridY - 30, x, gridY - 18, linePaint);//短X刻度。
                    //canvas.drawText(dateX.get(n), x, gridY + 5, textPaint);//X具体刻度值。
                    Path pathXValue = new Path();
                    pathXValue.moveTo(x - DisplayUtil.dip2px(getContext(), 8), gridY - DisplayUtil.dip2px(getContext(), 3));
                    pathXValue.lineTo(x + DisplayUtil.dip2px(getContext(), 6), gridY + DisplayUtil.dip2px(getContext(), 6));
                    canvas.drawPath(pathXValue, pathXValuePaint);
                    canvas.drawTextOnPath(dateX.get(n), pathXValue, 0, 0, textPaint);
                }

                if (x != gridX) {
                    pathY.moveTo(x, y - 10);
                    pathY.lineTo(x, 30 + 10);
                    linePaint.setColor(Color.parseColor("#F1EFEF"));
                    canvas.drawPath(pathY, linePaint);
                }
            }

            linePaint.setColor(getResources().getColor(R.color.new_gray_color));
        }

        float my = 0;

        if(dateY!=null){

            for(int n=0;n<dateY.size();n++){
                //取Y刻度坐标.
                my = gridY-30 - (n)*ySpace;
//                UIUtils.log(my,"fdsafss",ySpace);
                //画y轴具体刻度值。
                float yy = dateY.get(n);
                canvas.drawText(String.valueOf((int)yy),gridX - DisplayUtil.dip2px(getContext(), 15),my,textPaintY);

                if(my != gridY-30){
                    //linePaint.setPathEffect(effect);//设法虚线间隔样式。
                    //画除X轴之外的------背景虚线一条-------
                    path.moveTo(gridX, my);//背景【虚线起点】。
                    path.lineTo(getWidth() - paddingR, my);//背景【虚线终点】。
                    linePaint.setColor(Color.parseColor("#F1EFEF"));
                    canvas.drawPath(path, linePaint);
                }
            }

            linePaint.setColor(getResources().getColor(R.color.new_gray_color));
        }

        if(data!=null&&data.size()>0){
            float lastPointX = 0; //前一个点
            float lastPointY = 0;
            float currentPointX = 0;//当前点
            float currentPointY = 0;
            for(int n=0;n<data.size();n++){
                List<Float> da = data.get(n);
                List<Float> da_x = new ArrayList<>();
                List<Float> da_y = new ArrayList<>();
                /**
                 * 曲线路劲
                 */
                Path curvePath = new Path();
                /**
                 * 渐变色路径
                 */
                Path jianBianPath = new Path();

                for(int m=0;m<da.size();m++){
                    currentPointX = m * xSpace + gridX;
                    currentPointY = gridY-30 - ((da.get(m)-yStart)*(ySpace/spaceYT));
                    da_x.add(currentPointX);
                    da_y.add(currentPointY);
//                    if(m>0){
//                        canvas.drawLine(lastPointX, lastPointY, currentPointX, currentPointY, chartLinePaint);
//                    }
//                    lastPointX = currentPointX;
//                    lastPointY = currentPointY;
                }
                List<Cubic> calculate_y = calculate(da_y);
                List<Cubic> calculate_x = calculate(da_x);
                curvePath.moveTo(calculate_x.get(0).eval(0), calculate_y.get(0).eval(0));
                jianBianPath.moveTo(gridX,gridY - 20 - 10);
                jianBianPath.lineTo(calculate_x.get(0).eval(0), calculate_y.get(0).eval(0));
//                chartLinePaint.setColor(colors.get(n));
                chartLinePaint.setColor(getResources().getColor(R.color.theme_color));
                //chartLinePaint.setColor(getResources().getColor(R.color.orange_color));
                float lastx = 0;
                for (int i = 0; i < calculate_x.size(); i++) {
                    for (int j = 1; j <= STEPS; j++) {
                        float u = j / (float) STEPS;
                        curvePath.lineTo(calculate_x.get(i).eval(u), calculate_y.get(i)
                                .eval(u));
                        jianBianPath.lineTo(calculate_x.get(i).eval(u), calculate_y.get(i)
                                .eval(u));
                        lastx = calculate_x.get(i).eval(u);
                    }
                }
                jianBianPath.lineTo(lastx,gridY - 20 - 10);
                canvas.drawPath(curvePath, chartLinePaint);

                //Shader mShader = new LinearGradient(0,30 + 10,0,gridY - 20 - 10,new int[] {getResources().getColor(R.color.theme_color),getResources().getColor(R.color.theme_pressed_color),Color.TRANSPARENT},null,Shader.TileMode.REPEAT);
                //Shader mShader = new LinearGradient(0,30 + 10,0,gridY - 20 - 10,new int[] {0xBFFAAC7D, 0xBFFAAC7D, 0xBFF8EBE3},null,Shader.TileMode.REPEAT);
                Shader mShader = new LinearGradient(0,30 + 10,0,gridY - 15,new int[] {0xBFA8C6F8, 0xBFC7D9F8, 0x00ffffff},null,Shader.TileMode.REPEAT);

//新建一个线性渐变，前两个参数是渐变开始的点坐标，第三四个参数是渐变结束的点的坐标。连接这2个点就拉出一条渐变线了，玩过PS的都懂。然后那个数组是渐变的颜色。下一个参数是渐变颜色的分布，如果为空，每个颜色就是均匀分布的。最后是模式，这里设置的是循环渐变

                chartJianbianPaint.setShader(mShader);
                canvas.drawPath(jianBianPath, chartJianbianPaint);
            }
        }



//    /**
//     * 画点.
//     *
//     * @param canvas
//     */
//    private void drawPoints(Canvas canvas) {
//        for (int i = 0; i < points.size(); i++) {
//            Point p = points.get(i);
//            canvas.drawCircle(p.x, p.y, 5, paint);
//        }
//    }
    }

    /**
     * 计算曲线.
     *
     * @param x
     * @return
     */
    private List<Cubic> calculate(List<Float> x) {
        int n = x.size() - 1;
        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];
        int i;
        /*
         * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]|
         * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
         * 1| | . | |3(x[n] - x[n-2])| [ 1 2] [D[n]] [3(x[n] - x[n-1])]
         *
         * by using row operations to convert the matrix to upper triangular and
         * then back sustitution. The D[i] are the derivatives at the knots.
         */

        gamma[0] = 1.0f / 2.0f;
        for (i = 1; i < n; i++) {
            gamma[i] = 1 / (4 - gamma[i - 1]);
        }
        gamma[n] = 1 / (2 - gamma[n - 1]);

        delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
        for (i = 1; i < n; i++) {
            delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1])
                    * gamma[i];
        }
        delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

        D[n] = delta[n];
        for (i = n - 1; i >= 0; i--) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

        /* now compute the coefficients of the cubics */
        List<Cubic> cubics = new LinkedList<Cubic>();
        for (i = 0; i < n; i++) {
            Cubic c = new Cubic(x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i))
                    - 2 * D[i] - D[i + 1], 2 * (x.get(i) - x.get(i + 1)) + D[i]
                    + D[i + 1]);
            cubics.add(c);
        }
        return cubics;
    }

    class Cubic{
        float a,b,c,d;         /* a + b*u + c*u^2 +d*u^3 */

        public Cubic(float a, float b, float c, float d){
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }


        /** evaluate cubic */
        public float eval(float u) {
            return (((d*u) + c)*u + b)*u + a;
        }
    }
}
