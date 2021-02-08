#ifndef __COMMON_UTILS_H_
#define __COMMON_UTILS_H_

#include <stdio.h>
#include <android/log.h>
#include <assert.h>

/** FFmpeg Video解码的头文件 **/
#include "ffmpeg_video_decoder.h"

/** Gralloc Buffer图形缓冲区分配与操作的头文件 **/
#include "gralloc_buffer.h"

/** 自定义LOG标识 **/
#define TAG "chatroom_smp_native"

/** ALOG日志宏 **/
#define ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define ALOGI(...)  __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define ALOGW(...)  __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

/**
 * 需要注册的函数列表,放在JNINativeMethod 类型的数组中,以后如果需要增加函数,只需在这里添加就行
 * 参数:
 * 1.java中用native关键字声明的函数名
 * 2.签名(传进来参数类型和返回值类型的说明)
 * 3.C/C++中对应函数的函数名(地址)
 */
#ifdef FFMPEG_VIDEO_DECODER
static JNINativeMethod gFFmpegMethods[] = {
    {"ffmpeg_video_render","(Ljava/lang/String;Ljava/lang/Object;)I",(void*)ffmpeg_video_render},
};
#endif

#ifdef GRALLOC_FRAME_BUFFER
static JNINativeMethod gGrallocMethods[] = {
    {"gralloc_framebuffer","()I",(void*)gralloc_framebuffer},
};
#endif

/** 此函数通过调用RegisterNatives方法来注册我们的函数 **/
int registerNativeMethods(JNIEnv* env, const char* className,JNINativeMethod* gMethods,int methodsNum)
{
    jclass clazz;
    /** 找到声明native方法的类 **/
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    /** 注册函数参数: java类 所要注册的函数数组 注册函数的个数 **/
    if (env->RegisterNatives(clazz,gMethods,methodsNum) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

#ifdef FFMPEG_VIDEO_DECODER
static int registerFFmpegNatives(JNIEnv* env)
{
    /** 指定类的路径,通过FindClass方法来找到对应的类 **/
    const char* className  = "com/itheima/ui/JniFFmpegVideoDecoderActivity";
    return registerNativeMethods(env,className,gFFmpegMethods, sizeof(gFFmpegMethods)/ sizeof(gFFmpegMethods[0]));
}
#endif

#ifdef GRALLOC_FRAME_BUFFER
static int registerGrallocNatives(JNIEnv* env)
{
    /** 指定类的路径,通过FindClass方法来找到对应的类 **/
    const char* className  = "com/itheima/ui/JniGrallocBufferActivity";
    return registerNativeMethods(env,className,gGrallocMethods, sizeof(gGrallocMethods)/ sizeof(gGrallocMethods[0]));
}
#endif

/** 当JAVA上层调用System.loadLibrary接口时,将会在C/C++文件中回调JNI_OnLoad函数 **/
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;

    /** 用于获取JNIEnv **/
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);

#ifdef FFMPEG_VIDEO_DECODER
    /** 注册函数registerFFmpegNatives => registerNativeMethods => env->RegisterNatives **/
    if(!registerFFmpegNatives(env)){
        return -1;
    }
#endif

#ifdef GRALLOC_FRAME_BUFFER
    /** 注册函数registerGrallocNatives => registerNativeMethods => env->RegisterNatives **/
    if(!registerGrallocNatives(env)) {
        return -1;
    }
#endif

    /** 返回jni的版本 **/
    return JNI_VERSION_1_4;
}

#endif