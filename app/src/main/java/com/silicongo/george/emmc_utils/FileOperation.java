package com.silicongo.george.emmc_utils;

import android.widget.TextView;

/**
 * Created by Administrator on 2015/12/1.
 */
public class FileOperation {
    static {
        System.loadLibrary("MmcUtils");
    }

    public static native String is_support_direct_io(String path);
    public static native boolean write_file(String path, int size, int times, int pattern);
    public static native boolean read_file(String path, int size, int times, int pattern);
}
