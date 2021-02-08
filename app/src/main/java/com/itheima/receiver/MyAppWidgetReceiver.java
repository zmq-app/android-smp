package com.itheima.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.itheima.smp.R;
import com.itheima.service.MyAppWidgetListService;
import com.itheima.ui.HomeActivity;
import com.itheima.utils.BitmapUtils;
import com.itheima.utils.CommonConstants;

/**
 * @Subject  AppWidget的使用[本质是广播接受者BroadCastReceiver]
 * @Function [AppWidgetProvider] [AppWidgetService,AppWidgetServiceImpl] [AppWidgetHost::Callbacks,AppWidgetHostView]
 * @URL      https://www.cnblogs.com/joy99/p/6346829.html
 * @Author   zhangming
 * @Date     2018-11-04 16:22
 */
public class MyAppWidgetReceiver extends AppWidgetProvider {
    private RemoteViews mRemoteViews;
    public static final String CHANGE_IMAGE_ACTION = "com.itheima.action.CHANGE_IMAGE";
    public static final String ROTATE_IMAGE_ACTION = "com.itheima.action.ROTATE_IMAGE";

    private int[] imgs = new int[]{ R.drawable.monitor_launcher,R.mipmap.ic_launcher };
    private static int curImageIndex;  //必须为静态变量,记录当前图标的索引值

    /** 小部件被添加或小部件被更新时调用,更新周期为XML文件my_app_widget_info指定的updatePeriodMillis值 **/
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(CommonConstants.TAG,"MyAppWidgetReceiver onUpdate...");

        //TODO Button设置Text和TextView控件一样,因为Button本身继承自TextView
        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
        mRemoteViews.setImageViewResource(R.id.img_widget,R.drawable.monitor_launcher);
        mRemoteViews.setTextViewText(R.id.btn_widget,context.getResources().getString(R.string.app_widget_btn_text));

        //setOnClickFillInIntent  可以将fillInIntent添加到pendingIntent中
        //(1)为Button设置点击事件,PendingIntent.getActivity:当待定意图发生时,效果相当于context.startActivity(intent);
        Intent btnIntent = new Intent(context, HomeActivity.class);
        PendingIntent pBtnIntent = PendingIntent.getActivity(context,200,btnIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_widget,pBtnIntent);
        //(2)为ImageView设置点击事件,PendingIntent.getBroadcast：当待定意图发生时,效果相当于context.sendBroadcast(intent);
        //(2)注意:在android8.0之后,如果使用PendingIntent.getBroadcast(..)发送延时广播给自己(MyAppWidgetReceiver),则必须要给Intent设置Component
        //(2)否则在源码的AMS BroadcastQueue类的processNextBroadcastLocked方法中,报错"Background execution not allowed: receiving Intent"
        Intent imgIntent = new Intent();
        imgIntent.setAction(ROTATE_IMAGE_ACTION);
        imgIntent.setComponent(new ComponentName(context, MyAppWidgetReceiver.class));
        PendingIntent pImgIntent = PendingIntent.getBroadcast(context,0,imgIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.img_widget,pImgIntent);

        //设置ListView的adapter
        //(01)intent: 对应启动ListViewService(RemoteViewsService)的intent
        //(02)setRemoteAdapter: 设置ListView的适配器
        //通过setRemoteAdapter将ListView和ListViewService关联起来,
        //以达到通过GridWidgetService更新gridview控件的目标
        Intent listIntent = new Intent(context, MyAppWidgetListService.class);
        mRemoteViews.setRemoteAdapter(R.id.list_widget,listIntent);
        mRemoteViews.setEmptyView(R.id.list_widget,android.R.id.empty);

