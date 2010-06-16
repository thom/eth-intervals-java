#include <jni.h>
#include <syscall.h>
#include "ch_ethz_hwloc_Place.h"

/* Implementation for class ch_ethz_hwloc_Place */

long int gettid();

/*
 * Class:     ch_ethz_hwloc_Place
 * Method:    setAffinity
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL
Java_ch_ethz_hwloc_Place_setAffinity(JNIEnv *env, jobject obj,
		jintArray physical_units) {
	// TODO: setAffinity
}

/*
 * Class:     ch_ethz_hwloc_Place
 * Method:    getAffinity
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL
Java_ch_ethz_hwloc_Place_getAffinity(JNIEnv *env, jobject obj) {
	// TODO: getAffinity;
	return NULL;
}

/*
 * Class:     ch_ethz_hwloc_Place
 * Method:    getThreadId
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_ch_ethz_hwloc_Place_getThreadId(JNIEnv *env, jobject obj) {
	return gettid();
}

long int gettid() {
	return syscall(SYS_gettid);
}
