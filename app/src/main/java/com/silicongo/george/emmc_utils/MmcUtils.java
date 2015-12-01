package com.silicongo.george.emmc_utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    private static final String emmcUtilsExecuteBinaryDirList[] = {"/system/bin/", "/sbin/"};
    private static final String emmcUtilsExecuteBinaryDev[] = {"/system", "/"};
    private String emmcUtilsExecuteBinaryDir;
    private static final String emmcUtilsExecuteBinaryName = "mmc";
    private ExecuteBackgroundCmd executeBackgroundCmd;
    private TextView tvOutput;

    static {
        System.loadLibrary("MmcUtils");
    }

    public MmcUtils(TextView tv) {
        tvOutput = tv;
    }

    public static native String getMMCBlockPath();

    public boolean checkEmmcExecuteFile(Context context) {
        boolean status = false;
        /* Check for the binary file is exist or not */
        for (String path : emmcUtilsExecuteBinaryDirList) {
            File executeFile = new File(path + emmcUtilsExecuteBinaryName);
            if (executeFile.exists() == true) {
                emmcUtilsExecuteBinaryDir = path;
                status = true;
                break;
            }
        }

        if (status == false) {
            /* Check if we need to create the mmc file */
            String emmcBinSDDirName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bin/";
            File emmcBinSDDir = new File(emmcBinSDDirName);
            if (emmcBinSDDir.exists() == false) {
                emmcBinSDDir.mkdirs();
            }
            File emmcBinSD = new File(emmcBinSDDirName + emmcUtilsExecuteBinaryName);
            if(emmcBinSD.exists() == false) {
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = context.getResources().openRawResource(R.raw.mmc);
                    fos = new FileOutputStream(emmcBinSDDirName + emmcUtilsExecuteBinaryName);
                    byte[] buffer = new byte[81920];
                    int count = 0;

                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    fos.close();
                    is.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            }

            int count = 0;
            for (String path : emmcUtilsExecuteBinaryDirList) {
                if (new File(path).exists()) {
                    emmcUtilsExecuteBinaryDir = path;
                    /* try to copy the file to the /system/bin directory */
                    if (emmcBinSD.exists()) {
                        /* remount the parent directory */
                        String[] mountCmdInfo = execShell(new String[]{"mount"});
                        String mountDevPathInfo = null;
                        for(String str:mountCmdInfo){
                            if(str.contains(emmcUtilsExecuteBinaryDev[count])){
                                int end, match = 0;
                                for(end=0; end<str.length(); end++){
                                    if(str.charAt(end) == ' '){
                                        if(++match == 2){
                                            break;
                                        }
                                    }
                                }
                                if(match == 2) {
                                    mountDevPathInfo = str.substring(0, end);
                                    break;
                                }
                            }
                        }

                        if(mountDevPathInfo != null){
                            String remountCmd[] = {"su", "-c", "mount -o rw,remount " + mountDevPathInfo};
                            execShell(remountCmd);
                        }

                        /* mount system as rw */
                        String[] copyFile = {"su", "-c", "cp " + emmcBinSDDirName + emmcUtilsExecuteBinaryName
                                + " " + emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName};
                        String[] changeMode = {"su", "-c", "chmod 0774 " +
                                emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName};
                        String[] changeOwner = {"su", "-c", "chown root:shell " +
                                emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName};

                        execShell(copyFile);
                        execShell(changeMode);
                        execShell(changeOwner);

                        if(new File(emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName).exists()){
                            status = true;
                            break;
                        }
                    }
                }
                count++;
            }
        }
        return status;
    }

    public static String getEmmcPath() {
        strEmmcBlockPath = getMMCBlockPath();
        if ((strEmmcBlockPath != null) && (strEmmcBlockPath.compareTo("") == 0x0)) {
            strEmmcBlockPath = null;
        }

        return strEmmcBlockPath;
    }

    public void getEmmcFeature() {
        String shellCmd = emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName + " extcsd read " + strEmmcBlockPath;
        executeBackgroundCmd = new ExecuteBackgroundCmd(tvOutput);
        executeBackgroundCmd.execute(shellCmd);
    }

    public void getWriteProtectStatus() {
        String shellCmd = emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName + " writeprotect get " + strEmmcBlockPath;
        executeBackgroundCmd = new ExecuteBackgroundCmd(tvOutput);
        executeBackgroundCmd.execute(shellCmd);
    }

    public void doSanitize() {
        String shellCmd = emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName + " sanitize " + strEmmcBlockPath;
        executeBackgroundCmd = new ExecuteBackgroundCmd(tvOutput);
        executeBackgroundCmd.execute(shellCmd);
    }

    public void doBKOPS() {
        String shellCmd = emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName + " bkops enable " + strEmmcBlockPath;
        executeBackgroundCmd = new ExecuteBackgroundCmd(tvOutput);
        executeBackgroundCmd.execute(shellCmd);
    }

    public int[] getEmmcExtCsd() {
        int[] extcsd = new int[512];
        String[] shellCmd = new String[]{"su", "-c", emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName +
                " extcsd dump " + strEmmcBlockPath};
        String[] cmdOutput;

        if (strEmmcBlockPath == null) {
            return null;
        }

        /* Check for the binary file is exist or not */
        File executeFile = new File(emmcUtilsExecuteBinaryDir + emmcUtilsExecuteBinaryName);
        if (executeFile.exists() == true) {
            cmdOutput = execShell(shellCmd);

            /* decode command output */
            int extcsd_offset = 0;
            int char_dat_offset;
            for (int i = 0; cmdOutput[i] != null; i++) {
                int current_offset_dec = 0xffff, current_offset_hex = 0xffff;
                for (int j = 0; j < cmdOutput[i].length(); j++) {
                    char_dat_offset = cmdOutput[i].charAt(j);
                    /* first: decode the dec position */
                    if (char_dat_offset == ' ') {
                        continue;
                    }
                    if (current_offset_dec == 0xffff) {
                        while ((char_dat_offset >= '0') && (char_dat_offset <= '9')) {
                            if (current_offset_dec == 0xffff) {
                                current_offset_dec = 0x0;
                            }
                            current_offset_dec = current_offset_dec * 10 + (char_dat_offset - '0');
                            if (++j < cmdOutput[i].length()) {
                                char_dat_offset = cmdOutput[i].charAt(j);
                            } else {
                                break;
                            }
                        }
                        if ((current_offset_dec == 0xffff) || (char_dat_offset != ':')) {
                            break;
                        }
                        continue;
                    }
                    /* second: decode the oct position */
                    if (current_offset_hex == 0xffff) {
                        while (((char_dat_offset >= '0') && (char_dat_offset <= '9')) ||
                                ((char_dat_offset >= 'a') && (char_dat_offset <= 'f'))) {
                            if (current_offset_hex == 0xffff) {
                                current_offset_hex = 0x0;
                            }
                            if ((char_dat_offset >= '0') && (char_dat_offset <= '9')) {
                                char_dat_offset = char_dat_offset - '0';
                            } else {
                                char_dat_offset = char_dat_offset - 'a' + 10;
                            }
                            current_offset_hex = current_offset_hex * 0x10 + char_dat_offset;
                            if (++j < cmdOutput[i].length()) {
                                char_dat_offset = cmdOutput[i].charAt(j);
                            } else {
                                break;
                            }
                        }
                        if ((current_offset_dec == 0xffff) || (char_dat_offset != ':')) {
                            break;
                        }

                        /* Check data is right */
                        if ((current_offset_hex != current_offset_dec) ||
                                (current_offset_hex != extcsd_offset)) {
                            Log.i(TAG, "offset value not match");
                        }
                        continue;
                    }

                    if (extcsd_offset > 512) {
                        Log.e(TAG, "Extcsd offset exceed max number" + extcsd_offset + " > 512");
                        break;
                    }

                    extcsd[extcsd_offset] = 0x0;

                    while (((char_dat_offset >= '0') && (char_dat_offset <= '9')) ||
                            ((char_dat_offset >= 'a') && (char_dat_offset <= 'f'))) {
                        if ((char_dat_offset >= '0') && (char_dat_offset <= '9')) {
                            char_dat_offset = char_dat_offset - '0';
                        } else {
                            char_dat_offset = char_dat_offset - 'a' + 10;
                        }
                        extcsd[extcsd_offset] = extcsd[extcsd_offset] * 0x10 + char_dat_offset;
                        if (++j < cmdOutput[i].length()) {
                            char_dat_offset = cmdOutput[i].charAt(j);
                        } else {
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
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                if (valid_cmd_line < cmdInfo.length) {
                    cmdInfo[valid_cmd_line] = line;
                    valid_cmd_line++;
                } else {
                    Log.e(TAG, "output buffer not enough to hold string");
                }
                Log.i("execShell", line);
            }
        } catch (IOException t) {
            t.printStackTrace();
            cmdInfo[valid_cmd_line] = t.getMessage();
            valid_cmd_line++;
        } finally {
            if (p != null) p.destroy();
        }

        cmdInfo[valid_cmd_line] = null;
        return cmdInfo;
    }

    private class ExecuteBackgroundCmd extends AsyncTask<String, String, Void> {
        private static final String TAG = "executeBackgroundCmd";
        private TextView tvOutput;

        public ExecuteBackgroundCmd(TextView tv) {
            tvOutput = tv;
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(String... urls) {
            String[] cmd = new String[]{"su", "-c", urls[0]};
            Process p = null;
            try {
                p = Runtime.getRuntime().exec(cmd);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                p.getInputStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    publishProgress(line);
                    Log.i("execShell", line);
                }
            } catch (IOException t) {
                t.printStackTrace();
            } finally {
                if (p != null) p.destroy();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
        }

        protected void onProgressUpdate(String... progress) {
            if (tvOutput != null) {
                tvOutput.append(progress[0] + "\n");
            }
        }
    }
}
