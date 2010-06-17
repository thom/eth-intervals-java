#define _GNU_SOURCE
#include <jni.h>
#include <syscall.h>
#include <sched.h>
#include <pthread.h>
#include <stdbool.h>
#include <unistd.h>
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
 * Signature: (I)[Z
 */
JNIEXPORT jbooleanArray JNICALL
Java_ch_ethz_hwloc_Place_getAffinity(JNIEnv *env, jobject obj) {
	int s, i;
	int online_cpus;
	cpu_set_t cpuset;
	pthread_t thread;
	jintArray result;

	// Number of online processors - same as
	// Runtime.getRuntime().availableProcessors()
	online_cpus = sysconf(_SC_NPROCESSORS_ONLN);

	result = (*env)->NewIntArray(env, online_cpus);
	if (result == NULL) {
		(*env)->ThrowNew(env, (*env)->FindClass(env,
				"java/lang/OutOfMemoryError"), "Out of memory!");
	}

	thread = pthread_self();

	/* Check the actual affinity mask assigned to the thread */
	s = pthread_getaffinity_np(thread, sizeof(cpu_set_t), &cpuset);
	if (s != 0) {
		(*env)->ThrowNew(env, (*env)->FindClass(env,
				"ch/ethz/hwloc/GetAffinityException"), "Couldn't get affinity!");
	}

	jboolean tmp[online_cpus];
	for (i = 0; i < online_cpus; i++) {
		if (CPU_ISSET(i, &cpuset)) {
			tmp[i] = true;
		} else {
			tmp[i] = false;
		}
	}

	// Move from the temporary array to the java array
	(*env)->SetBooleanArrayRegion(env, result, 0, online_cpus, tmp);
	return result;
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
