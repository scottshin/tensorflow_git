
# Uncomment this if you're using STL in your project
# See CPLUSPLUS-SUPPORT.html in the NDK documentation for more information
# APP_STL := stlport_static 

APP_STL := stlport_static 
STLPORT_FORCE_REBUILD := true

APP_PLATFORM := android-19
#APP_OPTIM := debug
APP_ABI = armeabi armeabi-v7a
#APP_ABI=all



#PLATFORM_PREFIX := /opt/android-ext/
#APP_CPPFLAGS := -I$(PLATFORM_PREFIX)/include/freetype2/



APP_CFLAGS += -Wno-error=format-security



