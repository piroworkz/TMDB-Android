//
// Created by David Luna on 29/06/24_
//
#include <jni.h>
#define UNUSED(x) (void)(x)

JNIEXPORT jstring JNICALL
Java_com_davidluna_tmdb_core_1data_di_NativeModule_getApiKey(JNIEnv *env, jobject pVoid) {
    UNUSED(pVoid);
    return (*env)->NewStringUTF(env, MY_API_KEY);
}

JNIEXPORT jstring JNICALL
Java_com_davidluna_tmdb_core_1data_di_NativeModule_getBaseUrl(JNIEnv *env, jobject pVoid) {
    UNUSED(pVoid);
    return (*env)->NewStringUTF(env, BASE_URL);
}

