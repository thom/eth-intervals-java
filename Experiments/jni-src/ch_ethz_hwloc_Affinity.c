#define _GNU_SOURCE
#include <jni.h>
#include <syscall.h>
#include <sched.h>
#include <pthread.h>
#include <stdbool.h>
#include <unistd.h>
#include "ch_ethz_hwloc_Affinity.h"

/* Implementation for class ch_ethz_hwloc_Place */

long int gettid();
void set_affinity(JNIEnv *env, const cpu_set_t *cpuset);

/*
 * Class:     ch_ethz_hwloc_Affinity
 * Method:    set
 * Signature: (I)V
 */
JNIEXPORT void JNICALL
Java_ch_ethz_hwloc_Affinity_set__I(JNIEnv *env, jclass class,
		jint physical_unit) {
	cpu_set_t cpuset;

	CPU_ZERO(&cpuset);
	CPU_SET(physical_unit, &cpuset);

	set_affinity(env, &cpuset);
}

/*
 * Class:     ch_ethz_hwloc_Affinity
 * Method:    set
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL
Java_ch_ethz_hwloc_Affinity_set___3I(JNIEnv *env, jclass class,
		jintArray physical_units) {
	int i;
	cpu_set_t cpuset;

	CPU_ZERO(&cpuset);

	jsize len = (*env)->GetArrayLength(env, physical_units);
	jint *units = (*env)->GetIntArrayElements(env, physical_units, 0);
	for (i = 0; i < len; i++) {
		CPU_SET(units[i], &cpuset);
	}

	set_affinity(env, &cpuset);
}

/*
 * Class:     ch_ethz_hwloc_Affinity
 * Method:    get
 * Signature: ()[Z
 */
JNIEXPORT jbooleanArray JNICALL
Java_ch_ethz_hwloc_Affinity_get(JNIEnv *env, jclass class) {
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
 * Class:     ch_ethz_hwloc_Affinity
 * Method:    getThreadId
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_ch_ethz_hwloc_Affinity_getThreadId(JNIEnv *env, jobject obj) {
	return gettid();
}

long int gettid() {
	return syscall(SYS_gettid);
}

void set_affinity(JNIEnv *env, const cpu_set_t *cpuset) {
	int s;
	pthread_t thread = pthread_self();

	/* Set affinity mask for thread */
	s = pthread_setaffinity_np(thread, sizeof(cpu_set_t), cpuset);
	if (s != 0) {
		(*env)->ThrowNew(env, (*env)->FindClass(env,
				"ch/ethz/hwloc/SetAffinityException"), "Couldn't set affinity!");
	}
}
