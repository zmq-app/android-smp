package com.itheima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.itheima.service.HijackService;

public class AutoStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if ((action != null) && action.equals("android.intent.action.BOOT_COMPLETED")) {
                Log.i("zhangming","AutoStartReceiver BOOT_COMPLETED...");
                //启动HijackService
                Intent _intent = new Intent(context, HijackService.class);
                context.startService(_intent);
            }
        }
    }
}
