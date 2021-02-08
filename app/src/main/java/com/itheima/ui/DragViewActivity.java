package com.itheima.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itheima.smp.R;

/**
 * @Subject DragView拖拽案例
 * @Author  zhangming
 * @Date    2019-11-05 11:36
 */
public class DragViewActivity extends Activity {
    private LinearLayout mLeftBounds,mRightBounds;
    private ImageView mDragView;

    private static final String LABEL_INTENT = "label";
    private static final String TAG = DragViewActivity.class.getSimpleName();

    private HandlerThread mThread;
    private Handler mHandler;
    private Runnable mBackgroundRunnable = new Runnable() {
        @Override
        public void run() {
            long tid = Thread.currentThread().getId();
            int priority = Thread.currentThread().getPriority();
            String name = Thread.currentThread().getName();
            String content = "tid = "+tid+" name = "+name+" priority = "+priority;
            /* HandlerThread run方法中开启Looper循环,故可以在子线程中更新UI操作 */
            Toast.makeText(DragViewActivity.this, content, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);

        mLeftBounds = (LinearLayout) findViewById(R.id.ll_left_bounds);
        mRightBounds = (LinearLayout) findViewById(R.id.ll_right_bounds);
        mDragView = (ImageView) findViewById(R.id.iv_drag_view);

        mDragView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent();
                ClipData clipData = ClipData.newIntent(LABEL_INTENT, intent);
                //Intent clipIntent = clipData.getItemAt(0).getIntent();

                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(clipData, shadowBuilder, mDragView, View.DRAG_FLAG_OPAQUE); //同一个Activity内部传递localState:[控件ImageView mDragView]
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);  //震动反馈

                if (mThread == null) {
                    mThread = new HandlerThread("DragHandlerThread");
                    mThread.start();
                    mHandler = new Handler(mThread.getLooper());
                    mHandler.post(mBackgroundRunnable);
                }

                return true;
            }
        });

        mLeftBounds.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.i(TAG, "mLeftBounds开始拖拽");
                        mDragView.setVisibility(View.INVISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.i(TAG, "mLeftBounds结束拖拽");
                        mDragView.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.i(TAG, "mLeftBounds拖拽的view进入监听的view时");
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.i(TAG, "mLeftBounds拖拽的view离开监听的view时");
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        float x = event.getX();
                        float y = event.getY();
                        Log.i(TAG, "mLeftBounds拖拽的view在leftBounds中的位置:imageX =" + x + ",imageY=" + y);
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.i(TAG, "mLeftBounds释放拖拽的view");
                        ImageView localState = (ImageView) event.getLocalState();
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.topMargin = (int) event.getY() - localState.getHeight() / 2;
                        layoutParams.leftMargin = (int) event.getX() - localState.getWidth() / 2;
                        ((ViewGroup) localState.getParent()).removeView(localState);
                        mLeftBounds.addView(localState, layoutParams);
                        break;
                }
                //是否响应拖拽事件,true响应,返回false只能接受到ACTION_DRAG_STARTED事件,后续事件不会收到
                return true;
            }
        });

        mRightBounds.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                //获取事件
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.i(TAG, "mRightBounds开始拖拽");
                        mDragView.setVisibility(View.INVISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.i(TAG, "mRightBounds结束拖拽");
                        mDragView.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.i(TAG, "mRightBounds拖拽的view进入监听的view时");
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.i(TAG, "mRightBounds拖拽的view离开监听的view时");
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        float x = event.getX();
                        float y = event.getY();
                        Log.i(TAG, "mRightBounds拖拽的view在rightBounds中的位置:imageX =" + x + ",ImageY =" + y);
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.i(TAG, "mRightBounds释放拖拽的view");
                        ImageView localState = (ImageView) event.getLocalState(); //获取传递的localState,向下强转型成ImageView实例
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.topMargin = (int) event.getY() - localState.getHeight() / 2;
                        layoutParams.leftMargin = (int) event.getX() - localState.getWidth() / 2;
                        ((ViewGroup) localState.getParent()).removeView(localState);
                        mRightBounds.addView(localState, layoutParams);
                        break;
                }
                //是否响应拖拽事件,true响应,返回false只能接受到ACTION_DRAG_STARTED事件,后续事件不会收到
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mThread != null && mThread.isAlive()) {
            mHandler.removeCallbacks(mBackgroundRunnable);
            /** 退出HandlerThread的消息循环,结束线程ThreadName="DragHandlerThread" **/
            mThread.getLooper().quitSafely();
            Log.i(TAG,"onDestroy thread isAlive = "+mThread.isAlive()+" isInterrupted = "+mThread.isInterrupted()+" isDaemon = "+mThread.isDaemon());
            mBackgroundRunnable = null;
            mThread = null;
        }
        super.onDestroy();
    }
}
