package com.itheima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("com.itheima.home.action".equalsIgnoreCase(action)) {
            Intent homeIntent = new Intent();
            homeIntent.setAction(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(homeIntent);
        } else if ("com.itheima.click.action".equalsIgnoreCase(action)) {
            android.util.Log.v("zhangming","========= dismiss Notification ========");
        }
    }
}
