package com.itheima.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by zhangming on 2018/9/1.
 */
public class SlideViewPager extends ViewPager {
    private String TAG = SlideViewPager.class.getSimpleName();
    //是否可以进行滑动
    private boolean isSlide;

    public void setSlide(boolean slide) {
        isSlide = slide;
    }


    public SlideViewPager(Context context) {
        super(context);
    }

    public SlideViewPager(Context context,AttributeSet attrs){
        super(context, attrs);
    }

    /**
     * 1.dispatchTouchEvent一般情况不做处理
     *,如果修改了默认的返回值,子孩子都无法收到事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);   // return true;不行
    }

    /**
     * 是否拦截
     * 拦截:会走到自己的onTouchEvent方法里面来
     * 不拦截:事件传递给子孩子
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log.i(TAG,"onInterceptTouchEvent isSlide = "+isSlide);
        // return false;//可行,不拦截事件,
        // return true; //不行,孩子无法处理事件
        //return super.onInterceptTouchEvent(ev);//不行,会有细微移动
        if (isSlide){
            return super.onInterceptTouchEvent(ev);
        }else{
            //TODO 事件传递给子View,保证ViewPager中三个Fragment视图中的各个View控件响应点击和滑动事件
            return false;
        }
    }

    /**
     * 是否消费事件
     * 消费:事件就结束
     * 不消费:往父控件传
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //Log.i(TAG,"onTouchEvent isSlide = "+isSlide);
        //return false;// 可行,不消费,传给父控件
        //return true;// 可行,消费,拦截事件
        //super.onTouchEvent(ev); //不行,
        //虽然onInterceptTouchEvent中拦截了,
        //但是如果viewpager里面子控件不是viewgroup,还是会调用这个方法.
        if (isSlide){
            return super.onTouchEvent(ev);
        }else {
            return true;  // 可行,消费,拦截事件
        }
    }
}
