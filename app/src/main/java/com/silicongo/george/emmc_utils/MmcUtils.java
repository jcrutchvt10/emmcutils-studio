package com.silicongo.george.emmc_utils;

/**
 * Created by suxch on 2015/11/15.
 */
public class MmcUtils {
    static {
        System.loadLibrary("MmcUtils");
    }
    public static native byte[] getExtcsd();
    public static native byte[] getCsd();
    public static native boolean CMD6(int argument);
}
