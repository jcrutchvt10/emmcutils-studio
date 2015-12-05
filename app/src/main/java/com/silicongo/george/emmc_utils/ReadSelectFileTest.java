package com.silicongo.george.emmc_utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReadSelectFileTest.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReadSelectFileTest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadSelectFileTest extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /* progressBar */
    private ProgressBar pbOperationState;

    /* TextView */
    private TextView tvSelectReadFile;
    private TextView tvOutputInfo;

    /* Button */
    private Button btSelectReadFile;
    private Button btGenerateReadFile;
    private Button btStartReadFile;

    /* ScrollView */
    private ScrollView svOutputInfo;

    private String strReadFile;

    private ReadFileInfo readFileTask;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReadSelectFileTest.
     */
    // TODO: Rename and change types and number of parameters
    public static ReadSelectFileTest newInstance(String param1, String param2) {
        ReadSelectFileTest fragment = new ReadSelectFileTest();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ReadSelectFileTest() {
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
        View v = inflater.inflate(R.layout.fragment_read_select_file_test, container, false);

        pbOperationState = (ProgressBar) v.findViewById(R.id.pbOperationState);
        tvSelectReadFile = (TextView) v.findViewById(R.id.tvSelectReadFile);
        tvOutputInfo = (TextView) v.findViewById(R.id.tvOutputInfo);
        btSelectReadFile = (Button) v.findViewById(R.id.btSelectReadFile);
        btGenerateReadFile = (Button) v.findViewById(R.id.btGenerateReadFile);
        btStartReadFile = (Button) v.findViewById(R.id.btStartReadFile);
        svOutputInfo = (ScrollView) v.findViewById(R.id.svOutputInfo);

        setDisplayCtrl(true);

        btSelectReadFile.setOnClickListener(this);
        btGenerateReadFile.setOnClickListener(this);
        btStartReadFile.setOnClickListener(this);

        strReadFile = null;

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
            case R.id.btSelectReadFile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Select a File to Read"), 0x0);
                break;
            case R.id.btGenerateReadFile:
                break;
            case R.id.btStartReadFile:
                if((readFileTask != null) && (readFileTask.getStatus() == AsyncTask.Status.RUNNING)){
                    readFileTask.cancel(true);
                    setDisplayCtrl(true);
                }else{
                    readFileTask = new ReadFileInfo();
                    readFileTask.execute();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch (requestCode) {
            case 0x0:
                if(resultCode == Activity.RESULT_OK){
                    String str = data.getData().getPath();
                    strReadFile = null;
                    if(str != null){
                        File fileTmp = new File(str);
                        if((fileTmp.exists() == true) && (fileTmp.isFile())){
                            strReadFile = str;
                        }
                    }
                    if(strReadFile != null) {
                        tvSelectReadFile.setText(strReadFile);
                    }else{
                        tvSelectReadFile.setText("");
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDisplayCtrl(boolean enable){
        pbOperationState.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
        tvSelectReadFile.setEnabled(enable);
        tvOutputInfo.setEnabled(!enable);
        btSelectReadFile.setEnabled(enable);
        btGenerateReadFile.setEnabled(enable);
        btStartReadFile.setText(enable ? "Start Read Test" : "Stop");
    }

    /* AsyncTask to update the screen information */
    private class ReadFileInfo extends AsyncTask<String, Integer, Void> {
        private static final String TAG = "ReadFileInfo";
        private long fileSize;
        private long fileCurrentReadPos;
        private byte[] fileBuffer;
        private FileInputStream fin;

        private long startTimestamp;
        private long currentTimestamp;

        private int lastUpdateInterval;
        private long lastUpdateFilePos;

        public ReadFileInfo() {
            lastUpdateInterval = 0x0;
            lastUpdateFilePos = 0x0;
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(String... urls) {
            Integer[] progressResult = {0x0, 0x0, 0x0};
            if(strReadFile != null){
                File fileRead = new File(strReadFile);
                /* Get the file size */
                fileSize = fileRead.length();
                fileCurrentReadPos = 0x0;
                startTimestamp = System.currentTimeMillis();

                fileBuffer = new byte[16384];
                try {
                    fin = new FileInputStream(strReadFile);
                    while (fileCurrentReadPos < fileSize){
                        fin.read(fileBuffer, 0x0, fileBuffer.length);
                        fileCurrentReadPos += fileBuffer.length;
                        currentTimestamp = System.currentTimeMillis();
                        progressResult[0] = new Integer((int)(currentTimestamp - startTimestamp));
                        publishProgress(progressResult);
                        if(isCancelled()){
                            break;
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (java.io.IOException e){
                }
            }
            return null;
        }

        protected void onPreExecute() {
            setDisplayCtrl(false);
            tvOutputInfo.setText("");
        }

        protected void onPostExecute(Void result) {
            setDisplayCtrl(true);
        }

        protected void onProgressUpdate(Integer... progress) {
            int val = progress[0];
            pbOperationState.setProgress((int)(fileCurrentReadPos*100/fileSize));

            if((val - lastUpdateInterval) > 1000){
                int interval = (int)(val - lastUpdateInterval);
                int length = (int)(fileCurrentReadPos - lastUpdateFilePos);
                double speed = length/interval*1000/1024/1024;
                double average_speed = fileCurrentReadPos/val*1000/1024/1024;
                String strSpeed = String.format("Read: %.3f MB/S, Average: %.3f MB/S\n",
                        speed, average_speed);
                tvOutputInfo.append(strSpeed);
                svOutputInfo.post(new Runnable() {
                    public void run() {
                        svOutputInfo.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                lastUpdateInterval = val;
                lastUpdateFilePos = fileCurrentReadPos;
            }
        }
    }
}
