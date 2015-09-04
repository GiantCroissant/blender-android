package com.giantcroissant.blender;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CookBookDetailToDoFragment.OnCookBookDetailToDoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CookBookDetailToDoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookBookDetailToDoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private CheckListAdapter mCheckListAdapter;
    private OnCookBookDetailToDoFragmentInteractionListener mListener;
    private Cookbook cookBook;
    private ProgressBar checkProgressBar;
    private ListView checkListListView;
    private View rootView;
    private ArrayList<CheckListItem> newCheckListItems;
    private int currentIndex;

    public static CookBookDetailToDoFragment newInstance(int sectionNumber ,Cookbook cookBook) {
        CookBookDetailToDoFragment fragment = new CookBookDetailToDoFragment();
        fragment.cookBook = cookBook;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public CookBookDetailToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cook_book_detial_to_do, container, false);
        checkListListView = (ListView) rootView.findViewById(R.id.checkListView);
        checkListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        checkProgressBar = (ProgressBar) rootView.findViewById(R.id.checkProgressBar);
//        createFakeData();

        // Inflate the layout for this fragment
        return rootView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onCookBookDetailToDoFragmentInteraction(string);
        }
    }

    private void checkAllcheckBox()
    {
        checkProgressBar.setMax(newCheckListItems.size());
        int currentCheckIndex = 0;
        for (CheckListItem newCheckListItem : newCheckListItems) {
            if(newCheckListItem.radioButton.isChecked()) //newCheckListItem.checkBox.isChecked()
            {
                currentCheckIndex += 1;
            }
        }
        checkProgressBar.setProgress(currentCheckIndex);
    }

    private void createFakeData()
    {
        ArrayList<String> newSteps = new ArrayList<String>();
        newSteps.add("步驟1");
        newSteps.add("步驟2");
        newSteps.add("步驟3");
        newSteps.add("步驟4");
        newSteps.add("步驟5");

        newCheckListItems = new ArrayList<CheckListItem>();
        for (String newStep : newSteps) {
            newCheckListItems.add(new CheckListItem(UUID.randomUUID().toString(), newStep, false));
        }

    }

    private void setUIValue()
    {
        TextView cookBookNameText = (TextView) rootView.findViewById(R.id.cookBookNameText);
        cookBookNameText.setText(cookBook.getName());
    }

    private void setStepsList()
    {

        ArrayList<String> newSteps = cookBook.getSteps();
        ArrayList<String> newTimeOfSteps = cookBook.getTimeOfSteps();
        ArrayList<String> newSpeedOfSteps = cookBook.getSpeedOfSteps();

        newCheckListItems = new ArrayList<CheckListItem>();

        for (int i = 0; i < newSteps.size(); i++) {
            String tmpStep = (i+1)+")."+ newSteps.get(i)+"。";
            if(Integer.parseInt(newTimeOfSteps.get(i)) > 0 && Integer.parseInt(newSpeedOfSteps.get(i))> 0)
            {
                tmpStep = tmpStep + " 啟動果汁機，轉速 " + newSpeedOfSteps.get(i) + "，" + newTimeOfSteps.get(i) + "秒。";
            }
            newCheckListItems.add(new CheckListItem(UUID.randomUUID().toString(), tmpStep, false));
        }
    }

    private void selectItem(int position) {

    }

    public void setConfrim()
    {
        if(getFinished())
        {
            return;
        }
//        newCheckListItems.get(currentIndex).checkBox.setChecked(true);
//        Log.e("XXX", String.valueOf(newCheckListItems.get(currentIndex).radioButton));
        newCheckListItems.get(currentIndex).radioButton.setChecked(true);
        currentIndex++;
    }

    public void setReStart()
    {
        for (CheckListItem newCheckListItem : newCheckListItems) {
//            newCheckListItem.checkBox.setChecked(false);
            newCheckListItem.radioButton.setChecked(false);
        }
        currentIndex = 0;
    }

    public boolean getIsNeedStartBlender()
    {
        if(getFinished())
        {
            return false;
        }
        ArrayList<String> newTimeOfSteps = cookBook.getTimeOfSteps();
        ArrayList<String> newSpeedOfSteps = cookBook.getSpeedOfSteps();
        return Integer.parseInt(newTimeOfSteps.get(currentIndex)) > 0 && Integer.parseInt(newSpeedOfSteps.get(currentIndex))> 0;
    }

    public int getCookBoookTimeOfSteps()
    {
        if(getFinished())
        {
            return 0;
        }
        return Integer.parseInt(cookBook.getTimeOfSteps().get(currentIndex));
    }

    public int getCookBoookSpeedOfSteps()
    {
        if(getFinished())
        {
            return 0;
        }
        return Integer.parseInt(cookBook.getSpeedOfSteps().get(currentIndex));
    }

    public boolean getFinished()
    {
        return currentIndex >= newCheckListItems.size();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mListener.onCookBookDetailToDoFragmentInteraction("Ok");
        setUIValue();
        setStepsList();
        CompoundButton.OnCheckedChangeListener newOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            View parentRow = (View) compoundButton.getParent().getParent();
            ListView listView = (ListView) parentRow.getParent();
//            Log.e("XXX", compoundButton.toString());
//            Log.e("XXX", compoundButton.getParent().toString());
//            Log.e("XXX", compoundButton.getParent().getParent().toString());
//            Log.e("XXX", compoundButton.getParent().getParent().getParent().toString());
//            Log.e("XXX", compoundButton.getParent().getParent().getParent().getParent().toString());

//            final int position = listView.getPositionForView(parentRow);

//            newCheckListItems.get(currentIndex).radioButton.setChecked(b);
//            Log.e("XXX",String.valueOf(position));
//            Log.e("XXX", compoundButton.getParent().getParent().getParent().toString());
//            Log.e("XXX",String.valueOf(b));
            checkAllcheckBox();
        }
    };

        mCheckListAdapter = new CheckListAdapter(this.getActivity() , R.layout.check_list_item, newCheckListItems, newOnCheckedChangeListener);
        checkListListView.setAdapter(mCheckListAdapter);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((CookBookDetailActivity) activity).onFragmentAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

        try {
            mListener = (OnCookBookDetailToDoFragmentInteractionListener) activity;
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
    public interface OnCookBookDetailToDoFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onCookBookDetailToDoFragmentInteraction(String string);
    }
}
