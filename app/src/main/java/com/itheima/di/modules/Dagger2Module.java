package com.itheima.di.modules;

import dagger.Module;
import dagger.Provides;

/**
 * 这个类的命名以Module结尾,里面针对Person类含有@Inject构造函数的参数.
 * 添加provide方法,这些provide方法需要添加@Provides注解,并且命名开头以provide开头.
 * Module类作为依赖提供方,提供构造函数中的参数
 */
@Module
public class Dagger2Module {
    private int id;
    private String name;

    public Dagger2Module(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Provides
    int provideId(){
        return id;
    }

    @Provides
    String provideName(){
        return name;
    }
}
