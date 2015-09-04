package com.giantcroissant.blender;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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

    private View rootView;
    private DeviceScanFragment deviceScanFragment;
    private DeviceControlFragment deviceControlFragment;

    public static AutoTestFragment newInstance(int sectionNumber) {
        AutoTestFragment fragment = new AutoTestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
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

        ImageButton blenderSettingButton = (ImageButton) rootView.findViewById(R.id.BlenderSettingButton_SelectColor);
        ImageButton blenderControlButton = (ImageButton) rootView.findViewById(R.id.BlenderControlButton_SelectColor);
        if (tabIndex == 0)
        {
            blenderSettingButton.setImageResource(R.color.TabSelectColor);
            blenderControlButton.setImageResource(R.color.TabNoSelectColor);
            if(deviceScanFragment == null)
            {
                deviceScanFragment = new DeviceScanFragment();
            }
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.sub_content, deviceScanFragment.newInstance());
            fragmentTransaction.commit();

        }
        else if(tabIndex == 1)
        {
            blenderSettingButton.setImageResource(R.color.TabNoSelectColor);
            blenderControlButton.setImageResource(R.color.TabSelectColor);

            if(deviceControlFragment == null)
            {
                deviceControlFragment = new DeviceControlFragment();
            }
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.sub_content, deviceControlFragment.newInstance());
            fragmentTransaction.commit();
        }

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
