package com.itheima.di.components;

import com.itheima.di.modules.Dagger2Module;
import com.itheima.ui.Dagger2Activity;

import dagger.Component;

/* 创建一个Component类,作为依赖提供方DaggerBean,Dagger2Module和依赖需求方Dagger2Activity成员变量直接的桥梁 */
@Component(modules = Dagger2Module.class)
public interface Dagger2Component {
    void inject(Dagger2Activity activity);
}
