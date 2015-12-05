package com.silicongo.george.emmc_utils;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmmcSpeedTest.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmmcSpeedTest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmmcSpeedTest extends Fragment implements View.OnClickListener {
    private static final String TAG = "EmmcSpeedTest";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final Map<String, File> testDirMap = ExternalStorage.getAllStorageLocations();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<String> testStorageList;
    private ArrayAdapter<String> testStorageArrayAdapter;

    private int[] testSizeValue = {512, 1024, 4096, 16384, 32768,
            64 * 1024, 128 * 1024, 256 * 1024, 512 * 1024, 1024 * 1024, 2 * 1024 * 1024,
            4 * 1024 * 1024, 6 * 1024 * 1024, 8 * 1024 * 1024, 16 * 1024 * 1024, 32 * 1024 * 1024};
    private String[] testSizeString = {"512 Bytes", "1K", "4K", "16K", "32K",
            "64K", "128K", "256K", "512K", "1M", "2M", "4M", "8M", "16M", "32M"};
    private List<String> testSizeList;
    private ArrayAdapter<String> testSizeArrayAdapter;

    private int[] testTimesValue = {100, 1000, 10000, 100000, -1};
    private String[] testTimesString = {"100", "1000", "10000", "100000", "Infinite"};
    private List<String> testTimesList;
    private ArrayAdapter<String> testTimesArrayAdapter;

    private int[] testPatternValue = {0x01, 0x0, 0xaa, 0x55, 0xff};
    private String[] testPatternString = {"Random", "0x00", "0xaa", "0x55", "0xff"};
    private List<String> testPatternList;
    private ArrayAdapter<String> testPatternArrayAdapter;

    private int[] testOperationValue = {0x00, 0x01};
    private String[] testOperationString = {"Read", "Write"};
    private List<String> testOperationList;
    private ArrayAdapter<String> testOperationArrayAdapter;

    private ExecuteReadWriteFile executeReadWriteFile;

    private static final String testDir = "test";

    /* UI control field */
    /* textview */
    private TextView twTextOutputInfo;

    /* Spinner */
    private Spinner spStorageChoice;
    private Spinner spTestTimes;
    private Spinner spTestSize;
    private Spinner spTestPattern;
    private Spinner spTestOperation;

    /* Button */
    private Button btTestStart;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmmcSpeedTest.
     */
    // TODO: Rename and change types and number of parameters
    public static EmmcSpeedTest newInstance(String param1, String param2) {
        EmmcSpeedTest fragment = new EmmcSpeedTest();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EmmcSpeedTest() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_emmc_speed_test, container, false);

        twTextOutputInfo = (TextView) v.findViewById(R.id.twTextOutputInfo);

        spStorageChoice = (Spinner) v.findViewById(R.id.spStorageChoice);
        spTestTimes = (Spinner) v.findViewById(R.id.spTestTimes);
        spTestSize = (Spinner) v.findViewById(R.id.spTestSize);
        spTestPattern = (Spinner) v.findViewById(R.id.spTestPattern);
        spTestOperation = (Spinner) v.findViewById(R.id.spTestOperation);

        btTestStart = (Button) v.findViewById(R.id.btTestStart);

        testStorageList = new ArrayList<>();
        Collection<File> c = testDirMap.values();
        Iterator it = c.iterator();
        for (; it.hasNext();) {
            testStorageList.add(it.next().toString());
        }
        testStorageArrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, testStorageList);
        spStorageChoice.setAdapter(testStorageArrayAdapter);

        testSizeList = new ArrayList<>();
        for (String str : testSizeString) {
            testSizeList.add(str);
        }
        testSizeArrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, testSizeList);
        spTestSize.setAdapter(testSizeArrayAdapter);

        testTimesList = new ArrayList<>();
        for (String str : testTimesString) {
            testTimesList.add(str);
        }
        testTimesArrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, testTimesList);
        spTestTimes.setAdapter(testTimesArrayAdapter);

        testPatternList = new ArrayList<>();
        for (String str : testPatternString) {
            testPatternList.add(str);
        }
        testPatternArrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, testPatternList);
        spTestPattern.setAdapter(testPatternArrayAdapter);

        testOperationList = new ArrayList<>();
        for (String str : testOperationString) {
            testOperationList.add(str);
        }
        testOperationArrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, testOperationList);
        spTestOperation.setAdapter(testOperationArrayAdapter);

        btTestStart.setOnClickListener(this);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btTestStart:
                if ((executeReadWriteFile == null) || (executeReadWriteFile.getStatus() != AsyncTask.Status.RUNNING)) {
                    executeReadWriteFile = new ExecuteReadWriteFile(twTextOutputInfo);
                    executeReadWriteFile.execute();
                } else {
                    executeReadWriteFile.cancel(true);
                    refreashDisplayCtrl(true);
                }
                break;
            default:
                break;
        }
    }

    private void refreashDisplayCtrl(boolean enable) {
        spStorageChoice.setEnabled(enable);
        spTestTimes.setEnabled(enable);
        spTestSize.setEnabled(enable);
        spTestPattern.setEnabled(enable);
        spTestOperation.setEnabled(enable);

        btTestStart.setText(enable ? "Start" : "Stop");
    }

    private class ExecuteReadWriteFile extends AsyncTask<String, Integer, Void> {
        private static final String TAG = "ExecuteReadWriteFile";
        private TextView tvOutput;
        long time_start, time_end;
        String test_dir;
        int test_times = 100;
        int test_pattern = 0x0;
        int test_size = 512;
        int test_type = 0x0;
        long output_interval;
        long output_length;

        long total_interval;
        long total_length;

        boolean status;

        double speed;
        int unit = 0x0;
        String[] unitStr = {"Bytes", "KB", "MB"};
        String info = new String();

        public ExecuteReadWriteFile(TextView tv) {
            tvOutput = tv;
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(String... urls) {
            int test_count = 0;
            Integer val[] = {0x0, 0x0, 0x0};
            output_interval = 0x0;
            output_length = 0x0;
            total_interval = 0x0;
            total_length = 0x0;

            if(test_times == -1){
                test_times = Integer.MAX_VALUE;
            }

            if (test_type == 0x0) {
                status = FileOperation.write_file(test_dir, test_size, 1, test_pattern);
                if (status == false) {
                    return null;
                }
            }
            int i;
            for (i = 0; i < test_times; i++) {
                if (isCancelled() == true) {
                    break;
                }
                time_start = System.currentTimeMillis();
                if (test_type == 0x0) {
                    status = FileOperation.read_file(test_dir, test_size, 1, test_pattern);
                } else {
                    status = FileOperation.write_file(test_dir, test_size, 1, test_pattern);
                }
                if(status == false){
                    break;
                }
                time_end = System.currentTimeMillis();
                val[0] = new Integer((int) (time_end - time_start));
                val[1] = new Integer(test_size);
                val[2] = new Integer(test_count++);
                publishProgress(val);
            }
            return null;
        }

        protected void onPreExecute() {
            refreashDisplayCtrl(false);
            /* Check for the test dir is exist */
            test_dir = spStorageChoice.getSelectedItem().toString() + "/" + testDir;
            File testFileDir = new File(test_dir);
            if (testFileDir.exists() == false) {
                testFileDir.mkdirs();
            }

            String[] file_list = testFileDir.list();
            for (String str : file_list) {
                File file = new File(test_dir + str);
                if (file.isFile() == true) {
                    file.delete();
                }
            }

            String val = spTestTimes.getSelectedItem().toString();
            int count = 0;
            for (String str : testTimesString) {
                if (val.compareTo(str) == 0) {
                    test_times = testTimesValue[count];
                    break;
                }
                count++;
            }

            val = spTestSize.getSelectedItem().toString();
            count = 0;
            for (String str : testSizeString) {
                if (val.compareTo(str) == 0) {
                    test_size = testSizeValue[count];
                    break;
                }
                count++;
            }

            val = spTestPattern.getSelectedItem().toString();
            count = 0;
            for (String str : testPatternString) {
                if (val.compareTo(str) == 0) {
                    test_pattern = testPatternValue[count];
                    break;
                }
                count++;
            }

            val = spTestOperation.getSelectedItem().toString();
            count = 0;
            for (String str : testOperationString) {
                if (val.compareTo(str) == 0) {
                    test_type = testOperationValue[count];
                    break;
                }
                count++;
            }
        }

        protected void onPostExecute(Void result) {
            if ((status == true)&&(total_interval != 0)) {
                speed = total_length * 1000 / total_interval;
                unit = 0x0;
                while (speed > 1024) {
                    speed /= 1024;
                    unit++;
                    if (unit >= 2) {
                        break;
                    }
                }
                info = String.format("%s %s %s:  %f %s/S", "Average",
                        (test_type == 0) ? "Read" : "Write", "Speed", speed, unitStr[unit]);
                tvOutput.setText(info);
            } else {
                tvOutput.setText("Test Error\n");
            }
            refreashDisplayCtrl(true);
        }

        protected void onProgressUpdate(Integer... progress) {
            output_interval += progress[0];
            output_length += progress[1];

            total_interval += progress[0];
            total_length += progress[1];

            unit = 0x0;
            if ((output_interval > 1000) || (output_length > 1024 * 512)) {
                speed = output_length * 1000 / output_interval;
                while (speed > 1024) {
                    speed /= 1024;
                    unit++;
                    if (unit >= 2) {
                        break;
                    }
                }
                info = String.format("%d:  %f %s/S", progress[2], speed, unitStr[unit]);
                tvOutput.setText(info);
                output_interval = 0x0;
                output_length = 0x0;
            }
        }
    }
}
