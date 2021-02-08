package com.itheima.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.itheima.di.components.DaggerNetComponent;
import com.itheima.di.components.NetComponent;
import com.itheima.di.modules.NetModule;
import com.itheima.utils.CommonConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangming on 2018/8/4.
 */
public class SmpApplication extends Application {
    private NetComponent netComponent;
    private boolean isDebugRouter = true;

    @Override
    public void onCreate() {
        super.onCreate();

        int screenDensity = getResources().getDisplayMetrics().densityDpi;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        Log.d(CommonConstants.TAG, "screenDensity: " + screenDensity + " screenWidth: " + screenWidth + " screenHeight: " + screenHeight);

        setApp(this);
        initNetComponent();
        //CommonConstants.SOFTWARE_VERSION = getAppVersionName(this);

        if (isDebugRouter) {
            //ARouter初始化
            ARouter.openLog();
            //打印日志
            ARouter.openDebug();
        }
        ARouter.init(this);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init();
    }

    /**
     * getAppVersionName
     * @param context
     * @return String
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // Get the package info
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo("com.itheima.smp", 0);
            versionName = pi.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public void getRunningProcess() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> am = manager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo processInfo : am) {
            System.out.printf("pid = %d,processName = %s,importance = %d\n",
                processInfo.pid,processInfo.processName,processInfo.importance);
        }
    }

    /**
     * 实例
     */
    private static Application app = null;

    /**
     * 获取App实例
     * @return 实例
     */
    public static Application getApp() {
        return app;
    }

    /**
     * 设置App实例
     * @param app the app to set
     */
    public static void setApp(Application app) {
        SmpApplication.app = app;
    }

    /**
     * 获取当前app应用的全局上下文,作为Application实例返回
     * @param context
     * @return
     */
    public static SmpApplication get(Context context){
        return (SmpApplication)context.getApplicationContext();
    }

    /**
     * 初始化NetComponent
     */
    private void initNetComponent(){
        netComponent = DaggerNetComponent.builder()
                .netModule(new NetModule())
                .build();
    }

    public NetComponent getNetComponent() {
        return netComponent;
    }


    /**
     * 简易Activity劫持
     */
    private List<String> hijackedList = new ArrayList<>(); //存放已经被劫持的程序

    public boolean hasProgressBeHijacked(String processName) {
        return hijackedList.contains(processName);
    }

    public void addHijacked(String processName) {
        hijackedList.add(processName);
    }

    public void clearHijacked() {
        hijackedList.clear();
    }
}