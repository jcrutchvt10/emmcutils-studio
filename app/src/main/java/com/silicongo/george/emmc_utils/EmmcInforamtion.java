package com.silicongo.george.emmc_utils;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmmcInforamtion.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmmcInforamtion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmmcInforamtion extends Fragment implements View.OnClickListener, FireMissilesDialogFragment.NoticeDialogListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public String emmcDevPath;

    /* scroll view */
    private ScrollView svCmdLineOutput;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmmcInforamtion.
     */
    // TODO: Rename and change types and number of parameters
    public static EmmcInforamtion newInstance(String param1, String param2) {
        EmmcInforamtion fragment = new EmmcInforamtion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EmmcInforamtion() {
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
        View v = inflater.inflate(R.layout.fragment_emmc_inforamtion, container, false);

        /* Init the display ctrl */
        twEmmcDevPath = (TextView) v.findViewById(R.id.EmmcDevPathInfo);
        twEmmcVersion = (TextView) v.findViewById(R.id.EmmcVersionInfo);
        twEmmcSpeed = (TextView) v.findViewById(R.id.EmmcSpeedInfo);

        btGetFeature = (Button) v.findViewById(R.id.emmcGetFeature);
        btGetWriteProtectStatus = (Button) v.findViewById(R.id.emmcGetWriteProtectStatus);
        btDoSanitize = (Button) v.findViewById(R.id.emmcDoSanitize);
        btDoBKOPS = (Button) v.findViewById(R.id.emmcDoBKOPS);
        btClearContent = (Button) v.findViewById(R.id.clearContent);

        svCmdLineOutput = (ScrollView) v.findViewById(R.id.svCmdLineOutput);

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
        twCmdLineOutput = (TextView) v.findViewById(R.id.cmdLineOutput);

        if (RootUtil.isDeviceRooted() == false) {
            FireMissilesDialogFragment dialogFragment = new FireMissilesDialogFragment();
            dialogFragment.show(getFragmentManager(), "Dialog");
        } else {
            if ((initEmmcInfo == null) || (initEmmcInfo.getStatus() == AsyncTask.Status.FINISHED)) {
                initEmmcInfo = new InitEmmcInfo();
                initEmmcInfo.execute();
            }
        }

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
            mmcUtils = new MmcUtils(twCmdLineOutput, svCmdLineOutput);
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(String... urls) {

            mmcUtils.checkEmmcExecuteFile(getContext());

            /* Init the global var */
            if (emmcDevPath == null) {
                emmcDevPath = MmcUtils.getEmmcPath();
            }
            if (extcsd == null) {
                extcsd = mmcUtils.getEmmcExtCsd();
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
                if ((extcsd[0] == -1) || (extcsd[0] == -2)) {
                    twEmmcVersion.setText(emmcVersion[emmcVersion.length - 1] + ", Err Code: " + extcsd[0]);
                    twEmmcSpeed.setText(emmcSpeed[emmcSpeed.length - 1]);
                } else {
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
                    twEmmcSpeed.setText(emmcSpeed[speed]);
                }
            }

            btGetFeature.setEnabled(true);
            btGetWriteProtectStatus.setEnabled(true);
            btDoSanitize.setEnabled(true);
            btDoBKOPS.setEnabled(true);
            btClearContent.setEnabled(true);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        getActivity().finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        getActivity().finish();
    }
}
