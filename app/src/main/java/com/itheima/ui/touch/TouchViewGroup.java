package com.itheima.ui.touch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class TouchViewGroup extends LinearLayout {
    public TouchViewGroup(Context context) {
        super(context);
    }

    public TouchViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.v("zhangming","TouchViewGroup dispatchTouchEvent ev.getAction() = "+ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.v("zhangming","TouchViewGroup onInterceptTouchEvent ev.getAction() = "+ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("zhangming","TouchViewGroup onTouchEvent ev.getAction() = "+event.getAction());
        return super.onTouchEvent(event);
    }
}
