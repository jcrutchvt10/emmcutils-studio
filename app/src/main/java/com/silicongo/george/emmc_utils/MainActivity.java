package com.silicongo.george.emmc_utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public String emmcDevPath;

    /* textview to display emmc information */
    private TextView twEmmcDevPath;
    private TextView twEmmcVersion;
    private TextView twEmmcSpeed;

    private int[] extcsd;

    /* static text */
    private static final String[] emmcSpeed = {"Backwards compatibility interface timing",
            "High Speed(52MHz)", "HS200", "HS400", "Unknown"};
    private static final String[] emmcBusMode = {"1 bit data bus", "4 bit data bus",
            "8 bit data bus", "Reserved", "4 bit data bus(dual data rate)",
            "8 bit data bus(dual data rate)", "Reserved"};
    private static final String[] emmcVersion = {"MMC v4.0", "MMC v4.1", "MMC v4.2", "MMC v4.3",
            "Obsolete", "MMC v4.41", "MMC v4.5 or v4.51", "MMC v5.0 or v5.01", "MMC v5.1",
            "Unknown"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MmcUtils.checkEmmcExecuteFile(this);

        /* Init the global var */
        emmcDevPath = MmcUtils.getEmmcPath();

        /* Init the display ctrl */
        twEmmcDevPath = (TextView)findViewById(R.id.EmmcDevPathInfo);
        twEmmcVersion = (TextView)findViewById(R.id.EmmcVersionInfo);
        twEmmcSpeed = (TextView)findViewById(R.id.EmmcSpeedInfo);

        if(emmcDevPath != null){
            twEmmcDevPath.setText(emmcDevPath);
        }else{
            twEmmcDevPath.setText(R.string.no_emmc_device);
        }

        extcsd = MmcUtils.getEmmcExtCsd();

        if(extcsd != null) {
            int version = extcsd[192];
            if (version > emmcVersion.length) {
                version = emmcVersion.length;
            }
            twEmmcVersion.setText(emmcVersion[version]);

            int speed = extcsd[185] & 0x0f;
            if(speed > emmcSpeed.length){
                speed = emmcSpeed.length;
            }
            int busMode = extcsd[183] & 0x0f;
            if(busMode > emmcBusMode.length){
                busMode = emmcBusMode.length;
            }
            twEmmcSpeed.setText(emmcSpeed[speed] + " / " +emmcBusMode[busMode]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
