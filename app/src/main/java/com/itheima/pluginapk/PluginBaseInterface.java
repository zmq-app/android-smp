package com.itheima.pluginapk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * @Subject 公共接口PluginBaseInterface,定义App中Activity的生命周期[宿主APP和插件APP中均包含同一接口]
 * @Description 插件是没有被安装的,它没有上下文,也就没有生命周期,所以插件Activity的生命周期就要由宿主APP来控制
 * @Author  zhangming
 * @Date    2019-11-07 19:35
 */
public interface PluginBaseInterface {
    /* 需要宿主app注入给插件app上下文,对应Activity attach回调生命周期 */
    void attach(Activity activity);

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onDestroy();

    void onSaveInstanceState(Bundle outState);

    void setContentView(int layoutResID);

    View findViewById(int id);
}
