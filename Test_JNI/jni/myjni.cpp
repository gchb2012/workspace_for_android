#include <jni.h>
#include "amcomdef.h"

JNIEXPORT jint native_add(JNIEnv *env, jobject thiz, jint a, jint b)
{
	int result = a + b;
	int c = 0;

	//LOGI("c: %d + %d = %d", a, b, result);

	return result;
}

static JNINativeMethod gMethods[] =
{
	/* name,							signature,									funcPtr */
	{ "native_add",						"(II)I",									(void*) native_add },
};


int register_arcsoft_android_ArcGif(JNIEnv* env)
{
	jclass clazz = env->FindClass("com/zdf/test_jni/MainActivity");

	if (clazz == MNull)
	{
		return -1;
	}
	if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(JNINativeMethod)) < 0)
	{
		return -1;
	}
	return 0;
}

extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv* env = 0;
	jint result = -1;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return result;
	}

	register_arcsoft_android_ArcGif(env);

	return JNI_VERSION_1_4;
}
