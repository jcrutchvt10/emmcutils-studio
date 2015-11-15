/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License v2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 021110-1307, USA.
 */

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/param.h>
#include <sys/types.h>
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <libgen.h>
#include <limits.h>
#include <ctype.h>

#include "mmc.h"
#include "mmc_cmds.h"

int read_extcsd(int fd, __u8 *ext_csd)
{
    int ret = 0;
    struct mmc_ioc_cmd idata;
    memset(&idata, 0, sizeof(idata));
    memset(ext_csd, 0, sizeof(__u8) * EXT_CSD_SIZE);
    idata.write_flag = 0;
    idata.opcode = MMC_SEND_EXT_CSD;
    idata.arg = 0;
    idata.flags = MMC_RSP_SPI_R1 | MMC_RSP_R1 | MMC_CMD_ADTC;
    idata.blksz = EXT_CSD_SIZE;
    idata.blocks = 1;
    mmc_ioc_cmd_set_data(idata, ext_csd);

    ret = ioctl(fd, MMC_IOC_CMD, &idata);
    if (ret)
        perror("ioctl SEND_EXT_CSD");

    return ret;
}

int write_extcsd_value(int fd, __u8 index, __u8 value)
{
    int ret = 0;
    struct mmc_ioc_cmd idata;

    memset(&idata, 0, sizeof(idata));
    idata.write_flag = 1;
    idata.opcode = MMC_SWITCH;
    idata.arg = (MMC_SWITCH_MODE_WRITE_BYTE << 24) |
                (index << 16) |
                (value << 8) |
                EXT_CSD_CMD_SET_NORMAL;
    idata.flags = MMC_RSP_SPI_R1B | MMC_RSP_R1B | MMC_CMD_AC;

    ret = ioctl(fd, MMC_IOC_CMD, &idata);
    if (ret)
        perror("ioctl Write EXT CSD");

    return ret;
}

int do_read_extcsd(int nargs, char **argv) {
    __u8 ext_csd[EXT_CSD_SIZE];
    int fd, ret;
    char *device;
    int retval = 0x0;

    device = argv[1];

    fd = open(device, O_RDWR);
    if (fd < 0) {
        retval = -1;
        return retval;
    }

    ret = read_extcsd(fd, ext_csd);
    if (ret) {
        retval = -2;
        goto ReadExtCsdExit;
    }

ReadExtCsdExit:
    close(fd);
    return retval;
}
