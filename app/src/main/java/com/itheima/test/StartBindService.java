package com.itheima.test;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.itheima.utils.CommonConstants;

/**
 * @Subject  Start Or Bind方式启动Service
 * @Function Start方式启动Service onCreate() ==> onStartCommand() ==> onStart(); ==> onDestroy();
 * @Function Bind方式启动Service onCreate()  ==> onBind(); ==> onUnBind(); ==> onDestroy();
 * @Author   zhangming
 * @Date     2018-10-12 10:31
 */
public class StartBindService extends Service {
    //TODO onCreate方法在启动后仅仅调用一次,之后多次调用startService or BindService方法均不会被调用
    @Override
    public void onCreate() {
        Log.i(CommonConstants.TAG,"MyTestService onCreate...");
        super.onCreate();
    }

    //TODO onStartCommand方法伴随多次调用startService or BindService方法,均会调用多次 [优先onStartCommand,之后onStart]
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(CommonConstants.TAG,"MyTestService onStartCommand...");
        return super.onStartCommand(intent, flags, startId);
    }

    //TODO onStart方法伴随多次调用startService or BindService方法,均会调用多次 [优先onStartCommand,之后onStart]
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(CommonConstants.TAG,"MyTestService onStart...");
        super.onStart(intent, startId);
    }

    //TODO onDestroy方法在启动后仅仅在退出时调用一次
    @Override
    public void onDestroy() {
        Log.i(CommonConstants.TAG,"MyTestService onDestroy...");
        super.onDestroy();
    }

    //TODO onBind方法启动后仅仅调用一次,之后多次调用startService or BindService方法均不会被调用
    @Override
    public IBinder onBind(Intent intent) {
        MyBinder mBinder = new MyBinder();
        Log.i(CommonConstants.TAG,"MyTestService onBind,mBinder = "+mBinder.toString());
        return mBinder;
    }

    //TODO onUnbind仅仅当调用unbindService方法时执行一次,而多次调用unbindService方法会抛出异常,需对MyConnection实例conn进行判空检测
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(CommonConstants.TAG,"MyTestService onUnbind...");
        return true;
    }

    //TODO 重新绑定服务的前提条件:服务处于启动状态,且执行过onUnbind方法返回true,下次再调用bindService方法时,将会执行onRebind方法,而不会执行onBind方法
    //TODO 比如:先执行startService方法,然后执行bindService方法,再执行unbindService方法,但没有进行stopService,最后再次执行bindService方法时,服务的生命周期将执行onRebind方法
    @Override
    public void onRebind(Intent intent) {
        Log.i(CommonConstants.TAG,"MyTestService onRebind...");
        super.onRebind(intent);
    }

    public class MyBinder extends Binder {
        public void systemOut(){
            Log.i(CommonConstants.TAG,"MyBinder systemOut function is called...");
        }
    }
}
