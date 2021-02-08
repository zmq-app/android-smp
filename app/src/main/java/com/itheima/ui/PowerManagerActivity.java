package com.itheima.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.itheima.smp.R;

/**
 * @Subject 电源唤醒休眠管理功能类
 * @Author  zhangming
 * @Date    2019-11-30 19:36
 */
public class PowerManagerActivity extends Activity {
    private PowerManager pm;
    private PowerManager.WakeLock pmLock;
    private Button powerBtn,wakeLockBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        init();
    }

    private void init() {
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //pmLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"WakeLockTag");
        pmLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,"WakeLockTag");

        powerBtn = (Button) findViewById(R.id.power_btn);
        powerBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (pmLock != null && pmLock.isHeld()) {
                    pmLock.release();
                }
                pm.goToSleep(event.getEventTime());
                return true;
            }
        });

        wakeLockBtn = (Button) findViewById(R.id.wake_lock);
        wakeLockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pmLock != null && !pmLock.isHeld()) {
                    pmLock.acquire();
                }
            }
        });
    }
}
