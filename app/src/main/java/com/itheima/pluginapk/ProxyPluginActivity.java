package com.itheima.pluginapk;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @Subject 代理APP插件Activity
 * @Author  zhangming
 * @Date    2019-11-07 19:35
 */
public class ProxyPluginActivity extends Activity {
    //插件APP暴露的接口,准备在宿主APP的生命周期回调中进行调用
    private PluginBaseInterface appInterface = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 通过反射拿到class,但不能用以下方式
         * ClassLoader.loadClass(className);
         * Class.forName(className)
         * 因为插件APP是没有被安装!!!
         * 所以这里我们调用重写的classLoader
         */
        try {
            //获取插件APP的className
            String className = getIntent().getStringExtra("className");
            //加载主类Activity的Class类对象
            Class activityClass = getClassLoader().loadClass(className);
            //反射获取接口实例appInterface
            appInterface = (PluginBaseInterface)activityClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //调用第三方APP插件主Activity的attach和onCreate生命周期回调接口
        if (appInterface != null) {
            appInterface.attach(this);
            Bundle bundle = new Bundle();
            appInterface.onCreate(bundle);
        }
    }

    /* @region jide begin,add zmq for在Activity生命周期回调第三方插件APP的接口 */
    @Override
    protected void onStart() {
        super.onStart();
        if (appInterface != null) {
            appInterface.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appInterface != null) {
            appInterface.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (appInterface != null) {
            appInterface.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appInterface != null) {
            appInterface.onDestroy();
        }
    }
    /* @region jide end,add zmq for在Activity生命周期回调第三方插件APP的接口 */

    /* 重写覆盖ClassLoader,使用Framework DexClassLoader实例,默认App系统类加载器是PathClassLoader,而BootClassLoader是其parent类加载器 */
    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }

    /* 不用系统的resources,自己实现一个resources */
    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }
}
