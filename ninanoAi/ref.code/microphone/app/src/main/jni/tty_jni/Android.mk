#ifeq ($(strip $(BUILD_WITH_GST)),true)

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE:= libttyjni

LOCAL_DECOMP_FILES:= \
	class/decomp.cpp \
	class/uart.cpp \
	class/sctrl.c

LOCAL_SRC_FILES:= $(LOCAL_DECOMP_FILES) \
	JNIHelp.c \
	onload.cpp

#LOCAL_SHARED_LIBRARIES := 

LOCAL_ARM_MODE :=arm
LOCAL_CFLAGS := -DHAVE_CONFIG_H -DFPM_ARM -ffast-math -O3
LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/android 

LOCAL_CFLAGS := \
    -DHAVE_CONFIG_H \
    -DFPM_DEFAULT

LOCAL_LDLIBS := \
	-llog

include $(BUILD_SHARED_LIBRARY)
#endif
