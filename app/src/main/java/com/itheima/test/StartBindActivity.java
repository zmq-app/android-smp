package com.itheima.test;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.itheima.utils.CommonConstants;
import com.itheima.utils.CommonUtils;

/**
 * Created by zhangming on 2018/10/14.
 */

public class StartBindActivity extends Activity {
    private MyConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = this.getWindow().getDecorView();
        FrameLayout cView = (FrameLayout)decorView.findViewById(android.R.id.content);
        cView.addView(getContentView());
    }

    private ViewGroup getContentView(){
        RelativeLayout layoutGroup = new RelativeLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, CommonUtils.dip2px(this,300f));
        lp.gravity = Gravity.CENTER;
        layoutGroup.setLayoutParams(lp);
        layoutGroup.setBackgroundColor(Color.RED);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        Button blueBtn = new Button(this);
        blueBtn.setBackgroundColor(Color.BLUE);
        blueBtn.setTextColor(Color.BLACK);
        blueBtn.setTextSize(18f);
        blueBtn.setText("Start Service");
        blueBtn.setLayoutParams(params1);
        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });
        layoutGroup.addView(blueBtn);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        //TODO 如果写成params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM | RelativeLayout.CENTER_HORIZONTAL);
        //TODO 效果等同于params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //TODO 因为12 | 14 = (1110)2 = 14 = RelativeLayout.CENTER_HORIZONTAL
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        Button greenBtn = new Button(this);
        greenBtn.setBackgroundColor(Color.GREEN);
        greenBtn.setTextColor(Color.BLACK);
        greenBtn.setTextSize(18f);
        greenBtn.setText("Stop Service");
        greenBtn.setLayoutParams(params2);
        greenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });
        layoutGroup.addView(greenBtn);

        return layoutGroup;
    }

    private class MyConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //TODO 只有当我们自己写的MyService的onBind方法返回值不为null时,才会被调用
            //TODO 且当多次调用BindService时,均会多次调用onServiceConnected方法
            Log.i(CommonConstants.TAG,"onServiceConnected,service = "+service.toString());

            //TODO 同一进程,调用MyBinder类的systemOut方法
            StartBindService.MyBinder mBinder = (StartBindService.MyBinder)service;
            mBinder.systemOut();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //这个方法只有出现异常时才会调用,服务端正常退出不会调用
            Log.i(CommonConstants.TAG,"onServiceDisconnected......");
            if (conn != null) {
                conn = null;
            }
        }
    }

    void startService() {
        Intent i = new Intent();
        i.setPackage("com.itheima.smp");
        i.setAction("com.itheima.mytest.service");
        i.addCategory("com.itheima.mytest.category");
        //startService(i);

        /* MyConnection实例conn进行判空检测,防止多次重复调用unbindService方法时抛出异常 */
        if (conn == null) {
            conn = new MyConnection();
        }
        /* 仅仅调用一次"onCreate->onBind" */
        bindService(i,conn, Service.BIND_AUTO_CREATE);
    }

    void stopService() {
        //TODO (1) 执行bindService后一定要unbindService,否则会出现内存泄露[ServiceConnectionLeaked]
        //TODO android.app.ServiceConnectionLeaked: Activity com.itheima.ui.MyTestActivity has leaked ServiceConnection
        //TODO com.itheima.ui.MyTestActivity$MyConnection@941acdf that was originally bound here
        //TODO (2) unbindService方法只能调用一次,onDestroy方法也只执行一次,多次调用会抛出异常,需对MyConnection实例conn进行判空检测
        if (conn != null) {
            Log.e(CommonConstants.TAG,"unbindService conn = "+conn.toString());
            /* 仅仅调用一次"onUnbind->onDestroy" */
            unbindService(conn);
            conn = null;
        }

        /**
        Intent i = new Intent();
        i.setPackage("com.itheima.smp");
        i.setAction("com.itheima.mytest.service");
        i.addCategory("com.itheima.mytest.category");
        stopService(i);
        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
