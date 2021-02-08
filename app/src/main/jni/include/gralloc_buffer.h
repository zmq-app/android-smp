#ifndef __GRALLOC_BUFFER_H__
#define __GRALLOC_BUFFER_H__

#include <jni.h>

extern "C" JNIEXPORT jint
JNICALL
gralloc_framebuffer(JNIEnv* env,jobject obj);

#endif //__GRALLOC_BUFFER_H__