        //setPendingIntentTemplate 设置pendingIntent模板
        //注意:在android8.0之后,如果使用PendingIntent.getBroadcast(..)发送延时广播给自己(MyAppWidgetReceiver),则必须要给Intent设置Component
        //否则在源码的AMS BroadcastQueue类的processNextBroadcastLocked方法中,报错"Background execution not allowed: receiving Intent"
        Intent toIntent = new Intent(CHANGE_IMAGE_ACTION);
        toIntent.setComponent(new ComponentName(context, MyAppWidgetReceiver.class));
        PendingIntent updateIntent = PendingIntent.getBroadcast(context,200,toIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setPendingIntentTemplate(R.id.list_widget,updateIntent);

        //更新AppWidget视图,执行对应的延后意图PendingIntent
        ComponentName cName = new ComponentName(context, MyAppWidgetReceiver.class);
        appWidgetManager.updateAppWidget(cName, mRemoteViews);
    }

    /** 当窗口小部件第一次被添加到桌面时回调此方法,可添加多次但只在第一次调用 **/
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i(CommonConstants.TAG,"MyAppWidgetReceiver onEnabled...");
    }

    /** 当最后一个小部件被删除时调用该方法 **/
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.i(CommonConstants.TAG,"MyAppWidgetReceiver onDisabled...");
    }

    /** 调用父类的onReceive方法,最后根据不同的Action来分别调用onEnable,onDisable,onUpdate方法 **/
    /** 后续实现了接收自定义的Action,本质是广播接受者的onReceive方法 **/
    @Override
    public void onReceive(final Context context, Intent intent) {
        //@region jide begin,add zmq for 优先调用父类的onReceive方法,用于接收Action = ACTION_APPWIDGET_UPDATE,ACTION_APPWIDGET_ENABLED...
        super.onReceive(context, intent);
        //@region jide end,add zmq for 优先调用父类的onReceive方法,用于接收Action = ACTION_APPWIDGET_UPDATE,ACTION_APPWIDGET_ENABLED...
        if (TextUtils.equals(CHANGE_IMAGE_ACTION, intent.getAction())) {
            Log.i(CommonConstants.TAG, "MyAppWidgetReceiver onReceive CHANGE_IMAGE_ACTION...");
            Bundle extras = intent.getExtras();
            int position = extras.getInt(MyAppWidgetListService.INITENT_DATA_KEY);

            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
            mRemoteViews.setImageViewResource(R.id.img_widget, imgs[position % imgs.length]);
            curImageIndex = position % imgs.length;

            ComponentName cName = new ComponentName(context, MyAppWidgetReceiver.class);
            AppWidgetManager.getInstance(context).updateAppWidget(cName, mRemoteViews);
        } else if (TextUtils.equals(ROTATE_IMAGE_ACTION, intent.getAction())) {
            Log.i(CommonConstants.TAG, "MyAppWidgetReceiver onReceive ROTATE_IMAGE_ACTION...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(), imgs[curImageIndex]);
                    for (int i = 0; i < 37; i++) {
                        RemoteViews rViews = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
                        float degree = (i * 10) % 360;
                        rViews.setImageViewBitmap(R.id.img_widget, BitmapUtils.getInstance().rotateBitmap(srcBitmap, degree));

                        /* 注意:在android8.0之后,如果使用PendingIntent.getBroadcast(..)发送延时广播给自己(MyAppWidgetReceiver),则必须要给Intent设置Component */
                        /* 否则在源码的AMS BroadcastQueue类的processNextBroadcastLocked方法中,报错"Background execution not allowed: receiving Intent" */
                        Intent imgIntent = new Intent();
                        imgIntent.setAction(ROTATE_IMAGE_ACTION);
                        imgIntent.setComponent(new ComponentName(context, MyAppWidgetReceiver.class));
                        PendingIntent pImgIntent = PendingIntent.getBroadcast(context,0,imgIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                        rViews.setOnClickPendingIntent(R.id.img_widget,pImgIntent);

                        ComponentName cName = new ComponentName(context, MyAppWidgetReceiver.class);
                        AppWidgetManager.getInstance(context).updateAppWidget(cName,rViews);
                        SystemClock.sleep(30);
                    }
                }
            }).start();
        }
    }
}
