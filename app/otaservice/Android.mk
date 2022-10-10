LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := privapp-permissions-com.vnptt.ota.xml
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/permissions
LOCAL_SRC_FILES := $(LOCAL_MODULE)
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PACKAGE_NAME := OTAService
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_STATIC_JAVA_LIBRARIES := org.apache.http.legacy
LOCAL_SRC_FILES := $(call all-subdir-java-files) \
    src/com/vnptt/ota/service/IResponseQueryNewVersion.aidl \
    src/com/vnptt/ota/service/IServiceManagement.aidl
LOCAL_CERTIFICATE := platform
LOCAL_DEX_PREOPT := false

LOCAL_REQUIRED_MODULES := privapp-permissions-com.vnptt.ota.xml

include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
