package com.itheima.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.itheima.smp.R;
import com.itheima.utils.CommonConstants;

public class MyCircleView extends View {
    public MyCircleView(Context context) {
        super(context);
    }

    public MyCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** 此处需要重写此自定义view onMeasure函数,定义具体尺寸,否则该view会填充当前父容器的剩余空间大小,与使用match_parent效果一致,但不会超过父容器的空间大小 **/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = 600;
        int heightSpecSize = 600;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /** 在矩形view的区域内的画布上绘制一个圆 **/
    @Override
    protected void onDraw(Canvas canvas) {
        Paint myPaint = new Paint();
        myPaint.setStrokeWidth(2f);
        myPaint.setAntiAlias(true);
        myPaint.setColor(getResources().getColor(R.color.blue));

        final int paddingLeft   = getPaddingLeft();
        final int paddingRight  = getPaddingRight();
        final int paddingTop    = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        Log.i(CommonConstants.TAG,"paddingLeft: "+paddingLeft+" paddingRight: "+paddingRight+
            " paddingTop: "+paddingTop+" paddingBottom: "+paddingBottom);

        int cWidth  = getWidth() - paddingLeft - paddingRight;
        int cHeight = getHeight() - paddingTop - paddingBottom;
        int cRadius = Math.min(cWidth,cHeight)/2;
        canvas.drawCircle(paddingLeft+cWidth/2,paddingTop+cHeight/2,cRadius,myPaint);
    }
}
