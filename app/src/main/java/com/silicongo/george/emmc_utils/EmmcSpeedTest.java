package com.silicongo.george.emmc_utils;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
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
public class EmmcSpeedTest extends Fragment {
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

    private String[] testSizeString = {"512 Bytes", "1K", "4K", "16K", "32K",
            "64K", "128K", "256K", "512K", "1M"};
    private List<String> testSizeList;
    private ArrayAdapter<String> testSizeArrayAdapter;

    private String[] testTimesString = {"100", "1000", "10000", "100000", "Infinite"};
    private List<String> testTimesList;
    private ArrayAdapter<String> testTimesArrayAdapter;

    /* UI control field */
    /* textview */
    private TextView twTextOutputInfo;

    /* RadioButton */
    private RadioButton rbExternelStorage;
    private RadioButton rbInternelStorage;

    /* Spinner */
    private Spinner spTestTimes;
    private Spinner spTestSize;

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

        rbExternelStorage = (RadioButton) v.findViewById(R.id.rbExternalStorage);
        rbInternelStorage = (RadioButton) v.findViewById(R.id.rbInternalStorage);

        spTestTimes = (Spinner) v.findViewById(R.id.spTestTimes);
        spTestSize = (Spinner) v.findViewById(R.id.spTestSize);

        btTestStart = (Button) v.findViewById(R.id.btTestStart);

        if (testDir.get(ExternalStorage.EXTERNAL_SD_CARD) == null) {
            rbExternelStorage.setVisibility(View.INVISIBLE);
        }
        rbInternelStorage.setSelected(true);

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

}
