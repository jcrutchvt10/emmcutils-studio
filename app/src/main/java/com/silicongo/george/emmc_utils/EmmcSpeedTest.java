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

    private static final Map<String, File> testDir = ExternalStorage.getAllStorageLocations();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<String> testStorageList;
    private ArrayAdapter<String> testStorageArrayAdapter;

    private int[] testSizeValue = {512, 1024, 4096, 16384, 32768,
            64 * 1024, 128 * 1024, 256 * 1024, 512 * 1024, 1024 * 1024};
    private String[] testSizeString = {"512 Bytes", "1K", "4K", "16K", "32K",
            "64K", "128K", "256K", "512K", "1M"};
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

    private static final String TestDir = "test";

    /* UI control field */
    /* textview */
    private TextView twTextOutputInfo;

    /* Spinner */
    private Spinner spStorageChoice;
    private Spinner spTestTimes;
    private Spinner spTestSize;
    private Spinner spTestPattern;

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

        btTestStart = (Button) v.findViewById(R.id.btTestStart);

        testStorageList = new ArrayList<>();
        String externalSDCard = testDir.get(ExternalStorage.EXTERNAL_SD_CARD).toString();
        if (externalSDCard != null) {
            testStorageList.add(externalSDCard);
        }
        String SDCard = testDir.get(ExternalStorage.SD_CARD).toString();
        if (SDCard != null) {
            testStorageList.add(SDCard);
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

        btTestStart.setText(enable ? "Start" : "Stop");
    }

    private class ExecuteReadWriteFile extends AsyncTask<String, String, Void> {
        private static final String TAG = "ExecuteReadWriteFile";
        private TextView tvOutput;
        long time_start, time_end;
        String test_dir;
        int test_times;
        int test_pattern;
        int test_size;
        boolean status;

        public ExecuteReadWriteFile(TextView tv) {
            tvOutput = tv;
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(String... urls) {
            time_start = System.currentTimeMillis();
            status = FileOperation.rw_file(test_dir, test_times, test_size, test_pattern);
            time_end = System.currentTimeMillis();
            return null;
        }

        protected void onPreExecute() {
            refreashDisplayCtrl(false);
            /* Check for the test dir is exist */
            test_dir = spStorageChoice.getSelectedItem().toString() + "/" + testDir;
            File testDir = new File(test_dir);
            if (testDir.exists() == false) {
                testDir.mkdirs();
            }

            String[] file_list = testDir.list();
            for (String str : file_list) {
                File file = new File(test_dir + str);
                if (file.isFile() == true) {
                    file.delete();
                }
            }

            String val = spTestTimes.getSelectedItem().toString();
            int count = 0;
            for(String str:testTimesString){
                if(val.compareTo(str) == 0){
                    test_times = testTimesValue[count];
                    break;
                }
                count++;
            }

            val = spTestSize.getSelectedItem().toString();
            count = 0;
            for(String str:testSizeString){
                if(val.compareTo(str) == 0){
                    test_size = testSizeValue[count];
                    break;
                }
                count++;
            }

            val = spTestPattern.getSelectedItem().toString();
            count = 0;
            for(String str:testPatternString){
                if(val.compareTo(str) == 0){
                    test_pattern = testPatternValue[count];
                    break;
                }
                count++;
            }
        }

        protected void onPostExecute(Void result) {
            if(status == true){
                tvOutput.setText("");
            }else{
                tvOutput.setText("Test Error\n");
            }
            refreashDisplayCtrl(true);
        }

        protected void onProgressUpdate(String... progress) {
        }
    }
}
