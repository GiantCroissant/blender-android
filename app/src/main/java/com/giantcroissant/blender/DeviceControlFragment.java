package com.giantcroissant.blender;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeviceControlFragment.OnDeviceControlFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceControlFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnDeviceControlFragmentInteractionListener mListener;
    private View rootView;

    private LinearLayout blenderSettingView;
    private TextView blenderSettingTitle;
    private NumberPicker blenderSettingNumberPicker;
    private EditText setTimeEditText;
    private EditText setSpeedEditText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceControlFragment newInstance() {
        DeviceControlFragment fragment = new DeviceControlFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public DeviceControlFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_device_control, container, false);
        blenderSettingView = (LinearLayout) rootView.findViewById(R.id.blenderSettingView);
        blenderSettingTitle = (TextView) rootView.findViewById(R.id.blenderSettingTitle);
        blenderSettingNumberPicker = (NumberPicker) rootView.findViewById(R.id.blenderSettingNumberPicker);
        setTimeEditText = (EditText) rootView.findViewById(R.id.setTimeEditText);
        setSpeedEditText = (EditText) rootView.findViewById(R.id.setSpeedEditText);
        setNumberPickerTextColor(blenderSettingNumberPicker,getResources().getColor(R.color.ColorPrimary));
        setTimeEditText.setText("1");
        setSpeedEditText.setText("1");
        blenderSettingView.setVisibility(View.INVISIBLE);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onDeviceControlFragmentInteraction(string);
        }
    }


    public void setBlenderSettingView(boolean visibility)
    {

        blenderSettingView = (LinearLayout) rootView.findViewById(R.id.blenderSettingView);
        if(visibility)
        {
            blenderSettingView.setVisibility(View.VISIBLE);
        }
        else
        {
            blenderSettingView.setVisibility(View.INVISIBLE);
        }
    }

    public void setSpeedView()
    {
        blenderSettingTitle.setText("選擇轉速");
        blenderSettingNumberPicker.setMinValue(1);
        blenderSettingNumberPicker.setMaxValue(5);
//        blenderSettingNumberPicker.setWrapSelectorWheel(false);
        blenderSettingNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        blenderSettingNumberPicker.setValue(Integer.parseInt(setSpeedEditText.getText().toString()));
    }

    public void setValue()
    {
        if(blenderSettingTitle.getText().toString().compareTo("選擇轉速") == 0)
        {
            setSpeedEditText.setText(String.valueOf(blenderSettingNumberPicker.getValue()));
        }
        else if(blenderSettingTitle.getText().toString().compareTo("選擇時間") == 0)
        {
            setTimeEditText.setText(String.valueOf(blenderSettingNumberPicker.getValue()));
        }
    }

    public void setTimeView()
    {
        blenderSettingTitle.setText("選擇時間");
        blenderSettingNumberPicker.setMinValue(1);
        blenderSettingNumberPicker.setMaxValue(60);
//        blenderSettingNumberPicker.setWrapSelectorWheel(false);
        blenderSettingNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        blenderSettingNumberPicker.setValue(Integer.parseInt(setTimeEditText.getText().toString()));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDeviceControlFragmentInteractionListener) activity;
//            mListener.onDeviceControlFragmentInteraction("Ok");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDeviceControlFragmentInteraction("Exit");
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
    public interface OnDeviceControlFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onDeviceControlFragmentInteraction(String string);
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);

                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
//                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalAccessException e){
//                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
//                    Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }
}
