package com.silicongo.george.emmc_utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    public String emmcDevPath;

    /* textview to display emmc information */
    private TextView twEmmcDevPath;
    private TextView twEmmcVersion;
    private TextView twEmmcSpeed;

    /* Button Control list */
    private Button btGetFeature;
    private Button btGetWriteProtectStatus;
    private Button btDoSanitize;
    private Button btDoBKOPS;
    private Button btClearContent;

    /* cmd line output */
    private TextView twCmdLineOutput;

    private MmcUtils mmcUtils;
    private int[] extcsd;

    private InitEmmcInfo initEmmcInfo;

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

        /* Init the display ctrl */
        twEmmcDevPath = (TextView) findViewById(R.id.EmmcDevPathInfo);
        twEmmcVersion = (TextView) findViewById(R.id.EmmcVersionInfo);
        twEmmcSpeed = (TextView) findViewById(R.id.EmmcSpeedInfo);

        btGetFeature = (Button) findViewById(R.id.emmcGetFeature);
        btGetWriteProtectStatus = (Button) findViewById(R.id.emmcGetWriteProtectStatus);
        btDoSanitize = (Button) findViewById(R.id.emmcDoSanitize);
        btDoBKOPS = (Button) findViewById(R.id.emmcDoBKOPS);
        btClearContent = (Button) findViewById(R.id.clearContent);

        btGetFeature.setEnabled(false);
        btGetWriteProtectStatus.setEnabled(false);
        btDoSanitize.setEnabled(false);
        btDoBKOPS.setEnabled(false);
        btClearContent.setEnabled(false);

        btGetFeature.setOnClickListener(this);
        btGetWriteProtectStatus.setOnClickListener(this);
        btDoSanitize.setOnClickListener(this);
        btDoBKOPS.setOnClickListener(this);
        btClearContent.setOnClickListener(this);

        /* Cmdline output textview */
        twCmdLineOutput = (TextView) findViewById(R.id.cmdLineOutput);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initEmmcInfo = new InitEmmcInfo();
        initEmmcInfo.execute();
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

    public void onClick(View v) {
        String[] output;
        switch (v.getId()) {
            case R.id.emmcGetFeature:
                twCmdLineOutput.append("\nGet Emmc Feature:\n");
                mmcUtils.getEmmcFeature();
                break;
            case R.id.emmcGetWriteProtectStatus:
                twCmdLineOutput.append("\nGet Emmc Write Protect Status:\n");
                mmcUtils.getWriteProtectStatus();
                break;
            case R.id.emmcDoSanitize:
                twCmdLineOutput.append("\nDo Sanitize:\n");
                mmcUtils.doSanitize();
                break;
            case R.id.emmcDoBKOPS:
                twCmdLineOutput.append("\nDo BKOPS:\n");
                mmcUtils.doBKOPS();
                break;
            case R.id.clearContent:
                twCmdLineOutput.setText("");
                break;
            default:
                break;
        }
    }

    /* AsyncTask to update the screen information */
    private class InitEmmcInfo extends AsyncTask<String, Integer, Void> {
        private static final String TAG = "InitEmmcInfo";

        public InitEmmcInfo() {
            mmcUtils = new MmcUtils(twCmdLineOutput);
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(String... urls) {

            MmcUtils.checkEmmcExecuteFile(getApplicationContext());

            /* Init the global var */
            if (emmcDevPath == null) {
                emmcDevPath = MmcUtils.getEmmcPath();
            }
            if (extcsd == null) {
                extcsd = MmcUtils.getEmmcExtCsd();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (emmcDevPath != null) {
                twEmmcDevPath.setText(emmcDevPath);
            } else {
                twEmmcDevPath.setText(R.string.no_emmc_device);
            }

            if (extcsd != null) {
                int version = extcsd[192];
                if (version > emmcVersion.length) {
                    version = emmcVersion.length;
                }
                twEmmcVersion.setText(emmcVersion[version]);

                int speed = extcsd[185] & 0x0f;
                if (speed > emmcSpeed.length) {
                    speed = emmcSpeed.length;
                }
                int busMode = extcsd[183] & 0x0f;
                if (busMode > emmcBusMode.length) {
                    busMode = emmcBusMode.length;
                }
                twEmmcSpeed.setText(emmcSpeed[speed] + " / " + emmcBusMode[busMode]);
            }

            btGetFeature.setEnabled(true);
            btGetWriteProtectStatus.setEnabled(true);
            btDoSanitize.setEnabled(true);
            btDoBKOPS.setEnabled(true);
            btClearContent.setEnabled(true);
        }
    }
}
