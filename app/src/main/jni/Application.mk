APP_STL          := stlport_static
APP_BUILD_SCRIPT:=$(call my-dir)/Android.mk
APP_PLATFORM := android-26

## 注:使用"ndk-build"命令编译时,需要强制指明应用程序基于哪些指令集进行编译 ##
## 否则会报错"XXX/src/main/obj/local/arm64-v8a/libavformat-56.so: error adding symbols: File in wrong format" ##
## 因为如果不写Application.mk文件,会默认编译出所有Android.mk中写的libs/modules,其中APP_ABI会默认指定为armeabi ##
## 参考链接: "https://blog.csdn.net/vincent_blog/article/details/9179271" ##
APP_ABI := armeabi armeabi-v7a

## 问题:使用ndk-r14b编译opus动态库的时候会发生错误,"error in backend: Cannot select: 0x4043770:XXX" ##
## 解决:指定ndk交叉编译链的版本 ##
NDK_TOOLCHAIN_VERSION := 4.9