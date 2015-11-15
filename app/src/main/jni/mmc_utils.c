#include "com_silicongo_george_emmc_utils_MmcUtils.h"
#include <android/log.h>
#include <jni.h>
#include "mmc.h"
#include "misc.h"
#include "mmc_cmds.h"

#define  LOG_TAG    "native-dev"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

/*
 * Class:     com_silicongo_george_emmc_utils_MmcUtils
 * Method:    getExtcsd
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_silicongo_george_emmc_1utils_MmcUtils_getExtcsd
        (JNIEnv *env, jclass jclass1){
    unsigned char extcsd[EXT_CSD_SIZE];
    int retval;
    char *pro_argv[2];

    emmc_dev_path[0] = 0;
    retval = dump_directory_file(dir_etc_name);
    LOGI("dump directory file: %d", retval);
    if(retval == 0x0){
        LOGI("file: %s", emmc_dev_path);
        pro_argv[0] = "main.o";
        pro_argv[1] = emmc_dev_path;
        retval = do_read_extcsd(2, pro_argv);
        LOGI("read extcsd status: %d", retval);
    }

    jbyteArray jextcsd = (*env)->NewByteArray(env, EXT_CSD_SIZE);
    (*env)->SetByteArrayRegion(env, jextcsd, 0, EXT_CSD_SIZE, extcsd);

    return jextcsd;
}

/*
 * Class:     com_silicongo_george_emmc_utils_MmcUtils
 * Method:    getCsd
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_silicongo_george_emmc_1utils_MmcUtils_getCsd
        (JNIEnv *env, jclass jclass1){
    unsigned char csd[CSD_SIZE];
    int i;

    for(i=0; i<CSD_SIZE; i++){
        csd[i] = i;
    }

    jbyteArray jcsd = (*env)->NewByteArray(env, EXT_CSD_SIZE);
    (*env)->SetByteArrayRegion(env, jcsd, 0, CSD_SIZE, csd);

    return jcsd;
}

/*
 * Class:     com_silicongo_george_emmc_utils_MmcUtils
 * Method:    CMD6
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_silicongo_george_emmc_1utils_MmcUtils_CMD6
        (JNIEnv *env, jclass jclass1, jint jint1){
    jboolean retval = 0x0;

    return retval;
}