//
// Created by Administrator on 2015/12/1.
//

#include "FileOperation.h"
#include "stdlib.h"
#include "time.h"
#include <stdio.h>

#include <fcntl.h>

char result_string[128];
char filename[256];
bool support_direct = false;
char file_buffer[128 * 1024];

/*
 * Class:     com_silicongo_george_emmc_utils_FileOperation
 * Method:    is_support_direct_io
 * Signature: ()Z
 */
JNIEXPORT jstring JNICALL Java_com_silicongo_george_emmc_1utils_FileOperation_is_1support_1direct_1io
        (JNIEnv *env, jclass jclass1, jstring path) {

    int handle;
    const char *filePath = (env)->GetStringUTFChars(path, false);

    handle = open(filePath, O_WRONLY | O_CREAT);
    if (handle == -1) {
        sprintf(result_string, " Cannot open %s for writing\n", filePath);
    }
    else {
        close(handle);
        remove(filePath);
        handle = open(filePath, O_WRONLY | O_CREAT | O_DIRECT);
        if (handle == -1) {
            sprintf(result_string, " No O_DIRECT");
        }
        else {
            close(handle);
            remove(filePath);
            sprintf(result_string, " OK");
            support_direct = true;
        }
    }
    return (env)->NewStringUTF(result_string);
}

/*
 * Class:     com_silicongo_george_emmc_utils_FileOperation
 * Method:    rw_file
 * Signature: (Ljava/lang/String;III)Z
 */
JNIEXPORT jboolean JNICALL Java_com_silicongo_george_emmc_1utils_FileOperation_rw_1file
        (JNIEnv *env, jclass jclass1, jstring path, jint size, jint times, jint pattern) {

    int handle;
    const char *filePath = (env)->GetStringUTFChars(path, false);
    int test_times = times;
    int test_size = size;
    int test_pattern = pattern;
    int i, j;
    int write_length;
    jboolean status = JNI_TRUE;

    if(pattern == 0x01){
        srand((unsigned int)time(NULL));
        for(j=0; j<sizeof(file_buffer); j++){
            file_buffer[j] = rand();
        }
    }else{
        memset(file_buffer, pattern, sizeof(file_buffer));
    }

    for (i = 0; i < test_times; i++) {
        sprintf(filename, "%s/%s%d", filePath, "test", i);
        handle = open(filename, O_WRONLY | O_CREAT | O_DIRECT);
        if (handle == -1) {
            status = JNI_FALSE;
            break;
        }

        for(j=0; j<size; j+=sizeof(file_buffer)){
            write_length = ((j + sizeof(file_buffer)) > j)?j:sizeof(file_buffer);
            write(handle, file_buffer, write_length);
        }

        close(handle);
    }

    for(; i>=0; i--){
        sprintf(filename, "%s/%s%d", filePath, "test", i);
        remove(filename);
    }

    return status;
}
