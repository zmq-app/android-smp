package com.itheima.di.modules;

import com.itheima.presenter.MainContract;

import dagger.Module;
import dagger.Provides;

//第一步 添加@Module注解
@Module
public class MainModule {
    private final MainContract.View mView;

    //第二步 使用Provider注解 实例化对象
    public MainModule(MainContract.View mView) {
        this.mView = mView;
    }

    @Provides
    public MainContract.View provideMainView(){
        return mView;
    }
}
