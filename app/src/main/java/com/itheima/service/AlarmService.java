package com.itheima.service;

import android.app.AlarmManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.itheima.utils.CommonUtils;

/**
 * @Subject Alarm定时器服务(全局定时闹钟)
 * @Function 从极光推送原理分析引入的问题
 * @Date 2020-09-02 19:50
 * @Author zhangming
 */
public class AlarmService extends Service {
    private static final String ALARM_BROADCAST_ACTION = "com.itheima.receiver.AlarmBroadcastReceiver";
    private static final long INTERVAL_TIMES = (5*1000);  //intervalTime = (5*1000)ms = 5s

    @Override
    public void onCreate() {
        super.onCreate();
        initAlarmService(INTERVAL_TIMES);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startService(new Intent(AlarmService.this,AlarmRemoteService.class));
        bindService(new Intent(AlarmService.this,AlarmRemoteService.class),connection,Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AlarmBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initAlarmService(long intervalTime) {
        Intent intent = new Intent();
        /* @region begin,android8.0之后,隐式匹配发送广播,需要为intent设置ComponentName */
        if (Build.VERSION.SDK_INT >= 26) {
            ComponentName componentName = new ComponentName(getApplicationContext(),"com.itheima.receiver.AlarmBroadcastReceiver");
            intent.setComponent(componentName);
        }
        intent.setAction(ALARM_BROADCAST_ACTION);
        /* @region end,android8.0之后,隐式匹配发送广播,需要为intent设置ComponentName */

        /* @region begin,Intent传递alarmType和intervalTime */
        //intent.putExtra("alarmType",AlarmManager.RTC_WAKEUP);
        intent.putExtra("alarmType",AlarmManager.ELAPSED_REALTIME);
        intent.putExtra("intervalTime",intervalTime);
        /* @region end,Intent传递alarmType和intervalTime */

        /* AlarmManager发起定时器封装方法 */
        CommonUtils.alarmFunc(this, intent, AlarmManager.RTC_WAKEUP, INTERVAL_TIMES);
        //CommonUtils.alarmFunc(this, intent, AlarmManager.ELAPSED_REALTIME, INTERVAL_TIMES);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IAlarmInterface alarmInterface = IAlarmInterface.Stub.asInterface(service);
            try {
                Log.i("zhangming", "AlarmService connect to " + alarmInterface.getServiceName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("zhangming","AlarmService onServiceDisconnected...");
            startService(new Intent(AlarmService.this,AlarmRemoteService.class));
            bindService(new Intent(AlarmService.this,AlarmRemoteService.class), connection, Context.BIND_IMPORTANT);
        }
    };

    private class AlarmBinder extends IAlarmInterface.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return AlarmService.class.getName();
        }
    }
}
