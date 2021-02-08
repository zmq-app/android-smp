package com.itheima.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.itheima.smp.R;

/**
 * @Subject Canvas画布的基本操作
 * @Author  zhangming
 * @Date    2019-05-26 15:35
 */
public class CanvasBitmap extends View {
    private Paint mPaint;
    
    public CanvasBitmap(Context context) {
        this(context,null);
    }

    public CanvasBitmap(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CanvasBitmap(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(36f);
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(0,0,400,400,mPaint);

        //@region jide begin,add zmq for save()画布将当前的状态保存; restore()画布取出原来所保存的状态
        //@region jide begin,add zmq for 保证在这两个函数之间所做的操作不会对原来在canvas上所画图形产生影响[类似于中断时处理机保护现场,进行压栈操作]
        canvas.save();
        mPaint.setColor(Color.YELLOW);
        canvas.rotate(45,200,200);
        canvas.scale(0.5f,0.5f);
        canvas.drawRect(0,0,400,400,mPaint); //nWidth=nHeight=400*0.5=200(px)
        canvas.restore();
        //@region jide end,add zmq for 保证在这两个函数之间所做的操作不会对原来在canvas上所画图形产生影响[类似于中断时处理机保护现场,进行压栈操作]
        //@region jide end,add zmq for save()画布将当前的状态保存; restore()画布取出原来所保存的状态

        canvas.save();
        mPaint.setColor(Color.BLUE);
        canvas.translate(400,400);
        canvas.drawCircle(200,200,200,mPaint);
        canvas.restore();

        //@region jide begin,add zmq for使用bitmap构造的canvas案例[叠加图层]
        //如果是用bitmap构造了一个canvas,那这个canvas上绘制的图像也都会保存在这个bitmap上,而不是画在View上
        //如果想画在View上就必须使用OnDraw(Canvas canvas)函数中传进来的canvas画一遍bitmap才能画到view上
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.monitor_launcher);
        if(srcBitmap != null) {
            Bitmap dstBitmap = Bitmap.createBitmap(srcBitmap.getWidth(),
                srcBitmap.getHeight(),Bitmap.Config.ARGB_8888);
            Canvas mCanvas = new Canvas(dstBitmap);
            Matrix matrix = new Matrix();
            matrix.setScale(0.5f,0.5f,srcBitmap.getWidth()/2,srcBitmap.getHeight()/2);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(48f);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTextAlign(Paint.Align.CENTER);  //rectF.centerX() or srcBitmap.getWidth()/2

            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float top = fontMetrics.top;       //为基线到字体上边框的距离
            float bottom = fontMetrics.bottom; //为基线到字体下边框的距离
            int baseLineY = (int)(srcBitmap.getHeight()/2 - top/2 - bottom/2); //基线中间点的y轴计算公式

            mCanvas.drawBitmap(srcBitmap,matrix,textPaint); //实质在内存中绘制图像并保存到dstBitmap中
            mCanvas.drawText("Hello",srcBitmap.getWidth()/2,baseLineY,textPaint);
            canvas.drawBitmap(dstBitmap,400,800,mPaint);
        }
        //@region jide end,add zmq for使用bitmap构造的canvas案例[叠加图层]
    }
}
