/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_silicongo_george_emmc_utils_MmcUtils */

#ifndef _Included_com_silicongo_george_emmc_utils_MmcUtils
#define _Included_com_silicongo_george_emmc_utils_MmcUtils
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_silicongo_george_emmc_utils_MmcUtils
 * Method:    getExtcsd
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_silicongo_george_emmc_1utils_MmcUtils_getExtcsd
  (JNIEnv *, jclass);

/*
 * Class:     com_silicongo_george_emmc_utils_MmcUtils
 * Method:    getCsd
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_silicongo_george_emmc_1utils_MmcUtils_getCsd
  (JNIEnv *, jclass);

/*
 * Class:     com_silicongo_george_emmc_utils_MmcUtils
 * Method:    CMD6
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_silicongo_george_emmc_1utils_MmcUtils_CMD6
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
