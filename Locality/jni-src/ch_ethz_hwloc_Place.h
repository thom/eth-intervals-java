/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ch_ethz_hwloc_Place */

#ifndef _Included_ch_ethz_hwloc_Place
#define _Included_ch_ethz_hwloc_Place
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ch_ethz_hwloc_Place
 * Method:    setAffinity
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_ch_ethz_hwloc_Place_setAffinity
  (JNIEnv *, jobject, jintArray);

/*
 * Class:     ch_ethz_hwloc_Place
 * Method:    getAffinity
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_ch_ethz_hwloc_Place_getAffinity
  (JNIEnv *, jobject);

/*
 * Class:     ch_ethz_hwloc_Place
 * Method:    getThreadId
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_ch_ethz_hwloc_Place_getThreadId
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
