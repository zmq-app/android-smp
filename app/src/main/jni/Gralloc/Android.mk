###  $LOCAL_PATH = "${prefix}/ChatRoom_SMP/app/src/main/jni/Gralloc"  ###
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/../include/

LOCAL_SRC_FILES := gralloc_buffer.cpp

### Gralloc模块定义宏GRALLOC_FRAME_BUFFER ###
LOCAL_CFLAGS += -DGRALLOC_FRAME_BUFFER -Wno-unused-parameter -O2 -Wall -Werror

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE := libjni_gralloc_buffer

LOCAL_LDLIBS := -llog -lz

include $(BUILD_SHARED_LIBRARY)