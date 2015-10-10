package com.giantcroissant.blender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.giantcroissant.blender.dummy.DummyContent;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnAboutCompanyItemFragmentInteractionListener}
 * interface.
 */
public class AboutCompanyItemFragment extends android.support.v4.app.Fragment  implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnAboutCompanyItemFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    AboutCompanyAdapter mAboutCompanyAdapter;
    private View rootView;

    private ArrayList<CompanyItemSystem> companyItemSystems = new ArrayList<CompanyItemSystem>();
//    private ArrayList<CompanyItem> companyItems;

    // TODO: Rename and change types of parameters
    public AboutCompanyItemFragment newInstance(ArrayList<CompanyItemSystem> companyItemSystems) {
        AboutCompanyItemFragment fragment = new AboutCompanyItemFragment();
        this.companyItemSystems = new ArrayList<CompanyItemSystem>();
//        this.companyItemSystems = companyItemSystems;
        for (CompanyItemSystem companyItemSystem : companyItemSystems) {
            this.companyItemSystems.add(companyItemSystem);
        }

//        Log.e("xxx", "w");
//        Log.e("xxx", String.valueOf(this.companyItemSystems.size()));
//        this.companyItems = companyItemSystem.contentIds;
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AboutCompanyItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_aboutcompanyitem, container, false);


        mListView = (ListView) rootView.findViewById(R.id.aboutcompanylistView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mAboutCompanyAdapter = new AboutCompanyAdapter(this.getActivity() , R.layout.about_company_item, CompanyData.getInstance().companyItemSystems);
        mListView.setAdapter(mAboutCompanyAdapter);
        mAboutCompanyAdapter.notifyDataSetChanged();

        return rootView;
    }
    private void selectItem(int position) {

        Intent intent = new Intent(this.getActivity(), CompanyItemSystemActivity.class);

        intent.putExtra("position", position);
        intent.putExtra("itemListViewID", CompanyData.getInstance().companyItemSystems.get(position).getId());
        intent.putExtra("itemListViewTitle", CompanyData.getInstance().companyItemSystems.get(position).getTitle());
        intent.putExtra("itemListViewContent", CompanyData.getInstance().companyItemSystems.get(position).getContent());
        intent.putExtra("itemListViewIconUrl", CompanyData.getInstance().companyItemSystems.get(position).getIconUrl());

        startActivityForResult(intent, 0);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAboutCompanyItemFragmentInteractionListener) activity;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onAboutCompanyItemFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
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
    public interface OnAboutCompanyItemFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onAboutCompanyItemFragmentInteraction(String id);
    }

}
