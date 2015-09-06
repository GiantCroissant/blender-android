package com.giantcroissant.blender;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AutoTestFragment.OnAutoTestFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AutoTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutoTestFragment extends Fragment
        implements CookBooksDataFragment
{

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnAutoTestFragmentInteractionListener mListener;
    private boolean switchIsChecked;
    private View rootView;
    private DeviceScanFragment deviceScanFragment;
    private DeviceControlFragment deviceControlFragment;

    public static AutoTestFragment newInstance(int sectionNumber, boolean switchIsChecked) {
        AutoTestFragment fragment = new AutoTestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        fragment.switchIsChecked = switchIsChecked;
        return fragment;
    }

    public AutoTestFragment() {
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

        rootView = inflater.inflate(R.layout.fragment_auto_test, container, false);
        setCurrentTab(0);
        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onAutoTestFragmentInteraction(string);
        }
    }

    public void setCurrentTab(int tabIndex) {

        Button blenderSettingButton = (Button) rootView.findViewById(R.id.BlenderSettingButton);
        Button blenderControlButton = (Button) rootView.findViewById(R.id.BlenderControlButton);
        ImageButton blenderSettingButtonColor = (ImageButton) rootView.findViewById(R.id.BlenderSettingButton_SelectColor);
        ImageButton blenderControlButtonColor = (ImageButton) rootView.findViewById(R.id.BlenderControlButton_SelectColor);
        if (tabIndex == 0)
        {
            blenderSettingButton.setTextColor(getResources().getColor(R.color.White));
            blenderControlButton.setTextColor(getResources().getColor(R.color.c70White));
            blenderSettingButtonColor.setImageResource(R.color.TabSelectColor);
            blenderControlButtonColor.setImageResource(R.color.TabNoSelectColor);
            if(deviceScanFragment == null)
            {
                deviceScanFragment = new DeviceScanFragment();
            }
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.sub_content, deviceScanFragment.newInstance(switchIsChecked));
            fragmentTransaction.commit();

        }
        else if(tabIndex == 1)
        {
            blenderSettingButton.setTextColor(getResources().getColor(R.color.c70White));
            blenderControlButton.setTextColor(getResources().getColor(R.color.White));
            blenderSettingButtonColor.setImageResource(R.color.TabNoSelectColor);
            blenderControlButtonColor.setImageResource(R.color.TabSelectColor);

            if(deviceControlFragment == null)
            {
                deviceControlFragment = new DeviceControlFragment();
            }
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.sub_content, deviceControlFragment.newInstance());
            fragmentTransaction.commit();
        }

    }

    public void setBlenderSettingView(boolean visibility)
    {

        FragmentManager fm = getFragmentManager();//if added by xml
        DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);
        fragment.setBlenderSettingView(visibility);
    }

    public void setSpeedView()
    {
        FragmentManager fm = getFragmentManager();//if added by xml
        DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);
        fragment.setSpeedView();
    }

    public void setValue()
    {
        FragmentManager fm = getFragmentManager();//if added by xml
        DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);
        fragment.setValue();
    }

    public void setTimeView()
    {
        FragmentManager fm = getFragmentManager();//if added by xml
        DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);
        fragment.setTimeView();
    }

    public void deviceScanIsOk()
    {
        mListener.onAutoTestFragmentInteraction("OK");
    }

    public void setSwitchChecked(boolean isChecked)
    {
        switchIsChecked = isChecked;
//        FragmentManager fm = getFragmentManager();//if added by xml
//        DeviceScanFragment fragment = (DeviceScanFragment)fm.findFragmentById(R.id.sub_content);
//        fragment.setSwitch(isChecked);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mListener = (OnAutoTestFragmentInteractionListener) activity;
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

    @Override
    public void upDateListView(Realm realm) {

    }

    public void onClick(View view)
    {
        FragmentManager fm = getFragmentManager();//if added by xml
        if (view.getId() == R.id.startBlenderButton)
        {
            DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);

        }
        else if (view.getId() == R.id.stopBlenderButton)
        {
            DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);
        }
        else if (view.getId() == R.id.cancelSettingButton)
        {
            DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);
            fragment.setBlenderSettingView(false);
        }
        else if (view.getId() == R.id.confrimSettingButton)
        {
            DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);
            fragment.setValue();
            fragment.setBlenderSettingView(false);
        }
        else if (view.getId() == R.id.setTimeEditText)
        {
            DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);
            fragment.setBlenderSettingView(true);
            fragment.setTimeView();
        }
        else if (view.getId() == R.id.setSpeedEditText)
        {
            DeviceControlFragment fragment = (DeviceControlFragment)fm.findFragmentById(R.id.sub_content);
            fragment.setBlenderSettingView(true);
            fragment.setSpeedView();
        }
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
    public interface OnAutoTestFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onAutoTestFragmentInteraction(String String);
    }

}
