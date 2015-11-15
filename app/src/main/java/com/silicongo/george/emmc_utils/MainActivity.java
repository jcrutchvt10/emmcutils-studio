package com.silicongo.george.emmc_utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(execShell(new String[]{"su", "-c", "chmod 0777 /dev/block/mmcblk0"}) == true) {
            byte[] extcsd = MmcUtils.getExtcsd();
            execShell(new String[]{"su", "-c", "chmod 0760 /dev/block/mmcblk0"});
        }
    }

    public boolean execShell(String cmd[]) {
        boolean status = true;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                Log.i("exeShell", line);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            status = false;
        }
        return status;
    }
}
