package com.itheima.model;

import java.io.Serializable;

public class ContactItemBean implements Serializable {
    private String mContactName;
    private int mItemType;

    public ContactItemBean(String name, int type) {
        this.mContactName = name;
        this.mItemType = type;
    }

    public String getContactName() {
        return mContactName;
    }

    public int getItemType() {
        return mItemType;
    }
}
