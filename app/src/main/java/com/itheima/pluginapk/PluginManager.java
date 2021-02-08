package com.itheima.pluginapk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * @Subject APP插件管理类,封装部分方法,桥接HostAppActivity和ProxyPluginActivity
 * @Author  zhangming
 * @Date    2019-11-07 20:36
 */
public class PluginManager {
    //-------1:构建插件管理单例类start--------
    private static PluginManager instance;

    private PluginManager() {}

    /* 由于多线程实例会共享静态变量instance,使用双重判空和同步块实现多线程并发的懒汉式单例模型 */
    public static PluginManager getInstance() {
        if (instance == null) {
            synchronized (PluginManager.class) {
                if (instance == null) {
                    instance = new PluginManager();
                }
            }
        }
        return instance;
    }
    //-------1:构建插件管理单例类end--------

    private Context context;
    private String entryActivityName;
    private DexClassLoader dexClassLoader;
    private Resources resources;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }


    //-------2:加载第三方APP插件入口path start--------
    public void loadPluginPath(String path) {
        setEntryActivityName(path);
        setClassLoader(path);
        setResources(path);
    }
    //-------2:加载第三方APP插件入口path end--------


    //-------3:获取插件APP入口activity name,用于宿主APP跳转至插件APP中 start--------
    private void setEntryActivityName(String path) {
        PackageManager pm = context.getPackageManager();
        //参数一是apk的路径,参数二是希望得到的内容
        PackageInfo packageInfo = pm.getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES);
        entryActivityName = packageInfo.activities[0].name; //保存主类入口Activity名称
    }

    public String getEntryActivityName() {
        return entryActivityName;
    }
    //-------3:获取插件APP入口activity name,用于宿主APP跳转至插件APP中 end--------


    //-------4:构造framework classLoader start-------------
    private void setClassLoader(String path) {
        //dex的缓存路径
        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);
        //dexOutFile.getAbsolutePath();  dexOutFile.getAbsoluteFile().getAbsolutePath(); 两者结果一致
        //BaseDexClassLoader.getSystemClassLoader();
        dexClassLoader = new DexClassLoader(path,dexOutFile.getAbsolutePath(),null,context.getClassLoader());
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }
    //-------4:构造framework classLoader end-------------


    //-------5:构造resources,用于获取third-app的系统资源 start--------
    public void setResources(String path) {
        try {
            //将APK加入到AssetManager中进行管理
            //由于构建resources必须要传入AssetManager,这里先通过反射创建一个AssetManager实例
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath",String.class);  //方法名,不定参数的类型Type
            addAssetPath.invoke(assetManager,path);  //反射Method实例.invoke(反射对象实例,反射方法的不定参数)
            //创建Resources实例
            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Resources getResources() {
        return resources;
    }
    //-------5:构造resources,用于获取third-app的系统资源 end--------
}
