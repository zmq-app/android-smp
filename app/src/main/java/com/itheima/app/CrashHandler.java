package com.itheima.app;

import android.os.Process;

/**
 * @Subject  android进程全局的crash监控,系统中crash的处理和分发
 * @URL      https://blog.csdn.net/u011578734/article/details/107038011
 * @Author   zhangming
 * @Date     2020-10-04 12:50
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mSystemDefaultCrashHandler;

    private CrashHandler() {
    }

    /* CrashHandler饿汉式单例获取 */
    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init() {
        /* 保存系统当前进程全局默认的RuntimeInit.UncaughtExceptionHandler实例 */
        mSystemDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        System.out.println("mSystemDefaultCrashHandler className = "+mSystemDefaultCrashHandler.getClass().getSimpleName());
        /* 重新设置当前进程中所有线程的全局默认的UncaughtExceptionHandler实例为CrashHandler类实例 */
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable throwable) {
        if (throwable != null) {
            String msg = throwable.getMessage();
            System.out.println("uncaughtException message = "+msg);
            throwable.printStackTrace();
        }
        /* 如果系统提供了默认的异常处理handler,则交由系统结束进程,否则交由自己结束进程 */
        if (mSystemDefaultCrashHandler != null) {
            mSystemDefaultCrashHandler.uncaughtException(t,throwable);
        } else {
            Process.killProcess(Process.myPid());
        }
    }
}
