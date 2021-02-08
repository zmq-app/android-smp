package com.itheima.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.itheima.utils.CommonConstants;
import com.itheima.utils.CommonUtils;

public class MyLinearLayout extends LinearLayout {
    private Context mContext;
    
    public MyLinearLayout(Context context) {
        super(context);
        mContext = context;
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;   
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.i(CommonConstants.TAG,"MyLinearLayout widthSpecSize: "+widthSpecSize+" heightSpecSize: "+heightSpecSize);

        int[] args = CommonUtils.getScreenWidthandHeight(mContext);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(args[0],MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(args[1]/2,MeasureSpec.EXACTLY);

        /**
        int wMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize,MeasureSpec.EXACTLY);
        int hMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize/4,MeasureSpec.EXACTLY);
        getChildAt(0).measure(wMeasureSpec,hMeasureSpec);
        */
        //保存更改后的测量宽高widthSpecSize,heightSpecSize
        //setMeasuredDimension(widthSpecSize,heightSpecSize);

        //调用FrameLayout类中的onMeasure方法,将自定义父控件中的测量规则以及子控件中的LayoutParams配置
        //通过进入measureChildWithMargins方法去调用getChildMeasureSpec函数,从而获取到每个子控件对应的测量规则
        //最后使用上述获取子控件得到的测量规则,调用measure方法对每个子控件进行测量
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //计算圆形View的周围外接矩形的宽度cWidth和高度cHeight
        View circleView = getChildAt(0);
        int cWidth  = circleView.getMeasuredWidth();
        int cHeight = circleView.getMeasuredHeight();

        //计算MyLinearLayout控件的宽度llWidth和高度llHeight
        int llWidth  = right - left;
        int llHeight = bottom - top;

        //计算view周围外接矩形左上角的坐标(mLeft,mTop)
        int mLeft = (llWidth - cWidth)/2;
        int mTop  = (llHeight - cHeight)/2;

        //定位view的位置
        circleView.layout(mLeft,mTop,mLeft+cWidth,mTop+cHeight);
    }

    /**
    09-19 18:52:06.394 29305-29305/com.itheima.smp I/ChatRoom_SMP: MyView Width: 0 MyView Height: 0
        MyView Measure Width: 0 MyView Measure Height: 0
    09-19 18:52:06.394 29305-29305/com.itheima.smp D/ChatRoom_SMP: mAspectRatio = 0.6
    09-19 18:52:06.395 29305-29305/com.itheima.smp D/ChatRoom_SMP: is LinearLayout
    09-19 18:52:06.408 29305-29305/com.itheima.smp I/ChatRoom_SMP: onMeasure widthSpecSize: 1078 heightSpecSize: 1768
        Measure Width = 0 Measure Height = 0
        MyLinearLayout widthSpecSize: 1078 heightSpecSize: 1796
    09-19 18:52:06.409 29305-29305/com.itheima.smp I/ChatRoom_SMP: screen width = 1080 screen height = 2030
    09-19 18:52:06.417 29305-29305/com.itheima.smp I/ChatRoom_SMP: onMeasure widthSpecSize: 1080 heightSpecSize: 1964
    09-19 18:52:06.418 29305-29305/com.itheima.smp I/ChatRoom_SMP: Measure Width = 1078 Measure Height = 1796
        MyLinearLayout widthSpecSize: 1080 heightSpecSize: 1799
        screen width = 1080 screen height = 2030
        MyFrameLayout onLayout getChildCount = 1
        nWidth = 1080 nHeight = 1015
    */
}
