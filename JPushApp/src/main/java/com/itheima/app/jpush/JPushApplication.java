package com.itheima.app.jpush;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

public class JPushApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);  // 初始化JPush
    }
}
