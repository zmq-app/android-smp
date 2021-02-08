package com.itheima.utils;

/**
 * @Subject 设计模式[建造者模式]
 * @Author  zhangming
 * @Date    2018-09-22
 */
public final class VideoConfiguration {
    //初始化方式一: 在定义变量时直接赋值
    //初始化方式二: 声明完变量后在构造方法中为其赋值 [如果采用用这种方式,那么每个构造方法中都要有该整型变量赋值的语句]
    public final int width;
    public final int height;
    public final int minBps;
    public final int maxBps;
    public final int fps;
    public final int ifi;  //关键帧的间隔
    public final String mime;

    public static final int DEFAULT_WIDTH   = 360;
    public static final int DEFAULT_HEIGHT  = 640;
    public static final int DEFAULT_FPS     = 15;
    public static final int DEFAULT_MIN_BPS = 400;
    public static final int DEFAULT_MAX_BPS = 1300;
    public static final int DEFAULT_IFI     = 2;
    public static final String DEFAULT_MIME = "video/avc";

    public VideoConfiguration(final Builder builder){
        this.width  = builder.width;
        this.height = builder.height;
        this.minBps = builder.minBps;
        this.maxBps = builder.maxBps;
        this.fps    = builder.fps;
        this.ifi    = builder.ifi;
        this.mime   = builder.mime;
    }

    //创建一个默认的VideoConfiguration配置
    public static VideoConfiguration createDefault(){
        return new Builder().build();
    }

    public static class Builder{
        private int width   = DEFAULT_WIDTH;
        private int height  = DEFAULT_HEIGHT;
        private int minBps  = DEFAULT_MIN_BPS;
        private int maxBps  = DEFAULT_MAX_BPS;
        private int fps     = DEFAULT_FPS;
        private int ifi     = DEFAULT_IFI;
        private String mime = DEFAULT_MIME;

        public Builder setSize(int width,int height){
            this.width  = width;
            this.height = height;
            return this;
        }

        public Builder setBps(int minBps, int maxBps){
            this.minBps = minBps;
            this.maxBps = maxBps;
            return this;
        }

        public Builder setFps(int fps){
            this.fps = fps;
            return this;
        }

        public Builder setMime(String mime){
            this.mime = mime;
            return this;
        }

        public VideoConfiguration build(){
            return new VideoConfiguration(this);
        }
    }
}
