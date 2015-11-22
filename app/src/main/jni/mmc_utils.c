#include <string.h>
#include "com_silicongo_george_emmc_utils_MmcUtils.h"
#include "mmc.h"
#include "misc.h"
#include "jni.h"


/*
 * Class:     com_silicongo_george_emmc_utils_MmcUtils
 * Method:    getMMCBlockPath
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_silicongo_george_emmc_1utils_MmcUtils_getMMCBlockPath
        (JNIEnv *env, jclass jclass1)
{
    int ret;
    emmc_dev_path[0] = 0x0;
    ret = dump_directory_file(dir_etc_name);
    if(ret != 0x0){
        LOGI("Find emmc directory fail");
    }
    jstring jEmmcPath = (*env)->NewStringUTF(env, emmc_dev_path);
    return jEmmcPath;
}
