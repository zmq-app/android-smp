#ifndef __FFMPEG_VIDEO_DECODER
#define __FFMPEG_VIDEO_DECODER

#include <jni.h>

extern "C" JNIEXPORT jint
JNICALL
ffmpeg_video_render(JNIEnv* env,jobject obj, jstring inputVideoPath, jobject surface);

#endif //__FFMPEG_VIDEO_DECODER