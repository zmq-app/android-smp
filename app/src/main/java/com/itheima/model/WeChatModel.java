package com.itheima.model;

public class WeChatModel {
    //发送类型
    public static final int SEND = 0;
    //接收类型
    public static final int RECEIVE = 1;

    private int imgId;
    private String name;
    private String content;

    //收发类型
    private int type;

    public WeChatModel(int imgId, String name, String content, int type) {
        this.imgId = imgId;
        this.name = name;
        this.content = content;
        this.type = type;
    }

    public int getImgId() {
        return imgId;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}
