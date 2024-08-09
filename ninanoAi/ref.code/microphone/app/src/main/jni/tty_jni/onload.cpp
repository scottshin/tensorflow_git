#include "JNIHelp.h"
#include "jni.h"
#include "log.h"

#define LOG_TAG "native"

//int register_qn_smartad_gpiosw(JNIEnv* env);
int register_decomp(JNIEnv* env);
//int register_mp3(JNIEnv* env);

extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv* env = NULL;
	jint result = -1;

	LOGI("JNI_OnLoad");
	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("GetEnv failed!");
		return result;
	}
	LOG_ASSERT(env, "Could not retrieve the env!");

	register_decomp(env);
	//register_qn_smartad_gpiosw(env);		// decomp
	//register_mp3(env);

	return JNI_VERSION_1_4;
}
