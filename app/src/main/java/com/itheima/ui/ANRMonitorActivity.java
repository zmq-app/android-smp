package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.itheima.smp.R;

/**
 * @Subject 子线程持有同步锁(Activity对象锁)导致主线程阻塞,产生ANR
 * @Author  zhangming
 * @Date    2020-06-30 11:17
 */
public class ANRMonitorActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                testANR();
            }
        }).start();

        SystemClock.sleep(100);
        initView();
    }

    /**
     * [1] Thread.sleep和Object.wait本地方法的区别就是sleep不会释放锁,当睡眠的时间到了之后,线程会自动进入可执行状态,等待cpu的执行.
     * [2] wait方法会释放锁(所以前提就是线程在执行wait方法之前必须要获得锁,也就意味着只能在同步方法或者同步代码块中调用wait方法),
     * 释放锁之后,必须要有另外线程执行notify或者notifyAll,才会唤醒执行了wait方法的线程,该线程才能等待cpu执行
     */
    private synchronized void testANR() {
        android.util.Log.i("zhangming","?????? ANRMonitorActivity sleep 30s...");
        SystemClock.sleep(30*1000);
    }

    private synchronized void initView() {
        android.util.Log.i("zhangming","?????? ANRMonitorActivity initView...");
    }
}
