package com.itheima.app.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

public class MyPushReceiver extends BroadcastReceiver {
    private static final String TAG = MyPushReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case JPushInterface.ACTION_NOTIFICATION_RECEIVED:
                    Bundle notificationBundle = intent.getExtras();
                    if (notificationBundle != null) {
                        int notifactionId = notificationBundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                        Log.d(TAG, "ACTION_NOTIFICATION_RECEIVED 接收到推送下来的通知的ID = "+notifactionId);
                    } else {
                        Log.e(TAG,"ACTION_NOTIFICATION_RECEIVED notificationBundle is null...");
                    }
                    break;
                case JPushInterface.ACTION_MESSAGE_RECEIVED:
                    Bundle messageBundle = intent.getExtras();
                    if (messageBundle != null) {
                        Log.d(TAG,"ACTION_MESSAGE_RECEIVED 接收到推送下来的自定义消息Message = "+messageBundle.getString(JPushInterface.EXTRA_MESSAGE));
                    } else {
                        Log.e(TAG,"ACTION_MESSAGE_RECEIVED messageBundle is null...");
                    }
                    break;
                case JPushInterface.ACTION_NOTIFICATION_OPENED:
                    /* 点击跳转Activity界面 */
                    Log.d(TAG, "ACTION_NOTIFICATION_OPENED 用户点击打开了通知...");
                    break;
                case JPushInterface.ACTION_CONNECTION_CHANGE:
                    /* 网络状态变化,断开或连接过程将会收到此广播 */
                    boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                    Log.w(TAG, "ACTION_CONNECTION_CHANGE action = " + intent.getAction() +",connected state change to "+connected);
                    break;
                default:
                    break;
            }
        }
    }
}
