#include <stdio.h>
#include <dirent.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <sys/param.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <libgen.h>
#include <limits.h>
#include <ctype.h>
#include "mmc.h"

#define FILE_BUF_MAX_LEN	2048

const char *dir_etc_name = "/dev";
char emmc_dev_path[FILE_BUF_MAX_LEN];

int dump_directory_file(const char *path)
{
    int retval = 0;
    DIR *dir_etc;
    struct dirent *dir_entry;
    char *file_buf;
    struct stat file_stat;
    dir_etc = opendir(path);
    if(dir_etc == NULL){
        printf("Open dir %s fail\n", path);
        retval = -1;
        goto GetDirEntryFail;
    }

    file_buf = (char *)malloc(FILE_BUF_MAX_LEN);
    if(file_buf == NULL){
        printf("Alloc Memory Fail\n");
        retval = -2;
        goto GetMemoryFail;
    }

    do{
        dir_entry = readdir(dir_etc);
        if(dir_entry != NULL){
            if((strcmp(dir_entry->d_name, ".") == 0) || (strcmp(dir_entry->d_name, "..") == 0))
            {
                continue;
            }
            if((strcmp(dir_entry->d_name, "vold") == 0))
            {
                continue;
            }
            snprintf(file_buf, FILE_BUF_MAX_LEN, "%s/%s", path, dir_entry->d_name);
            if(lstat(file_buf, &file_stat) == 0){
                if(S_ISDIR(file_stat.st_mode)){
                    /* Recursion call function */
                    printf("Open Directory: %s, mode_t: 0x%x\n", file_buf, file_stat.st_mode);
                    dump_directory_file(file_buf);
                }else{
                    if((major(file_stat.st_rdev) == 179) && (minor(file_stat.st_rdev) == 0x0)){
                        if(emmc_dev_path[0] == 0){
                            strcpy(emmc_dev_path, file_buf);
                        }
                        printf("name: %s, major: %d, minor: %d\n",
                               file_buf, major(file_stat.st_rdev), minor(file_stat.st_rdev));
                    }
                }
            }else{
                printf("Error getting file stat name: %s\n", file_buf);
            }
        }
    }while(dir_entry);

    printf("Exit to parent Directory\n");

    free(file_buf);
    GetMemoryFail:
    closedir(dir_etc);
    GetDirEntryFail:
    return retval;
}
