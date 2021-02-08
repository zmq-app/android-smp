package com.itheima.di.components;

import com.itheima.di.modules.MainModule;
import com.itheima.di.scope.MainScope;
import com.itheima.ui.MvpMainActivity;

import dagger.Component;

/**
 * @Subject MainComponent组件
 * @Author  zhangming
 * @Date    2020-11-01 21:22
 */
@MainScope
@Component(modules = MainModule.class,dependencies = NetComponent.class)
public interface MainComponent {
    void inject(MvpMainActivity activity);
}
