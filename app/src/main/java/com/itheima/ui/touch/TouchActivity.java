package com.itheima.ui.touch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.itheima.smp.R;

public class TouchActivity extends Activity {
    private Handler mainHandler,childHandler;
    private Thread childThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_touch);
        //@region jide begin,add zmq for SYSTEM_UI_FLAG_FULLSCREEN/SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and SYSTEM_UI_FLAG_HIDE_NAVIGATION/SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION Attributes
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.holo_blue_light));
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.holo_orange_light));
        //@region jide end,add zmq for SYSTEM_UI_FLAG_FULLSCREEN/SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and SYSTEM_UI_FLAG_HIDE_NAVIGATION/SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION Attributes
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.v("zhangming","TouchActivity dispatchTouchEvent ev.getAction() = "+ev.getAction());
        if (childThread == null) {
            childThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    childHandler = new Handler();
                    mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("zhangming","MainThread tid = "+Thread.currentThread().getId());
                        }
                    },3000);
                    Looper.loop();
                }
            });
            childThread.start();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("zhangming","TouchActivity onTouchEvent ev.getAction() = "+event.getAction());
        if(childHandler != null){
            childHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.v("zhangming","ChildThread tid = "+Thread.currentThread().getId());
                }
            });
            childHandler = null;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (childThread != null) {
            childThread.interrupt();
            childThread = null;
        }
        if (mainHandler != null) {
            mainHandler = null;
        }
        if(childHandler != null){
            childHandler = null;
        }
    }
}
