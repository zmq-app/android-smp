###  $LOCAL_PATH = "${prefix}/ChatRoom_SMP/app/src/main/jni/FFmpeg"  ###
LOCAL_PATH := $(call my-dir)

###  (1)引入第三方ffmpeg库(参考链接: "https://blog.csdn.net/qq_26514147/article/details/77994799")  ###
include $(CLEAR_VARS)
LOCAL_MODULE := libavcodec
LOCAL_SRC_FILES := ../prebuilt_libs/libavcodec-56.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavdevice
LOCAL_SRC_FILES := ../prebuilt_libs/libavdevice-56.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavfilter
LOCAL_SRC_FILES := ../prebuilt_libs/libavfilter-5.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavformat
LOCAL_SRC_FILES := ../prebuilt_libs/libavformat-56.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavutil
LOCAL_SRC_FILES := ../prebuilt_libs/libavutil-54.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libpostproc
LOCAL_SRC_FILES := ../prebuilt_libs/libpostproc-53.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libswresample
LOCAL_SRC_FILES := ../prebuilt_libs/libswresample-1.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libswscale
LOCAL_SRC_FILES := ../prebuilt_libs/libswscale-3.so
include $(PREBUILT_SHARED_LIBRARY)


###  (2)编译JNI ffmpeg解码Demo共享库libjni_ffmpeg_video.so  ###
include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/include/ \
	$(LOCAL_PATH)/../include/

LOCAL_SRC_FILES := src/ffmpeg_video_decoder.cpp

### FFmpeg模块定义宏FFMPEG_VIDEO_DECODER ###
LOCAL_CFLAGS += -DFFMPEG_VIDEO_DECODER -Wno-unused-parameter -O2 -Wall -Werror

LOCAL_SHARED_LIBRARIES := \
    libavcodec \
    libavdevice \
    libavfilter \
    libavformat \
    libavutil \
    libpostproc \
    libswresample \
    libswscale

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE := libjni_ffmpeg_video

###  (3)链接liblog.so,libandroid.so [ANativeWindow_fromSurface接口实现,头文件"framework/native/include/android/native_window_jni.h"]  ###
LOCAL_LDLIBS := -llog -lz -landroid

include $(BUILD_SHARED_LIBRARY)