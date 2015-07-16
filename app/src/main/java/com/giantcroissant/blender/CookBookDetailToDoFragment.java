package com.giantcroissant.blender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CookBookDetailToDoFragment.OnCookBookDetailVideoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CookBookDetailToDoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookBookDetailToDoFragment extends Fragment {

    private CheckListAdapter mCheckListAdapter;
    private OnCookBookDetailVideoFragmentInteractionListener mListener;

    private ProgressBar checkProgressBar;
    private ListView checkListListView;
    private View rootView;
    private ArrayList<CheckListItem> newCheckListItems;

    public static CookBookDetailToDoFragment newInstance(String param1, String param2) {
        CookBookDetailToDoFragment fragment = new CookBookDetailToDoFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
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
        createFakeData();

        CompoundButton.OnCheckedChangeListener newOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                View parentRow = (View) compoundButton.getParent().getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);

                newCheckListItems.get(position).setIsFinished(b);
//                Log.e("XXX",String.valueOf(position));
//                Log.e("XXX", compoundButton.getParent().getParent().getParent().toString());
//                Log.e("XXX",String.valueOf(b));
                checkAllcheckBox();
            }
        };

        mCheckListAdapter = new CheckListAdapter(this.getActivity() , R.layout.check_list_item, newCheckListItems, newOnCheckedChangeListener);
        checkListListView.setAdapter(mCheckListAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onCookBookDetailVideoFragmentInteraction(string);
        }
    }

    private  void checkAllcheckBox()
    {
        checkProgressBar.setMax(newCheckListItems.size());
        int currentCheckIndex = 0;
        for (CheckListItem newCheckListItem : newCheckListItems) {
            if(newCheckListItem.getIsFinished())
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

    private void selectItem(int position) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCookBookDetailVideoFragmentInteractionListener) activity;
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
    public interface OnCookBookDetailVideoFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onCookBookDetailVideoFragmentInteraction(String string);
    }
}
