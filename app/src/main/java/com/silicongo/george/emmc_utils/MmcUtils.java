package com.silicongo.george.emmc_utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by suxch on 2015/11/15.
 */
public class MmcUtils {
    private static final String TAG = "MmcUtils";
    private static String strEmmcBlockPath;

    private static final String emmcUtilsExecuteBinary = "/system/bin/mmc";

    static {
        System.loadLibrary("MmcUtils");
    }

    public static native String getMMCBlockPath();

    public static boolean checkEmmcExecuteFile(Context context) {
        boolean status = false;
        /* Check for the binary file is exist or not */
        File executeFile = new File(emmcUtilsExecuteBinary);
        if(executeFile.exists() == true){
            status = true;
        }else{
            /* try to copy the file to the /system/bin directory */
            String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bin/";
            File file = new File(filename);
            if(file.exists() == false) {
                file.mkdirs();
            }
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getResources().openRawResource(R.raw.mmc);
                fos = new FileOutputStream(filename + "mmc");
                byte[] buffer = new byte[81920];
                int count = 0;

                while ((count = is.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } finally {

            }
            file = new File(filename + "mmc");
            if(file.exists()){
                /* mount system as rw */
                String[] mountSystem = {"su", "-c", "mount -o rw,remount /system"};
                String[] copyFile = {"su", "-c", "cp " + filename + "mmc " + emmcUtilsExecuteBinary};
                String[] changeMode = {"su", "-c", "chmod 0774 " + emmcUtilsExecuteBinary};
                String[] changeOwner = {"su", "-c", "chown root:shell " + emmcUtilsExecuteBinary};

                execShell(mountSystem);
                execShell(copyFile);
                execShell(changeMode);
                execShell(changeOwner);

                if(executeFile.exists() == true){
                    status = true;
                }
            }
        }
        return status;
    }

    public static String getEmmcPath(){
        strEmmcBlockPath = getMMCBlockPath();
        if((strEmmcBlockPath != null) && (strEmmcBlockPath.compareTo("") == 0x0)){
            strEmmcBlockPath = null;
        }

        return strEmmcBlockPath;
    }

    public static String[] getEmmcFeature(){
        String[] shellCmd = new String[]{"su", "-c", emmcUtilsExecuteBinary +
                " extcsd read "+ strEmmcBlockPath};
        String[] cmdOutput;

        cmdOutput = execShell(shellCmd);

        return cmdOutput;
    }

    public static String[] getWriteProtectStatus(){
        String[] shellCmd = new String[]{"su", "-c", emmcUtilsExecuteBinary +
                " writeprotect get "+ strEmmcBlockPath};
        String[] cmdOutput;

        cmdOutput = execShell(shellCmd);

        return cmdOutput;
    }

    public static String[] doSanitize(){
        String[] shellCmd = new String[]{"su", "-c", emmcUtilsExecuteBinary +
                " sanitize "+ strEmmcBlockPath};
        String[] cmdOutput;

        cmdOutput = execShell(shellCmd);

        return cmdOutput;
    }

    public static String[] doBKOPS(){
        String[] shellCmd = new String[]{"su", "-c", emmcUtilsExecuteBinary +
                " bkops enable "+ strEmmcBlockPath};
        String[] cmdOutput;

        cmdOutput = execShell(shellCmd);

        return cmdOutput;
    }

    public static int[] getEmmcExtCsd()
    {
        int[] extcsd = new int[512];
        String[] shellCmd = new String[]{"su", "-c", emmcUtilsExecuteBinary +
                " extcsd dump "+ strEmmcBlockPath};
        String[] cmdOutput;

        if(strEmmcBlockPath == null){
            return null;
        }

        /* Check for the binary file is exist or not */
        File executeFile = new File(emmcUtilsExecuteBinary);
        if(executeFile.exists() == true){
            cmdOutput = execShell(shellCmd);

            /* decode command output */
            int extcsd_offset = 0;
            int char_dat_offset;
            for(int i=0; cmdOutput[i]!=null; i++){
                int current_offset_dec = 0xffff, current_offset_hex = 0xffff;
                for(int j=0; j<cmdOutput[i].length(); j++){
                    char_dat_offset = cmdOutput[i].charAt(j);
                    /* first: decode the dec position */
                    if(char_dat_offset == ' '){
                        continue;
                    }
                    if(current_offset_dec == 0xffff){
                        while((char_dat_offset >= '0') &&(char_dat_offset <= '9')){
                            if(current_offset_dec == 0xffff){
                                current_offset_dec = 0x0;
                            }
                            current_offset_dec = current_offset_dec*10 + (char_dat_offset - '0');
                            if(++j<cmdOutput[i].length()) {
                                char_dat_offset = cmdOutput[i].charAt(j);
                            }else{
                                break;
                            }
                        }
                        if((current_offset_dec == 0xffff) || (char_dat_offset != ':')){
                            break;
                        }
                        continue;
                    }
                    /* second: decode the oct position */
                    if(current_offset_hex == 0xffff){
                        while(((char_dat_offset >= '0') && (char_dat_offset <= '9')) ||
                                ((char_dat_offset >= 'a') && (char_dat_offset <= 'f'))){
                            if(current_offset_hex == 0xffff){
                                current_offset_hex = 0x0;
                            }
                            if((char_dat_offset >= '0') && (char_dat_offset <= '9')) {
                                char_dat_offset = char_dat_offset - '0';
                            }else{
                                char_dat_offset = char_dat_offset - 'a' + 10;
                            }
                            current_offset_hex = current_offset_hex * 0x10 + char_dat_offset;
                            if(++j<cmdOutput[i].length()) {
                                char_dat_offset = cmdOutput[i].charAt(j);
                            }else{
                                break;
                            }
                        }
                        if((current_offset_dec == 0xffff) || (char_dat_offset != ':')){
                            break;
                        }

                        /* Check data is right */
                        if((current_offset_hex != current_offset_dec) ||
                                (current_offset_hex != extcsd_offset)){
                            Log.i(TAG, "offset value not match");
                        }
                        continue;
                    }

                    if(extcsd_offset > 512){
                        Log.e(TAG, "Extcsd offset exceed max number" + extcsd_offset + " > 512");
                        break;
                    }

                    extcsd[extcsd_offset] = 0x0;

                    while(((char_dat_offset >= '0') && (char_dat_offset <= '9')) ||
                            ((char_dat_offset >= 'a') && (char_dat_offset <= 'f'))){
                        if((char_dat_offset >= '0') && (char_dat_offset <= '9')) {
                            char_dat_offset = char_dat_offset - '0';
                        }else{
                            char_dat_offset = char_dat_offset - 'a' + 10;
                        }
                        extcsd[extcsd_offset] = extcsd[extcsd_offset] * 0x10 + char_dat_offset;
                        if(++j<cmdOutput[i].length()) {
                            char_dat_offset = cmdOutput[i].charAt(j);
                        }else{
                            break;
                        }
                    }
                    extcsd_offset++;
                }
            }
        }

        return extcsd;
    }

    public static String[] execShell(String cmd[]) {
        String cmdInfo[] = new String[500];
        int valid_cmd_line = 0x0;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                if(valid_cmd_line < cmdInfo.length) {
                    cmdInfo[valid_cmd_line] = line;
                    valid_cmd_line++;
                }else{
                    Log.e(TAG, "output buffer not enough to hold string");
                }
                Log.i("execShell", line);
            }
        } catch (IOException t) {
            t.printStackTrace();
            cmdInfo[valid_cmd_line] = t.getMessage();
            valid_cmd_line++;
        }

        cmdInfo[valid_cmd_line] = null;
        return cmdInfo;
    }
}
