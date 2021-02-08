package com.itheima.model;

import javax.inject.Inject;

/**
 * @Subject Dagger实体类,依赖提供方
 * @Author  zhangming
 * @Date    2021-01-24 16:35
 */
public class DaggerBean {
    private int id;
    private String name;

    /* 作为依赖提供方,@Inject注解添加到类的构造函数上 */
    @Inject
    public DaggerBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "DaggerBean[id = "+id+" name = "+name+"]";
    }
}
