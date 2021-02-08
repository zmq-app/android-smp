package com.itheima.ui.touch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View {
    public TouchView(Context context) {
        super(context);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.v("zhangming","TouchView dispatchTouchEvent ev.getAction() = "+event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("zhangming","TouchView onTouchEvent ev.getAction() = "+event.getAction());
        return super.onTouchEvent(event);
    }
}
