#ifndef __MMC_CMDS_H__
#define __MMC_CMDS_H__

#include <asm-generic/int-ll64.h>

int read_extcsd(int fd, __u8 *ext_csd);
int write_extcsd_value(int fd, __u8 index, __u8 value);
int do_read_extcsd(int nargs, char **argv);

#endif
