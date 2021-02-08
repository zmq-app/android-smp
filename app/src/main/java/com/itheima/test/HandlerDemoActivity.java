package com.itheima.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.itheima.utils.CommonConstants;

/**
 * @Subject 主线程和子线程Handler机制
 * @Author  zhangming
 * @Date    2018-10-12 14:10
 */
public class HandlerDemoActivity extends Activity {
    private Handler mHandler1,mHandler2;
    private Looper  mLooper;

    /** mHandler1关联子线程的Looper和MessageQueue,故mHandler1的回调方法handleMessage运行在子线程中 **/
    /** mHandler2关联主线程的Looper和MessageQueue,故mHandler2的回调方法handleMessage运行在主线程中 **/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO Main Thread Id = 2
        Log.i(CommonConstants.TAG,"Main Thread Id = "+Thread.currentThread().getId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mLooper = Looper.myLooper();
                mHandler2 = new Handler(Looper.getMainLooper(),new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        //TODO (3) current Thread Id= 2 msg.what = 36 [实质在主线程运行回调函数handleMessage]
                        Log.i(CommonConstants.TAG,"(3) current Thread Id= "+Thread.currentThread().getId()+" msg.what = "+msg.what);
                        return false;
                    }
                });
                Looper.loop();
            }
        }).start();

        while(mLooper == null);
        mHandler1 = new Handler(mLooper,new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //TODO (2) current Thread Id= 3706 msg.what = 32 [实质在子线程运行回调函数handleMessage]
                Log.i(CommonConstants.TAG,"(2) current Thread Id= "+Thread.currentThread().getId()+" msg.what = "+msg.what);
                return false;
            }
        });
        mHandler1.sendEmptyMessage(32);

        while(mHandler2 == null);
        mHandler2.sendEmptyMessage(36);

        //TODO Message m = Message.obtain(); m.callback = r;  m.callback.run();
        mHandler1.post(new Runnable() {
            @Override
            public void run() {
                //TODO mHandler1 post Function,Thread Id = 3706
                Log.i(CommonConstants.TAG,">>>>>>> mHandler1 post Function,Thread Id = "+Thread.currentThread().getId());
            }
        });
        mHandler2.post(new Runnable() {
            @Override
            public void run() {
                //TODO mHandler2 post Function,Thread Id = 2
                Log.i(CommonConstants.TAG,">>>>>>> mHandler2 post Function,Thread Id = "+Thread.currentThread().getId());
            }
        });

    }
}
