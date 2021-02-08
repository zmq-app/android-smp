LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := inject
LOCAL_SRC_FILES += src/ptraceInject.c \
	src/InjectModule.c

LOCAL_C_INCLUDES += $(LOCAL_PATH)/include/

LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog

include $(BUILD_EXECUTABLE)


include $(CLEAR_VARS)
LOCAL_MODULE := InjectFunction
LOCAL_SRC_FILES := src/InjectFunction.c

LOCAL_C_INCLUDES += $(LOCAL_PATH)/include/

LOCAL_ARM_MODE := arm

LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)