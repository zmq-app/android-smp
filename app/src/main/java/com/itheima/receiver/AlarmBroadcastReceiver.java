package com.itheima.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.itheima.network.TcpStreamClient;
import com.itheima.utils.CommonUtils;

/**
 * @Subject Alarm服务广播,用于实时发送心跳包
 * @Reason 大部分移动无线网络运营商都在链路一段时间没有数据通讯时会淘汰NAT表中的对应项,造成链路中断;为了不让NAT表失效,需要定时的发心跳,以刷新NAT表项,避免被淘汰
 * @Function 在android客户端使用Push推送时,应该使用AlarmManager来实现心跳功能,使其真正实现长连接
 * @URL  https://www.cnblogs.com/manuosex/p/3660727.html
 * @Date 2020-09-02 19:50
 * @Author zhangming
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmBroadcastReceiver.class.getSimpleName();
    private static final String[] actions = new String[] {
        "com.itheima.receiver.AlarmBroadcastReceiver"
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if ((action != null) && action.equals(actions[0])) {
                /* Intent获取参数alarmType和intervalTime */
                int alarmType = intent.getIntExtra("alarmType", AlarmManager.RTC_WAKEUP);
                long intervalTime = intent.getLongExtra("intervalTime",0);
                Log.i(TAG,"AlarmManager type = "+alarmType+" intervalTime = "+intervalTime+" ms");

                /* AlarmManager发起定时器封装方法 */
                CommonUtils.alarmFunc(context, intent, AlarmManager.RTC_WAKEUP, intervalTime);
                //CommonUtils.alarmFunc(context, intent, AlarmManager.ELAPSED_REALTIME, intervalTime);

                /* 干活,向服务器模拟发送心跳包 */
                //TcpStreamClient.getInstance().start();

                /* 进程会被拉起重启,再次kill本进程 */
                //System.exit(0);
            }
        }
    }
}
