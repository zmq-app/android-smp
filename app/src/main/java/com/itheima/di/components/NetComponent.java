package com.itheima.di.components;

import com.itheima.di.modules.NetModule;
import com.itheima.presenter.ApiService;

import javax.inject.Singleton;

import dagger.Component;

//第一步 添加@Component
//第二步 添加module
@Component(modules = NetModule.class)
@Singleton
public interface NetComponent {
    ApiService getApiService();
}
